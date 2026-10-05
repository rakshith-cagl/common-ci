import {TestBed} from '@angular/core/testing';
import { ClickStopPropagation } from './stopEventDirective';
import { By } from "@angular/platform-browser";
import { Component} from '@angular/core';

@Component({
    template: `<div class="outer-div" clickstoppropagation (click)="handleDivClick('OUTER')"><div class="inner-div" (click)="handleDivClick('INNER')"></div></div>`
  })
  class TestClickStopPropagationComponent {

    handleDivClick(divType: any) {
        console.log(divType)
    }
  }


describe('Directive: ClickStopPropagation', () => {
   beforeEach(async () => {
     await TestBed.configureTestingModule({
       imports: [
       ],
       declarations: [
          ClickStopPropagation, TestClickStopPropagationComponent
       ],
       providers: []
     }).compileComponents();
   });
  
   it('should create the TestClickStopPropagationComponent', () => {
     const fixture = TestBed.createComponent(TestClickStopPropagationComponent);
     const component = fixture.componentInstance;
     spyOn(component, 'handleDivClick');
     let btn = fixture.debugElement.query(By.css('.outer-div'));
     btn.triggerEventHandler('click', {target: {}, stopPropagation: function() {}});
     fixture.detectChanges();
     expect(component.handleDivClick).toHaveBeenCalled();
   });

});