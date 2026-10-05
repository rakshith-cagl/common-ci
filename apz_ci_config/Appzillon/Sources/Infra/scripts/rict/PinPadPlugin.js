function PinPadPluginClass() {
    console.log("PinPadPluginClass.js: is created");
}
PinPadPluginClass.prototype.loadMasterKey = function (successCallback, errorCallback, requestParams) {

    var master_key = requestParams.master_key;
    var master_key_length = requestParams.master_key_length;
    var pin_mk_index = requestParams.pin_mk_index;
    var args = [master_key,master_key_length,pin_mk_index];

    Bridge.exec(successCallback, errorCallback, "PinPad", "loadMasterKey", args);
}

PinPadPluginClass.prototype.loadSessionKey = function (successCallback, errorCallback, requestParams) {
      var p_sk_key = requestParams.p_sk_key;
    var sk_length = requestParams.sk_length;
    var session_key_index = requestParams.session_key_index;
    var master_key_index = requestParams.master_key_index;
    var args = [p_sk_key,sk_length,session_key_index,master_key_index];

    Bridge.exec(successCallback, errorCallback, "PinPad", "loadSessionKey", args);
}

PinPadPluginClass.prototype.getEncPinBlock = function (successCallback, errorCallback, requestParams) {
   var card_number = requestParams.card_number;
    var card_length = requestParams.card_length;
    var session_key_index =requestParams.session_key_index;
    var amount = requestParams.amount;
    var args = [card_number,card_length,session_key_index,amount];

    Bridge.exec(successCallback, errorCallback, "PinPad", "getEncPinBlock", args);
}

var PinPadPlugin = new PinPadPluginClass();
