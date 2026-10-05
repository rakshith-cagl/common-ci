import { Component, Input, Output, HostListener,  ViewEncapsulation, ViewChild, ElementRef, AfterViewInit,EventEmitter } from '@angular/core';
import $ from 'jquery';
@Component({
    selector: 'MenuComponent',
    templateUrl: 'menu.component.html',
    encapsulation:ViewEncapsulation.None
})

export class MenuComponent implements AfterViewInit {
    @ViewChild('content',{static:false}) contentRef!:ElementRef;
    @ViewChild('contextWrapper',{static:false}) contextWrapperRef!:ElementRef;

    @Input()
    props:any;
    
    @Output()
	public apzMenuClick: EventEmitter<any> = new EventEmitter<any>();


    @HostListener('window:resize', ['$event'])
    onResize(event:any) {
        setTimeout(this.addMobileMenuClickListener.bind(this), 2000);
    }

    ngAfterViewInit(){
        if(this.contentRef){
        	Object.assign(this.contentRef.nativeElement, this.props.content.direct);
        }
        if(this.contextWrapperRef){
        	Object.assign(this.contextWrapperRef.nativeElement, this.props.contextWrapper.direct);    
        }
    }


    ngAfterViewChecked() {
        this.addMobileMenuClickListener();
    }
    
    //Below two functions are the changes done to make menu clicks to work in mobile view. For details, check JIRA ID ADPPD-1217. 
    addMobileMenuClickListener() {
        let navElement = document.getElementById(this.props.id+"-mobile");
        if (navElement)
            navElement.addEventListener('click', this.handleMenuClick.bind(this));
	}
    
	handleMenuClick(event:any) {
		let listOfLiIds = this.props.menucomponentlist;
	    if(event.target){
			let closestLists  = $(<HTMLElement>event.target).closest("li");
			if (closestLists.length > 0 && listOfLiIds && listOfLiIds.includes(closestLists[0].id)) {
				this.apzMenuClick.emit(event);
			}
		}
	}
    
}