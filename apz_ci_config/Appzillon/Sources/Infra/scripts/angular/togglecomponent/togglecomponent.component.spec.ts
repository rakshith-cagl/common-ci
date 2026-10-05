import { TestBed } from '@angular/core/testing';
import { ToggleComponent  } from './togglecomponent.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('ToggleComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
  beforeEach(async () => {
   const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
    await TestBed.configureTestingModule({
      imports: [
      ],
      declarations: [
        ToggleComponent
      ],
      providers: [WindowRef, {
       provide: DataService,
       useValue: spy
    }]
    }).compileComponents();
    mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
  });

 it('should create the ToggleComponent', () => {
   const fixture = TestBed.createComponent(ToggleComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('isUiElm() should return true when toggle element is an UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'FORM', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}}
  expect(component.isUiElm()).toBeTrue();
});

it('isUiElm() should return false when toggle element is bind to an interface', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'FORM', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}}
  expect(component.isUiElm()).toBeFalse();
});


it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'FORM', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'FORM', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should emit labelIconClick event', () => {
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.iconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
});


it('should invoke life cycle methods', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_1': {'ui': "N", container:  "SonarA__NewScreen__ct_frm_6"}}}});
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'FORM', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}}
  component.elmData = component.apz.scrMetaData.elmsMap[component.props.id];
  component.containerId = component.elmData.container;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'el_tgl_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle method for container type TABLE', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_1': {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"}}}});
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  component.row = 1;
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'TABLE', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}}
  component.elmData = component.apz.scrMetaData.elmsMap[component.props.id];
  component.containerId = component.elmData.container;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'el_tgl_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle methods for control type TABLE', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_11': {'ui': "N", container:  "SonarA__NewScreen__ct_frm_6"}}}});
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.rowIndex = 0;
  component.props = {id:'angRef__Toggle__el_tgl_11', ToggleType:'WITHLABEL', tdClasses:' pri', cntrType:'LIST', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-swch  pri etw-50','style':{}}}, firstButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'on','original-title':'', 'enabled':'enabled','style':{}}}, firstLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 1'}}, apzcontrol:'', secondButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'off','original-title':'', 'enabled':'enabled','style':{}}}, secondLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 2'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_11_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_11','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'className':'eic etw-60'}}}
  component.elmData = component.apz.scrMetaData.elmsMap[component.props.id];
  component.containerId = component.elmData.container;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'el_tgl_11': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N", container:  "SonarA__NewScreen__ct_frm_6"}}}});
  const fixture = TestBed.createComponent(ToggleComponent);
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


it('should handle the label change on invoking onChangeLabel()', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_11': {'ui': "N", container: {}}}}});
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  component.values = {};
  component.props = {id:'angRef__Toggle__el_tgl_11', ToggleType:'WITHLABEL', tdClasses:' pri', cntrType:'LIST', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-swch  pri etw-50','style':{}}}, firstButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'on','original-title':'', 'enabled':'enabled','style':{}}}, firstLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 1'}}, apzcontrol:'', secondButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'off','original-title':'', 'enabled':'enabled','style':{}}}, secondLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 2'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_11_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_11','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'className':'eic etw-60'}}}
  component.elm = component.props.id.split("__").pop(); 
  component.onChangeLabel({target: {value: 'on'}})
  expect(component.values['el_tgl_11']).toEqual('on');
  component.onChangeLabel({target: {value: 'off'}})
  expect(component.values['el_tgl_11']).toEqual('off');
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_11': {'ui': "Y", container: {}}}}});
  component.onChangeLabel({target: {value: 'on'}})      
  expect(component.value).toEqual('on');
  component.onChangeLabel({target: {value: 'off'}})
  expect(component.value).toEqual('off');
});

it('should handle the change of checked value through onCheckedChange()', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_1': {'ui': "N", container: {}}}}});
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  component.values = {};
  component.id = "angRef__Toggle__el_tgl_1";
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'TABLE', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}}
  component.elm = component.props.id.split("__").pop(); 
  component.onCheckedChange({target: {checked: true}})
  expect(component.values['el_tgl_1']).toEqual(component.props.content.direct.checkedval);
  component.onCheckedChange({target: {checked: false}})
  expect(component.values['el_tgl_1']).toEqual(component.props.content.direct.uncheckedval);
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_1': {'ui': "Y", container: {}}}}});
  component.onCheckedChange({target: {checked: true}})      
  expect(component.value).toEqual(component.props.content.direct.checkedval);
  component.onCheckedChange({target: {checked: false}})
  expect(component.value).toEqual(component.props.content.direct.uncheckedval);
});

it('should handle the change of checked value through setContentAfterViewinit()', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_1': {'ui': "N", container: {}}}}});
  const fixture = TestBed.createComponent(ToggleComponent);
  const component = fixture.componentInstance;
  component.values = {};
  component.elm = "el_tgl_1";
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'TABLE', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}} 
  component.values['el_tgl_1'] = component.props.content.direct.checkedval;
  component.setContentAfterViewinit();
  expect(component.checked).toEqual(true);
  component.values['el_tgl_1'] = component.props.content.direct.uncheckedval;
  component.setContentAfterViewinit();
  expect(component.checked).toEqual(false);
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHLABEL', tdClasses:' pri', cntrType:'LIST', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-swch  pri etw-50','style':{}}}, firstButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'on','original-title':'', 'enabled':'enabled','style':{}}}, firstLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 1'}}, apzcontrol:'', secondButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'off','original-title':'', 'enabled':'enabled','style':{}}}, secondLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 2'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_11_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_11','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'className':'eic etw-60'}}}
  component.id = component.props.id
  component.values['el_tgl_1'] = component.props.firstButton.direct.value;
  component.setContentAfterViewinit();
  component.values['el_tgl_1'] = component.props.secondButton.direct.value;
  component.setContentAfterViewinit();
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_1': {'ui': "Y", container: {}}}}});
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'TABLE', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}} 
  component.value = "on";
  component.id = component.props.id;
  component.setContentAfterViewinit();
  expect(component.checked).toEqual(true);
  component.value = "off";
  component.setContentAfterViewinit();
  expect(component.checked).toEqual(false);
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHLABEL', tdClasses:' pri', cntrType:'LIST', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-swch  pri etw-50','style':{}}}, firstButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'on','original-title':'', 'enabled':'enabled','style':{}}}, firstLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 1'}}, apzcontrol:'', secondButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'off','original-title':'', 'enabled':'enabled','style':{}}}, secondLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 2'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_11_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_11','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'className':'eic etw-60'}}}
  component.value = "on";
  component.setContentAfterViewinit();
  expect(component.value).toEqual(component.props.firstButton.direct.value);
  component.value = "off";
  component.setContentAfterViewinit();
  expect(component.value).toEqual(component.props.secondButton.direct.value);
  component.row = 1;
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_1': {'ui': "N", container: {}}}}});
  component.values = {};
  component.elm = "el_tgl_1";
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'TABLE', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}} 
  component.values['el_tgl_1'] = component.props.content.direct.checkedval;
  component.setContentAfterViewinit();
  expect(component.checked).toEqual(true);
  component.values['el_tgl_1'] = component.props.content.direct.uncheckedval;
  component.setContentAfterViewinit();
  expect(component.checked).toEqual(false);
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHLABEL', tdClasses:' pri', cntrType:'LIST', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-swch  pri etw-50','style':{}}}, firstButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'on','original-title':'', 'enabled':'enabled','style':{}}}, firstLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 1'}}, apzcontrol:'', secondButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'off','original-title':'', 'enabled':'enabled','style':{}}}, secondLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 2'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_11_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_11','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'className':'eic etw-60'}}}
  component.id = component.props.id
  component.values['el_tgl_1'] = component.props.firstButton.direct.value;
  component.setContentAfterViewinit();
  component.values['el_tgl_1'] = component.props.secondButton.direct.value;
  component.setContentAfterViewinit();
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__Toggle__el_tgl_1': {'ui': "Y", container: {}}}}});
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHOUTLABEL', tdClasses:' pri', cntrType:'TABLE', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-togl  pri etw-50','style':{}}}, content:{'direct':{'className':' etw-50',  'checkedval':'on',  'uncheckedval':'off', 'enabled':'enabled','apztype':'toggleswitch'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_1_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Toggle__el_tgl_1_li','className':'eic etw-60'}}} 
  component.value = "on";
  component.id = component.props.id;
  component.setContentAfterViewinit();
  expect(component.checked).toEqual(true);
  component.value = "off";
  component.setContentAfterViewinit();
  expect(component.checked).toEqual(false);
  component.props = {id:'angRef__Toggle__el_tgl_1', ToggleType:'WITHLABEL', tdClasses:' pri', cntrType:'LIST', defaultvalue:'', toggleWrapper:{'direct':{'className':'ett-swch  pri etw-50','style':{}}}, firstButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'on','original-title':'', 'enabled':'enabled','style':{}}}, firstLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 1'}}, apzcontrol:'', secondButton:{'direct':{'className':'ett-swch  etw-50 etw-50','aria-labelledby':'angRef__Toggle__el_tgl_11_ctrl_div','type':'radio','value':'off','original-title':'', 'enabled':'enabled','style':{}}}, secondLabel:{'direct':{'className':' etw-50','original-title':'', 'enabled':'enabled','value':'Label 2'}}, widgettype:'TOGGLESWITCH', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Toggle Switch - Primary', 'direct':{'id':'angRef__Toggle__el_tgl_11_grp_lbl','htmlFor':'angRef__Toggle__el_tgl_11','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'className':'eic etw-60'}}}
  component.value = "on";
  component.setContentAfterViewinit();
  expect(component.value).toEqual(component.props.firstButton.direct.value);
  component.value = "off";
  component.setContentAfterViewinit();
  expect(component.value).toEqual(component.props.secondButton.direct.value);
});
});