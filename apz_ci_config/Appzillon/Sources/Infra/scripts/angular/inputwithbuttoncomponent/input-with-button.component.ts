import { Component, OnInit,Input, ViewChild, ElementRef, AfterViewInit, DoCheck, ViewEncapsulation, 
    OnChanges,ChangeDetectorRef,EventEmitter,Output } from '@angular/core';
import { WindowRef,apz} from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';


@Component({
    selector: 'InputWithButtonComponent',
    templateUrl: 'input-with-button.component.html',
    encapsulation:ViewEncapsulation.None
})

export class InputWithButtonComponent implements OnInit,AfterViewInit, DoCheck, OnChanges {
    @ViewChild('inpBtnWrapper',{static:false}) inpBtnWrapperRef!:ElementRef;
    @ViewChild('buttonWrapper',{static:false}) buttonWrapperRef!:ElementRef;
    @ViewChild('content',{static:false}) contentRef!:ElementRef;
    @ViewChild('buttonContent',{static:false}) buttonContentRef!:ElementRef;
    @ViewChild('buttonTitle',{static:false}) buttonTitleRef!:ElementRef;
    @ViewChild('wrapperClasses',{static:false}) inpBtnWrapperClassesRef!: ElementRef;
    @ViewChild('labelWrapper',{static:false}) inpBtnLabelWrapperRef!: ElementRef;
    @ViewChild('labelProps',{static:false}) inpBtnLabelPropsRef!: ElementRef;
    @ViewChild('contentWrapper',{static:false}) inpBtnContentWrapperRef!: ElementRef;
    @ViewChild('spanWrapper',{static:false}) spanWrapperRef!:ElementRef;
   
    @Input()
    public rowIndex:any;

    @Input()
    public row:any;
    apz:any = apz;
    values:any;
    elm="";
    containerId='';
    elmData:any;

    @Input()
    public props:any;
    id:any;
    index:any;
    value:any;
    
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();

	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();

    @Output()
    public buttonClick: EventEmitter<any> = new EventEmitter<any>();
    
    @Output()
    public labelIconClick: EventEmitter<any> = new EventEmitter<any>();

    constructor(public winRef:WindowRef,public dataServiceUtils:DataService,private cd:ChangeDetectorRef){
	}

    ngOnInit() {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.value = this.props.defaultValue;
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.elm = this.props.id.split("__").pop();
        this.setContent();
        }
        ngAfterViewInit(){
            if(this.props.cntrType == "FORM"){
               Object.assign(this.inpBtnWrapperClassesRef.nativeElement, this.props.wrapperClasses);
               Object.assign(this.inpBtnLabelWrapperRef.nativeElement, this.props.labelWrapper.direct);
               Object.assign(this.inpBtnLabelPropsRef.nativeElement, this.props.labelProps.direct);
               Object.assign(this.inpBtnContentWrapperRef.nativeElement, this.props.contentWrapper.direct);
           }
            if(this.props.cntrType == "FORM" || this.props.cntrType == "TABLE" || this.props.cntrType == "NAVBAR" || this.props.cntrType == "LIST"){
                Object.assign(this.inpBtnWrapperRef.nativeElement, this.props.inpBtnWrapper);
            }
            if(this.props.buttonPosition == 'LEFT' || this.props.buttonPosition == 'RIGHT'){
               Object.assign(this.buttonWrapperRef.nativeElement, this.props.buttonWrapper.direct);
            
               Object.assign(this.buttonContentRef.nativeElement, this.props.buttonContent.direct);
               Object.assign(this.contentRef.nativeElement, this.props.content.direct);
            }
           if(this.props.hasButtonIcon == "Y" || this.props.hasButtonIcon == "N"){
               if(this.props.iconPosition == "RIGHT" || this.props.iconPosition == "LEFT"){
                Object.assign(this.buttonTitleRef.nativeElement,this.props.buttonTitle.direct);
               }
           }
           if(this.spanWrapperRef){
               Object.assign(this.spanWrapperRef.nativeElement,this.props.spanWrapper);
           }
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

        ngOnChanges(){
          
            this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
            this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
            
           
        }
    
    isUiElm() {
        return (apz.scrMetaData.elmsMap[this.props.id].ui == "Y") ? true : false;
    }

    buttonClickEvent(event: any){
	    this.buttonClick.emit(event);
    }

    labelIconClickEvent(event: any) {
        this.labelIconClick.emit(event);
    }
}
