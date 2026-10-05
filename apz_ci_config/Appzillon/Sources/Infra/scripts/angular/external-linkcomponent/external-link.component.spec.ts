import { TestBed } from '@angular/core/testing';
import { ExternalLinkComponent  } from './external-link.component';
import { WindowRef, setApz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from '../data_angular';
describe('ExternalLinkComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        ExternalLinkComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the ExternalLinkComponent', () => {
   const fixture = TestBed.createComponent(ExternalLinkComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(ExternalLinkComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  component.row = 1;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});

it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_extl_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(ExternalLinkComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'elmnts__Elements__el_extl_1', attrs: {'defaultvalue': 'DESC 1', cssclasses: 'pri', contentalignment: 'LEFT', state: 'DISABLED', tooltip: 'Helper tip', options: 'N', appearance: 'apr-cls'}, type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'FORM', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'Choose Expertise', 'direct':{'id':'elmnts__Elements__el_dpd_1_grp_lbl','htmlFor':'elmnts__Elements__el_dpd_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_dpd_1_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.snoCls).toEqual(" sno");
});

it('should cover alternate flows of life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_extl_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(ExternalLinkComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__el_extl_1', attrs: {'defaultvalue': 'DESC 1', contentalignment: 'CENTER', tooltip: 'Helper tip', options: 'Y'}, type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'FORM', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb wrapped'}, labelProps:{'title':'Choose Expertise', 'direct':{'id':'elmnts__Elements__el_dpd_1_grp_lbl','htmlFor':'elmnts__Elements__el_dpd_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_dpd_1_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
  expect(component.extLinkFormlabelWrapperRef.nativeElement).toBeDefined();
});

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(ExternalLinkComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_extl_1', tdClasses:' pri', apzcontrol:'', title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.rowIndex = 0;
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(ExternalLinkComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_extl_1', tdClasses:' pri', apzcontrol:'', title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});
});