
var Bridge = Bridge || {};

Bridge = {
    appKey:123456,
    deviceId : null,
    sessionKey : null,
    isReady : null,
    deviceready : function(successCallback, errorCallback, agrs){
        Bridge.exec(function(data){
            Bridge.sessionKey = data['Session_Key'];
            Bridge.deviceId = data['Device_Id']
            Bridge.isReady = true;
            successCallback();
        }, errorCallback, "deviceReady", "", []);
    },
     getUserDetails: function(successCallback, errorCallback, agrs) {
        Bridge.exec(successCallback, errorCallback, "userDetails", "", []);
    },
    callbacks: {
        // success: null,
        // failure: null
    },
    getRandomNum : function(){
        return (Bridge.deviceId + new Date()/1);
    },
    callbackId: function() {
        return Math.floor(Math.random() * 1000000);
    },
    callbackSuccess: function(callbackId, args) {
        //SUCCESS Fn

        Bridge.callbackFromNative(callbackId, true, args);
    },
    callbackError: function(callbackId, args) {
        //FAILURE Fn
        Bridge.callbackFromNative(callbackId, false, args);
    },
    callbackFromNative: function(callbackId, status, args) {

        if (status) {

            Bridge.callbacks[callbackId].success(args)

        } else {
            Bridge.callbacks[callbackId].failure(args)
        }
        //delete success/failure from list
        if (Bridge.callbacks[callbackId]) {
            delete Bridge.callbacks[callbackId];
        }
    },
    log : function(message) {
        try {
            var randomNum = Bridge.callbackId(), args = [message];
            console.log(args);
            mConnectBridge.exec("console", "log", args, randomNum, Bridge.sessionKey);
        } catch (e) {
            alert(e);
        }
    },
    exec: function(successFn, failFn, serviceName, methodName, args) {
        var randomNum = Bridge.callbackId();
        try {
            
            Bridge.callbacks[randomNum] = {
                success: successFn,
                failure: failFn
            };

            console.log(args)

            mConnectBridge.exec(serviceName, methodName, args, randomNum, Bridge.sessionKey);
        } catch (e) {
            alert(e);
            Bridge.callbackError(randomNum, "Framework load Err")
        }
    }
};

Bridge.deviceready(function(){},function(){},[]);
