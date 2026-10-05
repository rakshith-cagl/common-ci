import { TestBed } from '@angular/core/testing';
import { FormComponent  } from './formcomponent';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('FormComponent', () => {
  let mockWindowReference: jasmine.SpyObj<WindowRef>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('WindowRef', ['nativeWindow']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        FormComponent
     ],
     providers: [{
      provide: WindowRef,
      useValue: spy
   }, DataService]
   }).compileComponents();
   mockWindowReference = TestBed.inject(WindowRef) as jasmine.SpyObj<WindowRef>;
 });

 it('should create the FooterComponent', () => {
   const fixture = TestBed.createComponent(FormComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

//  it('should invoke life cycle methods', () => {
// //  setWindow({'scrMetaData': {'elmsMap': {'elmnts__Elements__ct_frm_22': {'ui': "Y"}}}});
//   setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__ct_frm_22': {'ui': "Y"}}}});
//   const fixture = TestBed.createComponent(FormComponent);
//   const component = fixture.componentInstance;
//   component.apz = {'scrMetaData': {'elmsMap': {'elmnts__Elements__ct_frm_22': {'ui': "Y"}}}};
//  // mockWindowReference.nativeWindow.and.returnValue({apz: {'scrMetaData': {'elmsMap': {'elmnts__Elements__ct_frm_22': {'ui': "Y"}}}}});
//   component.props = {id:'elmnts__Elements__ct_frm_22', className:'crt-form  hor pri', widgettype:'FORM', widgetcategory:'CONTAINER'}
//   fixture.detectChanges();
//   expect(component.metadata).toEqual(component.apz.scrMetaData.containersMap[component.props.id]);
// });
});