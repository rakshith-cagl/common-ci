import { NgModule } from '@angular/core';

import { HyperLinkComponent } from './hyper-link.component';
import { CommonModule } from '@angular/common';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [HyperLinkComponent],
    declarations: [HyperLinkComponent],
   
})
export class HyperLinkComponentModule { }
