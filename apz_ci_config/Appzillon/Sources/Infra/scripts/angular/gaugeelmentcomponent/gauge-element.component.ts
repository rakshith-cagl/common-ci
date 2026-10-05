import { Component, OnInit, Input, Output, ViewEncapsulation, ViewChild, 
    ElementRef,ChangeDetectionStrategy, AfterViewInit,DoCheck, EventEmitter, 
    AfterViewChecked,ChangeDetectorRef } from '@angular/core';
import {WindowRef,apz} from "../appzillon.service";
import {DataService} from '../data_angular';
import $ from 'jquery';

@Component({
    selector: 'GuageElementComponent',
    templateUrl: 'gauge-element.component.html',
    encapsulation:ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
})

export class GuageElementComponent implements OnInit, AfterViewInit,DoCheck,AfterViewChecked {
    @ViewChild('gaugeprops',{static:false}) gaugepropsRef!:ElementRef;
    
    @Input()
    props:any;
    @Input()
    public row:any;
    containerId='';
    values:any;
    elm="";
    elmData:any;
    @Input()
    public rowIndex:any;
    id:any;
    gaugeValue="";
    gauges:any;
    forceUpdate = false;
    rendered=false;

    @Output()
	public labelIconClick: EventEmitter<any> = new EventEmitter<any>();
    

    constructor(public winRef:WindowRef,public dataServiceUtils:DataService,private cdRef: ChangeDetectorRef){
	}

    ngOnInit() {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex: this.props.id;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
     }

    ngAfterViewInit(){
        if(this.gaugepropsRef){
            Object.assign(this.gaugepropsRef.nativeElement, this.props.direct);
        }
    }
    ngDoCheck(){
        this.setContent();
    }
    ngAfterViewChecked(){
        if(!this.rendered )
        this.paintGuage();
    }
    async renderData(){
       if(!this.forceUpdate){
        await apz.data.reRenderComponent(this.elmData.container);
        this.rendered = false;
        this.forceUpdate = true;
       }
    }
     paintGuage(){
        let obj = $("#"+this.id)[0];
        if(this.gaugeValue && obj && this.elmData.type == "GAUGE"){
			   let myObj = this;
			   let gauges = new myObj.winRef.nativeWindow.Apz.Gauges(apz);
			   let gaugeId = apz.getObjIdWORowNumber(obj);
			   let gaugeObj = apz.scrMetaData.gaugesMap[gaugeId];
			   let newGaugeObj = apz.copyJSONObject(gaugeObj);
			   newGaugeObj.uiId = myObj.id;
			   newGaugeObj.value = myObj.gaugeValue;
			   try {
			       if(!myObj.rendered){
			             myObj.rendered = true;
			             gauges.paintGauge(newGaugeObj,myObj.renderData,myObj);
			       }
			   } catch(e) {
			      console.log("Problems with Gauges Library");
			   }
        }
    }
   
    setContent(){
        if(this.row){
            this.values = this.dataServiceUtils.getMultiRecContent(this.elmData,this.containerId,this.rowIndex);
            this.elm = this.props.id.split("__").pop(); 
            this.gaugeValue = this.values[this.elm];
        } else{
            if(!this.dataServiceUtils.isUiElm(this.props.id)){
                this.values = this.dataServiceUtils.setElmValue(this.props.id);
                this.elm = this.props.id.split("__").pop();
                this.gaugeValue = this.values[this.elm];
            } 
        }   
    }

    labelIconClickEvent(event: any) {
        this.labelIconClick.emit(event);
    }
}