import { TestBed } from '@angular/core/testing';
import { RadioButtonComponent  } from './radio-button.component';
import {WindowRef,setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('RadioButtonComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        RadioButtonComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the RadioButtonComponent', () => {
   const fixture = TestBed.createComponent(RadioButtonComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('isUiElm() should return true when radio button is an UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__genderRadio': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(RadioButtonComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__genderRadio', radioType:'APPZILLON', value:'Male', tdClasses:' pri', cntrType:'FORM', spanWrapper:{'apztype':'radiogroup', 'className':'etb-rdio ett-rdio hor pri'}, radioHintRequired:'N' , orientation:'H' , variation:'' , content:{'direct':{ 'enabled':'enabled'}}, widgettype:'RADIO', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Select your Gender', 'direct':{'id':'elmnts__Elements__genderRadio_grp_lbl','htmlFor':'elmnts__Elements__genderRadio','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__genderRadio_li','className':'eic etw-60'}}};
  expect(component.isUiElm()).toBeTrue();
});

it('isUiElm() should return false when radio button is bind to an interface', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__genderRadio': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(RadioButtonComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__genderRadio', radioType:'APPZILLON', value:'Male', tdClasses:' pri', cntrType:'FORM', spanWrapper:{'apztype':'radiogroup', 'className':'etb-rdio ett-rdio hor pri'}, radioHintRequired:'N' , orientation:'H' , variation:'' , content:{'direct':{ 'enabled':'enabled'}}, widgettype:'RADIO', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Select your Gender', 'direct':{'id':'elmnts__Elements__genderRadio_grp_lbl','htmlFor':'elmnts__Elements__genderRadio','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__genderRadio_li','className':'eic etw-60'}}};
  expect(component.isUiElm()).toBeFalse();
});

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(RadioButtonComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__genderRadio', radioType:'APPZILLON', value:'Male', tdClasses:' pri', cntrType:'FORM', spanWrapper:{'apztype':'radiogroup', 'className':'etb-rdio ett-rdio hor pri'}, radioHintRequired:'N' , orientation:'H' , variation:'' , content:{'direct':{ 'enabled':'enabled'}}, widgettype:'RADIO', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Select your Gender', 'direct':{'id':'elmnts__Elements__genderRadio_grp_lbl','htmlFor':'elmnts__Elements__genderRadio','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__genderRadio_li','className':'eic etw-60'}}};
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(RadioButtonComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__genderRadio', radioType:'APPZILLON', value:'Male', tdClasses:' pri', cntrType:'FORM', spanWrapper:{'apztype':'radiogroup', 'className':'etb-rdio ett-rdio hor pri'}, radioHintRequired:'N' , orientation:'H' , variation:'' , content:{'direct':{ 'enabled':'enabled'}}, widgettype:'RADIO', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Select your Gender', 'direct':{'id':'elmnts__Elements__genderRadio_grp_lbl','htmlFor':'elmnts__Elements__genderRadio','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__genderRadio_li','className':'eic etw-60'}}};
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should emit labelIconClick event', () => {
  const fixture = TestBed.createComponent(RadioButtonComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.iconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(RadioButtonComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "Y"}}}});
  component.setContent();
  expect(component.value).toEqual(component.props.value);
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  component.row = 1;
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "Y"}}}});
  component.setContent();
  expect(component.value).toEqual(component.props.value);
});


it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__genderRadio': {'staticOptions': [{val: 1, desc: 'One'}]}}}});
  const fixture = TestBed.createComponent(RadioButtonComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'elmnts__Elements__genderRadio', radioType:'APPZILLON', value:'Male', tdClasses:' pri', cntrType:'FORM', spanWrapper:{'apztype':'radiogroup', 'className':'etb-rdio ett-rdio hor pri'}, radioHintRequired:'N' , orientation:'H' , variation:'' , content:{'direct':{ 'enabled':'enabled'}}, widgettype:'RADIO', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Select your Gender', 'direct':{'id':'elmnts__Elements__genderRadio_grp_lbl','htmlFor':'elmnts__Elements__genderRadio','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__genderRadio_li','className':'eic etw-60'}}};
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_pgb_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle methods for control type NAVBAR', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__genderRadio': {'staticOptions': [{val: 1, desc: 'One'}]}}}});
  const fixture = TestBed.createComponent(RadioButtonComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__genderRadio', radioType:'APPZILLON', value:'Male', tdClasses:' pri', cntrType:'LIST', spanWrapper:{'apztype':'radiogroup', 'className':'etb-rdio ett-rdio hor pri'}, radioHintRequired:'N' , orientation:'H' , variation:'' , content:{'direct':{ 'enabled':'enabled'}}, widgettype:'RADIO', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Select your Gender', 'direct':{'id':'elmnts__Elements__genderRadio_grp_lbl','htmlFor':'elmnts__Elements__genderRadio','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__genderRadio_li','className':'eic etw-60'}}};
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_pgb_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
  expect(component.rdRowIndex).toEqual(component.rowIndex);
});

it('should return true if passed value matches with props value varibale on invoking getCheckedVal()', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__genderRadio': {'ui': 'Y','staticOptions': [{val: 'One', desc: 'One'}]}}}});
  const fixture = TestBed.createComponent(RadioButtonComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__genderRadio', value: 'One',radioType:'APPZILLON', tdClasses:' pri', cntrType:'FORM', spanWrapper:{'apztype':'radiogroup', 'className':'etb-rdio ett-rdio hor pri'}, radioHintRequired:'N' , orientation:'H' , variation:'' , content:{'direct':{ 'enabled':'enabled'}}, widgettype:'RADIO', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Select your Gender', 'direct':{'id':'elmnts__Elements__genderRadio_grp_lbl','htmlFor':'elmnts__Elements__genderRadio','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__genderRadio_li','className':'eic etw-60'}}};
  expect(component.getCheckedVal('One')).toBeTruthy();
});

it('should set props value variable to the passed value on invoking radioClicked()', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__genderRadio': {'ui': 'Y','staticOptions': [{val: 'One', desc: 'One'}, {val: 'Two', desc: 'Two'}]}}}});
  const fixture = TestBed.createComponent(RadioButtonComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__genderRadio',radioType:'APPZILLON', tdClasses:' pri', cntrType:'FORM', spanWrapper:{'apztype':'radiogroup', 'className':'etb-rdio ett-rdio hor pri'}, radioHintRequired:'N' , orientation:'H' , variation:'' , content:{'direct':{ 'enabled':'enabled'}}, widgettype:'RADIO', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Select your Gender', 'direct':{'id':'elmnts__Elements__genderRadio_grp_lbl','htmlFor':'elmnts__Elements__genderRadio','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__genderRadio_li','className':'eic etw-60'}}};
  component.radioClicked({target: {value: 'Two'}})
  expect(component.props.value).toEqual('Two');
  component.elm = 'genderRadio';
  component.values = {'genderRadio': ''};
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__genderRadio': {'ui': 'N','staticOptions': [{val: 'One', desc: 'One'}, {val: 'Two', desc: 'Two'}]}}}});
  component.radioClicked({target: {value: 'Two'}})
  expect(component.values[component.elm]).toEqual('Two');
});

});