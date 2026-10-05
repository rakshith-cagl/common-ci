 import { TestBed } from '@angular/core/testing';
 import {WindowRef,apz, setApz} from '../../../../appzillon/scripts/angular/appzillon.service';
 import {DataService} from '../../../../appzillon/scripts/angular/data_angular';
 import { BadgeComponent } from './badge.component';
 describe('BadgeComponent', () => {
  let mockDataService: jasmine.SpyObj<DataService>;
 beforeEach(async () => {
  const spy = jasmine.createSpyObj('dataService', ['setElmValue', 'eventBinding', 'getMultiRecContent', 'getContent']);
   await TestBed.configureTestingModule({
     imports: [
     ],
     declarations: [
      BadgeComponent
     ],
     providers: [WindowRef, {
      provide: DataService,
      useValue: spy
   }]
   }).compileComponents();
   mockDataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
 });

  it('should create the BadgeComponent', () => {
    const fixture = TestBed.createComponent(BadgeComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should invoke life cycle methods', () => {
    setApz({'scrMetaData': {'elmsMap': {'test_badge_id': {'ui': "Y"}}}});
    const fixture = TestBed.createComponent(BadgeComponent);
    const component = fixture.componentInstance;
    component.props = {'id': 'test_badge_id', 'cntrType': 'FORM',  content:{'direct':{'className':'ett-bttn pri med'}}, wrapperClasses:{'className':'eoc srb'}, labelProps:{'direct':{}}, labelWrapper:{'direct':{'className':'etw-40'}}, contentWrapper:{'direct':{'id':'ListAn__MSTList__el_btn_1_li','className':'eic etw-60'}}};
    component.rowIndex = 1;
    fixture.detectChanges();
    expect(component.contentRef.nativeElement).toBeDefined();
  });

  it('should cover alternate flows', () => {
    setApz({'scrMetaData': {'elmsMap': {'test_badge_id': {'ui': "Y"}}}});
    const fixture = TestBed.createComponent(BadgeComponent);
    const component = fixture.componentInstance;
    component.props = {'id': 'test_badge_id', 'cntrType': 'FORM',  content:{'direct':{'className':'ett-bttn pri med'}}, wrapperClasses:{'className':'eoc srb'}, labelProps:{'direct':{}}, labelWrapper:{'direct':{'className':'etw-40'}}, contentWrapper:{'direct':{'id':'ListAn__MSTList__el_btn_1_li','className':'eic etw-60'}}};
    fixture.detectChanges();
    expect(component.contentRef.nativeElement).toBeDefined();
  });

  it('should invoke ngOnChanges()', () => {
    const fixture = TestBed.createComponent(BadgeComponent);
    const component = fixture.componentInstance;
    component.props = {'id': 'test_badge_id', 'cntrType': 'FORM',  content:{'direct':{'className':'ett-bttn pri med'}}, wrapperClasses:{'className':'eoc srb'}, labelProps:{'direct':{}}, labelWrapper:{'direct':{'className':'etw-40'}}, contentWrapper:{'direct':{'id':'ListAn__MSTList__el_btn_1_li','className':'eic etw-60'}}};
    component.ngOnChanges();
    expect(component.id).toEqual(component.props.id);
    expect(component.index).toEqual(-1);
    component.rowIndex = 0;
    component.ngOnChanges();
    expect(component.id).toEqual(component.props.id + "_"+component.rowIndex);
    expect(component.index).toEqual(component.rowIndex);
  });

  it('should set elm and values variables in setContent() on the basis of row', () => {
    setApz({isNull: function(param:any) {return false}, 'scrMetaData': {'elmsMap': {'InterfaceQuery__i__tbAsmiIntfMaster__appId': {'ui': "N"}}}});
    const fixture = TestBed.createComponent(BadgeComponent);
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