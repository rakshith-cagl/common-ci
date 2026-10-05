//
//  APZDecrypt.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZDecrypt.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
#import "CryptoUtility.h"

@interface APZDecrypt()
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZDecrypt

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZDecrypt--execute"];
    if(jsonDict != nil){
        self.pluginId=[jsonDict objectForKey:PLUGINID];
        [self aesDecrypt:jsonDict];
        [self cleanPlugin];
    }
    else{
    }
}




-(void)aesDecrypt:(NSDictionary *)jsonDict{
    //    NSMutableString *keyStr = [CryptoUtility checkKeyLengthDecrypt:[jsonDict objectForKey:STORAGE_AES_KEY]];
    //    NSData *keyy = [CryptoUtility AESKeyForPasswordDecrypt:keyStr];
    //   if (keyy!=nil) {
    //    NSString * rIv=[CryptoUtility reverseStringDecrypt:keyStr];
    NSString *resultStr=[CryptoUtility getAESDecryptedString:[jsonDict objectForKey:STORAGE_AES_CTEXT] :[jsonDict objectForKey:STORAGE_AES_KEY]];
    if (resultStr==nil) {
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_D_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        
        [Logger logger_Log:@"E" :@"APZDecrypt--AES Decryption error"];
        
    }else{
        NSArray * resultkeys=[NSArray arrayWithObjects:DECRYPTID,CBTEXT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:[jsonDict objectForKey:DECRYPTID],resultStr,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"I" :@"APZDecrypt--success"];
        
    }
    //   }else{
    //        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    //        NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_K_D_ERROR,nil];
    //       [self jsLayerCall:JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
    //        [Logger logger_Log:@"E" :@"APZDecrypt--AES key derivation error"];
    //    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZDecrypt--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}
@end

