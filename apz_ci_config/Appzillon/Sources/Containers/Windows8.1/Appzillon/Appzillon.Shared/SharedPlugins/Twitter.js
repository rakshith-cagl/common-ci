WinContainer.Twitter = (function () {
    var id = null;
    var twitterURL = "https://api.twitter.com/oauth/request_token";
    var tokenUrl = "https://api.twitter.com/oauth/access_token";
    var oauthUrl = "https://api.twitter.com/oauth/authenticate?oauth_token=";

    function getClientId() {
        return WinContainer.Settings.appProperties.twitterClientId;
    }
    function getSecretKey() {
        return WinContainer.Settings.appProperties.twitterSecretKey;
    }
    function getCallbackUrl() {
        return 'http://localhost';
    }
    function phoneLogin(startURI, endURI) {
        WinContainer.webAuthenticationType = "TWITTER";
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
        //Get request token and authorize
        var callbackURL = getCallbackUrl();
        var clientID = getClientId();
        var clientSecret = getSecretKey();
        var oauth_token;
        var oauth_token_secret;

        var timestamp = Math.round(new Date().getTime() / 1000.0);
        var nonce = Math.random();
        nonce = Math.floor(nonce * 1000000000);

        //Compute Base Signature
        var sigBaseStringParams = "oauth_callback=" + encodeURIComponent(callbackURL);
        sigBaseStringParams += "&" + "oauth_consumer_key=" + clientID;
        sigBaseStringParams += "&" + "oauth_nonce=" + nonce;
        sigBaseStringParams += "&" + "oauth_signature_method=HMAC-SHA1";
        sigBaseStringParams += "&" + "oauth_timestamp=" + timestamp;
        sigBaseStringParams += "&" + "oauth_version=1.0";
        var sigBaseString = "POST&";
        sigBaseString += encodeURIComponent(twitterURL) + "&" + encodeURIComponent(sigBaseStringParams);

        var keyText = clientSecret + "&";
        var signature = getSignature(sigBaseString, keyText);

        var dataToPost = "oauth_callback=\"" + encodeURIComponent(callbackURL)
            + "\", oauth_consumer_key=\"" + clientID
            + "\", oauth_nonce=\"" + nonce
            + "\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"" + timestamp
            + "\", oauth_version=\"1.0\", oauth_signature=\"" + encodeURIComponent(signature) + "\"";
        var httpContent = new Windows.Web.Http.HttpStringContent("", Windows.Storage.Streams.UnicodeEncoding.utf8, "application/x-www-form-urlencoded");

        var client = new Windows.Web.Http.HttpClient();
        client.defaultRequestHeaders.authorization = new Windows.Web.Http.Headers.HttpCredentialsHeaderValue("OAuth", dataToPost);
        client.postAsync(new Windows.Foundation.Uri(twitterURL), httpContent).done(function (result) {
            twitterLogin(result.content.toString());
        }, function (e) {
            WinContainer.failureCallback(id, "APZ-CNT-247", e.message);
            WinContainer.Log.error(e.message);
        });

    }

    function twitterLogin(response) {
        //Post call to get User Information
        var callbackURL = getCallbackUrl();
        var keyValPairs = response.split("&");

        for (var i = 0; i < keyValPairs.length; i++) {
            var splits = keyValPairs[i].split("=");
            switch (splits[0]) {
                case "oauth_token":
                    oauth_token = splits[1];
                    break;
                case "oauth_token_secret":
                    oauth_token_secret = splits[1];
                    break;
            }
        }
        var startURI = new Windows.Foundation.Uri(oauthUrl + oauth_token + '&' + "include_email=true");
        var endURI = new Windows.Foundation.Uri(callbackURL);
        WinContainer.isPhone() ? phoneLogin(startURI, endURI) : desktopLogin(startURI, endURI);
    }


    function getSignature(sigBaseString, keyText) {
        var keyMaterial = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(keyText, Windows.Security.Cryptography.BinaryStringEncoding.Utf8);
        var macAlgorithmProvider = Windows.Security.Cryptography.Core.MacAlgorithmProvider.openAlgorithm("HMAC_SHA1");
        var key = macAlgorithmProvider.createKey(keyMaterial);
        var tbs = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(sigBaseString, Windows.Security.Cryptography.BinaryStringEncoding.Utf8);
        var signatureBuffer = Windows.Security.Cryptography.Core.CryptographicEngine.sign(key, tbs);
        var signature = Windows.Security.Cryptography.CryptographicBuffer.encodeToBase64String(signatureBuffer);

        return signature;
    }


    function loginSuccess(r) {
        //Post call to get access token and access token secret
        var clientId = getClientId();
        var clientSecret = getSecretKey();
        var responseData = r.substring(r.indexOf("oauth_token"));
        var request_token;
        var oauth_verifier;
        var callbackURL = getCallbackUrl();
        var keyValPairs = responseData.split("&");

        for (var i = 0; i < keyValPairs.length; i++) {
            var splits = keyValPairs[i].split("=");
            switch (splits[0]) {
                case "oauth_token":
                    request_token = splits[1];
                    break;
                case "oauth_verifier":
                    oauth_verifier = splits[1];
                    break;
            }
        }


        var url = new Windows.Foundation.Uri(tokenUrl);
        var timeStamp = Math.round(new Date().getTime() / 1000.0);
        var nonce = Math.random();
        nonce = Math.floor(nonce * 1000000000);

        var sigBaseStringParams = "oauth_consumer_key=" + clientId;
        sigBaseStringParams += "&" + "oauth_nonce=" + nonce;
        sigBaseStringParams += "&" + "oauth_signature_method=HMAC-SHA1";
        sigBaseStringParams += "&" + "oauth_timestamp=" + timeStamp;
        sigBaseStringParams += "&" + "oauth_token=" + request_token;
        sigBaseStringParams += "&" + "oauth_version=1.0";

        var sigBaseString = "POST&";
        sigBaseString += encodeURIComponent(tokenUrl) + "&" + encodeURIComponent(sigBaseStringParams);

        var keyText = clientSecret + "&";
        var signature = getSignature(sigBaseString, keyText);
        var authorizationHeaderParams = "OAuth oauth_consumer_key=\"" + clientId
            + "\", oauth_nonce=\"" + nonce
            + "\", oauth_signature_method=\"HMAC-SHA1\", oauth_signature=\"" + encodeURIComponent(signature)
            + "\", oauth_timestamp=\"" + timeStamp
            + "\", oauth_token=\"" + encodeURIComponent(request_token) + "\", oauth_version=\"1.0\"";


        try {
            var httpContent = new Windows.Web.Http.HttpStringContent("oauth_verifier=" + oauth_verifier + '&' + "include_email=true", Windows.Storage.Streams.UnicodeEncoding.utf8, "application/x-www-form-urlencoded");
            var client = new Windows.Web.Http.HttpClient();
            client.defaultRequestHeaders.authorization = new Windows.Web.Http.Headers.HttpCredentialsHeaderValue("OAuth", authorizationHeaderParams);
            client.postAsync(new Windows.Foundation.Uri(tokenUrl), httpContent).done(function (result) {
                getoauthToken(result.content.toString(), oauth_verifier, clientId, clientSecret);
            }, function (e) {
                WinContainer.failureCallback(id, "APZ-CNT-247", e.message);
                WinContainer.Log.error(e.message);
            });
        }
        catch (e) {
            WinContainer.failureCallback(id, "APZ-CNT-247", e.message);
            WinContainer.Log.error(e.description);
        }
    }

    function getoauthToken(response, oauth_verifier, clientId, clientSecret) {
        //Get call to get user information.
        var access_token;
        var oauth_token_secret;
        var screen_name;
        keyValPairs = response.split("&");

        for (var j = 0; j < keyValPairs.length; j++) {
            var tokens = keyValPairs[j].split("=");
            switch (tokens[0]) {
                case "oauth_token":
                    access_token = tokens[1];
                    break;
                case "oauth_token_secret":
                    oauth_token_secret = tokens[1];
                    break;
                case "screen_name":
                    screen_name = tokens[1];
                    break;
            }
        }

        try {
            var ntwitterURL = "https://api.twitter.com/1.1/account/verify_credentials.json";
            var ntimeStamp = Math.round(new Date().getTime() / 1000.0);
            var nnonce = Math.random();
            nnonce = Math.floor(nnonce * 1000000000);
            var nsigBaseStringParams = "include_email=" + true;
            nsigBaseStringParams += "&" + "oauth_consumer_key=" + clientId;
            nsigBaseStringParams += "&" + "oauth_nonce=" + nnonce;
            nsigBaseStringParams += "&" + "oauth_signature_method=HMAC-SHA1";
            nsigBaseStringParams += "&" + "oauth_timestamp=" + ntimeStamp;
            nsigBaseStringParams += "&" + "oauth_token=" + access_token;
            nsigBaseStringParams += "&" + "oauth_version=1.0";
            var nsigBaseString = "GET&";
            nsigBaseString += encodeURIComponent(ntwitterURL) + "&" + encodeURIComponent(nsigBaseStringParams);

            //Calculate Signature
            var nkeyText = clientSecret + "&" + oauth_token_secret;
            var nsignature = getSignature(nsigBaseString, nkeyText);

            ntwitterURL += "?" + nsigBaseStringParams + "&oauth_signature=" + encodeURIComponent(nsignature);
            var nauthorizationHeaderParams = "OAuth oauth_consumer_key=\"" + clientId + "\", oauth_nonce=\"" + nnonce + "\", oauth_signature=\"" + encodeURIComponent(nsignature) + "\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"" + ntimeStamp + "\", oauth_token=\"" + access_token + "\", oauth_version=\"1.0\"";
            var httpContent = new Windows.Web.Http.HttpStringContent("oauth_verifier=" + oauth_verifier + '&' + "include_email=true", Windows.Storage.Streams.UnicodeEncoding.utf8, "application/json");
            var client = new Windows.Web.Http.HttpClient();
            client.defaultRequestHeaders.authorization = new Windows.Web.Http.Headers.HttpCredentialsHeaderValue("OAuth", nauthorizationHeaderParams);
            client.getStringAsync(new Windows.Foundation.Uri(ntwitterURL))/*, httpContent)*/.done(function (result) {
                var userInfo = JSON.parse(result);
                var userDetail = {};
                userDetail.id = id;
                userDetail.twitId = userInfo.id;
                userDetail.name = userInfo.name;
                userDetail.twitterName = userInfo.screen_name;
                userDetail.pictureURL = userInfo.profile_image_url;
                userDetail.email = userInfo.email;
                WinContainer.successCallback(userDetail);

            }, function (e) {
                WinContainer.failureCallback(id, "APZ-CNT-247", e.message);
                WinContainer.Log.error(e.message);
            });
        }
        catch (e) {
            WinContainer.failureCallback(id, "APZ-CNT-247", e.message);
            WinContainer.Log.error(e.description);
        }
    }
    return {
        Login: startLogin,
        loginSuccess: loginSuccess
    }

})();