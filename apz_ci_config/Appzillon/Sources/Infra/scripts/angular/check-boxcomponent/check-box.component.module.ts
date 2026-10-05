import { NgModule } from '@angular/core';

import { CheckboxComponent } from './check-box.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';

@NgModule({
    imports: [CommonModule,FormsModule,EventStopDirectiveModule,EventDelagationModule],
    exports: [CheckboxComponent],
    declarations: [CheckboxComponent],
    entryComponents:[CheckboxComponent],
})
export class CheckBoxComponentModule { }
