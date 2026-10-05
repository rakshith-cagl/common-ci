import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SecRowComponent } from './secrowcomponent';


@NgModule({
    imports: [
      CommonModule
      ],
     entryComponents:[SecRowComponent],
    declarations:[SecRowComponent],
      exports:[SecRowComponent]
})

export class SecRowComponentModule{}