import { Component, OnInit, Input, ViewChild, ElementRef, AfterViewInit,DoCheck, ViewEncapsulation, 
    OnChanges,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz} from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';


@Component({
    selector: 'ImageComponent',
    templateUrl: 'image.component.html',
    encapsulation:ViewEncapsulation.None
})

export class ImageComponent implements OnInit, AfterViewInit, OnChanges,DoCheck {
    @ViewChild('spanContent',{static:false}) spanContentRef!:ElementRef;
    @ViewChild('imageContent',{static:false}) imageContentRef!:ElementRef;
    @ViewChild('formlabelWrapper',{static:false}) imgFormlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) imgFormlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) imgFormContentWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) imgFormWrapperClassRef!:ElementRef;

    @Input()
    public props:any;

    @Input()
    public rowIndex:any;

    @Input()
    public row:any;

    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
    
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";
    id:any;
    index:any;
    
       
    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {}

    ngOnInit() {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.elm = this.props.id.split("__").pop();
        this.setContent();
    }

    ngAfterViewInit(){
        if(this.props.cntrType == "FORM" || this.props.cntrType == "TABLE" || this.props.cntrType == "NAVBAR" || this.props.cntrType == "LIST"){
        Object.assign(this.spanContentRef.nativeElement, this.props.spanContent.direct);
        Object.assign(this.imageContentRef.nativeElement, this.props.imageContent.direct);
        }

        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.imgFormlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.imgFormlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.imgFormContentWrapperRef.nativeElement,this.props.contentWrapper.direct)
            Object.assign(this.imgFormWrapperClassRef.nativeElement,this.props.wrapperClasses)
        }
    }

    ngDoCheck(){
        this.setContent();
    }

    ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
    }

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }

}