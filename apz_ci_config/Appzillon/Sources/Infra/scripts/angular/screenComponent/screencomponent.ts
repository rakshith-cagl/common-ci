import {Component,Input,ViewEncapsulation} from '@angular/core';
import { WindowRef } from '../appzillon.service';

@Component({
	selector:'ScreenComponent',
	templateUrl:"./screencomponent.html",
	encapsulation:ViewEncapsulation.None
})

export class ScreenComponent {
	constructor(public winref:WindowRef){}
	@Input()
	public props:any;
	checkScreenType(){
		return this.props.screenType=='PG'?true:false;
	}
}
