import { Component, OnInit, Input, ViewChild, ElementRef,DoCheck, AfterViewInit, ViewEncapsulation, 
    Output,EventEmitter } from '@angular/core';
import { WindowRef,apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';
import $ from 'jquery';

@Component({
    selector: 'DropDownasButtonComponent',
    templateUrl: 'dropdownbutton.component.html',
    encapsulation:ViewEncapsulation.None

})

export class DropDownButtonComponent implements OnInit,AfterViewInit,DoCheck {
    @ViewChild('wrapperClasses',{static:false}) dpDownWrapperClassesRef!: ElementRef;
    @ViewChild('labelWrapper',{static:false}) dpDownLabelWrapperRef!: ElementRef;
    @ViewChild('labelProps',{static:false}) dpDownLabelPropsRef!: ElementRef;
    @ViewChild('contentWrapper',{static:false}) dpDownContentWrapperRef!: ElementRef;
    @ViewChild('spanWrapper',{static:false}) dpDownSpanWrapperRef!: ElementRef;

    @Input()
    public props:any;

    @Input()
    public rowIndex:any;

    @Input()
    public row:any;

    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();

    id:any;
    attrs:any;
    value:any;
    alignment:any;
    stateAttr:any;
    state:any;
    statOpts:any;
    toolTip:any;
    tooltipCls:any;
    cssCls:any;
    snoCls:any;
    widthCls:any;
    widthStyleAttr:any;
    disabledCls:any;
    apprCls:any;
    defVal:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";
    
    

    constructor(public winRef:WindowRef,public dataServiceUtils: DataService){
	}

    ngOnInit() {
        this.id = (this.rowIndex != undefined)? this.props.id + "_" + this.rowIndex:this.props.id;
        this.attrs = this.props.attrs;
        this.value = this.attrs.defaultvalue;
        this.alignment = "";this.toolTip = ""; this.tooltipCls = "";this.cssCls = ""; this.snoCls = "";
        this.widthCls="";
        this.stateAttr = "";this.widthStyleAttr =""; this.disabledCls = ""; this.defVal = "&nbsp;";this.apprCls="";
        this.state = this.attrs.state;
        this.statOpts=[];
        if (this.attrs.contentalignment == "LEFT") {
	        this.alignment = " lft";
	    } else if (this.attrs.contentalignment == "CENTER") {
	        this.alignment = " cen";
	    } else if (this.attrs.contentalignment == "RIGHT") {
	        this.alignment = " rht";
	    }
	    if (this.state=='DISABLED') {
	        this.stateAttr = ' disabled="disabled"';
	        this.disabledCls= " disabled";
	    }
	    if (this.attrs.tooltip) {
	        this.toolTip = " original-title="+this.attrs.tooltip+"";
	        this.tooltipCls = " tooltipcls";
	    }
	    if (this.attrs.options == "N") {
	     	this.snoCls = " sno";
	    }
	    if (this.attrs.cssclasses != undefined) {
	         this.cssCls = " " + this.attrs.cssclasses;
	    }
	    if (this.attrs.appearance != undefined && this.attrs.appearance != null && this.attrs.appearance != "") {
         	this.apprCls = " " + this.attrs.appearance;
     	}
        this.statOpts = this.attrs.staticoptions;
        
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.elm = this.props.id.split("__").pop();
        this.setContent();
    }

    ngAfterViewInit(){
        if(this.props.cntrType == "FORM"){
            Object.assign(this.dpDownWrapperClassesRef.nativeElement, this.props.wrapperClasses);
            Object.assign(this.dpDownLabelWrapperRef.nativeElement, this.props.labelWrapper.direct);
            Object.assign(this.dpDownLabelPropsRef.nativeElement, this.props.labelProps.direct);
            Object.assign(this.dpDownContentWrapperRef.nativeElement, this.props.contentWrapper.direct);
             }
        if(this.dpDownSpanWrapperRef){
            Object.assign(this.dpDownSpanWrapperRef.nativeElement, this.props.spanWrapper);
        }
    }
         
    ngDoCheck(){
        this.setContent();
    }
    
    setContent() {
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId)	
    }
        


    handleClick(elmObj:any,e:any){
		e.preventDefault();
			e.stopPropagation();
			let elmId = $(elmObj).children('button').attr("id");
			let obj = $("#"+elmId)[0];
            let iconObj = $(obj).parents("#"+obj.id+"_ext:first").children('span');
            
    		$(".is-open").not($(obj).parents("#"+obj.id+"_ext:first")).each(function(i:any,divObj:any){
                if($(divObj).hasClass('is-open')){
    				iconObj = $(divObj).children('span');
    				$(iconObj).children('svg').remove();
    				$(iconObj).append('<svg class="ett-icon icon-down px34"><use xlink:href="#icon-down"></use></svg>');
    				$(divObj).removeClass('is-open'); 
    			}
    		});
    		let disabled = document.getElementById(obj.id)?.hasAttribute("disabled");
    		if(!disabled){
    			if($(obj).parent().hasClass('is-open')){
    				$(obj).parent().removeClass('is-open');
    			} else {
    				$(obj).parent().addClass('is-open');
    			}
    		}
    	
    		$('html').on('click', function(event:any) {
    			if ($(event.target).parents('#'+obj.id+'_ext').length==0 && event.target.id !== obj.id+'_ext') {
    				$(obj).parents("#"+obj.id+"_ext:first").removeClass('is-open');
    				let iconObj = $(obj).parents("#"+obj.id+"_ext:first").children('span');
    				$(iconObj).children('svg').remove();
    				$(iconObj).append('<svg class="ett-icon icon-down px34"><use xlink:href="#icon-down"></use></svg>');
    			}
            });
            
            $("#"+elmId+'_ext').find('li:not(.is-disabled)').on( 'click', function(e:any) {
                $(e.currentTarget).find('a')[0].click();
	  		    e.stopPropagation();
			});
        }

}
