import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { BodyComponent } from './bodycomponent';


@NgModule({
    imports: [
      CommonModule
      ],
     entryComponents:[BodyComponent],
    declarations:[BodyComponent],
      exports:[BodyComponent]
})

export class BodyComponentModule{}