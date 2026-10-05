import { TestBed } from '@angular/core/testing';
import { SecColComponent  } from './seccolcomponent';
describe('SecColComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        SecColComponent
     ],
     providers: []
   }).compileComponents();
 });

 it('should create the SecColComponent', () => {
   const fixture = TestBed.createComponent(SecColComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });
 
 it('should invoke life cycle methods', () => {
  const fixture = TestBed.createComponent(SecColComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__sc_col_1', cntrType:'NAVBAR' , ColWrapper:{'direct':{'className':' pri scb-sml100 scb-med100 scb-lar100 scb-xxl100', 'style' : {}}} }
  fixture.detectChanges();
  expect(component.columnTag).toEqual('li');
});

it('should set columnTag value to span when container type is not NAVBAR', () => {
  const fixture = TestBed.createComponent(SecColComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__sc_col_1', cntrType:'FORM' , ColWrapper:{'direct':{'className':' pri scb-sml100 scb-med100 scb-lar100 scb-xxl100', 'style' : {}}} }
  fixture.detectChanges();
  expect(component.columnTag).toEqual('span')
});

it('should return false if container type is NAVBAR else true on invoking containerCheck()', () => {
  const fixture = TestBed.createComponent(SecColComponent);
  const component = fixture.componentInstance;
  component.props = {id:'elmnts__Elements__sc_col_1', cntrType:'NAVBAR' , ColWrapper:{'direct':{'className':' pri scb-sml100 scb-med100 scb-lar100 scb-xxl100', 'style' : {}}} }
  expect(component.containerCheck()).toBeFalsy();
  component.props = {id:'elmnts__Elements__sc_col_1', cntrType:'FORM' , ColWrapper:{'direct':{'className':' pri scb-sml100 scb-med100 scb-lar100 scb-xxl100', 'style' : {}}} }
  expect(component.containerCheck()).toBeTruthy();
});

});