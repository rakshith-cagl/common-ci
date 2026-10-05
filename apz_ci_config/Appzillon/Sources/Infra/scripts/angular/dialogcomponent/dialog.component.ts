import { Component, Input, ViewChild, ElementRef, AfterViewInit, ViewEncapsulation } from '@angular/core';

@Component({
    selector: 'DialogComponent',
    templateUrl: 'dialog.component.html',
    encapsulation:ViewEncapsulation.None
})

export class DialogComponent implements AfterViewInit {
    @ViewChild('dialogWrapper',{static:false}) dialogWrapperRef!: ElementRef;

    @Input()
    public props:any;

    ngAfterViewInit(){
        if(this.dialogWrapperRef){
            Object.assign(this.dialogWrapperRef.nativeElement, this.props.dialogWrapper.direct);
        }
    }
}