import { Component, OnInit, Input, ViewChild, ElementRef, AfterViewInit, ViewEncapsulation,DoCheck,
     OnChanges,ChangeDetectorRef,Output,EventEmitter} from '@angular/core';
import { WindowRef,apz} from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';


@Component({
    selector: 'SliderComponent',
    templateUrl: 'slider.component.html',
    encapsulation:ViewEncapsulation.None
})

export class SliderComponent implements OnInit,AfterViewInit,OnChanges,DoCheck {
    @ViewChild('content',{static:false})contentRef!: ElementRef;
    @ViewChild('wrapperClasses',{static:false}) sliderWrapperClassesRef!: ElementRef;
    @ViewChild('labelWrapper',{static:false}) sliderLabelWrapperRef!: ElementRef;
    @ViewChild('labelProps',{static:false}) sliderLabelPropsRef!: ElementRef;
    @ViewChild('contentWrapper',{static:false}) sliderContentWrapperRef!: ElementRef;
    @ViewChild('spanWrapper',{static:false}) sliderSpanWrapperRef!: ElementRef;
    
    @Input()
    public rowIndex:any;
   
    @Input()
    public row:any;
    
    @Input()
    public props:any;
   
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	
    @Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
    
    @Output()
	public labelIconClick: EventEmitter<any> = new EventEmitter<any>();
    
    id:any;
    value:any;
    index:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";
    
    constructor(public winRef:WindowRef,public dataServiceUtils:DataService,private cd: ChangeDetectorRef){
	}

    ngOnInit() {
        this.index = (this.rowIndex!= undefined) ?this.rowIndex:-1;
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.elm = this.props.id.split("__").pop();
        this.setContent();
    }
    
    ngDoCheck(){
        this.setContent();
    }

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId)	
    }
    setHandler(){
        this.cd.detectChanges();
    }
    ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
    }
    
    isUiElm(){
        return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
    }

     setValue(props:any,event:any){
        if(!this.isUiElm()){
            let val = event.target.value;
            this.values[this.elm] = val;
            
        }
        if(this.isUiElm()){
            let val = event.target.value;
            this.props.value = val;
            
        }
     

     }
     

     ngAfterViewInit(){
         if(this.props.cntrType == "FORM"){
	        Object.assign(this.sliderWrapperClassesRef.nativeElement, this.props.wrapperClasses);
	        Object.assign(this.sliderLabelWrapperRef.nativeElement, this.props.labelWrapper.direct);
	        Object.assign(this.sliderLabelPropsRef.nativeElement, this.props.labelProps.direct);
	        Object.assign(this.sliderContentWrapperRef.nativeElement, this.props.contentWrapper.direct);
         }
        if(this.props.cntrType == "FORM" || this.props.cntrType == "TABLE" || this.props.cntrType == "NAVBAR" || this.props.cntrType == "LIST"){
        	Object.assign(this.contentRef.nativeElement, this.props.content.direct);
        	Object.assign(this.sliderSpanWrapperRef.nativeElement, this.props.spanWrapper);
        }
        
    }

    iconClickEvent(event: any){
        this.labelIconClick.emit(event);
    }
}