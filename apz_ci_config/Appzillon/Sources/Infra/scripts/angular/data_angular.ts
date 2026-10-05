import {Injectable} from '@angular/core';
import {WindowRef,apz} from './appzillon.service';

@Injectable()
export class DataService {
    constructor(public winRef:WindowRef){}
    //Databinding of Angular component
    setElmValue(id:any){
        // expects Id of an element
        let splittedId = id.split("__");
        let elmsMapData = apz.scrMetaData.elmsMap[id];
        let nodeId = elmsMapData.nodeId;
        let nodeName = elmsMapData.nodeName;
        let nodesMapData = apz.scrMetaData.nodesMap[nodeId];
        let parentsArray:any[] = [];
        parentsArray = this.getFinalNodeIdValue(nodesMapData, parentsArray);
        let ifaceId = nodesMapData.ifaceId;
        this.initInterfaceIfUndefined(nodesMapData);
        let data = apz.data.scrdata[ifaceId];
        if(parentsArray.length!=0){
            data = this.handleMultiParentNode(data, parentsArray, nodeName, nodeId);
        }
        if(nodesMapData.multiRec=='Y'){
            return this.handleMultiRecordNode(data, nodesMapData, nodeName, splittedId[3]);

        }else{
            return this.handleNonMultiRecordNode(data, nodeName, splittedId[3]);
        }
    }

    handleMultiRecordNode(data:any, nodesMapData:any, nodeName:string, interfaceId:string) {
        let parentsNodesID= nodesMapData.parent;
            let parentsNodesMapData = apz.scrMetaData.nodesMap[parentsNodesID];
            let currentRec = 0;
            if(parentsNodesMapData?.multiRec== 'Y'){
                if (parentsNodesMapData.currRec != -1) 
                    currentRec = parentsNodesMapData.currRec;
                if(data[currentRec][interfaceId]==undefined) {
					data[currentRec][interfaceId]={};
				}
                return data[currentRec][interfaceId];

            }else{
                if (nodesMapData.currRec != -1) 
                    currentRec = nodesMapData.currRec;
                if(nodeName.indexOf("_Req")!=-1 || nodeName.indexOf("_Res")!=-1){
                    //data[splittedId[3]]=[]
                }else{
                    this.assignIfUndefined(data, interfaceId, [])
                    data = data[interfaceId];
                }
                this.assignIfUndefined(data, currentRec, {})
                return data[currentRec];
            }
    }

    assignIfUndefined(data:any, key: any, valueToBeAssigned: any) {
        if (data[key] == undefined) {
            data[key] = valueToBeAssigned;
        }
    }

    handleNonMultiRecordNode(data:any, nodeName:string, interfaceId:string) {
        if(nodeName.indexOf("_Req")!=-1 || nodeName.indexOf("_Res")!=-1){
            return data;
        }else{
            if (data[interfaceId]==undefined) {
                data[interfaceId] = {};
            }
            return data[interfaceId];
        }
    }



    initInterfaceIfUndefined(nodesMapData: any) {
        let ifaceId = nodesMapData.ifaceId;
        if(apz.data.scrdata[ifaceId]==undefined){
            if(nodesMapData.multiRec=='Y') 
                apz.data.scrdata[ifaceId]=[]
            else 
                    apz.data.scrdata[ifaceId]={};
         }
    }

    handleMultiParentNode(data: any, parentsArray:any, nodeName: string, nodeId: string) {
        while(parentsArray.length>0){
            let rootParent = parentsArray.pop();
            let nodesMapSplitId = nodeId.split(nodeName)[0] + rootParent;
            let parentNode = apz.scrMetaData.nodesMap[nodesMapSplitId];
            let parMultiRec = "N";
            if(parentNode?.multiRec == "Y"){
                parMultiRec = "Y";
            }
            if(data[rootParent]==undefined && parMultiRec != "Y"){
                data[rootParent]={};
           }else if(data[rootParent]==undefined && parMultiRec == "Y"){
                data[rootParent]=[];
                let currentNodeRec= (parentNode.currRec==-1)?0:parentNode.currRec;
                if(data[rootParent][currentNodeRec] == undefined){
                    data[rootParent][currentNodeRec]={};
                }
           }
           data = data[rootParent];
        }
        return data;
    }

    getFinalNodeIdValue(nodesMapData:any,parentsArray:any){
        let nodeParentId="";
        while(nodesMapData.parents.length>1){
            let nodeParents = JSON.parse(JSON.stringify(nodesMapData.parents));
            nodeParentId = nodeParents.pop();
            if(parentsArray.indexOf(nodeParentId.split("__")[3])==-1){
                parentsArray.push(nodeParentId.split("__")[3])
            }
            nodesMapData = apz.scrMetaData.nodesMap[nodeParentId];
        }
       return parentsArray;
    }

    getMultiRecContent(elmData:any,containerId:any,rowIndex:any){
        let values:any = "";
        if(elmData.nodeId){
            let startRec = apz.data.getStartRec(containerId);
            let nodeData = apz.data.getDataPointer([elmData.nodeId], rowIndex + startRec);
            if (nodeData) {
                if (apz.getDataType(nodeData) == "Array") {
                  values = nodeData[rowIndex];
                  
                } else {
                  values = nodeData;
                }
              }
        }
        return values;
    }

    eventBinding(elmObj:any,events:any){
        try {
            for(let keys in events){
                elmObj[keys] = eval(events[keys]);
            } 
        } catch (error) {
            console.log('Failed to bind event', error);
        }
    }

    isUiElm(id: string){
        return (apz.scrMetaData.elmsMap[id].ui=="Y")?true:false;
    }

    getContent(id:any, row: any, rowIndex : any, elmData: any,containerId: any) {
        let values;
        if(row) {
            values = this.getMultiRecContent(elmData,containerId,rowIndex);
        } else {
            if(!this.isUiElm(id)){
                values = this.setElmValue(id);   
            }
        }
        return values;
    }
}