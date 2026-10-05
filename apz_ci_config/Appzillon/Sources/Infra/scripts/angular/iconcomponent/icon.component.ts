import { Component, OnInit, Input, ViewChild, ElementRef,DoCheck, AfterViewInit, ViewEncapsulation,
     OnChanges,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz} from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';

@Component({
    selector: 'IconComponent',
    templateUrl: 'icon.component.html',
    encapsulation:ViewEncapsulation.None
})

export class IconComponent implements OnInit, AfterViewInit,OnChanges,DoCheck {
    @ViewChild('apzEvent',{static:false}) apzEventRef!:ElementRef;

    @ViewChild('formlabelWrapper',{static:false}) iconFormlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) iconFormlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) iconFormContentWrapperRef!:ElementRef;
    @ViewChild('navSapnwrapper',{static:false}) navSapnwrapperRef!:ElementRef;
    @ViewChild('tableSpanWrapper',{static:false}) tableSpanWrapperRef!:ElementRef;
    @ViewChild('formSpanWrapper',{static:false}) formSpanWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) iconFormWrapperClassRef!:ElementRef;

    @Input()
    public rowIndex:any;

    @Input()
    public row:any;
    elmData:any;
    containerId='';
    originalTitle:any;
    values:any;
    apz:any = apz;
    elm="";

    @Input()
    public props:any;
    id:any;
    index:any;
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
   
    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {}

    ngOnInit() { 
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.originalTitle = this.props.IconContent?.direct['original-title'];
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.elm = this.props.id.split("__").pop();
        this.setContent();
        }

    ngDoCheck(){
        this.setContent();
    }

    ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
    }

    ngAfterViewInit(){
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.iconFormlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.iconFormlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.iconFormContentWrapperRef.nativeElement,this.props.contentWrapper.direct)
            Object.assign(this.iconFormWrapperClassRef.nativeElement,this.props.wrapperClasses) 
        }
    }
   
    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }
        

}
