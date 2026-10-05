/**
 * This class is Appzillon base class where call back hook methods will added. And all the user 
 * defined Angular components will extend this class by default and overrides hook methods if required. 
 */
export class AppzillonBaseClass {
    /**
     * This is a pre call back hook for table and list row click.
     * @param rowObject - Row data object holding container ID, row number and event object.
     *                    {'containerId': containerId, 'rowNo': rowNo, 'event': event}
     */
    preRowClicked(rowObject: any) { 
	 //Override this base class method in your component.
    }

    /**
     * This is a post call back hook for table and list row click.
     * @param rowObject - Row data object holding container ID, row number and event object.
     *                    {'containerId': containerId, 'rowNo': rowNo, 'event': event}
     */
    postRowClicked(rowObject: any) { 
	 //Override this base class method in your component.
    }

    /**
     * This is a pre call back hook for bread crumb click action.
     * @param eventTarget - Event target object.
     */
    preBreadcrumbAction(eventTarget: any) {
	 //Override this base class method in your component.
    }

    /**
     * This is a post call back hook for bread crumb click action.
     * @param eventTarget - Event target object.
     */
    postBreadcrumbAction(eventTarget: any) {
	 //Override this base class method in your component.
    }

     /**
     * This is a pre call back hook for collapsible panel click action.
     * @param eventTarget - Event target object.
     */
    preCollapsibleAction(eventTarget: any) {
	 //Override this base class method in your component.
    }

    /**
     * This is a post call back hook for collapsible panel click action.
     * @param eventTarget - Event target object.
     */
    postCollapsibleAction(eventTarget: any) {
	 //Override this base class method in your component.
    }
    
      /**
     * This is a pre call back hook for tab click.
     * @param eventTarget - Event target object.
     */
    preTabAction(eventTarget: any) {
	 //Override this base class method in your component.
    }

    /**
     * This is a post call back hook for tab click.
     * @param eventTarget - Event target object.
     */
    postTabAction(eventTarget: any) {
	  //Override this base class method in your component.
    }
    
    /**
     * This is a pre call back hook for LOV.
     * @param param - Param object holds container and element ID.
     *                    {'containerId': containerId, 'elementId': elementId}
     */
    preCallLov(param: any) {
	   //Override this base class method in your component.
    }

    /**
     * This is a post call back hook for LOV.
     * @param param - Param object holds container and element ID..
     *                    {'containerId': containerId, 'elementId': elementId}
     */
    postCallLov(param: any) {
	  //Override this base class method in your component.
    }
}
