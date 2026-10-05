import { Component, ViewEncapsulation,Input, AfterViewInit, ViewChild, ElementRef } from '@angular/core';

@Component({
    selector: 'BreadCrumbComponent',
    templateUrl: 'breadcrumb.component.html',
    encapsulation:ViewEncapsulation.None
})

export class BreadCrumbComponent implements AfterViewInit {
    @ViewChild('content',{static:false}) contentRef!:ElementRef;
    @Input()
    props:any;

    ngAfterViewInit(){
        if(this.contentRef){
        Object.assign(this.contentRef.nativeElement,this.props.content.direct) 
        }
    }
}