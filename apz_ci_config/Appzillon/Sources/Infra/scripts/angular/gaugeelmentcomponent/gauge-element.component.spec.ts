import { TestBed } from '@angular/core/testing';
import { GuageElementComponent  } from './gauge-element.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('GuageElementComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'isUiElm']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        GuageElementComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the GuageElementComponent', () => {
   const fixture = TestBed.createComponent(GuageElementComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

it('should emit iconClick event', () => {
  const fixture = TestBed.createComponent(GuageElementComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.labelIconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
});

it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__DashboardDummy__o__GaugeSpeed__gaugeBulb': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(GuageElementComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'elmnts__DashboardDummy__o__GaugeSpeed__gaugeBulb', tdClasses:' pri',apzcontrol:'', cntrType:'FORM', direct:{'className':'','style':{'height':'PX;'}, 'original-title':''}, spanWrapper:{'className':'ecn ','style':{}}, osSpecific:'N', hasLov:'N', hasSymbol:'Y', hasIcon:'N', isdateOrDateTime:'N', elmWrapper :{'direct':{'className':''}}, buttonState :{}, elementIcon :'', lovIcon :'', widgettype:'GAUGE', widgetcategory:'ELEMENT', wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Element Gauge Thermometer', 'direct':{'id':'elmnts__DashboardDummy__o__GaugeSpeed__gaugeBulb_grp_lbl','htmlFor':'elmnts__DashboardDummy__o__GaugeSpeed__gaugeBulb','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__DashboardDummy__o__GaugeSpeed__gaugeBulb_li','className':'eic etw-60'}}}
  mockDataService.isUiElm.withArgs(component.props.id).and.returnValue(false);
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'gaugeBulb': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should cover alternate flows of life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__DashboardDummy__o__GaugeSpeed__gaugeBulb': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(GuageElementComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__DashboardDummy__o__GaugeSpeed__gaugeBulb', tdClasses:' pri',apzcontrol:'', cntrType:'FORM', direct:{'className':'','style':{'height':'PX;'}, 'original-title':''}, spanWrapper:{'className':'ecn ','style':{}}, osSpecific:'N', hasLov:'N', hasSymbol:'Y', hasIcon:'N', isdateOrDateTime:'N', elmWrapper :{'direct':{'className':''}}, buttonState :{}, elementIcon :'', lovIcon :'', widgettype:'GAUGE', widgetcategory:'ELEMENT', wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Element Gauge Thermometer', 'direct':{'id':'elmnts__DashboardDummy__o__GaugeSpeed__gaugeBulb_grp_lbl','htmlFor':'elmnts__DashboardDummy__o__GaugeSpeed__gaugeBulb','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__DashboardDummy__o__GaugeSpeed__gaugeBulb_li','className':'eic etw-60'}}}
  mockDataService.isUiElm.withArgs(component.props.id).and.returnValue(false);
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'gaugeBulb': 'y'});
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should set values, elm and gaugeValue variable values in setContent() when row is defined and if its not a UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(GuageElementComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.row = 1;
  component.elmData = {'ui': 'N'} //component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  mockDataService.isUiElm.withArgs(component.props.id).and.returnValue(false);
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});

it('should set values, elm and gaugeValue variable values setContent() when row is not defined and if its not a UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(GuageElementComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = {'ui': 'N'}; //component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  component.rowIndex = 1;
  mockDataService.isUiElm.withArgs(component.props.id).and.returnValue(false);
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});

it('should rerender the component when renderData() is invoked', () => {
  setApz({data: {reRenderComponent: function(val:any) {return true}},'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(GuageElementComponent);
  const component = fixture.componentInstance;
  component.elmData = {'ui': 'N'};
  component.forceUpdate = false;
  component.renderData();
  expect(component.rendered).toBeFalse();
});
});