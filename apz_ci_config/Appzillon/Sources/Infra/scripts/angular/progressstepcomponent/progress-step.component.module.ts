import { NgModule } from '@angular/core';

import { ProgressStepComponent } from './progress-step.component';
import { CommonModule } from '@angular/common';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventStopDirectiveModule],
    exports: [ProgressStepComponent],
    declarations: [ProgressStepComponent],
    providers: [],
})
export class ProgressStepComponentModule { }
