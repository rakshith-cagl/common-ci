import { Component, OnInit, ViewEncapsulation, Input, ViewChild, ElementRef, AfterViewInit, DoCheck,
     OnChanges,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';
import $ from 'jquery';

@Component({
    selector: 'TagsInputComponent',
    templateUrl: 'tagsInputComponent.component.html',
    encapsulation:ViewEncapsulation.None
})

export class TagsInputComponent implements OnInit,AfterViewInit,DoCheck,OnChanges {
    @ViewChild('tagsWrapper',{static:false}) tagsWrapperRef!:ElementRef;
    @ViewChild('tagsInputContent',{static:false}) InputContentRef!:ElementRef;
    @ViewChild('tagsIconWrapper',{static:false}) tagsIconWrapperRef!:ElementRef;

    @ViewChild('tagsFormlabelWrapper',{static:false}) formlabelWrapperRef!:ElementRef;
    @ViewChild('tagsFormlabelProps',{static:false}) formlabelPropsRef!:ElementRef;
    @ViewChild('tagsFormContentWrapper',{static:false}) formContentWrapperRef!:ElementRef;
    @ViewChild('navSapnwrapper',{static:false}) navSapnwrapperRef!:ElementRef;
    @ViewChild('tableSpanWrapper',{static:false}) tableSpanWrapperRef!:ElementRef;
    @ViewChild('tagsFormSpanWrapper',{static:false}) formSpanWrapperRef!:ElementRef;
    @ViewChild('tagsFormWrapperClass',{static:false}) formWrapperClassRef!:ElementRef;

    @Input() props :any;
    @Input()
    public rowIndex:any;
    @Input()
    public row:any;
    @Output()
	public labelIconClick: EventEmitter<any> = new EventEmitter<any>();
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
   

    id:any;
    index :any;
    apz:any = apz;
    values:any={};
    elm='';
    elmData:any;
    value:any;
    containerId:any='';
    
     
    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {
     }   
    ngOnInit() {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.value = this.props.value?this.props.value:'';
        this.setContent()
    }
    
    ngDoCheck(){
        this.setContent()
    }
    
    setContent() {
        if (this.row) {
            if (!this.isUiElm()) {
                this.values = this.dataServiceUtils.getMultiRecContent(this.elmData, this.containerId, this.rowIndex);
                this.elm = this.props.id.split("__").pop();
                if (!this.isUiElm() && $('#' + this.id)[0]) {
                    if (apz.getElmValue(this.id) !== this.values[this.elm]) {
                        apz.setElmValue(this.id, this.values[this.elm], true);
                    }
                }
            }
        } else {
            this.setContentForNonUIElm()
        }
    }

    setContentForNonUIElm() {
        if (!this.isUiElm()) {
            this.values = this.dataServiceUtils.setElmValue(this.props.id);
            this.elm = this.props.id.split("__").pop();
            if (!this.isUiElm() && $('#' + this.id)[0]) {
                if (apz.getElmValue(this.id) !== this.values[this.elm]) {
                    apz.setElmValue(this.id, this.values[this.elm], true);
                }
            }
        }
    }
    
    getDataValue(){
        return this.values[this.elm] || "";
    }
    
    ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
    }

    isUiElm(){
		return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
	}
   
    ngAfterViewInit(){
        Object.assign(this.tagsWrapperRef.nativeElement,this.props.tagsWrapper);
        Object.assign(this.InputContentRef.nativeElement,this.props.content.direct);
        if((this.props.cntrType == 'NAVBAR' || this.props.cntrType == 'LIST')){
            Object.assign(this.navSapnwrapperRef.nativeElement,this.props.spanWrapper) 
        }
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.formlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.formlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.formContentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
            Object.assign(this.formSpanWrapperRef.nativeElement,this.props.spanWrapper) 
            Object.assign(this.formWrapperClassRef.nativeElement,this.props.wrapperClasses) 
        }
        if(this.props.cntrType == 'TABLE' ){
            Object.assign(this.tableSpanWrapperRef.nativeElement,this.props.spanWrapper) 
        }
        
    }

    iconClickEvent(event: any){
        this.labelIconClick.emit(event);
    }

    
}