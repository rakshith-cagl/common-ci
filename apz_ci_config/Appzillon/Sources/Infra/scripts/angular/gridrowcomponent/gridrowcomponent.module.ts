import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { GridRowComponent } from './gridrowcomponent';


@NgModule({
    imports: [
      CommonModule
      ],
     entryComponents:[GridRowComponent],
    declarations:[GridRowComponent],
      exports:[GridRowComponent]
})

export class GridRowComponentModule{}