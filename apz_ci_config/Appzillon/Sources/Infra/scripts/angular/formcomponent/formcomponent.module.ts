import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormComponent } from './formcomponent';


@NgModule({
    imports: [
      CommonModule
      ],
     entryComponents:[FormComponent],
    declarations:[FormComponent],
      exports:[FormComponent]
})

export class FormComponentModule{}