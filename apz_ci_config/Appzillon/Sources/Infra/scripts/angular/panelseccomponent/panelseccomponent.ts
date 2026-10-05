import {Component,Input,ViewChild,ElementRef,ViewEncapsulation, Output, EventEmitter} from '@angular/core';
import { apz } from '../appzillon.service';

@Component({
	selector:'PanelSecComponent',
	templateUrl:"./panelseccomponent.html",
	encapsulation:ViewEncapsulation.None
})

export class PanelSecComponent{
	@ViewChild('panelSectionDirect',{static:false})panelSectionDirect!: ElementRef;
	@ViewChild('ulWrapper',{static:false})ulWrapper!: ElementRef;
	@ViewChild('accContentWrapper',{static:false})accContentWrapper!: ElementRef;
	@ViewChild('propsDirect',{static:false})propsDirect!: ElementRef;
	@ViewChild('collapsibleContentWrapper',{static:false})collapsibleContentWrapper!: ElementRef;
	@Input()
	public props:any;
	public apz = apz;


	@Output() preCollapsibleAction = new EventEmitter<object>();
	@Output() postCollapsibleAction = new EventEmitter<object>();
	
	ifCheckForPanelType(){
		if(this.props.panelType == 'SIMPLE' || this.props.panelType == 'CAROUSEL' || this.props.panelType == 'COLLAPSIBLE'){
			return true;
		}else{
			return false;
		}
	}

	checkTitleValue(){
		return (this.props.titleValue)?true:false;
	}
	ifCheckForAccordionType(){
		return (this.props.panelType=='ACCORDION')?true:false;
	}

	checkTitleIcon(){
		return (this.props.titleIcon)?true:false;
	}
	ngAfterViewInit(){
		if(this.panelSectionDirect){
			Object.assign(this.panelSectionDirect.nativeElement,this.props.panelSection.direct);
		}
		if(this.accContentWrapper){
			Object.assign(this.accContentWrapper.nativeElement,this.props.accContentWrapper.direct);
		}
		if(this.propsDirect){
			Object.assign(this.propsDirect.nativeElement,this.props.direct);
		}
		if(this.collapsibleContentWrapper){
			Object.assign(this.collapsibleContentWrapper.nativeElement,this.props.collapsibleContentWrapper.direct);
		}
	}


	 //Appzillon collapsibleAction call back method
	beforeCollapsibleAction(event:any) {
		this.preCollapsibleAction.emit(event);
		return true;
	}

	//Appzillon collapsibleAction click call back method
	afterCollapsibleAction(event:any) {
		this.postCollapsibleAction.emit(event);
	}
}