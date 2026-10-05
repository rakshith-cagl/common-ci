import {NgModule} from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ClickStopPropagation} from './stopEventDirective';

@NgModule({
    imports:[
        CommonModule,
        FormsModule
    ],
    declarations:[ClickStopPropagation],
    exports:[ClickStopPropagation]
})
export class EventStopDirectiveModule {  }