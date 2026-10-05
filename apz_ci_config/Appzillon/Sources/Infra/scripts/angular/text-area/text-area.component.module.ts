import { NgModule } from '@angular/core';

import { TextAreaComponent } from './text-area.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        EventDelagationModule,
        EventStopDirectiveModule
    ],
    exports: [TextAreaComponent],
    declarations: [TextAreaComponent],
})
export class TextAreaModule { }
