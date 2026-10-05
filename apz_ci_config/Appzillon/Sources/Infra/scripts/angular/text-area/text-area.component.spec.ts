import { TestBed } from '@angular/core/testing';
import { TextAreaComponent  } from './text-area.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('TextAreaComponent', () => {
 let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      TextAreaComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the TextAreaComponent', () => {
   const fixture = TestBed.createComponent(TextAreaComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('isUiElm() should return true when text element is an UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__TeaxtArea__el_txt_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(TextAreaComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__TeaxtArea__el_txt_1', tdClasses:' pri', apzcontrol:'', value:'ID	', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__TeaxtArea__el_txt_1_grp_lbl','htmlFor':'angRef__TeaxtArea__el_txt_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__TeaxtArea__el_txt_1_li','className':'eic etw-100'}}}
  expect(component.isUiElm()).toBeTrue();
});

it('isUiElm() should return false when text element is bind to an interface', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__TeaxtArea__el_txt_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(TextAreaComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__TeaxtArea__el_txt_1', tdClasses:' pri', apzcontrol:'', value:'ID	', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__TeaxtArea__el_txt_1_grp_lbl','htmlFor':'angRef__TeaxtArea__el_txt_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__TeaxtArea__el_txt_1_li','className':'eic etw-100'}}}
  expect(component.isUiElm()).toBeFalse();
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(TextAreaComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', attrs: {defaultValue: '12345678912345678'},tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  //component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
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

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(TextAreaComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'angRef__TeaxtArea__el_txt_1', tdClasses:' pri', apzcontrol:'', value:'ID	', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__TeaxtArea__el_txt_1_grp_lbl','htmlFor':'angRef__TeaxtArea__el_txt_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__TeaxtArea__el_txt_1_li','className':'eic etw-100'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(TextAreaComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__TeaxtArea__el_txt_1', tdClasses:' pri', apzcontrol:'', value:'ID	', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__TeaxtArea__el_txt_1_grp_lbl','htmlFor':'angRef__TeaxtArea__el_txt_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__TeaxtArea__el_txt_1_li','className':'eic etw-100'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle methods', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__TeaxtArea__el_txt_1': {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"}}}});
  const fixture = TestBed.createComponent(TextAreaComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'angRef__TeaxtArea__el_txt_1', tdClasses:' pri', apzcontrol:'', value:'ID	', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__TeaxtArea__el_txt_1_grp_lbl','htmlFor':'angRef__TeaxtArea__el_txt_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__TeaxtArea__el_txt_1_li','className':'eic etw-100'}}}
  component.elmData =  {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"};
  component.containerId = component.elmData.container;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle methods for control type TABLE', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__TeaxtArea__el_txt_1': {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"}}}});
  const fixture = TestBed.createComponent(TextAreaComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'angRef__TeaxtArea__el_txt_1', tdClasses:' pri', apzcontrol:'', value:'ID	', heading:'h3', cntrType:'TABLE', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__TeaxtArea__el_txt_1_grp_lbl','htmlFor':'angRef__TeaxtArea__el_txt_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__TeaxtArea__el_txt_1_li','className':'eic etw-100'}}}
  component.elmData = {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"};
  component.containerId = component.elmData.container;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should cover alternate flows of life cycle', () => {
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'angRef__TeaxtArea__el_txt_1': {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"}}}});
  const fixture = TestBed.createComponent(TextAreaComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.rowIndex = 0;
  component.props = {id:'angRef__TeaxtArea__el_txt_1',hasLov: "Y", tdClasses:' pri', apzcontrol:'', buttonIcon: {'direct': {}},iconPosition: "LEFT", value:'ID	', heading:'h3', cntrType:'LIST', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', textSpanWrapper: {'direct': {}}, elmSpanWrapper: {'direct': {}}, btnWrapper: {'direct': {}}, data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__TeaxtArea__el_txt_1_grp_lbl','htmlFor':'angRef__TeaxtArea__el_txt_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__TeaxtArea__el_txt_1_li','className':'eic etw-100'}}}
  component.elmData =  {'ui': "N", container: "SonarA__NewScreen__ct_frm_6"};
  component.containerId = component.elmData.container;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
  component.setHandler(); //Need to find a better way to cover this. 
});

it('should emit labelIconClick event', () => {
  const fixture = TestBed.createComponent(TextAreaComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.iconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
});
});