import { NgModule } from '@angular/core';

import { BreadCrumbComponent } from './breadcrumb.component';
import { CommonModule } from '@angular/common';

@NgModule({
    imports: [CommonModule],
    exports: [BreadCrumbComponent],
    declarations: [BreadCrumbComponent],
    providers: [],
})
export class BreadCrumbModule { }
