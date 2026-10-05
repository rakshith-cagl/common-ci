import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
    selector: 'NavbarComponent',
    templateUrl: 'navbar.component.html',
    encapsulation:ViewEncapsulation.None

})

export class NavbarComponent {
    @Input()
    public props:any;
}