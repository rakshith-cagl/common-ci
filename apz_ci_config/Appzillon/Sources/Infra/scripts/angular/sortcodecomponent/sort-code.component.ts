import { Component, OnInit, Input, ViewEncapsulation, ViewChild,DoCheck, ElementRef, AfterViewInit,
    ChangeDetectorRef, OnChanges,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz} from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';
import $ from 'jquery';

@Component({
    selector: 'SortCodeComponent',
    templateUrl: 'sort-code.component.html',
    encapsulation:ViewEncapsulation.None
})

export class SortCodeComponent implements OnInit, AfterViewInit, OnChanges,DoCheck {
    @ViewChild('wrapperClasses',{static:false}) sortCodeWrapperClassesRef!: ElementRef;
    @ViewChild('labelWrapper',{static:false}) sortCodeLabelWrapperRef!: ElementRef;
    @ViewChild('labelProps',{static:false}) sortCodeLabelPropsRef!: ElementRef;
    @ViewChild('contentWrapper',{static:false}) sortCodeContentWrapperRef!: ElementRef;
    @ViewChild('spanWrapper',{static:false}) sortCodeSpanWrapperRef!: ElementRef;
    
    
    id:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";
    value:any;
    value1="";
    value2="";
    value3="";
    value1Len=0;
    value2Len=0;
    value3Len=0;
    attrs:any;
    snoClass:any;
    cssClass:any;
    toolTipCls:any;
    contentAllignCls:any;
    accCls:any;
    rowNo:any;
    index:any;
    inputClasses:any;
    spanClasses:any;

    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
    @Input()
    public rowIndex:any;

    @Input()
    public row:any;
    
    @Input()
    public props:any;

    constructor(public winRef:WindowRef,public dataServiceUtils:DataService,private cd: ChangeDetectorRef) {
    }

    ngOnInit() {
        this.id = (this.rowIndex != undefined)?this.props.id + "_" + this.rowIndex:this.props.id;
	    this.value = this.props.attrs.defaultvalue;
   
         this.attrs = this.props.attrs;
         this.snoClass = (this.attrs.options=="N")?" sno":"";
         this.cssClass = (this.attrs.cssclasses)?this.attrs.cssclasses:"";
         this.toolTipCls =(this.attrs.tooltip)?" tooltipcls":"";
         this.contentAllignCls = ""; this.accCls=""; this.rowNo="";
        if(this.attrs.contentalignment == "LEFT"){
            this.contentAllignCls = "lft";
        }else if(this.attrs.contentalignment == "RIGHT"){
            this.contentAllignCls = " rht";
        }else{
            this.contentAllignCls = " cen";
        }
         if (this.attrs.account=="Y") {
            this.accCls = " accountreq";
        }
	    this.inputClasses = "ett-inpt "+this.attrs.appearance+this.contentAllignCls;
        this.spanClasses = "ett-srtc "+this.attrs.appearance+this.cssClass+this.snoClass+this.toolTipCls;
        if (this.attrs.rowno != undefined && this.attrs.rowno != -1) {
            this.rowNo = "_"+this.attrs.rowno;
         }
         this.elmData = apz.scrMetaData.elmsMap[this.props.id];
         this.containerId = this.elmData.container;
         this.setContent();
         }

         ngAfterViewInit(){
            if(this.props.cntrType == "FORM"){
                Object.assign(this.sortCodeWrapperClassesRef.nativeElement, this.props.wrapperClasses);
                Object.assign(this.sortCodeLabelWrapperRef.nativeElement, this.props.labelWrapper.direct);
                Object.assign(this.sortCodeLabelPropsRef.nativeElement, this.props.labelProps.direct);
                Object.assign(this.sortCodeContentWrapperRef.nativeElement, this.props.contentWrapper.direct);
                 }
                 if(this.sortCodeSpanWrapperRef){
                    Object.assign(this.sortCodeSpanWrapperRef.nativeElement, this.props.spanWrapper);
                 }
        }
         
         ngDoCheck(){
             this.setContent();
         }

         ngOnChanges(){
             
            this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
            this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
           
        }

    setContent() {
        if (this.row) {
            this.values = this.dataServiceUtils.getMultiRecContent(this.elmData, this.containerId, this.rowIndex);
            this.elm = this.props.id.split("__").pop();
        } else {
            if (!this.dataServiceUtils.isUiElm(this.props.id)) {
                this.setContentForNonUIElm();
            } else {
                this.setContentForUIElm();
            }
        }
    }

    setContentForUIElm() {
        if (!apz.isNull(this.value)) {
            this.value1Len = $("#" + this.id + "_00").val() ? (<any>$("#" + this.id + "_00").val())!.length : 0;
            this.value2Len = $("#" + this.id + "_11").val() ? (<any>$("#" + this.id + "_11").val())!.length : 0;
            this.value3Len = $("#" + this.id + "_22").val() ? (<any>$("#" + this.id + "_22").val())!.length : 0;
            if (this.value1Len == 0 && this.value2Len == 0 && this.value3Len == 0) {
                this.value1 = this.value.substr(0, 2);
                this.value2 = this.value.substr(2, 2);
                this.value3 = this.value.substr(4, 2);
            } else {
                this.value1 = this.value.substr(0, this.value1Len);
                this.value2 = this.value.substr(this.value1Len, this.value2Len);
                this.value3 = this.value.substr(this.value2Len + this.value1Len, this.value3Len);
            }
        }
    }

    setContentForNonUIElm() {
        this.values = this.dataServiceUtils.setElmValue(this.props.id);
        this.elm = this.props.id.split("__").pop();
        if (!apz.isNull(this.values[this.elm])) {
            this.value1Len = $("#" + this.id + "_00").val() ? (<any>$("#" + this.id + "_00").val())!.length : 0;
            this.value2Len = $("#" + this.id + "_11").val() ? (<any>$("#" + this.id + "_11").val())!.length : 0;
            this.value3Len = $("#" + this.id + "_22").val() ? (<any>$("#" + this.id + "_22").val())!.length : 0;
            if (this.value1Len == 0 && this.value2Len == 0 && this.value3Len == 0) {
                this.value1 = this.values[this.elm].substr(0, 2);
                this.value2 = this.values[this.elm].substr(2, 2);
                this.value3 = this.values[this.elm].substr(4, 2);
            } else {
                this.value1 = this.values[this.elm].substr(0, this.value1Len);
                this.value2 = this.values[this.elm].substr(this.value1Len, this.value2Len);
                this.value3 = this.values[this.elm].substr(this.value2Len + this.value1Len, this.value3Len);
            }

        }
    }
    
         
     saveValue(props:any,obj:any,event?:any){
        if(!this.dataServiceUtils.isUiElm(this.props.id)){
	        let value:any = "";
	        let elmObj = $(obj).parents("span:first")[0];
	        let id = apz.getObjIdWORowNumber(elmObj);
	        value = $(elmObj).find("#"+id+"_00").val();
	        value = value + $(elmObj).find("#"+id+"_11").val();
	        value = value + $(elmObj).find("#"+id+"_22").val();
	        this.values[this.elm] = value;
        }
    }

}