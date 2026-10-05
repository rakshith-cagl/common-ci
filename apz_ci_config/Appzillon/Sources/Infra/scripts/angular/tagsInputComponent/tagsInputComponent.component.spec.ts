import { TestBed } from '@angular/core/testing';
import { TagsInputComponent  } from './tagsInputComponent.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('TagsInputComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      TagsInputComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the TagsInputComponent', () => {
   const fixture = TestBed.createComponent(TagsInputComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('isUiElm() should return true when tags input is an UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Tags__el_tag_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(TagsInputComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Tags__el_tag_1', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'angRef__Tags__el_tag_1_span_-1', 'className':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'', 'style':{}, 'placeHolder':'ID - el_tag_1',  'enabled':'enabled','original-title':'', 'type':'text','name':'el_tag_1'}}, tagsWrapper:{'className':'ett-tags etb-tags etw-50 pri', 'disabled':''}, IconContent:{'name':'','direct':{'className':'icon  px24' }}, tagsHintRequired:'N' , widgettype:'TAGS', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_tag_1', 'direct':{'id':'angRef__Tags__el_tag_1_grp_lbl','htmlFor':'angRef__Tags__el_tag_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Tags__el_tag_1_li','className':'eic etw-60'}}}
  expect(component.isUiElm()).toBeTrue();
});

it('isUiElm() should return false when tags input is bind to an interface', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Tags__el_tag_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(TagsInputComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Tags__el_tag_1', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'angRef__Tags__el_tag_1_span_-1', 'className':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'', 'style':{}, 'placeHolder':'ID - el_tag_1',  'enabled':'enabled','original-title':'', 'type':'text','name':'el_tag_1'}}, tagsWrapper:{'className':'ett-tags etb-tags etw-50 pri', 'disabled':''}, IconContent:{'name':'','direct':{'className':'icon  px24' }}, tagsHintRequired:'N' , widgettype:'TAGS', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_tag_1', 'direct':{'id':'angRef__Tags__el_tag_1_grp_lbl','htmlFor':'angRef__Tags__el_tag_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Tags__el_tag_1_li','className':'eic etw-60'}}}
  expect(component.isUiElm()).toBeFalse();
});


it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(TagsInputComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'angRef__Tags__el_tag_1', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'angRef__Tags__el_tag_1_span_-1', 'className':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'', 'style':{}, 'placeHolder':'ID - el_tag_1',  'enabled':'enabled','original-title':'', 'type':'text','name':'el_tag_1'}}, tagsWrapper:{'className':'ett-tags etb-tags etw-50 pri', 'disabled':''}, IconContent:{'name':'','direct':{'className':'icon  px24' }}, tagsHintRequired:'N' , widgettype:'TAGS', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_tag_1', 'direct':{'id':'angRef__Tags__el_tag_1_grp_lbl','htmlFor':'angRef__Tags__el_tag_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Tags__el_tag_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(TagsInputComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Tags__el_tag_1', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'angRef__Tags__el_tag_1_span_-1', 'className':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'', 'style':{}, 'placeHolder':'ID - el_tag_1',  'enabled':'enabled','original-title':'', 'type':'text','name':'el_tag_1'}}, tagsWrapper:{'className':'ett-tags etb-tags etw-50 pri', 'disabled':''}, IconContent:{'name':'','direct':{'className':'icon  px24' }}, tagsHintRequired:'N' , widgettype:'TAGS', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_tag_1', 'direct':{'id':'angRef__Tags__el_tag_1_grp_lbl','htmlFor':'angRef__Tags__el_tag_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Tags__el_tag_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});


it('should emit labelIconClick event', () => {
  const fixture = TestBed.createComponent(TagsInputComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.iconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
});

it('should invoke life cycle methods', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Tags__el_tag_1': {'ui': "N", container: {}}}}});
  const fixture = TestBed.createComponent(TagsInputComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'angRef__Tags__el_tag_1', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'angRef__Tags__el_tag_1_span_-1', 'className':'ecn etw-50'}, cntrType:'FORM', value:'', content:{'direct':{'className':'', 'style':{}, 'placeHolder':'ID - el_tag_1',  'enabled':'enabled','original-title':'', 'type':'text','name':'el_tag_1'}}, tagsWrapper:{'className':'ett-tags etb-tags etw-50 pri', 'disabled':''}, IconContent:{'name':'','direct':{'className':'icon  px24' }}, tagsHintRequired:'N' , widgettype:'TAGS', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_tag_1', 'direct':{'id':'angRef__Tags__el_tag_1_grp_lbl','htmlFor':'angRef__Tags__el_tag_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Tags__el_tag_1_li','className':'eic etw-60'}}}
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_tag_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle methods for control type TABLE', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Tags__el_tag_1': {'ui': "N", container: {}}}}});
  const fixture = TestBed.createComponent(TagsInputComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.rowIndex = 0;
  component.props = {id:'angRef__Tags__el_tag_1', value: 'Test val', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'angRef__Tags__el_tag_1_span_-1', 'className':'ecn etw-50'}, cntrType:'TABLE', content:{'direct':{'className':'', 'style':{}, 'placeHolder':'ID - el_tag_1',  'enabled':'enabled','original-title':'', 'type':'text','name':'el_tag_1'}}, tagsWrapper:{'className':'ett-tags etb-tags etw-50 pri', 'disabled':''}, IconContent:{'name':'','direct':{'className':'icon  px24' }}, tagsHintRequired:'N' , widgettype:'TAGS', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_tag_1', 'direct':{'id':'angRef__Tags__el_tag_1_grp_lbl','htmlFor':'angRef__Tags__el_tag_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Tags__el_tag_1_li','className':'eic etw-60'}}}
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_tag_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should invoke life cycle methods for control type LIST', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Tags__el_tag_1': {'ui': "N", container: {}}}}});
  const fixture = TestBed.createComponent(TagsInputComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'angRef__Tags__el_tag_1', value: 'Test val', tdClasses:' pri', apzcontrol:'', spanWrapper:{'id':'angRef__Tags__el_tag_1_span_-1', 'className':'ecn etw-50'}, cntrType:'LIST', content:{'direct':{'className':'', 'style':{}, 'placeHolder':'ID - el_tag_1',  'enabled':'enabled','original-title':'', 'type':'text','name':'el_tag_1'}}, tagsWrapper:{'className':'ett-tags etb-tags etw-50 pri', 'disabled':''}, IconContent:{'name':'','direct':{'className':'icon  px24' }}, tagsHintRequired:'N' , widgettype:'TAGS', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_tag_1', 'direct':{'id':'angRef__Tags__el_tag_1_grp_lbl','htmlFor':'angRef__Tags__el_tag_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Tags__el_tag_1_li','className':'eic etw-60'}}}
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_tag_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should return values variable data if exists else empty string on invoking getDataValue()', () => {
  const fixture = TestBed.createComponent(TagsInputComponent);
  const component = fixture.componentInstance;
  expect(component.getDataValue()).toEqual('');
  component.elm = 'el_tag_1';
  component.values = {'el_tag_1': 'Test val'};
  expect(component.getDataValue()).toEqual(component.values["el_tag_1"]);
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(TagsInputComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', attrs: {defaultValue: '12345678912345678'},tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  component.value = component.props.attrs.defaultValue;
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  component.row = 1;
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});
});