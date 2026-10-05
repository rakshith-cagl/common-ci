import { NgModule } from '@angular/core';

import { CardNumberComponent } from './card-number.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,FormsModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [CardNumberComponent],
    declarations: [CardNumberComponent],
    providers: [],
})
export class CardNumberModule { }
