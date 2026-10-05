import { DataService } from './data_angular';
import { WindowRef, setApz } from "./appzillon.service";
describe('DataService', () => {
    let dataAngularService: DataService;
    beforeEach(() => { dataAngularService = new DataService(new WindowRef()); });
  
    it('should set element value for the passed id()', () => {
        setApz(
            {"data": {
                "scrdata": {
                "Databa__InterfaceQuery_Req" : {
                "tbAsmiIntfMaster": [
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "Active_InactiveUsers_Query",
                        "interfaceId": "Active_InactiveUsers_Query"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "ApplicationUsageReport",
                        "interfaceId": "ApplicationUsageReport"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAppUsageReport",
                        "interfaceId": "appzillonAppUsageReport"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAuditLog",
                        "interfaceId": "appzillonAuditLog"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAuthenticationRequest",
                        "interfaceId": "appzillonAuthenticationRequest"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonChangePassword",
                        "interfaceId": "appzillonChangePassword"
                    }
                 
                ]
            }}}, 
            "scrMetaData": {
                "elmsMap":{ Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId: {
                "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId",
                "name": "appId",
                "container": "Databa__Interfaces__InterfaceSearchResults",
                "type": "TEXT",
                "ui": "N",
                "custom": "N",
                "readOnly": "N",
                "email": "N",
                "mask": "",
                "runtimeFormat": "",
                "skipFormat": "N",
                "displayAsLiteral": "N",
                "lovId": "Databa__",
                "lovWidthClass": "lcol",
                "lovMinWidth": "px",
                "staticOptions": [],
                "defVal": "",
                "returnFields": [],
                "bindVariables": [],
                "nodeName": "tbAsmiIntfMaster",
                "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                "ifaceName": "Databa__InterfaceQuery",
                "ifaceId": "Databa__InterfaceQuery",
                "dataType": "STRING",
                "relNode": "",
                "relElm": "",
                "cents": "N",
                "pad": "N",
                "padChar": "",
                "mand": "N",
                "pattern": "",
                "maxDec": null,
                "lenType": "V",
                "maxLen": null,
                "minLen": null,
                "maxVal": null,
                "minVal": null,
                "isArray": "N"
            }}
        , nodesMap: {
            "Databa__InterfaceQuery__i__Databa": {"multiRec": "N", currRec: 0},
            "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req1": {
            "dml": "",
            "name": "Databa__InterfacesDetailQuery_Req",
            "id": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req1",
            "ifaceName": "Databa__InterfacesDetailQuery",
            "ifaceId": "Databa__InterfacesDetailQuery",
            "extName": "Databa__InterfacesDetailQuery_Req",
            "parent": "",
            "multiRec": "N",
            "mrParent": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "relType": "1:1",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [],
            "childs": [
                "Databa__InterfacesDetailQuery__i__tbAsmiIntfMaster"
            ],
            "elms": [],
            "elmsMap": [],
            "currRec": 0
        },"Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req": {
            "dml": "",
            "name": "Databa__InterfacesDetailQuery_Req",
            "id": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "ifaceName": "Databa__InterfacesDetailQuery",
            "ifaceId": "Databa__InterfacesDetailQuery",
            "extName": "Databa__InterfacesDetailQuery_Req",
            "parent": "",
            "multiRec": "N",
            "mrParent": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "relType": "1:1",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [],
            "childs": [
                "Databa__InterfacesDetailQuery__i__tbAsmiIntfMaster"
            ],
            "elms": [],
            "elmsMap": [],
            "currRec": 0
        },Databa__InterfaceQuery__i__tbAsmiIntfMaster: {
            "dml": "",
            "name": "tbAsmiIntfMaster",
            "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
            "ifaceName": "Databa__InterfaceQuery",
            "ifaceId": "Databa__InterfaceQuery",
            "extName": "tbAsmiIntfMaster",
            "parent": "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req",
            "multiRec": "Y",
            "mrParent": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
            "relType": "1:N",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [
                "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req",
                "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req1"
            ],
            "childs": [],
            "elms": [
                {
                    "name": "interfaceId",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__interfaceId",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "interfaceId",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "appId",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "appId",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "description",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__description",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "description",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "authStat",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__authStat",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "authStatus",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                }
            ],
            "elmsMap": [],
            "group": "GRP_Interfaces_1",
            "currRec": 0
        }}}})
    expect(dataAngularService.setElmValue("Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId")).toEqual({});
    });


    it('should set elm value for multi record()', () => {
        setApz(
            {"data": {
                "scrdata": {
                "Databa__InterfaceQuery_Req" : {
                "tbAsmiIntfMaster": [
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "Active_InactiveUsers_Query",
                        "interfaceId": "Active_InactiveUsers_Query"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "ApplicationUsageReport",
                        "interfaceId": "ApplicationUsageReport"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAppUsageReport",
                        "interfaceId": "appzillonAppUsageReport"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAuditLog",
                        "interfaceId": "appzillonAuditLog"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAuthenticationRequest",
                        "interfaceId": "appzillonAuthenticationRequest"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonChangePassword",
                        "interfaceId": "appzillonChangePassword"
                    }
                 
                ]
            }}}, 
            "scrMetaData": {
                "elmsMap":{ Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId: {
                "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId",
                "name": "appId",
                "container": "Databa__Interfaces__InterfaceSearchResults",
                "type": "TEXT",
                "ui": "N",
                "custom": "N",
                "readOnly": "N",
                "email": "N",
                "mask": "",
                "runtimeFormat": "",
                "skipFormat": "N",
                "displayAsLiteral": "N",
                "lovId": "Databa__",
                "lovWidthClass": "lcol",
                "lovMinWidth": "px",
                "staticOptions": [],
                "defVal": "",
                "returnFields": [],
                "bindVariables": [],
                "nodeName": "tbAsmiIntfMaster",
                "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                "ifaceName": "Databa__InterfaceQuery",
                "ifaceId": "Databa__InterfaceQuery",
                "dataType": "STRING",
                "relNode": "",
                "relElm": "",
                "cents": "N",
                "pad": "N",
                "padChar": "",
                "mand": "N",
                "pattern": "",
                "maxDec": null,
                "lenType": "V",
                "maxLen": null,
                "minLen": null,
                "maxVal": null,
                "minVal": null,
                "isArray": "N"
            }}
        , nodesMap: {
            "Databa__InterfaceQuery__i__Databa": {"multiRec": "Y", currRec: 0},
            "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req1": {
            "dml": "",
            "name": "Databa__InterfacesDetailQuery_Req",
            "id": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req1",
            "ifaceName": "Databa__InterfacesDetailQuery",
            "ifaceId": "Databa__InterfacesDetailQuery",
            "extName": "Databa__InterfacesDetailQuery_Req",
            "parent": "",
            "multiRec": "N",
            "mrParent": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "relType": "1:1",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [],
            "childs": [
                "Databa__InterfacesDetailQuery__i__tbAsmiIntfMaster"
            ],
            "elms": [],
            "elmsMap": [],
            "currRec": 0
        },"Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req": {
            "dml": "",
            "name": "Databa__InterfacesDetailQuery_Req",
            "id": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "ifaceName": "Databa__InterfacesDetailQuery",
            "ifaceId": "Databa__InterfacesDetailQuery",
            "extName": "Databa__InterfacesDetailQuery_Req",
            "parent": "",
            "multiRec": "Y",
            "mrParent": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "relType": "1:1",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [],
            "childs": [
                "Databa__InterfacesDetailQuery__i__tbAsmiIntfMaster"
            ],
            "elms": [],
            "elmsMap": [],
            "currRec": 0
        },Databa__InterfaceQuery__i__tbAsmiIntfMaster: {
            "dml": "",
            "name": "tbAsmiIntfMaster",
            "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
            "ifaceName": "Databa__InterfaceQuery",
            "ifaceId": "Databa__InterfaceQuery",
            "extName": "tbAsmiIntfMaster",
            "parent": "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req",
            "multiRec": "Y",
            "mrParent": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
            "relType": "1:N",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [
                "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req",
                "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req1"
            ],
            "childs": [],
            "elms": [
                {
                    "name": "interfaceId",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__interfaceId",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "interfaceId",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "appId",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "appId",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "description",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__description",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "description",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "authStat",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__authStat",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "authStatus",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                }
            ],
            "elmsMap": [],
            "group": "GRP_Interfaces_1",
            "currRec": 0
        }}}})
    expect(dataAngularService.setElmValue("Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId")).toEqual({});
    });


    it('should send the final parent on invoking getFinalNodeIdValue()',() => {
        setApz({scrMetaData: {nodesMap: {'Databa__InterfaceQuery__i__Databa__InterfaceQuery_ReqParent2': {childs: ['child1'], parents: []}}}});
        let parentsArray:any[] = [];
        let nodesMap = {parents: ['Databa__InterfaceQuery__i__Databa__InterfaceQuery_ReqParent1', 'Databa__InterfaceQuery__i__Databa__InterfaceQuery_ReqParent2']}
        dataAngularService.getFinalNodeIdValue(nodesMap, parentsArray)
        expect(parentsArray[0]).toEqual('Databa')
    });


    it('should return window object on invoking nativeWindow()', () => {
        setApz(
            {"data": {
                "scrdata": {
                "Databa__InterfaceQuery_Req" : {
                "tbAsmiIntfMaster": [
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "Active_InactiveUsers_Query",
                        "interfaceId": "Active_InactiveUsers_Query"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "ApplicationUsageReport",
                        "interfaceId": "ApplicationUsageReport"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAppUsageReport",
                        "interfaceId": "appzillonAppUsageReport"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAuditLog",
                        "interfaceId": "appzillonAuditLog"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAuthenticationRequest",
                        "interfaceId": "appzillonAuthenticationRequest"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonChangePassword",
                        "interfaceId": "appzillonChangePassword"
                    }
                 
                ]
            }}}, 
            "scrMetaData": {
                "elmsMap":{ Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId: {
                "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId",
                "name": "appId",
                "container": "Databa__Interfaces__InterfaceSearchResults",
                "type": "TEXT",
                "ui": "N",
                "custom": "N",
                "readOnly": "N",
                "email": "N",
                "mask": "",
                "runtimeFormat": "",
                "skipFormat": "N",
                "displayAsLiteral": "N",
                "lovId": "Databa__",
                "lovWidthClass": "lcol",
                "lovMinWidth": "px",
                "staticOptions": [],
                "defVal": "",
                "returnFields": [],
                "bindVariables": [],
                "nodeName": "tbAsmiIntfMaster",
                "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                "ifaceName": "Databa__InterfaceQuery",
                "ifaceId": "Databa__InterfaceQuery",
                "dataType": "STRING",
                "relNode": "",
                "relElm": "",
                "cents": "N",
                "pad": "N",
                "padChar": "",
                "mand": "N",
                "pattern": "",
                "maxDec": null,
                "lenType": "V",
                "maxLen": null,
                "minLen": null,
                "maxVal": null,
                "minVal": null,
                "isArray": "N"
            }}
        , nodesMap: {
            "Databa__InterfaceQuery__i__Databa": {"multiRec": "N", currRec: 0},
            "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req1": {
            "dml": "",
            "name": "Databa__InterfacesDetailQuery_Req",
            "id": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req1",
            "ifaceName": "Databa__InterfacesDetailQuery",
            "ifaceId": "Databa__InterfacesDetailQuery",
            "extName": "Databa__InterfacesDetailQuery_Req",
            "parent": "",
            "multiRec": "N",
            "mrParent": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "relType": "1:1",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [],
            "childs": [
                "Databa__InterfacesDetailQuery__i__tbAsmiIntfMaster"
            ],
            "elms": [],
            "elmsMap": [],
            "currRec": 0
        },"Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req": {
            "dml": "",
            "name": "Databa__InterfacesDetailQuery_Req",
            "id": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "ifaceName": "Databa__InterfacesDetailQuery",
            "ifaceId": "Databa__InterfacesDetailQuery",
            "extName": "Databa__InterfacesDetailQuery_Req",
            "parent": "",
            "multiRec": "N",
            "mrParent": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "relType": "1:1",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [],
            "childs": [
                "Databa__InterfacesDetailQuery__i__tbAsmiIntfMaster"
            ],
            "elms": [],
            "elmsMap": [],
            "currRec": 0
        },Databa__InterfaceQuery__i__tbAsmiIntfMaster: {
            "dml": "",
            "name": "tbAsmiIntfMaster",
            "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
            "ifaceName": "Databa__InterfaceQuery",
            "ifaceId": "Databa__InterfaceQuery",
            "extName": "tbAsmiIntfMaster",
            "parent": "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req",
            "multiRec": "Y",
            "mrParent": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
            "relType": "1:N",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [
                "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req",
                "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req1"
            ],
            "childs": [],
            "elms": [
                {
                    "name": "interfaceId",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__interfaceId",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "interfaceId",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "appId",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "appId",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "description",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__description",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "description",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "authStat",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__authStat",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "authStatus",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                }
            ],
            "elmsMap": [],
            "group": "GRP_Interfaces_1",
            "currRec": 0
        }}}})
    expect(dataAngularService.setElmValue("Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId")).toEqual({});
    });


    it('should set elm value for a non multi record()', () => {
        setApz(
            {"data": {
                "scrdata": {
                "Databa__InterfaceQuery_Req" : {
                "tbAsmiIntfMaster": [
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "Active_InactiveUsers_Query",
                        "interfaceId": "Active_InactiveUsers_Query"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "ApplicationUsageReport",
                        "interfaceId": "ApplicationUsageReport"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAppUsageReport",
                        "interfaceId": "appzillonAppUsageReport"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAuditLog",
                        "interfaceId": "appzillonAuditLog"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonAuthenticationRequest",
                        "interfaceId": "appzillonAuthenticationRequest"
                    },
                    {
                        "authStat": "Un-Authorized",
                        "appId": "Explor",
                        "description": "appzillonChangePassword",
                        "interfaceId": "appzillonChangePassword"
                    }
                 
                ]
            }}}, 
            "scrMetaData": {
                "elmsMap":{ Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId: {
                "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId",
                "name": "appId",
                "container": "Databa__Interfaces__InterfaceSearchResults",
                "type": "TEXT",
                "ui": "N",
                "custom": "N",
                "readOnly": "N",
                "email": "N",
                "mask": "",
                "runtimeFormat": "",
                "skipFormat": "N",
                "displayAsLiteral": "N",
                "lovId": "Databa__",
                "lovWidthClass": "lcol",
                "lovMinWidth": "px",
                "staticOptions": [],
                "defVal": "",
                "returnFields": [],
                "bindVariables": [],
                "nodeName": "tbAsmiIntfMaster",
                "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                "ifaceName": "Databa__InterfaceQuery",
                "ifaceId": "Databa__InterfaceQuery",
                "dataType": "STRING",
                "relNode": "",
                "relElm": "",
                "cents": "N",
                "pad": "N",
                "padChar": "",
                "mand": "N",
                "pattern": "",
                "maxDec": null,
                "lenType": "V",
                "maxLen": null,
                "minLen": null,
                "maxVal": null,
                "minVal": null,
                "isArray": "N"
            }}
        , nodesMap: {
            "Databa__InterfaceQuery__i__Databa": {"multiRec": "Y", currRec: 0},
            "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req": {
            "dml": "",
            "name": "Databa__InterfacesDetailQuery_Req",
            "id": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "ifaceName": "Databa__InterfacesDetailQuery",
            "ifaceId": "Databa__InterfacesDetailQuery",
            "extName": "Databa__InterfacesDetailQuery_Req",
            "parent": "",
            "multiRec": "Y",
            "mrParent": "Databa__InterfacesDetailQuery__i__Databa__InterfacesDetailQuery_Req",
            "relType": "1:1",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [],
            "childs": [
                "Databa__InterfacesDetailQuery__i__tbAsmiIntfMaster"
            ],
            "elms": [],
            "elmsMap": [],
            "currRec": 0
        },Databa__InterfaceQuery__i__tbAsmiIntfMaster: {
            "dml": "",
            "name": "tbAsmiIntfMaster",
            "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
            "ifaceName": "Databa__InterfaceQuery",
            "ifaceId": "Databa__InterfaceQuery",
            "extName": "tbAsmiIntfMaster",
            "parent": "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req",
            "multiRec": "N",
            "mrParent": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
            "relType": "1:N",
            "ns": "",
            "nsAlias": "",
            "isoTags": "",
            "parents": [
                "Databa__InterfaceQuery__i__Databa__InterfaceQuery_Req",
            ],
            "childs": [],
            "elms": [
                {
                    "name": "interfaceId",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__interfaceId",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "interfaceId",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "appId",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "appId",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "description",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__description",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "description",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                },
                {
                    "name": "authStat",
                    "id": "Databa__InterfaceQuery__i__tbAsmiIntfMaster__authStat",
                    "dml": "",
                    "nodeId": "Databa__InterfaceQuery__i__tbAsmiIntfMaster",
                    "nodeName": "tbAsmiIntfMaster",
                    "ifaceName": "Databa__InterfaceQuery",
                    "ifaceId": "Databa__InterfaceQuery",
                    "extName": "authStatus",
                    "dataType": "STRING",
                    "relNode": "",
                    "relElm": "",
                    "cents": "N",
                    "pad": "N",
                    "padChar": "",
                    "mand": "N",
                    "pattern": "",
                    "maxDec": null,
                    "lenType": "V",
                    "maxLen": null,
                    "minLen": null,
                    "maxVal": null,
                    "minVal": null,
                    "ns": "",
                    "nsAlias": "",
                    "isArray": "N"
                }
            ],
            "elmsMap": [],
            "group": "GRP_Interfaces_1",
            "currRec": 0
        }}}})
    expect(dataAngularService.setElmValue("Databa__InterfaceQuery__i__tbAsmiIntfMaster__appId")).toEqual({});
    });

    it('should return scr data based on the node id and row passed to it on invoking getMultiRecContent()',() => {
        setApz({getDataType: function(param: any) { return "Array";}, data:{ getStartRec: function(param: any) { return 0;}, getDataPointer: function(param1: any, param2: any) {return [{appId: "test"}]}}});
        expect(dataAngularService.getMultiRecContent({nodeId: 'id1'}, "", 0)).toEqual({appId: 'test'});
        setApz({getDataType: function(param: any) { return "String";}, data:{ getStartRec: function(param: any) { return 0;}, getDataPointer: function(param1: any, param2: any) {return "Test"}}});
        expect(dataAngularService.getMultiRecContent({nodeId: 'id1'}, "", 0)).toEqual("Test");
    });


    it('should bind passed events on invoking eventBinding()',() => {
        let elmObj:any = {};
        dataAngularService.eventBinding(elmObj, {click: function() {return true}});
        expect(elmObj['click']).toBeDefined();
    });

    it('isUiElm() should return true when an is an UI element', () => {
        setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_ic_1': {'ui': "Y"}}}});
        expect(dataAngularService.isUiElm('elmnts__Elements__el_ic_1')).toBeTrue();
      });
      
    it('isUiElm() should return false when an is bind to an interface', () => {
    setApz({'scrMetaData': {'elmsMap': {'elmnts__Elements__el_ic_1': {'ui': "N"}}}});
    expect(dataAngularService.isUiElm('elmnts__Elements__el_ic_1')).toBeFalse();
    });

});
