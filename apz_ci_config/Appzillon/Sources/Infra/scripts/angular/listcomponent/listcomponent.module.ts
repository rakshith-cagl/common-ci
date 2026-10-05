import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ListComponent } from './listcomponent';


@NgModule({
    imports: [
     CommonModule
    ],
    entryComponents:[ListComponent],
    declarations:[ListComponent],
    exports:[ListComponent]
})

export class ListComponentModule{}