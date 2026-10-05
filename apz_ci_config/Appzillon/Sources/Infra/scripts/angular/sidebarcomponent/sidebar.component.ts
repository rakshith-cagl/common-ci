import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
    selector: 'SideBarComponent',
    templateUrl: 'sidebar.component.html',
    encapsulation:ViewEncapsulation.None
})

export class SideBarComponent {
    @Input()
    public props:any;
}