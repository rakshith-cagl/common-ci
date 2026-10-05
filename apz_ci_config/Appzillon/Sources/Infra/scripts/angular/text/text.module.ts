import { NgModule } from '@angular/core';

import { TextComponent } from './text.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';

@NgModule({
    imports: [CommonModule,FormsModule,EventStopDirectiveModule,EventDelagationModule],
    exports: [TextComponent],
    declarations: [TextComponent]
})
export class TextModule { }
