import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PanelComponent } from './panelcomponent';


@NgModule({
    imports: [
      CommonModule
      ],
     entryComponents:[PanelComponent],
    declarations:[PanelComponent],
      exports:[PanelComponent]
})

export class PanelComponentModule{}