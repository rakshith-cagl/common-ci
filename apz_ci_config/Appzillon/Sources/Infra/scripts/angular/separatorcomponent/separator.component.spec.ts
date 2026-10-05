import { TestBed } from '@angular/core/testing';
import { SeparatorComponent  } from './separator.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('SeparatorComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      SeparatorComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the SeparatorComponent', () => {
   const fixture = TestBed.createComponent(SeparatorComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(SeparatorComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_sep_1', tdClasses:' pri', cntrType:'FORM', ProgressWrapper:{'direct':{'className':'ecn etw-30','style':{}}}, divContent:{'direct':{'className':'ett-prgs pri','style':{},'original-title':'', 'value':'0.80'}},  apzcontrol:'', content:{'direct':{'className':'determinate', 'value':'0.80','widthvaltext':'80.0%'}}, contentstyle:{'style':{'width':'80.0%'}}, ProgressHintRequired:'N' , widgettype:'PROGRESSBAR', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'HTML', 'direct':{'id':'elmnts__Elements__el_pgb_1_grp_lbl','htmlFor':'elmnts__Elements__el_pgb_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_pgb_1_li','className':'eic etw-60'}}}
  component.rowIndex = 0;
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(SeparatorComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_sep_1', tdClasses:' pri', cntrType:'FORM', ProgressWrapper:{'direct':{'className':'ecn etw-30','style':{}}}, divContent:{'direct':{'className':'ett-prgs pri','style':{},'original-title':'', 'value':'0.80'}},  apzcontrol:'', content:{'direct':{'className':'determinate', 'widthvaltext':'80.0%'}}, contentstyle:{'style':{'width':'80.0%'}}, ProgressHintRequired:'N' , widgettype:'PROGRESSBAR', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'HTML', 'direct':{'id':'elmnts__Elements__el_pgb_1_grp_lbl','htmlFor':'elmnts__Elements__el_pgb_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_pgb_1_li','className':'eic etw-60'}}}
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(SeparatorComponent);
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
  setApz({isNull: function(param:any) {return false},'scrMetaData': {'elmsMap': {'elmnts__Elements__el_sep_1': {'ui': "N", container: "appId_lst_1"}}}});
  const fixture = TestBed.createComponent(SeparatorComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.rowIndex = 0;
  component.elmData = component.apz['scrMetaData']['elmsMap'].elmnts__Elements__el_sep_1;
  component.containerId = "appId_lst_1";
  component.props = {id:'elmnts__Elements__el_sep_1',attrs: {}, tdClasses:' pri', cntrType:'FORM', ProgressWrapper:{'direct':{'className':'ecn etw-30','style':{}}}, divContent:{'direct':{'className':'ett-prgs pri','style':{},'original-title':'', 'value':'0.80'}},  apzcontrol:'', content:{'direct':{'className':'determinate', 'value':'0.80','widthvaltext':'80.0%'}}, contentstyle:{'style':{'width':'80.0%'}}, ProgressHintRequired:'N' , widgettype:'PROGRESSBAR', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'HTML', 'direct':{'id':'elmnts__Elements__el_pgb_1_grp_lbl','htmlFor':'elmnts__Elements__el_pgb_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_pgb_1_li','className':'eic etw-60'}}}
  mockDataService.getContent.withArgs(component.props.id, component.row, 0, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should invoke life cycle methods for control type LIST', () => {
  setApz({isNull: function(param:any) {return true}, 'scrMetaData': {'elmsMap': {'elmnts__Elements__el_sep_1': {'ui': "N", container: "SonarA__NewScreen__ct_tbl_1"}}}});
  const fixture = TestBed.createComponent(SeparatorComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.row = 0;
  component.containerId = "SonarA__NewScreen__ct_tbl_1";
  component.elmData = component.apz['scrMetaData']['elmsMap'].elmnts__Elements__el_sep_1;
  component.props = {id:'elmnts__Elements__el_sep_1',attrs: {options: 'N',cssclasses: 'pri', tooltip: 'Y', orientation: 'Y', appearance: 'Y', stepitems: {}}, tdClasses:' pri', cntrType:'LIST', ProgressWrapper:{'direct':{'className':'ecn etw-30','style':{}}}, divContent:{'direct':{'className':'ett-prgs pri','style':{},'original-title':'', 'value':'0.80'}},  apzcontrol:'', content:{'direct':{'className':'determinate','widthvaltext':'80.0%'}}, contentstyle:{'style':{'width':'80.0%'}}, ProgressHintRequired:'N' , widgettype:'PROGRESSBAR', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'HTML', 'direct':{'id':'elmnts__Elements__el_pgb_1_grp_lbl','htmlFor':'elmnts__Elements__el_pgb_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_pgb_1_li','className':'eic etw-60'}}}
  mockDataService.getContent.withArgs(component.props.id, 0, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});
});