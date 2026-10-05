import { TestBed } from '@angular/core/testing';
import { GridColComponent  } from './gridcolcomponent';
describe('GridColComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        GridColComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the GridColComponent', () => {
   const fixture = TestBed.createComponent(GridColComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('should invoke life cycle methods', () => {
  const fixture = TestBed.createComponent(GridColComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__gr_col_5',apzcontrol:'', ColWrapper:{'direct':{'className':' gcb-col12   ', 'style' : {}}}, widgettype:'COLUMN', widgetcategory:'GRID'}
  fixture.detectChanges();
  expect(component.colWrapper.nativeElement).toBeDefined();
});
});