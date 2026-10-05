import { Injectable, createNgModuleRef, NgModuleRef, Injector, Type, Inject, ApplicationRef, EmbeddedViewRef } from '@angular/core';
import { LAZY_WIDGETS } from './tokens';
import { apz } from './appzillon.service';
import $ from 'jquery';

@Injectable()
export class LazyLoaderService {

  constructor(private injector: Injector,
    private applicationRef: ApplicationRef,
    @Inject(LAZY_WIDGETS) private lazyWidgets: { [key: string]: () => Promise<Type<any>> }) { }


  async load(name: string, domElm: string, proc: any) {
    if (this.lazyWidgets[name]) {
      const ngModuleOrNgModuleFactory = await this.lazyWidgets[name]();
      let htmlDomObj;
      let moduleFactory = this.createModule(ngModuleOrNgModuleFactory)
      const entryComponent = moduleFactory.instance.constructor.entry;
      for (const eachComp of entryComponent) {
        let component = eachComp;
        let compFactory = moduleFactory.componentFactoryResolver.resolveComponentFactory(component).create(this.injector); //NOSONAR
        if (compFactory.hostView) {
          this.applicationRef.attachView(compFactory.hostView);
          htmlDomObj = (compFactory.hostView as EmbeddedViewRef<any>)
            .rootNodes[0] as HTMLElement;
          document.getElementById(domElm)?.appendChild(htmlDomObj);
        }
        apz.compFactory[name] = { compFactory: compFactory, domId: domElm, proc: proc };
        compFactory.changeDetectorRef.detectChanges();
      }
      this.updateRolePage(domElm);
      if (htmlDomObj && (domElm != "page_1" && domElm != "page_2") && $(".rolepage").length > 1) {
        $($(".rolepage").not(":first")).removeClass("rolepage");
        let scrId = "#scr__" + proc.appId + "__" + proc.scr + "__main";
        let scrHtml = $(scrId).find(".pagebody").children();
        $(scrId).empty();
        $(scrId).append(scrHtml);
      }
      if (proc != undefined) {
        apz.scrScriptsLoaded(proc);
      }
    }
  }

  updateRolePage(domElm: string) {
    if (domElm == "page_1" || domElm == "page_2") {
      let rolePagId = (domElm == "page_1") ? "page_2" : "page_1";
      $("#" + rolePagId).find(".rolepage").removeClass("rolepage");
    }
  }

  createModule(ngModuleOrNgModuleFactory: any) {
    let moduleFactory;
    if (ngModuleOrNgModuleFactory instanceof NgModuleRef) {
      moduleFactory = ngModuleOrNgModuleFactory;
    } else {
      moduleFactory = createNgModuleRef(ngModuleOrNgModuleFactory)
    }
    return moduleFactory;
  }

  async loadProjectModule(appId: any) {
    if (this.lazyWidgets[appId]) {
      const ngModuleOrNgModuleFactory = await this.lazyWidgets[appId]();
      let moduleFactory;
      let component;
      if (ngModuleOrNgModuleFactory instanceof NgModuleRef) {
        moduleFactory = ngModuleOrNgModuleFactory;
      } else {
        moduleFactory = createNgModuleRef(ngModuleOrNgModuleFactory);
      }
      const entryComponent = moduleFactory.instance.constructor.entry;
      if (entryComponent.length > 0) {
        for (const eachComp of entryComponent) {
          component = eachComp;
          let compFactory = moduleFactory.componentFactoryResolver.resolveComponentFactory(component).create(this.injector); //NOSONAR
          this.applicationRef.attachView(compFactory.hostView);
          compFactory.changeDetectorRef.detectChanges();
        }
      }
      apz.appFactory[appId] = true;
    }
  }

}