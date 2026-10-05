import { TestBed } from '@angular/core/testing';
import { PopOverComponent  } from './pop-over.component';
describe('PopOverComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        PopOverComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the PopOverComponent and invoke life cycle methods', () => {
   const fixture = TestBed.createComponent(PopOverComponent);
   const app = fixture.componentInstance;
   app.props = {id: 'elmnts_pop_1', content: {direct: {className: 'pri'}}};
   fixture.detectChanges()
   expect(app).toBeTruthy();
 });
});