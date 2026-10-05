import { Component, Input, ViewEncapsulation} from '@angular/core';

@Component({
    selector: 'HeaderComponent',
    templateUrl: 'header.component.html',
    encapsulation:ViewEncapsulation.None

})

export class HeaderComponent {
    @Input()
    public props:any;
}