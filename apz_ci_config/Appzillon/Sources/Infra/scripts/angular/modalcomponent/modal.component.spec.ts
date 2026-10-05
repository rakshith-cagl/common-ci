import { TestBed } from '@angular/core/testing';
import { ModalComponent  } from './modal.component';
describe('ModalComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        ModalComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the ModalComponent', () => {
   const fixture = TestBed.createComponent(ModalComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('should cover life cycle methods', () => {
  const fixture = TestBed.createComponent(ModalComponent);
  const component = fixture.componentInstance;
  component.props = {id:'Explor__Landing__ct_modal_2' , widgettype:'MODAL', modalDivWrapper:{'direct':{'className':'crt-hmnu icp','style':{}}}, parentDivWrapper:{'direct':{'className':'ett-ctmu etb-ctmu pri '}}, iconWrapper:{'name':'icon-burger','className':'px24 icon-burger'}};
  fixture.detectChanges();
  expect(component.parentDivWrapperRef).toBeDefined();
});
});