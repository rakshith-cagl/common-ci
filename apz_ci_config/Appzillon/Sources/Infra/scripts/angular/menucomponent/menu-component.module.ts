import { NgModule } from '@angular/core';

import { MenuComponent } from './menu.component';
import { CommonModule } from '@angular/common';

@NgModule({
    imports: [CommonModule],
    exports: [MenuComponent],
    declarations: [MenuComponent],
    providers: [],
})
export class MenuModule { }
