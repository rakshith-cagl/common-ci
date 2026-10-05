import { TestBed } from '@angular/core/testing';
import { SliderComponent  } from './slider.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';

describe('SliderComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      SliderComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the SliderComponent', () => {
   const fixture = TestBed.createComponent(SliderComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('isUiElm() should return true when progress step is an UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__selectAmt': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(SliderComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__selectAmt', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  expect(component.isUiElm()).toBeTrue();
});

it('isUiElm() should return false when progress step is bind to an interface', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__selectAmt': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SliderComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__selectAmt', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  expect(component.isUiElm()).toBeFalse();
});

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(SliderComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__selectAmt', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(SliderComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__selectAmt', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should emit labelIconClick event', () => {
  const fixture = TestBed.createComponent(SliderComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.iconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SliderComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  component.row = 1;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});

it('should invoke life cycle methods', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'elmnts__Elements__selectAmt': {'ui': "N",container: "SonarA__NewScreen__ct_tbl_1"}}}});
  const fixture = TestBed.createComponent(SliderComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.rowIndex = 0;
  component.elmData = component.apz['scrMetaData']['elmsMap'].elmnts__Elements__selectAmt;
  component.containerId = "SonarA__NewScreen__ct_tbl_1";
  component.props = {id:'elmnts__Elements__selectAmt', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  mockDataService.getContent.withArgs(component.props.id, component.row, 0, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should invoke life cycle methods for control type LIST', () => {
  setApz({isNull: function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'elmnts__Elements__selectAmt': {'ui': "N", container: "SonarA__NewScreen__ct_tbl_1"}}}});
  const fixture = TestBed.createComponent(SliderComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.row = 0;
  component.elmData = component.apz['scrMetaData']['elmsMap'].elmnts__Elements__selectAmt;
  component.containerId = "SonarA__NewScreen__ct_tbl_1";
  component.props = {id:'elmnts__Elements__selectAmt', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'LIST', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  mockDataService.getContent.withArgs(component.props.id, 0, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should set props value variable from target value on invoking setValue()', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__selectAmt': {'ui': 'Y'}}}});
  const fixture = TestBed.createComponent(SliderComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__selectAmt', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'elmnts__Elements__selectAmt_span_-1', 'class':'ecn etw-50'}, cntrType:'LIST', value:'', content:{'direct':{'className':'ett-rnge  etw-50 sec', 'style':{},'max' : '100000', 'min':'10',  'enabled':'enabled','original-title':'', 'name':'selectAmt'}}, SliderHintRequired:'N' , widgettype:'SLIDER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Choose your amount(in rupees)', 'direct':{'id':'elmnts__Elements__selectAmt_grp_lbl','htmlFor':'elmnts__Elements__selectAmt','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__selectAmt_li','className':'eic etw-60'}}};
  component.setValue(component.props, {target: {value: 1000}})
  expect(component.props.value).toEqual(1000);
  component.elm = 'selectAmt';
  component.values = {'selectAmt': ''};
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__selectAmt': {'ui': 'N'}}}});
  component.setValue(component.props, {target: {value: 1000}})
  expect(component.values[component.elm]).toEqual(1000);
  component.setHandler();
});


});