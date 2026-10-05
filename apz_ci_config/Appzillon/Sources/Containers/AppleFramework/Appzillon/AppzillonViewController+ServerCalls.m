//
//  AppzillonViewController+ServerCalls.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 28/07/16.
//
//

#import "AppzillonViewController+ServerCalls.h"
#import "CallServer.h"
#import "Reachability.h"
#import "CryptoUtility.h"

@implementation AppzillonViewController (ServerCalls)
-(void)getServerNonce{
    //    BOOL isRechable = [self checkInternetReachbility];
    //    if(isRechable){
    NSMutableDictionary *appzillonHeader=[[NSMutableDictionary alloc]init];
    [appzillonHeader setObject:appString forKey:@"appId"];
    [appzillonHeader setObject:@"null" forKey:@"sessionId"];
    [appzillonHeader setObject:uniqueID forKey:@"deviceId"];
    [appzillonHeader setObject:@"CSNONCE" forKey:@"requestId"];
    [appzillonHeader setObject:[NSNumber numberWithBool:false] forKey:@"async"];
    [appzillonHeader setObject:@"null" forKey:@"userId"];
    [appzillonHeader setObject:@"Login" forKey:@"screenId"];
    [appzillonHeader setObject:[NSNumber numberWithBool:true] forKey:@"status"];
    [appzillonHeader setObject:@"APPZILLON" forKey:@"source"];
    [appzillonHeader setObject:[NSNumber numberWithBool:false] forKey:@"clientNonce"];
    [appzillonHeader setObject:@"appzillonGetAppSecTokens" forKey:@"interfaceId"];
    [appzillonHeader setObject:@"IOS" forKey:@"os"];
    [appzillonHeader setObject:ipAddress forKey:@"origination"];
    [appzillonHeader setObject:@"" forKey:@"requestKey"];
    NSMutableDictionary *appzillonBody=[[NSMutableDictionary alloc] init];
    NSMutableDictionary *appzillonGetAppSecTokensRequest=[[NSMutableDictionary alloc] init];
    [appzillonGetAppSecTokensRequest setObject:appString forKey:@"appId"];
    [appzillonGetAppSecTokensRequest setObject:uniqueID forKey:@"deviceId"];
    [appzillonBody setObject:appzillonGetAppSecTokensRequest forKey:@"appzillonGetAppSecTokensRequest"];
    NSMutableDictionary *getServerNonceRequest=[[NSMutableDictionary alloc]init];
    [getServerNonceRequest setObject:appzillonHeader forKey:@"appzillonHeader"];
    [getServerNonceRequest setObject:appzillonBody forKey:@"appzillonBody"];
    
    //    NSString *ipurl = [self.appPropertyDictionary objectForKey:@"serverUrl"];
    NSString *ipurl = self.APZServerURL;
    [CallServer callServerWithRequest:getServerNonceRequest :ipurl :@"getServerNonceRequestSuccess" :@"getServerNonceRequestFailure" :self :appString :self];
    //    }
}
-(void)getServerNonceRequestSuccess:(NSMutableDictionary*)successResult{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationWillEnterForegroundNotification object:nil];
    //    if(networkAlert != nil){
    //        [networkAlert dismissViewControllerAnimated:true completion:nil];
    //    }
    NSLog(@"getServerNonceRequestSuccess");
    NSDictionary *appzillonGetAppSecTokensResponse=[[successResult objectForKey:@"appzillonBody"] objectForKey:@"appzillonGetAppSecTokensResponse"];
    if ([appzillonGetAppSecTokensResponse objectForKey:@"status"]) {
        self.isServerNonceReceived = @"Y";
        //Hitting infra method to confirm that we received the serverNonce
        @synchronized(self){
            [webView evaluateJavaScript:[NSString stringWithFormat:@"apz.server.setAppSecToken('%@');",self.isServerNonceReceived] completionHandler:nil];
        }
        APZServerNonce= [appzillonGetAppSecTokensResponse objectForKey:@"serverNonce"];
        APZSafeToken = [appzillonGetAppSecTokensResponse objectForKey:@"safeToken"] ;
        APZSessionToken = [appzillonGetAppSecTokensResponse objectForKey:@"sessionToken"];
        APZServerToken = [CryptoUtility getAESDecryptedString:[self.appPropertyDictionary objectForKey:@"serverToken"] :SERVERTOKENDECRYPTKEY];
        if (self.serverNonceJSON==nil) {
            if ([appzillonGetAppSecTokensResponse objectForKey:@"serverNonce"]!=nil) {
                [self locationCheckBeforeMultifactorReg];
            }
        }else{
            NSArray *returnResultkeys=nil;
            NSArray *returnResult=nil;
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.serverNonceJSON objectForKey:PLUGINID] :true :false :returnResultkeys :returnResult]];
        }
        
    }
}
-(void)getServerNonceRequestFailure:(NSMutableDictionary*)failureResult{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationWillEnterForegroundNotification object:nil];
    //    if(networkAlert != nil){
    //        [networkAlert dismissViewControllerAnimated:true completion:nil];
    //    }
    //    [self showAlert:@"Server Error" alterMsg:@"Unable to connect to the server"];
    NSLog(@"getServerNonceRequestFailure");
    self.isServerNonceReceived = @"N";
    if ([[shared.containerPropsDictionary objectForKey:@"OFFLINESUPPORT"] isEqualToString:@"Y"] ||[[shared.containerPropsDictionary objectForKey:@"OFFLINESUPPORT"] isEqualToString:@"y"]) {
        [self injectHtmlInWebview];
    }
    [self performSelector:@selector(getServerNonce) withObject:self afterDelay:5.0 ];
}
//
//-(BOOL)checkInternetReachbility{
//    if ([[Reachability reachabilityForInternetConnection]currentReachabilityStatus]==NotReachable)
//    {
//        if(networkAlert != nil){
//            [networkAlert dismissViewControllerAnimated:true completion:nil];
//        }
//        [self showAlert:@"Network Error" alterMsg:@"No internet connection found"];
//        return  false;
//    }else{
//        return  true;
//    }
//}

//-(void)showAlert:(NSString *)title alterMsg:(NSString *)msg{
//    UIWindow* window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
//    window.rootViewController = [UIViewController new];
//    window.windowLevel = UIWindowLevelAlert + 1;
//    networkAlert = [UIAlertController alertControllerWithTitle:title message:msg preferredStyle:UIAlertControllerStyleAlert];
//    [networkAlert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
//        window.hidden = YES;
//    }]];
//    [window makeKeyAndVisible];
//    [window.rootViewController presentViewController:networkAlert animated:YES completion:nil];
//}

#pragma mark- multifactorRequest for Device Registartion
-(void)CallDeviceRegistration:(CLLocation *)location{
    //    BOOL isRechable = [self checkInternetReachbility];
    //    if(isRechable){
    self.runTimeDict = [[NSMutableDictionary alloc] initWithContentsOfFile:self.runTimeDictPath];
    NSUInteger registerdVersion=[[self.runTimeDict objectForKey:@"registeredVersion"] intValue];
    NSString *registrationDone=[self .runTimeDict objectForKey:@"deviceRegistrationDone"];
    NSUInteger versioninInt = [[[UIDevice currentDevice] systemVersion]intValue];
    self.currentVersion = [NSString stringWithFormat: @"%d", (int)versioninInt];
    if (registerdVersion < versioninInt || ![registrationDone isEqualToString:@"YES"]) {
        [self multifactorReq:self.currentVersion :location];
    }
    else if([appString isEqualToString:[shared.containerPropsDictionary objectForKey:@"MAINAPPID"]] &&(shared.isAppActive==YES))
    {
        [self appMasterDetailsRequest];
        [self APNSRegistration];
        shared.isAppActive=NO;
    }
    //    }
}

-(void)multifactorReq:(NSString *)version :(CLLocation *)location{
    //    BOOL isRechable = [self checkInternetReachbility];
    //    if(isRechable){
    NSString *latitude;
    NSString *longitude;
    if (location) {
        latitude = [[NSString alloc] initWithFormat:@"%f", location.coordinate.latitude];
        longitude = [[NSString alloc] initWithFormat:@"%f",location.coordinate.longitude];
    }else{
        latitude=@"";
        longitude=@"";
    }
    NSString *deviceModel =[UIDevice currentDevice].model;
    NSString *deviceName=[[UIDevice currentDevice] name];
    CGSize resolutionSize=[UIScreen mainScreen].bounds.size ;
    NSString *systemResolution=[NSString stringWithFormat:@"%ix%i",(int)ceil(resolutionSize.width),(int)ceil(resolutionSize.height)];
    NSMutableDictionary *deviceRegisterRequest=[[NSMutableDictionary alloc] init];
    NSMutableDictionary *appzillonHeader=[[NSMutableDictionary alloc] init];
    [appzillonHeader setObject:@"true" forKey:@"preLogin"];
    [appzillonHeader setObject:appString forKey:@"appId"];
    [appzillonHeader setObject:@"lauchApp" forKey:@"screenID"];
    [appzillonHeader setObject:@"000NEW" forKey:@"requestKey"];
    [appzillonHeader setObject:@"appzillonDeviceRegistration" forKey:@"interfaceId"];
    [appzillonHeader setObject:[NSNumber numberWithBool:true] forKey:@"status"];
    [appzillonHeader setObject:@"null" forKey:@"sessionId"];
    [appzillonHeader setObject:@"APPZILLON" forKey:@"source"];
    [appzillonHeader setObject:uniqueID forKey:@"deviceId"];
    [appzillonHeader setObject:ipAddress forKey:@"origination"];
    [appzillonHeader setObject:latitude forKey:@"latitude"];
    [appzillonHeader setObject:longitude forKey:@"longitude"];
    [appzillonHeader setObject:@"IOS" forKey:@"userId"];
    NSMutableDictionary *deviceRegisterRequestInBody=[[NSMutableDictionary alloc] init];
    [deviceRegisterRequestInBody setObject:@"IOS" forKey:@"os"];
    [deviceRegisterRequestInBody setObject:appString forKey:@"appId"];
    [deviceRegisterRequestInBody setObject:version forKey:@"osVersion"];
    [deviceRegisterRequestInBody setObject:uniqueID forKey:@"deviceId"];
    [deviceRegisterRequestInBody setObject:@"null" forKey:@"mobile1"];
    [deviceRegisterRequestInBody setObject:@"null" forKey:@"mobile2"];
    [deviceRegisterRequestInBody setObject:deviceModel forKey:@"model"];
    [deviceRegisterRequestInBody setObject:systemResolution forKey:@"screenResolution"];
    [deviceRegisterRequestInBody setObject:deviceName forKey:@"deviceName"];
    [deviceRegisterRequestInBody setObject:latitude forKey:@"latitude"];
    [deviceRegisterRequestInBody setObject:longitude forKey:@"longitude"];
    [deviceRegisterRequestInBody setObject:[[NSUserDefaults standardUserDefaults] objectForKey:@"storedBundleShortVersion"] forKey:@"appVersion"];
    [deviceRegisterRequestInBody setObject:@"Apple" forKey:@"make"];
    NSMutableDictionary *appzillonBody=[[NSMutableDictionary alloc]init];
    [appzillonBody setObject:deviceRegisterRequestInBody forKey:@"deviceRegisterRequest"];
    [deviceRegisterRequest setObject:appzillonHeader forKey:@"appzillonHeader"];
    [deviceRegisterRequest setObject:appzillonBody forKey:@"appzillonBody"];
    //    NSString *ipurl = [self.appPropertyDictionary objectForKey:@"serverUrl"];
    NSString *ipurl = self.APZServerURL;
    [CallServer callServerWithRequest:deviceRegisterRequest :ipurl :@"afRequestSuccessfulForDeviceReg" :@"afRequestFailureForDeviceReg" :self :appString :self];
    //    }
}

-(void)afRequestSuccessfulForDeviceReg:(NSMutableDictionary *)jsonDict{
    NSDictionary * jsonHeader = [jsonDict objectForKey:@"appzillonHeader"];
    BOOL responseStatus=[[jsonHeader objectForKey:@"status"] boolValue];
    if (responseStatus) {
        self.runTimeDict = [[NSMutableDictionary alloc] initWithContentsOfFile:self.runTimeDictPath];
        [self.runTimeDict setObject:@"YES" forKey:@"deviceRegistrationDone"];
        [self.runTimeDict setObject:self.currentVersion forKey:@"registeredVersion"];
        [self.runTimeDict writeToFile:self.runTimeDictPath atomically: YES];
        if(shared.isAppActive){
            [self appMasterDetailsRequest];
            shared.isAppActive=NO;
            NSLog(@"Device Registration Done");
        }
    }else{
        NSLog(@"Device Registration failure");
    }
    [self APNSRegistration];
}

-(void)afRequestFailureForDeviceReg:(NSMutableDictionary *)response{
    NSLog(@"Device Registration failure");
}

-(void)appMasterDetailsRequest{
    NSMutableDictionary *appzillonHeader=[[NSMutableDictionary alloc]init];
    [appzillonHeader setObject:@"true" forKey:@"preLogin"];
    [appzillonHeader setObject:appString forKey:@"appId"];
    [appzillonHeader setObject:uniqueID forKey:@"deviceId"];
    [appzillonHeader setObject:@"APPZILLON" forKey:@"source"];
    [appzillonHeader setObject:ipAddress forKey:@"origination"];
    [appzillonHeader setObject:@"asa" forKey:@"sessionId"];
    [appzillonHeader setObject:@"IOS" forKey:@"userId"];
    [appzillonHeader setObject:[NSNumber numberWithBool:true] forKey:@"status"];
    [appzillonHeader setObject:@"sad" forKey:@"requestKey"];
    [appzillonHeader setObject:@"appzillonGetAppMasterDetails" forKey:@"interfaceId"];
    [appzillonHeader setObject:@"login" forKey:@"screenId"];
    NSMutableDictionary *appzillonAppMasterRequestInBody=[[NSMutableDictionary alloc]init];
    [appzillonAppMasterRequestInBody setObject:appString forKey:@"appId"];
    [appzillonAppMasterRequestInBody setObject:@"IOS" forKey:@"os"];
    [appzillonAppMasterRequestInBody setObject:uniqueID forKey:@"deviceId"];
    [appzillonAppMasterRequestInBody setObject:[[NSUserDefaults standardUserDefaults] objectForKey:@"storedBundleShortVersion"] forKey:@"appVersion"];
    [appzillonAppMasterRequestInBody setObject:[[NSUserDefaults standardUserDefaults] objectForKey:@"updateAppVersion"] forKey:@"updateAppVersion"];
    NSMutableDictionary *appzillonBody=[[NSMutableDictionary alloc] init];
    [appzillonBody setObject:appzillonAppMasterRequestInBody forKey:@"appzillonAppMasterRequest"];
    NSMutableDictionary *appzillonAppMasterRequest=[[NSMutableDictionary alloc]init];
    [appzillonAppMasterRequest setObject:appzillonBody forKey:@"appzillonBody"];
    [appzillonAppMasterRequest setObject:appzillonHeader forKey:@"appzillonHeader"];
    //    NSMutableString * concatstr=[@"{\"appzillonHeader\":{\"preLogin\":\"true\",\"appId\": \"" mutableCopy];
    //    NSString *deviceID=[[NSUserDefaults standardUserDefaults] objectForKey:@"uniqueID"];
    //    [concatstr appendString:appString];
    //    [concatstr appendString:@"\",\"deviceId\":\""];
    //    [concatstr appendString:deviceID];
    //    [concatstr appendString:@"\",\"source\":\"APPZILLON"];
    //    [concatstr appendString:@"\",\"origination\":\""];
    //    [concatstr appendString:ipAddress];
    //    [concatstr appendString:@"\",\"serverNonce\":\""];
    //    [concatstr appendString:[self.appPropertyDictionary objectForKey:@"serverNonce"]];
    //    [concatstr appendString:@"\",\"sessionToken\":\""];
    //    [concatstr appendString:[self.appPropertyDictionary objectForKey:@"sessionToken"]];
    //    [concatstr appendString:@"\",\"clientNonce\":\""];
    //    [concatstr appendString:[CallServer getCurrnetDateTime]];
    //    [concatstr appendString:@"\",\"sessionId\":\"asa"];
    //    [concatstr appendString:@" "];
    //    [concatstr appendString:@"\",\"userId\":\"IOS\",\"status\":true,"];
    //    [concatstr appendString:@"\"requestKey\":\""];
    //    [concatstr appendString:@"sad"];
    //    [concatstr appendString:@"\",\"interfaceId\":\"appzillonGetAppMasterDetails\","];
    //    [concatstr appendString:@"\"screenId\":\"login\"},\"appzillonBody\":{\"appzillonAppMasterRequest\":\{\"appId\": \""];
    //    [concatstr appendString:appString];
    //    [concatstr appendString:@"\",\"os\":\"IOS"];
    //    [concatstr appendString:@"\",\"deviceId\":\""];
    //    [concatstr appendString:deviceID];
    //    [concatstr appendString:@"\"}}}"];
    //    NSString *ipurl = [self.appPropertyDictionary objectForKey:@"serverUrl"];
    NSString *ipurl = self.APZServerURL;
    //    NSURL *serverURL = [NSURL URLWithString:ipurl];
    //    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:serverURL];
    //    [request setHTTPBody:[concatstr dataUsingEncoding:NSUTF8StringEncoding]];
    [CallServer callServerWithRequest:appzillonAppMasterRequest :ipurl :@"afRequestSuccessfulForAppMasterDetailsRequest" :@"afRequestFailureForAppMasterDetailsRequest" :self :appString :self];
}

-(void)afRequestSuccessfulForAppMasterDetailsRequest:(NSMutableDictionary *)jsonDict{
    [self injectHtmlInWebview];
    NSDictionary * jsonBody = (NSMutableDictionary *) [ jsonDict objectForKey:@"appzillonBody"];
    NSDictionary * jsonHeader = (NSMutableDictionary *) [ jsonDict objectForKey:@"appzillonHeader"];
    BOOL status=[[jsonHeader objectForKey:@"status"] boolValue];
    if (status) {
        NSMutableDictionary *mainAppInfo=[[jsonBody objectForKey:[shared.containerPropsDictionary objectForKey:@"MAINAPPID"]]mutableCopy];
        NSString *wipeoutFlag=[mainAppInfo objectForKey:@"wipeout"];
        if ([wipeoutFlag isEqualToString:@"Y"]) {
            [self EmptySandbox];
        }else{
            NSString *mainAppPropertyPath = [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",[shared.containerPropsDictionary objectForKey:@"MAINAPPID"]]];
            NSMutableDictionary* mainAppDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:mainAppPropertyPath];
            if([[shared.containerPropsDictionary objectForKey:@"OTAREQUIRED"] isEqualToString:@"Y"]){
                NSString *appVersion=[mainAppInfo objectForKey:@"appVersion"];
                NSString  *storedAppVersion = [CryptoUtility decryptSingleValue:[mainAppDictionary objectForKey:@"appVersion"]];
                if (![appVersion isEqualToString:storedAppVersion]) {
                    [mainAppInfo setObject:@"Y" forKey:@"upgradeRequired"];
                    [mainAppDictionary setObject:[CryptoUtility encryptSingleValue:@"Y"] forKey:@"upgradeRequired"];
                    [mainAppDictionary writeToFile:mainAppPropertyPath atomically:YES];
                }else{
                    [mainAppInfo setObject:@"N" forKey:@"upgradeRequired"];
                    [mainAppDictionary setObject:[CryptoUtility encryptSingleValue:@"N"] forKey:@"upgradeRequired"];
                    [mainAppDictionary writeToFile:mainAppPropertyPath atomically:YES];
                }
            }else{
                [mainAppDictionary setObject:[CryptoUtility encryptSingleValue:@"N"] forKey:@"upgradeRequired"];
                [mainAppInfo setObject:@"N" forKey:@"upgradeRequired"];
                [mainAppDictionary writeToFile:mainAppPropertyPath atomically:YES];
            }
            [mainAppDictionary setObject:[CryptoUtility encryptPlistData:mainAppInfo] forKey:@"masterDetailsResponse"];
            [mainAppDictionary setObject:[CryptoUtility encryptSingleValue:[mainAppInfo objectForKey:@"updateAction"]] forKey:@"updateAction"];
            [mainAppDictionary writeToFile:mainAppPropertyPath atomically:YES];
            NSString *json = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:mainAppInfo options:0 error:nil] encoding:NSUTF8StringEncoding];
            @synchronized(self) {
                [webView evaluateJavaScript:[NSString stringWithFormat:@"appzillon.util.instructions(%@);",json] completionHandler:nil];
            }
        }
    }
}
-(void)afRequestFailureForAppMasterDetailsRequest:(NSMutableDictionary *)responseString{
    [self injectHtmlInWebview];
    NSString *mainAppPropertyPath = [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",[shared.containerPropsDictionary objectForKey:@"MAINAPPID"]]];
    NSMutableDictionary* mainAppDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:mainAppPropertyPath];
    //    if([mainAppDictionary objectForKey:@"masterDetailsResponse"]!=NULL)
    if([mainAppDictionary objectForKey:@"masterDetailsResponse"]!=NULL){
        [webView evaluateJavaScript:[NSString stringWithFormat:@"appzillon.util.instructions('%@')",[CryptoUtility decryptPlistData:[mainAppDictionary objectForKey:@"masterDetailsResponse"]]] completionHandler:nil];
        
    }
}

-(void)notificationRegWithToken{
    self.runTimeDictPath=[[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/Container.plist",appString]];
    self.runTimeDict = [[NSMutableDictionary alloc] initWithContentsOfFile:self.runTimeDictPath];
    self.tokenStored=[self.runTimeDict objectForKey:@"tokenStored"];
    NSString *serverRegDone=[self.runTimeDict objectForKey:@"serverRegistrationDone"];
    if ( (![serverRegDone isEqualToString:@"YES"])||(![shared.regID isEqualToString:self.tokenStored])) {
        if(shared.regID){
            NSString *NotificationStatus = [shared.containerPropsDictionary objectForKey:@"NOTIFICATION"];
            if ([NotificationStatus isEqualToString:@"Y"]) {
                [self.runTimeDict setObject:shared.regID forKey:@"tokenStored"];
                [self.runTimeDict writeToFile:self.runTimeDictPath atomically: YES];
                [self registerAppzillonAPNS];
            }
        }
        else{
            NSLog(@"Could not connect to apns, check your certificates and Wifi connection");
        }
    }
    else{
        NSLog(@"Device is already Registerd in Specified Server");
    }
    
}


#pragma mark - Wipeout the sandbox
-(void)EmptySandbox
{
    [webView evaluateJavaScript:[NSString stringWithFormat:@"localStorage.clear();"] completionHandler:nil];
    NSFileManager *fileMgr = [[NSFileManager alloc] init];
    NSError *error = nil;
    NSArray *files = [fileMgr contentsOfDirectoryAtPath:self.sandboxPath error:nil];
    while (files.count > 0) {
        NSArray *directoryContents = [fileMgr contentsOfDirectoryAtPath:self.sandboxPath error:&error];
        if (error == nil) {
            for (NSString *path in directoryContents) {
                NSString *fullPath = [self.sandboxPath stringByAppendingPathComponent:path];
                BOOL removeSuccess = [fileMgr removeItemAtPath:fullPath error:&error];
                files = [fileMgr contentsOfDirectoryAtPath:self.sandboxPath error:nil];
                if (!removeSuccess) {
                    NSLog(@"Error in deleteing Sandbox");
                }
            }
        } else {
            NSLog(@"Error in deleteing Sandbox");
        }
    }
    //    NSString *containerPlist = [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Container.plist"]];
    //    NSFileManager *fileManager = [NSFileManager defaultManager];
    //    if (![fileManager fileExistsAtPath:containerPlist]){
    //        NSMutableDictionary *flagDictionary = [[NSMutableDictionary alloc] init];
    //        [flagDictionary setObject:@"YES" forKey:@"wipedOut"];
    //        [flagDictionary writeToFile:containerPlist atomically: YES];
    //    }
    [[NSUserDefaults standardUserDefaults] setObject:@"YES" forKey:@"wipedOut"];
    self.appWiped=YES;
}
#pragma mark Server Reg for Notifications request
-(void) registerAppzillonAPNS
{
    //    NSString *ipurl = [self.appPropertyDictionary objectForKey:@"serverUrl"];
    NSString *ipurl = self.APZServerURL;
    NSString *deviceID=[[NSUserDefaults standardUserDefaults] objectForKey:@"uniqueID"];
    NSUInteger versioninInt = [[[UIDevice currentDevice] systemVersion]integerValue];
    NSString *version = [NSString stringWithFormat: @"%d", (int)versioninInt];
    NSString *reg = [shared.regID mutableCopy];
    NSString *deviceName=[[UIDevice currentDevice] name];
    reg=[reg stringByReplacingOccurrencesOfString:@"<" withString:@""];
    reg=[reg stringByReplacingOccurrencesOfString:@">" withString:@""];
    reg=[reg stringByReplacingOccurrencesOfString:@" " withString:@""];
    NSMutableDictionary *appzillonHeader=[[NSMutableDictionary alloc] init];
    [appzillonHeader setObject:appString forKey:@"appId"];
    [appzillonHeader setObject:@"sessionId" forKey:@"sessionId"];
    [appzillonHeader setObject:uniqueID forKey:@"deviceId"];
    [appzillonHeader setObject:@"000NEW" forKey:@"requestKey"];
    [appzillonHeader setObject:@"IOS" forKey:@"userId"];
    [appzillonHeader setObject:@"login" forKey:@"screenId"];
    [appzillonHeader setObject:@"false" forKey:@"async"];
    [appzillonHeader setObject:@"" forKey:@"requestID"];
    [appzillonHeader setObject:[NSNumber numberWithBool:true] forKey:@"status"];
    [appzillonHeader setObject:@"appzillonNotificationRegistration" forKey:@"interfaceId"];
    
    NSMutableDictionary *appzillonBody=[[NSMutableDictionary alloc] init];
    [appzillonBody setObject:deviceID forKey:@"deviceId"];
    [appzillonBody setObject:@"APPZILLON" forKey:@"source"];
    [appzillonBody setObject:ipAddress forKey:@"origination"];
    [appzillonBody setObject:@"iOS" forKey:@"osId"];
    [appzillonBody setObject:reg forKey:@"regId"];
    [appzillonBody setObject:deviceName forKey:@"deviceName"];
    [appzillonBody setObject:version forKey:@"osVersion"];
    [appzillonBody setObject:appString forKey:@"appId"];
    NSMutableDictionary *appzillonNotifRegRequest=[[NSMutableDictionary alloc] init];
    [appzillonNotifRegRequest setObject:appzillonBody forKey:@"appzillonBody"];
    [appzillonNotifRegRequest setObject:appzillonHeader forKey:@"appzillonHeader"];
    //    NSMutableString * concatstr=[@"{\"appzillonHeader\":{\"appId\": \"" mutableCopy];
    //    [concatstr appendString:appString];
    //    [concatstr appendString:@"\",\"sessionId\":\"sessionId\",\"deviceId\":\""];
    //    [concatstr appendString:deviceID];
    //    [concatstr appendString:@"\",\"serverNonce\":\""];
    //    [concatstr appendString:[self.appPropertyDictionary objectForKey:@"serverNonce"]];
    //    [concatstr appendString:@"\",\"sessionToken\":\""];
    //    [concatstr appendString:[self.appPropertyDictionary objectForKey:@"sessionToken"]];
    //    [concatstr appendString:@"\",\"clientNonce\":\""];
    //    [concatstr appendString:[CallServer getCurrnetDateTime]];
    //    [concatstr appendString:@"\",\"requestKey\":\"000NEW\",\"userId\":\"IOS\",\"screenId\":\"login\",\"async\":\"false\",\"requestID\":\"\",\"status\":true,"];
    //    [concatstr appendString:@"\"interfaceId\":\"appzillonNotificationRegistration\"},\"appzillonBody\":{\"deviceId\":\""];
    //    [concatstr appendString:deviceID];
    //    [concatstr appendString:@"\",\"source\":\"APPZILLON"];
    //    [concatstr appendString:@"\",\"origination\":\""];
    //    [concatstr appendString:ipAddress];
    //    [concatstr appendString:@"\",\"osId\":\"iOS\",\"regId\":\""];
    //    [concatstr appendString:reg];
    //    [concatstr appendString:@"\",\"deviceName\":\""];
    //    [concatstr appendString:deviceName];
    //    [concatstr appendString:@"\",\"osVersion\":\"" ];
    //    [concatstr appendString:version];
    //    [concatstr appendString:@"\",\"appId\":\""];
    //    [concatstr appendString:appString];
    //    [concatstr appendString:@"\"}}"];
    //    NSURL *serverURL = [NSURL URLWithString:ipurl];
    //    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:serverURL];
    //    [request setHTTPBody:[appzillonNotifRegRequest dataUsingEncoding:NSUTF8StringEncoding]];
    [CallServer callServerWithRequest:appzillonNotifRegRequest :ipurl :@"afRequestSuccessful" :@"afRequestFailure" :self :appString :self];
}

-(void)afRequestSuccessful:(NSMutableDictionary *)jsonDict{
    NSMutableDictionary * jsonBody = (NSMutableDictionary *) [ jsonDict objectForKey:@"appzillonHeader"];
    BOOL val = [[jsonBody objectForKey:@"status"] boolValue];
    if (val) {
        self.runTimeDict = [[NSMutableDictionary alloc] initWithContentsOfFile:self.runTimeDictPath];
        [self.runTimeDict setObject:@"YES" forKey:@"serverRegistrationDone"];
        [self.runTimeDict writeToFile:self.runTimeDictPath atomically: YES];
    }else{
        NSLog(@"Registration Failed for Push Notification");
    }
}

-(void)afRequestFailure:(NSMutableDictionary *)responseString{
    NSLog(@"----pushnotifircation registration failure----");
}
-(void)APNSRegistration{
    
#if __IPHONE_OS_VERSION_MIN_REQUIRED > __IPHONE_7_0 && __IPHONE_OS_VERSION_MIN_REQUIRED < __IPHONE_10_0
    
    [[UIApplication sharedApplication] registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:(UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge) categories:nil]];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [[UIApplication sharedApplication] registerForRemoteNotifications];
    });
    
#elif __IPHONE_OS_VERSION_MIN_REQUIRED > __IPHONE_10_0
    if (@available(iOS 10.0, *)) {
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
        UNAuthorizationOptions options = UNAuthorizationOptionAlert + UNAuthorizationOptionSound;
        center.delegate=shared.self;
        [center getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings * _Nonnull settings) {
            if (settings.authorizationStatus != UNAuthorizationStatusAuthorized) {
                [center requestAuthorizationWithOptions:options
                                      completionHandler:^(BOOL granted, NSError * _Nullable error) {
                    if (granted) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [[UIApplication sharedApplication] registerForRemoteNotifications];
                        });
                    }
                    
                }];
            }
        }];
        //    [center removeAllDeliveredNotifications];
        //    [center removeAllPendingNotificationRequests];
        [self notificationRegister:center];
    }
#else
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes: (UIRemoteNotificationTypeNewsstandContentAvailability| UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
    
#endif
    
    
}

-(void)locationCheckBeforeMultifactorReg{
    if ([[self.appPropertyDictionary objectForKey:@"trackLocation"]isEqualToString:@"Y"])
    {
        [self locationPermission];
    }
    else
    {
        [self CallDeviceRegistration:nil];
    }
    //    BOOL isFirstLaunch = [[NSUserDefaults standardUserDefaults] boolForKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",appString]];
    //    if (isFirstLaunch == NO)
    //    {
    //        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",appString]];
    //    }
    if (shared.pushMessege) {
        [self notifCallback];
    }
    self.calledFromPlugin=NO;
}
-(void)notificationRegister:(UNUserNotificationCenter *)center{
    NSString* domainPath=[[NSString alloc]initWithFormat:@"/Assets/apps/%@/screens/config/",appString];
    NSString *path = [[MiscellaneousMethods getAppBundle] pathForResource:@"notif_details" ofType:@"json" inDirectory:domainPath];
    if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {
        NSData *data = [NSData dataWithContentsOfFile:path];
        NSArray*notifJSON=[NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
        NSSet *catagorySet =[[NSSet alloc]init];
        for (NSDictionary *notificationJSON in notifJSON) {
            UNMutableNotificationContent *content = [UNMutableNotificationContent new];
            content.categoryIdentifier =[notificationJSON objectForKey:@"notification_code"];
            NSArray *actions=[notificationJSON objectForKey:@"actions"];
            NSMutableArray *actionArray=[[NSMutableArray alloc]init];
            for (NSDictionary *actionDic in actions) {
                UNNotificationAction *notifAction = [UNNotificationAction actionWithIdentifier:[actionDic objectForKey:@"action_code"] title:[actionDic objectForKey:@"action_display"] options:UNNotificationActionOptionForeground];
                [actionArray addObject:notifAction];
            }
            
            UNNotificationCategory *catagory = [UNNotificationCategory categoryWithIdentifier:[notificationJSON objectForKey:@"notification_code"] actions:actionArray intentIdentifiers:@[] options:UNNotificationCategoryOptionNone];
            catagorySet= [catagorySet setByAddingObject:catagory];
        }
        [center setNotificationCategories:catagorySet];
    }
    
}
-(void)startNetworkRechabilityListener{
    [[AFNetworkReachabilityManager sharedManager] startMonitoring];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(reachabilityChanged:)
                                                 name:AFNetworkingReachabilityDidChangeNotification
                                               object:nil];
}
-(void)stopNetworkReachabilityListener{
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:AFNetworkingReachabilityDidChangeNotification
                                                  object:nil];
}
- (void)reachabilityChanged:(NSNotification *)notification
{
    NSNumber *statusItem = notification.userInfo[AFNetworkingReachabilityNotificationStatusItem];
    AFNetworkReachabilityStatus status = [statusItem integerValue];
    NSArray *returnResultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
    NSArray *returnResult=nil;
    if ((status==1) ||(status ==2)) {
        returnResult=[NSArray arrayWithObjects:@"on",nil];
    }else{
        returnResult=[NSArray arrayWithObjects:@"off",nil];
    }
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.nwListenerDict objectForKey:PLUGINID] :true :true :returnResultkeys :returnResult]];
}
@end





