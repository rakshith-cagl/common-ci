import { NgModule } from '@angular/core';

import { LabelComponent } from './label.component';
import { CommonModule } from '@angular/common';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';

@NgModule({
    imports: [CommonModule,EventStopDirectiveModule,EventDelagationModule],
    exports: [LabelComponent],
    declarations:  [LabelComponent],
   
})
export class LabelComponentModule { }
