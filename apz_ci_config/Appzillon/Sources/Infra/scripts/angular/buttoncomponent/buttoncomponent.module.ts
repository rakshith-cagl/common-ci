import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ButtonComponent } from './buttoncomponent';
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
     entryComponents:[ButtonComponent],
    declarations:[ButtonComponent],
      exports:[ButtonComponent]
})

export class ButtonComponentModule{}