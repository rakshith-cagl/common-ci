import {Component, TemplateRef, ViewEncapsulation,ViewChild,ElementRef,ChangeDetectorRef, DoCheck,Input} from '@angular/core';
import { WindowRef } from '../appzillon.service';
import { DataService } from '../data_angular';
@Component({
	selector:'FormComponent',
	templateUrl:"./formcomponent.html",
	encapsulation:ViewEncapsulation.None})

export class FormComponent implements DoCheck {
	@ViewChild('formEvents',{static:false})formEventsRef!: ElementRef;
	@Input() templateRef!: TemplateRef<any>;
	@Input()
	public props:any;
	@Input()
	public titleValue:string="";
	public metadata:any;
	public apz:any;
	public render=true;
	public totalRecs=true;

	constructor(public winRef:WindowRef,public cd:ChangeDetectorRef,public dataService:DataService){
	}
	ngOnInit(){
		this.apz = this.winRef.nativeWindow.apz;
		this.metadata = this.apz.scrMetaData.containersMap[this.props.id];
	}

	ngDoCheck(){
		this.metadata = this.apz.scrMetaData.containersMap[this.props.id];
		this.totalRecs = true;
	}

}