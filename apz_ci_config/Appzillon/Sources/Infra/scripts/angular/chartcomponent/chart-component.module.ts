import { NgModule } from '@angular/core';
import { ChartComponent } from './chart.component';
import { CommonModule } from '@angular/common';

@NgModule({
    imports: [CommonModule],
    exports: [ChartComponent],
    declarations: [ChartComponent],
    providers: [],
})
export class ChartModule { }
