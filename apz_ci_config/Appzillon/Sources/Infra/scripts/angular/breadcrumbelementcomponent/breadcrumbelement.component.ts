import { Component, ViewEncapsulation, Input, Output, ViewChild, ElementRef, AfterViewInit, EventEmitter } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from '../data_angular';


@Component({
    selector: 'BreadCrumbElementComponent',
    templateUrl: 'breadcrumbelement.component.html',
    encapsulation:ViewEncapsulation.None
})

export class BreadCrumbElementComponent implements AfterViewInit {
    @ViewChild('breadCrumbWrapperClass',{static:false}) breadCrumbWrapperClassRef!:ElementRef;
    @ViewChild('breadCrumbClass',{static:false}) breadCrumbClassRef!:ElementRef;
    @ViewChild('titleIcon',{static:false}) titleIconRef!:ElementRef;

    @Input()
    props:any;
    apz:any = apz;

    @Output() preBreadcrumbAction = new EventEmitter<object>();
	@Output() postBreadcrumbAction = new EventEmitter<object>();

    constructor(public winRef:WindowRef,public dataServiceUtils:DataService){
	}

    ngAfterViewInit(){
        if(this.breadCrumbWrapperClassRef){
            Object.assign(this.breadCrumbWrapperClassRef.nativeElement,this.props.breadCrumbWrapperClass) 
        }
        if(this.breadCrumbClassRef){
            Object.assign(this.breadCrumbClassRef.nativeElement,this.props.breadCrumbClass) ;
        }
        if(this.titleIconRef){
            Object.assign(this.titleIconRef.nativeElement,this.props.titleIcon.direct) 
        }
    }

    //Appzillon breadCrumbAction call back method 
	beforeBreadcrumbAction(event:any) {
		this.preBreadcrumbAction.emit(event);
		return true;
	}

	//Appzillon breadCrumbAction click call back method 
	afterBreadcrumbAction(event:any) {
		this.postBreadcrumbAction.emit(event);
	}
}