import {Component,OnInit,ViewChild,ElementRef,ViewEncapsulation, DoCheck, AfterViewInit,Input,ChangeDetectorRef,
	EventEmitter, Output} from '@angular/core';
import {WindowRef,apz} from "../appzillon.service";
import {DataService} from '../data_angular';

@Component({
	selector:'InputComponent',
	templateUrl:"./inputcomponent.html",
	encapsulation:ViewEncapsulation.None
})

export class InputComponent implements OnInit,DoCheck,AfterViewInit{
	@ViewChild('wrapperClasses',{static:false}) wrapperClassRef!:ElementRef;
	@ViewChild('labelWrapper',{static:false}) labelWrapperRef!:ElementRef;
	@ViewChild('labelProps',{static:false}) labelPropsRef!:ElementRef;
	@ViewChild('contentWrapper',{static:false}) contentWrapperRef!:ElementRef;

	@ViewChild('spanWrapper',{static:false}) spanWrapperRef!:ElementRef;
 	@ViewChild('elmWrapper',{static:false}) elmWrapperRef!: ElementRef; 
	@ViewChild('contentIcon',{static:false}) contentIconRef!: ElementRef; 
	@ViewChild('content',{static:false}) contentRef!: ElementRef; 
	@ViewChild('listOrNavSpanWrapper',{static:false})listOrNavSpanWrapperRef!:ElementRef;
	

	@Input()
	public props:any;
	isWrapped = false;
	iconPosition = "";
	maxLength ='';
	 public currentRec:any;
	 @Input()
    public row:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";
    id:any;
    value:any;
	index:any;
	@Input()
	public rowIndex:any;
	@Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public iconClick: EventEmitter<any> = new EventEmitter<any>();

	@Output()
	public labelIconClick: EventEmitter<any> = new EventEmitter<any>();
	
	@Output()
	public preCallLov: EventEmitter<any> = new EventEmitter<any>();

	@Output()
	public postCallLov: EventEmitter<any> = new EventEmitter<any>();


	 

	constructor(public winRef:WindowRef,public dataServiceUtils:DataService,private cd: ChangeDetectorRef){
	}

	ngOnInit(){
		this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex: this.props.id;
		this.value = this.props.value;
		this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
	if (this.props.hasSymbol == "Y" || this.props.hasIcon == "Y" || this.props.isdateOrDateTime == "Y" || this.props.hasLov == "Y") {
		this.isWrapped = true;
			if(this.props.hasIcon == "Y" || this.props.isdateOrDateTime=="Y" || this.props.hasLov == "Y"){
				this.iconPosition = this.props.contentIcon ? this.props.contentIcon.position : '';
			}
		}
	if(this.props.content.direct.maxLength=="undefined" || this.props.content.direct.maxLength==""){
		this.maxLength = "";
		}
	  	this.elmData = apz.scrMetaData.elmsMap[this.props.id];
		this.elm = this.props.id.split("__").pop();
    	this.containerId = this.elmData.container;
    	this.setContent();
    }	
		ngDoCheck(){
			this.setContent();
		}
		setContent(){
			this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
		}
		setHandler(){
			this.cd.detectChanges();
		}
		isMultiRec(){
			let container = apz.scrMetaData.elmsMap[this.props.id].container;
			return (apz.scrMetaData.containersMap[container].multiRec=="Y")?true:false;
		}	
		ngOnChanges(){
			this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
			this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
		}

	isUiElm() {
		return (apz.scrMetaData.elmsMap[this.props.id].ui == "Y") ? true : false;
	}

	ngAfterViewInit() {
		this.initLabelRefs();
		if(this.contentWrapperRef){
			Object.assign(this.contentWrapperRef.nativeElement, this.props.contentWrapper.direct);
		}
		if(this.contentRef){
			Object.assign(this.contentRef.nativeElement, this.props.content.direct);
		}
		if(this.spanWrapperRef){
			Object.assign(this.spanWrapperRef.nativeElement, this.props.spanWrapper);
		}	
		if(this.elmWrapperRef){
			Object.assign(this.elmWrapperRef.nativeElement, this.props.elmWrapper.direct);
		}	
		if(this.contentIconRef){
			Object.assign(this.contentIconRef.nativeElement, this.props.contentIcon.direct);
			if(this.row){
				for(let keys in this.props.contentIconEvent){
					this.props.contentIconEvent[keys] = this.props.contentIconEvent[keys].replace(this.props.id,this.id);
				}
			}
			if (this.props.hasIconEvent != 'Y') {
				this.dataServiceUtils.eventBinding(this.contentIconRef.nativeElement,this.props.contentIconEvent);
			}
		}
		if(this.wrapperClassRef){
			Object.assign(this.wrapperClassRef.nativeElement,this.props.wrapperClasses);
		}
		if(this.listOrNavSpanWrapperRef){
			Object.assign(this.listOrNavSpanWrapperRef.nativeElement,this.props.spanWrapper);
		}
	}

	initLabelRefs() {
		if(this.labelWrapperRef){
			Object.assign(this.labelWrapperRef.nativeElement, this.props.labelWrapper.direct);
		}	
		if(this.labelPropsRef){
			Object.assign(this.labelPropsRef.nativeElement, this.props.labelProps.direct);
		}
	}

	iconClickEvent(event: any){
        this.iconClick.emit(event);
    }

	labelIconClickEvent(event: any) {
        this.labelIconClick.emit(event);
    }
    
    beforeCallLov(elementId: any, containerId: any) {
		this.preCallLov.emit({'containerId': containerId, 'elementId': elementId});
	}
	
	afterCallLov(elementId: any, containerId: any) {
		this.postCallLov.emit({'containerId': containerId, 'elementId': elementId});
	}

}