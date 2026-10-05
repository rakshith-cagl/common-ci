import { TestBed } from '@angular/core/testing';
import { BreadCrumbComponent } from './breadcrumb.component';
describe('BreadCrumbComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        BreadCrumbComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the BreadCrumbComponent', () => {
   const fixture = TestBed.createComponent(BreadCrumbComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });


it('should call life cycle methods', () => {
  const fixture = TestBed.createComponent(BreadCrumbComponent);
  const component = fixture.componentInstance;
  component.props = {'id': 'test_bread_crumb_1', content:{'direct':{}}};
  fixture.detectChanges();
  expect(component.contentRef.nativeElement).toBeDefined();
});
});