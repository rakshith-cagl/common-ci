//
//  APZSecureStorage.m
//  Appzillon
//
//  Created by Aman Gupta on 30/01/18.
//

#import "APZSecureStorage.h"
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
#import "CallServer.h"
#import <Security/Security.h>
#import <LocalAuthentication/LocalAuthentication.h>
#import <LocalAuthentication/LAPublicDefines.h>
#import "AppzillonLogin.h"
#import "CryptoUtility.h"

@interface APZSecureStorage()
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(strong,nonatomic)NSString *pluginId;
@property(strong,nonatomic)NSDictionary *jsonDict;
@end



@implementation APZSecureStorage
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}
-(void)executePlugin:(NSDictionary *)jsonDict{
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    self.jsonDict = jsonDict;
    NSString *command = [jsonDict objectForKey:@"command"];
    if([command isEqualToString:@"PLGN_STR_CRD_SECURELY"]){
        NSString *userId = [jsonDict objectForKey:@"userId"];
        NSString *password = [jsonDict objectForKey:@"password"];
        NSString * value ;
        if(userId != nil && ![userId isEqualToString:@""] && password != nil && ![password isEqualToString:@""]){
            value = [NSString stringWithFormat: @"%@%@%@",userId, @"#",password];}
        NSString *key = [@"key_" stringByAppendingString:self.viewController.uniqueID];
        [[NSUserDefaults standardUserDefaults] setObject:@"Y" forKey:[@"isBiometricNeeded_" stringByAppendingString:key]];
        [[NSUserDefaults standardUserDefaults] synchronize];
        [self storeCredentialsSecurely:key userPassword:value isBiometricNeeded:@"Y" completionHandler:^(BOOL success, NSError *error){
        }];
    }
    else if([command isEqualToString:@"PLGN_LOGIN"]){
        NSDictionary *dict = [jsonDict objectForKey:@"params"];
        NSString *isBiometric = [dict objectForKey:@"isBiometric"];
        if([isBiometric isEqualToString:@"Y"]){
            [self login:jsonDict];}
        else {
            [AppzillonLogin proceedTologin:jsonDict viewController:self.viewController webView:self.webView];
        }
    }
    else if([command isEqualToString:@"PLGN_STR_SECURELY"]){
        NSString *key = [jsonDict objectForKey:@"key"];
        NSString *value = [jsonDict objectForKey:@"value"];
        NSString *biometricNeeded = [jsonDict objectForKey:@"promptBiometric"];
        [self storeSecurely:key value:value isBiometricNeeded:biometricNeeded completionHandler:^(BOOL success, NSError *error){
            if(success && error == nil){
                NSArray *resultkeys=[NSArray arrayWithObjects:@"result",nil];
                NSArray *result=[NSArray arrayWithObjects:@"Successful",nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
            }
        }];
    }
    else if([command isEqualToString:@"PLGN_RTR_SECURELY"]){
        NSString *key = [jsonDict objectForKey:@"key"];
        [self retrieveSecurely:key  completionHandler:^(NSString *value, NSError* error, BOOL success){
            if(value != nil && ![value isEqualToString:@""] && success && error == nil){
                NSArray *resultkeys=[NSArray arrayWithObjects:@"value",nil];
                NSArray *result=[NSArray arrayWithObjects:value,nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                [Logger logger_Log:@"E" :@"APZSecureStorage--Retrieved Successfully"];
            }
        }];
    }
}

#pragma mark - Secure Storage Plugins

-(void)storeSecurely:(NSString *)key  value:(NSString *)value  isBiometricNeeded:(NSString *)isBiometricNeeded completionHandler:(void (^)(BOOL success, NSError* error))completionHandler{
    if([isBiometricNeeded isEqualToString:@"Y"]) {
        if(key != nil && ![key isEqualToString:@""] && value != nil && ![value isEqualToString:@"" ]){
            [self verifyUser:@"Are you the device owner?" completionHandler:^(BOOL success, NSError * error){
                if(success && error == nil){
                    BOOL status = [self saveDataWithBiometric:key value:value];
                    if (status) {
                        [[NSUserDefaults standardUserDefaults] setObject:@"Y" forKey:[@"isBiometricNeeded_" stringByAppendingString:key]];
                        [[NSUserDefaults standardUserDefaults] synchronize];
                        completionHandler(YES, nil);
                    }
                }
                else{
                    if(error.code == 99972 && [error.localizedDescription isEqualToString:@"No identities are enrolled."]){
                        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                        NSArray *result=[NSArray arrayWithObjects:BIOAUTH_NOT_ENROLLED,nil];
                        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                    }
                    else{
                        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                        NSArray *result;
                        if ([error.localizedDescription isEqualToString:@"Canceled by user."]) {
                            result=[NSArray arrayWithObjects:@"APZ-CNT-231",nil];
                        }else if ([error.localizedDescription isEqualToString:@"Application retry limit exceeded."]){
                            result=[NSArray arrayWithObjects:@"APZ-CNT-235",nil];
                        }else if ([error.localizedDescription isEqualToString:@"Biometry is locked out."]){
                            result=[NSArray arrayWithObjects:@"APZ-CNT-236",nil];
                        }else if ([error.localizedDescription isEqualToString:@"Fallback authentication mechanism selected."]){
                            result=[NSArray arrayWithObjects:@"APZ-CNT-237",nil];
                        }else{
                            result=[NSArray arrayWithObjects:@"APZ-CNT-232",nil];
                        }
                        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                    }
                }
            }];
            
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-231",nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        }
    }else if([isBiometricNeeded isEqualToString:@"N"]){
        if(key != nil && ![key isEqualToString:@""] && value != nil && ![value isEqualToString:@""]){
            //            BOOL status = [self saveDataWithBiometric:key value:value];
            BOOL status = [self saveDataWithoutBiometric:key value:value];
            if (status) {
                [[NSUserDefaults standardUserDefaults] setObject:@"N" forKey:[@"isBiometricNeeded_" stringByAppendingString:key]];
                [[NSUserDefaults standardUserDefaults] synchronize];
                completionHandler(YES, nil);
            }else{
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-232",nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            }
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-231",nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-231",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    }
}
-(void)retrieveSecurely:(NSString *)key  completionHandler:(void (^)(NSString *value, NSError* error, BOOL success))completionHandler{
    NSString *isbiometricNeeded = [[NSUserDefaults standardUserDefaults] objectForKey:[@"isBiometricNeeded_" stringByAppendingString:key]];
    if([isbiometricNeeded isEqualToString:@"Y"]){
        [self verifyUserforLogin:@"Authenticate With Biometric To Unlock." completionHandler:^(BOOL success, NSError * error){
            if(success && error == nil){
                NSString *value;
                value = [self getDataFromKeychain:key];
                if(key != nil && ![key isEqualToString:@""]  && value != nil && ![value isEqualToString:@""]){
                    completionHandler(value,nil,YES);
                }else{
                    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                    NSArray *result;
                    if ([error.localizedDescription isEqualToString:@"Canceled by user."]) {
                        result=[NSArray arrayWithObjects:@"APZ-CNT-231",nil];
                    }else if ([error.localizedDescription isEqualToString:@"Application retry limit exceeded."]){
                        result=[NSArray arrayWithObjects:@"APZ-CNT-235",nil];
                    }else if ([error.localizedDescription isEqualToString:@"Biometry is locked out."]){
                        result=[NSArray arrayWithObjects:@"APZ-CNT-236",nil];
                    }else if ([error.localizedDescription isEqualToString:@"Fallback authentication mechanism selected."]){
                        result=[NSArray arrayWithObjects:@"APZ-CNT-237",nil];
                    }else{
                        result=[NSArray arrayWithObjects:@"APZ-CNT-232",nil];
                    }
                    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                }
            }
            else{
                if(error.code == 99972 && [error.localizedDescription isEqualToString:@"No identities are enrolled."]){
                    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                    NSArray *result=[NSArray arrayWithObjects:BIOAUTH_NOT_ENROLLED,nil];
                    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                }
                else{
                    //Below lines of code is to notify the user for biometric fail conditions.
                    NSMutableDictionary *callbackDict = [[NSMutableDictionary alloc] initWithDictionary:[[self.jsonDict objectForKey:@"params"] objectForKey:@"reqFull"]];
                    NSMutableDictionary *headerDict = [[NSMutableDictionary alloc] initWithDictionary:[[[self.jsonDict objectForKey:@"params"] objectForKey:@"reqFull"]objectForKey:@"appzillonHeader"]];
                    [headerDict setObject:[NSNumber numberWithBool:false] forKey:@"status"];
                    [callbackDict setObject:headerDict forKey:@"appzillonHeader"];
                    NSString *errorCode, *errorMessage;
                    if ([error.localizedDescription isEqualToString:@"Canceled by user."]) {
                        errorCode = @"APZ-CNT-231";
                        errorMessage = @"Fingerprint operation canceled.";
                    }else if ([error.localizedDescription isEqualToString:@"Application retry limit exceeded."]){
                        errorCode = @"APZ-CNT-235";
                        errorMessage = @"Application retry limit exceeded.";
                    }else if ([error.localizedDescription isEqualToString:@"Biometry is locked out."]){
                        errorCode = @"APZ-CNT-236";
                        errorMessage = @"Biometry is locked out.";
                    }else if ([error.localizedDescription isEqualToString:@"Fallback authentication mechanism selected."]){
                        errorCode = @"APZ-CNT-237";
                        errorMessage = @"Fallback authentication mechanism selected.";
                    }else{
                        errorCode = @"APZ-CNT-232";
                        errorMessage = @"Too many attempts. Try again later.";
                    }
                    NSMutableDictionary *errorDict = [[NSMutableDictionary alloc] init];
                    [errorDict setObject:errorCode forKey:@"errorCode"];
                    [errorDict setObject:errorMessage forKey:@"errorMessage"];
                    NSArray *errorArray = [[NSArray alloc] initWithObjects:errorDict, nil];
                    [callbackDict setObject:errorArray forKey:@"appzillonErrors"];
                    NSMutableDictionary *callbackDictFinal = [[NSMutableDictionary alloc] init];
                    [callbackDictFinal setObject:callbackDict forKey:@"resFull"];
                    [callbackDictFinal setObject:[NSNumber numberWithBool:false] forKey:@"status"];
                    
                    NSArray *resultkeys=[NSArray arrayWithObjects:@"params",@"reqId",@"errorCode",nil];
                    NSArray *result=[NSArray arrayWithObjects:callbackDictFinal,[self.jsonDict objectForKey:@"reqId"],errorCode,nil];
                    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                }}
        }];
    }
    else if([isbiometricNeeded isEqualToString:@"N"]){
        NSString *value;
        value = [self getDataFromKeychain:key];
        if(key != nil && ![key isEqualToString:@""]  && value != nil && ![value isEqualToString:@""]){
            completionHandler(value,nil,YES);
        }
        else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-232",nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-232",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    }
}

-(void)storeCredentialsSecurely:(NSString *)userId  userPassword:(NSString *)password  isBiometricNeeded:(NSString *)isBiometricNeeded completionHandler:(void (^)(BOOL success, NSError* error))completionHandler{
    [self storeSecurely:userId value:password isBiometricNeeded:isBiometricNeeded completionHandler:^(BOOL success, NSError *error){
        if(success && error == nil){
            NSArray *resultkeys=[NSArray arrayWithObjects:@"result",nil];
            NSArray *result=[NSArray arrayWithObjects:@"Successful",nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        }
    }];
}

-(void)login:(NSDictionary *)jsonDict{
    NSString *key;
    key = [self getUserKey];
    [self retrieveSecurely:key  completionHandler:^(NSString *value, NSError* error,  BOOL success){
        if(success && error == nil){
            NSString *str= value;
            NSArray *values = [str componentsSeparatedByString:@"#"];
            NSString *userId=[values objectAtIndex:0];
            NSString *password=[values objectAtIndex:1];
            if(userId != nil && ![userId isEqualToString:@""] && password != nil && ![password isEqualToString:@""]){
                [AppzillonLogin proceedTologinWithBiometric:jsonDict userId:userId userPin:password viewController:self.viewController webView:self.webView];
            }else{
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result;
                if ([error.localizedDescription isEqualToString:@"Canceled by user."]) {
                    result=[NSArray arrayWithObjects:@"APZ-CNT-231",nil];
                }else if ([error.localizedDescription isEqualToString:@"Application retry limit exceeded."]){
                    result=[NSArray arrayWithObjects:@"APZ-CNT-235",nil];
                }else if ([error.localizedDescription isEqualToString:@"Biometry is locked out."]){
                    result=[NSArray arrayWithObjects:@"APZ-CNT-236",nil];
                }else if ([error.localizedDescription isEqualToString:@"Fallback authentication mechanism selected."]){
                    result=[NSArray arrayWithObjects:@"APZ-CNT-237",nil];
                }else{
                    result=[NSArray arrayWithObjects:@"APZ-CNT-232",nil];
                }
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            }
        }
    }];
}


#pragma mark - Custom Methods

- (void)verifyUser:(NSString *)msg completionHandler:(void (^)(BOOL success, NSError * error))completionHandler{
    LAContext *context=[[LAContext alloc] init];
    NSError *error;
    if ([context canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:&error]) {
        NSData *domainStateData = [context evaluatedPolicyDomainState];
        [[NSUserDefaults standardUserDefaults] setObject:domainStateData forKey:@"BioMetricDomainState"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        completionHandler(YES,nil);
    } else {
        error = [NSError errorWithDomain:@"" code:99972 userInfo:@{NSLocalizedDescriptionKey:error.localizedDescription}];
        completionHandler(NO,error);
    }
}


- (void)verifyUserforLogin:(NSString *)msg completionHandler:(void (^)(BOOL success, NSError * error))completionHandler{
    LAContext *context=[[LAContext alloc] init];
    NSError *error;
    if ([context canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:&error]) {
        NSData *domainStateData = [context evaluatedPolicyDomainState];
        NSData *oldDomianStateData = [[NSUserDefaults standardUserDefaults] objectForKey:@"BioMetricDomainState"];
        if (oldDomianStateData != nil) {
            if (![oldDomianStateData isEqual:domainStateData]) {
                [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"BioMetricDomainState"];
                [[NSUserDefaults standardUserDefaults] setObject:domainStateData forKey:@"BioMetricDomainState"];
                [[NSUserDefaults standardUserDefaults] synchronize];
                
                NSMutableDictionary *callbackDict = [[NSMutableDictionary alloc] initWithDictionary:[[self.jsonDict objectForKey:@"params"] objectForKey:@"reqFull"]];
                NSMutableDictionary *headerDict = [[NSMutableDictionary alloc] initWithDictionary:[[[self.jsonDict objectForKey:@"params"] objectForKey:@"reqFull"]objectForKey:@"appzillonHeader"]];
                [headerDict setObject:[NSNumber numberWithBool:false] forKey:@"status"];
                [callbackDict setObject:headerDict forKey:@"appzillonHeader"];
                NSString *errorCode, *errorMessage;
                
                errorCode = @"APZ-CNT-234";
                errorMessage = @"Fingerprint Changed.";
                
                NSMutableDictionary *errorDict = [[NSMutableDictionary alloc] init];
                [errorDict setObject:errorCode forKey:@"errorCode"];
                [errorDict setObject:errorMessage forKey:@"errorMessage"];
                NSArray *errorArray = [[NSArray alloc] initWithObjects:errorDict, nil];
                [callbackDict setObject:errorArray forKey:@"appzillonErrors"];
                NSMutableDictionary *callbackDictFinal = [[NSMutableDictionary alloc] init];
                [callbackDictFinal setObject:callbackDict forKey:@"resFull"];
                [callbackDictFinal setObject:[NSNumber numberWithBool:false] forKey:@"status"];
                
                NSArray *resultkeys=[NSArray arrayWithObjects:@"params",@"reqId",@"errorCode",nil];
                NSArray *result=[NSArray arrayWithObjects:callbackDictFinal,[self.jsonDict objectForKey:@"reqId"],errorCode,nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                return;
            }else{
                completionHandler(YES,nil);
            }
        }else{
            completionHandler(YES,nil);
        }
    } else {
        error = [NSError errorWithDomain:@"" code:99972 userInfo:@{NSLocalizedDescriptionKey:error.localizedDescription}];
        completionHandler(NO,error);
    }
}

-(BOOL) saveDataWithoutBiometric:(NSString*)key value:(NSString*)value{
    NSString * base64Encoding = [CryptoUtility getAESEncryptedString:value :key];
    NSData* passwordData = [base64Encoding dataUsingEncoding:NSUTF8StringEncoding];
    BOOL keychainStatus = NO;
    
    NSDictionary *attributes = @{
        //Sec class, in this case just a password
        (__bridge id)kSecClass: (__bridge id)kSecClassGenericPassword,
        //Our service UUID/Name
        (__bridge id)kSecAttrService: key,
        //The data to insert
        (__bridge id)kSecValueData: passwordData,
        //Whether or not we want to prompt on insert
        (__bridge id)kSecUseAuthenticationUI: @YES,
    };
    SecItemDelete((__bridge CFDictionaryRef) attributes);
    OSStatus status = SecItemAdd((__bridge CFDictionaryRef)attributes, nil);
    if (status ==  0) {
        keychainStatus = YES;
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-232",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        keychainStatus = NO;
    }
    return keychainStatus;
}

-(BOOL) saveDataWithBiometric:(NSString*)key value:(NSString*)value{
    NSString * base64Encoding = [CryptoUtility getAESEncryptedString:value :key];
    NSData* passwordData = [base64Encoding dataUsingEncoding:NSUTF8StringEncoding];
    BOOL keychainStatus = NO;
    
    SecAccessControlRef sacRef;
    CFErrorRef *err = nil;
    sacRef = SecAccessControlCreateWithFlags(kCFAllocatorDefault,
                                             kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly,
                                             kSecAccessControlUserPresence,
                                             err);
    
    NSDictionary *attributes = @{
        //Sec class, in this case just a password
        (__bridge id)kSecClass: (__bridge id)kSecClassGenericPassword,
        //Our service UUID/Name
        (__bridge id)kSecAttrService: key,
        //The data to insert
        (__bridge id)kSecValueData: passwordData,
        //Whether or not we want to prompt on insert
        (__bridge id)kSecUseAuthenticationUI: @YES,
        //Our security access control reference
        (__bridge id)kSecAttrAccessControl: (__bridge_transfer id)sacRef
    };
    SecItemDelete((__bridge CFDictionaryRef) attributes);
    OSStatus status = SecItemAdd((__bridge CFDictionaryRef)attributes, nil);
    if (status ==  0) {
        keychainStatus = YES;
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-232",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        keychainStatus = NO;
    }
    return keychainStatus;
}

-(NSString *) getDataFromKeychain:(NSString *)key{
    
    NSString* pass;
    
    NSDictionary *query = @{
        (__bridge id)kSecClass: (__bridge id)kSecClassGenericPassword,
        (__bridge id)kSecAttrService: key,
        (__bridge id)kSecReturnData: @YES,
        (__bridge id)kSecUseOperationPrompt: @"Authenticate With Biometric To Unlock."
    };
    CFTypeRef dataTypeRef = NULL;
    OSStatus status = SecItemCopyMatching((__bridge CFDictionaryRef)(query), &dataTypeRef);
    if (status == errSecSuccess){
        NSData *resultData = ( __bridge_transfer NSData *)dataTypeRef;
        
        NSString *result = [[NSString alloc]
                            initWithData:resultData
                            encoding:NSUTF8StringEncoding];
        NSString *decryptbase64String = [CryptoUtility getAESDecryptedString:result :key];
        pass = decryptbase64String;
    }else{
        NSLog(@"Something went wrong");
    }
    return pass;
}

-(NSString *)getUserKey{
    NSString *key = [@"key_" stringByAppendingString:self.viewController.uniqueID];
    NSString *isbiometricNeeded = [[NSUserDefaults standardUserDefaults] objectForKey:[@"isBiometricNeeded_" stringByAppendingString:key]];
    if([isbiometricNeeded isEqualToString:@"Y"]){
        return key;
    }else{
        NSArray *keys = [[[NSUserDefaults standardUserDefaults] dictionaryRepresentation] allKeys];
        NSString *newKey = @"";
        for(NSString* foundKey in keys){
            if([foundKey containsString:@"isBiometricNeeded_"]){
                NSArray *keyArray =  [foundKey componentsSeparatedByString: @"isBiometricNeeded_"];
                NSString *value = [self getDataFromKeychain:[keyArray objectAtIndex:1]];
                [[NSUserDefaults standardUserDefaults] removeObjectForKey:foundKey];
                [[NSUserDefaults standardUserDefaults] synchronize];
                newKey = [@"key_" stringByAppendingString:self.viewController.uniqueID];
                [[NSUserDefaults standardUserDefaults] setObject:@"Y" forKey:[@"isBiometricNeeded_" stringByAppendingString:newKey]];
                [[NSUserDefaults standardUserDefaults] synchronize];
                [self saveDataWithBiometric:newKey value:value];
            }
        }
        return newKey;
    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZSecureStorage--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}


@end



