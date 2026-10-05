WinContainer.Settings = (function () {
    var containerprops = appprops = userprefs = {};
    var Container = Windows.Storage.ApplicationData.current.localSettings.containers;
    var ls = Windows.Storage.ApplicationData.current.localSettings;
    function getFile(file) {
        var httpfreq = new XMLHttpRequest();
        try {
            httpfreq.open("GET", "ms-appx:///" + file, false);
            httpfreq.send(null);
            var status = httpfreq.status;
            if ((status === 200) || (status === 0) || (status === 1100)) {
                return JSON.parse(httpfreq.responseText);
            } else
                return null;
        } catch (e) {
            WinContainer.Log.fatal(e.description);
            return null;
        }
    }
    function initUserPrefs() {
        if (Container.hasKey("UserPrefs")) {
            var values = Container.lookup("UserPrefs").values
            for (var key in values)
                if (typeof values[key] !== "function")
                    userprefs[key] = values[key];
        }
        else {
            ls.createContainer("UserPrefs", Windows.Storage.ApplicationDataCreateDisposition.Always);
            userprefs = getFile("apps/" + containerprops[JsonKey.MAINAPP] + "/screens/config/userprefs.json");
            for (var key in userprefs)
                ls.containers.lookup("UserPrefs").values[key] = userprefs[key];
        }
    }
    function initAppProps() {
        if (Container.hasKey("AppProps")) {
            var values = Container.lookup("AppProps").values;
            for (var key in values)
                if (typeof values[key] !== "function")
                    appprops[key] = values[key];
        }
        else {
            ls.createContainer("AppProps", Windows.Storage.ApplicationDataCreateDisposition.Always);
            appprops = getFile("apps/" + containerprops[JsonKey.MAINAPP] + "/screens/config/appprops.json");
            for (var key in appprops)
                ls.containers.lookup("AppProps").values[key] = appprops[key];
        }
    }
    function initContainerProps() {
        containerprops = getFile("containerprops.json");
    }

    initContainerProps();
    initUserPrefs();
    initAppProps();  
     
    function getUserPreferences(req) {
        req.userPrefs = userprefs;
        WinContainer.successCallback(req);
    }
    function setUserPreferences(req) {
        var keyVal = req.userPrefs;
        for (var key in keyVal)
            ls.containers.lookup("UserPrefs").values[key] = keyVal[key];
        initUserPrefs();
        WinContainer.successCallback(req);
    }
    function setUserPreference(req) {
        if (Container.hasKey("userSettings")) {
            ls.containers.lookup("userSettings").values[req.key] = req.value;
        }
        else {
            ls.createContainer("userSettings", Windows.Storage.ApplicationDataCreateDisposition.Always);
            ls.containers.lookup("userSettings").values[req.key] = req.value;
        }
        WinContainer.successCallback(req);
    }
    function getUserPreference(req)
    {
        req.value = ls.containers.lookup("userSettings").values[req.key];
        WinContainer.successCallback(req);
    }
    function getAppProperties(req) {
        req.appProps = appprops;
        WinContainer.successCallback(req);
    }
    function setAppProperties(req) {
        var keyVal = req.appProps;
        for (var key in keyVal)
            ls.containers.lookup("AppProps").values[key] = keyVal[key];
        initAppProps();
        WinContainer.successCallback(req);
    }
    return {
        containerProperties: containerprops,
        appProperties: appprops,
        userPreferences: userprefs,
        getUserPrefs: getUserPreferences,
        setUserPrefs: setUserPreferences,
        setuserpreference: setUserPreference,
        getuserpreference:getUserPreference,
        getAppProps: getAppProperties,
        setAppProps: setAppProperties
    }
})();
