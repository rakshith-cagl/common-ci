import {Directive,HostListener,EventEmitter,Input} from '@angular/core';
@Directive({
    selector: "[delegateEvent]"
  })

export class EventDelagation {
    @Input()
	public blurEvent: EventEmitter<any> = new EventEmitter<any>();
	@Input()
    public focusEvent: EventEmitter<any> = new EventEmitter<any>();  

     

    @HostListener('blur',["$event"])
    @HostListener('focus',["$event"])
    
    onEvent(event:Event){
        if(event.type=="blur"){
            this.blurEvent.emit(event);
        }
        if(event.type=="focus"){
            this.focusEvent.emit(event);
        }
    }
}