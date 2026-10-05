import { TestBed } from '@angular/core/testing';
import { SideBarComponent  } from './sidebar.component';
describe('SideBarComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        SideBarComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the SideBarComponent', () => {
   const fixture = TestBed.createComponent(SideBarComponent);
   const app = fixture.componentInstance;
   app.props = {id: 'elmts_sdbr_1', className: 'pri', sidebartype: 'STATIC'}
   fixture.detectChanges();
   expect(app).toBeTruthy();
 });
});