import { NgModule } from '@angular/core';

import { GuageElementComponent } from './gauge-element.component';
import { CommonModule } from '@angular/common';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventStopDirectiveModule],
    exports: [GuageElementComponent],
    declarations: [GuageElementComponent],
    providers: [],
})
export class GaugeElementModule { }
