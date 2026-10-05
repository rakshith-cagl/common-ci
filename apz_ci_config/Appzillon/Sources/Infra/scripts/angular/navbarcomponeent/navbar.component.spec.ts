import { TestBed } from '@angular/core/testing';
import { NavbarComponent  } from './navbar.component';
describe('NavbarComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        NavbarComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the NavbarComponent', () => {
   const fixture = TestBed.createComponent(NavbarComponent);
   const app = fixture.componentInstance;
   app.props = {id: 'elmnts_navbar_1', className: 'pri'}
   fixture.detectChanges();
   expect(app).toBeTruthy();
 });
});