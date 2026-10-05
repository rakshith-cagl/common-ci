import { NgModule } from '@angular/core';

import { GaugeComponent } from './gauge.component';
import { CommonModule } from '@angular/common';

@NgModule({
    imports: [CommonModule],
    exports: [GaugeComponent],
    declarations: [GaugeComponent],
    providers: [],
})
export class GaugeComponentModule { }
