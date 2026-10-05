//--------Plugin files-----------
WinContainer.NFC = (function () {
    var proximityDevice = null;
    var publishedMessageId = -1;
    var subscribedMessageId = -1;
    var keepReceiving = false;
    var nfc = WinContainer.isPhone() ? new AppzillonNFC.NFCPlugin() : null;
    function sendNFC(json) {
        proximityDevice = Windows.Networking.Proximity.ProximityDevice.getDefault();
        if (!proximityDevice) {
            WinContainer.failureCallback(json.id, "APZ-CNT-214", "Device not found");
            WinContainer.Log.fatal("NFC Device Not Found Or Access Denied By User");
            return;
        }
        
        var action = json.action ? json.action.toUpperCase() : null;
        var type = json.type ? json.type.toUpperCase() : null;
        var content = json.content ? json.content : null;


        function msgRecievedHandler(e, value) {
            WinContainer.successCallback(json);
        }
        function publishMessageToDevice(msg) {
            (publishedMessageId != -1) ? proximityDevice.stopPublishingMessage(publishedMessageId) : null;
            var dataWriter = new Windows.Storage.Streams.DataWriter();
            dataWriter.unicodeEncoding = Windows.Storage.Streams.UnicodeEncoding.utf16LE;
            dataWriter.writeString(msg);
            publishedMessageId = proximityDevice.publishBinaryMessage("WindowsUri", dataWriter.detachBuffer(), msgRecievedHandler);
        }
        function publishMessageToTag(msg) {
            /*(publishedMessageId != -1) ? proximityDevice.stopPublishingMessage(publishedMessageId) : null;
            var dataWriter = new Windows.Storage.Streams.DataWriter();
            dataWriter.unicodeEncoding = Windows.Storage.Streams.UnicodeEncoding.utf8;
            dataWriter.writeString(msg);
            publishedMessageId = proximityDevice.publishBinaryMessage("Windows:WriteTag.Appzillon", dataWriter.detachBuffer(), msgRecievedHandler);
            */publishedMessageId = nfc.publishMessageToTag(msg, msgRecievedHandler);
        }
        function publishUrlToDevice(url) {
            (publishedMessageId != -1) ? proximityDevice.stopPublishingMessage(publishedMessageId) : null;
            try {
                var uri = new Windows.Foundation.Uri(url);
                publishedMessageId = proximityDevice.publishUriMessage(uri, msgRecievedHandler);
            } catch (e) {
            }
        }
        function publishUrlToTag(url) {
            (publishedMessageId != -1) ? proximityDevice.stopPublishingMessage(publishedMessageId) : null;
            var dataWriter = new Windows.Storage.Streams.DataWriter();
            dataWriter.unicodeEncoding = Windows.Storage.Streams.UnicodeEncoding.utf16LE;
            dataWriter.writeString(url);
            publishedMessageId = proximityDevice.publishBinaryMessage("WindowsUri:WriteTag", dataWriter.detachBuffer(), msgRecievedHandler);
        }

        switch (action) {
            case 'DEVICE': (type == 'URL') ? publishUrlToDevice(content) : publishMessageToDevice(content);
                break;
            case 'TAG': (type == 'URL') ? publishUrlToTag(content) : publishMessageToTag(content);
                break;
        }
    }

    function receiveNFC(json) {
        proximityDevice = Windows.Networking.Proximity.ProximityDevice.getDefault();
        if (!proximityDevice) {
            WinContainer.failureCallback(json.id, "APZ-CNT-214", "Device not found");
            WinContainer.Log.fatal("NFC Device not Found or Access Denied by user");
            return;
        }

        function subscribeForMessage() {
            keepReceiving = true;
            nfc.subscribeForMessage(messageReceived);
            if (subscribedMessageId === -1) {
                subscribedMessageId = proximityDevice.subscribeForMessage("Windows.Appzillon", messageReceived);
            }
        }
        function messageReceived(device, message) {
            if (!keepReceiving)
                return;
            var res = {};
            res.id = json.id;
            res.text = message;
            WinContainer.successCallback(res);
            setTimeout(function () { nfc.subscribeForMessage(messageReceived); }, 0);
        }
        subscribeForMessage();
    }
    function stopNFC(json) {
        keepReceiving = false;
        var proximityDevice1 = Windows.Networking.Proximity.ProximityDevice.getDefault();
        if (!proximityDevice1) {
            WinContainer.failureCallback(json.id, "APZ-CNT-214", "Device not found");
            WinContainer.Log.fatal("NFC Device Not Found In Use");
            return;
        }
        try {
            proximityDevice.stopSubscribingForMessage(subscribedMessageId);
            subscribedMessageId = -1;
        } catch (e) { }
        try {
            nfc.stopPublishing();
            proximityDevice.stopPublishingMessage(publishedMessageId);
            publishedMessageId = -1;
        } catch (e) { }
        proximityDevice = null;
        WinContainer.successCallback(json);
    };
    return {
        send: sendNFC,
        receive: receiveNFC,
        stop: stopNFC
    }
})();
