import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {provideRoutes} from '@angular/router';
import {lazyWidgets, lazyArrayToObj} from './appzillon/scripts/angular/lazy-widgets';
import { AppComponent } from './app.component';
import { LazyLoaderService } from './appzillon/scripts/angular/lazy-loader.service';
import {LAZY_WIDGETS} from './appzillon/scripts/angular/tokens';
import {WindowRef} from './appzillon/scripts/angular/appzillon.service';
import {DataService} from './appzillon/scripts/angular/data_angular';
import { HttpClientModule } from '@angular/common/http';
APZIMPORT

@NgModule({
  imports:      [ BrowserModule, FormsModule,CommonModule,HttpClientModule ],
  declarations: [ AppComponent APZDIRECTIVESLIST APZPIPESLIST],//DIRECTIVESLIST => user defined Directives, PIPESLIST => user defined pipes
  bootstrap:    [ AppComponent ],
  providers: [{ provide: LAZY_WIDGETS, useFactory: lazyArrayToObj }, 
    LazyLoaderService, provideRoutes(lazyWidgets),WindowRef,HttpClientModule,DataService APZSERVICELIST,APZPIPESLIST] //SERVICELIST => User defined Services
})
export class AppModule { }
