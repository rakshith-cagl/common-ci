import {Component,Input,OnInit,ViewChild,ElementRef,ViewEncapsulation} from '@angular/core';

@Component({
	selector:'SecColComponent',
	templateUrl:"./seccolcomponent.html",
	encapsulation:ViewEncapsulation.None
})

export class SecColComponent implements OnInit{
	@ViewChild('spanColWrapper',{static:false}) spanColWrapper!:ElementRef;	
	@Input()
	public props:any;
	columnTag:any;
	ngOnInit(){
		this.columnTag = this.props.cntrType == "NAVBAR" ? "li" : "span";
	}
	containerCheck(){
		return (this.props.cntrType=='NAVBAR')?false:true;
	}
	ngAfterViewInit(){
		if(this.spanColWrapper){
		Object.assign(this.spanColWrapper.nativeElement,this.props.ColWrapper.direct);
		}
	}
}