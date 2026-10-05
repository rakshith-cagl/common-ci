import { TestBed } from '@angular/core/testing';
import { DropDownButtonComponent  } from './dropdownbutton.component';
import { WindowRef, setApz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from '../data_angular';
describe('DropDownButtonComponent', () => {
 let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        DropDownButtonComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the DropDownButtonComponent', () => {
   const fixture = TestBed.createComponent(DropDownButtonComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_dpd_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(DropDownButtonComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'elmnts__Elements__el_dpd_1', attrs: {'defaultvalue': 'DESC 1', contentalignment: 'LEFT', state: 'DISABLED', tooltip: 'Helper tip', options: 'N', cssclasses: 'select2-dd', appearance: 'apr-cls'}, type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'FORM', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'Choose Expertise', 'direct':{'id':'elmnts__Elements__el_dpd_1_grp_lbl','htmlFor':'elmnts__Elements__el_dpd_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_dpd_1_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.alignment).toEqual(' lft');
});

it('should cover alternate flows of life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_dpd_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(DropDownButtonComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__el_dpd_1', attrs: {'defaultvalue': 'DESC 1', contentalignment: 'CENTER', state: 'DISABLED', tooltip: 'Helper tip', options: 'N', cssclasses: 'select2-dd', appearance: 'apr-cls'}, type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'NAVBAR', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'Choose Expertise', 'direct':{'id':'elmnts__Elements__el_dpd_1_grp_lbl','htmlFor':'elmnts__Elements__el_dpd_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_dpd_1_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.alignment).toEqual(' cen');
  expect(component.dpDownSpanWrapperRef.nativeElement).toBeDefined();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should set alignment variable to rht when content alignment is sent as RIGHT', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_dpd_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(DropDownButtonComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__el_dpd_1', attrs: {'defaultvalue': 'DESC 1', contentalignment: 'RIGHT', state: 'DISABLED', tooltip: 'Helper tip', options: 'N', cssclasses: 'select2-dd', appearance: 'apr-cls'}, type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'NAVBAR', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'Choose Expertise', 'direct':{'id':'elmnts__Elements__el_dpd_1_grp_lbl','htmlFor':'elmnts__Elements__el_dpd_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_dpd_1_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.alignment).toEqual(' rht');
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(DropDownButtonComponent);
  const component = fixture.componentInstance;
  component.state = {};
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  component.row = 1;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(DropDownButtonComponent);
  const component = fixture.componentInstance;
  component.state = {};
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  component.row = 1;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});

// it('should handle click event', () => {
//   const fixture = TestBed.createComponent(DropDownButtonComponent);
//   const component = fixture.componentInstance;
//   const event = new MouseEvent('click'); 
//   spyOn(event, 'preventDefault');
//   spyOn(event, 'stopPropagation');
//   component.handleClick("<div><button id='test'></button></div>", event);
// });
});