import { NgModule } from '@angular/core';

import { SortCodeComponent } from './sort-code.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,FormsModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [SortCodeComponent],
    declarations: [SortCodeComponent],
    providers: [],
})
export class SortCodeModule { }
