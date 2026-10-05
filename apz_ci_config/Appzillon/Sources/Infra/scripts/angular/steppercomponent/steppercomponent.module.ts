import { NgModule } from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StepperComponent } from './steppercomponent.component';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,
        FormsModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [StepperComponent],
    declarations: [StepperComponent],
})
export class StepperModule { }
