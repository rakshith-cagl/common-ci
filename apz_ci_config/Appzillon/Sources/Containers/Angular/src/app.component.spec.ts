import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import {LazyLoaderService} from './appzillon/scripts/angular/lazy-loader.service';
import {WindowRef} from './appzillon/scripts/angular/appzillon.service';
import { LAZY_WIDGETS } from './appzillon/scripts/angular/tokens';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        AppComponent
      ],
      providers: [LazyLoaderService, WindowRef,   {
        provide: LAZY_WIDGETS,
        useValue: {},
       }]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
