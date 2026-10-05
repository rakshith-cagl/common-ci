import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
    selector: 'FooterComponent',
    templateUrl: 'footer.component.html',
    encapsulation:ViewEncapsulation.None
})

export class FooterComponent {
    @Input()
    public props:any;
}