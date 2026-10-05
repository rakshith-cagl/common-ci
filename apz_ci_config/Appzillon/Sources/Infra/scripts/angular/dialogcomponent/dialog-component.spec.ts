import { TestBed } from '@angular/core/testing';
import { DialogComponent  } from './dialog.component';
describe('DialogComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        DialogComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the DialogComponent', () => {
   const fixture = TestBed.createComponent(DialogComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('should invoke life cycle methods', () => {
  const fixture = TestBed.createComponent(DialogComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__el_dlg_1', tdClasses:' pri', apzcontrol:'', cntrType:'FORM', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , dialogWrapper:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  fixture.detectChanges();
  expect(component.dialogWrapperRef.nativeElement).toBeDefined();
});
});