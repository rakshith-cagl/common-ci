import { TestBed } from '@angular/core/testing';
import { PanelComponent  } from './panelcomponent';
describe('PanelComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        PanelComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the PanelComponent', () => {
   const fixture = TestBed.createComponent(PanelComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('should emit preTabAction event', () => {
  const fixture = TestBed.createComponent(PanelComponent);
  const component = fixture.componentInstance;
  spyOn(component.preTabAction, 'emit');
  component.beforeTabAction({});
  expect(component.preTabAction.emit).toHaveBeenCalled();
});

it('should emit postTabAction event', () => {
  const fixture = TestBed.createComponent(PanelComponent);
  const component = fixture.componentInstance;
  spyOn(component.postTabAction, 'emit');
  component.afterTabAction({});
  expect(component.postTabAction.emit).toHaveBeenCalled();
});

it('should return true when panel type is SIMPLE, ACCORDIAN, CAROUSEL and COLLAPSIBLE else false', () => {
  const fixture = TestBed.createComponent(PanelComponent);
  const component = fixture.componentInstance;
  component.props = {panelType: 'SIMPLE'};
  expect(component.ifCheckForPanelType()).toEqual(true);
  component.props = {panelType: 'ACCORDION'};
  expect(component.ifCheckForPanelType()).toEqual(true);
  component.props = {panelType: 'CAROUSEL'};
  expect(component.ifCheckForPanelType()).toEqual(true);
  component.props = {panelType: 'COLLAPSIBLE'};
  expect(component.ifCheckForPanelType()).toEqual(true);
  component.props = {panelType: 'NONE'};
  expect(component.ifCheckForPanelType()).toEqual(false);
});


});