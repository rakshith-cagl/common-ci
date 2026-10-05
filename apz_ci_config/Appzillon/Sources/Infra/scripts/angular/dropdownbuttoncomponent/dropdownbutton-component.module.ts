import { NgModule } from '@angular/core';

import { DropDownButtonComponent } from './dropdownbutton.component';
import { CommonModule } from '@angular/common';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [DropDownButtonComponent],
    declarations: [DropDownButtonComponent],
    providers: [],
})
export class DropDownButtonModule { }
