import {environment} from './appzillon/scripts/angular/environments/environment';
import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app.module';
if(environment.production){
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule).then((ref:any) => {
  // Ensure Angular destroys itself on hot reloads.
  if ((window as any)['ngRef']) {
    (window as any)['ngRef'].destroy();
  }
  (window as any)['ngRef'] = ref;
  // Otherwise, log the boot error
}).catch((err:any) => console.error(err));
