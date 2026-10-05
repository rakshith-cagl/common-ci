import { Component, Input, ViewChild, ElementRef, AfterViewInit, ViewEncapsulation} from '@angular/core';

@Component({
    selector: 'ModalComponent',
    templateUrl: 'modal.component.html',
    encapsulation:ViewEncapsulation.None
})

export class ModalComponent implements AfterViewInit {  
    @ViewChild('parentDivWrapper',{static:false}) parentDivWrapperRef!:ElementRef;
    @ViewChild('modalDivWrapper',{static:false}) modalDivWrapperRef!:ElementRef;  
    @Input()
    public props:any;

    ngAfterViewInit(){
        if(this.parentDivWrapperRef){
            Object.assign(this.parentDivWrapperRef.nativeElement, this.props.parentDivWrapper.direct);
        }
        if(this.modalDivWrapperRef){
            Object.assign(this.modalDivWrapperRef.nativeElement, this.props.modalDivWrapper.direct);
        }
    }
}