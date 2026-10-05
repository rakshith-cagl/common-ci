import {TestBed} from '@angular/core/testing';
import { EventDelagation } from './delegateEvent';
import { By } from "@angular/platform-browser";
import { Component} from '@angular/core';

@Component({
    template: `<div class="outer-div" delegateEvent (blur)="handleBlurFocusEvent('BLUR')"><div class="inner-div" delegateEvent (focus)="handleBlurFocusEvent('FOCUS')"></div></div>`
  })
  class TestEventDelegationComponent {

    handleBlurFocusEvent(eventType: any) {
        console.log(eventType)
    }
  }


describe('Directive: eventDelagation', () => {
   beforeEach(async () => {
     await TestBed.configureTestingModule({
       imports: [
       ],
       declarations: [
        EventDelagation, TestEventDelegationComponent
       ],
       providers: []
     }).compileComponents();
   });
  
   it('should create the TestEventDelegationComponent', () => {
     const fixture = TestBed.createComponent(TestEventDelegationComponent);
     const component = fixture.componentInstance;
     spyOn(component, 'handleBlurFocusEvent');
     let div = fixture.debugElement.query(By.css('.outer-div'));
     div.triggerEventHandler('blur', {type: "blur"});
     div.triggerEventHandler('focus', {type: "focus"});
     fixture.detectChanges();
     expect(component.handleBlurFocusEvent).toHaveBeenCalled();
   });

});