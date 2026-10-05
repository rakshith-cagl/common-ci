import { Component, OnInit, Input, ViewChild, ElementRef, AfterViewInit, ViewEncapsulation,DoCheck, OnChanges } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';


@Component({
    selector: 'ProgressStepComponent',
    templateUrl: 'progress-step.component.html',
    encapsulation:ViewEncapsulation.None

})

export class ProgressStepComponent implements OnInit,AfterViewInit,OnChanges,DoCheck {
    @ViewChild('wrapperClasses',{static:false}) progressStepWrapperClassesRef!: ElementRef;
    @ViewChild('labelWrapper',{static:false}) progressStepLabelWrapperRef!: ElementRef;
    @ViewChild('labelProps',{static:false}) progressStepLabelPropsRef!: ElementRef;
    @ViewChild('contentWrapper',{static:false}) progressStepContentWrapperRef!: ElementRef;
    @ViewChild('spanWrapper',{static:false}) progressStepSpanWrapperRef!: ElementRef;
    @Input()
    public props:any;
    id:any;
    attrs:any;
    value:any;
    snoCls:any;
    cssCls:any;
    toolTipCls:any;
    orientCls:any;
    apprCls:any;
    ulWrapperClass:any;
    stepPercent = "";
	actCls = "";
    progressSteps= [];
    step:any;
    steps:any;
    stepName:any;
    stepStatus:any;
    
    @Input()
    public rowIndex:any;

    @Input()
    public row:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";

    constructor(public winRef:WindowRef,public dataServiceUtils:DataService){
	}

  ngOnInit() { 
        this.id = (this.rowIndex != undefined)?this.props.id + "_" + this.rowIndex:this.props.id;
	    this.attrs = this.props.attrs;
	    this.value = this.props.value;
        this.snoCls = (this.attrs.options=="N")?" sno":"";
        this.cssCls = (this.attrs.cssclasses)?this.attrs.cssclasses:"";
        this.toolTipCls =(this.attrs.tooltip)?" tooltipcls":"";
        this.orientCls = (this.attrs.orientation)?this.attrs.orientation:"";
        this.apprCls = (this.attrs.appearance)?this.attrs.appearance:"";
        this.ulWrapperClass = "ett-pgst"+this.toolTipCls+" "+this.cssCls+this.snoCls+" "+this.apprCls+" "+this.orientCls;
    if(apz.isNull(this.props.value)){
        this.progressSteps = this.props.attrs.stepitems;
    }else{
        this.progressSteps = this.props.value;
    }
    this.elmData = apz.scrMetaData.elmsMap[this.props.id];
    this.containerId = this.elmData.container;
    this.elm = this.props.id.split("__").pop(); 
    this.setContent();
  }

  ngAfterViewInit(){
    if(this.props.cntrType == "FORM"){
        Object.assign(this.progressStepWrapperClassesRef.nativeElement, this.props.wrapperClasses);
        Object.assign(this.progressStepLabelWrapperRef.nativeElement, this.props.labelWrapper.direct);
        Object.assign(this.progressStepLabelPropsRef.nativeElement, this.props.labelProps.direct);
        Object.assign(this.progressStepContentWrapperRef.nativeElement, this.props.contentWrapper.direct);
    }
    if(this.progressStepSpanWrapperRef){
        Object.assign(this.progressStepSpanWrapperRef.nativeElement, this.props.spanWrapper);
    }
    } 
    
    ngDoCheck(){
        this.setContent();
    }

    ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
    }

    stepSplit(step:any){
        return step.split('~')
    }

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }
}