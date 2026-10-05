import { Component, OnInit,Input, ViewChild, ElementRef,DoCheck, AfterViewInit, ViewEncapsulation, OnChanges } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';
@Component({
    selector: 'BulletsComponent',
	templateUrl: 'bullet.component.html',
	encapsulation:ViewEncapsulation.None
})

export class BulletComponent implements OnInit, AfterViewInit,OnChanges,DoCheck {
	@ViewChild('formlabelWrapper',{static:false}) bulletFormlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) bulletFormlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) bulletFormContentWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) bulletFormWrapperClassRef!:ElementRef;

	
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";  
    id:any;
    value:any;
    attrs:any;
    bulletOptions = [];
    toolTip:any; 
    tooltipCls:any;  
    cssCls:any;
    apprCls:any;
    ordlCls:any;
    showClass:any;

	@Input()
    public props:any;

	@Input()
	public rowIndex:any;

	@Input()
    public row:any;


    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {}

    ngOnInit() {
    this.id = (this.rowIndex != undefined)?this.props.id + "_" + this.rowIndex:this.props.id;
	this.value = this.props.value;
	this.attrs = this.props.attrs;
	this.bulletOptions = [];
	this.toolTip = ""; 
	this.tooltipCls = "";
	this.cssCls = "";
	this.apprCls=" pri";
	this.ordlCls="ordl ";
	this.showClass = "";

	 if(this.attrs.options == "N") {
	     this.showClass = " sno";
	 }
	 if (this.attrs.cssclasses != undefined) {
	     this.cssCls = " " + this.attrs.cssclasses;
	 }
	 if (this.attrs.appearance != undefined) {
	     this.apprCls = " " + this.attrs.appearance;
	 }
	 if (this.attrs.tooltip) {
	    this.toolTip = " original-title="+this.attrs.tooltip+"";
	    this.tooltipCls = " tooltipcls";
	 }

   	this.bulletOptions = this.props.attrs.bulletitems;
	this.elm = this.props.id.split("__").pop(); 
	this.elmData = apz.scrMetaData.elmsMap[this.props.id];
	this.containerId = this.elmData.container;
	this.setContent();
	}

	ngAfterViewInit(){
		if(this.props.cntrType == 'FORM' ){
			Object.assign(this.bulletFormlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
			Object.assign(this.bulletFormlabelPropsRef.nativeElement,this.props.labelProps.direct) 
			Object.assign(this.bulletFormContentWrapperRef.nativeElement,this.props.contentWrapper.direct)
			Object.assign(this.bulletFormWrapperClassRef.nativeElement,this.props.wrapperClasses) 

		}
	}

	ngDoCheck(){
		this.setContent();
	}

	ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
    }
	
	setContent(){
		this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
	}

	

	}