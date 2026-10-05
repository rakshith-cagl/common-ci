import { Component, OnInit, Input, ElementRef, ViewChild, AfterViewInit, ViewEncapsulation,DoCheck,
     OnChanges,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';

@Component({
    selector: 'ProgressBarComponent',
    templateUrl: 'progress-bar.component.html',
    encapsulation:ViewEncapsulation.None
})

export class ProgressBarComponent implements OnInit, AfterViewInit, OnChanges, DoCheck {
    @ViewChild('ProgressWrapper',{static:false}) ProgressWrapperRef!:ElementRef;
    @ViewChild('divContent',{static:false}) divContentRef!:ElementRef;
    @ViewChild('content',{static:false}) contentRef!:ElementRef;

    @ViewChild('formlabelWrapper',{static:false}) formlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) formlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) formContentWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) formWrapperClassRef!:ElementRef;

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
    widthvaltext:any;
    width:any;
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
    @Output()
	public labelIconClick: EventEmitter<any> = new EventEmitter<any>();

    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {}

    ngOnInit() { 
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.value = this.props.content.direct.value || this.props.content.direct.widthvaltext;
	    this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.setContent();
        }
    

    ngAfterViewInit(){
        if(this.props.progressBarType == "GENERIC" || this.props.progressBarType == "OSSPECIFIC"){
           Object.assign(this.ProgressWrapperRef.nativeElement, this.props.ProgressWrapper.direct);
           Object.assign(this.divContentRef.nativeElement, this.props.divContent.direct);
        }
        if(this.contentRef){
            Object.assign(this.contentRef.nativeElement, this.props.content.direct);
        }
    if(this.props.cntrType == 'FORM' ){
        Object.assign(this.formlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
        Object.assign(this.formlabelPropsRef.nativeElement,this.props.labelProps.direct) 
        Object.assign(this.formContentWrapperRef.nativeElement,this.props.contentWrapper.direct)
        Object.assign(this.formWrapperClassRef.nativeElement,this.props.wrapperClasses) 
    
    }
}
       
    isUiElm(){
        return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
    }
    ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;  
    }

        ngDoCheck(){
            this.setContent();
        }

        setContent(){
            if(this.row){
                if(!this.isUiElm()){
                this.values = this.dataServiceUtils.getMultiRecContent(this.elmData,this.containerId,this.rowIndex);
                this.elm = this.props.id.split("__").pop(); 
                this.widthvaltext = 100 * this.values[this.elm];
                this.width = 100 * this.values[this.elm]
            }else{
                this.value = this.props.content.direct.value || this.props.content.direct.widthvaltext;
            }
        }else{
                if(!this.isUiElm()){
                    this.values = this.dataServiceUtils.setElmValue(this.props.id);
                    this.elm = this.props.id.split("__").pop();
                    this.widthvaltext = 100 * this.values[this.elm];
                    this.width = 100 * this.values[this.elm]
                }else{
                    this.value = this.props.content.direct.value || this.props.content.direct.widthvaltext;
                }
            }
        }



    iconClickEvent(event: any){
        this.labelIconClick.emit(event);
    }
}