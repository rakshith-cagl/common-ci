import { TestBed } from '@angular/core/testing';
import { TableComponent  } from './tablecomponent';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
describe('TableComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        TableComponent
     ],
     providers: [WindowRef, DataService]
   }).compileComponents();
 });

 it('should create the TableComponent', () => {
   const fixture = TestBed.createComponent(TableComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('should return true if passed object is empty else false on invoking isEmpty()', () => {
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  expect(component.isEmpty({})).toEqual(true);
  expect(component.isEmpty({'k1': 'v1'})).toEqual(false);
});

it('should emit preRowClicked event', () => {
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  spyOn(component.preRowClicked, 'emit');
  component.beforeRowClicked("c1", 1, {});
  expect(component.preRowClicked.emit).toHaveBeenCalled();
});

it('should emit postRowClicked event', () => {
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  spyOn(component.postRowClicked, 'emit');
  component.afterRowClicked("c1", 1, {});
  expect(component.postRowClicked.emit).toHaveBeenCalled();
});

it('should cover the lifecycle methods', () => {
  setApz({initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return []},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'angRef__Table__ct_tbl_2': {'currPage': 1, 'totalRecs': 10, multiRec: "Y"}}}});
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  component.props = { id:'angRef__Table__ct_tbl_2',  className:'tabl fixedheader', role:'grid', tableWrapperClass:'crb-tabl crt-tabl pri var1 u-relative', expandableIcon:'', collapsibleIcon:'', dynamicpagesize:'N', widgettype :'TABLE', widgetcategory:'CONTAINER' , tblWrapper:{direct:{className:'tabl-ctr', style:{ 'height':'250px' }}}, trClasses:' pri ', titleIcon:'icon-table', titleIconSize:' px24', titleValue:'Table - Primary', paginationStyle:'PAGE3', nativeTable:'N', searchable:'Y', tableState:'', responsive:'N', rowSelectorName:'ct_tbl_2', rowSelector:'Y', rowSelectorType:'CHECKBOX', appearance:'pri', rowSelectorClass:'group-checkable', advancedTabClass:'dticon control dt-cssicon', expandablePosition:'RIGHT',headerWrappers:[{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_1','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_1_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_2','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_2_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_3','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_3_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_4','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_4_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_5','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_5_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'}]};
  fixture.detectChanges();
  expect(component.pagCls).toEqual("pgsize");
});

it('should cover the alternate flows of life cycle methods', () => {
  setApz({initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return []},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {}}});
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  component.props = { id:'angRef__Table__ct_tbl_2', staticOptions: [], className:'tabl fixedheader', role:'grid', tableWrapperClass:'crb-tabl crt-tabl pri var1 u-relative', expandableIcon:'', collapsibleIcon:'', dynamicpagesize:'N', widgettype :'TABLE', widgetcategory:'CONTAINER' , tblWrapper:{direct:{className:'tabl-ctr', style:{ 'height':'250px' }}}, trClasses:' pri ', titleIcon:'icon-table', titleIconSize:' px24', titleValue:'Table - Primary', paginationStyle:'PAGE1', nativeTable:'N', searchable:'Y', tableState:'', responsive:'Y', rowSelectorName:'ct_tbl_2', rowSelector:'Y', rowSelectorType:'CHECKBOX', rowSelectorClass:'group-checkable', advancedTabClass:'dticon control dt-cssicon', expandablePosition:'RIGHT',headerWrappers:[{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_1','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_1_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_2','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_2_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_3','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_3_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_4','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_4_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_5','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_5_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'}]};
  fixture.detectChanges();
  expect(component.pagCls).toEqual("pgs-ctr");
});


it('should cover the life cycle method ngDoCheck()', () => {
  setApz({initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return {'test': []}},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'angRef__Table__ct_tbl_2': {'currPage': 1, 'totalRecs': 10, multiRec: "Y"}}}});
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  component.props = { id:'angRef__Table__ct_tbl_2', staticOptions: [], className:'tabl fixedheader', role:'grid', tableWrapperClass:'crb-tabl crt-tabl pri var1 u-relative', expandableIcon:'', collapsibleIcon:'', dynamicpagesize:'N', widgettype :'TABLE', widgetcategory:'CONTAINER' , tblWrapper:{direct:{className:'tabl-ctr', style:{ 'height':'250px' }}}, trClasses:' pri ', titleIcon:'icon-table', titleIconSize:' px24', titleValue:'Table - Primary', paginationStyle:'PAGE1', nativeTable:'N', searchable:'Y', tableState:'', responsive:'Y', rowSelectorName:'ct_tbl_2', rowSelector:'Y', rowSelectorType:'CHECKBOX', rowSelectorClass:'group-checkable', advancedTabClass:'dticon control dt-cssicon', expandablePosition:'RIGHT',headerWrappers:[{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_1','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_1_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_2','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_2_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_3','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_3_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_4','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_4_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_5','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_5_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'}]};
  component.curPage = 2;
  component.totalRecs = 12;
  component.ngDoCheck();
  expect(component.pageChanged).toEqual(true);
});


it('should cover the life cycle method ngAfterViewChecked()', () => {
  setApz({initRow: function(param1: any, param2: any, param3: any) {}, initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return []},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'angRef__Table__ct_tbl_2': {'currPage': 1, 'totalPages': 1,'totalRecs': 10, multiRec: "Y", elms: [{'id': 'test'}]}}}});
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  component.props = { id:'angRef__Table__ct_tbl_2', staticOptions: [], className:'tabl fixedheader', role:'grid', tableWrapperClass:'crb-tabl crt-tabl pri var1 u-relative', expandableIcon:'', collapsibleIcon:'', dynamicpagesize:'N', widgettype :'TABLE', widgetcategory:'CONTAINER' , tblWrapper:{direct:{className:'tabl-ctr', style:{ 'height':'250px' }}}, trClasses:' pri ', titleIcon:'icon-table', titleIconSize:' px24', titleValue:'Table - Primary', paginationStyle:'PAGE1', nativeTable:'N', searchable:'Y', tableState:'', responsive:'Y', rowSelectorName:'ct_tbl_2', rowSelector:'Y', rowSelectorType:'CHECKBOX', rowSelectorClass:'group-checkable', advancedTabClass:'dticon control dt-cssicon', expandablePosition:'RIGHT',headerWrappers:[{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_1','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_1_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_2','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_2_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_3','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_3_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_4','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_4_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_5','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_5_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'}]};
  component.pageChanged = true;
  component.metadata = {'currPage': 1, 'totalPages': 1,'totalRecs': 10, multiRec: "Y", elms: [{'id': 'test'}]};
  component.ngAfterViewChecked();
  expect(component.rows).toEqual([]);
});

it('should cover the alternate flows of life cycle method ngAfterViewChecked()', () => {
  setApz({initRow: function(param1: any, param2: any, param3: any) {}, initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return []},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'angRef__Table__ct_tbl_2': {'currPage': 1, 'totalPages': 1,'totalRecs': 10, multiRec: "Y", elms: [{'id': 'test'}]}}}});
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  component.props = { id:'angRef__Table__ct_tbl_2', staticOptions: [], className:'tabl fixedheader', role:'grid', tableWrapperClass:'crb-tabl crt-tabl pri var1 u-relative', expandableIcon:'', collapsibleIcon:'', dynamicpagesize:'N', widgettype :'TABLE', widgetcategory:'CONTAINER' , tblWrapper:{direct:{className:'tabl-ctr', style:{ 'height':'250px' }}}, trClasses:' pri ', titleIcon:'icon-table', titleIconSize:' px24', titleValue:'Table - Primary', paginationStyle:'PAGE1', nativeTable:'N', searchable:'Y', tableState:'', responsive:'Y', rowSelectorName:'ct_tbl_2', rowSelector:'Y', rowSelectorType:'CHECKBOX', rowSelectorClass:'group-checkable', advancedTabClass:'dticon control dt-cssicon', expandablePosition:'RIGHT',headerWrappers:[{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_1','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_1_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_2','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_2_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_3','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_3_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_4','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_4_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_5','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_5_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'}]};
  component.metadata = {'currPage': 1, 'totalPages': 1,'totalRecs': 10, multiRec: "Y", elms: [{'id': 'test'}]};
  component.rows = [{}];
  component.rowDeleted = true;
  component.ngAfterViewChecked();
  expect(component.rowDeleted).toEqual(false);
});

it('should cover the alternate flows of life cycle ngAfterViewChecked()', () => {
  setApz({initDataTables: function(param1: any, param2: any) {} ,initRow: function(param1: any, param2: any, param3: any) {}, initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return []},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'angRef__Table__ct_tbl_2': {'currPage': 1, 'totalPages': 1,'totalRecs': 10, multiRec: "Y", elms: [{'id': 'test'}]}}}});
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  component.rowDeleted = true;
  component.rows = [{}];
  component.props = { id:'angRef__Table__ct_tbl_2', staticOptions: [], className:'tabl fixedheader', role:'grid', tableWrapperClass:'crb-tabl crt-tabl pri var1 u-relative', expandableIcon:'', collapsibleIcon:'', dynamicpagesize:'N', widgettype :'TABLE', widgetcategory:'CONTAINER' , tblWrapper:{direct:{className:'tabl-ctr', style:{ 'height':'250px' }}}, trClasses:' pri ', titleIcon:'icon-table', titleIconSize:' px24', titleValue:'Table - Primary', paginationStyle:'PAGE1', nativeTable:'N', searchable:'Y', tableState:'READONLY', responsive:'Y', rowSelectorName:'ct_tbl_2', rowSelector:'Y', rowSelectorType:'CHECKBOX', rowSelectorClass:'group-checkable', advancedTabClass:'dticon control dt-cssicon', expandablePosition:'RIGHT',headerWrappers:[{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_1','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_1_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_2','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_2_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_3','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_3_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_4','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_4_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_5','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_5_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'}]};
  component.metadata = {'currPage': 1, 'totalPages': 3, 'pageSize': 3, 'totalRecs': 10, multiRec: "Y", elms: [{'id': 'test'}]};
  component.ngAfterViewChecked();
  expect(component.rows.length).toEqual(1);
});

it('should cover the life cycle method getTableRowInfo()', () => {
  setApz({initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return {'test': []}},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'angRef__Table__ct_tbl_2': {'currPage': 1, 'totalRecs': 10, multiRec: "Y"}}}});
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  component.props = { id:'angRef__Table__ct_tbl_2', staticOptions: [], className:'tabl fixedheader', role:'grid', tableWrapperClass:'crb-tabl crt-tabl pri var1 u-relative', expandableIcon:'', collapsibleIcon:'', dynamicpagesize:'N', widgettype :'TABLE', widgetcategory:'CONTAINER' , tblWrapper:{direct:{className:'tabl-ctr', style:{ 'height':'250px' }}}, trClasses:' pri ', titleIcon:'icon-table', titleIconSize:' px24', titleValue:'Table - Primary', paginationStyle:'PAGE1', nativeTable:'N', searchable:'Y', tableState:'', responsive:'Y', rowSelectorName:'ct_tbl_2', rowSelector:'Y', rowSelectorType:'CHECKBOX', rowSelectorClass:'group-checkable', advancedTabClass:'dticon control dt-cssicon', expandablePosition:'RIGHT',headerWrappers:[{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_1','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_1_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_2','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_2_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_3','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_3_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_4','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_4_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'},{headerWrapper:{'direct':{'lovid':'angRef__Table__el_txt_5','className':'sorting  pri  ','aria-controls':'angRef__Table__ct_tbl_2_table','aria-label':'Input','style':{}}} , headerEvent:{'onclick' : '($event)=>{apz.sortAction(`angRef__Table__ct_tbl_2`,`angRef__Table__el_txt_5_0`, $event.target)}'}, headerTitle:'Input', tdClasses:' pri'}]};
  component.metadata = {'nodes': ['test']};
  component.getTableRowInfo();
  expect(component.dataRows).toEqual([]);
});

it('should initialise table rows on invoking getInitializationContent()', () => {
  setApz({initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return {'test': []}},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'angRef__Table__ct_tbl_2': {'currPage': 1, 'totalRecs': 10, multiRec: "Y"}}}});
  const fixture = TestBed.createComponent(TableComponent);
  const component = fixture.componentInstance;
  component.dataRows = [0, 1];
  component.rows = [1, 1];
  component.metadata = {'multiRec': "Y"};
  component.getInitializationContent();
  expect(component.rows[0]).toEqual(true);
  component.rows = [0, 1];
  component.getInitializationContent();
  expect(component.rows[0]).toEqual(false);
});

});