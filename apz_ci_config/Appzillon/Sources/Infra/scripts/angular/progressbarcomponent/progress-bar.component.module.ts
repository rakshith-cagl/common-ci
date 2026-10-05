import { NgModule } from '@angular/core';

import { ProgressBarComponent } from './progress-bar.component';
import { CommonModule } from '@angular/common';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [ProgressBarComponent],
    declarations: [ProgressBarComponent],
    providers: [],
})
export class ProgressBarComponentModule { }
