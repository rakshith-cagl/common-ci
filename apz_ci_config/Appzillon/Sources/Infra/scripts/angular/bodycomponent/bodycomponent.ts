import {Component,ViewEncapsulation,Input} from '@angular/core';
@Component({
	selector:'BodyComponent',
	templateUrl:"./bodycomponent.html",
	encapsulation:ViewEncapsulation.None
})

export class BodyComponent{
	@Input()
	public props:any;
}
