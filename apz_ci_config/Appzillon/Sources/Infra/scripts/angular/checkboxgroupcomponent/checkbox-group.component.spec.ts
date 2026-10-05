import { TestBed } from '@angular/core/testing';
import { CheckboxGroupComponent  } from './checkbox-group.component';
import { WindowRef, setApz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from '../data_angular';
describe('CheckboxGroupComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        CheckboxGroupComponent
     ],
     providers: [WindowRef,DataService]
   }).compileComponents();
 });

 it('should create the CheckboxGroupComponent', () => {
    const fixture = TestBed.createComponent(CheckboxGroupComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
 });

 it('should emit iconClick event', () => {
  const fixture = TestBed.createComponent(CheckboxGroupComponent);
  const component = fixture.componentInstance;
  spyOn(component.iconClick, 'emit');
  component.iconClickEvent({});
  expect(component.iconClick.emit).toHaveBeenCalled();
});

it('should invoke life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_cbx_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(CheckboxGroupComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_cbx_1', index: 0, value: 'yes', tdClasses:' pri', apzcontrol:'', cntrType:'FORM', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  fixture.detectChanges();
  expect(component.formlabelWrapperRef.nativeElement).toBeDefined();
  expect(component.value).toEqual(component.props.value);
  expect(component.id).toEqual(component.props.id + "_" + component.props.index);
});

it('should cover alternate flows of life cycle methods', () => {
  setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_cbx_1': {'ui': "Y"}}}});
  const fixture = TestBed.createComponent(CheckboxGroupComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_cbx_1', value: 'yes', tdClasses:' pri', apzcontrol:'', cntrType:'FORM', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  fixture.detectChanges();
  expect(component.id).toEqual(component.props.id);
});
});