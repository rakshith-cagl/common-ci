import { Component, OnInit, ViewEncapsulation, Input, ViewChild, ElementRef, AfterViewInit, DoCheck,
     OnChanges,ChangeDetectorRef,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from '../../angular/data_angular';

@Component({
    selector: 'TextAreaComponent',
    templateUrl: 'text-area.component.html',
    encapsulation:ViewEncapsulation.None
})

export class TextAreaComponent implements OnInit, AfterViewInit,DoCheck,OnChanges {
    @ViewChild('textSpanWrapper',{static:false}) textSpanWrapperRef!:ElementRef;
    @ViewChild('elmSpanWrapper',{static:false}) elmSpanWrapperRef!:ElementRef;
    @ViewChild('textAreaElement',{static:false}) textAreaElementRef!:ElementRef;
    @ViewChild('textAreaIconButtonWrapper',{static:false}) textAreaIconButtonWrapperRef!:ElementRef;

    @ViewChild('textAreaFormlabelWrapper',{static:false}) textAreaFormlabelWrapperRef!:ElementRef;
    @ViewChild('textAreaFormlabelProps',{static:false}) textAreaFormlabelPropsRef!:ElementRef;
    @ViewChild('textAreaFormContentWrapper',{static:false}) textAreaFormContentWrapperRef!:ElementRef;
    @ViewChild('navSapnwrapper',{static:false}) textAreaNavSapnwrapperRef!:ElementRef;
    @ViewChild('tableSpanWrapper',{static:false}) textAreaTableSpanWrapperRef!:ElementRef;
    @ViewChild('textAreaFormSpanWrapper',{static:false}) textAreaFormSpanWrapperRef!:ElementRef;
    @ViewChild('textAreaFormWrapperClass',{static:false}) textAreaFormWrapperClassRef!:ElementRef;

    @Input() props:any;
    
    @Input()
    public row:any;
    @Input()
    public rowIndex:any;

    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
    @Output()
	public labelIconClick: EventEmitter<any> = new EventEmitter<any>();

    id:any;
    index:any;
    //apz:any = apz;
    values:any={};
    elm='';
    elmData:any;
    containerId:any='';
    
    constructor(public winRef:WindowRef, public dataServiceUtils:DataService,private cd: ChangeDetectorRef) {
     }

    ngOnInit() {
        this.id=(this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.elm = this.props.id.split("__").pop(); 
        this.setContent();
    }

    ngAfterViewInit(){
        if(this.props.hasLov === 'Y'){
            Object.assign(this.textAreaElementRef.nativeElement,this.props.textSpanWrapper.direct);
            Object.assign(this.elmSpanWrapperRef.nativeElement,this.props.elmSpanWrapper.direct);
            Object.assign(this.textAreaIconButtonWrapperRef.nativeElement,this.props.btnWrapper.direct);
        }
        
        if((this.props.cntrType == 'NAVBAR' || this.props.cntrType == 'LIST')){
            Object.assign(this.textAreaNavSapnwrapperRef.nativeElement,this.props.spanWrapper) 

        }
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.textAreaFormlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.textAreaFormlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.textAreaFormContentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
            Object.assign(this.textAreaFormSpanWrapperRef.nativeElement,this.props.spanWrapper) 
            Object.assign(this.textAreaFormWrapperClassRef.nativeElement,this.props.wrapperClasses) 
        }
        if(this.props.cntrType == 'TABLE' ){
            Object.assign(this.textAreaTableSpanWrapperRef.nativeElement,this.props.spanWrapper) 

        }
        if(this.textAreaElementRef){
            Object.assign(this.textAreaElementRef.nativeElement,this.props.content.direct);
        }
    }

    isUiElm(){
		return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
	}

    ngDoCheck(){
        this.setContent();
    }
    ngOnChanges(){
        this.id=(this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1; 
    }
 
    setHandler(){
        this.cd.detectChanges();
    }
    onChange(event:Event):void{
       //  this.apz.changeHandler(this.props, event)
    } 

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }
    
    onBlur(event:Event):void{ 
        // this.apz.blurHandler(this.props, event)
    }
    onClick(){
        //this.apz.lov.callLov(this.id, this.props.containerId, this.props.appId)
    }

    iconClickEvent(event: any){
        this.labelIconClick.emit(event);
    }
   
}