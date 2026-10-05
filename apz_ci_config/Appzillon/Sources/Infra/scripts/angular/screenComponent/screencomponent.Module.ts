import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ScreenComponent } from './screencomponent';


@NgModule({
    imports: [
      CommonModule
      ],
     entryComponents:[ScreenComponent],
    declarations:[ScreenComponent],
      exports:[ScreenComponent]
})

export class ScreenComponentModule{}