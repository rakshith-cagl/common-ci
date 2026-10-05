import { Component, OnInit, Input, ViewChild, ElementRef, AfterViewInit,DoCheck, ViewEncapsulation,
     OnChanges,Output,EventEmitter} from '@angular/core';
import {apz} from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';

@Component({
    selector: 'HyperLinkComponent',
    templateUrl: 'hyper-link.component.html',
    encapsulation:ViewEncapsulation.None
})

export class HyperLinkComponent implements OnInit,AfterViewInit,OnChanges,DoCheck {
    @ViewChild('anchorTag',{static:false}) anchorTagRef!:ElementRef;
    @ViewChild('formlabelWrapper',{static:false}) formlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) formlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) formContentWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) formWrapperClassRef!:ElementRef;
    @ViewChild('contentIcon',{static:false}) contentIconRef!:ElementRef;

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
    index:any;
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
    public focus: EventEmitter<any> = new EventEmitter<any>();
    @Output()
    public iconClick: EventEmitter<any> = new EventEmitter<any>();
    
    constructor(public dataServiceUtils:DataService) { }

    ngOnInit() { 
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.elm = this.props.id.split("__").pop();
        this.setContent();
        }

    ngAfterViewInit(){
        if(this.anchorTagRef){            
            Object.assign(this.anchorTagRef.nativeElement,this.props.anchorTag.direct);
        }
        
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.formlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.formlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.formContentWrapperRef.nativeElement,this.props.contentWrapper.direct)
            Object.assign(this.formWrapperClassRef.nativeElement,this.props.wrapperClasses) 

        }
    }

    ngOnChanges(){
        
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        
        
    }

    ngDoCheck(){
        this.setContent();

    }

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }



    iconClickEvent(event: any){
        this.iconClick.emit(event);
    }
}