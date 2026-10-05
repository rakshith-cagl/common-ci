import { TestBed } from '@angular/core/testing';
import { ChartComponent  } from './chart.component';
describe('ChartComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        ChartComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the ChartComponent', () => {
   const fixture = TestBed.createComponent(ChartComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });
});