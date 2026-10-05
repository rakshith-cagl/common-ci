WinContainer.Facebook = (function () {
    var facebookURL = "https://www.facebook.com/dialog/oauth?client_id=";
    var tokenUrl = "https://graph.facebook.com/me?access_token=";
    var id = null;
    function getClientId() {
        return WinContainer.Settings.appProperties.facebookAppId;
    }
    function getCallbackUrl() {
        return WinContainer.getCurrentApplicationCallbackUri();
    }
    function phoneLogin(startURI, endURI) {
        WinContainer.webAuthenticationType = "FACEBOOK";
        Windows.Security.Authentication.Web.WebAuthenticationBroker.authenticateAndContinue(startURI);
        //  Windows.Security.Authentication.Web.WebAuthenticationBroker.authenticateAndContinue(startURI, endURI, null, Windows.Security.Authentication.Web.WebAuthenticationOptions.none);
    }
    function desktopLogin(startURI, endURI) {
        Windows.Security.Authentication.Web.WebAuthenticationBroker.authenticateAsync(Windows.Security.Authentication.Web.WebAuthenticationOptions.none, startURI)
               .done(function (result) {
                   loginSuccess(result.responseData);
               }, function (e) {
                   WinContainer.failureCallback(id, "APZ-CNT-247");
               });
    }
    function startLogin(req) {
        id = req.id;
        var clientID = getClientId();
        var callbackURL = getCallbackUrl();
        facebookURL += clientID + "&redirect_uri=" + encodeURIComponent(callbackURL) + "&scope=public_profile,email&display=popup&response_type=token";//&scope=read_stream
        var fburl = "https://www.facebook.com/dialog/oauth?client_id=" + clientID + "&scope=public_profile,email&display=popup&response_type=token&redirect_uri=" + callbackURL;
        var startURI = new Windows.Foundation.Uri(fburl);
        var endURI = new Windows.Foundation.Uri(callbackURL);
        WinContainer.isPhone() ? phoneLogin(startURI, endURI) : desktopLogin(startURI, endURI);
    }
    function loginSuccess(webAuthResultResponseData) {
        var responseData = webAuthResultResponseData.substring(webAuthResultResponseData.indexOf("access_token"));
        var keyValPairs = responseData.split("&");
        var access_token;
        var expires_in;
        for (var i = 0; i < keyValPairs.length; i++) {
            var splits = keyValPairs[i].split("=");
            switch (splits[0]) {
                case "access_token":
                    access_token = splits[1];
                    break;
                case "expires_in":
                    expires_in = splits[1];
                    break;
            }
        }
        var requiredField = "&fields=picture,email,id,name,first_name,last_name,age_range,link,gender,locale,timezone,updated_time,verified";
        var client = new Windows.Web.Http.HttpClient();
        client.getStringAsync(new Windows.Foundation.Uri(tokenUrl + access_token + requiredField)).done(function (result) {

            var userInfo = JSON.parse(result);

            var userDetail = {};
            userDetail.id = id;
            userDetail.fbId = userInfo.id;
            userDetail.email = userInfo.email;
            userDetail.name = userInfo.name;
            userDetail.firstName = userInfo.first_name;
            userDetail.lastName = userInfo.last_name;
            userDetail.pictureURL = userInfo.picture.data.url;
            userDetail.gender = userInfo.gender;
            userDetail.locale = userInfo.locale;
            userDetail.isVerified = userInfo.verified ? 'true' : 'false';
            WinContainer.successCallback(userDetail);
        }, function (e) {
            WinContainer.failureCallback(id, 'APZ-CNT-247');
        });
    }
    return {
        Login: startLogin,
        loginSuccess: loginSuccess
    }
})();