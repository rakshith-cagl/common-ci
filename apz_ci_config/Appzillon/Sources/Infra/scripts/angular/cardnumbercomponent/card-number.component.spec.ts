import { TestBed } from '@angular/core/testing';
import { ChangeDetectorRef } from '@angular/core';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
import { CardNumberComponent  } from './card-number.component';
describe('CardNumberComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      CardNumberComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

    it('should create the CardNumberComponent', () => {
      const fixture = TestBed.createComponent(CardNumberComponent);
      const app = fixture.componentInstance;
      expect(app).toBeTruthy();
    });

    it('isUiElm() should return true when card number is an UI element', () => {
      setApz({'scrMetaData': {'elmsMap': {'angRef__CardNumber__el_txt_1': {'ui': "Y"}}}});
      const fixture = TestBed.createComponent(CardNumberComponent);
      const component = fixture.componentInstance;
      component.props = {'id': 'angRef__CardNumber__el_txt_1'};
      expect(component.isUiElm()).toBeTrue();
    });

    it('saveValue() should set value to val variable based on if current element is UI element or bind to an interface', () => {
      setApz({'scrMetaData': {'elmsMap': {'angRef__CardNumber__el_txt_1': {'ui': "Y"}}}});
      const fixture = TestBed.createComponent(CardNumberComponent);
      const component = fixture.componentInstance;
      component.props = {'id': 'angRef__CardNumber__el_txt_1'};
      component.saveValue(component.props, {target: {value: '1234 5678 1234 5678'}});
      expect(component.value).toEqual('1234567812345678');
      setApz({'scrMetaData': {'elmsMap': {'angRef__CardNumber__el_txt_1': {'ui': "N"}}}});
      component.elm = 'angRef,CardNumber';
      component.values = [];
      component.saveValue(component.props, {target: {value: '1234 5678 1234 5678'}});
      expect(component.values[component.elm]).toEqual('1234567812345678');
    });

    it('isUiElm() should return false when card number is bind to an interface', () => {
      setApz({'scrMetaData': {'elmsMap': {'angRef__CardNumber__el_txt_1': {'ui': "N"}}}});
      const fixture = TestBed.createComponent(CardNumberComponent);
      const component = fixture.componentInstance;
      component.props = {'id': 'angRef__CardNumber__el_txt_1'};
      expect(component.isUiElm()).toBeFalse();
    });

    it('should invoke life cycle methods', () => {
      setApz({'isNull': function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'angRef__CardNumber__el_txt_1': {'ui': "Y", 'container': {}}}}});
      const fixture = TestBed.createComponent(CardNumberComponent);
      const component = fixture.componentInstance;
      component.row = 0;
      component.props = {id:'angRef__CardNumber__el_txt_1',  attrs: {'labelrequired': 'Y', 'contentalignment': 'LEFT', 'appearance': 'appr-cls', state: 'DISABLED','placeholder': 'Sample placeholder', 'rowno': 1, 'defaultvalue': '', options: 'Y', tooltip: 'Y'}, tdClasses:' pri', apzcontrol:'', buttonTitle: {value: 'Test'}, value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
      fixture.detectChanges();
      expect(component.wrapperClassesRef.nativeElement).toBeDefined();
    });

    it('should set contentAllignCls and stateAttr variables based on the value passed to it', () => {
      setApz({'isNull': function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'angRef__CardNumber__el_txt_1': {'ui': "Y", 'container': {}}}}});
      const fixture = TestBed.createComponent(CardNumberComponent);
      const component = fixture.componentInstance;
      component.row = 0;
      component.props = {id:'angRef__CardNumber__el_txt_1',  attrs: {'labelrequired': 'Y', 'contentalignment': 'CENTER', state: 'READONLY','cssclasses': 'pri','placeholder': 'Sample placeholder', 'rowno': 1, 'defaultvalue': '', options: 'N', tooltip: 'Y'}, tdClasses:' pri', apzcontrol:'', buttonTitle: {value: 'Test'}, value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
      fixture.detectChanges();
      expect(component.contentAllignCls).toEqual('cen');
      expect(component.stateAttr).toEqual({ readOnly: 'readOnly' });
    });

    it('should set stateArr variable to empty string if value is not sent in props', () => {
      setApz({'isNull': function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'angRef__CardNumber__el_txt_1': {'ui': "Y", 'container': {}}}}});
      const fixture = TestBed.createComponent(CardNumberComponent);
      const component = fixture.componentInstance;
      component.row = 0;
      component.rowIndex = 0;
      component.props = {id:'angRef__CardNumber__el_txt_1',  attrs: {'labelrequired': 'Y', 'contentalignment': 'RIGHT','placeholder': 'Sample placeholder', 'rowno': 1, 'defaultvalue': '', options: 'Y', 'appearance': ''}, tdClasses:' pri', apzcontrol:'', buttonTitle: {value: 'Test'}, value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
      fixture.detectChanges();
      expect(component.contentAllignCls).toEqual('rht');
      expect(component.stateAttr).toEqual({ stateAttr: '' });
    });


it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(CardNumberComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__CardNumber__el_txt_1', tdClasses:' pri', apzcontrol:'', title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.rowIndex = 0;
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(CardNumberComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__CardNumber__el_txt_1', tdClasses:' pri', apzcontrol:'', title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('setHandler() should invoke detect changes', () => {
  setApz({'isNull': function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'angRef__CardNumber__el_txt_1': {'ui': "Y", 'container': {}}}}});
  const fixture = TestBed.createComponent(CardNumberComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.rowIndex = 0;
  component.props = {id:'angRef__CardNumber__el_txt_1',  attrs: {'labelrequired': 'Y', 'contentalignment': 'RIGHT','placeholder': 'Sample placeholder', 'rowno': 1, 'defaultvalue': '', options: 'Y', 'appearance': ''}, tdClasses:' pri', apzcontrol:'', buttonTitle: {value: 'Test'}, value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.setHandler();
});

it('should assign formatted card number value to num variable when row is defined', () => {
  setApz({'isNull': function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'angRef__CardNumber__el_txt_1': {'ui': "Y", 'container': {}}}}});
  const fixture = TestBed.createComponent(CardNumberComponent);
  const component = fixture.componentInstance;
  component.row = 1;
  component.value = '1234567812345678'
  component.props = {id:'angRef__CardNumber__el_txt_1',  attrs: {'labelrequired': 'Y', 'contentalignment': 'LEFT', state: 'DISABLED','placeholder': 'Sample placeholder', 'rowno': 1, 'defaultvalue': '', options: 'Y', tooltip: 'Y'}, tdClasses:' pri', apzcontrol:'', buttonTitle: {value: 'Test'}, value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.setContent();
  expect(component.num).toEqual('1234 5678 1234 5678');
});

it('should assign formatted card number value to num variable when row is not defined', () => {
  setApz({'isNull': function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'angRef__CardNumber__el_txt_1': {'ui': "Y", 'container': {}}}}});
  const fixture = TestBed.createComponent(CardNumberComponent);
  const component = fixture.componentInstance;
  component.value = '1234567812345678'
  component.props = {id:'angRef__CardNumber__el_txt_1',  attrs: {'labelrequired': 'Y', 'contentalignment': 'LEFT', state: 'DISABLED','placeholder': 'Sample placeholder', 'rowno': 1, 'defaultvalue': '', options: 'Y', tooltip: 'Y'}, tdClasses:' pri', apzcontrol:'', buttonTitle: {value: 'Test'}, value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.setContent();
  expect(component.num).toEqual('1234 5678 1234 5678');
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({'isNull': function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(CardNumberComponent);
  const component = fixture.componentInstance;
  component.id = "InterfaceQuery__i__tbAsmiIntfMaster__appId";
  component.props = {id: 'InterfaceQuery__i__tbAsmiIntfMaster__appId'};
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'appId': '1234567812345678'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  expect(component.num).toEqual('1234 5678 1234 5678');
  component.row = 1;
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': '1234567812345678'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  expect(component.num).toEqual('1234 5678 1234 5678');
});

});