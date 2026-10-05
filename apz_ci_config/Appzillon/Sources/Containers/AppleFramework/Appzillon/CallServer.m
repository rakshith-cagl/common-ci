//
//  CallServer.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 09/11/17.
//

#import "CallServer.h"
#import "AFNetworking/AFNetworking.h"
#import "Constants.h"
#import "APZJsonUtil.h"
#import "HashUtility.h"
#import "CryptoUtility.h"
#import "Base64.h"
#import "Reachability.h"


@implementation CallServer
UIAlertController *networkAlert;

+(void)callServerWithRequest :(NSMutableDictionary*)requestDict :(NSString *)serverIP :(NSString*)successCB :(NSString*)failureCB :(id)className :(NSString*)appID :(AppzillonViewController*)APZViewController {
    //    BOOL isRechable = [self checkInternetReachbility];
    //    if(isRechable){
    NSString *interfaceId=[[requestDict objectForKey:@"appzillonHeader"] objectForKey:@"interfaceId"];
    BOOL isRechable = [self checkInternetReachbility];
    if(isRechable){
        NSString *appPropertyPath= [self getAppPropertiesDictionaryPath:appID];
        NSMutableDictionary *appPropertiesDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:appPropertyPath];
        NSString*clientNonce=[CallServer getCurrnetDateTime];
        [requestDict setObject:[self getUpdatedAppzillonHeader:requestDict :appPropertiesDictionary :clientNonce :APZViewController] forKey:@"appzillonHeader"];
        
        NSMutableDictionary *plainRequest=requestDict;
        NSString *requestString;
        if ([interfaceId isEqualToString:@"appzillonFilePushService"]||[interfaceId isEqualToString:@"appzillonFilePushServiceWSRequest"]) {
            requestString=[self getStringObject:requestDict];
        }else{
            requestDict=[self payloadEncryption:appPropertiesDictionary :requestDict :appID :interfaceId :APZViewController];
            requestString=[self startHashingProcess:appPropertiesDictionary :plainRequest :interfaceId :clientNonce :requestDict :APZViewController];
        }
        
        //Add interfaceID to the severUrl, if user prefers this:
        if ([[APZViewController.appPropertyDictionary objectForKey:@"IFACEIDINURI"] isEqualToString:@"Y"]) {
            serverIP = [NSString stringWithFormat: @"%@/services/%@", serverIP,interfaceId];
        }
        
        NSURL *serverURL = [NSURL URLWithString:serverIP];
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:serverURL];
        [request setHTTPBody:[requestString dataUsingEncoding:NSUTF8StringEncoding]];
        request=[self createHttpHeader:request :@"POST"];
        //        AFHTTPSessionManager *client = [[AFHTTPSessionManager alloc] initWithBaseURL:serverURL sessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
        AFHTTPSessionManager *client = [self getAFHTTPSessionManager:serverURL];
        //        if ([[appPropertiesDictionary objectForKey:@"sslPinning"]isEqualToString:@"Y"])
        if ([[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"sslPinning"]]isEqualToString:@"Y"]){
            client.securityPolicy=[self setSecurityPolicyForSSLPinning:appID];
        }else if([[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"trustAllCertificates"]]isEqualToString:@"Y"]){
            client.securityPolicy.allowInvalidCertificates=YES;
            client.securityPolicy.validatesDomainName=NO;
        }
        NSURLSessionDataTask *dataTask = [client dataTaskWithRequest:request uploadProgress:nil downloadProgress:nil completionHandler:^(NSURLResponse *response, id responseObject, NSError *error ,NSData *responseData) {
            if (error) {
                NSString* methodName = [NSString stringWithFormat:@"%@:",failureCB];
                SEL normalSelector = NSSelectorFromString(methodName);
                if ([className respondsToSelector:normalSelector])
                {
                    NSMutableDictionary *errorCodeDictionary=[[NSMutableDictionary alloc]init];
                    [errorCodeDictionary setValue:[NSString stringWithFormat:@"%ld",(long)error.code ]forKey:@"errorCode"];
                    [errorCodeDictionary setValue:error.localizedDescription forKey:@"errorMsg"];
                    [self showAlertMessage:isRechable:interfaceId];
                    [className performSelector:normalSelector withObject:errorCodeDictionary];
                }
            } else {
                NSMutableDictionary *finalResponse= [self payloadDecryption__DataIntegrityCheck:appPropertiesDictionary :responseObject :appID :appPropertyPath :interfaceId :clientNonce :responseData :APZViewController];
                if ([finalResponse objectForKey:@"dataIntegrityValidation"]) {
                    [finalResponse removeObjectForKey:@"dataIntegrityValidation"];
                    NSString* methodName = [NSString stringWithFormat:@"%@:",successCB];
                    SEL normalSelector = NSSelectorFromString(methodName);
                    if ([className respondsToSelector:normalSelector])
                    {
                        [className performSelector:normalSelector withObject:finalResponse];
                    }
                }
                
            }
        }];
        [dataTask resume];
    }else{
        if([self nativeAlertRequired:interfaceId]){
            [self showAlertMessage:isRechable :interfaceId];
        }
        [self callFailureCallback:className :failureCB];
    }
    //    }
}


+(void)callServerForUploadWithRequest :(NSMutableDictionary*)requestDict :(NSString*)successCB :(NSString*)failureCB :(id)className :(NSString*)appID :(NSString*)serverIP :(NSString*)path :(NSString*)fileName :(NSString*)mimeType :(AppzillonViewController*)APZViewController{
    //    BOOL isRechable = [self checkInternetReachbility];
    //    if(isRechable){
    NSString *interfaceId=[[requestDict objectForKey:@"appzillonHeader"] objectForKey:@"interfaceId"];
    BOOL isRechable = [self checkInternetReachbility];
    if(isRechable){
        NSString *appPropertyPath= [self getAppPropertiesDictionaryPath:appID];
        NSMutableDictionary *appPropertiesDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:appPropertyPath];
        NSString*clientNonce=[CallServer getCurrnetDateTime];
        [requestDict setObject:[self getUpdatedAppzillonHeader:requestDict :appPropertiesDictionary :clientNonce :APZViewController] forKey:@"appzillonHeader"];
        NSError *err;
        NSData *dataFromDict = [NSJSONSerialization dataWithJSONObject:requestDict
                                                               options:NSJSONWritingPrettyPrinted
                                                                 error:&err];
        NSMutableURLRequest *request = [[AFHTTPRequestSerializer serializer] multipartFormRequestWithMethod:@"POST" URLString:serverIP parameters:nil constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
            [formData appendPartWithFileURL:[NSURL fileURLWithPath:path] name:fileName fileName:fileName mimeType:mimeType error:nil];
            [formData appendPartWithFormData:dataFromDict name:@"appzillonRequest"];
        } error:nil];
        [request setHTTPMethod:@"POST"];
        //        AFURLSessionManager *manager = [[AFURLSessionManager alloc] initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
        AFURLSessionManager *manager = [self getAFURLSessionManager];
        //        if ([[appPropertiesDictionary objectForKey:@"sslPinning"]isEqualToString:@"Y"])
        if ([[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"sslPinning"]]isEqualToString:@"Y"]) {
            manager.securityPolicy=[self setSecurityPolicyForSSLPinning:appID];
        }else if([[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"trustAllCertificates"]]isEqualToString:@"Y"]){
            manager.securityPolicy.allowInvalidCertificates=YES;
            manager.securityPolicy.validatesDomainName=NO;
        }
        NSURLSessionUploadTask *uploadTask;
        uploadTask = [manager
                      uploadTaskWithStreamedRequest:request
                      progress:^(NSProgress * _Nonnull uploadProgress) {
            dispatch_async(dispatch_get_main_queue(), ^{
                
            });
        }
                      completionHandler:^(NSURLResponse * response, id responseObject, NSError* error) {
            if (error) {
                NSString* methodName = [NSString stringWithFormat:@"%@:",failureCB];
                SEL normalSelector = NSSelectorFromString(methodName);
                if ([className respondsToSelector:normalSelector])
                {
                    NSMutableDictionary *errorCodeDictionary=[[NSMutableDictionary alloc]init];
                    [errorCodeDictionary setValue:[NSString stringWithFormat:@"%ld",(long)error.code ]forKey:@"errorCode"];
                    [errorCodeDictionary setValue:error.localizedDescription forKey:@"errorMsg"];
                    [className performSelector:normalSelector withObject:errorCodeDictionary];
                }
            } else {
                NSString* methodName = [NSString stringWithFormat:@"%@:",successCB];
                SEL normalSelector = NSSelectorFromString(methodName);
                if ([className respondsToSelector:normalSelector])
                {
                    [className performSelector:normalSelector withObject:responseObject];
                }
            }
        }];
        [uploadTask resume];
    }else{
        if([self nativeAlertRequired:interfaceId]){
            [self showAlertMessage:isRechable :interfaceId];
        }
        [self callFailureCallback:className :failureCB];
    }
}

+(void)callServerFromInfraWithRequest :(NSMutableDictionary*)requestJSON :(AppzillonViewController*)viewController :(WKWebView*)webView :(NSString*)appID{
    BOOL isRechable = [self checkInternetReachbility];
    if(isRechable){
        NSString *appPropertyPath= [self getAppPropertiesDictionaryPath:appID];
        NSMutableDictionary *appPropertiesDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:appPropertyPath];
        NSMutableDictionary *infraReqJson =[[requestJSON objectForKey:@"params"] objectForKey:@"reqFull"];
        NSString *interfaceId=[[infraReqJson objectForKey:@"appzillonHeader"] objectForKey:@"interfaceId"];
        NSString*clientNonce=[CallServer getCurrnetDateTime];
        [infraReqJson setObject:[self getUpdatedAppzillonHeader:infraReqJson :appPropertiesDictionary :clientNonce :viewController] forKey:@"appzillonHeader"];
        NSMutableDictionary *plainRequest=infraReqJson;
        infraReqJson=[self payloadEncryption:appPropertiesDictionary :infraReqJson :appID :interfaceId :viewController];
        [[requestJSON objectForKey:@"params"] setObject:infraReqJson forKey:@"reqFull"];
        
         //Add interfaceID to the severUrl, if user prefers this:
        NSURL *serverURL;
        if ([[viewController.appPropertyDictionary objectForKey:@"IFACEIDINURI"] isEqualToString:@"Y"]) {
//            serverURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@/services/%@",[[requestJSON objectForKey:@"params"] objectForKey:@"url"],interfaceId]];
            serverURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@/services/%@",viewController.APZServerURL,interfaceId]];
        }else{
//            serverURL = [NSURL URLWithString:[[requestJSON objectForKey:@"params"] objectForKey:@"url"]];
            serverURL = [NSURL URLWithString:viewController.APZServerURL];
        }
        
        NSString *reqId=[requestJSON objectForKey:@"reqId"];
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:serverURL];
        request=[self createHttpHeader:request :@"POST"];
        NSString *requestString= [self startHashingProcess:appPropertiesDictionary :plainRequest :interfaceId :clientNonce :infraReqJson :viewController];
        [request setHTTPBody:[requestString dataUsingEncoding:NSUTF8StringEncoding]];
        //        AFHTTPSessionManager *client = [[AFHTTPSessionManager alloc] initWithBaseURL:serverURL sessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
        AFHTTPSessionManager *client = [self getAFHTTPSessionManager:serverURL];
        //        if ([[appPropertiesDictionary objectForKey:@"sslPinning"]isEqualToString:@"Y"])
        if ([[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"sslPinning"]]isEqualToString:@"Y"]){
            client.securityPolicy=[self setSecurityPolicyForSSLPinning:appID];
        }else if([[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"trustAllCertificates"]]isEqualToString:@"Y"]){
            client.securityPolicy.allowInvalidCertificates=YES;
            client.securityPolicy.validatesDomainName=NO;
        }
        NSURLSessionDataTask *dataTask = [client dataTaskWithRequest:request uploadProgress:nil downloadProgress:nil completionHandler:^(NSURLResponse *response, id responseObject, NSError *error ,NSData *responseData) {
            if (error) {
                @synchronized(self) {
                    NSMutableDictionary *errorJSON=[[NSMutableDictionary alloc] init];
                    [errorJSON setValue:[NSString stringWithFormat:@"%ld",(long)error.code ]forKey:@"errorCode"];
                    [errorJSON setValue:error.localizedDescription forKey:@"errorMsg"];
                    NSDictionary *responseJSON=[[NSDictionary alloc] initWithObjectsAndKeys:[[requestJSON objectForKey:@"params"] objectForKey:@"reqFull"],@"reqFull",[NSNumber numberWithBool:false],@"status",nil];
                    NSArray *resultkeys=[NSArray arrayWithObjects:@"params",@"error",@"reqId",nil];
                    NSArray *result=[NSArray arrayWithObjects:responseJSON,errorJSON,reqId,nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:[requestJSON objectForKey:PLUGINID] :false :false :resultkeys :result]];
                }
            } else {
                if (responseObject) {
                    NSMutableDictionary *finalResponse= [self payloadDecryption__DataIntegrityCheck:appPropertiesDictionary :responseObject :appID :appPropertyPath :interfaceId :clientNonce :responseData :viewController];
                    if ([finalResponse objectForKey:@"dataIntegrityValidation"]) {
                        [finalResponse removeObjectForKey:@"dataIntegrityValidation"];
                        NSDictionary *responseJSON=[[NSDictionary alloc] initWithObjectsAndKeys:finalResponse,@"resFull",[NSNumber numberWithBool:true],@"status",nil];
                        NSArray *resultkeys=[NSArray arrayWithObjects:@"params",@"reqId",nil];
                        NSArray *result=[NSArray arrayWithObjects:responseJSON,reqId,nil];
                        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:[requestJSON objectForKey:PLUGINID] :true :false :resultkeys :result]];
                    }else{
                        NSDictionary *responseJSON=[[NSDictionary alloc] initWithObjectsAndKeys:finalResponse,@"resFull",[NSNumber numberWithBool:false],@"status",nil];
                        NSArray *resultkeys=[NSArray arrayWithObjects:@"params",@"reqId",ERROR_CODE,nil];
                        NSArray *result=[NSArray arrayWithObjects:responseJSON,reqId,@"APZ-CNT-230",nil];
                        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:[requestJSON objectForKey:PLUGINID] :false :false :resultkeys :result]];
                    }
                }
            }
        }];
        [dataTask resume];
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:@"reqId",ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[requestJSON objectForKey:@"reqId"],NETWORKERRORCODE,nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:[requestJSON objectForKey:PLUGINID] :false :false :resultkeys :result]];
    }
}

+(NSString *)startHashingProcess:(NSDictionary*)appPropertiesDictionary :(NSMutableDictionary*)requestDictionary :(NSString*)interfaceId :(NSString*)clientNonce :(NSMutableDictionary*)updateReqDictionary :(AppzillonViewController*)viewController{
    NSString * requestDictString;
    //    if ([[appPropertiesDictionary objectForKey:@"dataIntegrity"]isEqualToString:@"Y"])
    if ([[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"dataIntegrity"]]isEqualToString:@"Y"]) {
        if ([interfaceId isEqualToString:@"appzillonGetAppSecTokens"] || [interfaceId isEqualToString:@"appzillonOnAppLaunch"]) {
            requestDictString =[self getStringObject:updateReqDictionary];
        }else{
            requestDictString=[self getHashedAPZPayload:appPropertiesDictionary :requestDictionary :clientNonce :updateReqDictionary :viewController];
            
        }
    }else{
        requestDictString =[self getStringObject:updateReqDictionary];
    }
    
    return requestDictString;
}

+(NSMutableString*)getHashedAPZPayload:(NSDictionary*)appPropertiesDictionary :(NSMutableDictionary*)apzJSON :(NSString*)clientNonce :(NSMutableDictionary*)updateReqDictionary :(AppzillonViewController*)viewController{
    id appzillonHeader=[apzJSON objectForKey:@"appzillonHeader"];
    id appzillonBody=[apzJSON objectForKey:@"appzillonBody"];
    id updatedAPZHeader=[updateReqDictionary objectForKey:@"appzillonHeader"];
    id updatedAPZbody=[updateReqDictionary objectForKey:@"appzillonBody"];
    NSString* serverNonce=viewController.APZServerNonce;
    NSString* serverToken=viewController.APZServerToken;
    NSString *salt=[serverNonce stringByAppendingString:serverToken];
    NSString *hashedPin=[HashUtility genHash:clientNonce :salt];
    
    //In case of encryption enabled values comes as string checks below  are needed
    
    NSString * appzillonHeaderString;
    NSString * appzillonBodyString;
    if ([appzillonHeader isKindOfClass:[NSString class]]) {
        appzillonHeaderString=appzillonHeader;
    }else{
        appzillonHeaderString= [self getStringObject:appzillonHeader];
    }
    if ([appzillonBody isKindOfClass:[NSString class]]) {
        appzillonBodyString=appzillonBody;
    }else{
        appzillonBodyString= [self getStringObject:appzillonBody];
    }
    NSString * updatedappzillonHeaderString;
    NSString * updatedappzillonBodyString;
    if ([updatedAPZHeader isKindOfClass:[NSString class]]) {
        updatedappzillonHeaderString=updatedAPZHeader;
    }else{
        updatedappzillonHeaderString= [self getStringObject:updatedAPZHeader];
    }
    if ([updatedAPZbody isKindOfClass:[NSString class]]) {
        updatedappzillonBodyString=updatedAPZbody;
    }else{
        updatedappzillonBodyString= [self getStringObject:updatedAPZbody];
    }
    NSMutableString * appzillonRequestString=[@"{\"appzillonHeader\":" mutableCopy];
    [appzillonRequestString appendString:appzillonHeaderString];
    [appzillonRequestString appendString:@",\"appzillonBody\":"];
    [appzillonRequestString appendString:appzillonBodyString];
    [appzillonRequestString appendString:@"}"];
    //    NSString*string=[appzillonRequestString stringByReplacingOccurrencesOfString:@"\\" withString:@""];
    appzillonRequestString=[self stringByRemovingUnwantedChars:appzillonRequestString];
    NSString *hashedPayload=[self getHashForJSONStrong:appzillonRequestString :hashedPin];
    NSString *appzillonSafe=[updateReqDictionary objectForKey:@"appzillonSafe"];
    NSMutableString * concatstr=[@"{\"appzillonQop\":\"" mutableCopy];
    
    if (appzillonSafe==nil) {
        [concatstr appendString:hashedPayload];
        [concatstr appendString:@"\",\"appzillonHeader\":"];
        [concatstr appendString:[self stringByRemovingUnwantedChars:updatedappzillonHeaderString]];
        [concatstr appendString:@",\"appzillonBody\":"];
        [concatstr appendString:[self stringByRemovingUnwantedChars:updatedappzillonBodyString]];
        [concatstr appendString:@"}"];
    }else{
        [concatstr appendString:hashedPayload];
        [concatstr appendString:@"\",\"appzillonSafe\":\""];
        [concatstr appendString:appzillonSafe];
        [concatstr appendString:@"\",\"appzillonHeader\":\""];
        [concatstr appendString:[self stringByRemovingUnwantedChars:updatedappzillonHeaderString]];
        [concatstr appendString:@"\",\"appzillonBody\":\""];
        [concatstr appendString:[self stringByRemovingUnwantedChars:updatedappzillonBodyString]];
        [concatstr appendString:@"\"}"];
        
    }
    
    return concatstr;
}

+(NSMutableDictionary *)payloadEncryption:(NSDictionary*)appPropertiesDictionary :(NSMutableDictionary*)requestDictionary :(NSString*)appID :(NSString*)interfaceId :(AppzillonViewController *)viewController{
    NSMutableDictionary *finalRequest;
    //    if ([[appPropertiesDictionary objectForKey:@"payloadEncryption"] isEqualToString:@"Y"])
    if ([[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"payloadEncryption"]] isEqualToString:@"Y"]){
        NSDictionary *appzillonHeader=[requestDictionary objectForKey:@"appzillonHeader"];
        NSDictionary *appzillonBody=[requestDictionary objectForKey:@"appzillonBody"];
        //        NSString *publicKey=[self getPublicKey:appID];
        if ([interfaceId isEqualToString:@"appzillonGetAppSecTokens"] || [interfaceId isEqualToString:@"appzillonOnAppLaunch"]) {
            NSString *tokenString=[self getRandomString];
            NSString *encAppzillonHeader=[CryptoUtility getAESEncryptedStringForServerCalls:appzillonHeader :tokenString];
            NSString *encAppzillonBody=[CryptoUtility getAESEncryptedStringForServerCalls:appzillonBody :tokenString];
            NSString *appzillonSafe=[CryptoUtility RSAEncrypt:tokenString :appID];
            finalRequest=[[NSMutableDictionary alloc]initWithObjectsAndKeys:encAppzillonHeader,@"appzillonHeader",encAppzillonBody,@"appzillonBody",appzillonSafe,@"appzillonSafe", nil];
        }else{
            //            NSString* safeToken=[appPropertiesDictionary objectForKey:@"safeToken"];
            NSString* safeToken = [CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"safeToken"]];
            NSString *encAppzillonHeader=[CryptoUtility getAESEncryptedStringForServerCalls:appzillonHeader:safeToken];
            NSString *encAppzillonBody=[CryptoUtility getAESEncryptedStringForServerCalls:appzillonBody :safeToken];
            NSString *encSafeToken=[CryptoUtility RSAEncrypt:safeToken :appID];
            finalRequest=[[NSMutableDictionary alloc]initWithObjectsAndKeys:encAppzillonHeader,@"appzillonHeader",encAppzillonBody,@"appzillonBody",encSafeToken,@"appzillonSafe", nil];
        }
    }else{
        finalRequest=requestDictionary;
    }
    return finalRequest;
}


+(NSMutableDictionary *)payloadDecryption__DataIntegrityCheck:(NSMutableDictionary*)appPropertiesDictionary :(NSMutableDictionary*)responseDictionary :(NSString*)appID :(NSString*)appPropertyPath :(NSString*)interfaceId :(NSString*)clientNonce :(NSData*)responseData :(AppzillonViewController*)viewController{
    NSMutableDictionary *finalResponse=[[NSMutableDictionary alloc] init];
    
    BOOL dataIntegrityValidation=true;
    if ([interfaceId isEqualToString:@"appzillonFilePushService"]||[interfaceId isEqualToString:@"appzillonFilePushServiceWS"]) {
        finalResponse=[responseDictionary mutableCopy];
    }else{
        //        BOOL payloadEncryption=[[appPropertiesDictionary objectForKey:@"payloadEncryption"] isEqualToString:@"Y"];
        //        BOOL dataIntegrity=[[appPropertiesDictionary objectForKey:@"dataIntegrity"] isEqualToString:@"Y"];
        BOOL payloadEncryption=[[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"payloadEncryption"]] isEqualToString:@"Y"];
        BOOL dataIntegrity=[[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"dataIntegrity"]] isEqualToString:@"Y"];
        if (payloadEncryption) {
            //        NSString *publicKey=[self getPublicKey:appID];
            NSArray *allnode=[responseDictionary allKeys];
            if ([allnode containsObject:@"appzillonSafe"]) {
                NSString *appzillonSafe=[responseDictionary objectForKey:@"appzillonSafe"];
                NSString *safeToken= [CryptoUtility RSADecrypt:appzillonSafe :appID];
                [finalResponse setObject:safeToken forKey:@"appzillonSafe"];
                //                [appPropertiesDictionary setObject:safeToken forKey:@"safeToken"];
                [appPropertiesDictionary setObject:[CryptoUtility encryptSingleValue:safeToken] forKey:@"safeToken"];
                [appPropertiesDictionary writeToFile:appPropertyPath atomically:YES];
                for (NSString*nodeName in allnode) {
                    if ([nodeName isEqualToString:@"appzillonQop"]){
                        [finalResponse setObject:[responseDictionary objectForKey:nodeName] forKey:nodeName];
                    }
                    else if(![nodeName isEqualToString:@"appzillonSafe"]){
                        if (dataIntegrity) {
                            [finalResponse setObject:[CryptoUtility getAESDecryptedStringForServerCalls:[responseDictionary objectForKey:nodeName]:safeToken] forKey:nodeName];
                        }else{
                            [finalResponse setObject:[self getDictionaryObject:[CryptoUtility getAESDecryptedStringForServerCalls:[responseDictionary objectForKey:nodeName]:safeToken]] forKey:nodeName];
                        }
                        
                    }
                }
            }else{
                for (NSString*nodeNname in allnode) {
                    if (dataIntegrity) {
                        [finalResponse setObject:[CryptoUtility RSADecrypt:[responseDictionary objectForKey:nodeNname] :appID] forKey:nodeNname];
                    }else{
                        [finalResponse setObject:[self getDictionaryObject:[CryptoUtility RSADecrypt:[responseDictionary objectForKey:nodeNname] :appID]] forKey:nodeNname];
                    }
                }
            }
        }
        if (dataIntegrity&&payloadEncryption) {
            if ([interfaceId isEqualToString:@"appzillonGetAppSecTokens"] || [interfaceId isEqualToString:@"appzillonOnAppLaunch"]) {
                dataIntegrityValidation=true;
            }else{
                NSArray *allKeyes=[finalResponse allKeys];
                //If appzillon Adds some other nodes except appzillonheader appzillonbody or appzillonerrors,code needs to updated and sequences has to be preserved.
                NSMutableString *appzillonRes=[@"{" mutableCopy];
                if ([allKeyes containsObject:@"appzillonErrors"]) {
                    [appzillonRes appendString:@"\"appzillonErrors\":"];
                    [appzillonRes appendString:[NSString  stringWithFormat:@"%@",[finalResponse objectForKey:@"appzillonErrors"]]];
                    [appzillonRes appendString:@","];
                }
                if ([allKeyes containsObject:@"appzillonHeader"]) {
                    [appzillonRes appendString:@"\"appzillonHeader\":"];
                    [appzillonRes appendString:[NSString  stringWithFormat:@"%@", [finalResponse objectForKey:@"appzillonHeader"]]];
                    [appzillonRes appendString:@","];
                }
                if ([allKeyes containsObject:@"appzillonBody"]) {
                    [appzillonRes appendString:@"\"appzillonBody\":"];
                    [appzillonRes appendString:[NSString  stringWithFormat:@"%@", [finalResponse objectForKey:@"appzillonBody"]]];
                }
                [appzillonRes appendString:@"}"];
                
                NSString* serverNonce=viewController.APZServerNonce;
                NSString* serverToken=viewController.APZServerToken;
                NSString *salt=[serverNonce stringByAppendingString:serverToken];
                NSString *hashedPin=[HashUtility genHash:clientNonce :salt];
                NSString *hashedValueOfResponse=[self getHashForJSONStrong:[self stringByRemovingUnwantedChars:appzillonRes] :hashedPin];
                if (![hashedValueOfResponse isEqualToString:[finalResponse objectForKey:@"appzillonQop"]]) {
                    dataIntegrityValidation=false;
                }
            }
            //needed becouse due to encryption we are getting value as string,they need to be converted into dictioanry
            NSArray *allnodesInFinalResponse=[finalResponse allKeys];
            NSArray *finalResponseKeys=@[@"appzillonHeader",@"appzillonBody",@"appzillonErrors"];
            for (NSString* nodesVal in allnodesInFinalResponse) {
                if ([finalResponseKeys containsObject:nodesVal]) {
                    [finalResponse setObject:[self getDictionaryObject:[finalResponse objectForKey:nodesVal]] forKey:nodesVal];
                }
            }
            
        }
        if(dataIntegrity&&(!payloadEncryption)){
            if ([interfaceId isEqualToString:@"appzillonGetAppSecTokens"] || [interfaceId isEqualToString:@"appzillonOnAppLaunch"]) {
                dataIntegrityValidation=true;
            }else{
                NSString* newStr = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
                NSString *appzillonQops=[newStr substringToIndex:MAX(1, 83)];
                NSArray *appzillonQopARStr=[appzillonQops componentsSeparatedByString:@"\""];
                NSString *appzillonQop=[appzillonQopARStr objectAtIndex:3];
                NSString *appzillonRes=[newStr substringFromIndex:MAX(1, 83)];
                appzillonRes=[@"{" stringByAppendingString:appzillonRes];
                //            NSDictionary *appzillonResponse=[self getDictionaryObject:appzillonReq];
                NSString* serverNonce = viewController.APZServerNonce;
                NSString* serverToken = viewController.APZServerToken;
                NSString *salt=[serverNonce stringByAppendingString:serverToken];
                NSString *hashedPin=[HashUtility genHash:clientNonce :salt];
                NSString *hashedValueOfResponse=[self getHashForJSONStrong:appzillonRes :hashedPin];
                if (![hashedValueOfResponse isEqualToString:appzillonQop]) {
                    dataIntegrityValidation=false;
                }
            }
            finalResponse=[responseDictionary mutableCopy];
            [finalResponse removeObjectForKey:@"appzillonQop"];
        }
        
        if((!dataIntegrity)&&(!payloadEncryption)){
            finalResponse=[responseDictionary mutableCopy];
            //        dataIntegrityValidation=true;
        }
    }
    if (dataIntegrityValidation) {
        [finalResponse setObject:[NSNumber numberWithBool:TRUE] forKey:@"dataIntegrityValidation"];
    }
    return finalResponse;
}


+(AFSecurityPolicy*)setSecurityPolicyForSSLPinning:(NSString*)appID{
    NSString *inAppPath=[NSString stringWithFormat:@"/Assets/apps/%@/sslCertificates",appID];
    NSString *certDirectory=[[[MiscellaneousMethods getAppBundle] bundlePath] stringByAppendingPathComponent:inAppPath];
    NSArray *certArray=[[NSFileManager defaultManager] contentsOfDirectoryAtPath:certDirectory error:nil];
    NSMutableArray *certDataArray=[[NSMutableArray alloc]init];
    if (certArray.count>0) {
        for (NSString *fileName in certArray) {
            NSData *fileData=[NSData dataWithContentsOfFile:[certDirectory stringByAppendingPathComponent:fileName]];
            [certDataArray addObject:fileData];
            
        }
    }
    AFSecurityPolicy *securityPolicy = [AFSecurityPolicy policyWithPinningMode:AFSSLPinningModeCertificate withPinnedCertificates:[NSSet setWithArray:certDataArray]];
    securityPolicy.pinningEnabled=YES;
    securityPolicy.allowInvalidCertificates=YES;
    return securityPolicy;
}


+(NSMutableURLRequest*)createHttpHeader:(NSMutableURLRequest*)request :(NSString*)httpMethod{
    [request setHTTPMethod:httpMethod];
    [request addValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [request setValue: @"application/json" forHTTPHeaderField:@"Content-Type"];
    return request;
}


+(NSString*)getAppPropertiesDictionaryPath:(NSString*)appID{
    NSString *inAppPlistFilePath=[NSString stringWithFormat:@"Assets/apps/%@/plist/AppProperties.plist",appID];
    NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    NSString *documentsDirectory = [[NSString alloc] initWithString:[paths objectAtIndex:0]];
    NSString *appPropertyPath=[documentsDirectory stringByAppendingPathComponent:inAppPlistFilePath];
    return appPropertyPath;
}
+(NSString*)getHashForJSONStrong:(NSString*)jsonString :(NSString*)hashedPin{
    NSData *jsonStringdata = [jsonString  dataUsingEncoding:NSUTF8StringEncoding];
    NSString *jsonStringBase64=[Base64 encode:jsonStringdata];
    NSString *hashedPayload=[HashUtility genHash:jsonStringBase64 :hashedPin];
    return hashedPayload;
}
+(NSMutableDictionary *)getUpdatedAppzillonHeader :(NSMutableDictionary *)requestDict :(NSDictionary *)appPropertiesDictionary :(NSString*)clientNonce :(AppzillonViewController*)APZViewController{
    NSMutableDictionary *appzillonHeader=[[NSMutableDictionary alloc]init];
    appzillonHeader=[requestDict objectForKey:@"appzillonHeader"];
    if (APZViewController.APZServerNonce!=nil) {
        [appzillonHeader setObject:APZViewController.APZServerNonce forKey:@"serverNonce"];
        [appzillonHeader setObject:APZViewController.APZSessionToken forKey:@"sessionToken"];
        [appzillonHeader setObject:clientNonce forKey:@"clientNonce"];
    }
    return appzillonHeader;
}
+(NSString*)getRandomString{
    CFUUIDRef theUUID = CFUUIDCreate(NULL);
    CFStringRef string = CFUUIDCreateString(NULL, theUUID);
    CFRelease(theUUID);
    NSString *str = [(__bridge NSString*)string stringByReplacingOccurrencesOfString:@"-"withString:@""];
    return str;
}
+(NSDictionary*)getDictionaryObject:(id)content{
    NSMutableDictionary* jsonDictionary = [[NSMutableDictionary alloc] init];;
    if ([content isKindOfClass:[NSDictionary class]]) {
        jsonDictionary=[[NSMutableDictionary alloc]initWithDictionary:content];
    }else{
        NSError *error;
        NSData *stringContentData = [content dataUsingEncoding:NSUTF8StringEncoding];
        if (stringContentData) {
              jsonDictionary= [NSJSONSerialization JSONObjectWithData:stringContentData options:0 error:&error];
        }
    }
    return jsonDictionary;
}
+(NSString*)getStringObject:(id)content{
    NSMutableString *convertedString = [[NSMutableString alloc] init];
    if ([content isKindOfClass:[NSString class]]) {
        convertedString=content;
    }else{
        NSData * dictionaryData = [NSJSONSerialization  dataWithJSONObject:content options:0 error:nil];
        if (dictionaryData) {
            convertedString= [[NSMutableString alloc] initWithData:dictionaryData   encoding:NSUTF8StringEncoding];
        }
    }
    return convertedString;
}

+(NSString*)getCurrnetDateTime{
    NSTimeInterval timeInMiliseconds = [[NSDate date] timeIntervalSince1970];
    NSString *currnetTime=[NSString stringWithFormat:@"%f",timeInMiliseconds];
    return currnetTime;
}
+(NSMutableString*)stringByRemovingUnwantedChars:(NSString*)stringWithChars{
    //    stringWithChars = [stringWithChars stringByReplacingOccurrencesOfString:@"[\r\n]"
    //                                                           withString:@""
    //                                                              options:NSRegularExpressionSearch
    //                                                                range:NSMakeRange(0, stringWithChars.length)];
    //    NSString *leadingTrailingWhiteSpacesPattern = @"\\s(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    //    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:leadingTrailingWhiteSpacesPattern options:NSRegularExpressionCaseInsensitive error:NULL];
    //
    //    NSRange stringRange = NSMakeRange(0, stringWithChars.length);
    //    NSMutableString *trimmedAppzillonReq = [regex stringByReplacingMatchesInString:stringWithChars options:NSMatchingReportProgress range:stringRange withTemplate:@""];
    //    return trimmedAppzillonReq;
    return stringWithChars;
}

+(BOOL)checkInternetReachbility{
    if ([[Reachability reachabilityForInternetConnection]currentReachabilityStatus]==NotReachable)
    {
        //        if(networkAlert != nil){
        //            [networkAlert dismissViewControllerAnimated:true completion:nil];
        //        }
        //        [self showAlert:@"Network Error" alterMsg:@"No internet connection found"];
        return  false;
    }else{
        return  true;
    }
}
+(BOOL)nativeAlertRequired:(NSString*)interfaceId{
    BOOL alertNeeded=NO;
    NSArray *startUpServerCallList=@[@"appzillonGetAppSecTokens",@"appzillonOnAppLaunch",@"appzillonDeviceRegistration",@"appzillonGetAppMasterDetails",@"appzillonNotificationRegistration"];
    if([startUpServerCallList containsObject:interfaceId]){
        alertNeeded=YES;
    }
    return alertNeeded;
    
}
+(void)callFailureCallback:(NSString*)className :(NSString*)failureCB{
    NSString* methodName = [NSString stringWithFormat:@"%@:",failureCB];
    SEL normalSelector = NSSelectorFromString(methodName);
    if ([className respondsToSelector:normalSelector])
    {
        NSMutableDictionary *errorCodeDictionary=[[NSMutableDictionary alloc]init];
        [errorCodeDictionary setValue:NETWORKERRORCODE forKey:ERROR_CODE];
        [className performSelector:normalSelector withObject:errorCodeDictionary];
    }
}
+(AFHTTPSessionManager*)getAFHTTPSessionManager:(NSURL*)serverURL{
    NSURLSessionConfiguration* config = [NSURLSessionConfiguration defaultSessionConfiguration];
    config.timeoutIntervalForRequest = CONTAINERTIMEOUT;
    if (@available(iOS 14.0, *)) {
        config.waitsForConnectivity = true;
        config.timeoutIntervalForResource = CONTAINERTIMEOUT;
    }
    AFHTTPSessionManager *client = [[AFHTTPSessionManager alloc] initWithBaseURL:serverURL sessionConfiguration:config];
    return client;
}
+(AFURLSessionManager*)getAFURLSessionManager{
    NSURLSessionConfiguration* config = [NSURLSessionConfiguration defaultSessionConfiguration];
    config.timeoutIntervalForRequest = CONTAINERTIMEOUT;
    if (@available(iOS 14.0, *)) {
        config.waitsForConnectivity = true;
        config.timeoutIntervalForResource = CONTAINERTIMEOUT;
    }
    AFURLSessionManager *client = [[AFURLSessionManager alloc] initWithSessionConfiguration:config];
    return client;
}
+(void)showAlertMessage :(BOOL)isReachable :(NSString *)interfaceId{
    NSString* containerPropsPath = [[MiscellaneousMethods getAppBundle] pathForResource:@"containerprops" ofType:@"plist"];
    NSDictionary *containerPropsDictionary=[[NSDictionary alloc]initWithContentsOfFile:containerPropsPath];
    if ([[containerPropsDictionary objectForKey:@"OFFLINESUPPORT"] isEqualToString:@"Y"] ||[[containerPropsDictionary objectForKey:@"OFFLINESUPPORT"] isEqualToString:@"y"]) {
        //Dont popup network alert, if the app is supported offline mode
    }else{
        if(networkAlert != nil){
            [networkAlert dismissViewControllerAnimated:true completion:nil];
        }
        UIWindow* window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
        window.rootViewController = [UIViewController new];
        window.windowLevel = UIWindowLevelAlert + 1;
        if ((isReachable && [interfaceId isEqualToString:@"appzillonGetAppSecTokens"]) || (isReachable && [interfaceId isEqualToString:@"appzillonOnAppLaunch"])) {
            networkAlert = [UIAlertController alertControllerWithTitle:@"Network Error" message:@"Unable to connect to the server. Retrying in 5 seconds." preferredStyle:UIAlertControllerStyleAlert];
        }else{
            networkAlert = [UIAlertController alertControllerWithTitle:@"Network Error" message:@"No internet connection found" preferredStyle:UIAlertControllerStyleAlert];
        }
        [networkAlert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
            window.hidden = YES;
        }]];
        [window makeKeyAndVisible];
        [window.rootViewController presentViewController:networkAlert animated:YES completion:nil];
    }
}

+(void)nonAppzillonServerCallsWithRequest:(NSMutableDictionary *)requestDictionary webView:(WKWebView*)webView :(NSString *)appID{
    BOOL isRechable = [self checkInternetReachbility];
        if(isRechable){
            NSDictionary* httpHeaders = [requestDictionary objectForKey:@"httpHeaders"];
            NSString *serverIP = [requestDictionary objectForKey:@"url"];
            NSURL *serverURL = [[NSURL alloc] initWithString:serverIP];
            
            NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:serverURL];
            [request setHTTPMethod:@"POST"];
            
            NSArray *httpHeaderKeys = [httpHeaders allKeys];
            for (NSString* key in httpHeaderKeys) {
               [request addValue:[httpHeaders objectForKey:key] forHTTPHeaderField:key];
            }

            NSString *requestString = [self getStringObject:[requestDictionary objectForKey:@"request"]];
            [request setHTTPBody:[requestString dataUsingEncoding:NSUTF8StringEncoding]];
            AFHTTPSessionManager *client = [self getAFHTTPSessionManager:serverURL];
            NSString *appPropertyPath= [self getAppPropertiesDictionaryPath:appID];
            NSMutableDictionary *appPropertiesDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:appPropertyPath];
            if ([[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"sslPinning"]]isEqualToString:@"Y"]){
                client.securityPolicy=[self setSecurityPolicyForSSLPinning:appID];
            }else if([[CryptoUtility decryptSingleValue:[appPropertiesDictionary objectForKey:@"trustAllCertificates"]]isEqualToString:@"Y"]){
                client.securityPolicy.allowInvalidCertificates=YES;
                client.securityPolicy.validatesDomainName=NO;
            }
            NSURLSessionDataTask *dataTask = [client dataTaskWithRequest:request uploadProgress:nil downloadProgress:nil completionHandler:^(NSURLResponse *response, id responseObject, NSError *error ,NSData *responseData) {
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
                NSDictionary * responseHTTPHeaders = [httpResponse allHeaderFields];
                NSArray *resultkeys = [[NSArray alloc] init];
                NSArray *result = [[NSArray alloc] init];
                BOOL status = false;
                if (error) {
                    @synchronized(self) {
                       resultkeys=[NSArray arrayWithObjects:@"httpCode",@"httpHeaders",nil];
                        if (!responseHTTPHeaders) {
                            responseHTTPHeaders = [[NSDictionary alloc] init];
                        }
                       result=[NSArray arrayWithObjects:[NSNumber numberWithUnsignedInteger:httpResponse.statusCode],responseHTTPHeaders,nil];
                    }
                } else {
                    status = true;
                    resultkeys=[NSArray arrayWithObjects:@"httpCode",@"httpHeaders",@"response",nil];
                                result=[NSArray arrayWithObjects:[NSNumber numberWithUnsignedInteger:httpResponse.statusCode],responseHTTPHeaders,responseObject,nil];
                }
                
                [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:[requestDictionary objectForKey:PLUGINID] :status :false :resultkeys :result]];
            }];
            [dataTask resume];
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:NETWORKERRORCODE,nil];
            [MiscellaneousMethods jsLayerCall: webView:JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:[requestDictionary objectForKey:PLUGINID] :false :false :resultkeys :result]];
        }
}

@end




