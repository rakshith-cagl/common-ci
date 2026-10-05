import {Component,ViewEncapsulation,Input} from '@angular/core';

@Component({
	selector:'SecRowComponent',
	templateUrl:"./secrowcomponent.html",
	encapsulation:ViewEncapsulation.None
})

export class SecRowComponent{
	@Input()
	public props:any;
}