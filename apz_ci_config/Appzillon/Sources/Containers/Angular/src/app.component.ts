import { Component, ViewContainerRef, ViewChild, OnInit } from '@angular/core';
import {LazyLoaderService} from './appzillon/scripts/angular/lazy-loader.service';
import {WindowRef} from './appzillon/scripts/angular/appzillon.service';
@Component({
  selector: 'my-app',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  @ViewChild('container', {read: ViewContainerRef, static: false}) container!: ViewContainerRef;
  name = 'Angular';
  constructor(private loader: LazyLoaderService,public winRef:WindowRef){}

  ngOnInit(){
    this.winRef.nativeWindow.componentLoader = this.loader;
  }
}
