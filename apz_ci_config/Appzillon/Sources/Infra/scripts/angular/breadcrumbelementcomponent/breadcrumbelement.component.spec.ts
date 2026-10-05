import { TestBed } from '@angular/core/testing';
import {WindowRef} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
import { BreadCrumbElementComponent } from './breadcrumbelement.component';
describe('BreadCrumbElementComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        BreadCrumbElementComponent
     ],
     providers: [WindowRef,DataService]
   }).compileComponents();
 });

 it('should create the BreadCrumbElementComponent', () => {
   const fixture = TestBed.createComponent(BreadCrumbElementComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

it('should emit preBreadcrumbAction', () => {
  const fixture = TestBed.createComponent(BreadCrumbElementComponent);
  const component = fixture.componentInstance;
  spyOn(component.preBreadcrumbAction, 'emit');
  component.beforeBreadcrumbAction({});
  expect(component.preBreadcrumbAction.emit).toHaveBeenCalled();
});

it('should emit postBreadcrumbAction', () => {
  const fixture = TestBed.createComponent(BreadCrumbElementComponent);
  const component = fixture.componentInstance;
  spyOn(component.postBreadcrumbAction, 'emit');
  component.afterBreadcrumbAction({});
  expect(component.postBreadcrumbAction.emit).toHaveBeenCalled();
});

it('should call life cycle methods', () => {
  const fixture = TestBed.createComponent(BreadCrumbElementComponent);
  const component = fixture.componentInstance;
  component.props = {'id': 'test_bread_crumb_1',hasTitleIcon: true, buttonTitle: {'value': 'Test'},breadCrumbClass: {'className':'eoc srb'}, breadCrumbWrapperClass:{'className':'eoc srb'}, titleIcon:{'direct':{}}};
  fixture.detectChanges();
  expect(component.breadCrumbWrapperClassRef.nativeElement).toBeDefined();
});




});