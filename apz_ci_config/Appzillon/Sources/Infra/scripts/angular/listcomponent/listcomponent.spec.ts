import { TestBed } from '@angular/core/testing';
import { ListComponent  } from './listcomponent';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
describe('ListComponent', () => {
 beforeEach(async () => {
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
        ListComponent
     ],
     providers: [DataService, WindowRef]
   }).compileComponents();
 });

 it('should create the ListComponent', () => {
   const fixture = TestBed.createComponent(ListComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

 it('should emit preRowClicked event', () => {
  const fixture = TestBed.createComponent(ListComponent);
  const component = fixture.componentInstance;
  spyOn(component.preRowClicked, 'emit');
  component.beforeRowClicked("c1", 1, {});
  expect(component.preRowClicked.emit).toHaveBeenCalled();
});

it('should emit postRowClicked event', () => {
  const fixture = TestBed.createComponent(ListComponent);
  const component = fixture.componentInstance;
  spyOn(component.postRowClicked, 'emit');
  component.afterRowClicked("c1", 1, {});
  expect(component.postRowClicked.emit).toHaveBeenCalled();
});

it('should return true if passed object is empty else false on invoking isEmpty()', () => {
  const fixture = TestBed.createComponent(ListComponent);
  const component = fixture.componentInstance;
  expect(component.isEmpty({})).toEqual(true);
  expect(component.isEmpty({'k1': 'v1'})).toEqual(false);
});

it('should cover the lifecycle methods', () => {
  setApz({data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return {'test': []}},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'test': {'isChildList': "Y"}, 'angRef__List__ct_lst_1': {'currPage': 1, isChildList: 'Y', 'totalRecs': 10, childs: ["test"], multiRec: "Y"}}}});
  const fixture = TestBed.createComponent(ListComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__List__ct_lst_1' , className:'crt-list pri' , pagesize:'999'  , widgettype:'LIST',  widgetcategory:'CONTAINER' , dynamicpagesize:'N', titleIcon:'icon-list-ul', titleIconSize:' px24', titleValue:'List', containerState:'READONLY', paginationStyle :'', ulWrapper:{'direct':{'className':'pri'}}, listWrapper:{'direct':{'id':'angRef__List__ct_lst_1_row_-1','className':'srb pri wrapped','rowNo':'-1'}}}
  component.pageChanged = true;
  fixture.detectChanges();
  expect(component.pagCls).toEqual("pgsize");
});

it('should cover the alternate flows of lifecycle methods', () => {
  setApz({data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return {'test': []}},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {}}});
  const fixture = TestBed.createComponent(ListComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__List__ct_lst_1' , targetOverlayWrapper: {direct: {targetOverlayId: 1}}, className:'crt-list pri' , pagesize:'999'  , appearance: "pri", staticOptions: [], widgettype:'LIST',  widgetcategory:'CONTAINER' , dynamicpagesize:'N', titleIcon:'icon-list-ul', titleIconSize:' px24', titleValue:'List', containerState:'READONLY', paginationStyle :'PAGE1', ulWrapper:{'direct':{'className':'pri'}}, listWrapper:{'direct':{'id':'angRef__List__ct_lst_1_row_-1','className':'srb pri wrapped','rowNo':'-1'}}}
  fixture.detectChanges();
  expect(component.pagCls).toEqual("pgs-ctr");
});

it('should cover the life cycle method ngDoCheck()', () => {
  setApz({data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return {'test': []}},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'test': {'isChildList': "Y"}, 'angRef__List__ct_lst_1': {'currPage': 1, isChildList: 'Y', 'totalRecs': 10, childs: ["test"], multiRec: "Y"}}}});
  const fixture = TestBed.createComponent(ListComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__List__ct_lst_1' , className:'crt-list pri' , pagesize:'999'  , widgettype:'LIST',  widgetcategory:'CONTAINER' , dynamicpagesize:'N', titleIcon:'icon-list-ul', titleIconSize:' px24', titleValue:'List', containerState:'READONLY', paginationStyle :'', ulWrapper:{'direct':{'className':'pri'}}, listWrapper:{'direct':{'id':'angRef__List__ct_lst_1_row_-1','className':'srb pri wrapped','rowNo':'-1'}}}
  component.curPage = 2;
  component.ngDoCheck();
  expect(component.pageChanged).toEqual(true);
});

it('should cover the life cycle method getTableRowInfo()', () => {
  setApz({initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return {'test': []}},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'angRef__List__ct_lst_1': {'currPage': 1, 'totalRecs': 10, multiRec: "Y"}}}});
  const fixture = TestBed.createComponent(ListComponent);
  const component = fixture.componentInstance;
  component.props = {id:'angRef__List__ct_lst_1' , className:'crt-list pri' , pagesize:'999'  , appearance: "pri", staticOptions: [], widgettype:'LIST',  widgetcategory:'CONTAINER' , dynamicpagesize:'N', titleIcon:'icon-list-ul', titleIconSize:' px24', titleValue:'List', containerState:'READONLY', paginationStyle :'PAGE1', ulWrapper:{'direct':{'className':'pri'}}, listWrapper:{'direct':{'id':'angRef__List__ct_lst_1_row_-1','className':'srb pri wrapped','rowNo':'-1'}}}
  component.metadata = {'nodes': ['test']};
  component.getListRowInfo();
  expect(component.dataRows).toEqual([]);
});


it('should initialise table rows on invoking getInitializationContent()', () => {
  setApz({initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return {'test': []}},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'angRef__Table__ct_tbl_2': {'currPage': 1, 'totalRecs': 10, multiRec: "Y"}}}});
  const fixture = TestBed.createComponent(ListComponent);
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

it('should initialise rows on invoking initRows()', () => {
  setApz({initFixedHeaderTable: function(param1: any, param2: any) {return true;}, data: {initFixedHeaderTable: function(param1: any, param2: any) {return true;}, getPageRecords: function(param: any) {return {'test': []}},getPageNumbers: function(param: any) {return []}, getStartRec: function(param: any) {return 1}, getLimit: function(param: any) {return 5}}, 'scrMetaData': {'containersMap': {'angRef__Table__ct_tbl_2': {'currPage': 1, 'totalRecs': 10, multiRec: "Y"}}}});
  const fixture = TestBed.createComponent(ListComponent);
  const component = fixture.componentInstance;
  component.rows = [0, 1];
  component.metadata = {elms: [{id: 'test'}]};
  component.initRows();
  expect(component.rows.length).toBeGreaterThan(0); //Need to find a better way to check
});


});