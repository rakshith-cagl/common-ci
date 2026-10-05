import {NgModule} from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';
import {EventDelagation} from './delegateEvent';

@NgModule({
    imports:[
        CommonModule,
        FormsModule
    ],
    declarations:[EventDelagation],
    exports:[EventDelagation]
})
export class EventDelagationModule { 
	
 }