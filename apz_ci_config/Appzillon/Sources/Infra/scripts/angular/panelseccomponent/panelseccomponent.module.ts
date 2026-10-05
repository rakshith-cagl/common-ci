import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PanelSecComponent } from './panelseccomponent';


@NgModule({
    imports: [
      CommonModule
      ],
     entryComponents:[PanelSecComponent],
    declarations:[PanelSecComponent],
      exports:[PanelSecComponent]
})

export class PanelSecComponentModule{}