import { Component, OnInit, ViewEncapsulation, Input, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { apz } from "../appzillon.service";
import { DataService } from '../data_angular';
@Component({
    selector: "ReadOnlyElementComponent",
    templateUrl: "./readonlyelements.html",
    encapsulation: ViewEncapsulation.None
})

export class ReadOnlyElementComponent implements OnInit, AfterViewInit {
    @Input() props: any;
    id: string = "";
    staticClass = "";
    staticOptions = [];
    elmData: any;
    value = "";
    values: { [key: string]: any } = {};
    elm = "";
    @ViewChild('labelProps', { static: false }) labelProps!: ElementRef;
    @ViewChild('nonStaticContent', { static: false }) nonStaticContent!: ElementRef;
    @ViewChild('staticContent', { static: false }) staticContent!: ElementRef;
    @ViewChild('otherWidget', { static: false }) otherWidget!: ElementRef;

    constructor(private dataServiceUtils: DataService) {
    }
    ngOnInit() {
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.id = this.props.id;
        this.value = this.props.value;
        if (apz.scrMetaData.elmsMap[this.props.id].staticOptions != undefined) {
            this.staticOptions = apz.scrMetaData.elmsMap[this.props.id].staticOptions;
        }
    }
    ngDoCheck() {
        this.setContent();
    }
    setContent() {
        if (!this.isUiElm()) {
            this.values = this.dataServiceUtils.setElmValue(this.props.id);
            this.elm = this.props.id.split("__").pop();
        } else {
            this.value = this.props.value;
        }
    }
    isUiElm() {
        return apz.scrMetaData.elmsMap[this.props.id].ui == "Y";
    }
    getClassName(val:any) {
        let staticClass = (this.value == val) ? "" : "sno";
        return this.props.content.direct.className + staticClass;
    }
    ngAfterViewInit() {
        if (this.labelProps) {
            Object.assign(this.labelProps.nativeElement, this.props.labelProps.direct);
        }
        if (this.staticContent) {
            Object.assign(this.staticContent.nativeElement, this.props.content.direct);
        }
        if (this.nonStaticContent) {
            Object.assign(this.nonStaticContent.nativeElement, this.props.content.direct);
        }
        if (this.otherWidget) {
            Object.assign(this.otherWidget.nativeElement, this.props.content.direct);
        }
    }
}
