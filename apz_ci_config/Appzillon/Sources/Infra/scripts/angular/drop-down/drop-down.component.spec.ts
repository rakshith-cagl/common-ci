import { TestBed } from '@angular/core/testing';
import { DropdownComponent  } from './drop-down.component';
import { WindowRef, setApz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from '../data_angular';
describe('DropdownComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        DropdownComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the DropdownComponent', () => {
   const fixture = TestBed.createComponent(DropdownComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('should emit labelIconClick event', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  spyOn(component.labelIconClick, 'emit');
  component.iconClickEvent({});
  expect(component.labelIconClick.emit).toHaveBeenCalled();
 });

 it('should emit click event', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  spyOn(component.click, 'emit');
  component.autoCompleteClicked();
  component.multiSelectTagClicked();
  expect(component.click.emit).toHaveBeenCalledTimes(2);
 });

 it('should emit change event', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  spyOn(component.change, 'emit');
  component.autoCompleteValueChanged({});
  component.multiSelectTagValueChanged({});
  expect(component.change.emit).toHaveBeenCalledTimes(2);
 });

 it('isUiElm() should return true when input is an UI element', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Dropdown__el_dpd_0', type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'FORM', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  expect(component.isUiElm()).toBeTrue();
});

it('isUiElm() should return false when input is bind to an interface', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Dropdown__el_dpd_0', type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'FORM', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  expect(component.isUiElm()).toBeFalse();
});

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Dropdown__el_dpd_0', type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'FORM', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  component.rowIndex = 0;
  component.ngOnChanges({});
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
  expect(component.index).toEqual(component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Dropdown__el_dpd_0', type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'FORM', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  component.ngOnChanges({});
  expect(component.id).toEqual(component.props.id);
});


it('isMultiRec() should return true when element has multi record else false', () => {
  setApz({'scrMetaData': {nodesMap: {'node1': {multiRec: 'Y'}}, containersMap: {'elmnts__Elements__ct_frm_10': {multiRec: 'Y'}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "N", container: 'elmnts__Elements__ct_frm_10', nodeId: 'node1'}}}});
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__Dropdown__el_dpd_0', type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'FORM', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  expect(component.isMultiRec()).toBeTrue();
  setApz({'scrMetaData': {nodesMap: {'node1': {multiRec: 'N'}}, containersMap: {'elmnts__Elements__ct_frm_10': {multiRec: 'Y'}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "N", container: 'elmnts__Elements__ct_frm_10', nodeId: 'node1'}}}});
  expect(component.isMultiRec()).toBeFalse();
});

it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "Y"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.row = 1;
  component.props = {id:'angRef__Dropdown__el_dpd_0', type:'SIMPLE', tdClasses:' pri', value:'', cntrType:'FORM', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should invoke life cycle method alternate flows', () => {
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "Y"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10', staticOptions: [{desc: 'One', val: 1}]}}}});
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 1;
  component.props = {id:'angRef__Dropdown__el_dpd_0',type:'MULTISELECTTAGS', tdClasses:' pri', value:'', cntrType:'LIST', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id + "_" + component.rowIndex);
});

it('should cover life cycle method flows for container type TABLE', () => {
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "Y"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.row = 1;
  component.props = {id:'angRef__Dropdown__el_dpd_0', type:'AUTOCOMPLETE', tdClasses:' pri', value:'', cntrType:'TABLE', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.dpdwnTableSpanWrapperRef.nativeElement).toBeDefined();
});

it('should cover life cycle method flows for dropdown type MULTISELECTCHECKBOX', () => {
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "Y"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.row = 1;
  component.props = {id:'angRef__Dropdown__el_dpd_0', type:'MULTISELECTCHECKBOX', tdClasses:' pri', value:'', cntrType:'TABLE', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.multiSelectcheckboxDivWrapperRef.nativeElement).toBeDefined();
});

it('should cover life cycle method flows for dropdown type WITHSUBOPTIONS', () => {
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "Y"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.row = 1;
  component.props = {id:'angRef__Dropdown__el_dpd_0', type:'WITHSUBOPTIONS', tdClasses:' pri', value:'', cntrType:'TABLE', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.withSubOptDivWrapperRef.nativeElement).toBeDefined();
});

it('should cover life cycle method flows for dropdown type NATIVE', () => {
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "Y"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.row = 1;
  component.props = {id:'angRef__Dropdown__el_dpd_0', selectTag: {direct: {}}, type:'NATIVE', tdClasses:' pri', value:'', cntrType:'TABLE', spanWrapper:{'className':'ecn etw-50'}, divWrapper:{'direct':{'className':'etb-slct ett-slct  pri etw-50','original-title':'','style':{} }}, content:{'direct':{'className':'sub-elt',   'enabled':'enabled','original-title':'' ,'multiple':'','readOnly':'readOnly'}}, optionsDivWrapper:{'direct':{'className':'sub-ctr'}}, widgettype:'DROPDOWN', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'ID - el_dpd_0', 'direct':{'id':'angRef__Dropdown__el_dpd_0_grp_lbl','htmlFor':'angRef__Dropdown__el_dpd_0','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Dropdown__el_dpd_0_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.selectNativeRef.nativeElement).toBeDefined();
});

it('should return matched value from options on invoking setDrdDscValue()', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.staticOptions = [{desc: 'One', val : 1}, {desc: 'Two', val: 2}];
  expect(component.setDrdDscValue(1)).toEqual({ desc: 'One', val: 1 });
});

it('should set multi select dropdown value on invoking setMultiDrdValValue()', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}];
  expect(component.setMultiDrdValValue("One, Two")).toEqual("1, 2");
});

it('should set multi select dropdown description value on invoking setMultiDrdDscValue()', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}];
  expect(component.setMultiDrdDscValue("1, 2")).toEqual("One, Two");
});

it('should change multi select dropdown value on invoking changeMultiDrdValValue()', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}, {desc: 'Three', val : "3"}];
  expect(component.changeMultiDrdValValue("One, Two, Three", "Three")).toEqual("1, 2");
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}, {desc: 'Three', val : "3"}];
  expect(component.changeMultiDrdValValue("One, Two", "Three")).toEqual("1, 2, 3");
});


it('should check if passed values exists on invoking checkedIputs()', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "N"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "N", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  component.elm = "el_dpd_0";
  component.props = {id: 'angRef__Dropdown__el_dpd_0'};
  component.values = {};
  expect(component.checkedIputs("1")).toBeFalse();
  component.values = {'el_dpd_0': "1, 2"};
  expect(component.checkedIputs("1")).toBeTrue();
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "Y"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  expect(component.checkedIputs("1")).toBeFalse();
  component.props = {id: 'angRef__Dropdown__el_dpd_0', value: '1, 2'};
  expect(component.checkedIputs("1")).toBeTrue();
});

it('should set values on change of multi select option onMultiSelectOption()', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "N"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "N", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  component.elm = "el_dpd_0";
  component.id = "angRef__Dropdown__el_dpd_0";
  component.multiChBxVal = "1, 2";
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "MULTISELECTCHECKBOX"};
  component.values = {};
  spyOn(component.change, 'emit');
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}, {desc: 'Three', val : "3"}];
  component.onMultiSelectOption({value: 'Three'}, {}, "")
  expect(component.change.emit).toHaveBeenCalledTimes(1);
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "N"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  component.onMultiSelectOption({value: 'Three'}, {}, "")
  expect(component.change.emit).toHaveBeenCalledTimes(1);
});

it('should set values on change of multi select option onSelectOption()', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "N"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "N", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  component.elm = "el_dpd_0";
  component.id = "angRef__Dropdown__el_dpd_0";
  component.selectedValue = {desc: 'One', val: '1'};
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "SIMPLE"};
  component.values = {};
  spyOn(component.change, 'emit');
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}, {desc: 'Three', val : "3"}];
  component.onSelectOption({getAttribute: function(param: any) {return '3'}}, {}, "")
  expect(component.change.emit).toHaveBeenCalledTimes(1);
  expect(component.selectedValue).toEqual({desc: 'Three', val : "3"})
  component.onSelectOption({getAttribute: function(param: any) {return '4'}}, {}, "")
  expect(component.selectedValue).toEqual({desc: '', val : ''})
});

it('should set values on change of multi select option onSelectOption() for type sub options', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "N"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "N", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  component.elm = "el_dpd_0";
  component.id = "angRef__Dropdown__el_dpd_0";
  component.selectedValue = {desc: 'One', val: '1'};
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "WITHSUBOPTIONS"};
  component.values = {};
  spyOn(component.change, 'emit');
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}, {desc: 'Three', val : "3"}];
  component.onSelectOption({getAttribute: function(param: any) {return '2'}}, {}, "")
  expect(component.change.emit).toHaveBeenCalledTimes(1);
  expect(component.selectedValue).toEqual({desc: 'Two', val : "2"})
  component.onSelectOption({getAttribute: function(param: any) {return '4'}}, {}, "")
  expect(component.selectedValue).toEqual({desc: '', val : ''})
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "AUTOCOMPLETE"};
  component.selectedValue = {desc: 'One', val: '1'};
  component.onSelectOption({getAttribute: function(param: any) {return '2'}}, {}, "")
  expect(component.values["el_dpd_0"]).toEqual("2");
});

it('should set values on change of multi select option onSelectOption() when it is a UI element', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "N"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "SIMPLE"};
  component.selectedValue = {desc: 'One', val: '1'};
  spyOn(component.change, 'emit');
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}, {desc: 'Three', val : "3"}];
  component.onSelectOption({getAttribute: function(param: any) {return '3'}}, {}, "")
  expect(component.change.emit).toHaveBeenCalledTimes(1);
  expect(component.selectedValue).toEqual({desc: 'Three', val : "3"})
  component.onSelectOption({getAttribute: function(param: any) {return '4'}}, {}, "")
  expect(component.selectedValue).toEqual({desc: '', val : ''})
  //Type SUB OPTIONS
  component.id = "angRef__Dropdown__el_dpd_0";
  component.selectedValue = {desc: 'One', val: '1'};
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "WITHSUBOPTIONS"};
  component.onSelectOption({getAttribute: function(param: any) {return '2'}}, {}, "")
  expect(component.selectedValue).toEqual({desc: '', val : ''})
  component.onSelectOption({getAttribute: function(param: any) {return '4'}}, {}, "")
  expect(component.selectedValue).toEqual({desc: '', val : ''})
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "AUTOCOMPLETE", value: ''};
  component.selectedValue = {desc: 'One', val: '1'};
  component.onSelectOption({getAttribute: function(param: any) {return '2'}}, {}, "")
  expect(component.props.value).toEqual("2");
});

it('should update selectedValue on invoking setContent()', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "N"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "N", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "SIMPLE"};
  component.containerId = "elmnts__Elements__ct_frm_10";
  component.row = 1;
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}, {desc: 'Three', val : "3"}];
  component.elm = component.props.id.split("__").pop();
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'el_dpd_0': '2'});
  component.setContent();
  expect(component.selectedValue).toEqual({desc: 'Two', val: '2'});
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'el_dpd_0': '4'});
  component.setContent();
  expect(component.selectedValue).toEqual({desc: '', val: ''});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "MULTISELECTCHECKBOX"};
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'el_dpd_0': '2'});
  component.setContent();
  expect(component.multiChBxVal).toEqual("Two");
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({});
  component.setContent();
  expect(component.multiChBxVal).toEqual(''); 
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "WITHSUBOPTIONS"};
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'el_dpd_0': '2'});
  component.setContent();
  expect(component.selectedValue).toEqual({desc: 'Two', val: '2'});
  mockDataService.getMultiRecContent.withArgs(component.elmData, component.containerId, component.rowIndex).and.returnValue({'el_dpd_0': '4'});
  component.setContent();
  expect(component.selectedValue).toEqual({desc: '', val: ''});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "MULTISELECTTAGS"};
  component.setContent(); 
  expect(component.options.multiple).toBeTrue();
  expect(component.options.tags).toBeTrue();
});

it('should update selectedValue on invoking setContent() when row is defined and is a UI element', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "N"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "SIMPLE", value: '2'};
  component.row = 1;
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}, {desc: 'Three', val : "3"}];
  component.setContent();
  expect(component.selectedValue).toEqual({desc: 'Two', val: '2'});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "SIMPLE", value: ''};
  component.setContent();
  expect(component.selectedValue).toEqual({desc: '', val: ''});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "MULTISELECTCHECKBOX", value : '2'};
  component.setContent();
  expect(component.multiChBxVal).toEqual("Two");
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "MULTISELECTCHECKBOX", value : ''};
  component.setContent();
  expect(component.multiChBxVal).toEqual(''); 
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "WITHSUBOPTIONS", value: '2'};
  component.setContent();
  expect(component.selectedValue).toEqual({desc: 'Two', val: '2'});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "WITHSUBOPTIONS", value: '4'};
  component.setContent();
  expect(component.selectedValue).toEqual({desc: '', val: ''});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "MULTISELECTTAGS"};
  component.setContent(); 
  expect(component.options.multiple).toBeTrue();
  expect(component.options.tags).toBeTrue();
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "AUTOCOMPLETE"};
  component.setContent();
  expect(component.autoCompleteOptions).toBeDefined();
});

it('should update selectedValue on invoking setContent() when row is not defined', () => {
  const fixture = TestBed.createComponent(DropdownComponent);
  const component = fixture.componentInstance;
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "N"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "N", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "SIMPLE"};
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}, {desc: 'Three', val : "3"}];
  component.elm = component.props.id.split("__").pop();
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_dpd_0': '2'});
  component.setContent();
  expect(component.selectedValue).toEqual({desc: 'Two', val: '2'});
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_dpd_0': ''});
  component.setContent();
  expect(component.selectedValue).toEqual({desc: '', val: ''});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "MULTISELECTCHECKBOX"};
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_dpd_0': '2'});
  component.setContent();
  expect(component.multiChBxVal).toEqual("Two");
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_dpd_0': ''});
  component.setContent();
  expect(component.multiChBxVal).toEqual(''); 
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "WITHSUBOPTIONS"};
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_dpd_0': '2'});
  component.setContent();
  expect(component.selectedValue).toEqual({desc: 'Two', val: '2'});
  mockDataService.setElmValue.withArgs(component.props.id).and.returnValue({'el_dpd_0': ''});
  component.setContent();
  expect(component.selectedValue).toEqual({desc: '', val: ''});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "MULTISELECTTAGS"};
  component.setContent(); 
  expect(component.options.multiple).toBeTrue();
  expect(component.options.tags).toBeTrue();
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "AUTOCOMPLETE"};
  component.setContent();
  expect(component.autoCompleteOptions).toBeDefined();
  //For a non UI element
  setApz({'scrMetaData': {containersMap: {elmnts__Elements__ct_frm_10: {multiRec: "N"}},'elmsMap': {'angRef__Dropdown__el_dpd_0': {'ui': "Y", 'container': 'elmnts__Elements__ct_frm_10'}}}});
  component.staticOptions = [{desc: 'One', val : "1"}, {desc: 'Two', val: "2"}, {desc: 'Three', val : "3"}];
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "SIMPLE", value: '2'};
  component.setContent();
  expect(component.selectedValue).toEqual({desc: 'Two', val: '2'});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "SIMPLE", value: ''};
  component.setContent();
  expect(component.selectedValue).toEqual({desc: '', val: ''});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "MULTISELECTCHECKBOX", value : '2'};
  component.setContent();
  expect(component.multiChBxVal).toEqual("Two");
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "MULTISELECTCHECKBOX", value : ''};
  component.setContent();
  expect(component.multiChBxVal).toEqual(''); 
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "WITHSUBOPTIONS", value: '2'};
  component.setContent();
  expect(component.selectedValue).toEqual({desc: 'Two', val: '2'});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "WITHSUBOPTIONS", value: '4'};
  component.setContent();
  expect(component.selectedValue).toEqual({desc: '', val: ''});
  component.props = {id: 'angRef__Dropdown__el_dpd_0', type: "AUTOCOMPLETE"};
  component.setContent();
  expect(component.autoCompleteOptions).toBeDefined();
});

});