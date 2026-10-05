import { TestBed } from '@angular/core/testing';
import { GaugeComponent  } from './gauge.component';
describe('GaugeComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        GaugeComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the GaugeComponent', () => {
   const fixture = TestBed.createComponent(GaugeComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });
});