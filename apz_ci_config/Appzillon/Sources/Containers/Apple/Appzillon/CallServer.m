//
//  CallServer.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 09/11/17.
//

#import "CallServer.h"
#import "Constants.h"
#import "HashUtility.h"
#import "CryptoUtility.h"
#import <APPZILLONPRODUCTNAME-Swift.h>

@implementation CallServer
UIAlertController *networkAlert;

+(void)callServerWithRequest :(NSMutableDictionary*)requestDict :(NSString *)serverIP :(NSString*)appID :(AppzillonViewController*)APZViewController completionHandler:(void (^)(BOOL, NSDictionary *))completionHandler {
    NSString *interfaceId=[[requestDict objectForKey:@"appzillonHeader"] objectForKey:@"interfaceId"];
    if([APZNetworkUtility.shared isConnectedToNetwork]){
        NSString *appPropertyPath= [APZNetworkUtility.shared getAppPropertiesDictionaryPathWithAppID:appID];
        NSMutableDictionary *appPropertiesDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:appPropertyPath];
        NSString*clientNonce= [APZNetworkUtility.shared getCurrentDateTime];
        [requestDict setObject:[APZNetworkUtility.shared getUpdatedAppzillonHeader:requestDict clientNonce:clientNonce apzViewController:APZViewController] forKey:@"appzillonHeader"];
        NSMutableDictionary *plainRequest=requestDict;
        NSString *requestString;
        if ([interfaceId isEqualToString:@"appzillonFilePushService"]||[interfaceId isEqualToString:@"appzillonFilePushServiceWSRequest"]) {
            //requestString=[self getStringObject:requestDict];
            requestString=[APZNetworkUtility.shared getStringObjectWithContent:requestDict];
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
        request = [APZNetworkUtility.shared createHttpHeaderWithRequest:request httpMethod:@"POST"];
        //NEW CODE
        [APZNetworkManager.shared callServerWithRequestWithServerIP:serverIP
                                                         request:request
                                         appPropertiesDictionary:appPropertiesDictionary
                                                           appID:appID
                                               completionHandler:^(NSURLResponse *response, id responseObject, NSError *error ,NSData *responseData) {
            if (error) {
                NSMutableDictionary *errorCodeDictionary=[[NSMutableDictionary alloc]init];
                [errorCodeDictionary setValue:[NSString stringWithFormat:@"%ld",(long)error.code ]forKey:@"errorCode"];
                [errorCodeDictionary setValue:error.localizedDescription forKey:@"errorMsg"];
                [APZNetworkUtility.shared showAlertMessage:[APZNetworkUtility.shared isConnectedToNetwork] interfaceId:interfaceId];
                completionHandler(NO, errorCodeDictionary);
                
            } else {
                NSMutableDictionary *finalResponse= [self payloadDecryption__DataIntegrityCheck:appPropertiesDictionary :responseObject :appID :appPropertyPath :interfaceId :clientNonce :responseData :APZViewController];
                if ([finalResponse objectForKey:@"dataIntegrityValidation"]) {
                    [finalResponse removeObjectForKey:@"dataIntegrityValidation"];
                    completionHandler(YES, finalResponse);
                }
            }
        }];
    }else{
        if([APZNetworkUtility.shared nativeAlertRequired:interfaceId]){
            [APZNetworkUtility.shared showAlertMessage:[APZNetworkUtility.shared isConnectedToNetwork] interfaceId:interfaceId];
        }
        NSMutableDictionary *errorCodeDictionary=[[NSMutableDictionary alloc]init];
        [errorCodeDictionary setValue:NETWORKERRORCODE forKey:ERROR_CODE];
        completionHandler(NO, errorCodeDictionary);
    }
}

+(void)callServerForUploadWithRequest :(NSMutableDictionary*)requestDict :(NSString*)appID :(NSString*)serverIP :(NSString*)path :(NSString*)fileName :(NSString*)mimeType :(AppzillonViewController*)APZViewController completionHandler:(void (^)(BOOL, NSDictionary *))completionHandler {
    NSString *interfaceId=[[requestDict objectForKey:@"appzillonHeader"] objectForKey:@"interfaceId"];
    if([APZNetworkUtility.shared isConnectedToNetwork]){
        NSString *appPropertyPath= [APZNetworkUtility.shared getAppPropertiesDictionaryPathWithAppID:appID];
        NSMutableDictionary *appPropertiesDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:appPropertyPath];
        NSString*clientNonce=[APZNetworkUtility.shared getCurrentDateTime];
        [requestDict setObject:[APZNetworkUtility.shared getUpdatedAppzillonHeader:requestDict clientNonce:clientNonce apzViewController:APZViewController] forKey:@"appzillonHeader"];
        NSError *err;
        NSData *dataFromDict = [NSJSONSerialization dataWithJSONObject:requestDict
                                                               options:NSJSONWritingPrettyPrinted
                                                                 error:&err];
        //NEW CODE
        [APZNetworkManager.shared callServerForUploadWithServerIP:serverIP
                                       appPropertiesDictionary:appPropertiesDictionary
                                                         appID:appID
                                                          path:path
                                                      fileName:fileName
                                                          data:dataFromDict
                                             completionHandler:^(NSURLResponse *response, id responseObject, NSError *error) {
            if (error) {
                NSMutableDictionary *errorCodeDictionary=[[NSMutableDictionary alloc]init];
                [errorCodeDictionary setValue:[NSString stringWithFormat:@"%ld",(long)error.code ]forKey:@"errorCode"];
                [errorCodeDictionary setValue:error.localizedDescription forKey:@"errorMsg"];
                completionHandler(NO, errorCodeDictionary);
            } else {
                NSError* responseError = nil;
                NSDictionary *responseDict = [NSJSONSerialization JSONObjectWithData:responseObject options:0 error:&responseError];
                completionHandler(YES, responseDict);
            }
        }];
    }else{
        if([APZNetworkUtility.shared nativeAlertRequired:interfaceId]){
            [APZNetworkUtility.shared showAlertMessage:[APZNetworkUtility.shared isConnectedToNetwork] interfaceId:interfaceId];
        }
        NSMutableDictionary *errorCodeDictionary=[[NSMutableDictionary alloc]init];
        [errorCodeDictionary setValue:NETWORKERRORCODE forKey:ERROR_CODE];
        completionHandler(NO, errorCodeDictionary);
    }
}

+(void)callServerFromInfraWithRequest :(NSMutableDictionary*)requestJSON :(AppzillonViewController*)viewController :(WKWebView*)webView :(NSString*)appID{
    if([APZNetworkUtility.shared isConnectedToNetwork]){
        NSString *appPropertyPath= [APZNetworkUtility.shared getAppPropertiesDictionaryPathWithAppID:appID];
        NSMutableDictionary *appPropertiesDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:appPropertyPath];
        NSMutableDictionary *infraReqJson =[[requestJSON objectForKey:@"params"] objectForKey:@"reqFull"];
        NSString *interfaceId=[[infraReqJson objectForKey:@"appzillonHeader"] objectForKey:@"interfaceId"];
        NSString*clientNonce=[APZNetworkUtility.shared getCurrentDateTime];
        [infraReqJson setObject:[APZNetworkUtility.shared getUpdatedAppzillonHeader:infraReqJson clientNonce:clientNonce apzViewController:viewController] forKey:@"appzillonHeader"];
        NSMutableDictionary *plainRequest=infraReqJson;
        infraReqJson=[self payloadEncryption:appPropertiesDictionary :infraReqJson :appID :interfaceId :viewController];
        [[requestJSON objectForKey:@"params"] setObject:infraReqJson forKey:@"reqFull"];
        
         //Add interfaceID to the severUrl, if user prefers this:
        NSURL *serverURL;
        if ([[viewController.appPropertyDictionary objectForKey:@"IFACEIDINURI"] isEqualToString:@"Y"]) {
            serverURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@/services/%@",viewController.APZServerURL,interfaceId]];
        }else{
            serverURL = [NSURL URLWithString:viewController.APZServerURL];
        }
        
        NSString *reqId=[requestJSON objectForKey:@"reqId"];
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:serverURL];
        request = [APZNetworkUtility.shared createHttpHeaderWithRequest:request httpMethod:@"POST"];
        NSString *requestString= [self startHashingProcess:appPropertiesDictionary :plainRequest :interfaceId :clientNonce :infraReqJson :viewController];
        [request setHTTPBody:[requestString dataUsingEncoding:NSUTF8StringEncoding]];
        
        [APZNetworkManager.shared callServerFromInfraWithServerURL:serverURL
                                                        request:request
                                        appPropertiesDictionary:appPropertiesDictionary
                                                          appID:appID
                                              completionHandler:^(NSURLResponse *response, id responseObject, NSError *error ,NSData *responseData) {
            
            if (error) {
                @synchronized(self) {
                    NSMutableDictionary *errorJSON=[[NSMutableDictionary alloc] init];
                    [errorJSON setValue:[NSString stringWithFormat:@"%ld",(long)error.code ]forKey:@"errorCode"];
                    [errorJSON setValue:error.localizedDescription forKey:@"errorMsg"];
                    NSDictionary *responseJSON=[[NSDictionary alloc] initWithObjectsAndKeys:[[requestJSON objectForKey:@"params"] objectForKey:@"reqFull"],@"reqFull",[NSNumber numberWithBool:false],@"status",nil];
                    NSArray *resultkeys=[NSArray arrayWithObjects:@"params",@"error",@"reqId",nil];
                    NSArray *result=[NSArray arrayWithObjects:responseJSON,errorJSON,reqId,nil];
                    [MiscellaneousMethod.shared jsLayerCallWithWebView:webView jsFunctionName:JSCALLBACKMEHTOD parameter:[APZJsonUtility.shared createResponseJSONStringWithPluginId:[requestJSON objectForKey:PLUGINID] status:false keepAlive:false responseKeys:resultkeys responseValues:result]];
                }
            } else {
                if (responseObject) {
                    NSMutableDictionary *finalResponse= [self payloadDecryption__DataIntegrityCheck:appPropertiesDictionary :responseObject :appID :appPropertyPath :interfaceId :clientNonce :responseData :viewController];
                    if ([finalResponse objectForKey:@"dataIntegrityValidation"]) {
                        [finalResponse removeObjectForKey:@"dataIntegrityValidation"];
                        NSDictionary *responseJSON=[[NSDictionary alloc] initWithObjectsAndKeys:finalResponse,@"resFull",[NSNumber numberWithBool:true],@"status",nil];
                        NSArray *resultkeys=[NSArray arrayWithObjects:@"params",@"reqId",nil];
                        NSArray *result=[NSArray arrayWithObjects:responseJSON,reqId,nil];
                        [MiscellaneousMethod.shared jsLayerCallWithWebView:webView jsFunctionName:JSCALLBACKMEHTOD parameter:[APZJsonUtility.shared createResponseJSONStringWithPluginId:[requestJSON objectForKey:PLUGINID] status:true keepAlive:false responseKeys:resultkeys responseValues:result]];
                    }else{
                        NSDictionary *responseJSON=[[NSDictionary alloc] initWithObjectsAndKeys:finalResponse,@"resFull",[NSNumber numberWithBool:false],@"status",nil];
                        NSArray *resultkeys=[NSArray arrayWithObjects:@"params",@"reqId",ERROR_CODE,nil];
                        NSArray *result=[NSArray arrayWithObjects:responseJSON,reqId,@"APZ-CNT-230",nil];
                        [MiscellaneousMethod.shared jsLayerCallWithWebView:webView jsFunctionName:JSCALLBACKMEHTOD parameter:[APZJsonUtility.shared createResponseJSONStringWithPluginId:[requestJSON objectForKey:PLUGINID] status:false keepAlive:false responseKeys:resultkeys responseValues:result]];
                    }
                }
            }
        }];
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:@"reqId",ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[requestJSON objectForKey:@"reqId"],NETWORKERRORCODE,nil];
        [MiscellaneousMethod.shared jsLayerCallWithWebView:webView jsFunctionName:JSCALLBACKMEHTOD parameter:[APZJsonUtility.shared createResponseJSONStringWithPluginId:[requestJSON objectForKey:PLUGINID] status:false keepAlive:false responseKeys:resultkeys responseValues:result]];
    }
}

+(NSString *)startHashingProcess:(NSDictionary*)appPropertiesDictionary :(NSMutableDictionary*)requestDictionary :(NSString*)interfaceId :(NSString*)clientNonce :(NSMutableDictionary*)updateReqDictionary :(AppzillonViewController*)viewController{
    NSString * requestDictString;
    //    if ([[appPropertiesDictionary objectForKey:@"dataIntegrity"]isEqualToString:@"Y"])
    if ([[CryptoSwiftManager.shared decryptSingleValueWithValue:[appPropertiesDictionary objectForKey:@"dataIntegrity"]]isEqualToString:@"Y"]) {
        if ([interfaceId isEqualToString:@"appzillonGetAppSecTokens"] || [interfaceId isEqualToString:@"appzillonOnAppLaunch"]) {
            requestDictString =[APZNetworkUtility.shared getStringObjectWithContent:updateReqDictionary];
        }else{
            requestDictString=[self getHashedAPZPayload:appPropertiesDictionary :requestDictionary :clientNonce :updateReqDictionary :viewController];
        }
    }else{
        requestDictString =[APZNetworkUtility.shared getStringObjectWithContent:updateReqDictionary];
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
        appzillonHeaderString= [APZNetworkUtility.shared getStringObjectWithContent:appzillonHeader];
    }
    if ([appzillonBody isKindOfClass:[NSString class]]) {
        appzillonBodyString=appzillonBody;
    }else{
        appzillonBodyString= [APZNetworkUtility.shared getStringObjectWithContent:appzillonBody];
    }
    NSString * updatedappzillonHeaderString;
    NSString * updatedappzillonBodyString;
    if ([updatedAPZHeader isKindOfClass:[NSString class]]) {
        updatedappzillonHeaderString=updatedAPZHeader;
    }else{
        updatedappzillonHeaderString= [APZNetworkUtility.shared getStringObjectWithContent:updatedAPZHeader];
    }
    if ([updatedAPZbody isKindOfClass:[NSString class]]) {
        updatedappzillonBodyString=updatedAPZbody;
    }else{
        updatedappzillonBodyString= [APZNetworkUtility.shared getStringObjectWithContent:updatedAPZbody];
    }
    NSMutableString * appzillonRequestString=[@"{\"appzillonHeader\":" mutableCopy];
    [appzillonRequestString appendString:appzillonHeaderString];
    [appzillonRequestString appendString:@",\"appzillonBody\":"];
    [appzillonRequestString appendString:appzillonBodyString];
    [appzillonRequestString appendString:@"}"];
    //    NSString*string=[appzillonRequestString stringByReplacingOccurrencesOfString:@"\\" withString:@""];
    appzillonRequestString=[APZNetworkUtility.shared stringByRemovingUnwantedChars:appzillonRequestString];
//    NSString *hashedPayload=[APZNetworkUtility.shared getHashForJSONStringWithJsonString:appzillonRequestString hashedPin:hashedPin];
    NSString *hashedPayload=[self getHashForResponseJSONString:appzillonRequestString :hashedPin];
    NSString *appzillonSafe=[updateReqDictionary objectForKey:@"appzillonSafe"];
    NSMutableString * concatstr=[@"{\"appzillonQop\":\"" mutableCopy];
    
    if (appzillonSafe==nil) {
        [concatstr appendString:hashedPayload];
        [concatstr appendString:@"\",\"appzillonHeader\":"];
        [concatstr appendString:[APZNetworkUtility.shared stringByRemovingUnwantedChars:updatedappzillonHeaderString]];
        [concatstr appendString:@",\"appzillonBody\":"];
        [concatstr appendString:[APZNetworkUtility.shared stringByRemovingUnwantedChars:updatedappzillonBodyString]];
        [concatstr appendString:@"}"];
    }else{
        [concatstr appendString:hashedPayload];
        [concatstr appendString:@"\",\"appzillonSafe\":\""];
        [concatstr appendString:appzillonSafe];
        if ([[CryptoSwiftManager.shared decryptSingleValueWithValue:[appPropertiesDictionary objectForKey:@"payloadEncryption"]] isEqualToString:@"Y"]){
            [concatstr appendString:@"\",\"appzillonSafeBit\":\""];
            [concatstr appendString:@"1"];
            [concatstr appendString:@"\",\"hash\":\""];
            [concatstr appendString:@"0"];
            [concatstr appendString:@"\",\"encMode\":\""];
            [concatstr appendString:@"1"];
        }
        [concatstr appendString:@"\",\"appzillonHeader\":\""];
        [concatstr appendString:[APZNetworkUtility.shared stringByRemovingUnwantedChars:updatedappzillonHeaderString]];
        [concatstr appendString:@"\",\"appzillonBody\":\""];
        [concatstr appendString:[APZNetworkUtility.shared stringByRemovingUnwantedChars:updatedappzillonBodyString]];
        [concatstr appendString:@"\"}"];
        
    }
    return concatstr;
}

+(NSMutableDictionary *)payloadEncryption:(NSDictionary*)appPropertiesDictionary :(NSMutableDictionary*)requestDictionary :(NSString*)appID :(NSString*)interfaceId :(AppzillonViewController *)viewController{
    NSMutableDictionary *finalRequest;
    //    if ([[appPropertiesDictionary objectForKey:@"payloadEncryption"] isEqualToString:@"Y"])
    if ([[CryptoSwiftManager.shared decryptSingleValueWithValue:[appPropertiesDictionary objectForKey:@"payloadEncryption"]] isEqualToString:@"Y"]){
        NSDictionary *appzillonHeader=[requestDictionary objectForKey:@"appzillonHeader"];
        NSDictionary *appzillonBody=[requestDictionary objectForKey:@"appzillonBody"];
        //        NSString *publicKey=[self getPublicKey:appID];
        if ([interfaceId isEqualToString:@"appzillonGetAppSecTokens"] || [interfaceId isEqualToString:@"appzillonOnAppLaunch"]) {
            NSString *tokenString=[APZNetworkUtility.shared getRandomString];
            
            NSString *encAppzillonHeader=[CryptoSwiftManager.shared getAESEncryptedStringForServerCallsWithDataToEncrypt:appzillonHeader keyStr:tokenString];
            NSString *encAppzillonBody=[CryptoSwiftManager.shared getAESEncryptedStringForServerCallsWithDataToEncrypt:appzillonBody keyStr:tokenString];
            
            NSString *appzillonSafe=[CryptoUtility RSAEncrypt:tokenString :appID];
            finalRequest=[[NSMutableDictionary alloc]initWithObjectsAndKeys:encAppzillonHeader,@"appzillonHeader",encAppzillonBody,@"appzillonBody",appzillonSafe,@"appzillonSafe", @"1", @"appzillonSafeBit", @"0", @"hash", @"1", @"encMode", nil];
        }else{
            //            NSString* safeToken=[appPropertiesDictionary objectForKey:@"safeToken"];
            NSString* safeToken = [CryptoSwiftManager.shared decryptSingleValueWithValue:[appPropertiesDictionary objectForKey:@"safeToken"]];
            
            NSString *encAppzillonHeader=[CryptoSwiftManager.shared getAESEncryptedStringForServerCallsWithDataToEncrypt:appzillonHeader keyStr:safeToken];
            NSString *encAppzillonBody=[CryptoSwiftManager.shared getAESEncryptedStringForServerCallsWithDataToEncrypt:appzillonBody keyStr:safeToken];
            
            NSString *encSafeToken=[CryptoUtility RSAEncrypt:safeToken :appID];
            finalRequest=[[NSMutableDictionary alloc]initWithObjectsAndKeys:encAppzillonHeader,@"appzillonHeader",encAppzillonBody,@"appzillonBody",encSafeToken,@"appzillonSafe", @"1", @"appzillonSafeBit", @"0", @"hash", @"1", @"encMode", nil];
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
        BOOL payloadEncryption=[[CryptoSwiftManager.shared decryptSingleValueWithValue:[appPropertiesDictionary objectForKey:@"payloadEncryption"]] isEqualToString:@"Y"];
        BOOL dataIntegrity=[[CryptoSwiftManager.shared decryptSingleValueWithValue:[appPropertiesDictionary objectForKey:@"dataIntegrity"]] isEqualToString:@"Y"];
        if (payloadEncryption) {
            //        NSString *publicKey=[self getPublicKey:appID];
            NSArray *allnode=[responseDictionary allKeys];
            if ([allnode containsObject:@"appzillonSafe"]) {
                NSString *appzillonSafe=[responseDictionary objectForKey:@"appzillonSafe"];
                NSString *safeToken= [CryptoUtility RSADecrypt:appzillonSafe :appID];
                [finalResponse setObject:safeToken forKey:@"appzillonSafe"];
                //                [appPropertiesDictionary setObject:safeToken forKey:@"safeToken"];
                [appPropertiesDictionary setObject:[CryptoSwiftManager.shared encryptSingleValueWithValue:safeToken] forKey:@"safeToken"];
                [appPropertiesDictionary writeToFile:appPropertyPath atomically:YES];
                for (NSString*nodeName in allnode) {
                    if ([nodeName isEqualToString:@"appzillonQop"]){
                        [finalResponse setObject:[responseDictionary objectForKey:nodeName] forKey:nodeName];
                    }
                    else if(![nodeName isEqualToString:@"appzillonSafe"]){
                        if (dataIntegrity) {
                            [finalResponse setObject:[CryptoSwiftManager.shared getAESDecryptedStringForServerCallsWithDataToDecrypt:[responseDictionary objectForKey:nodeName] keyStr:safeToken] forKey:nodeName];
                        }else{
                            [finalResponse setObject:[self getDictionaryObject:[CryptoSwiftManager.shared getAESDecryptedStringForServerCallsWithDataToDecrypt:[responseDictionary objectForKey:nodeName] keyStr:safeToken]] forKey:nodeName];
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
//                NSString *hashedValueOfResponse=[APZNetworkUtility.shared getHashForJSONStringWithJsonString:[APZNetworkUtility.shared stringByRemovingUnwantedChars:appzillonRes] hashedPin:hashedPin];
                NSString *hashedValueOfResponse=[self getHashForResponseJSONString:[APZNetworkUtility.shared stringByRemovingUnwantedChars:appzillonRes] :hashedPin];
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
//                NSString *hashedValueOfResponse=[APZNetworkUtility.shared getHashForJSONStringWithJsonString:appzillonRes hashedPin:hashedPin];
                NSString *hashedValueOfResponse=[self getHashForResponseJSONString:[APZNetworkUtility.shared stringByRemovingUnwantedChars:appzillonRes] :hashedPin];
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

//Fix given to handle Albenaian language and any other NON ASCII languages
+(NSString*)getHashForResponseJSONString:(NSString*)jsonString :(NSString*)hashedPin {
    NSString *escapedString = [self JSONString:jsonString];
    NSData *jsonStringdata = [[StringEscapeHandler escapeJavaWithString:escapedString] dataUsingEncoding:NSUTF8StringEncoding];
    NSString *jsonStringBase64=[Base64 encode:jsonStringdata];
    NSString *hashedPayload=[HashUtility genHash:jsonStringBase64 :hashedPin];
    return hashedPayload;
}

+(NSString *)JSONString:(NSString *)aString {
    NSMutableString *s = [NSMutableString stringWithString:aString];
    [s replaceOccurrencesOfString:@"\\" withString:@"\\\\" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [s length])];
    [s replaceOccurrencesOfString:@"\"" withString:@"\\\"" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [s length])];
    //[s replaceOccurrencesOfString:@"/" withString:@"\\/" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [s length])];
    
    [s replaceOccurrencesOfString:@"\n" withString:@"\\n" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [s length])];
//    [s replaceOccurrencesOfString:@"\b" withString:@"\\b" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [s length])];
//    [s replaceOccurrencesOfString:@"\f" withString:@"\\f" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [s length])];
//    [s replaceOccurrencesOfString:@"\r" withString:@"\\r" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [s length])];
//    [s replaceOccurrencesOfString:@"\t" withString:@"\\t" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [s length])];
    return [NSString stringWithString:s];
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
@end
