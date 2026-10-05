import {Component,ViewChild,ElementRef,ViewEncapsulation,Input} from '@angular/core';

@Component({
	selector:'GridColComponent',
	templateUrl:"./gridcolcomponent.html",
	encapsulation:ViewEncapsulation.None
})

export class GridColComponent{
	@ViewChild('colWrapper',{static:false})colWrapper!: ElementRef;
	@Input()
	public props:any;
	ngAfterViewInit() {
		Object.assign(this.colWrapper.nativeElement, this.props.ColWrapper.direct);
	}
}