(function () {
    var currPage = 1,
        numPages = 0,
        thePDF = null,
        zoomLevel = 4,
        pdfDiv = null,
        zoomValue = null;;

    function zoom(op) {
        if (op == "+") {
            if (zoomLevel < 15)
                zoomLevel++;
            else
                return;
        } else {
            if (zoomLevel > 1)
                zoomLevel--;
            else
                return;
        }
        var canvasArray = document.getElementById("pdfDiv").getElementsByTagName("img");
        var num = 1, can;
        for (can in canvasArray) {
            var canvas = canvasArray[can];
            if (num > numPages)
                break;
            if (can.indexOf("ms") >= 0) {
                canvas.style.width = zoomValue[zoomLevel] + "px";
                num++;
            }
        }
    }


    function handlePages(page) {

        var viewport = page.getViewport(2);
        var canvas = document.createElement("canvas");
        canvas.style.display = "block";
        var context = canvas.getContext('2d');
        canvas.height = viewport.height;
        canvas.width = viewport.width;

        page.render({ canvasContext: context, viewport: viewport }).then(
            function () {
                var a = canvas.msToBlob();
                var zoomMin = document.createElement("img");
                zoomMin.style.width = screen.height < screen.width ? screen.height + "px" : screen.width + "px";
                zoomMin.style.height = "auto";
                zoomMin.style.padding = "20px 20px";
                zoomMin.style.margin = "0 auto";

                zoomMin.src = URL.createObjectURL(a, { oneTimeOnly: true });
                pdfDiv.appendChild(zoomMin);

                currPage++;
                if (thePDF !== null && currPage <= numPages) {
                    thePDF.getPage(currPage).then(handlePages);
                } else {
                    thePDF.destroy();
                }
            }, function (e) {
                var a = e;
            });
    }
    function closePdf() {
        document.body.removeChild(pdfDiv1);
        currPage = 1;
        thePDF = null;
        pdfDiv = null;
    }
    function init() {
        currPage = 1;
        numPages = 0;
        thePDF = null;
        zoomLevel = 4;
        zoomValue = [];
        zoomValue[0] = 100;
        zoomValue[1] = 200;
        zoomValue[2] = 300;
        zoomValue[3] = 400;
        zoomValue[4] = 500;
        var i = 5;
        while (i < 15) {
            zoomValue[i] = zoomValue[i - 1] * 1.2;
            i++;
        }
    }

    function readPdf(json) {
        var filePath = 'ms-appdata:\\\\\\local\\' + json.filePath.replace(/\//g, "\\").replace(/\\\\/g, '\\');
        //  PDFJS.getDocument('ms-appx:///apps/com.iexceed.testapp/staticfiles/exa.pdf').then(function (pdf) {
        PDFJS.getDocument(filePath).then(function (pdf) {
            init();
            var pdfDiv1 = document.createElement("div");
            pdfDiv1.id = "pdfDiv1";
            pdfDiv1.style.width = "100%";
            pdfDiv1.style.height = "100%";//document.body.offsetHeight;//"auto";
            pdfDiv1.style.position = "fixed";
            pdfDiv1.style.backgroundColor = "black";
            pdfDiv1.style.top = "0";
            pdfDiv1.style.left = "0";
            pdfDiv1.style.zIndex = "99999";
            pdfDiv1.style.verticalAlign = "middle";
            pdfDiv1.style.textAlign = "center";
            pdfDiv1.style.overflow = "auto";

            pdfDiv = document.createElement("div");
            pdfDiv.id = "pdfDiv";
            pdfDiv.style.width = "100%";
            pdfDiv.style.height = "100%";//document.body.offsetHeight;//"auto";
            //pdfDiv.style.position = "fixed";
            pdfDiv.style.backgroundColor = "grey";
            //pdfDiv.style.top = "0";
            //pdfDiv.style.left = "0";
            //pdfDiv.style.zIndex = "99999";
            pdfDiv.style.verticalAlign = "middle";
            pdfDiv.style.textAlign = "center";
            pdfDiv.style.overflow = "auto";
            pdfDiv.style.msContentZooming = "zoom";
            pdfDiv.style.msScrollRails = "none";
            pdfDiv.style.msContentZoomLimitMin = "100%";
            pdfDiv.style.msContentZoomLimitMax = "500%";

            //var zoomPlus = document.createElement("img");
            //zoomPlus.id = "zoomMax";
            //zoomPlus.src = "ms-appx:///images/Square30x30.png";
            //zoomPlus.style.bottom = "0px";
            //zoomPlus.style.right = "10%";
            //zoomPlus.style.zIndex = "99999";
            //zoomPlus.style.position = "fixed";
            //zoomPlus.addEventListener("click", function () { zoom("+"); });

            //var zoomMin = document.createElement("img");
            //zoomMin.id = "zoomMin";
            //zoomMin.src = "ms-appx:///images/Square30x30.png";
            //zoomMin.style.bottom = "0px";
            //zoomMin.style.right = "5%";
            //zoomMin.style.position = "fixed";
            //zoomMin.style.zIndex = "99999";
            //zoomMin.addEventListener("click", function () { zoom('-'); });

            //var closeImg = document.createElement("img");
            //closeImg.id = "closePdf";
            //closeImg.src = "ms-appx:///images/Square150x150.png";
            //closeImg.style.zIndex = "99999";
            //closeImg.style.width = "5%";
            //closeImg.style.height = "auto";
            //closeImg.style.position = "fixed";
            //closeImg.addEventListener("click", function () { closePdf(); });
            var button = document.createElement('input');
            button.type = "submit";
            button.value = "X";
            button.style.position = "fixed";
            button.style.top = "5px";
            button.style.left = "5px";
            button.style.border = "2px solid #c2c2c2";
            button.style.padding = "1px 5px";
            button.style.backgroundColor = "#605F61";
            button.style.zIndex = 999999;
            button.style.height = "50px";
            button.style.width = "50px";
            button.style.borderRadius = "150px";
            button.addEventListener("click", function () {
                closePdf();
            }, false);
            pdfDiv.appendChild(button);
            pdfDiv1.appendChild(pdfDiv);
            document.body.appendChild(pdfDiv1);
            if (!navigator.maxTouchPoints)
                attachMouseKeyboard("pdfDiv");
            thePDF = pdf;
            numPages = pdf.numPages;

            pdf.getPage(1).then(handlePages);

        });
    }
    WinContainer.plugin.openFile = function (json) {
        try {
            if (json.filePath.indexOf('.pdf') >= 0) {
                readPdf(json);
            } else {
                WinContainer.failureCallback(json.id, "APZ-CNT-236");  //file not supported
            }
        } catch (e) {
            console.log(e.message);
            WinContainer.failureCallback(json.id, "APZ-CNT-077");  //invalid json
        }
       
    }
})();

