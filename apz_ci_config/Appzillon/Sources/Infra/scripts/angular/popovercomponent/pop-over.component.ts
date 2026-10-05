import { Component, Input, ViewEncapsulation, AfterViewInit, ViewChild, ElementRef } from '@angular/core';

@Component({
    selector: 'PopOverComponent',
    templateUrl: 'pop-over.component.html',
    encapsulation:ViewEncapsulation.None
})

export class PopOverComponent implements AfterViewInit {
    @ViewChild('content',{static:false}) contentRef!:ElementRef;    @Input()
    public props:any;

    ngAfterViewInit(){
        if(this.contentRef){
            Object.assign(this.contentRef.nativeElement, this.props.content.direct);
        }
    }
}