import { NgModule } from '@angular/core';

import { DialogComponent } from './dialog.component';
import { CommonModule } from '@angular/common';

@NgModule({
    imports: [CommonModule],
    exports: [DialogComponent],
    declarations: [DialogComponent],
    providers: [],
})
export class DialogModule { }
