import { Component, Input } from '@angular/core';

@Component({
    selector: 'GaugeComponent',
    templateUrl: 'gauge.component.html'
})

export class GaugeComponent {
    @Input()
    public props:any;
}