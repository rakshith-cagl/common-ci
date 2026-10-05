import { NgModule } from '@angular/core';

import { BadgeComponent } from './badge.component';
import { CommonModule } from '@angular/common';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventDelagationModule,EventStopDirectiveModule],
     entryComponents:[BadgeComponent],
    declarations: [BadgeComponent],
    exports: [BadgeComponent]
})
export class BadgeComponentModule { }