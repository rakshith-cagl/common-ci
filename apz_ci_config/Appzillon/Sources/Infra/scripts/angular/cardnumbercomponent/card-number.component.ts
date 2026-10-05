import { Component, OnInit, Input, ViewChild,DoCheck, ElementRef, AfterViewInit,ChangeDetectorRef, 
    ViewEncapsulation, OnChanges,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';

const Constants:any ={
    LEFT:"LEFT",
    CENTER:"CENTER",
    RIGHT:"RIGHT",
    DISABLED:"DISABLED",
    READONLY:"READONLY",
}

@Component({
    selector: 'CardNumberComponent',
    templateUrl: 'card-number.component.html',
    encapsulation:ViewEncapsulation.None
})

export class CardNumberComponent implements OnInit,AfterViewInit,DoCheck,OnChanges{
    @ViewChild('wrapperClasses',{static:false}) wrapperClassesRef!: ElementRef;
    @ViewChild('labelWrapper',{static:false}) labelWrapperRef!: ElementRef;
    @ViewChild('labelProps',{static:false}) labelPropsRef!: ElementRef;
    @ViewChild('contentWrapper',{static:false}) contentWrapperRef!: ElementRef;
    @ViewChild('spanWrapper',{static:false}) spanWrapperRef!: ElementRef;
    @ViewChild('state',{static:false}) stateRef!: ElementRef;

    @Input()
    public props:any;
    id:any;
    value:any;
    attrs:any;
    snoCls:any;
    cssCls:any;
    toolTipCls:any;
    contentAllignCls:any;
    apprCls:any;
    index:any;
    accCls="";
    placeHolder="";
    rowNo="";
    stateAttr:any;
    width="100";
    value1:any;
    value2:any;
    value3:any;
    value4:any;
    num:any;

    @Input()
    public rowIndex:any;

    @Input()
    public row:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();

    
    constructor(public winRef:WindowRef,public dataServiceUtils: DataService,private cd: ChangeDetectorRef) {
    }

    ngOnInit() { 
        this.id = (this.rowIndex != undefined)?this.props.id + "_" + this.rowIndex:this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;

	    this.value = this.props.attrs.defaultvalue;
        this.attrs = this.props.attrs;
        this.snoCls = (this.attrs.options=="N")?" sno":" ";
        this.cssCls = (this.attrs.cssclasses)?this.attrs.cssclasses:" ";
        this.toolTipCls =(this.attrs.tooltip)?" tooltipcls":" ";
       
        this.apprCls = (this.attrs.appearance)?this.attrs.appearance:"";
    if (this.attrs.placeholder) {
        this.placeHolder = this.attrs.placeholder;
    }
    if (this.attrs.rowno != undefined && this.attrs.rowno != -1) {
        this.rowNo = "_" + this.attrs.rowno;
    }
	this.assignContentAlignmentClass();
    if (this.attrs.state == Constants.DISABLED) {
        this.stateAttr = { disabled: 'disabled' };
    } else if (this.attrs.state == Constants.READONLY) {
        this.stateAttr = { readOnly: 'readOnly' };
    } else {
        this.stateAttr = { stateAttr: '' };
    }
    if (this.attrs.width) {
        this.width = this.attrs.width;
    }

     this.elmData = apz.scrMetaData.elmsMap[this.props.id];
     this.containerId = this.elmData.container;
     this.setContent();
     }
     
	assignContentAlignmentClass() {
		if (this.attrs.contentalignment == Constants.LEFT) {
			this.contentAllignCls = "lft";
		} else if (this.attrs.contentalignment == Constants.CENTER) {
			this.contentAllignCls = "cen";
		} else if (this.attrs.contentalignment == Constants.RIGHT) {
			this.contentAllignCls = "rht";
		}
	}

     ngDoCheck(){
        this.setContent();
     }

     setHandler(){
        this.cd.detectChanges();
    }
 
     setContent(){
         if(this.row){
             if(!this.isUiElm()){
                this.values = this.dataServiceUtils.getMultiRecContent(this.elmData,this.containerId,this.rowIndex);
                this.elm = this.props.id.split("__").pop();
                this.setContentForNonUIElm();
             }else{
                this.setContentForUIElm();
            }
        }else{
             if(!this.isUiElm()){
                 this.values = this.dataServiceUtils.setElmValue(this.id);
                 this.elm = this.props.id.split("__").pop();
                 this.setContentForNonUIElm();
             }else{
				this.setContentForUIElm();
				 }
         }
     }
     
	setContentForUIElm() {
		if (!apz.isNull(this.value)) {
			if (this.value.indexOf(" ") == -1) {
				this.value1 = this.value.substr(0, 4);
				this.value2 = this.value.substr(4, 4);
				this.value3 = this.value.substr(8, 4);
				this.value4 = this.value.substr(12, 4);
				this.num = this.value1 + ' ' + this.value2 + ' ' + this.value3 + ' ' + this.value4;
			}
		}
	}
	
	setContentForNonUIElm() {
		if (!apz.isNull(this.values[this.elm])) {
			if (this.values[this.elm].indexOf(" ") == -1) {
				this.value1 = this.values[this.elm].substr(0, 4);
				this.value2 = this.values[this.elm].substr(4, 4);
				this.value3 = this.values[this.elm].substr(8, 4);
				this.value4 = this.values[this.elm].substr(12, 4);
				this.num = this.value1 + ' ' + this.value2 + ' ' + this.value3 + ' ' + this.value4;
			}
		}
	}

 
     ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
     }
     
     isUiElm(){
         return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
     }
     saveValue(props:any,event:any){
        let val:any;
        if(!this.isUiElm()){
            val = event.target.value;
            if(val)
            this.values[this.elm] = val.split(" ").join("");    
        }else{
            val = event.target.value;
            if(val)
             this.value = val.split(" ").join("");
        }
     }
    
    ngAfterViewInit(){
        if(this.props.cntrType == "FORM"){
            Object.assign(this.wrapperClassesRef.nativeElement, this.props.wrapperClasses);
            Object.assign(this.labelWrapperRef.nativeElement, this.props.labelWrapper.direct);
            if (this.labelPropsRef)
                Object.assign(this.labelPropsRef.nativeElement, this.props.labelProps.direct);
            Object.assign(this.contentWrapperRef.nativeElement, this.props.contentWrapper.direct);
        }
        if(this.spanWrapperRef){
            Object.assign(this.spanWrapperRef.nativeElement, this.props.spanWrapper);

        }
        if(this.stateRef){
            Object.assign(this.stateRef.nativeElement, this.stateAttr);
        }
    }
}
