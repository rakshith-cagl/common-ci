import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
    selector: 'ChartComponent',
    templateUrl: 'chart.component.html',
    encapsulation:ViewEncapsulation.None
})

export class ChartComponent {
    @Input()
    props:any;
}