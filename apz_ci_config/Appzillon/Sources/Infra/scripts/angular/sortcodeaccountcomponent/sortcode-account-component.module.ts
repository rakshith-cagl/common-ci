import { NgModule } from '@angular/core';

import { SortCodeAccountComponent } from './sortcode-account.component';
import { CommonModule } from '@angular/common';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [SortCodeAccountComponent],
    declarations: [SortCodeAccountComponent],
    providers: [],
})
export class SortCodeAccountModule { }
