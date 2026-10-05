import {Component,Input,OnInit, TemplateRef,ViewChild,ElementRef, ViewEncapsulation, ChangeDetectorRef, 
	DoCheck, AfterViewInit, Output, EventEmitter } from '@angular/core';
import {DataService} from '../data_angular';
import { WindowRef,apz } from '../appzillon.service';

@Component({
	selector:'ListComponent',
	templateUrl:"./listcomponent.html",
    encapsulation:ViewEncapsulation.None
})

export class ListComponent implements OnInit, AfterViewInit,DoCheck{
    @ViewChild('ulWrapper',{static:false})ulWrapper!: ElementRef;
    @ViewChild('targetOverlayWrapper',{static:false})targetOverlayWrapper!: ElementRef;
    @ViewChild('listWrapper',{static:false})listWrapper!: ElementRef;
    @Input() templateRef!: TemplateRef<any>;
    @Input()
    public props:any;
    @Input()
    public rowIndex:any;
    public metadata:any;
	public apz = apz;
	public dataRows:any=null;
    public rows:any[] = [];
    public staticOptions=[];
	public pagCls:any;
	public apprCls:any;
	public pageNumber:any;
	public startRec:any;
	public endRec :any;
	public curPage:any;
	public pageChanged = false;
	@Output() preRowClicked = new EventEmitter<object>();
	@Output() postRowClicked = new EventEmitter<object>();
    constructor(public winRef:WindowRef,public cd:ChangeDetectorRef,public dataService:DataService){}

    ngOnInit(){
		this.metadata = apz.scrMetaData.containersMap[this.props.id] || {};
		this.curPage = this.metadata.currPage;
		let childs = this.metadata.childs;
		if(childs && childs.length>0){
			for(let eachChild of childs) {
				apz.scrMetaData.containersMap[eachChild].isChildList = "Y";
			}
		}
		this.pagCls = (this.props.paginationStyle === "PAGE1") ? "pgs-ctr" : "pgsize";
		this.pageNumber = !this.isEmpty(this.metadata)?apz.data.getPageNumbers(this.metadata):0;
		this.staticOptions = this.props.staticOptions ? this.props.staticOptions : [];
		this.apprCls = (this.props.appearance) ? (" " + this.props.appearance) : " pri";
		this.startRec= !this.isEmpty(this.metadata)?apz.data.getStartRec(this.props.id):0;
		this.endRec= !this.isEmpty(this.metadata)?apz.data.getLimit(this.props.id):0;
    }
    ngDoCheck(){
		this.metadata = apz.scrMetaData.containersMap[this.props.id] || {};
		if(this.curPage!= this.metadata.currPage){
			this.curPage = this.metadata.currPage;
			this.pageChanged = true;
		}
		this.pageNumber = !this.isEmpty(this.metadata)?apz.data.getPageNumbers(this.metadata):0;
		this.startRec= !this.isEmpty(this.metadata)?apz.data.getStartRec(this.props.id):0;
		this.endRec= !this.isEmpty(this.metadata)?apz.data.getLimit(this.props.id):0;
		this.getListRowInfo();
    }
    ngAfterViewChecked(){
		if(this.pageChanged){
			this.rows = [];
			this.pageChanged = false;
		}
		if(this.metadata.isChildList=='Y'){
			this.rows = [];
		}
		this.getInitializationContent();
		this.initRows();
	}
    initRows(){
        if(this.rows.length>0){
            let listRows:any[] = this.rows;
            let elmId =this.metadata.elms[0].id+'_0';
            if(document.getElementById(elmId)){
                for (let i = 0; i < listRows.length; i++) {
                    if (!listRows[i]) {
                        apz.initRow(this.props.id, "", (i + 1));
                        listRows[i] = true;
                    }
                }
            }
        }
    }
    getListRowInfo(){
		let cntrData = apz.data.getPageRecords(this.props.id);
		if(this.metadata.nodes){
			let firstNode = this.metadata.nodes[0];
			if(firstNode && cntrData){
				this.dataRows = cntrData[firstNode];
			}
		}
	}
	getInitializationContent(){
		if (this.dataRows && this.metadata.multiRec == "Y") {
			this.dataRows.map((row:any, index:any) => {
				if (this.rows[index]) {
				this.rows[index] = true;
				} else {
				this.rows[index] = false;
				}
			})
			this.rows.length = this.dataRows.length;
		  }
	}

    ngAfterViewInit(){
        if(this.ulWrapper){
			Object.assign(this.ulWrapper.nativeElement, this.props.ulWrapper.direct);
        }
        if(this.targetOverlayWrapper){
            Object.assign(this.targetOverlayWrapper.nativeElement,this.props.targetOverlayWrapper.direct)
        }
        if(this.listWrapper){
			Object.assign(this.listWrapper.nativeElement,this.props.listWrapper.direct);
        }
	}
	isEmpty(obj:any){
		return (Object.keys(obj).length==0)?true:false;
	}

	//Appzillon row click call back method 
	beforeRowClicked(containerId:any, rowNo:any, event:any) {
		this.preRowClicked.emit({'containerId': containerId, 'rowNo': rowNo, 'event': event});
		return true;
	}

	//Appzillon row click call back method 
	afterRowClicked(containerId:any, rowNo:any, event:any) {
		this.postRowClicked.emit({'containerId': containerId, 'rowNo': rowNo, 'event': event});
	}
}