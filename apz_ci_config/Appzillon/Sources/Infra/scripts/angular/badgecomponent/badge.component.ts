import { Component, OnInit,Input, ViewChild, ElementRef,DoCheck, AfterViewInit, ViewEncapsulation,
     OnChanges,Output,EventEmitter } from '@angular/core';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';


@Component({
    selector: 'BadgeComponent',
    templateUrl: 'badge.component.html',
    encapsulation:ViewEncapsulation.None
})

export class BadgeComponent implements OnInit,AfterViewInit,OnChanges,DoCheck {
    @ViewChild('content',{static:false})contentRef!: ElementRef;
    @ViewChild('wrapperClasses',{static:false})badgeWrapperClassesRef!: ElementRef;
    @ViewChild('labelWrapper',{static:false})badgeLabelWrapperRef!: ElementRef;
    @ViewChild('labelProps',{static:false})badgeLabelPropsRef!: ElementRef;
    @ViewChild('contentWrapper',{static:false})badgeContentWrapperRef!: ElementRef;
    // @ViewChild('tableSpanWrapper',{static:false})tableSpanWrapperRef: ElementRef;

    @Input() props :any;
    id:any;
	index :any;
    @Input()
    public rowIndex:any;
    @Input()
    public row:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";
    value:any;
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
    public focus: EventEmitter<any> = new EventEmitter<any>();
    
    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {
    }

    ngOnInit() { 
    this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
	this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
    this.value = this.props.content.direct.defaultValue;

    this.elmData = apz.scrMetaData.elmsMap[this.props.id];
    this.containerId = this.elmData.container;
    this.elm = this.props.id.split("__").pop();
    this.setContent();  
    }

    ngDoCheck(){
        this.setContent();
    }

    ngAfterViewInit(){
        if(this.props.cntrType == "FORM"){
            Object.assign(this.badgeWrapperClassesRef.nativeElement, this.props.wrapperClasses);
            Object.assign(this.badgeLabelWrapperRef.nativeElement, this.props.labelWrapper.direct);
            Object.assign(this.badgeLabelPropsRef.nativeElement, this.props.labelProps.direct);
            Object.assign(this.badgeContentWrapperRef.nativeElement, this.props.contentWrapper.direct);
             }
        if(this.props.cntrType == "NAVBAR" || this.props.cntrType == "LIST" || this.props.cntrType == "TABLE" || this.props.cntrType == "FORM"){
            Object.assign(this.contentRef.nativeElement, this.props.content.direct);
        }
    }

    ngOnChanges() {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
    }

    setContent() {
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId)	
    }
}