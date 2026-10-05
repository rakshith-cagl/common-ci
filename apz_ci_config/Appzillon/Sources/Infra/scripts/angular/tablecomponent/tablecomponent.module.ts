import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TableComponent } from './tablecomponent';


@NgModule({
    imports: [
      CommonModule
      ],
     entryComponents:[TableComponent],
    declarations:[TableComponent],
      exports:[TableComponent]
})

export class TableComponentModule{}