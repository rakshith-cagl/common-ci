import { Component, OnInit, Input, ViewChild, ElementRef, AfterViewInit, ChangeDetectorRef,DoCheck,
     ViewChildren, ViewEncapsulation, OnChanges,Output,EventEmitter} from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';

@Component({
    selector: 'RadioButtonComponent',
    templateUrl: 'radio-button.component.html',
    encapsulation:ViewEncapsulation.None

})

export class RadioButtonComponent implements OnInit, AfterViewInit, OnChanges,DoCheck {
    @ViewChild('spanWrapper',{static:false}) spanWrapper!:ElementRef;
    @ViewChildren('content') contentchildrenRef:any;

    @ViewChild('formlabelWrapper',{static:false}) formlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) formlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) formContentWrapperRef!:ElementRef;
    @ViewChild('navSapnwrapper',{static:false}) navSapnwrapperRef!:ElementRef;
    @ViewChild('spanWrapper',{static:false}) spanWrapperRef!:ElementRef;
    @ViewChild('formSpanWrapper',{static:false}) formSpanWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) formWrapperClassRef!:ElementRef;

    @Input()
    public rowIndex:any;
    public rdRowIndex:any;
    @Input()
    public row:any;

    elmData:any;
    containerId='';
    values:any={};
    apz:any = apz;
    elm="";

    @Input()
    public props:any;
    id:any;
    value:any;
    staticOptions = [];
    radioValue:any;
    state:any;
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
    @Output()
	public labelIconClick: EventEmitter<any> = new EventEmitter<any>();

    
    
    constructor(public winRef:WindowRef,public dataServiceUtils:DataService,private cd:ChangeDetectorRef){
	}
    getCheckedVal(optionVal:any){
        if(!this.isUiElm()){
            return this.values[this.elm] === optionVal;
        }
        else{
            return this.props.value === optionVal;

        }
    }
    ngOnInit() {
            this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
            this.rdRowIndex = (this.rowIndex != undefined) ? this.rowIndex : "-1";
            this.state = { checked: false}
	        if(apz.scrMetaData.elmsMap[this.props.id].staticOptions){
            this.staticOptions = apz.scrMetaData.elmsMap[this.props.id].staticOptions;
        } 
 
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.setContent();
        }
        
        ngDoCheck(){
            this.setContent();
        }

        radioClicked(event:any){
          
            if(!this.isUiElm()){
                let val = event.target.value;
                this.values[this.elm] = val;
             
            }else{
                let val = event.target.value;
                this.props.value = val;
            }
            this.cd.detectChanges();
        }

        setContent(){
            if(this.row){
                if(!this.isUiElm()){
                    this.values = this.dataServiceUtils.getMultiRecContent(this.elmData,this.containerId,this.rowIndex);
                    this.elm = this.props.id.split("__").pop(); 
                    
                
                }else{
                    this.value = this.props.value;
                }
            }else{
                if(!this.isUiElm()){
                    this.values = this.dataServiceUtils.setElmValue(this.props.id);
                    this.elm = this.props.id.split("__").pop();
                    
                
                }else{
                    this.value = this.props.value;
                }
            }
        }

        ngOnChanges(){
            
            this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
                   
        }
        isUiElm(){
            return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
        }
 
     ngAfterViewInit(){
        if(this.props.cntrType=="FORM" || this.props.cntrType=="TABLE" || this.props.cntrType=="NAVBAR" || this.props.cntrType=="LIST"){
            this.contentchildrenRef.forEach((element :ElementRef)=> {
            Object.assign(element.nativeElement, this.props.content.direct);
         });
        }
        if((this.props.cntrType == 'NAVBAR' || this.props.cntrType == 'LIST')){
             Object.assign(this.spanWrapperRef.nativeElement,this.props.spanWrapper) 

        }
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.formlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.formlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.formContentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
            Object.assign(this.spanWrapperRef.nativeElement,this.props.spanWrapper) 
            Object.assign(this.formWrapperClassRef.nativeElement,this.props.wrapperClasses) 

        }
    }  
    
    iconClickEvent(event: any){
        this.labelIconClick.emit(event);
    }
}
