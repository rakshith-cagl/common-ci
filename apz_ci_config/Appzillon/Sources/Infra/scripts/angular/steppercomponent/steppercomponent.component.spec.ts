import { TestBed } from '@angular/core/testing';
import { StepperComponent  } from './steppercomponent.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('StepperComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      StepperComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the StepperComponent', () => {
   const fixture = TestBed.createComponent(StepperComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });


 it('isUiElm() should return true when progress step is an UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Stepper__el_stp_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Stepper__el_stp_1', tdClasses:' pri', apzcontrol:'' , stepperWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_stepper','className':'ett-stpr etb-stpr  pri'}}, cntrType:'FORM', leftButton:{'direct':{'className':'ett-bttn med tsp ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_left'}}, leftIcon:{'name':'icon-minus','direct':{'className':'ett-icon icon-minus px24'}}, content:{'direct':{'className':'ett-inpt etw-100 pri lft','original-title':'','min':'-9007199254740991','max':'9007199254740991','value':'',  'enabled':'enabled', 'step':'', 'placeholder':'ID - el_stp_1'}}, rightButton:{'direct':{'className':'ett-bttn med tsp stpr-add ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_right'}}, rightIcon:{'name':'icon-plus','direct':{'className':'ett-icon icon-plus px24'}}, widgettype:'STEPPER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_stp_1', 'direct':{'id':'angRef__Stepper__el_stp_1_grp_lbl','htmlFor':'angRef__Stepper__el_stp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_li','className':'eic etw-60'}}}
  expect(component.isUiElm()).toBeTrue();
});

it('isUiElm() should return false when progress step is bind to an interface', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Stepper__el_stp_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Stepper__el_stp_1', tdClasses:' pri', apzcontrol:'' , stepperWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_stepper','className':'ett-stpr etb-stpr  pri'}}, cntrType:'FORM', leftButton:{'direct':{'className':'ett-bttn med tsp ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_left'}}, leftIcon:{'name':'icon-minus','direct':{'className':'ett-icon icon-minus px24'}}, content:{'direct':{'className':'ett-inpt etw-100 pri lft','original-title':'','min':'-9007199254740991','max':'9007199254740991','value':'',  'enabled':'enabled', 'step':'', 'placeholder':'ID - el_stp_1'}}, rightButton:{'direct':{'className':'ett-bttn med tsp stpr-add ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_right'}}, rightIcon:{'name':'icon-plus','direct':{'className':'ett-icon icon-plus px24'}}, widgettype:'STEPPER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_stp_1', 'direct':{'id':'angRef__Stepper__el_stp_1_grp_lbl','htmlFor':'angRef__Stepper__el_stp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_li','className':'eic etw-60'}}}
  expect(component.isUiElm()).toBeFalse();
});


it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'angRef__Stepper__el_stp_1', tdClasses:' pri', apzcontrol:'' , stepperWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_stepper','className':'ett-stpr etb-stpr  pri'}}, cntrType:'FORM', leftButton:{'direct':{'className':'ett-bttn med tsp ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_left'}}, leftIcon:{'name':'icon-minus','direct':{'className':'ett-icon icon-minus px24'}}, content:{'direct':{'className':'ett-inpt etw-100 pri lft','original-title':'','min':'-9007199254740991','max':'9007199254740991','value':'',  'enabled':'enabled', 'step':'', 'placeholder':'ID - el_stp_1'}}, rightButton:{'direct':{'className':'ett-bttn med tsp stpr-add ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_right'}}, rightIcon:{'name':'icon-plus','direct':{'className':'ett-icon icon-plus px24'}}, widgettype:'STEPPER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_stp_1', 'direct':{'id':'angRef__Stepper__el_stp_1_grp_lbl','htmlFor':'angRef__Stepper__el_stp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Stepper__el_stp_1', tdClasses:' pri', apzcontrol:'' , stepperWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_stepper','className':'ett-stpr etb-stpr  pri'}}, cntrType:'FORM', leftButton:{'direct':{'className':'ett-bttn med tsp ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_left'}}, leftIcon:{'name':'icon-minus','direct':{'className':'ett-icon icon-minus px24'}}, content:{'direct':{'className':'ett-inpt etw-100 pri lft','original-title':'','min':'-9007199254740991','max':'9007199254740991','value':'',  'enabled':'enabled', 'step':'', 'placeholder':'ID - el_stp_1'}}, rightButton:{'direct':{'className':'ett-bttn med tsp stpr-add ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_right'}}, rightIcon:{'name':'icon-plus','direct':{'className':'ett-icon icon-plus px24'}}, widgettype:'STEPPER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_stp_1', 'direct':{'id':'angRef__Stepper__el_stp_1_grp_lbl','htmlFor':'angRef__Stepper__el_stp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});


it('should emit labelIconClick event', () => {
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.iconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
});

it('should invoke life cycle methods', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Stepper__el_stp_1': {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"}}}});
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.elmData = {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"}
  component.containerId = component.elmData.container;
  component.props = {id:'angRef__Stepper__el_stp_1', tdClasses:' pri', apzcontrol:'' , stepperWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_stepper','className':'ett-stpr etb-stpr  pri'}}, cntrType:'FORM', leftButton:{'direct':{'className':'ett-bttn med tsp ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_left'}}, leftIcon:{'name':'icon-minus','direct':{'className':'ett-icon icon-minus px24'}}, content:{'direct':{'className':'ett-inpt etw-100 pri lft','original-title':'','min':'-9007199254740991','max':'9007199254740991','value':'',  'enabled':'enabled', 'step':'', 'placeholder':'ID - el_stp_1'}}, rightButton:{'direct':{'className':'ett-bttn med tsp stpr-add ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_right'}}, rightIcon:{'name':'icon-plus','direct':{'className':'ett-icon icon-plus px24'}}, widgettype:'STEPPER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_stp_1', 'direct':{'id':'angRef__Stepper__el_stp_1_grp_lbl','htmlFor':'angRef__Stepper__el_stp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_li','className':'eic etw-60'}}}
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'el_stp_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle methods for control type TABLE', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Stepper__el_stp_1': {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"}}}});
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.elmData = {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"}
  component.containerId = component.elmData.container;
  component.props = {id:'angRef__Stepper__el_stp_1', tdClasses:' pri', apzcontrol:'' , stepperWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_stepper','className':'ett-stpr etb-stpr  pri'}}, cntrType:'TABLE', leftButton:{'direct':{'className':'ett-bttn med tsp ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_left'}}, leftIcon:{'name':'icon-minus','direct':{'className':'ett-icon icon-minus px24'}}, content:{'direct':{'className':'ett-inpt etw-100 pri lft','original-title':'','min':'-9007199254740991','max':'9007199254740991','value':'',  'enabled':'enabled', 'step':'', 'placeholder':'ID - el_stp_1'}}, rightButton:{'direct':{'className':'ett-bttn med tsp stpr-add ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_right'}}, rightIcon:{'name':'icon-plus','direct':{'className':'ett-icon icon-plus px24'}}, widgettype:'STEPPER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_stp_1', 'direct':{'id':'angRef__Stepper__el_stp_1_grp_lbl','htmlFor':'angRef__Stepper__el_stp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_li','className':'eic etw-60'}}}
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'el_tgl_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle methods for content alignment centre', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Stepper__el_stp_1': {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"}}}});
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.rowIndex = 0;
  component.elmData = {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"}
  component.containerId = component.elmData.container;
  component.props = {id:'angRef__Stepper__el_stp_1', tdClasses:' pri', apzcontrol:'' , stepperWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_stepper','className':'ett-stpr etb-stpr  pri'}}, cntrType:'LIST', leftButton:{'direct':{'className':'ett-bttn med tsp ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_left'}}, leftIcon:{'name':'icon-minus','direct':{'className':'ett-icon icon-minus px24'}}, content:{'direct':{'className':'ett-inpt etw-100 pri lft','original-title':'','min':'-9007199254740991','max':'9007199254740991','value':'',  'enabled':'enabled', 'step':'', 'placeholder':'ID - el_stp_1'}}, rightButton:{'direct':{'className':'ett-bttn med tsp stpr-add ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_right'}}, rightIcon:{'name':'icon-plus','direct':{'className':'ett-icon icon-plus px24'}}, widgettype:'STEPPER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_stp_1', 'direct':{'id':'angRef__Stepper__el_stp_1_grp_lbl','htmlFor':'angRef__Stepper__el_stp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_li','className':'eic etw-60'}}}
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'el_stp_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
  component.setHandler();//Need to find a better way to cover this method.
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', attrs: {defaultValue: '12345678912345678'},tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  component.value = component.props.attrs.defaultValue;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  component.row = 1;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});

it('should validate the values and assign variable value on invokig validateThevalues()', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Stepper__el_stp_1': {'ui': "N", container: {}}}}});
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  component.values = {'el_stp_1': ''};
  component.elm = 'el_stp_1'
  component.props = {id:'angRef__Stepper__el_stp_1', value: '', tdClasses:' pri', apzcontrol:'' , stepperWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_stepper','className':'ett-stpr etb-stpr  pri'}}, cntrType:'FORM', leftButton:{'direct':{'className':'ett-bttn med tsp ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_left'}}, leftIcon:{'name':'icon-minus','direct':{'className':'ett-icon icon-minus px24'}}, content:{'direct':{'className':'ett-inpt etw-100 pri lft','original-title':'','min':'-9007199254740991','max':'9007199254740991','value':'',  'enabled':'enabled', 'step':'', 'placeholder':'ID - el_stp_1'}}, rightButton:{'direct':{'className':'ett-bttn med tsp stpr-add ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_right'}}, rightIcon:{'name':'icon-plus','direct':{'className':'ett-icon icon-plus px24'}}, widgettype:'STEPPER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_stp_1', 'direct':{'id':'angRef__Stepper__el_stp_1_grp_lbl','htmlFor':'angRef__Stepper__el_stp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_li','className':'eic etw-60'}}}
  component.validateThevalues({value: 3, getAttribute: function(param:any) {if (param == "max") return 2; else return 1; }});
  expect(component.values['el_stp_1']).toEqual(2);
  component.validateThevalues({value: 1, getAttribute: function(param:any) {if (param == "max") return 2; else return 1; }});
  expect(component.values['el_stp_1']).toEqual(1);
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Stepper__el_stp_1': {'ui': "Y", container: {}}}}});
  component.validateThevalues({value: 1, getAttribute: function(param:any) {if (param == "max") return 2; else return 1; }});
  expect(component.props.value).toEqual(1);
});

it('should assign variable value on invokig handleStepper()', () => {
  setApz({handleStepperclick: function(param:any) {return true},'scrMetaData': {'elmsMap': {'angRef__Stepper__el_stp_1': {'ui': "N", container: {}}}}});
  const fixture = TestBed.createComponent(StepperComponent);
  const component = fixture.componentInstance;
  component.id = "angRef__Stepper__el_stp_1";
  component.values = {'el_stp_1': ''};
  component.elm = 'el_stp_1'
  component.props = {id:'angRef__Stepper__el_stp_1', tdClasses:' pri', apzcontrol:'' , stepperWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_stepper','className':'ett-stpr etb-stpr  pri'}}, cntrType:'FORM', leftButton:{'direct':{'className':'ett-bttn med tsp ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_left'}}, leftIcon:{'name':'icon-minus','direct':{'className':'ett-icon icon-minus px24'}}, content:{'direct':{'className':'ett-inpt etw-100 pri lft','original-title':'','min':'-9007199254740991','max':'9007199254740991','value':'',  'enabled':'enabled', 'step':'', 'placeholder':'ID - el_stp_1'}}, rightButton:{'direct':{'className':'ett-bttn med tsp stpr-add ', 'enabled':'enabled','id':'angRef__Stepper__el_stp_1_button_right'}}, rightIcon:{'name':'icon-plus','direct':{'className':'ett-icon icon-plus px24'}}, widgettype:'STEPPER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_stp_1', 'direct':{'id':'angRef__Stepper__el_stp_1_grp_lbl','htmlFor':'angRef__Stepper__el_stp_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Stepper__el_stp_1_li','className':'eic etw-60'}}}
  component.handleStepper({currentTarget: 3});
  expect(component.values['el_stp_1']).toBeUndefined();//It is undefined in test case as UI didn't load.
  setApz({handleStepperclick: function(param:any) {return true},'scrMetaData': {'elmsMap': {'angRef__Stepper__el_stp_1': {'ui': "Y", container: {}}}}});
  component.handleStepper({currentTarget: 3});
  expect(component.props.value).toEqual('');//It is undefined in test case as UI didn't load.
});
});