//---------SignaturePad.js------------------------
WinContainer.plugin.signaturePad = function (json) {
    var id = json.id;
    try {
        var divSign = document.createElement('div');
        divSign.id = "signDiv1";

        divSign.style.zIndex = 9999999;
        divSign.style.margin = "auto";
        divSign.align = "center";
        document.body.appendChild(divSign);

        divSign.style.width = "100%";
        divSign.style.height = "100%";
        divSign.style.backgroundColor = 'black';
        divSign.style.display = 'block';
        divSign.style.position = "fixed";
        divSign.style.top = "0%";
        divSign.style.left = "0%";

        document.body.style.overflowY = "hidden";
        document.body.style.overflowX = "hidden";
        initControlPhone();

        function initControlPhone() {
            createControlElementsPhone();
            wireButtonEventsPhone();
            canvas = document.getElementById("signatureCanvas");
            canvas.addEventListener("mousedown", pointerDownPhone, false);
            canvas.addEventListener("mouseout", paintDotPhone, false);
            canvas.addEventListener("mouseup", pointerUpPhone, false);
            ctx = canvas.getContext("2d");

            ctx.lineWidth = 2.0;
            ctx.lineCap = "round";
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences
                = Windows.Graphics.Display.DisplayOrientations.portrait;
        }



        function createControlElementsPhone() {
            var elem = document.getElementById('sigArea');
            if (elem != null)
                elem.parentNode.removeChild(elem);
            var signatureArea = document.createElement("div"),
                labelDiv = document.createElement("div"),
                canvasDiv = document.createElement("div"),
                canvasElement = document.createElement("canvas"),
                buttonsContainer = document.createElement("div"),
                buttonClear = document.createElement("button"),
                buttonAccept = document.createElement("button");
                buttonClose = document.createElement("button");


            labelDiv.style.fontSize = "30px";
            labelDiv.style.color = "White";
            labelDiv.style.height = "50px";
            labelDiv.style.marginTop = '10%';
            labelDiv.textContent = "Signature Pad";

            canvasElement.id = "signatureCanvas";
            canvasElement.style.msTouchAction = "none";
            canvasElement.width = screen.width;
            canvasElement.height = screen.height - 150;
            canvasElement.style.backgroundColor = '#FFFFFF';
            canvasElement.style.border = "solid 2px black";
            canvasElement.align = "center";

            buttonClear.id = "btnClear";
            buttonClear.style.fontSize = '20px';
            buttonClear.textContent = "Clear";
            buttonClear.style.width = "30%";

            buttonAccept.id = "btnAccept";
            buttonAccept.textContent = "Accept";
            buttonAccept.style.fontSize = '20px';
            buttonAccept.style.width = "30%";

            buttonClose.id = "btnClose";
            buttonClose.textContent = "Close";
            buttonClose.style.fontSize = '20px';
            buttonClose.style.width = "30%";


            canvasDiv.appendChild(canvasElement);
            buttonsContainer.appendChild(buttonClear);
            buttonsContainer.appendChild(buttonAccept);
            buttonsContainer.appendChild(buttonClose);


            signatureArea.id = "sigArea";
            signatureArea.appendChild(labelDiv);
            signatureArea.appendChild(canvasDiv);
            signatureArea.appendChild(buttonsContainer);

            document.getElementById("signDiv1").appendChild(signatureArea);
			document.getElementById("btnClear").disabled = true;
            document.getElementById("btnAccept").disabled = true;
        }

        function pointerDownPhone(evt) {
			document.getElementById("btnClear").disabled = false;
            document.getElementById("btnAccept").disabled = false;
            ctx.beginPath();
            ctx.moveTo(evt.offsetX, evt.offsetY);
            canvas.addEventListener("mousemove", paintPhone, false);
        }

        function pointerUpPhone(evt) {
            canvas.removeEventListener("mousemove", paintPhone);
            paintPhone(evt);
        }
        function paintDotPhone(evt) {
            ctx.fillRect(evt.offsetX, evt.offsetY, 3, 3);
        }

        function paintPhone(evt) {
            ctx.lineTo(evt.offsetX, evt.offsetY);
            ctx.stroke();
        }

        function wireButtonEventsPhone() {
            var btnClear = document.getElementById("btnClear"),
                btnAccept = document.getElementById("btnAccept");
            btnClose = document.getElementById("btnClose");
            btnClear.addEventListener("click", function () {
                ctx.clearRect(0, 0, canvas.width, canvas.height);
				document.getElementById("btnClear").disabled = true;
                document.getElementById("btnAccept").disabled = true;
            }, false);

            btnAccept.addEventListener("click", function () {
                Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
                    Windows.Graphics.Display.DisplayOrientations.none;
                document.body.style.overflowY = "visible";
                document.body.style.overflowX = "visible";
				signCallbackPhone();
            }, false);
            btnClose.addEventListener("click", function () {
            //    appzillon.plugin.sendAuditLog("SignaturePad", "END");
                var elem = document.getElementById('signDiv1');
                elem.parentNode.removeChild(elem);
                Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
                    Windows.Graphics.Display.DisplayOrientations.none;
                document.body.style.overflowY = "visible";
                document.body.style.overflowX = "visible";

            }, false);
        }

        function getSignatureImagePhone() {
            // return canvas.msToBlob();
            return canvas.toDataURL();
            //return ctx.getImageData(0, 0, canvas.width, canvas.height).data;
        }

        function signCallbackPhone() {
            var base64Data = getSignatureImagePhone();
            base64Data = base64Data.split(",");
            var elem = document.getElementById('signDiv1');
            elem.parentNode.removeChild(elem);

            var data = JSON.stringify({ successMessage: "", path: null, encodedImage: base64Data[1] }, null, " ");
            var json = JSON.parse(data);
            json.id = id;
            WinContainer.successCallback(json);
        }
    } catch (e) {
        WinContainer.failureCallback(id, "APZ-CNT-212");
        WinContainer.Log.error(e.description);
    }
}