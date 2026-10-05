import { NgModule } from '@angular/core';

import { ImageComponent } from './image.component';
import { CommonModule } from '@angular/common';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [ImageComponent],
    declarations: [ImageComponent],
    providers: [],
})
export class ImageComponentModule { }
