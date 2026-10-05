import { NgModule } from '@angular/core';
import { DropdownComponent } from './drop-down.component';
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
    exports: [DropdownComponent],
    declarations: [DropdownComponent],
})
export class DropDownModule { }
