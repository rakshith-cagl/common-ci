//-------------------------------------------------------------------------------------------------------------
(function () {
    var currPage = 1,
        numPages = 0,
        thePDF = null,
        zoomLevel = 1,
        pdfDiv = null,
        zoomValue = null,
    _id = "";
    function zoom(op) {
        if (op == "+") {
            if (zoomLevel < 12)
                zoomLevel ++;
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
               // zoomMin.style.width = zoomValue[2] + "px";//"100%";
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
        zoomLevel = 1;
        zoomValue = [];
        zoomValue[0] = 100;
        zoomValue[1] = screen.width;
        //zoomValue[2] = 300;
        //zoomValue[3] = 400;
        //zoomValue[4] = 500;
        var i = 2;
        while (i < 12)
        {
            zoomValue[i] = parseInt(zoomValue[i - 1] * 1.2);
            i++;
        }
       
    }
   
   
    function readPdf(json) {
        var filePath = 'ms-appdata:///local\\' + json.filePath.replace(/\//g, "\\").replace(/\\\\/g, '\\');//'ms-appdata:\\\\\\local\\exa.pdf';for
        // PDFJS.getDocument('ms-appx:///apps/com.iexceed.testapp/staticfiles/exa.pdf').then(function (pdf) {
        try{
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
                // pdfDiv.style.position = "fixed";
                pdfDiv.style.backgroundColor = "grey";
                //pdfDiv.style.top = "0";
                //pdfDiv.style.left = "0";
                pdfDiv.style.zIndex = "99999";
                pdfDiv.style.verticalAlign = "middle";
                pdfDiv.style.textAlign = "center";
                pdfDiv.style.overflow = "auto";
                pdfDiv.style.msContentZooming = "zoom";
                pdfDiv.style.msScrollRails = "none";
                pdfDiv.style.msContentZoomLimitMin = "50%";
                pdfDiv.style.msContentZoomLimitMax = "400%";
                pdfDiv.style.margin = "0 auto";

                pdfDiv1.appendChild(pdfDiv);
                document.body.appendChild(pdfDiv1);
                thePDF = pdf;
                numPages = pdf.numPages;

                pdf.getPage(1).then(handlePages);
                var j = { "successMessage":"Read file susscess" };
                j.id = json.id;
                WinContainer.successCallback(j);

            },function(e){
                console.log(e.message);
                var j = {"errorCode":"APZ-CNT-","errorDescription":"Invalid json"};
                WinContainer.failureCallback(json.id, "");
            });
        } catch (e) {
            WinContainer.failureCallback(json.id, "invalid");
        }
    }
    WinContainer.plugin.openFile = function (json) {
        try{
            if (json.filePath.indexOf('.pdf') >= 0) {
                var app1 = WinJS.Application;
                app1.onbackclick = function () {
                    try {
                        closePdf();
                    } catch (e) { }
                    return true;
                }
                readPdf(json);
            } else {
                var j = {"errorCode":"APZ-CNT-022","errorDescription":"File not supported"};
                WinContainer.failureCallback(json.id,"");
            }
        } catch (e) {
            console.log(e.message);
            var j = {"errorCode":"APZ-CNT-","errorDescription":"Invalid json"};
            WinContainer.failureCallback(json.id, "");
        }
        
    }
    function pdfFail()
    {
        WinContainer.failureCallback(json.id, "");
    }
})();


