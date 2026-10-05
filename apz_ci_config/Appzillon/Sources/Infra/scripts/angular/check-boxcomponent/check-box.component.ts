import { Component, OnInit, ViewChild,DoCheck, ElementRef, AfterViewInit, ViewEncapsulation, 
    OnChanges, Output,EventEmitter,Input } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from '../data_angular';
import $ from 'jquery';



@Component({
    selector: 'CheckboxComponent',
    templateUrl: 'check-box.component.html',
    encapsulation:ViewEncapsulation.None
})

export class CheckboxComponent implements OnInit,AfterViewInit,OnChanges,DoCheck {
    @ViewChild('checkBoxWrapper',{static:false})checkBoxWrapperRef!:ElementRef;
    @ViewChild('content',{static:false})contentRef!:ElementRef;
    @ViewChild('label',{static:false})labelRef!:ElementRef;
    @ViewChild('formlabelWrapper',{static:false}) formlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) formlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) formContentWrapperRef!:ElementRef;
    @ViewChild('navSapnwrapper',{static:false}) navSapnwrapperRef!:ElementRef;
    @ViewChild('tableSpanWrapper',{static:false}) tableSpanWrapperRef!:ElementRef;
    @ViewChild('formSpanWrapper',{static:false}) formSpanWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) formWrapperClassRef!:ElementRef;
    @ViewChild('contentIcon', { static: false }) contentIconRef!: ElementRef;

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
    index:any;
    state :any;
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public iconClick: EventEmitter<any> = new EventEmitter<any>();


    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {}

    ngOnInit() {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.value = this.props.content.direct.defaultValue;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.state = { checked: false, indeterminate: false }
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.setContent();
        }
        
    ngDoCheck(){
            this.setContent();
    }

    setContent() {
        if (this.row) {
            if (!this.isUiElm()) {
                this.elm = this.props.id.split("__").pop();
                this.values = this.dataServiceUtils.getMultiRecContent(this.elmData, this.containerId, this.rowIndex);
                this.setContentForNonUIElm();
            } else {
                this.setContentForUIElm();
            }

        } else {
            if (!this.isUiElm()) {
                this.elm = this.props.id.split("__").pop();
                this.values = this.dataServiceUtils.setElmValue(this.props.id);
                this.setContentForNonUIElm();
            } else {
                this.setContentForUIElm();
            }
        }
    }

    isUiElm(){
        return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
    }

    setContentForNonUIElm() {
        if ('y' === this.values[this.elm]) {
            this.state.checked = true;
            $("#"+this.id).prop("checked",true);
            $("#"+this.id).prop("unchecked",false)
            $("#"+this.id).prop("indeterminate",false);
        } else if ('i' === this.values[this.elm]) {
            this.state.indeterminate = true;
            this.state.checked = false;

            $("#"+this.id).prop("indeterminate",true);
            $("#"+this.id).prop("checked",false);
            $("#"+this.id).prop("unchecked",false)

        }else{
            this.state.checked = false;
            $("#"+this.id).prop("checked",false);
            $("#"+this.id).prop("unchecked",true)
            $("#"+this.id).prop("indeterminate",false);

        }
    }

    setContentForUIElm() {
        if (this.props.content.direct.checkedval == this.value) {
            this.state.checked = true;
        } else if (this.props.content.direct.indeterminateval == this.value) {
            this.state.indeterminate = true;
        }
    }

     saveValue(props:any, event:any){
        if(!this.isUiElm()){
            if(event.target.checked){
               this.values[this.elm] = "y";
            }else if(event.target.indeterminate){
               this.values[this.elm] = "i";
            } else {
                 this.values[this.elm] = "n";
            }
            
        }else {
            if(event.target.checked){
                this.value = this.props.content.direct.checkedval;
            }else if(event.target.indeterminate){
                this.value = this.props.content.direct.indeterminateval;
            } else {
                this.value = this.props.content.direct.uncheckedval;
            }
            
        }

        }
    
        ngOnChanges(){
            
            this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
            this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
            
        }

    ngAfterViewInit(){
        if(this.checkBoxWrapperRef){
        Object.assign(this.checkBoxWrapperRef.nativeElement, this.props.checkBoxWrapper);
        
        }
        if(this.contentRef){
        Object.assign(this.contentRef.nativeElement, this.props.content.direct);
        Object.assign(this.contentRef.nativeElement, this.state);
        }
        if(this.labelRef){
            Object.assign(this.labelRef.nativeElement, this.props.label.direct);
        }

        if((this.props.cntrType == 'NAVBAR' || this.props.cntrType == 'LIST')){
            Object.assign(this.navSapnwrapperRef.nativeElement,this.props.spanWrapper) 

        }
        if(this.formContentWrapperRef ){
            Object.assign(this.formContentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
        }
        if(this.formWrapperClassRef){
            Object.assign(this.formWrapperClassRef.nativeElement,this.props.wrapperClasses) 

        }
        if(this.formSpanWrapperRef || this.formlabelWrapperRef || this.formlabelPropsRef){
            Object.assign(this.formSpanWrapperRef.nativeElement,this.props.spanWrapper) 
            Object.assign(this.formlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.formlabelPropsRef.nativeElement,this.props.labelProps.direct) 
        }
    }

    iconClickEvent(event: any){
        this.iconClick.emit(event);
    }
}