import { NgModule } from '@angular/core';

import { RadioButtonComponent } from './radio-button.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,FormsModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [RadioButtonComponent],
    declarations: [RadioButtonComponent],
    providers: [],
})
export class RadioButtonComponentModule { }
