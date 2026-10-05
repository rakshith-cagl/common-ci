import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SecColComponent } from './seccolcomponent';


@NgModule({
    imports: [
      CommonModule
      ],
     entryComponents:[SecColComponent],
    declarations:[SecColComponent],
      exports:[SecColComponent]
})

export class SecColComponentModule{}