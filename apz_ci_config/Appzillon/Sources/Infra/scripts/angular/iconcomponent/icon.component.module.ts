import { NgModule } from '@angular/core';

import { IconComponent } from './icon.component';
import { CommonModule } from '@angular/common';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [IconComponent],
    declarations: [IconComponent],
    providers: [],
})
export class IconComponentModule { }
