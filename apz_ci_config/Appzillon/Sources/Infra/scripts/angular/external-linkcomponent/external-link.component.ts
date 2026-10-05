import { Component, OnInit, Input, ViewChild,DoCheck, ElementRef, AfterViewInit, ViewEncapsulation, OnChanges } from '@angular/core';
import { WindowRef,apz} from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';

@Component({
    selector: 'ExternalLinkComponent',
    templateUrl: 'external-link.component.html',
    encapsulation:ViewEncapsulation.None
})

export class ExternalLinkComponent implements OnInit, AfterViewInit,OnChanges,DoCheck {
    @ViewChild('formlabelWrapper',{static:false}) extLinkFormlabelWrapperRef!:ElementRef;
    @ViewChild('formlabelProps',{static:false}) extLinkFormlabelPropsRef!:ElementRef;
    @ViewChild('formContentWrapper',{static:false}) extLinkFormContentWrapperRef!:ElementRef;
    @ViewChild('formWrapperClass',{static:false}) extLinkFormWrapperClassRef!:ElementRef;

    @Input()
    public rowIndex:any;

    @Input()
    public row:any;

    @Input()
    public props:any;
    
    elm="";
    id:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    toolTip:any;
    attrs:any;
    value:any;
    toolTipCls ="";
    snoCls:any;
    cssCls:any;
    apprCls:any;
    stateCls:any;
 
    constructor(public winRef:WindowRef, public dataServiceUtils:DataService) {}

    ngOnInit() {
        this.id = (this.rowIndex != undefined)?this.props.id + "_" + this.rowIndex:this.props.id;
        this.attrs = this.props.attrs;
        this.value = this.attrs.defaultvalue;
        this.snoCls = (this.attrs.options=="N")?" sno":" ";
        this.cssCls = (this.attrs.cssclasses)?this.attrs.cssclasses:"";
        this.apprCls = (this.attrs.appearance)?this.attrs.appearance:"";
        this.stateCls = (this.attrs.state == "DISABLED")?" disabled":"";
          if (this.attrs.tooltip) {
            this.toolTip = " original-title="+this.attrs.tooltip+"";
              this.toolTipCls = " tooltipcls";
         }
         this.elmData = apz.scrMetaData.elmsMap[this.props.id];
         this.elm = this.props.id.split("__").pop(); 
         this.containerId = this.elmData.container;
         this.setContent();
    }

    ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
    }

    ngDoCheck(){
        this.setContent();
    }

    ngAfterViewInit(){
        if(this.props.cntrType == 'FORM' ){
            Object.assign(this.extLinkFormlabelWrapperRef.nativeElement,this.props.labelWrapper.direct) 
            Object.assign(this.extLinkFormlabelPropsRef.nativeElement,this.props.labelProps.direct) 
            Object.assign(this.extLinkFormContentWrapperRef.nativeElement,this.props.contentWrapper.direct) 
            Object.assign(this.extLinkFormWrapperClassRef.nativeElement,this.props.wrapperClasses) 
        }
     }

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }
}