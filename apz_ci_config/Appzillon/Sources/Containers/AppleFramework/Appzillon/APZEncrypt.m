//
//  APZEncrypt.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZEncrypt.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Base64.h"
#import "Logger.h"
#import "CryptoUtility.h"

@interface APZEncrypt()
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZEncrypt
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZEncrypt--execute"];
    if(jsonDict != nil){
        self.pluginId=[jsonDict objectForKey:PLUGINID];
        [self aesEncrypt:jsonDict];
        [self cleanPlugin];
    }
    else{
        
    }
}


-(void)aesEncrypt:(NSDictionary *)jsonDict{
    //    NSMutableString *keyStr = [CryptoUtility checkKeyLength:[jsonDict objectForKey:STORAGE_AES_KEY]];
    //    NSData *keyy = [CryptoUtility AESKeyForPassword:keyStr];
    //    if (keyy!=nil) {
    //        NSString * rIv=[CryptoUtility reverseString:keyStr];
    NSString *base64String=[CryptoUtility getAESEncryptedString:[jsonDict objectForKey:STORAGE_AES_PTEXT] :[jsonDict objectForKey:STORAGE_AES_KEY]];
    if (base64String==nil) {
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_E_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZEncrypt--AES Encryption error"];
        
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ENCRYPTID,CBTEXT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:[jsonDict objectForKey:ENCRYPTID],base64String,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"I" :@"APZEncrypt--success"];
    }
    //    }else{
    //        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    //        NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_K_D_ERROR,nil];
    //        [self jsLayerCall:JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
    //        [Logger logger_Log:@"E" :@"APZEncrypt--AES key derivation error"];
    //    }
}



#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZEncrypt--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end
