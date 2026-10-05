import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { InputComponent } from './inputcomponent';
import { FormsModule } from '@angular/forms';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
@NgModule({
    imports: [
      CommonModule,
      FormsModule,
      EventStopDirectiveModule,
      EventDelagationModule
      ],
     entryComponents:[InputComponent],
    declarations:[InputComponent],
      exports:[InputComponent]
})

export class InputComponentModule{}