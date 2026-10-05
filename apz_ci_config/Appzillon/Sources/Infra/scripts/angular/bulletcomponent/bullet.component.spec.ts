import { TestBed } from '@angular/core/testing';
import {WindowRef, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
import { BulletComponent  } from './bullet.component';
describe('BulletComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      BulletComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

 it('should create the BulletComponent', () => {
   const fixture = TestBed.createComponent(BulletComponent);
   const app = fixture.componentInstance;
   expect(app).toBeTruthy();
 });

it('should invoke life cycle methods()', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Bullets__el_bullets_7': {'ui': "Y", 'container': {}}}}});
  const fixture = TestBed.createComponent(BulletComponent);
  const component = fixture.componentInstance;
  component.row = 0;
  component.props ={id:'angRef__Bullets__el_bullets_7', cntrType:'FORM', attrs:{'typeclass':'PRESENTATIONELEMENT','name':'angRef__Bullets__el_bullets_7','type':'PRESENTATIONELEMENT','id':'1db2fc7c4496bc2aafcb1bf3','pid':'4eaa10304a17aba294c4d27e','widgetcategory':'ELEMENT','widgettype':'Bullets','appearance':'pri','columnalignment':'LEFT','cntrdescreq':'Y','cssclasses':'bullet-css','custom':'Y','datatype':'STRING','headeralignment':'CENTER','labelwidth':'40','mandatory':'N','options':'Y','bulletitems':[{'bulletname':'Bullet 1'},{'bulletname':'Bullet 2'},{'bulletname':'Bullet 3'}],'orderedlist':'Y','parenttype':'FORM','OSTYPE':'SIMULATOR','APPID':'angRef','APPNAME':'angularReference','rowno':-1,'scr':'Bullets'}, widgettype:'Bullets', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb bullet-css'}, labelProps:{'title':'', 'direct':{'id':'angRef__Bullets__el_bullets_7_grp_lbl','htmlFor':'angRef__Bullets__el_bullets_7','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Bullets__el_bullets_7_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.bulletFormlabelWrapperRef.nativeElement).toBeDefined();
});

it('should cover alternate flows of life cycle methods()', () => {
  setApz({'scrMetaData': {'elmsMap': {'angRef__Bullets__el_bullets_7': {'ui': "Y", 'container': {}}}}});
  const fixture = TestBed.createComponent(BulletComponent);
  const component = fixture.componentInstance;
  component.rowIndex = 0;
  component.props ={id:'angRef__Bullets__el_bullets_7', cntrType:'FORM', attrs:{'tooltip':'Tooptip text', 'typeclass':'PRESENTATIONELEMENT','name':'angRef__Bullets__el_bullets_7','type':'PRESENTATIONELEMENT','id':'1db2fc7c4496bc2aafcb1bf3','pid':'4eaa10304a17aba294c4d27e','widgetcategory':'ELEMENT','widgettype':'Bullets','appearance':'pri','columnalignment':'LEFT','cntrdescreq':'Y','cssclasses':'bullet-css','custom':'Y','datatype':'STRING','headeralignment':'CENTER','labelwidth':'40','mandatory':'N','options':'N','bulletitems':[{'bulletname':'Bullet 1'},{'bulletname':'Bullet 2'},{'bulletname':'Bullet 3'}],'orderedlist':'Y','parenttype':'FORM','OSTYPE':'SIMULATOR','APPID':'angRef','APPNAME':'angularReference','rowno':-1,'scr':'Bullets'}, widgettype:'Bullets', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb bullet-css'}, labelProps:{'title':'', 'direct':{'id':'angRef__Bullets__el_bullets_7_grp_lbl','htmlFor':'angRef__Bullets__el_bullets_7','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-40'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'angRef__Bullets__el_bullets_7_li','className':'eic etw-60'}}};
  fixture.detectChanges();
  expect(component.showClass).toEqual(" sno");
  expect(component.tooltipCls).toEqual(" tooltipcls");
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
  expect(component.bulletFormlabelWrapperRef.nativeElement).toBeDefined();
});

it('should invoke ngOnChanges()', () => {
  const fixture = TestBed.createComponent(BulletComponent);
  const component = fixture.componentInstance;
  component.props = {'id': 'test_bullet_1', 'cntrType': 'FORM',  content:{'direct':{'className':'ett-bttn pri med'}}, wrapperClasses:{'className':'eoc srb'}, labelProps:{'direct':{}}, labelWrapper:{'direct':{'className':'etw-40'}}, contentWrapper:{'direct':{'id':'ListAn__MSTList__el_btn_1_li','className':'eic etw-60'}}};
  component.rowIndex = 0;
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
});

it('ngOnChanges() should assign props id if row index is not defined ', () => {
  const fixture = TestBed.createComponent(BulletComponent);
  const component = fixture.componentInstance;
  component.props = {'id': 'test_bullet_1', 'cntrType': 'FORM',  content:{'direct':{'className':'ett-bttn pri med'}}, wrapperClasses:{'className':'eoc srb'}, labelProps:{'direct':{}}, labelWrapper:{'direct':{'className':'etw-40'}}, contentWrapper:{'direct':{'id':'ListAn__MSTList__el_btn_1_li','className':'eic etw-60'}}};
  component.ngOnChanges();
  expect(component.id).toEqual(component.props.id);
});

it('should set elm and values variables in setContent() on the basis of row', () => {
  setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  const fixture = TestBed.createComponent(BulletComponent);
  const component = fixture.componentInstance;
  component.values = [];
  component.elm = 'appId';
  component.props = {id:'InterfaceQuery__i__tbAsmiIntfMaster__appId', attrs: {defaultValue: '12345678912345678'},tdClasses:' pri', apzcontrol:'', cntrType:'NAVBAR', spanWrapper:{'id':'elmnts__Elements__el_cbx_1_span_-1','className':'etb-chek ett-chek hor pri'}, checkBoxWrapper:{'htmlFor':'elmnts__Elements__el_cbx_1','className':''} , content:{'direct':{'className':'',   'enabled':'enabled','original-title':'' , 'Checked':'', 'checkedval':'y' , 'uncheckedval':'n' , 'indeterminateval':'i' , 'type':'CHECKBOX','defaultValue':'','aria-describedby':'','aria-labelledby':'elmnts__Elements__el_cbx_1_lbl','required':''}}, label:{'value':'',direct:{'className':'flb'}}, HintRequired:'N', hintValue :'', widgettype:'CHECKBOX', widgetcategory:'ELEMENT', data:{} , wrapperClasses:{'className':'eoc srb'}, labelProps:{'title':'Checkbox', 'direct':{'id':'elmnts__Elements__el_cbx_1_grp_lbl','htmlFor':'elmnts__Elements__el_cbx_1','className':'flb'}}, labelWrapper:{'direct':{'className':'etw-0'}}, hasLabelIcon:'N', contentWrapper:{'direct':{'id':'elmnts__Elements__el_cbx_1_li','className':'eic etw-100'}}};
  component.elmData = component.apz['scrMetaData']['elmsMap'].InterfaceQuery__i__tbAsmiIntfMaster__appId;
  component.containerId = "appId_lst_1";
  component.value = component.props.attrs.defaultValue;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
  setApz({'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
  component.row = 1;
  mockDataService.getContent.withArgs(component.props.id, component.row, component.rowIndex, component.elmData, component.containerId).and.returnValue({'appId': 'y'});
  component.setContent();
  expect(component.elm).toEqual(component.props.id.split("__").pop());
});

});