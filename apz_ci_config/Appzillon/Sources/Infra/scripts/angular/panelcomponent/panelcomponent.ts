import {Component,Input,ViewEncapsulation, Output, EventEmitter} from '@angular/core';
import { apz } from '../appzillon.service';


@Component({
	selector:'PanelComponent',
	templateUrl:"./panelcomponent.html",
	encapsulation:ViewEncapsulation.None
})

export class PanelComponent {
	@Input()
	public props:any;
	@Input()
	public apz=apz;
	
	@Output() preTabAction = new EventEmitter<object>();
	@Output() postTabAction = new EventEmitter<object>();

	ifCheckForPanelType(){
		if(this.props.panelType == 'SIMPLE' || this.props.panelType == 'ACCORDION' || this.props.panelType == 'CAROUSEL' || this.props.panelType == 'COLLAPSIBLE'){
			return true;
		}else{
			return false;
		}
	}
	
	 //Appzillon collapsibleAction call back method
	beforeTabAction(event:any) {
		this.preTabAction.emit(event);
		return true;
	}

	//Appzillon collapsibleAction click call back method
	afterTabAction(event:any) {
		this.postTabAction.emit(event);
	}
	
	
}
