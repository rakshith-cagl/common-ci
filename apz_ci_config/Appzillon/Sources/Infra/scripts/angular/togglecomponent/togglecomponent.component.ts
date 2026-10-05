import { Component, OnInit, ViewEncapsulation, Input, ViewChild, ElementRef, AfterViewInit, DoCheck,
     OnChanges, AfterViewChecked,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';
import $ from 'jquery';
@Component({
    selector: 'ToggleComponent',
    templateUrl: 'togglecomponent.component.html',
    encapsulation:ViewEncapsulation.None
})

export class ToggleComponent implements OnInit,AfterViewInit,DoCheck,OnChanges,AfterViewChecked {
    @ViewChild('labelToggleWrapper',{static:false}) labelToggleWrapperRef!:ElementRef;
    @ViewChild('firstLabelBotton',{static:false}) firstLabelBottonRef!:ElementRef;
    @ViewChild('firstLabel',{static:false}) firstLabelRef!:ElementRef;
    @ViewChild('secondLabelBotton',{static:false}) secondLabelBottonRef!:ElementRef;
    @ViewChild('secondLabel',{static:false}) secondLabelRef!:ElementRef;
    @ViewChild('withoutLabelToggleWrapper',{static:false}) withoutLabelToggleWrapperRef!:ElementRef;
    @ViewChild('withoutLabelInput',{static:false}) withoutLabelInputRef!:ElementRef;

    @ViewChild('toggleFormlabelWrapper',{static:false}) toggleFormlabelWrapperRef!:ElementRef;
    @ViewChild('toggleFormlabelProps',{static:false}) toggleFormlabelPropsRef!:ElementRef;
    @ViewChild('toggleFormContentWrapper',{static:false}) toggleFormContentWrapperRef!:ElementRef;
    @ViewChild('navSapnwrapper',{static:false}) toggleNavSapnwrapperRef!:ElementRef;
    @ViewChild('tableSpanWrapper',{static:false}) toggleTableSpanWrapperRef!:ElementRef;
    @ViewChild('toggleFormSpanWrapper',{static:false}) toggleFormSpanWrapperRef!:ElementRef;
    @ViewChild('toggleFormWrapperClass',{static:false}) toggleFormWrapperClassRef!:ElementRef;

    @Input() props :any;
    id:any;
	index :any;
    @Input()
    public rowIndex: any;
    @Input()
    public row: any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";
    value:any;
    checked:boolean=false;
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
    @Output()
	public labelIconClick: EventEmitter<any> = new EventEmitter<any>();

    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {
     }
    ngOnInit() {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.value = this.props.defaultvalue;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.elm = this.props.id.split("__").pop();
        this.setContent();
    }

    ngOnChanges(){
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1; 
         this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id; 
    }

    ngDoCheck(){
        this.setContent();
      }

    ngAfterViewChecked() {
        this.setContent()
        this.setContentAfterViewinit()
    }

    setContentAfterViewinit() {
        if (!this.isUiElm()) {
            this.setContentForNonUIElm();
        } else {
            this.setContentForUIElm();
        }
    }

    setContentForNonUIElm() {
        if (this.props.ToggleType === 'WITHOUTLABEL') {
            if (this.props.content.direct.checkedval === this.values[this.elm]) {
                this.setCheckedVal();
            } else {
               this.setUncheckedVal();
            }
        }
        if (this.props.ToggleType === 'WITHLABEL') {
            if (this.props.firstButton.direct.value === this.values[this.elm]) {
                $('#' + this.id + '_00').prop("checked", "true")
            } else if (this.props.secondButton.direct.value === this.values[this.elm]) {
                $('#' + this.id + '_11').prop("checked", "true")
            }
        }
    }

    setCheckedVal() {
        $('#' + this.id).prop("checked", true)
        $('#' + this.id).prop("value", this.props.content.direct.checkedval)
        this.checked = true;
    }

    setUncheckedVal() {
        $('#' + this.id).prop("checked", false)
        $('#' + this.id).prop("value", this.props.content.direct.uncheckedval)
        this.checked = false;
    }

    setContentForUIElm() {
        if (this.props.ToggleType === 'WITHOUTLABEL') {
            if (this.props.content.direct.checkedval === this.value) {
                this.setCheckedVal();
            } else {
               this.setUncheckedVal();
            }
        }
        if (this.props.ToggleType === 'WITHLABEL') {
            if (this.props.firstButton.direct.value === this.value) {
                $('#' + this.id + '_00').prop("checked", "true")
                if (!this.row) {
                    this.value = this.props.firstButton.direct.value;
                }
            } else if (this.props.secondButton.direct.value === this.value) {
                $('#' + this.id + '_11').prop("checked", "true")
                if (!this.row) {
                    this.value = this.props.secondButton.direct.value;
                }
            }
        }

    }


    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }

    onCheckedChange(event: any){
        if(!this.isUiElm()){
            
            
            if(true === event.target.checked){
                
                this.values[this.elm]  =  this.props.content.direct.checkedval
               
                $('#'+ this.id).prop("value",this.props.content.direct.checkedval)
              
              }else {

               $('#'+ this.id).prop("value",this.props.content.direct.uncheckedval)
                 this.values[this.elm] = this.props.content.direct.uncheckedval

              }
              

        }else{

              if(true === event.target.checked){

                $('#'+ this.id).prop("value",this.props.content.direct.checkedval)
                this.value  =  this.props.content.direct.checkedval
              
              }else {

                    $('#'+ this.id).prop("value",this.props.content.direct.uncheckedval)
                 this.value = this.props.content.direct.uncheckedval
              }
            
        }
    }
    isUiElm(){
		return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
    }
    onChangeLabel(event: any){
        if(!this.isUiElm()){
           
            
            if(this.props.firstButton.direct.value === event.target.value){
                
                this.values[this.elm] = this.props.firstButton.direct.value
              }else if(this.props.secondButton.direct.value === event.target.value){
                 
                 this.values[this.elm]  = this.props.secondButton.direct.value
              }
              

        }else{
            if(this.props.firstButton.direct.value === event.target.value){
              
              this.value = this.props.firstButton.direct.value
            }else if(this.props.secondButton.direct.value === event.target.value){
               
               this.value = this.props.secondButton.direct.value
            }
            
            
        }
    }
    ngAfterViewInit(){
       
        if((this.props.cntrType == 'NAVBAR' || this.props.cntrType == 'LIST')){
            Object.assign(this.toggleNavSapnwrapperRef.nativeElement,this.props.spanWrapper) 

        }
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.toggleFormlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.toggleFormlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.toggleFormContentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
            Object.assign(this.toggleFormSpanWrapperRef.nativeElement,this.props.spanWrapper) 
            Object.assign(this.toggleFormWrapperClassRef.nativeElement,this.props.wrapperClasses) 

        }
        if(this.props.cntrType == 'TABLE' ){
            Object.assign(this.toggleTableSpanWrapperRef.nativeElement,this.props.spanWrapper) 
        }
        if(this.props.ToggleType === 'WITHLABEL' ){
            Object.assign(this.labelToggleWrapperRef.nativeElement,this.props.toggleWrapper.direct);
            Object.assign(this.firstLabelRef.nativeElement,this.props.firstLabel.direct);
            Object.assign(this.secondLabelRef.nativeElement,this.props.secondLabel.direct);
            Object.assign(this.firstLabelBottonRef.nativeElement,this.props.firstButton.direct);
            Object.assign(this.secondLabelBottonRef.nativeElement,this.props.secondButton.direct);
        }else{
            Object.assign(this.withoutLabelToggleWrapperRef.nativeElement,this.props.toggleWrapper.direct);
            Object.assign(this.withoutLabelInputRef.nativeElement,this.props.content.direct);

        }
    }

    iconClickEvent(event: any){
        this.labelIconClick.emit(event);
    }

    
}