import { Component, OnInit, ViewEncapsulation, Input, ViewChild, ElementRef, AfterViewInit, DoCheck,
     OnChanges,ChangeDetectorRef,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';
import $ from 'jquery';
@Component({
    selector: 'StepperComponent',
    templateUrl: 'steppercomponent.component.html',
    encapsulation:ViewEncapsulation.None
})

export class StepperComponent implements OnInit,AfterViewInit,DoCheck,OnChanges {
    @ViewChild('steppearWrapper',{static:false}) stepperWrapperRef!:ElementRef;
    @ViewChild('stepperleftButton',{static:false}) stepperleftButtonRef!:ElementRef;
    @ViewChild('stepperInputContent',{static:false}) stepperInputContentRef!:ElementRef;
    @ViewChild('stepperRightButton',{static:false}) stepperRightButtonRef!:ElementRef;
    @ViewChild('stepperRightIcon',{static:false}) stepperRightIconRef!:ElementRef;
    @ViewChild('stepperleftIcon',{static:false}) stepperleftIconRef!:ElementRef;


    @ViewChild('stepperFormlabelWrapper',{static:false}) stepperFormlabelWrapperRef!:ElementRef;
    @ViewChild('stepperFormlabelProps',{static:false}) stepperFormlabelPropsRef!:ElementRef;
    @ViewChild('stepperFormContentWrapper',{static:false}) stepperFormContentWrapperRef!:ElementRef;
    @ViewChild('navSapnwrapper',{static:false}) stepperNavSapnwrapperRef!:ElementRef;
    @ViewChild('tableSpanWrapper',{static:false}) stepperTableSpanWrapperRef!:ElementRef;
    @ViewChild('stepperFormSpanWrapper',{static:false}) stepperFormSpanWrapperRef!:ElementRef;
    @ViewChild('stepperFormWrapperClass',{static:false}) stepperFormWrapperClassRef!:ElementRef;

    
    id:any;
    index :any;
    apz:any = apz;
    values:any  ={};
    value:any;
    elm='';
    elmData:any;
    containerId:any='';
    
    @Input() props :any;

    @Input()
	 public rowIndex:any;
	 @Input()
     public row:any;
     @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
    
    @Output()
	public labelIconClick: EventEmitter<any> = new EventEmitter<any>();

    constructor(public winRef:WindowRef, public dataServiceUtils:DataService,private cd: ChangeDetectorRef) {
     }   
    ngOnInit() {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.value = this.props.value;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.elm = this.props.id.split("__").pop();
        this.setContent()
    }

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId)
    }

    ngDoCheck(){
        
        this.setContent()
    }

    setHandler(){
        this.cd.detectChanges();
    }

    isUiElm(){
		return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
	}
    
    handleStepper(event:any):void{
        apz.handleStepperclick(event.currentTarget);
        let value = (<any>$('#' + this.id)[0])?.value;
        if(!this.isUiElm()){
                this.values[this.elm] =  value;
        }else{
            this.props.value = value;
        }
        this.cd.detectChanges();
   }

   ngOnChanges(){
    this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
    this.index = (this.rowIndex != undefined) ?this.rowIndex:-1; 
    }
    validateThevalues(pobj:any){
        let inputVal = parseFloat(pobj.value);
        let value = inputVal
         let minVal = parseFloat(pobj.getAttribute("min"));
         let maxVal = parseFloat(pobj.getAttribute("max"));
            if(inputVal <= minVal) {
                pobj.value = minVal;
                value = pobj.value
    
            } else if(inputVal >= maxVal) {
                pobj.value = maxVal;
                value = pobj.value
            }
            if(!this.isUiElm()){
                this.values[this.elm] =  value;
          }else{
             this.props.value = value;
          }
      

    }
    ngAfterViewInit(){
            Object.assign(this.stepperWrapperRef.nativeElement,this.props.stepperWrapper.direct);
            Object.assign(this.stepperleftButtonRef.nativeElement,this.props.leftButton.direct);
            Object.assign(this.stepperRightButtonRef.nativeElement,this.props.rightButton.direct);
            Object.assign(this.stepperInputContentRef.nativeElement,this.props.content.direct);
        if((this.props.cntrType == 'NAVBAR' || this.props.cntrType == 'LIST')){
            Object.assign(this.stepperNavSapnwrapperRef.nativeElement,this.props.spanWrapper) 

        }
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.stepperFormlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.stepperFormlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.stepperFormContentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
            Object.assign(this.stepperFormSpanWrapperRef.nativeElement,this.props.spanWrapper) 
            Object.assign(this.stepperFormWrapperClassRef.nativeElement,this.props.wrapperClasses) 

        }
        if(this.props.cntrType == 'TABLE' ){
            Object.assign(this.stepperTableSpanWrapperRef.nativeElement,this.props.spanWrapper) 
        }
        
    }


    iconClickEvent(event: any){
        this.labelIconClick.emit(event);
    }

    
}