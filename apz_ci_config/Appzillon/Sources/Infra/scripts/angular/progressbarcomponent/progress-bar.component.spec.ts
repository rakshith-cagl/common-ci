import { TestBed } from '@angular/core/testing';
import { ProgressBarComponent  } from './progress-bar.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('ProgressBarComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        ProgressBarComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the ProgressBarComponent', () => {
   const fixture = TestBed.createComponent(ProgressBarComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('should emit labelIconClick event', () => {
  const fixture = TestBed.createComponent(ProgressBarComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.iconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
});

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(ProgressBarComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_pgb_1', progressBarType:'GENERIC', tdClasses:' pri', cntrType:'FORM', ProgressWrapper:{'direct':{'className':'ecn etw-30','style':{}}}, divContent:{'direct':{'className':'ett-prgs pri','style':{},'original-title':'', 'value':'0.80'}},  apzcontrol:'', content:{'direct':{'className':'determinate', 'value':'0.80','widthvaltext':'80.0%'}}, contentstyle:{'style':{'width':'80.0%'}}, ProgressHintRequired:'N' , widgettype:'PROGRESSBAR', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'HTML', 'direct':{'id':'elmnts__Elements__el_pgb_1_grp_lbl','htmlFor':'elmnts__Elements__el_pgb_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_pgb_1_li','className':'eic etw-60'}}}
  component.rowIndex = 0;
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
  expect(component.index).toEqual(component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(ProgressBarComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_pgb_1', progressBarType:'OSSPECIFIC', tdClasses:' pri', cntrType:'FORM', ProgressWrapper:{'direct':{'className':'ecn etw-30','style':{}}}, divContent:{'direct':{'className':'ett-prgs pri','style':{},'original-title':'', 'value':'0.80'}},  apzcontrol:'', content:{'direct':{'className':'determinate', 'widthvaltext':'80.0%'}}, contentstyle:{'style':{'width':'80.0%'}}, ProgressHintRequired:'N' , widgettype:'PROGRESSBAR', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'HTML', 'direct':{'id':'elmnts__Elements__el_pgb_1_grp_lbl','htmlFor':'elmnts__Elements__el_pgb_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_pgb_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});


it('isUiElm() should return true when bullet is an UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_pgb_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(ProgressBarComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_pgb_1', progressBarType:'GENERIC', tdClasses:' pri', cntrType:'FORM', ProgressWrapper:{'direct':{'className':'ecn etw-30','style':{}}}, divContent:{'direct':{'className':'ett-prgs pri','style':{},'original-title':'', 'value':'0.80'}},  apzcontrol:'', content:{'direct':{'className':'determinate', 'value':'0.80','widthvaltext':'80.0%'}}, contentstyle:{'style':{'width':'80.0%'}}, ProgressHintRequired:'N' , widgettype:'PROGRESSBAR', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'HTML', 'direct':{'id':'elmnts__Elements__el_pgb_1_grp_lbl','htmlFor':'elmnts__Elements__el_pgb_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_pgb_1_li','className':'eic etw-60'}}}
  expect(component.isUiElm()).toBeTrue();
});

it('isUiElm() should return false when bullet is bind to an interface', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_pgb_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(ProgressBarComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_pgb_1', progressBarType:'GENERIC', tdClasses:' pri', cntrType:'FORM', ProgressWrapper:{'direct':{'className':'ecn etw-30','style':{}}}, divContent:{'direct':{'className':'ett-prgs pri','style':{},'original-title':'', 'value':'0.80'}},  apzcontrol:'', content:{'direct':{'className':'determinate', 'value':'0.80','widthvaltext':'80.0%'}}, contentstyle:{'style':{'width':'80.0%'}}, ProgressHintRequired:'N' , widgettype:'PROGRESSBAR', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'HTML', 'direct':{'id':'elmnts__Elements__el_pgb_1_grp_lbl','htmlFor':'elmnts__Elements__el_pgb_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_pgb_1_li','className':'eic etw-60'}}}
  expect(component.isUiElm()).toBeFalse();
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(ProgressBarComponent);
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
  expect(component.value).toEqual(component.props.content.direct.value);
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  component.row = 1;
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "Y"}}}});
  component.setContent();
  expect(component.value).toEqual(component.props.content.direct.value);
});

it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_pgb_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(ProgressBarComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'elmnts__Elements__el_pgb_1', progressBarType:'GENERIC', tdClasses:' pri', cntrType:'FORM', ProgressWrapper:{'direct':{'className':'ecn etw-30','style':{}}}, divContent:{'direct':{'className':'ett-prgs pri','style':{},'original-title':'', 'value':'0.80'}},  apzcontrol:'', content:{'direct':{'className':'determinate', 'value':'0.80','widthvaltext':'80.0%'}}, contentstyle:{'style':{'width':'80.0%'}}, ProgressHintRequired:'N' , widgettype:'PROGRESSBAR', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'HTML', 'direct':{'id':'elmnts__Elements__el_pgb_1_grp_lbl','htmlFor':'elmnts__Elements__el_pgb_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_pgb_1_li','className':'eic etw-60'}}}
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_pgb_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle methods for control type NAVBAR', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_pgb_1': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(ProgressBarComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__el_pgb_1', progressBarType:'OSSPECIFIC', tdClasses:' pri', cntrType:'FORM', ProgressWrapper:{'direct':{'className':'ecn etw-30','style':{}}}, divContent:{'direct':{'className':'ett-prgs pri','style':{},'original-title':'', 'value':'0.80'}},  apzcontrol:'', content:{'direct':{'className':'determinate','widthvaltext':'80.0%'}}, contentstyle:{'style':{'width':'80.0%'}}, ProgressHintRequired:'N' , widgettype:'PROGRESSBAR', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'HTML', 'direct':{'id':'elmnts__Elements__el_pgb_1_grp_lbl','htmlFor':'elmnts__Elements__el_pgb_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_pgb_1_li','className':'eic etw-60'}}}
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_pgb_1': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
  expect(component.index).toEqual(component.rowIndex);
});
});