import { TestBed } from '@angular/core/testing';
import { PanelSecComponent  } from './panelseccomponent';
describe('PanelSecComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        PanelSecComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the PanelSecComponent', () => {
   const fixture = TestBed.createComponent(PanelSecComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('should emit preCollapsibleAction event', () => {
  const fixture = TestBed.createComponent(PanelSecComponent);
  const component = fixture.componentInstance;
  spyOn(component.preCollapsibleAction, 'emit');
  component.beforeCollapsibleAction({});
  expect(component.preCollapsibleAction.emit).toHaveBeenCalled();
});

it('should emit postCollapsibleAction event', () => {
  const fixture = TestBed.createComponent(PanelSecComponent);
  const component = fixture.componentInstance;
  spyOn(component.postCollapsibleAction, 'emit');
  component.afterCollapsibleAction({});
  expect(component.postCollapsibleAction.emit).toHaveBeenCalled();
});

it('should return true when panel type is SIMPLE, ACCORDIAN, CAROUSEL and COLLAPSIBLE else false', () => {
  const fixture = TestBed.createComponent(PanelSecComponent);
  const component = fixture.componentInstance;
  component.props = {panelType: 'SIMPLE'};
  expect(component.ifCheckForPanelType()).toEqual(true);
  component.props = {panelType: 'CAROUSEL'};
  expect(component.ifCheckForPanelType()).toEqual(true);
  component.props = {panelType: 'COLLAPSIBLE'};
  expect(component.ifCheckForPanelType()).toEqual(true);
  component.props = {panelType: 'NONE'};
  expect(component.ifCheckForPanelType()).toEqual(false);
});

it('should return true on invoking ifCheckForAccordionType() when panel type is ACCORDIAN else false', () => {
  const fixture = TestBed.createComponent(PanelSecComponent);
  const component = fixture.componentInstance;
  component.props = {panelType: 'ACCORDION'};
  expect(component.ifCheckForAccordionType()).toEqual(true);
  component.props = {panelType: 'NONE'};
  expect(component.ifCheckForAccordionType()).toEqual(false);
});

it('should return true on invoking checkTitleValue() if titleValue is defined else false', () => {
  const fixture = TestBed.createComponent(PanelSecComponent);
  const component = fixture.componentInstance;
  component.props = {titleValue: 'ACCORDION'};
  expect(component.checkTitleValue()).toEqual(true);
  component.props = {};
  expect(component.checkTitleValue()).toEqual(false);
});

it('should return true on invoking checkTitleIcon() if titleIcon is defined else false', () => {
  const fixture = TestBed.createComponent(PanelSecComponent);
  const component = fixture.componentInstance;
  component.props = {titleIcon: 'Y'};
  expect(component.checkTitleIcon()).toEqual(true);
  component.props = {};
  expect(component.checkTitleIcon()).toEqual(false);
});

it('should invoke life cycle method', () => {
  const fixture = TestBed.createComponent(PanelSecComponent);
  const component = fixture.componentInstance;
  component.props = {panelType: "ACCORDION", ulWrapper: {className: "etw-100 ett-inpt pri lft"}, panelSection: {className: "etw-100 ett-inpt pri lft"}, accContentWrapper: {className: "etw-100 ett-inpt pri lft"}, direct: {className: "etw-100 ett-inpt pri lft"}, collapsibleContentWrapper: {className: "etw-100 ett-inpt pri lft"}};
  fixture.detectChanges();
  expect(component.panelSectionDirect.nativeElement).toBeDefined();
});

it('should cover alternate life cycle method flow for panel type TAB', () => {
  const fixture = TestBed.createComponent(PanelSecComponent);
  const component = fixture.componentInstance;
  component.props = {panelType: "TAB", ulWrapper: {className: "etw-100 ett-inpt pri lft"}, panelSection: {className: "etw-100 ett-inpt pri lft"}, accContentWrapper: {className: "etw-100 ett-inpt pri lft"}, direct: {className: "etw-100 ett-inpt pri lft"}};
  fixture.detectChanges();
  expect(component.propsDirect.nativeElement).toBeDefined();
});

it('should cover alternate life cycle method flow for panel type COLLAPSIBLE ', () => {
  const fixture = TestBed.createComponent(PanelSecComponent);
  const component = fixture.componentInstance;
  component.props = {panelType: "COLLAPSIBLE", ulWrapper: {className: "etw-100 ett-inpt pri lft"}, panelSection: {className: "etw-100 ett-inpt pri lft"}, accContentWrapper: {className: "etw-100 ett-inpt pri lft"}, direct: {className: "etw-100 ett-inpt pri lft"}, collapsibleContentWrapper: {className: "etw-100 ett-inpt pri lft"}};
  fixture.detectChanges();
  expect(component.collapsibleContentWrapper.nativeElement).toBeDefined();
});
});