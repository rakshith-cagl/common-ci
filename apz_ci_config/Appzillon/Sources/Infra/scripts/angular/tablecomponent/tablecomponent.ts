import {Component,OnInit,Input,TemplateRef,ViewChild,ElementRef, ViewEncapsulation, ChangeDetectorRef, DoCheck, 
			AfterViewInit, AfterViewChecked, Output, EventEmitter} from '@angular/core';
import { WindowRef,apz } from '../appzillon.service';
import { DataService } from '../data_angular';
import $ from 'jquery';


@Component({
	selector:'TableComponent',
	templateUrl:"./tablecomponent.html",
    encapsulation:ViewEncapsulation.None})
    
export class TableComponent implements DoCheck,OnInit,AfterViewInit,AfterViewChecked{
	@ViewChild('tableWrapper',{static:false})tableWrapper!: ElementRef;
	@ViewChild('tableEvents',{static:false})tableEventsRef!: ElementRef;
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
	public totalRecs:any;
	public rowDeleted = false;
	
	@Output() preRowClicked = new EventEmitter<object>();
	@Output() postRowClicked = new EventEmitter<object>();
    constructor(public winRef:WindowRef,public cd:ChangeDetectorRef,public dataService:DataService){
    	
	}
	ngOnInit(){
		this.metadata = apz.scrMetaData.containersMap[this.props.id] || {};
		this.curPage = this.metadata.currPage;
		this.rowDeleted = false;
		this.totalRecs = this.metadata.totalRecs;
		this.pagCls = (this.props.paginationStyle === "PAGE1") ? "pgs-ctr" : "pgsize";
		this.staticOptions = this.props.staticOptions ? this.props.staticOptions : [];
		this.apprCls = (this.props.appearance) ? (" " + this.props.appearance) : " pri";
		this.pageNumber = !this.isEmpty(this.metadata)?apz.data.getPageNumbers(this.metadata):0;
		this.startRec= !this.isEmpty(this.metadata)?apz.data.getStartRec(this.props.id):0;
		this.endRec= !this.isEmpty(this.metadata)?apz.data.getLimit(this.props.id):0;
	}

	ngDoCheck(){
		this.metadata = apz.scrMetaData.containersMap[this.props.id] || {};
		if(this.curPage!= this.metadata.currPage){
			this.pageChanged = true;
			this.curPage = this.metadata.currPage;
		}
		 if(this.totalRecs != this.metadata.totalRecs){
			this.rowDeleted = true;
			this.totalRecs = this.metadata.totalRecs;
		} 
		this.pageNumber = !this.isEmpty(this.metadata)?apz.data.getPageNumbers(this.metadata):0;
		this.startRec= !this.isEmpty(this.metadata)?apz.data.getStartRec(this.props.id):0;
		this.endRec= !this.isEmpty(this.metadata)?apz.data.getLimit(this.props.id):0;
		this.getTableRowInfo();
	}
	ngAfterViewChecked(){
		/* avoiding re-initialization of existing records on adding or removing rows */
		if(this.pageChanged){
			this.rows = [];
			this.pageChanged = false;
		}
		if(this.rowDeleted && this.rows.length>0){
			if(this.metadata.currPage == this.metadata.totalPages){
				this.rows.length=0;
			}else if(this.metadata.currPage< this.metadata.totalPages){
				let length= Math.ceil(this.metadata.totalPages/this.metadata.pageSize);
				this.rows.length = length;
			}
			this.rowDeleted = false;
		}
		this.getInitializationContent();
		let tableObj:any = $("#"+this.props.id);
   		if (this.props.tableState == "READONLY" && this.props.responsive == "Y" && ($.fn as any).DataTable?.isDataTable(tableObj)) {
     		 tableObj.DataTable().destroy();
   		}
		this.initRows();
		this.initTable();
	}
	ngAfterViewInit(){
		if(this.tableWrapper){
		Object.assign(this.tableWrapper.nativeElement, this.props.tblWrapper.direct);		
			}
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

	
	initRows(){
        if(this.rows.length>0){
            let tableRows:any[] = this.rows;
            let elmId =this.metadata.elms[0].id+'_0';
            if(document.getElementById(elmId)){
                for (let i = 0; i < tableRows.length; i++) {
                    if (!tableRows[i]) {
                        apz.initRow(this.props.id, "", (i + 1));
                        tableRows[i] = true;
                    }
                }
            }
        }
    }
	initTable(){
		let obj = $("#" + this.props.id + "_table");
		if (this.props.tableState == "READONLY" && this.props.responsive == "Y" && !($.fn as any).DataTable?.isDataTable(obj)) {
		  apz.initDataTables(obj, false);
		}
		if (this.props.nativeTable != "Y" && this.props.responsive != "Y") {
		  apz.initFixedHeaderTable(obj, true);
		}
	}

	getTableRowInfo(){
		let cntrData = apz.data.getPageRecords(this.props.id);
		let firstNode = (this.metadata.nodes)?this.metadata.nodes[0]:"";
		if (cntrData) {
			this.dataRows = cntrData[firstNode];
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

	isEmpty(obj:any){
		return (Object.keys(obj).length==0)?true:false;
	}
}