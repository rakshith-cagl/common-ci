import { TestBed } from '@angular/core/testing';
import { HeaderComponent  } from './header.component';
describe('HeaderComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        HeaderComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the HeaderComponent', () => {
   const fixture = TestBed.createComponent(HeaderComponent);
   const app = fixture.componentInstance;
   app.props = {id: 'elmnts__Elements__hdr_1', className: 'pri', headertype: 'SIMPLE'}
   fixture.detectChanges();
   expect(app).toBeTruthy();
 });
});