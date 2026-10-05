import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import {WindowRef} from '../../../../appzillon/scripts/angular/appzillon.service'; // /../ appzillon/scripts/angular/appzillon.service';
import {BodyComponent} from './bodycomponent';
describe('BodyComponent', () => {
  beforeEach( () => {
     TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
      ],
      providers: [WindowRef]
    }).compileComponents();
  });

  it('should create the MTList', () => {
    const fixture = TestBed.createComponent(BodyComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});