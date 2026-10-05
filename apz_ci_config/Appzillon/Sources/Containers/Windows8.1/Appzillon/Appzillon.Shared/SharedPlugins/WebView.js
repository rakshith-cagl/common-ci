//----WEBVIEW-------
var webviewopen = false;
WinContainer.WebView = (function () {
    var req_id, req_url;

    function createWebView() {
        var wDiv = document.createElement('Div');
        wDiv.id = "containerWebview";
        wDiv.style.position = "fixed";
        wDiv.style.width = '100%';
        wDiv.style.height = '100%';
        //wDiv.style.backgroundColor = 'white';
        wDiv.style.top = '0px';
        wDiv.style.left = '0px';
        //wDiv.style.border = '5px solid blue';
        wDiv.style.zIndex = '99999';
        webView = document.createElement("x-ms-webview");
        webView.style.width = '100%';
        webView.style.height = '100%';
        try {
            webView.src = req_url;
            if (!WinContainer.isPhone()) {
                var navdiv = document.createElement("Div");
                navdiv.id = "navbardiv";
                var button = document.createElement("button");
                button.id = "openurlCancel";
                document.body.appendChild(button);
              //  button.style.position = "absolute";
                button.style.width = "5%";
                button.style.height = "40px";
                button.style.float = "10px";
                button.style.top = "10px";
                button.style.left = "95%";
              //  button.style.zIndex = "9999";
                button.textContent = "Cancel";
                button.onclick = Cancel;
                button.innerText = "Cancel";
                button.style.visibility = "visible";
                button.style.borderColor = "black";
                navdiv.appendChild(button);
                wDiv.appendChild(navdiv);
            }
           
            wDiv.appendChild(webView);
            wDiv.style.zIndex = "2";
            document.body.appendChild(wDiv);
            webviewopen = true;
            webView.addEventListener("MSWebViewContentLoading", callback);
            webView.addEventListener("MSWebViewDOMContentLoaded", callback);
            webView.addEventListener("MSWebViewFrameContentLoading", callback);
            webView.addEventListener("MSWebViewFrameDOMContentLoaded", callback);
            webView.addEventListener("MSWebViewFrameNavigationCompleted", callback);
            webView.addEventListener("MSWebViewFrameNavigationStarting", callback);
            webView.addEventListener("MSWebViewNavigationCompleted", callback);
            webView.addEventListener("MSWebViewNavigationStarting", callback);
            webView.addEventListener("MSWebViewNewWindowRequested", callback);
            webView.addEventListener("MSWebViewUnviewableContentIdentified", callback);
            console.log("Can Go Back =" + webView.canGoBack + " can Go forward=" + webView.canGoForward);
        } catch (e) {
            WinContainer.failureCallback(req_id, "APZ-CNT-082");
            WinContainer.Log.error(e.description);
        }
       
    }
    function Cancel(){
        if (webviewopen)
        {
            var json = {};
            json.id = null;
            WinContainer.WebView.Close(json);
        }
    }
   
    function callback(e) {
        var res = {};
        res.id = req_id;
        res.URL = e.uri;
		res.text = res.URL;
        res.keepAlive = true;
        WinContainer.successCallback(res);
    }
    function launchWebview(req) {
        req_id = req.id;
        req_url = req.url||req.URL;
        createWebView();
    }
    function closeWebview(req) {
        try {
            req_id = null, req_url = null;
            document.body.removeChild(document.getElementById("containerWebview"));
            webviewopen = false;
            if(req.id != null)
            WinContainer.successCallback(req);       
        } catch (e) {
           if (req.id != null)
            WinContainer.failureCallback(req.id, "APZ-CNT-082");
            WinContainer.Log.error(e.description);
        }
    }

    return {
        Launch: launchWebview,
        Close: closeWebview
    }
})();