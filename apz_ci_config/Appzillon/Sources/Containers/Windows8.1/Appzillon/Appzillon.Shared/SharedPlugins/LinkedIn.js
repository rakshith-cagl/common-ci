WinContainer.LinkedIn=(function(){
    var id,
        linkedUrl = "https://www.linkedin.com/uas/oauth2/authorization?",
        tokenUrl = "https://www.linkedin.com/uas/oauth2/accessToken",
        accessUrl="https://api.linkedin.com/v1/people/~:(id,picture-url,first-name,last-name,maiden-name,formatted-name,headline,location,industry,summary,specialties,positions,email-address)?format=json&oauth2_access_token=";

    function getClientId() {
        return WinContainer.Settings.appProperties.linkedinClientId;
    }
    function getSecretKey() {
        return WinContainer.Settings.appProperties.linkedinSecretKey;
    }
    function getCallbackUrl() {
        return 'http://localhost';
    }
    function phoneLogin(startURI, endURI) {
        WinContainer.webAuthenticationType = "LINKEDIN";
        Windows.Security.Authentication.Web.WebAuthenticationBroker.authenticateAndContinue(startURI, endURI, null, Windows.Security.Authentication.Web.WebAuthenticationOptions.none);
    }
    function desktopLogin(startURI, endURI) {
        Windows.Security.Authentication.Web.WebAuthenticationBroker.authenticateAsync(
               Windows.Security.Authentication.Web.WebAuthenticationOptions.none, startURI, endURI)
               .done(function (result) {
                   loginSuccess(result.responseData);
               }, function (e) {
                   WinContainer.failureCallback(id, "APZ-CNT-247", e.message);
                   WinContainer.Log.error(e.message);
               });
    }
    function startLogin(req) {
        id = req.id;
        var linkedURL = linkedUrl;
        var clientID = getClientId();
        var callbackURL = getCallbackUrl();
        linkedURL += 'response_type=code&client_id=' + clientID + "&scope=r_basicprofile r_emailaddress&state=STATE&redirect_uri=" + encodeURIComponent(callbackURL);
        var startURI = new Windows.Foundation.Uri(linkedURL);
        var endURI = new Windows.Foundation.Uri(callbackURL);

        WinContainer.isPhone() ? phoneLogin(startURI, endURI) : desktopLogin(startURI, endURI);
    }

    function loginSuccess(r) {
        try {
            var code = r.substring(r.indexOf('code='), r.indexOf('&'));
            var secretKey = getSecretKey();
            var clientID = getClientId();
            var callbackUrl = getCallbackUrl();
            var res_url = new Windows.Foundation.Uri(tokenUrl);
            var str = code + '&client_id=' + clientID + '&client_secret=' + secretKey + '&redirect_uri=' + callbackUrl + '&grant_type=authorization_code';
            var client = new Windows.Web.Http.HttpClient();
            var stringContent = new Windows.Web.Http.HttpStringContent(str, Windows.Storage.Streams.UnicodeEncoding.utf8, "application/x-www-form-urlencoded");
            client.postAsync(res_url, stringContent).done(function (result) {
                var userInfo = JSON.parse(result.content.toString());
                var access_token = userInfo.access_token;
                var client = new Windows.Web.Http.HttpClient();
                client.getStringAsync(new Windows.Foundation.Uri(accessUrl + access_token)).done(function (result) {
                    authzInProgress = false;
                    var userInfo = JSON.parse(result);
                    var src = userInfo.picture;
                    var response = {
                        id:id,
                        email: userInfo.emailAddress,
                        firstName: userInfo.firstName,
                        lastName: userInfo.lastName,
                        name: userInfo.formattedName,
                        pictureURL: userInfo.pictureUrl,
                        linId: userInfo.id,
                        gender: "",
                        locale: "",
                        location: userInfo.location.name,
                        industry: userInfo.industry,
                        headline: userInfo.headline,
                        jobTitle: userInfo.positions.values[0].title,
                        company: userInfo.positions.values[0].company.name
                    }
                   WinContainer.successCallback(response)

                }, function (e) {
                    WinContainer.failureCallback(id, "APZ-CNT-247");
                    WinContainer.Log.error(e.message);
                });
            }, function (e) {
                WinContainer.failureCallback(id, "APZ-CNT-247");
                WinContainer.Log.error(e.message);
            });
        }
        catch (e) {
            WinContainer.failureCallback(id, "APZ-CNT-247");
            WinContainer.Log.error(e.description);
        }

    }
    return {
        Login: startLogin,
        loginSuccess: loginSuccess
    }
})();

