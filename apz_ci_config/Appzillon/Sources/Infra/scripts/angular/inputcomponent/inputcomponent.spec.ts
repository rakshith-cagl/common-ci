import { TestBed } from '@angular/core/testing';
import { InputComponent  } from './inputcomponent';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import { NgClass } from '@angular/common';
describe('InputComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        InputComponent
     ],
     providers: [{
      provide: DataService,
      useValue: spy
   }, WindowRef]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the InputComponent', () => {
   const fixture = TestBed.createComponent(InputComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('isUiElm() should return true when input is an UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_inp_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_inp_1', tdClasses:' pri', apzcontrol:'' , type:'text', formula:'' , value:'', cntrType:'FORM', cntrId:'elmnts__Elements__ct_frm_10', content:{'direct':{'className':'etw-100 ett-inpt pri lft', 'maxLength' : '524288', 'placeholder':'',  'enabled':'enabled','original-title':'',  'value':''}}, spanWrapper:{'className':'ecn etw-95','style':{}}, osSpecific:'N', hasLov:'N', hasSymbol:'N', hasIcon:'N', isdateOrDateTime:'N', elmWrapper :{'direct':{'className':''}}, buttonState :{}, elementIcon :'icon-grid', lovIcon :'icon-grid', widgettype:'INPUTBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Input Primary', 'direct':{'id':'elmnts__Elements__el_inp_1_grp_lbl','htmlFor':'elmnts__Elements__el_inp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_inp_1_li','className':'eic etw-60'}}}
  expect(component.isUiElm()).toBeTrue();
});

it('isUiElm() should return false when input is bind to an interface', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_inp_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_inp_1', tdClasses:' pri', apzcontrol:'' , type:'text', formula:'' , value:'', cntrType:'FORM', cntrId:'elmnts__Elements__ct_frm_10', content:{'direct':{'className':'etw-100 ett-inpt pri lft', 'maxLength' : '524288', 'placeholder':'',  'enabled':'enabled','original-title':'',  'value':''}}, spanWrapper:{'className':'ecn etw-95','style':{}}, osSpecific:'N', hasLov:'N', hasSymbol:'N', hasIcon:'N', isdateOrDateTime:'N', elmWrapper :{'direct':{'className':''}}, buttonState :{}, elementIcon :'icon-grid', lovIcon :'icon-grid', widgettype:'INPUTBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Input Primary', 'direct':{'id':'elmnts__Elements__el_inp_1_grp_lbl','htmlFor':'elmnts__Elements__el_inp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_inp_1_li','className':'eic etw-60'}}}
  expect(component.isUiElm()).toBeFalse();
});

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_inp_1', tdClasses:' pri', apzcontrol:'' , type:'text', formula:'' , value:'', cntrType:'FORM', cntrId:'elmnts__Elements__ct_frm_10', content:{'direct':{'className':'etw-100 ett-inpt pri lft', 'maxLength' : '524288', 'placeholder':'',  'enabled':'enabled','original-title':'',  'value':''}}, spanWrapper:{'className':'ecn etw-95','style':{}}, osSpecific:'N', hasLov:'N', hasSymbol:'N', hasIcon:'N', isdateOrDateTime:'N', elmWrapper :{'direct':{'className':''}}, buttonState :{}, elementIcon :'icon-grid', lovIcon :'icon-grid', widgettype:'INPUTBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Input Primary', 'direct':{'id':'elmnts__Elements__el_inp_1_grp_lbl','htmlFor':'elmnts__Elements__el_inp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_inp_1_li','className':'eic etw-60'}}}
  component.rowIndex = 0;
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
  expect(component.index).toEqual(component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_inp_1', tdClasses:' pri', apzcontrol:'' , type:'text', formula:'' , value:'', cntrType:'FORM', cntrId:'elmnts__Elements__ct_frm_10', content:{'direct':{'className':'etw-100 ett-inpt pri lft', 'maxLength' : '524288', 'placeholder':'',  'enabled':'enabled','original-title':'',  'value':''}}, spanWrapper:{'className':'ecn etw-95','style':{}}, osSpecific:'N', hasLov:'N', hasSymbol:'N', hasIcon:'N', isdateOrDateTime:'N', elmWrapper :{'direct':{'className':''}}, buttonState :{}, elementIcon :'icon-grid', lovIcon :'icon-grid', widgettype:'INPUTBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Input Primary', 'direct':{'id':'elmnts__Elements__el_inp_1_grp_lbl','htmlFor':'elmnts__Elements__el_inp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_inp_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should emit iconClick event', () => {
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  spyOn(component.iconClick, 'emit');
  component.iconClickEvent({});
  expect(component.iconClick.emit).toHaveBeenCalled();
});

it('should emit labelIconClick event', () => {
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.labelIconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
});

it('should emit preCallLov event', () => {
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  spyOn(component.preCallLov, 'emit');
  component.beforeCallLov('', '');
  expect(component.preCallLov.emit).toHaveBeenCalled();
});

it('should emit postCallLov event', () => {
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  spyOn(component.postCallLov, 'emit');
  component.afterCallLov('','');
  expect(component.postCallLov.emit).toHaveBeenCalled();
});

it('isMultiRec() should return true when element has multi record else false', () => {
  setApz({'scrMetaData': {containersMap: {'elmnts__Elements__ct_frm_10': {multiRec: 'Y'}},'elmsMap': {'elmnts__Elements__el_inp_1': {'ui': "N", container: 'elmnts__Elements__ct_frm_10'}}}});
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_inp_1', tdClasses:' pri', apzcontrol:'' , type:'text', formula:'' , value:'', cntrType:'FORM', cntrId:'elmnts__Elements__ct_frm_10', content:{'direct':{'className':'etw-100 ett-inpt pri lft', 'maxLength' : '524288', 'placeholder':'',  'enabled':'enabled','original-title':'',  'value':''}}, spanWrapper:{'className':'ecn etw-95','style':{}}, osSpecific:'N', hasLov:'N', hasSymbol:'N', hasIcon:'N', isdateOrDateTime:'N', elmWrapper :{'direct':{'className':''}}, buttonState :{}, elementIcon :'icon-grid', lovIcon :'icon-grid', widgettype:'INPUTBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Input Primary', 'direct':{'id':'elmnts__Elements__el_inp_1_grp_lbl','htmlFor':'elmnts__Elements__el_inp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_inp_1_li','className':'eic etw-60'}}}
  expect(component.isMultiRec()).toBeTrue();
  setApz({'scrMetaData': {containersMap: {'elmnts__Elements__ct_frm_10': {multiRec: 'N'}},'elmsMap': {'elmnts__Elements__el_inp_1': {'ui': "N", container: 'elmnts__Elements__ct_frm_10'}}}});
  expect(component.isMultiRec()).toBeFalse();
});

it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "Y"}},'elmsMap': {'elmnts__Elements__el_inp_1': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  component.row = 1;
  component.props = {id:'elmnts__Elements__el_inp_1', tdClasses:' pri', apzcontrol:'' , type:'text', formula:'' , value:'', cntrType:'FORM', cntrId:'elmnts__Elements__ct_frm_10', content:{'direct':{'className':'etw-100 ett-inpt pri lft', 'maxLength' : '524288', 'placeholder':'',  'enabled':'enabled','original-title':'',  'value':''}}, spanWrapper:{'className':'ecn etw-95','style':{}}, osSpecific:'N', hasLov:'Y', hasSymbol:'N', hasIcon:'N', isdateOrDateTime:'N', elmWrapper :{'direct':{'className':''}}, buttonState :{}, elementIcon :'icon-grid', lovIcon :'icon-grid', widgettype:'INPUTBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Input Primary', 'direct':{'id':'elmnts__Elements__el_inp_1_grp_lbl','htmlFor':'elmnts__Elements__el_inp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'Y',contentIcon: {direct: {}}, labelIconProps: {iconPos: 'LEFT', direct: {className: 'pri'}},contentIconEvent: {'k1': 'elmnts__Elements__el_inp_1'},contentWrapper:{'direct':{'id':'elmnts__Elements__el_inp_1_li','className':'eic etw-60'}}}
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should cover the alternate flows of life cycle methods', () => {
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "Y"}},'elmsMap': {'elmnts__Elements__el_inp_1': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__el_inp_1', svgContent: {direct: {className: 'pri'}},tdClasses:' pri', apzcontrol:'' , type:'text', formula:'' , value:'', cntrType:'LIST', cntrId:'elmnts__Elements__ct_frm_10', content:{'direct':{'className':'etw-100 ett-inpt pri lft', 'maxLength' : '', 'placeholder':'',  'enabled':'enabled','original-title':'',  'value':''}}, spanWrapper:{'className':'ecn etw-95','style':{}}, osSpecific:'N', hasLov:'Y', contentIcon: {position: "LEFT"}, hasSymbol:'N', hasIcon:'N', isdateOrDateTime:'N', elmWrapper :{'direct':{'className':''}}, buttonState :{}, elementIcon :'icon-grid', lovIcon :'icon-grid', widgettype:'INPUTBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Input Primary', 'direct':{'id':'elmnts__Elements__el_inp_1_grp_lbl','htmlFor':'elmnts__Elements__el_inp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_inp_1_li','className':'eic etw-60'}}}
  fixture.detectChanges();
  component.setHandler(); //Need to find a better way to handle this
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(InputComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.values).toBeDefined();
  component.row = 1;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.values).toBeDefined();
});
});