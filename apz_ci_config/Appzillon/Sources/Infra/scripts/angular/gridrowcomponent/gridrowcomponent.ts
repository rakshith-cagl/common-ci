import {Component,ViewEncapsulation,Input} from '@angular/core';

@Component({
	selector:'GridRowComponent',
	templateUrl:"./gridrowcomponent.html",
	encapsulation:ViewEncapsulation.None
})

export class GridRowComponent{
 @Input()
 public props:any={};
}