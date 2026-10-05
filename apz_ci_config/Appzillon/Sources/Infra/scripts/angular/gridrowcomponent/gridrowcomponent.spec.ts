import { TestBed } from '@angular/core/testing';
import { GridRowComponent  } from './gridrowcomponent';
describe('GridRowComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        GridRowComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the GridRowComponent', () => {
   const fixture = TestBed.createComponent(GridRowComponent);
   const app = fixture.componentInstance;
   app.props = {id:'elmnts__Elements__IconRow', className:'grb  mainRow', apzcontrol:'', 'widgettype':'ROW', 'widgetcategory':'GRID'};
   expect(app).toBeTruthy();
 });
});