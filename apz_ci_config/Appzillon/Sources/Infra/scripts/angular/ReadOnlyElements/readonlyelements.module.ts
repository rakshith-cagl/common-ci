import { NgModule } from '@angular/core';

import { ReadOnlyElementComponent } from './readonlyelement';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@NgModule({
    imports: [
    CommonModule,
    FormsModule
    ],
    exports: [ReadOnlyElementComponent],
    declarations: [ReadOnlyElementComponent],
})
export class ReadOnlyElementModule { }
