import {Input, Component,OnInit,ViewEncapsulation,ElementRef,ViewChild,DoCheck, AfterViewInit,Output,EventEmitter} from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import {DataService} from '../data_angular';

@Component({
	selector:'ButtonComponent',
	templateUrl:"./buttoncomponent.html",
	encapsulation:ViewEncapsulation.None
})

export class ButtonComponent implements OnInit, AfterViewInit,DoCheck{
	@ViewChild('labelWrapper',{static:false}) labelWrapper!: ElementRef;
	@ViewChild('labelProps',{static:false}) labelProps!:ElementRef;
	@ViewChild('labelIconProps',{static:false}) labelIconProps!:ElementRef;
	@ViewChild('contentProps',{static:false}) contentProps!:ElementRef;
	@ViewChild('content',{static:false}) content!:ElementRef;
	@ViewChild('buttonTitle',{static:false}) buttonTitle!:ElementRef;
	@ViewChild('buttonIcon',{static:false}) buttonIconRef!:ElementRef;
	@ViewChild('contentWrapper',{static:false}) contentWrapper!:ElementRef;

	@ViewChild('wrapperClasses',{static:false}) wrapperClasses!:ElementRef;
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
	@Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();



	constructor(public winRef:WindowRef,public dataServiceUtils:DataService){
	}

    ngOnInit() {
		this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
		this.value = this.props.buttonTitle.value || this.props.title;
		this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;

    	this.elmData = apz.scrMetaData.elmsMap[this.props.id];
    	this.containerId = this.elmData.container;
    	this.setContent();
    }

	ngAfterViewInit(){
		if(this.labelWrapper){
			Object.assign(this.labelWrapper.nativeElement, this.props.labelWrapper.direct);
		}	
		if(this.labelProps){
			Object.assign(this.labelProps.nativeElement, this.props.labelProps.direct);
		}	
		if(this.labelIconProps){
			Object.assign(this.labelIconProps.nativeElement, this.props.labelIconProps.direct);
		}	
		if(this.contentProps){
			Object.assign(this.contentProps.nativeElement, this.props.contentWrapper.direct);
		}	
		if(this.content){
			Object.assign(this.content.nativeElement, this.props.content.direct);
		}
		if(this.buttonTitle){
			Object.assign(this.buttonTitle.nativeElement, this.props.buttonTitle.direct);
		}
		if(this.buttonIconRef){
			Object.assign(this.buttonIconRef.nativeElement, this.props.buttonIcon.direct);
		}	
		if(this.contentWrapper){
			Object.assign(this.contentWrapper.nativeElement, this.props.contentWrapper.direct);
		}	
		if(this.wrapperClasses){
			Object.assign(this.wrapperClasses.nativeElement, this.props.wrapperClasses);
		}
	}
    
    ngDoCheck(){
        this.setContent();
    }

	ngOnChanges(){
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
    }
    
    isUiElm(){
        return (apz.scrMetaData.elmsMap[this.props.id].ui=="Y")?true:false;
	}

    setContent(){
        if(this.row){
			if(!this.isUiElm()){
            	this.values = this.dataServiceUtils.getMultiRecContent(this.elmData,this.containerId,this.rowIndex);
				this.elm = this.props.id.split("__").pop(); 
			}else{
				this.value = this.props.buttonTitle?.value || this.props.title;
			}
		}else{
            if(!this.isUiElm()){
                this.values = this.dataServiceUtils.setElmValue(this.props.id);
                this.elm = this.props.id.split("__").pop();
			}
			else{
				this.value = this.props.buttonTitle.value || this.props.title;
			}
        }
    }

    
}