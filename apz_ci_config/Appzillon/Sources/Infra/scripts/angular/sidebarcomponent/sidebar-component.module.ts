import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SideBarComponent } from './sidebar.component';

@NgModule({
    imports: [CommonModule],
    exports: [SideBarComponent],
    declarations: [SideBarComponent],
    providers: [],
})
export class SideBarModule { }
