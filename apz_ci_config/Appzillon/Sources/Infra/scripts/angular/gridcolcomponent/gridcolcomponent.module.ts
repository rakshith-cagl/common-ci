import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { GridColComponent } from './gridcolcomponent';


@NgModule({
    imports: [
      CommonModule
      ],
     entryComponents:[GridColComponent],
    declarations:[GridColComponent],
      exports:[GridColComponent]
})

export class GridColComponentModule{}