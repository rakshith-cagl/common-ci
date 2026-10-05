import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TagsInputComponent } from './tagsInputComponent.component';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        EventDelagationModule,
        EventStopDirectiveModule
    ],
    exports: [TagsInputComponent],
    declarations: [TagsInputComponent]
})
export class TagsInputModule { }
