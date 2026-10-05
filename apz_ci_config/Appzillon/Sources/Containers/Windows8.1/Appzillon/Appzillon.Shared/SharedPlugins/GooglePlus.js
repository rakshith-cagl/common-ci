WinContainer.GooglePlus = (function () {
   // var secretKey = "IuVG9M6QPpak_0d6dd3vSrzJ";
    //var clientId = "595817284795-0c4sk1n7v95nvcodhh0koq7psslve1jp.apps.googleusercontent.com";
    var id = null;
    var googleURL = "https://accounts.google.com/o/oauth2/auth?";
    var tokenUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=";
    var oauthUrl = "https://accounts.google.com/o/oauth2/token";

    function getEndUri() {
        return WinContainer.isPhone() ? "http://localhost" : "https://accounts.google.com/o/oauth2/approval";
    }
    function getClientId() {
        return WinContainer.Settings.appProperties.googlePlusClientId;
    }
    function getSecretKey() {
        return WinContainer.Settings.appProperties.googlePlusSecretKey;
    }
    function getCallbackUrl() {
        return WinContainer.isPhone() ? 'http://localhost' : "urn:ietf:wg:oauth:2.0:oob";
    }
    function phoneLogin(startURI, endURI) {
        WinContainer.webAuthenticationType = "GOOGLEPLUS";
        Windows.Security.Authentication.Web.WebAuthenticationBroker.authenticateAndContinue(startURI, endURI, null, Windows.Security.Authentication.Web.WebAuthenticationOptions.none);
    }
    function desktopLogin(startURI, endURI) {
        Windows.Security.Authentication.Web.WebAuthenticationBroker.authenticateAsync(
               Windows.Security.Authentication.Web.WebAuthenticationOptions.useTitle, startURI, endURI)
               .done(function (result) {
                   loginSuccess(result.responseData);
               }, function (e) {
                   WinContainer.failureCallback(id, "APZ-CNT-247");
                   WinContainer.Log.error(e.message);
               });
    }
    var startLogin = function (req) {
        id = req.id;
        var clientID = getClientId();
        var callbackURL = getCallbackUrl();
        googleURL += "client_id=" + clientID + "&redirect_uri=" + encodeURIComponent(callbackURL) + "&response_type=code&scope=openid email profile";//http://picasaweb.google.com/data";
        var startURI = new Windows.Foundation.Uri(googleURL);
        var endURI = new Windows.Foundation.Uri(getEndUri());

        WinContainer.isPhone() ? phoneLogin(startURI, endURI) : desktopLogin(startURI, endURI);
    }
    

    var loginSuccess = function(r) {
        try {
            var code = r.substring(r.indexOf('code='), r.indexOf('&'));
            var secretKey = getSecretKey();
            var clientID = getClientId();
            var callbackUrl = getCallbackUrl();
            var res_url = new Windows.Foundation.Uri(oauthUrl);
            var str = code + '&client_id=' + clientID + '&client_secret=' + secretKey + '&redirect_uri=' + callbackUrl + '&scope=&grant_type=authorization_code';
            var client = new Windows.Web.Http.HttpClient();
            var stringContent = new Windows.Web.Http.HttpStringContent(str, Windows.Storage.Streams.UnicodeEncoding.utf8, "application/x-www-form-urlencoded");
            client.postAsync(res_url, stringContent).done(function (result) {

                var userInfo = JSON.parse(result.content.toString());
                var access_token = userInfo.access_token;
                var client = new Windows.Web.Http.HttpClient();
                client.getStringAsync(new Windows.Foundation.Uri(tokenUrl + access_token)).done(function (result) {
                    authzInProgress = false;
                    var userInfo = JSON.parse(result);
                    var src = userInfo.picture;
                    var userDetail = {};
                    userDetail.id = id;
                    userDetail.gplusId = userInfo.id;
                    userDetail.email = userInfo.email;
                    userDetail.name = userInfo.name;
                    userDetail.firstName = userInfo.given_name;
                    userDetail.lastName = userInfo.family_name;
                    userDetail.pictureURL = userInfo.picture;

                    userDetail.gender = userInfo.gender;
                    userDetail.locale = userInfo.locale;
                    userDetail.isVerified = userInfo.verified_email ? 'true' : 'false';
                    WinContainer.successCallback(userDetail);

                }, function (e) {
                    WinContainer.failureCallback(id, "APZ-CNT-247");
                    WinContainer.Log.error(e.message);
                });
            }, function (e) {
                WinContainer.failureCallback(id, "APZ-CNT-247");
                WinContainer.Log.error(e.message);
            });
        } catch (e) {
            WinContainer.failureCallback(id, "APZ-CNT-247");
            WinContainer.Log.error(e.description);
        }
    }
    return {
        Login: startLogin,
        loginSuccess: loginSuccess
    }
})();