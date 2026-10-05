import { NgModule } from '@angular/core';

import { SliderComponent } from './slider.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,FormsModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [SliderComponent],
    declarations: [SliderComponent],
    providers: [],
})
export class SliderComponentModule { }
