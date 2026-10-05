function HttpPluginClass() {
    console.log("HttpPluginClass.js: is created");
    this.authClientId = null;
    this.authSecretKey = null;
    this.shiroAuthId = null;
    this.shiroPassword = null;
    this.tokenUrl = null;
    this.serviceUrl = null;
}

HttpPluginClass.prototype.errorFormater = function(errMsg, errorCallback) {
    var err = {
        "message": errMsg
    };
    errorCallback(err);
}

HttpPluginClass.prototype.getCredentials = function(successCallback, errorCallback) {
    var THIS = this;
    try {
        Bridge.exec(function(data) {
            THIS.authClientId = data['Auth_Client_ID'];
            THIS.authSecretKey = data['Auth_Secrete_Key'];
            THIS.shiroAuthId = data['SHIRO_Auth_ID'];
            THIS.shiroPassword = data['SHIRO_Auth_Password'];
            THIS.tokenUrl = data['Token_URL'];
            THIS.serviceUrl = data['Sync_URL'];
            successCallback();
        }, errorCallback, "HttpPlugin", "getCredentials", []);
    } catch (e) {
        THIS.errorFormater("Failed to load HTTP Credentials!", errorCallback)
    }
}

HttpPluginClass.prototype.TokenInvoker = function(successCallback, errorCallback, requestParams) {
    var THIS = this;
    var lErrCallback = function() {
        THIS.errorFormater("Failed to get Oauth Access Token!", errorCallback);
    };

    try {
        THIS.getCredentials(function() {
            var params = JSON.stringify({
                "user_name": requestParams.username,
                "password": requestParams.password,
                "authClientId": THIS.authClientId,
                "authSecretKey": THIS.authSecretKey,
                "shiroAuthId": THIS.shiroAuthId,
                "shiroPassword": THIS.shiroPassword,
                "tokenUrl": THIS.tokenUrl,
                "randomValue": Bridge.getRandomNum()
            });

            var args = [params];

            Bridge.exec(successCallback, errorCallback, "HttpPlugin", "getOauthAccessToken", args);
        }, function(error) {
            lErrCallback();
        });
    } catch (e) {
        lErrCallback();
    }

}

HttpPluginClass.prototype.getOauthAccessToken = function(successCallback, errorCallback, requestParams) {
    var THIS = this;
    if (Bridge.isReady) {
        THIS.TokenInvoker(successCallback, errorCallback, requestParams);
    } else {
        Bridge.deviceready(function() {
                THIS.TokenInvoker(successCallback, errorCallback, requestParams);
            },
            function() {
                THIS.errorFormater("Failed to load session key!", errorCallback);
            }, []);
    }
}

HttpPluginClass.prototype.payloadGenerator = function(Lattachments, isArray){
    var attachmentXML = (isArray)?[]:'<attachments>';

    for (var i = 0, len = Lattachments.length; i < len; i++) {
        var attchPayload = Lattachments[i]['payload'];
        attchPayload = (typeof attchPayload === 'object') ? JSON.stringify(attchPayload) : attchPayload;
        attchPayload = (attchPayload === null || attchPayload === 'null') ? "" : attchPayload;
        var attchJSON = Lattachments[i]['header'];

        var attchHeaderString = "";
        for (var key in attchJSON) {
            if (attchJSON.hasOwnProperty(key)) {
                attchHeaderString += key + ":" + attchJSON[key] + "\n";
            }
        }
        var reqJSON = {
            header: attchHeaderString,
            payload: attchPayload
        }

        if(isArray){
            attachmentXML.push(reqJSON);
        }
        else{
            attachmentXML += "<data>" + JSON.stringify(reqJSON) + "</data>"
        };
    }
    attachmentXML = (isArray)?attachmentXML:attachmentXML+'</attachments>';
    return attachmentXML;
}

HttpPluginClass.prototype.syncHandler = function(method, successCallback, errorCallback, requestParams) {
    var Lurl = requestParams.url;
    var Ltype = requestParams.type;
    var Lboundary = requestParams.boundary;
    var Lheaders = JSON.stringify(requestParams.headers);
    var Lattachments = requestParams.attachments ? requestParams.attachments : [];

    //prepare attachments
    var attachmentXML = this.payloadGenerator(Lattachments, 0);

    var args = [Lurl, Ltype, Lboundary, Lheaders, attachmentXML];
    this.exec(successCallback, errorCallback, "HttpPlugin", method, args);
}

HttpPluginClass.prototype.syncPost = function(successCallback, errorCallback, requestParams) {
    this.syncHandler("syncPost", successCallback, errorCallback, requestParams);
}

HttpPluginClass.prototype.syncGet = function(successCallback, errorCallback, requestParams) {
    this.syncHandler("syncGet", successCallback, errorCallback, requestParams);
}

HttpPluginClass.prototype.asyncPost = function(successCallback, errorCallback, requestParams) { 
    var Lattachments = requestParams['REQUEST_PAYLOAD'] ? requestParams['REQUEST_PAYLOAD'] : [];

    //prepare attachments
    var attachmentXML = this.payloadGenerator(Lattachments, 1);
    
    requestParams['REQUEST_PAYLOAD'] = attachmentXML;
    var asyncParams = JSON.stringify(requestParams);

    var args = [asyncParams];
    this.exec(successCallback, errorCallback, "HttpPlugin", "asyncPost", args);
}

HttpPluginClass.prototype.asyncPostStatus = function(successCallback, errorCallback, requestParams) {
    var args = [requestParams.id];
    this.exec(successCallback, errorCallback, "HttpPlugin", "asyncPostStatus", args);
}

HttpPluginClass.prototype.exec = function(successCallback, errorCallback, serviceName, methodName, args) {
    Bridge.exec(successCallback, errorCallback, serviceName, methodName, args);
}

HttpPluginClass.prototype.clientTransactionId = function(successCallback, errorCallback, jargs){

    var Largs = jargs.appName

    var args = [Largs];
     Bridge.exec(successCallback, errorCallback, "HttpPlugin", "clientTransactionId", args);
}

HttpPluginClass.prototype.syncHandlerXml = function(method, successCallback, errorCallback, requestParams) {
    var Lattachments = requestParams['REQUEST_PAYLOAD'] ? requestParams['REQUEST_PAYLOAD'] : [];

    //prepare attachments
    var attachmentXML = this.payloadGenerator(Lattachments, 1);

    requestParams['REQUEST_PAYLOAD'] = attachmentXML;


    var asyncParams = JSON.stringify(requestParams);


    var args = [asyncParams];
    this.exec(successCallback, errorCallback, "HttpPlugin", "syncPostXml", args);
}
HttpPluginClass.prototype.syncPostXml = function(successCallback, errorCallback, requestParams) {
    this.syncHandlerXml("syncPostXml", successCallback, errorCallback, requestParams);
}

var HttpPlugin = new HttpPluginClass();
