import {Directive, HostListener} from '@angular/core';
import $ from 'jquery';
@Directive({
    selector: "[clickstoppropagation]"
  })
 export class ClickStopPropagation {

    @HostListener('click',["$event"])
    @HostListener('keydown',["$event"])
    @HostListener('keypress',["$event"])
    @HostListener('keyup',["$event"])
    @HostListener('dblclick',["$event"])
    @HostListener('blur',["$event"])
    @HostListener('focus',["$event"])
    @HostListener('change',["$event"])
    @HostListener('mouseover',["$event"])
    @HostListener('mousemove',["$event"])
    @HostListener('mouseout',["$event"])
   @HostListener('mousedown',["$event"])
    @HostListener('mouseup',["$event"])
    @HostListener('mouseenter',["$event"])
    @HostListener('submit',["$event"])
    @HostListener('load',["$event"])
    @HostListener('unload',["$event"])
    @HostListener('reset',["$event"])
    @HostListener('select',["$event"])
    @HostListener('daychange',["$event"])
    @HostListener('eventselect',["$event"])
    @HostListener('monthchange',["$event"])
    @HostListener('monthloaded',["$event"])
    @HostListener('setdate',["$event"])
    onEvent(event:Event){
        if (event && event.target) {
            let target = $(event.target)
            if(target.attr("rowno") == undefined && target.parent()!.attr("rowno") == undefined){
                event.stopPropagation();
            }
        }
    }

  }