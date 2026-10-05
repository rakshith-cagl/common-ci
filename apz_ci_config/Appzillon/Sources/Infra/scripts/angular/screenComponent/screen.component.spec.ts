import { TestBed } from '@angular/core/testing';
import { ScreenComponent  } from './screencomponent';
import {WindowRef} from '../../../../appzillon/scripts/angular/appzillon.service';
describe('ScreenComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        ScreenComponent
     ],
     providers: [WindowRef]
   }).compileComponents();
 });

 it('should create the ScreenComponent', () => {
   const fixture = TestBed.createComponent(ScreenComponent);
   const app = fixture.componentInstance;
   app.props = {id:'scr__elmnts__Elements__main', className:'rolepage', screenType:'PG'};
   fixture.detectChanges();
   expect(app).toBeTruthy();
 });

 it('should return true when screen type is PG else false on invoking checkScreenType()', () => {
  const fixture = TestBed.createComponent(ScreenComponent);
  const app = fixture.componentInstance;
  app.props = {id:'scr__elmnts__Elements__main', className:'rolepage', screenType:'PG'}
  expect(app.checkScreenType()).toBeTruthy();
  app.props = {id:'scr__elmnts__Elements__main', className:'rolepage', screenType:'NO'}
  expect(app.checkScreenType()).toBeFalsy();
});
});