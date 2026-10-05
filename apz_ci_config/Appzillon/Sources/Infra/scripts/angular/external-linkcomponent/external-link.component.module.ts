import { NgModule } from '@angular/core';

import { ExternalLinkComponent } from './external-link.component';
import { CommonModule } from '@angular/common';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventStopDirectiveModule],
    exports: [ExternalLinkComponent],
    entryComponents:[ExternalLinkComponent],
    declarations: [ExternalLinkComponent],
  
})
export class ExternalLinkComponentModule{}