import { TestBed } from '@angular/core/testing';
import { IconComponent  } from './icon.component';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
describe('IconComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        IconComponent
     ],
     providers: [{
      provide: DataService,
      useValue: spy
   }, WindowRef]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the IconComponent', () => {
   const fixture = TestBed.createComponent(IconComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_ic_1': {'ui': "Y", 'container': {}}}}});
  const fixture = TestBed.createComponent(IconComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props = {id:'elmnts__Elements__el_ic_1', IconContent: {direct: {'original-title': 'test', className: 'pri cen'}},tdClasses:' pri cen', cntrType:'NAVBAR', anchorTag:{'direct':{'className':'ett-hypl pri','original-title':'','href':'javascript:'}}, value:'Click here to redirect', iconPosition:'LEFT', hyperLinkIcon:{'name':'','direct':{'className':'icon  px18'}}, HintRequired:'N' , widgettype:'HYPERLINK', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':''}, labelProps:{'direct':{}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_hpl_1_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should cover alternate life cycle methods flows', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_ic_1': {'ui': "Y", 'container': {}}}}});
  const fixture = TestBed.createComponent(IconComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__el_ic_1', IconContent: {direct: {'original-title': 'test',className: 'pri cen'}}, tdClasses:' pri cen', cntrType:'FORM', anchorTag:{'direct':{'className':'ett-hypl pri','original-title':'','href':'javascript:'}}, value:'Click here to redirect', iconPosition:'LEFT', hyperLinkIcon:{'name':'','direct':{'className':'icon  px18'}}, HintRequired:'N' , widgettype:'HYPERLINK', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':''}, labelProps:{'direct':{}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_hpl_1_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
  expect(component.index).toEqual(component.rowIndex);
});

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(IconComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_ic_1', tdClasses:' pri cen', cntrType:'FORM', anchorTag:{'direct':{'className':'ett-hypl pri','original-title':'','href':'javascript:'}}, value:'Click here to redirect', iconPosition:'LEFT', hyperLinkIcon:{'name':'','direct':{'className':'icon  px18'}}, HintRequired:'N' , widgettype:'HYPERLINK', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':''}, labelProps:{'direct':{}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_hpl_1_li','className':'eic etw-60'}}};
  component.rowIndex = 0;
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
  expect(component.index).toEqual(component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(IconComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_ic_1', tdClasses:' pri cen', cntrType:'FORM', anchorTag:{'direct':{'className':'ett-hypl pri','original-title':'','href':'javascript:'}}, value:'Click here to redirect', iconPosition:'LEFT', hyperLinkIcon:{'name':'','direct':{'className':'icon  px18'}}, HintRequired:'N' , widgettype:'HYPERLINK', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':''}, labelProps:{'direct':{}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_hpl_1_li','className':'eic etw-60'}}};
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(IconComponent);
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
});