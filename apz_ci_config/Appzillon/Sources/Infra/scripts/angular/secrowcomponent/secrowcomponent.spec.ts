import { TestBed } from '@angular/core/testing';
import { SecRowComponent  } from './secrowcomponent';
describe('SecRowComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        SecRowComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the SecRowComponent', () => {
   const fixture = TestBed.createComponent(SecRowComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });
});