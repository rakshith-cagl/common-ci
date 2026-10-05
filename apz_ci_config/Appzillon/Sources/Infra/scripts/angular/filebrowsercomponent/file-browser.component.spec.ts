import { TestBed } from '@angular/core/testing';
import { FileBrowserComponent  } from './file-browser.component';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('FileBrowserComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        FileBrowserComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the FileBrowserComponent', () => {
   const fixture = TestBed.createComponent(FileBrowserComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(FileBrowserComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_fil_1', tdClasses:' pri', apzcontrol:'', title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.rowIndex = 0;
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(FileBrowserComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_fil_1', tdClasses:' pri', apzcontrol:'', title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should emit preBreadcrumbAction', () => {
  const fixture = TestBed.createComponent(FileBrowserComponent);
  const component = fixture.componentInstance;
  spyOn(component.buttonClick, 'emit');
  component.buttonClickEvent({});
  expect(component.buttonClick.emit).toHaveBeenCalled();
});

it('should emit postBreadcrumbAction', () => {
  const fixture = TestBed.createComponent(FileBrowserComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.labelIconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
});

it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_fil_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(FileBrowserComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_fil_1', tdClasses:' pri', apzcontrol:'', cntrType:'FORM', fileButtonWrapper:{'direct':{'className':'nrb ett-file  pri'}}, fileButton:{'direct':{'className':'filebox ett-bttn med  pri','href':'javascript:;'}}, BrowseButton:{'direct':{'className':'','style':{},'defaultValue':'','required':'', 'multiple':'multiple'}}, UploadButtonRequired:'Y', UploadButton:{'direct':{'className':'ett-bttn icl med inf  pri'}}, iconContent:{'direct':{'name':'','className':'icon  px24'}}, widgettype:'FILEBROWSER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Upload File', 'direct':{'id':'elmnts__Elements__el_fil_1_grp_lbl','htmlFor':'elmnts__Elements__el_fil_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_fil_1_li','className':'eic etw-60'}}}
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should cover alternate flows of life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_fil_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(FileBrowserComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props = {id:'elmnts__Elements__el_fil_1', tdClasses:' pri', apzcontrol:'', cntrType:'FORM', fileButtonWrapper:{'direct':{'className':'nrb ett-file  pri'}}, fileButton:{'direct':{'className':'filebox ett-bttn med  pri','href':'javascript:;'}}, BrowseButton:{'direct':{'className':'','style':{},'defaultValue':'','required':'', 'multiple':'multiple'}}, UploadButtonRequired:'Y', UploadButton:{'direct':{'className':'ett-bttn icl med inf  pri'}}, iconContent:{'direct':{'name':'','className':'icon  px24'}}, widgettype:'FILEBROWSER', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Upload File', 'direct':{'id':'elmnts__Elements__el_fil_1_grp_lbl','htmlFor':'elmnts__Elements__el_fil_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_fil_1_li','className':'eic etw-60'}}}
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
  expect(component.fileBrowserWrapperClassesRef.nativeElement).toBeDefined();
});

it('should set state variable to be true based on the checkbox checked in setContent() when row is defined and if its not a UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(FileBrowserComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.row = 1;
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.values).toBeDefined();
});

it('should set state variable to be true based on the checkbox checked in setContent() when row is not defined and if its not a UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(FileBrowserComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  component.rowIndex = 1;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.values).toBeDefined();
});
});