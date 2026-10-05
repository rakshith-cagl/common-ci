import { NgModule } from '@angular/core';

import { InputWithButtonComponent } from './input-with-button.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,FormsModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [InputWithButtonComponent],
    declarations: [InputWithButtonComponent],
    
})
export class InputWithButtonComponentModule { }
