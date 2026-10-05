import {
    Component, OnInit, SimpleChanges, ViewEncapsulation, Input, ViewChild, ElementRef, AfterViewInit,
    ChangeDetectorRef, DoCheck, OnChanges, AfterViewChecked, Output, EventEmitter
} from '@angular/core';
import { WindowRef, apz } from 'src/appzillon/scripts/angular/appzillon.service';
import { DataService } from 'src/appzillon/scripts/angular/data_angular';
import $ from 'jquery';


@Component({
    selector: 'DropdownComponent',
    templateUrl: 'drop-down.component.html',
    encapsulation: ViewEncapsulation.None
})

export class DropdownComponent implements OnInit, AfterViewInit, DoCheck, OnChanges, AfterViewChecked {
    autoCompleteOptions: any = [];
    multiSelectTagsOptions: any = [];
    DROPDOWN_TYPE_SIMPLE = 'SIMPLE'
    DROPDOWN_TYPE_AUTOCOMPLETE = 'AUTOCOMPLETE'
    DROPDOWN_TYPE_MULTI_SELECT_CHECKBOX = 'MULTISELECTCHECKBOX'
    DROPDOWN_TYPE_WITH_SUB_OPTIONS = 'WITHSUBOPTIONS'
    DROPDOWN_TYPE_MULTI_SELECT_TAGS = 'MULTISELECTTAGS'
    DROPDOWN_TYPE_NATIVE = 'NATIVE'
    @ViewChild('simpleInputContent', { static: false }) simpleInputContentRef!: ElementRef;
    @ViewChild('multiSelectCheckInputContent', { static: false }) multiSelectCheckInputContentRef!: ElementRef;
    @ViewChild('suboptionsInputContent', { static: false }) suboptionsInputContentRef!: ElementRef;

    @ViewChild('autoCompleteDivWrapper', { static: false }) autoCompleteDivWrapperRef!: ElementRef;
    @ViewChild('simpleDivWrapper', { static: false }) simpleDivWrapperRef!: ElementRef;
    @ViewChild('multiSelectTagsDivWrapper', { static: false }) multiSelectTagsDivWrapperRef!: ElementRef;
    @ViewChild('selectTagAuto', { static: false }) selectTagAutoRef!: ElementRef;
    @ViewChild('selectMSTags', { static: false }) selectMSTagsRef!: ElementRef;
    @ViewChild('multiSelectcheckboxDivWrapper', { static: false }) multiSelectcheckboxDivWrapperRef!: ElementRef;
    @ViewChild('withSubOptionsDivWrapper', { static: false }) withSubOptionsDivWrapperRef!: ElementRef;
    @ViewChild('multiSelectCheckOptionsDivWrapper', { static: false }) multiSelectCheckOptionsDivWrapperRef!: ElementRef;

    @ViewChild('withSubOptDivWrapper', { static: false }) withSubOptDivWrapperRef!: ElementRef;
    @ViewChild('selectNative', { static: false }) selectNativeRef!: ElementRef;

    @ViewChild('formlabelWrapper', { static: false }) dpdwnFormlabelWrapperRef!: ElementRef;
    @ViewChild('formlabelProps', { static: false }) dpdwnFormlabelPropsRef!: ElementRef;
    @ViewChild('formContentWrapper', { static: false }) dpdwnFormContentWrapperRef!: ElementRef;
    @ViewChild('navSapnwrapper', { static: false }) dpdwnNavSapnwrapperRef!: ElementRef;
    @ViewChild('tableSpanWrapper', { static: false }) dpdwnTableSpanWrapperRef!: ElementRef;
    @ViewChild('formSpanWrapper', { static: false }) dpdwnFormSpanWrapperRef!: ElementRef;
    @ViewChild('formWrapperClass', { static: false }) dpdwnFormWrapperClassRef!: ElementRef;

    @Input() props: any;
    id: any;
    options!: any;
    staticOptions: any = [];
    index: any;
    values: any = {};
    elm = "";
    @Input()
    public rowIndex: any;
    @Input()
    public row: any;
    elmData: any;
    selectedValue: { desc: string, val: string } = {
        desc: '',
        val: ''
    };
    multiChBxVal: any = '';
    containerId = '';
    @Output()
    public blur: EventEmitter<any> = new EventEmitter<any>();
    @Output()
    public focus: EventEmitter<any> = new EventEmitter<any>();

    @Output()
    public click: EventEmitter<any> = new EventEmitter<any>();

    @Output()
    public change: EventEmitter<any> = new EventEmitter<any>();

    @Output()
    public labelIconClick: EventEmitter<any> = new EventEmitter<any>();

    constructor(public winRef: WindowRef, public dataServiceUtils: DataService, private cd: ChangeDetectorRef) {
    }

    ngOnInit() {
        if (apz.scrMetaData.elmsMap[this.props.id].staticOptions != undefined) {
            this.staticOptions = apz.scrMetaData.elmsMap[this.props.id].staticOptions;

        }
        this.options = {
        };
        this.elmData = apz.scrMetaData.elmsMap[this.props.id];
        this.containerId = this.elmData.container;
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ? this.rowIndex : -1;
        this.setContent()

    }

    ngAfterViewInit() {
        if (this.props.type == this.DROPDOWN_TYPE_SIMPLE) {
            Object.assign(this.simpleDivWrapperRef.nativeElement, this.props.divWrapper.direct)
            Object.assign(this.simpleInputContentRef.nativeElement, this.props.content.direct)
            this.dataServiceUtils.eventBinding(this.simpleInputContentRef.nativeElement, this.props.content.apzevents)
        }

        if (this.props.type == this.DROPDOWN_TYPE_MULTI_SELECT_TAGS) {
            Object.assign(this.multiSelectTagsDivWrapperRef.nativeElement, this.props.divWrapper.direct)
            $('#'+this.props.id).on('change', this.bindChangeEvent.bind(this));
        }

        if (this.props.type == this.DROPDOWN_TYPE_AUTOCOMPLETE) {
            Object.assign(this.autoCompleteDivWrapperRef.nativeElement, this.props.divWrapper.direct)
            $('#'+this.props.id).on('change', this.bindChangeEvent.bind(this));
        }

        if (this.props.type == this.DROPDOWN_TYPE_MULTI_SELECT_CHECKBOX) {
            Object.assign(this.multiSelectcheckboxDivWrapperRef.nativeElement, this.props.divWrapper.direct)
            Object.assign(this.multiSelectCheckOptionsDivWrapperRef.nativeElement, this.props.optionsDivWrapper.direct)
            Object.assign(this.multiSelectCheckInputContentRef.nativeElement, this.props.content.direct)
            this.dataServiceUtils.eventBinding(this.multiSelectCheckInputContentRef.nativeElement, this.props.content.apzevents)
        }

        if (this.props.type == this.DROPDOWN_TYPE_WITH_SUB_OPTIONS) {
            Object.assign(this.withSubOptDivWrapperRef.nativeElement, this.props.divWrapper.direct)
            Object.assign(this.withSubOptionsDivWrapperRef.nativeElement, this.props.optionsDivWrapper.direct)
            Object.assign(this.suboptionsInputContentRef.nativeElement, this.props.content.direct)
            this.dataServiceUtils.eventBinding(this.suboptionsInputContentRef.nativeElement, this.props.content.apzevents)
        }

        if (this.props.type == this.DROPDOWN_TYPE_NATIVE) {
            Object.assign(this.selectNativeRef.nativeElement, this.props.selectTag.direct)
            this.dataServiceUtils.eventBinding(this.selectNativeRef.nativeElement, this.props.apzevents)
        }

        if ((this.props.cntrType == 'NAVBAR' || this.props.cntrType == 'LIST')) {
            Object.assign(this.dpdwnNavSapnwrapperRef.nativeElement, this.props.spanWrapper)
        }

        if (this.props.cntrType == 'FORM') {
            Object.assign(this.dpdwnFormlabelWrapperRef.nativeElement, this.props.labelWrapper.direct)
            Object.assign(this.dpdwnFormlabelPropsRef.nativeElement, this.props.labelProps.direct)
            Object.assign(this.dpdwnFormContentWrapperRef.nativeElement, this.props.contentWrapper.direct)
            Object.assign(this.dpdwnFormSpanWrapperRef.nativeElement, this.props.spanWrapper)
            Object.assign(this.dpdwnFormWrapperClassRef.nativeElement, this.props.wrapperClasses)
        }
        if (this.props.cntrType == 'TABLE') {
            Object.assign(this.dpdwnTableSpanWrapperRef.nativeElement, this.props.spanWrapper)
        }
    }

    ngOnChanges(changes: SimpleChanges) {
        this.id = (this.rowIndex != undefined) ? this.props.id + "_" + this.rowIndex : this.props.id;
        this.index = (this.rowIndex != undefined) ? this.rowIndex : -1;
    }

    setHandler() {
        this.cd.detectChanges();
    }

    ngDoCheck() {
        if (apz.scrMetaData.elmsMap[this.props.id].staticOptions != undefined) {
            this.staticOptions = apz.scrMetaData.elmsMap[this.props.id].staticOptions;
        }
        this.setContent();
        const tagName = $("#" + this.id)[0] ? $("#" + this.id)[0].tagName : '';

        if (tagName === 'SELECT') {
            $("#" + this.id).trigger("change");
        }
    }

    ngAfterViewChecked() {
        let tagName = $("#" + this.id)[0] ? $("#" + this.id)[0].tagName : '';
        if (tagName === 'SELECT') {
            if (!this.isUiElm()) {
                apz.setElmValue(this.id, this.values[this.elm], true);
            }
        }
    }

    setDrdDscValue(val: any) {
        return this.staticOptions.find((element: any) => {
            return element.val === val
        })
    }

    setMultiDrdValValue(val: any) {
        let arVal = val.split(', ');
        let rtVal: any[] = [];
        arVal.forEach((element: any) => {
            const el: any = this.setDrdValValue(element)
            if (el) {
                rtVal.push(el.val)
            }

        });
        return rtVal.join(', ');
    }

    changeMultiDrdValValue(val: any, targetVal: any) {
        const arVal = val?.split(', ');
        const rtVal: any = [];
        const itemIndex = arVal?.indexOf(targetVal);
        if (itemIndex !== -1) {
            arVal?.splice(itemIndex, 1)
            arVal?.forEach((element: any) => {
                const el: any = this.setDrdValValue(element)
                if (el) {
                    const vl = el.val
                    rtVal.push(vl)
                }
            });
        } else {
            arVal?.forEach((element: any) => {

                const el: any = this.setDrdValValue(element)
                if (el) {
                    const vl = el.val
                    rtVal.push(vl)
                }
            });
            const trgt: any = this.setDrdValValue(targetVal)
            if (trgt) {
                rtVal.push(trgt.val)
            }
        };
        return rtVal.join(', ');
    }

    setMultiDrdDscValue(val: any) {
        let arVal = val.split(', ');
        let rtVal: any[] = [];
        arVal.forEach((element: any) => {
            const el: any = this.setDrdDscValue(element);
            if (el) {
                rtVal.push(el.desc)
            }

        });
        return rtVal.join(', ');
    }

    setDrdValValue(desc: any) {
        return this.staticOptions.find((element: any) => {
            return element.desc + '' === desc + '';
        })
    }

    checkedIputs(val: any) {
        val = val.toString();
        if (!this.isUiElm()) {
            const prpval = this.values[this.elm] ? this.values[this.elm].split(', ') : [];
            return prpval.includes(val);
        } else {
            const prpval = this.props.value ? this.props.value.split(',') : [];
            return prpval.includes(val);
        }
    }

    onMultiSelectOption(target: any, event: any, id: any) {
        let oldValue = this.multiChBxVal;
        let mltchbxval = (<HTMLInputElement>$('#' + this.id)[0])?.value;
        if (!this.isUiElm()) {
            if (this.props.type === this.DROPDOWN_TYPE_MULTI_SELECT_CHECKBOX) {
                if (target.value) {
                    this.values[this.elm] = this.changeMultiDrdValValue(mltchbxval, target.value);
                    this.multiChBxVal = this.values[this.elm];
                }
            }
        } else {
            if (this.props.type === this.DROPDOWN_TYPE_MULTI_SELECT_CHECKBOX) {
                if (target.value) {
                    this.props.value = this.changeMultiDrdValValue(mltchbxval, target.value);
                    this.multiChBxVal = this.props.value;
                }
            }

        }
        if (oldValue !== this.multiChBxVal) {
            this.change.emit();
        }
        this.cd.detectChanges();
    }

    onSelectOption(target: any, event: any, id: any) {
        let oldValue = this.selectedValue.desc;
        const value = target.getAttribute("data-value");
        if (!this.isUiElm()) {
            this.onSelectOptionOfNonUIElm(value);
        } else {
            this.onSelectOptionOfUIElm(value);
        }
        if (this.props.type === this.DROPDOWN_TYPE_SIMPLE) {
            this.click.emit();
        }
        if (oldValue !== this.selectedValue.desc) {
            this.change.emit();
        }
        this.cd.detectChanges();
    }

    onSelectOptionOfUIElm(value: any) {
        if (this.props.type === this.DROPDOWN_TYPE_SIMPLE) {
            const simpUiVal = this.setDrdDscValue(value);
            this.selectedValue = simpUiVal ? simpUiVal : { desc: '', val: '' };
        } else if (this.props.type === this.DROPDOWN_TYPE_WITH_SUB_OPTIONS) {
            const subOptVal: any = this.setDrdValValue((<HTMLInputElement>$('#' + this.id)[0])?.value);
            this.props.value = subOptVal ? subOptVal.val : '';
            this.selectedValue = subOptVal ? subOptVal : { val: '', desc: '' };
        } else {
            this.props.value = value;
        }
    }

    onSelectOptionOfNonUIElm(value: any) {
        if (this.props.type === this.DROPDOWN_TYPE_SIMPLE) {
            this.values[this.elm] = value;
            const simpVal = this.setDrdDscValue(this.values[this.elm])
            this.selectedValue = simpVal ? simpVal : { desc: '', val: '' };
        } else if (this.props.type === this.DROPDOWN_TYPE_WITH_SUB_OPTIONS) {
            const subOptVal: any = this.setDrdDscValue(value)
            this.values[this.elm] = subOptVal ? subOptVal.val : '';
            this.selectedValue = subOptVal ? subOptVal : { val: '', desc: '' };
        } else {
            this.values[this.elm] = value;
        }
    }

    setContent() {
        if (this.row) {
            if (!this.isUiElm()) {
                this.values = this.dataServiceUtils.getMultiRecContent(this.elmData, this.containerId, this.rowIndex);
                this.elm = this.props.id.split("__").pop();
                this.setContentForNonUIElm();
            } else {
                this.setContentForUIElm();
            }
        } else {
            if (!this.isUiElm()) {
                this.values = this.dataServiceUtils.setElmValue(this.props.id);
                this.elm = this.props.id.split("__").pop();
                this.setContentForNonUIElm();
            } else {
                this.setContentForUIElm();
            }
        }
    }

    setContentForNonUIElm() {
        switch (this.props.type) {
            case this.DROPDOWN_TYPE_SIMPLE: {
                const setRowSimpVal = this.setDrdDscValue(this.values[this.elm])
                this.selectedValue = setRowSimpVal ? setRowSimpVal : { desc: '', val: '' };
                break;
            }
            case this.DROPDOWN_TYPE_MULTI_SELECT_CHECKBOX: {
                const setmultival = this.values[this.elm] ? this.setMultiDrdDscValue(this.values[this.elm]) : ''
                this.multiChBxVal = setmultival ? setmultival : '';
                break;
            }
            case this.DROPDOWN_TYPE_WITH_SUB_OPTIONS: {
                const setRowSubOptVal = this.setDrdDscValue(this.values[this.elm]);
                this.selectedValue = setRowSubOptVal ? setRowSubOptVal : { desc: '', val: '' };
                break;
            }
            case this.DROPDOWN_TYPE_AUTOCOMPLETE: {
                this.autoCompleteOptions = $.map(this.staticOptions, function (obj: any) {
                    obj.id = obj.id || obj.val; // replace pk with your identifier
                    obj.text = obj.text || obj.desc
                    return obj;
                });
                break;
            }
            case this.DROPDOWN_TYPE_MULTI_SELECT_TAGS:
                {
                    this.options = {
                        multiple: true,
                        tags: true
                    };
                    this.multiSelectTagsOptions = $.map(this.staticOptions, function (obj: any) {
                        obj.id = obj.id || obj.val;
                        obj.text = obj.text || obj.desc
                        return obj;
                    });
                    break;
                }
            default:
                break;
        }
    }

    setContentForUIElm() {
        switch (this.props.type) {
            case this.DROPDOWN_TYPE_SIMPLE: {
                const setUiRowSimpVal = this.setDrdDscValue(this.props.value);
                this.selectedValue = setUiRowSimpVal ? setUiRowSimpVal : { desc: '', val: '' };
                break;
            }

            case this.DROPDOWN_TYPE_MULTI_SELECT_CHECKBOX: {
                let valml = this.props.value ? this.setMultiDrdDscValue(this.props.value) : '';
                this.multiChBxVal = valml ? valml : '';
                break;
            }

            case this.DROPDOWN_TYPE_WITH_SUB_OPTIONS: {
                const setUiRowSubOptVal = this.setDrdDscValue(this.props.value);
                this.selectedValue = setUiRowSubOptVal ? setUiRowSubOptVal : { desc: '', val: '' };
                break;
            }

            case this.DROPDOWN_TYPE_AUTOCOMPLETE: {
                this.autoCompleteOptions = $.map(this.staticOptions, function (obj: any) {
                    obj.id = obj.id || obj.val;
                    obj.text = obj.text || obj.desc
                    return obj;
                });
                break;
            }

            case this.DROPDOWN_TYPE_MULTI_SELECT_TAGS: {
                this.options = {
                    multiple: true,
                    tags: true
                };
                this.multiSelectTagsOptions = $.map(this.staticOptions, function (obj: any) {
                    obj.id = obj.id || obj.val;
                    obj.text = obj.text || obj.desc
                    return obj;
                });
                break;
            }
            default:
                break;
        }
    }

    isUiElm() {
        return (apz.scrMetaData.elmsMap[this.props.id].ui == "Y") ? true : false;
    }

    isMultiRec() {
        let nodeId = apz.scrMetaData.elmsMap[this.props.id].nodeId;
        let nodesMapData = apz.scrMetaData.nodesMap[nodeId].multiRec;
        return (nodesMapData == "Y") ? true : false;
    }

    iconClickEvent(event: any) {
        this.labelIconClick.emit(event);
    }
    
    bindChangeEvent(event: any) {
		this.change.emit();
	}
}
