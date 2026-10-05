import { Component, OnInit, Input, ViewEncapsulation,DoCheck, AfterViewInit, ViewChild, ElementRef,
     OnChanges,Output,EventEmitter } from '@angular/core';
import { WindowRef,apz} from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from '../data_angular';
import $ from 'jquery';
@Component({
    selector: 'FileBrowserComponent',
    templateUrl: 'file-browser.component.html',
    encapsulation:ViewEncapsulation.None
})

export class FileBrowserComponent implements OnInit, AfterViewInit,OnChanges,DoCheck {
    @ViewChild('fileButtonWrapper',{static:false}) fileButtonWrapperRef!:ElementRef;
    @ViewChild('fileButton',{static:false}) fileButtonRef!:ElementRef;
    @ViewChild('BrowseButton',{static:false}) BrowseButtonRef!:ElementRef;
    @ViewChild('UploadButton',{static:false}) UploadButtonRef!:ElementRef;

    @ViewChild('wrapperClasses',{static:false})fileBrowserWrapperClassesRef!: ElementRef;
    @ViewChild('labelWrapper',{static:false})fileBrowserLabelWrapperRef!: ElementRef;
    @ViewChild('labelProps',{static:false})fileBrowserLabelPropsRef!: ElementRef;
    @ViewChild('contentWrapper',{static:false})fileBrowserContentWrapperRef!: ElementRef;
    
    @Input()
    public props:any;
    id:any;
    value:any;
    index:any;

    @Input()
    public row:any;
    elmData:any;
    containerId='';
    values:any;
    apz:any = apz;
    elm="";
    
    @Input()
    public rowIndex:any;
    @Output()
	public blur: EventEmitter<any> = new EventEmitter<any>();
	@Output()
	public focus: EventEmitter<any> = new EventEmitter<any>();
	
	@Output()
	public buttonClick: EventEmitter<any> = new EventEmitter<any>();

    @Output()
	public labelIconClick: EventEmitter<any> = new EventEmitter<any>();
    
    constructor(public winRef:WindowRef, public dataServiceUtils: DataService) {}

    ngOnInit() {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.value = this.props.value;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;
        this.elm = this.props.id.split("__").pop();
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.setContent();  
    }

    ngAfterViewInit(){
        if(this.props.cntrType == "FORM"){
            Object.assign(this.fileBrowserWrapperClassesRef.nativeElement, this.props.wrapperClasses);
            Object.assign(this.fileBrowserLabelWrapperRef.nativeElement, this.props.labelWrapper.direct);
            Object.assign(this.fileBrowserLabelPropsRef.nativeElement, this.props.labelProps.direct);
            Object.assign(this.fileBrowserContentWrapperRef.nativeElement, this.props.contentWrapper.direct);
             }
        if(this.fileButtonWrapperRef){
        Object.assign(this.fileButtonWrapperRef.nativeElement, this.props.fileButtonWrapper.direct) 
        }
        if(this.fileButtonRef){
        Object.assign(this.fileButtonRef.nativeElement, this.props.fileButton.direct) 
        }
        if(this.BrowseButtonRef){
        Object.assign(this.BrowseButtonRef.nativeElement, this.props.BrowseButton.direct) 
        }
        if(this.UploadButtonRef){
        Object.assign(this.UploadButtonRef.nativeElement, this.props.UploadButton.direct) 
      }
    }


    ngDoCheck(){
        this.setContent();
    }

    setSelectedFileName(props:any,obj:any){
        let id = obj.id;
        let spanLi = $("#" + id).parent().parent().siblings().find('#selectedfiles');
        $ (spanLi).children().remove();
        let result = $(obj)[0].files;
        let file:any={};
        for (const iterator of result) {
            file = iterator;
            $(spanLi).append("<p>" + file.name +','+' '+"</p>");  
        }
        apz.data.setPropsData(props,file.name);
      }

    buttonClickEvent(event: any) {
        this.buttonClick.emit(event);
    }

    labelIconClickEvent(event: any) {
        this.labelIconClick.emit(event);
    }
    
    ngOnChanges(){
        
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ?this.rowIndex:-1;

    }

    setContent(){
        this.values = this.dataServiceUtils.getContent(this.props.id, this.row, this.rowIndex, this.elmData, this.containerId);
    }
}