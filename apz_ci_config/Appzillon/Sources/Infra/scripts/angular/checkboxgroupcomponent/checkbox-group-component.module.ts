import { NgModule } from '@angular/core';
import { CheckboxGroupComponent } from './checkbox-group.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@NgModule({
    imports: [CommonModule,FormsModule],
    exports: [CheckboxGroupComponent],
    declarations: [CheckboxGroupComponent],
  
})
export class CheckBoxGroupModule { }
