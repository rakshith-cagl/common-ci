import { Injectable } from '@angular/core';

function _window() : any {
   // return the global native browser window object
   return window;
}

function setApz(newValue: any) {
   apz = newValue;
}

@Injectable()
export class WindowRef {
   get nativeWindow() : any {
      return _window();
   }
}

export let apz= _window().apz;

export { setApz }