import { Component, OnInit, Input, ViewEncapsulation, ViewChild, ElementRef,
    ChangeDetectorRef, AfterViewInit, DoCheck,Output,EventEmitter} from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';
import $ from 'jquery';

@Component({
    selector: 'SortCodeAccountComponent',
    templateUrl: 'sortcode-account.component.html',
    encapsulation:ViewEncapsulation.None

})

export class SortCodeAccountComponent implements OnInit, AfterViewInit,DoCheck  {
    @ViewChild('wrapperClasses',{static:false}) wrapperClassesRef!: ElementRef;
    @ViewChild('labelWrapper',{static:false}) labelWrapperRef!: ElementRef;
    @ViewChild('labelProps',{static:false}) labelPropsRef!: ElementRef;
    @ViewChild('contentWrapper',{static:false}) contentWrapperRef!: ElementRef;
    @ViewChild('spanWrapper',{static:false}) spanWrapperRef!: ElementRef;
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
    snoClass:any;
    cssClass:any;
    toolTipCls:any;
    contentAllignCls:any;
    widthCls = "";
    widthAttr="";
    spanClasses:any;
    inputClases:any;
    rowno:any;
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();

    constructor(public winRef:WindowRef,public dataServiceUtils:DataService,private cd: ChangeDetectorRef) {
    }

    ngOnInit() {
        this.id = (this.rowIndex != undefined)?this.props.id + "_" + this.rowIndex:this.props.id;
        this.value = this.props.attrs.defaultvalue;
        this.attrs = this.props.attrs;
        this.snoClass = (this.attrs.options=="N")?" sno":"";
        this.cssClass = (this.attrs.cssclasses)?this.attrs.cssclasses:"";
        this.toolTipCls =(this.attrs.tooltip)?" tooltipcls":"";
        this.contentAllignCls = "";
        
        if(this.attrs.contentalignment == "LEFT"){
            this.contentAllignCls = "lft";
        }else if(this.attrs.contentalignment == "RIGHT"){
            this.contentAllignCls = " rht";
        }else{
            this.contentAllignCls = " cen";
        }
        this.rowno = (this.attrs.rowno)?this.attrs.rowno:"";
         if (this.attrs.width!="CUSTOM") {
                 this.widthCls = " etw-"+this.attrs.width;
         } else {
                 this.widthAttr = " width:"+this.attrs.customwidth+this.attrs.customwidthtype;
         }
    
        this.spanClasses = "ett-srta "+this.attrs.appearance+this.cssClass+this.snoClass+this.toolTipCls;
        this.inputClases = "ett-inpt "+this.attrs.appearance+this.contentAllignCls+this.widthCls;
        this.setContent();
    }

    ngAfterViewInit(){
        if(this.labelWrapperRef){
            Object.assign(this.labelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
        }
        if(this.labelPropsRef){
            Object.assign(this.labelPropsRef.nativeElement,this.props.labelProps.direct) 
        }
        if(this.contentWrapperRef){
            Object.assign(this.contentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
        }
        if(this.spanWrapperRef){
            Object.assign(this.spanWrapperRef.nativeElement,this.props.spanWrapper) 
        }
        if(this.wrapperClassesRef){
            Object.assign(this.wrapperClassesRef.nativeElement,this.props.wrapperClasses) 
        }
    }

    ngDoCheck(){
        this.setContent();
    }
    
    setContent(){
        if(this.row){
            this.values = this.dataServiceUtils.getMultiRecContent(this.elmData,this.containerId,this.rowIndex);
            this.elm = this.props.id.split("__").pop(); 
        }else{
            if(!this.dataServiceUtils.isUiElm(this.props.id)){
                this.values = this.dataServiceUtils.setElmValue(this.props.id);
                this.elm = this.props.id.split("__").pop();
                if(!apz.isNull(this.values[this.elm])){
                    this.value = this.values[this.elm].substr(0,8);
                }
            }else{
                if(!apz.isNull(this.value)){
                    this.value = this.value.substr(0,8);
                }
            }
        }
    }
    
     saveValue(props:any,obj:any){
        if(!this.dataServiceUtils.isUiElm(this.props.id)){
	        let value:any ="";
	        let elmObj = $(obj).parents("span:first")[0];
	        let objRowNo = apz.getObjRowNumber(elmObj);
	        let id = apz.getObjIdWORowNumber(elmObj);
	        let rowNo = "";
	        if (objRowNo!=-1) {
	            rowNo = "_"+objRowNo;
	        }
	        value = $(elmObj).find("#"+id+"_00"+rowNo+"").val();
	        this.values[this.elm] = value;
        }
    }

 
}