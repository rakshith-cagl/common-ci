import { TestBed } from '@angular/core/testing';
import { SortCodeAccountComponent  } from './sortcode-account.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('SortCodeAccountComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'isUiElm']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      SortCodeAccountComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the SortCodeAccountComponent', () => {
   const fixture = TestBed.createComponent(SortCodeAccountComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeAccountComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', attrs: {defaultValue: '123456789'},tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "Y"}}}});
  component.setContent();
  expect(component.value).toEqual(component.value.substr(0,8))
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  component.row = 1;
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});

it('should invoke life cycle methods', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__SortCodeAccount__el_sortcodeaccount_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeAccountComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'angRef__SortCodeAccount__el_sortcodeaccount_1', attrs: {rowno: 1, defaultvalue:'1234567812345678',contentalignment: 'LEFT', options: 'N',cssclasses: 'pri', tooltip: 'Y', orientation: 'Y', appearance: 'Y'},tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_sortcodeaccount_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should cover alternate flows of life cycle methods', () => {
  setApz({isNull: function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'angRef__SortCodeAccount__el_sortcodeaccount_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeAccountComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'angRef__SortCodeAccount__el_sortcodeaccount_1',attrs: {defaultvalue:'1234567812345678',contentalignment: 'RIGHT'}, tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_sortcodeaccount_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should cover alternate flow when content alignment is CENTRE', () => {
  setApz({isNull: function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'angRef__SortCodeAccount__el_sortcodeaccount_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeAccountComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'angRef__SortCodeAccount__el_sortcodeaccount_1',attrs: {defaultvalue:'1234567812345678', width: 'CUSTOM', contentalignment: 'CENTRE'}, tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_sortcodeaccount_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should assign value to value variable on invoking savevalue()', () => {
  setApz({getObjIdWORowNumber: function(params:any) {return 1}, getObjRowNumber: function(param:any) { return 1}, isNull: function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'angRef__SortCodeAccount__el_sortcodeaccount_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeAccountComponent);
  const component = fixture.componentInstance;
  component.values = {};
  component.props = {id:'angRef__SortCodeAccount__el_sortcodeaccount_1',attrs: {defaultvalue:'1234567812345678', width: 'CUSTOM', contentalignment: 'CENTRE'}, tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  mockDataService.isUiElm.withArgs(component.props.id).and.returnValue(false);
  component.saveValue(component.props, {});
  expect(component.values).toBeDefined();
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeAccountComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', attrs: {defaultValue: '123456789'},tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  mockDataService.isUiElm.withArgs(component.props.id).and.returnValue(false);
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "Y"}}}});
  mockDataService.isUiElm.withArgs(component.props.id).and.returnValue(true);
  component.setContent();
  expect(component.value).toEqual(component.value.substr(0,8))
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  component.row = 1;
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});

});