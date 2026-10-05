import { Component, OnInit,Input, AfterViewInit, ElementRef, ViewChild, ViewEncapsulation,DoCheck, OnChanges } from '@angular/core';
import { WindowRef,apz} from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';

@Component({
    selector: 'LabelComponent',
    templateUrl: 'label.component.html',
    encapsulation:ViewEncapsulation.None
})

export class LabelComponent implements OnInit,AfterViewInit,OnChanges,DoCheck {
    @ViewChild('content',{static:false})contentRef!:ElementRef;
    @ViewChild('contentIcon',{static:false})contentIconRef!:ElementRef;
    @ViewChild('formlabelWrapper',{static:false}) formlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) formlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) formContentWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) formWrapperClassRef!:ElementRef;

    @Input()
	public props:any;
    @Input()
    public rowIndex:any;
    @Input()
    public row:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";

    id:any;
    index:any;
    value:any;
    
    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {}

    ngOnInit() {
        this.id = (this.rowIndex != undefined)?this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.value = this.props.title;
        this.elm = this.props.id.split("__").pop(); 
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.setContent();
    }

    ngAfterViewInit(){
        if(this.props.hasIcon == "Y"){
            if(this.props.iconPosition == "RIGHT" || this.props.iconPosition == "LEFT"){
        Object.assign(this.contentIconRef.nativeElement,this.props.contentIcon.direct);
            }
        }
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.formlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.formlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.formContentWrapperRef.nativeElement,this.props.contentWrapper.direct)
            Object.assign(this.formWrapperClassRef.nativeElement,this.props.wrapperClasses) 
        }
        
        if(this.contentRef){
            Object.assign(this.contentRef.nativeElement,this.props.content.direct);
        }
    } 

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }

    ngDoCheck(){
        this.setContent();
    }
    
    ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
    }
}