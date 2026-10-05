import { NgModule } from '@angular/core';

import { BulletComponent } from './bullet.component';
import { CommonModule } from '@angular/common';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventStopDirectiveModule],
    exports: [BulletComponent],
    declarations: [BulletComponent],
   
})
export class BulletComponentModule { }
