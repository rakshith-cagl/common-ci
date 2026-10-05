import { TestBed } from '@angular/core/testing';
import { CheckboxComponent } from './check-box.component';
import { WindowRef, setApz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from '../data_angular';
describe('CheckboxComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
  beforeEach(async () => {
    const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent']);
    await TestBed.configureTestingModule({
      imports: [
      ],
      declarations: [
        CheckboxComponent
      ],
      providers: [WindowRef, {
        provide: DataService,
        useValue: spy
     }]
    }).compileComponents();
    mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
  });

  it('should create the CheckboxComponent', () => {
    const fixture = TestBed.createComponent(CheckboxComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('isUiElm() should return true when checkbox is an UI element', () => {
    setApz({ 'scrMetaData': { 'elmsMap': { 'elmnts__Elements__el_cbx_1': { 'ui': "Y" } } } });
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.props = { 'id': 'elmnts__Elements__el_cbx_1' };
    expect(component.isUiElm()).toBeTrue();
  });


  it('isUiElm() should return false when checkbox is bind to an interface', () => {
    setApz({ 'scrMetaData': { 'elmsMap': { 'elmnts__Elements__el_cbx_1': { 'ui': "N" } } } });
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.props = { 'id': 'elmnts__Elements__el_cbx_1' };
    expect(component.isUiElm()).toBeFalse();
  });

  it('should invoke ngOnChanges()', () => {
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.props = {id:'elmnts__Elements__el_cbx_1', tdClasses:' pri', apzcontrol:'', title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
    component.rowIndex = 0;
    component.ngOnChanges();
    expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
    expect(component.index).toEqual(component.rowIndex);
  });
  
  it('ngOnChanges() should assign props id if row index is not defined ', () => {
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.props = {id:'elmnts__Elements__el_cbx_1', tdClasses:' pri', apzcontrol:'', title: 'Test', value:'ID', heading:'h3', cntrType:'FORM', content:{'direct':{'className':'ett-hed3 pri fs24','original-title':''}}, hasSymbol:'N', hasIcon:'N', widgettype:'TEXT', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Text', 'direct':{'id':'angRef__Button__el_txt_14_grp_lbl','htmlFor':'angRef__Button__el_txt_14','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'Y', contentWrapper:{'direct':{'id':'angRef__Button__el_txt_14_li','className':'eic etw-100'}}};
    component.ngOnChanges();
    expect(component.id).toEqual(component.props.id);
  });

  it('saveValue() should set value to val variable based on if current element is UI element or bind to an interface', () => {
    setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_cbx_1': {'ui': "Y"}}}});
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.props = {id:'elmnts__Elements__el_cbx_1', tdClasses:' pri', apzcontrol:'', cntrType:'FORM', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}}
    component.saveValue(component.props, {target: {checked: true}});
    expect(component.value).toEqual('y');
    component.saveValue(component.props, {target: {indeterminate: true}});
    expect(component.value).toEqual('i');
    component.saveValue(component.props, {target: {unchecked: true}});
    expect(component.value).toEqual('n');  
    setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
    component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'FORM', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}}
    component.elm = 'InterfaceQuery,i,tbAsmiIntfMaster';
    component.values = [];
    component.saveValue(component.props, {target: {checked: true}});
    expect(component.values[component.elm]).toEqual('y');
    component.saveValue(component.props, {target: {indeterminate: true}});
    expect(component.values[component.elm]).toEqual('i');
    component.saveValue(component.props, {target: {unchecked: true}});
    expect(component.values[component.elm]).toEqual('n');
  });

  it('should invoke life cycle methods', () => {
    setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_cbx_1': {'ui': "Y"}}}});
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.row = 0;
    component.props = {id:'elmnts__Elements__el_cbx_1', tdClasses:' pri', apzcontrol:'', cntrType:'FORM', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
    fixture.detectChanges();
    expect(component.checkBoxWrapperRef.nativeElement).toBeDefined();
  });

  it('should cover life cycle methods alternate flow for different control type', () => {
    setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_cbx_1': {'ui': "Y"}}}});
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.rowIndex = 0;
    component.props = {id:'elmnts__Elements__el_cbx_1', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
    fixture.detectChanges();
    expect(component.navSapnwrapperRef.nativeElement).toBeDefined();
    expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
    expect(component.index).toEqual(component.rowIndex);
  });

  it('should emit iconClick event', () => {
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    spyOn(component.iconClick, 'emit');
    component.iconClickEvent({});
    expect(component.iconClick.emit).toHaveBeenCalled();
  });

  it('should set state variable to be true based on the checkbox checked in setContent()', () => {
    setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_cbx_1': {'ui': "Y"}}}});
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.state = {};
    component.value = 'y'
    component.props = {id:'elmnts__Elements__el_cbx_1', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
    component.setContent();
    expect(component.state.checked).toEqual(true);
    component.value = 'i';
    component.setContent();
    expect(component.state.indeterminate).toEqual(true);
  });

  it('should set state variable to be true based on the checkbox checked in setContent() when row is defined', () => {
    setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_cbx_1': {'ui': "Y"}}}});
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.state = {};
    component.value = 'y'
    component.props = {id:'elmnts__Elements__el_cbx_1', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
    component.row = 1;
    component.value = 'y';
    component.setContent();
    expect(component.state.checked).toEqual(true);
    component.value = 'i';
    component.setContent();
    expect(component.state.indeterminate).toEqual(true);
  });

  it('should set state variable to be true based on the checkbox checked in setContent() when row is defined and if its not a UI element', () => {
    setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.state = {};
    component.values = [];
    component.elm = 'appId';
    component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
    component.row = 1;
    component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
    component.containerId = "appId_lst_1";
    component.rowIndex = 1;
    mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': 'y'});
    component.setContent();
    expect(component.state.checked).toEqual(true);
    mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': 'i'});
    component.setContent();
    expect(component.state.checked).toEqual(false);
    expect(component.state.indeterminate).toEqual(true);
    mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'appId': 'n'});
    component.setContent();
    expect(component.state.checked).toEqual(false);
  });

  it('should set state variable to be true based on the checkbox checked in setContent() when row is not defined and if its not a UI element', () => {
    setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
    const fixture = TestBed.createComponent(CheckboxComponent);
    const component = fixture.componentInstance;
    component.state = {};
    component.values = [];
    component.elm = 'appId';
    component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
    component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
    component.containerId = "appId_lst_1";
    component.rowIndex = 1;
    mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'appId': 'y'});
    component.setContent();
    expect(component.state.checked).toEqual(true);
    mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'appId': 'i'});
    component.setContent();
    expect(component.state.checked).toEqual(false);
    expect(component.state.indeterminate).toEqual(true);
    mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'appId': 'n'});
    component.setContent();
    expect(component.state.checked).toEqual(false);
  });
});