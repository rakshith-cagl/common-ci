import { Component, OnInit, AfterViewInit, ViewEncapsulation, Input, DoCheck, ViewChild, ElementRef,
     OnChanges,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';

@Component({
    selector: 'TextComponent',
    templateUrl: 'text.component.html',
    encapsulation:ViewEncapsulation.None
})

export class TextComponent implements OnInit,AfterViewInit,DoCheck,OnChanges {
    @ViewChild('contentIcon',{static:false}) contentIconRef!:ElementRef;


    @ViewChild('formlabelWrapper',{static:false}) formlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) formlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) formContentWrapperRef!:ElementRef;
    @ViewChild('SpanWrapper',{static:false}) SpanWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) formWrapperClassRef!:ElementRef;
    id:any;
    index:any;
    values:any;
    apz:any =apz;
    elm:any="";
    @Input() props:any;
    @Input()
    public rowIndex:any;
    @Input()
    public row:any;
    elmData:any;
    containerId:any='';
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
     
    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) { 

    }

    ngOnInit() {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ? this.rowIndex:-1;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.elm = this.props.id.split("__").pop();
        this.setContent()
    }

    ngOnChanges() {
         this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ? this.rowIndex:-1;
    }

    ngAfterViewInit(){
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.formlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.formlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.formContentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
            Object.assign(this.formWrapperClassRef.nativeElement,this.props.wrapperClasses) 
        }
        if(this.SpanWrapperRef){
            Object.assign(this.SpanWrapperRef.nativeElement,this.props.spanWrapper);
        }
    }

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }
    ngDoCheck(){
        
        this.setContent()

    }
    
    isUiElm(){
		return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
	}

    
}