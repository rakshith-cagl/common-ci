var pullDown = (function () {

    var sX;
    var sY;
    var eX;
    var eY;
    var id;
    var screenId;
    var pulldowndone = false;
    var timed;
    _enablePullDown = function (json) {
        try {
            var progress = document.createElement("img");
            var fileval = null;
            progress.src = "ms-appx:///images/refresh.gif";
            progress.style.width = "50px";
            progress.style.height = "auto";
            progress.style.position = "absolute";
            progress.style.top = "50%";
            progress.style.left = "50%";
            progress.style.margin = "-25px 0 0 -25px";
            // progress.style.zIndex = "9999";

            var divP = document.createElement("div");
            divP.id = "progress";
            divP.style.backgroundColor = " rgba(229,229,229, 0)";//"white";//el.style.backgroundColor;
            divP.style.position = "fixed";
            divP.style.overflow = "hidden";
            // el.offsetTop;
            divP.style.top = "-50px";
            divP.style.width = "100%";
            divP.style.height = "50px";
            divP.style.zIndex = "9999";
            divP.translationY = 0;
            divP.style.transition = "0.3s";
            divP.appendChild(progress);
            document.body.appendChild(divP);

        } catch (e) {

        }
        //console.log("in enablePullDown plugin");
        // this.initNativeService(json);
        id = json.id;
        screenId = apz.currScr;
        try {
            if (window.navigator.pointerEnabled) {
                document.body.addEventListener("pointerdown", ptrEvtDown, false);
                document.body.addEventListener('pointermove', ptrMove, false);
                document.body.addEventListener('pointerup', ptrEnd, false);
                if (WinContainer.isPhone())
                {
                    document.body.addEventListener('touchmove', ptrMove, false);
                    document.body.addEventListener('touchend', ptrEnd,false);
                }
            }
            else {
                document.body.addEventListener('MSPointerDown', ptrEvtDown, false);
                document.body.addEventListener('MSPointerMove', ptrMove, false);
                document.body.addEventListener('MSPointerEnd', ptrEnd, false);
                if (WinContainer.isPhone()) {
                    document.body.addEventListener('touchmove', ptrMove, false);
                    document.body.addEventListener('touchend', ptrEnd, false);
                }
            }
            var js = {};
            js.id = id;
            js.status = true;
            js.event = "started";
            js.keepAlive = true;
            Apz.nativeServiceCB(js);
        } catch (e) {
            var js = {};
            js.id = id;
            js.status = false;
            Apz.nativeServiceCB(js);
        }
    };
    function ptrEnd(evt) {
          if (pulldowndone) {
            var j = {};
            j.id = id;
            j.screenId = screenId;
            j.event = "pulldown";
            j.status = true;
            j.keepAlive = true;
            var pr = document.getElementById("progress");
            pr.style.top = "50px";
            console.log("ptr end >>    pulldown happened");
            clearTimeout(timed);
            pulldowndone = false;
            Apz.nativeServiceCB(j);          //------------------needs to be changed
        }
        else {
            var pr = document.getElementById("progress");
            timed = setTimeout(hideRefresh, 300, pr);
        }
    }
    function ptrMove(evt) {
        try {
            //console.log("ptr move x :: " + evt.touches[0].screenX + "  move Y :: " + evt.touches[0].screenY);
            //  console.log("ptr move x1 :: " + evt.touches[0].pageX + "  move Y1 :: " + evt.touches[0].pageY);
            var pr = document.getElementById("progress");
            var dx = Math.abs(evt.pageX - sX);
            var dy = evt.pageY - sY;
            if (window.pageYOffset < 200 && dx < 65) {
                if (dy > 200) {
                    console.log("ptr move >>    pulldown happened");
                    pulldowndone = true;
                }
                else {
                    pr.style.top = (dy / 3) + "px";
                }

            }
            //   console.log("ptr move >>   y = " + dy + "  x =" + dx + "  yoffset =" + window.pageYOffset + "  start x = " + sX + " end x = " + evt.touches[0].pageX);
        } catch (ex) {
            console.log("ptr move error :: " + ex.message);
        }
    }
    function hideRefresh(pr) {
        if (pr == undefined || pr == null)
            return;
            pr.style.top = "-50px";
    }
    function ptrEvtDown(evt) {
        try {
            
            sX = evt.pageX;
                sY = evt.pageY;
                //console.log("ptr down y :: " + evt.touches[0].screenY);
            
        } catch (e) {
            console.log("ptr down error :: " + e.message);
        }
    }
    _disablePullDown = function (json) {
        // console.log("in disablePullDown plugin");
        pulldowndone = false;
        try {
            var pr = document.getElementById("progress");
            document.body.removeChild(pr);
            hideRefresh(pr);
        } catch (e) {
            console.log("in disablePullDown plugin err :: " + e.message);
        }
        //   console.log("in disablePullDown plugin");
        var id = json.id;
        // this.initNativeService(json);
        var currScrId = apz.currScr; //----
        try {
            if (window.navigator.pointerEnabled) {
                document.body.removeEventListener('pointerdown', ptrEvtDown, false);
                document.body.removeEventListener('pointermove', ptrMove, false);
                document.body.removeEventListener('pointerend', ptrEnd, false);
                if (WinContainer.isPhone()) {
                    document.body.removeEventListener('touchmove', ptrMove, false);
                    document.body.removeEventListener('touchend', ptrEnd, false);
                }
            }
            else {
                document.body.removeEventListener('MSPointerDown', ptrEvtDown, false);
                document.body.removeEventListener('MSPointerMove', ptrMove, false);
                document.body.removeEventListener('MSPointerEnd', ptrEnd, false);
                if (WinContainer.isPhone()) {
                    document.body.removeEventListener('touchmove', ptrMove, false);
                    document.body.removeEventListener('touchend', ptrEnd, false);
                }
            }
            var js = {};
            js.id = id;
            js.status = true;
            Apz.nativeServiceCB(js);
        } catch (e) {
            var js = {};
            js.id = id;
            js.status = false;
            Apz.nativeServiceCB(js);
        }
    };
    _hideRefresh = function (json) {
        var id = json.id == undefined ? "" : json.id;
        if (id != "") {
            //  this.initNativeService(json);
        }
        var pr = document.getElementById("progress");
        if (pr != undefined) {
            pr.style.top = "-50px";
            // document.body.removeChild(pr);
            if (id != "") {
                var js = {};
                js.id = id;
                js.status = true;
                Apz.nativeServiceCB(js);
            }
        }
        else {
            var js = {};
            js.id = id;
            js.status = false;
            js.errorCode = "UNDEFINED";
            Apz.nativeServiceCB(js);
        }
    };
    return {
        hideRefresh: _hideRefresh,
        disablePullDown: _disablePullDown,
        enablePullDown: _enablePullDown
    }
})();