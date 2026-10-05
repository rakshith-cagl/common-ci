WinContainer.Signature = (function () {
    var req_id;

    var signaturePad = function (req) {
        req_id = req.id;
        try {
            var divSign = document.createElement('div');
            divSign.id = "signDiv1";
            //divSign.appendChild(can);
            divSign.style.zIndex = 9999999;
            divSign.style.margin = "auto";

            document.body.appendChild(divSign);
            if (WinContainer.isPhone()) {
                divSign.align = "center";
                divSign.style.backgroundColor = 'black';
                divSign.style.display = 'block';
            }
            divSign.style.width = "100%";
            divSign.style.height = "100%";
            divSign.style.position = "fixed";
            divSign.style.top = "0%";
            divSign.style.left = "0%";
            document.body.style.overflowY = "hidden";
            document.body.style.overflowX = "hidden";
            if (WinContainer.isPhone()) {
                divSign.align = "center";
                divSign.style.backgroundColor = 'black';
                divSign.style.display = 'block';
            }
            initControl();
        } catch (e) {
            WinContainer.failureCallback(req_id, "APZ-CNT-212");
            WinContainer.Log.error(e.description);
        }
    }
    function initControl() {
        createControlElements();
        wireButtonEvents();
        canvas = document.getElementById("signatureCanvas");

       canvas.addEventListener("mousedown", pointerDown, false);
       canvas.addEventListener("mouseup", pointerUp, false);
     
        ctx = canvas.getContext("2d");
        ctx.lineCap = "round";

        if (WinContainer.isPhone()) {
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences
                = Windows.Graphics.Display.DisplayOrientations.portrait;
            ctx.lineWidth = 2.0;
            canvas.addEventListener("mouseout", paintDot, false);
        } else {
            ctx.lineWidth = 2.7;
        }
    }

    function createControlElements() {
        var elem = document.getElementById('sigArea');
        if (elem != null)
            elem.parentNode.removeChild(elem);
        var signatureArea = document.createElement("div"),
           labelDiv = document.createElement("div"),
           canvasDiv = document.createElement("div"),
           canvasElement = document.createElement("canvas"),
           buttonsContainer = document.createElement("div"),
           buttonClear = document.createElement("button"),
           buttonAccept = document.createElement("button"),
          buttonClose = document.createElement("button");
        canvasElement.id = "signatureCanvas";
        canvasElement.style.backgroundColor = 'white';
        signatureArea.id = "sigArea";
        
        labelDiv.id = "Label";
        labelDiv.textContent = "Signature Pad";
        if (WinContainer.isPhone()) {
            labelDiv.style.fontSize = "30px";
            labelDiv.style.color = "White";
            labelDiv.style.height = "50px";
            labelDiv.style.marginTop = '10%';
        }
        buttonClear.id = "btnClear";
        buttonClear.textContent = "Clear";

        buttonAccept.id = "btnAccept";
        buttonAccept.textContent = "Accept";

        buttonClose.id = "btnClose";
        buttonClose.textContent = "Close";

        if (WinContainer.isPhone()) {
            buttonClear.style.fontSize = buttonAccept.style.fontSize = buttonClose.style.fontSize = '20px';
            buttonClear.style.width = buttonAccept.style.width = buttonClose.style.width = "30%";
            canvasElement.width = screen.width;
            canvasElement.height = screen.height - 150;
            labelDiv.style.fontSize = "30px";
            labelDiv.style.color = "White";
            labelDiv.style.height = "50px";
            labelDiv.style.marginTop = '10%';
        } else {
            buttonClear.style.height = buttonAccept.style.height = buttonClose.style.height = "30px";
            buttonClear.style.width = buttonAccept.style.width = buttonClose.style.width = "70px";
            canvasElement.width = 476;
            canvasElement.height = 280;
            labelDiv.className = "signatureLabel";
            if (!WinContainer.isPhone()) {
                signatureArea.className = "signatureArea";
                signatureArea.style.position = 'absolute';
                signatureArea.style.top = "50%";
                signatureArea.style.left = "50%";
                signatureArea.style.margin = "-150px 0 0 -240px";
            }
        }

        canvasDiv.appendChild(canvasElement);
        buttonsContainer.appendChild(buttonClear);
        buttonsContainer.appendChild(buttonAccept);
        buttonsContainer.appendChild(buttonClose);

        signatureArea.appendChild(labelDiv);
        signatureArea.appendChild(canvasDiv);
        signatureArea.appendChild(buttonsContainer);

        document.getElementById("signDiv1").appendChild(signatureArea);
        document.getElementById("btnClear").disabled = true;
        document.getElementById("btnAccept").disabled = true;
    }

    function pointerDown(evt) {
        canvas.addEventListener("mousemove", paint, false);
        document.getElementById("btnClear").disabled = false;
        document.getElementById("btnAccept").disabled = false;
        ctx.beginPath();
        ctx.moveTo(evt.offsetX, evt.offsetY);
     
    }

    function pointerUp(evt) {
        canvas.removeEventListener("mousemove", paint);
        paint(evt);
    }
    function paintDot(evt) {
        ctx.fillRect(evt.offsetX, evt.offsetY, 3, 3);
    }

    function paint(evt) {
        ctx.lineTo(evt.offsetX, evt.offsetY);
        ctx.stroke();
    }

    function wireButtonEvents() {
        var btnClear = document.getElementById("btnClear"),
            btnAccept = document.getElementById("btnAccept");
        btnClose = document.getElementById("btnClose");
        btnClear.addEventListener("click", function () {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            document.getElementById("btnClear").disabled = true;
            document.getElementById("btnAccept").disabled = true;
        }, false);

        btnAccept.addEventListener("click", function () {
            if (WinContainer.isPhone()) {
                Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
                Windows.Graphics.Display.DisplayOrientations.none;
            }
            document.body.style.overflowY = "visible";
            document.body.style.overflowX = "visible";
            signCallback();
        }, false);
        btnClose.addEventListener("click", function () {
            if (WinContainer.isPhone()) {
                Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
                Windows.Graphics.Display.DisplayOrientations.none;
            }
            var elem = document.getElementById('signDiv1');
            elem.parentNode.removeChild(elem);
            document.body.style.overflowY = "visible";
            document.body.style.overflowX = "visible";

        }, false);
    }

    function getSignatureImage() {
        // return canvas.msToBlob();
        return canvas.toDataURL();
        //return ctx.getImageData(0, 0, canvas.width, canvas.height).data;
    }

    function signCallback() {
        var base64Data = getSignatureImage();
        base64Data = base64Data.split(",");
        var elem = document.getElementById('signDiv1');
        elem.parentNode.removeChild(elem);
        var res = {};
        res.id = req_id;
        res.encodedImage = base64Data[1];
        WinContainer.successCallback(res);
    }

    return {
        execute:signaturePad
    }

})();
