import { Component, OnInit,Input, ViewChild, ElementRef, AfterViewInit, ViewEncapsulation,DoCheck, OnChanges } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';

@Component({
    selector: 'SeparatorComponent',
    templateUrl: 'separator.component.html',
    encapsulation:ViewEncapsulation.None
})

export class SeparatorComponent implements OnInit, AfterViewInit,OnChanges,DoCheck {
    @ViewChild('formlabelWrapper',{static:false}) separatorFormlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) separatorFormlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) separatorFormContentWrapperRef!:ElementRef;
    @ViewChild('navSapnwrapper',{static:false}) navSapnwrapperRef!:ElementRef;
    @ViewChild('formSpanWrapper',{static:false}) separatorFormSpanWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) separatorFormWrapperClassRef!:ElementRef;

    @Input()
    public rowIndex:any;

    @Input()
    public row:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";

    @Input()
    public props:any;
    id:any;
    value:any;
    attrs:any;
    cssCls:any;
    apprCls:any;
    
   constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {}

    ngOnInit() { 
    this.id = (this.rowIndex!= undefined)?this.props.id + "_" + this.rowIndex:this.props.id;
	this.attrs = this.props.attrs;
	this.value = this.attrs.text;
    this.cssCls = (this.attrs.cssclasses)?this.attrs.cssclasses:"";
    this.apprCls = (this.attrs.appearance)?this.attrs.appearance:"";
    this.elm = this.props.id.split("__").pop();
    this.elmData = apz.scrMetaData.elmsMap[this.props.id];
    this.containerId = this.elmData.container;
    this.setContent();
    }

    ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
    }

    ngAfterViewInit(){
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.separatorFormlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.separatorFormlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.separatorFormContentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
            Object.assign(this.separatorFormSpanWrapperRef.nativeElement,this.props.spanWrapper) 
            Object.assign(this.separatorFormWrapperClassRef.nativeElement,this.props.wrapperClasses) 
        }
    }
    
    ngDoCheck(){
        this.setContent();
    }

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }
   
}