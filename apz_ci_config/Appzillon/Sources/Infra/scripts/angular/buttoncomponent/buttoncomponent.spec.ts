import { TestBed } from '@angular/core/testing';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
import { ButtonComponent  } from './buttoncomponent';
describe('ButtonComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      ButtonComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the ButtonComponent', () => {
   const fixture = TestBed.createComponent(ButtonComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });


 it('isUiElm() should return true when bullet is an UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'test_button_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(ButtonComponent);
  const component = fixture.componentInstance;
  component.props = {'id': 'test_button_1'};
  expect(component.isUiElm()).toBeTrue();
});

it('isUiElm() should return false when bullet is bind to an interface', () => {
  setApz({'scrMetaData': {'elmsMap': {'test_button_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(ButtonComponent);
  const component = fixture.componentInstance;
  component.props = {'id': 'test_button_1'};
  expect(component.isUiElm()).toBeFalse();
});

it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Button__el_txt_1': {'ui': "Y", 'container': {}}}}});
  const fixture = TestBed.createComponent(ButtonComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'angRef__Button__el_txt_1', tdClasses:' pri', apzcontrol:'', buttonTitle: {value: 'Test'}, value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', labelIconProps: {direct: {}},contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  fixture.detectChanges();
  expect(component.labelWrapper.nativeElement).toBeDefined();
});

it('should cover alternate life cycle methods flows', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Button__el_txt_1': {'ui': "Y", 'container': {}}}}});
  const fixture = TestBed.createComponent(ButtonComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'angRef__Button__el_txt_1', tdClasses:' pri', apzcontrol:'', buttonTitle: {}, title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
  expect(component.index).toEqual(component.rowIndex);
});

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(ButtonComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Button__el_txt_1', tdClasses:' pri', apzcontrol:'', title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.rowIndex = 0;
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
  expect(component.index).toEqual(component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(ButtonComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Button__el_txt_1', tdClasses:' pri', apzcontrol:'', title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(ButtonComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', title: 'Test', attrs: {defaultValue: '12345678912345678'},tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
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
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "Y"}}}});
  component.setContent();
  expect(component.value).toEqual(component.props.title);
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', title: 'Test', buttonTitle: {value: 'Test'}, attrs: {defaultValue: '12345678912345678'},tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.setContent();
  expect(component.value).toEqual(component.props.buttonTitle.value)
});

});