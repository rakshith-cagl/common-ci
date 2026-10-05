import { TestBed } from '@angular/core/testing';
import { SortCodeComponent  } from './sort-code.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('SortCodeComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'isUiElm']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      SortCodeComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the SortCodeComponent', () => {
   const fixture = TestBed.createComponent(SortCodeComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(SortCodeComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'angRef__SortCode__el_sortcode_1', cntrType:'FORM', attrs:{'typeclass':'PRESENTATIONELEMENT','name':'angRef__SortCode__el_sortcode_1','type':'PRESENTATIONELEMENT','id':'e69b18004e1995537ca83a83','pid':'dc8b9c604e778c29ba72aae8','widgetcategory':'ELEMENT','widgettype':'SortCode','appearance':'pri','contentalignment':'LEFT','cntrdescreq':'Y','custom':'Y','datatype':'STRING','events':[{'name':'ONBLUR'}],'headeralignment':'CENTER','labelalignment':'LEFT','labelrequired':'Y','labelwidth':'40','mandatory':'N','options':'Y','state':'ENABLED','title':'ID - el_sortcode_1','translatedefaultvalue':'N','account':'N','parenttype':'FORM','OSTYPE':'SIMULATOR','APPID':'angRef','APPNAME':'angularReference','rowno':-1,'scr':'SortCode'}, widgettype:'SortCode', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_sortcode_1', 'direct':{'id':'angRef__SortCode__el_sortcode_1_grp_lbl','htmlFor':'angRef__SortCode__el_sortcode_1','className':'flb lft'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__SortCode__el_sortcode_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(SortCodeComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__SortCode__el_sortcode_1', cntrType:'FORM', attrs:{'typeclass':'PRESENTATIONELEMENT','name':'angRef__SortCode__el_sortcode_1','type':'PRESENTATIONELEMENT','id':'e69b18004e1995537ca83a83','pid':'dc8b9c604e778c29ba72aae8','widgetcategory':'ELEMENT','widgettype':'SortCode','appearance':'pri','contentalignment':'LEFT','cntrdescreq':'Y','custom':'Y','datatype':'STRING','events':[{'name':'ONBLUR'}],'headeralignment':'CENTER','labelalignment':'LEFT','labelrequired':'Y','labelwidth':'40','mandatory':'N','options':'Y','state':'ENABLED','title':'ID - el_sortcode_1','translatedefaultvalue':'N','account':'N','parenttype':'FORM','OSTYPE':'SIMULATOR','APPID':'angRef','APPNAME':'angularReference','rowno':-1,'scr':'SortCode'}, widgettype:'SortCode', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_sortcode_1', 'direct':{'id':'angRef__SortCode__el_sortcode_1_grp_lbl','htmlFor':'angRef__SortCode__el_sortcode_1','className':'flb lft'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__SortCode__el_sortcode_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle methods', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__SortCode__el_sortcode_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'angRef__SortCode__el_sortcode_1', cntrType:'FORM', attrs:{'typeclass':'PRESENTATIONELEMENT','name':'angRef__SortCode__el_sortcode_1','type':'PRESENTATIONELEMENT','id':'e69b18004e1995537ca83a83','pid':'dc8b9c604e778c29ba72aae8','widgetcategory':'ELEMENT','widgettype':'SortCode','appearance':'pri','contentalignment':'LEFT','cntrdescreq':'Y','custom':'Y','datatype':'STRING','events':[{'name':'ONBLUR'}],'headeralignment':'CENTER','labelalignment':'LEFT','labelrequired':'Y','labelwidth':'40','mandatory':'N','options':'Y',cssclasses: 'pri', tooltip: 'Y','state':'ENABLED','title':'ID - el_sortcode_1','translatedefaultvalue':'N','account':'N','parenttype':'FORM','OSTYPE':'SIMULATOR','APPID':'angRef','APPNAME':'angularReference','rowno':-1,'scr':'SortCode'}, widgettype:'SortCode', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_sortcode_1', 'direct':{'id':'angRef__SortCode__el_sortcode_1_grp_lbl','htmlFor':'angRef__SortCode__el_sortcode_1','className':'flb lft'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__SortCode__el_sortcode_1_li','className':'eic etw-60'}}}
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_sortcode_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle methods for content alignment centre', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__SortCode__el_sortcode_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'angRef__SortCode__el_sortcode_1', cntrType:'FORM', attrs:{'typeclass':'PRESENTATIONELEMENT','name':'angRef__SortCode__el_sortcode_1','type':'PRESENTATIONELEMENT','id':'e69b18004e1995537ca83a83','pid':'dc8b9c604e778c29ba72aae8','widgetcategory':'ELEMENT','widgettype':'SortCode','appearance':'pri','contentalignment':'CENTER','cntrdescreq':'Y','custom':'Y','datatype':'STRING','events':[{'name':'ONBLUR'}],'headeralignment':'CENTER','labelalignment':'LEFT','labelrequired':'Y','labelwidth':'40','mandatory':'N','options':'Y',cssclasses: 'pri', tooltip: 'Y','state':'ENABLED','title':'ID - el_sortcode_1','translatedefaultvalue':'N','account':'N','parenttype':'FORM','OSTYPE':'SIMULATOR','APPID':'angRef','APPNAME':'angularReference','rowno':-1,'scr':'SortCode'}, widgettype:'SortCode', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_sortcode_1', 'direct':{'id':'angRef__SortCode__el_sortcode_1_grp_lbl','htmlFor':'angRef__SortCode__el_sortcode_1','className':'flb lft'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__SortCode__el_sortcode_1_li','className':'eic etw-60'}}}
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_sortcode_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should cover alternate flows of life cycle methods', () => {
  setApz({isNull: function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'angRef__SortCode__el_sortcode_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'angRef__SortCode__el_sortcode_1', cntrType:'FORM', attrs:{'typeclass':'PRESENTATIONELEMENT','name':'angRef__SortCode__el_sortcode_1','type':'PRESENTATIONELEMENT','id':'e69b18004e1995537ca83a83','pid':'dc8b9c604e778c29ba72aae8','widgetcategory':'ELEMENT','widgettype':'SortCode','appearance':'pri','contentalignment':'RIGHT','cntrdescreq':'Y','custom':'Y','datatype':'STRING','events':[{'name':'ONBLUR'}],'headeralignment':'CENTER','labelalignment':'LEFT','labelrequired':'Y','labelwidth':'40','mandatory':'N','options':'N','state':'ENABLED','title':'ID - el_sortcode_1','translatedefaultvalue':'N','account':'Y','parenttype':'FORM','OSTYPE':'SIMULATOR','APPID':'angRef','APPNAME':'angularReference','rowno':1,'scr':'SortCode'}, widgettype:'SortCode', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_sortcode_1', 'direct':{'id':'angRef__SortCode__el_sortcode_1_grp_lbl','htmlFor':'angRef__SortCode__el_sortcode_1','className':'flb lft'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__SortCode__el_sortcode_1_li','className':'eic etw-60'}}}
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_sortcode_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should assign value to value variable on invoking savevalue()', () => {
  setApz({getObjIdWORowNumber: function(params:any) {return 1}, getObjRowNumber: function(param:any) { return 1}, isNull: function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'angRef__SortCode__el_sortcode_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeComponent);
  const component = fixture.componentInstance;
  component.values = {};
  component.props = {id:'angRef__SortCode__el_sortcode_1', cntrType:'FORM', attrs:{'typeclass':'PRESENTATIONELEMENT','name':'angRef__SortCode__el_sortcode_1','type':'PRESENTATIONELEMENT','id':'e69b18004e1995537ca83a83','pid':'dc8b9c604e778c29ba72aae8','widgetcategory':'ELEMENT','widgettype':'SortCode','appearance':'pri','contentalignment':'RIGHT','cntrdescreq':'Y','custom':'Y','datatype':'STRING','events':[{'name':'ONBLUR'}],'headeralignment':'CENTER','labelalignment':'LEFT','labelrequired':'Y','labelwidth':'40','mandatory':'N','options':'N','state':'ENABLED','title':'ID - el_sortcode_1','translatedefaultvalue':'N','account':'Y','parenttype':'FORM','OSTYPE':'SIMULATOR','APPID':'angRef','APPNAME':'angularReference','rowno':1,'scr':'SortCode'}, widgettype:'SortCode', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_sortcode_1', 'direct':{'id':'angRef__SortCode__el_sortcode_1_grp_lbl','htmlFor':'angRef__SortCode__el_sortcode_1','className':'flb lft'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__SortCode__el_sortcode_1_li','className':'eic etw-60'}}}
  mockDataService.isUiElm.withArgs(component.props.id).and.returnValue(false);
  component.saveValue(component.props, {});
  expect(component.values).toBeDefined();
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SortCodeComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', attrs: {defaultValue: '12345678912345678'},tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  component.value = component.props.attrs.defaultValue;
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'appId': 'y'});
  mockDataService.isUiElm.withArgs(component.props.id).and.returnValue(false);
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "Y"}}}});
  mockDataService.isUiElm.withArgs(component.props.id).and.returnValue(true);
  component.setContent();
  expect(component.value1).toEqual(component.value.substr(0,2))
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  component.row = 1;
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});


});