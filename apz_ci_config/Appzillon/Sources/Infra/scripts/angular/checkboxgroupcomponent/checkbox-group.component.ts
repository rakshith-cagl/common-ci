import { Component, OnInit, EventEmitter, Input, Output, ViewEncapsulation, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { DataService } from '../data_angular';

@Component({
    selector: 'CheckboxGroupComponent',
    templateUrl: 'checkbox-group.component.html',
    encapsulation:ViewEncapsulation.None
})

export class CheckboxGroupComponent implements OnInit, AfterViewInit {
    @ViewChild('formlabelWrapper',{static:false}) formlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) formlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) formContentWrapperRef!:ElementRef;
    @ViewChild('formSpanWrapper',{static:false}) formSpanWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) formWrapperClassRef!:ElementRef;
    @ViewChild('contentIcon',{static:false}) contentIconRef!:ElementRef;

    @Input()
    public rowIndex:any;

    @Input()
    public row:any;

    @Input()
	public props:any;
    id:any;
    value:any;
	@Output()
	public iconClick: EventEmitter<any> = new EventEmitter<any>();

    
	constructor(public dataServiceUtils:DataService){}
    ngOnInit() {
        this.id = (this.props.index != undefined) ? this.props.id + "_" + this.props.index : this.props.id;
        this.value = this.props.value;
     }

     ngAfterViewInit(){
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.formlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.formlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.formContentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
            Object.assign(this.formSpanWrapperRef.nativeElement,this.props.spanWrapper) 
            Object.assign(this.formWrapperClassRef.nativeElement,this.props.wrapperClasses) 
        }
     }

    iconClickEvent(event: any){
        this.iconClick.emit(event);
    }
}
