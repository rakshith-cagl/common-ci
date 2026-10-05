import { NgModule } from '@angular/core';

import { FileBrowserComponent } from './file-browser.component';
import { CommonModule } from '@angular/common';
import {EventDelagationModule} from '../EventStopDirective/delegateEvent.module';
import {EventStopDirectiveModule} from '../EventStopDirective/stopEventDirective.module';

@NgModule({
    imports: [CommonModule,EventDelagationModule,EventStopDirectiveModule],
    exports: [FileBrowserComponent],
    declarations: [FileBrowserComponent],
    providers: [],
})
export class FileBrowserModule { }
