//
//  APZFileDecryption.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 26/11/14.
//
//

#import "APZFileDecryption.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Base64.h"
#import <CommonCrypto/CommonCryptor.h>
#import <CommonCrypto/CommonKeyDerivation.h>
#import <CommonCrypto/CommonDigest.h>
#import "Logger.h"
#import "AppzillonViewController.h"

@interface APZFileDecryption()
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZFileDecryption
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}
-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZFileDecryption--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if(jsonDict != nil){
        [self aesDecrypt:jsonDict];
        [self cleanPlugin];
    }
    else{
        
    }
}
const CCAlgorithm kFileDecryptAlgorithm = kCCAlgorithmAES128;
const NSUInteger kFileDecryptAlgorithmKeySize = kCCKeySizeAES128;
const NSUInteger kFileDecryptAlgorithmBlockSize = kCCBlockSizeAES128;
const NSUInteger kFileDecryptAlgorithmIVSize = kCCBlockSizeAES128;
const NSUInteger kFileDecryptPBKDFSaltSize = 16;
const NSUInteger kFileDecryptPBKDFRounds = 2;

-(NSString *) reverseString:(NSString *)string
{
    NSMutableString *reversedStr;
    NSUInteger len = [string length];
    reversedStr = [NSMutableString stringWithCapacity:len];
    while (len > 0){
        [reversedStr appendString:
         [NSString stringWithFormat:@"%C", [string characterAtIndex:--len]]];
    }
    return reversedStr;
}

-(NSMutableString *)checkKeyLength:(NSString *)keyStr{
    NSMutableString *finalKeyStr =[keyStr mutableCopy];
    NSUInteger keyLen = keyStr.length;
    if(keyLen < 16){
        NSUInteger nPadding = 16 -keyLen;
        for(int i =0;i<nPadding;i++){
            if(keyLen<16){
                [finalKeyStr insertString:@"$" atIndex:keyLen++];
            }
        }
    }
    return finalKeyStr;
}

-(NSData *)AESKeyForPassword:(NSString *)passwordKey{
    NSMutableData *derivedKey = [NSMutableData dataWithLength:kFileDecryptAlgorithmKeySize];
    NSData* data = [passwordKey dataUsingEncoding:NSUTF8StringEncoding];
    char *saltchar =(char *)[data bytes];
    NSUInteger saltLen= passwordKey.length;
    char firstChar= saltchar[0];
    saltchar[0]=saltchar[1];
    saltchar[1]=firstChar;
    char lastChar= saltchar[saltLen-1];
    saltchar[saltLen-1]=saltchar[saltLen-2];
    saltchar[saltLen-2]=lastChar;
    const uint8_t *saltcharBytes =(const uint8_t *)saltchar;
    int result = CCKeyDerivationPBKDF(kCCPBKDF2,
                                      [passwordKey UTF8String],
                                      passwordKey.length,
                                      saltcharBytes,
                                      saltLen,
                                      kCCPRFHmacAlgSHA1 ,
                                      kFileDecryptPBKDFRounds,                                              derivedKey.mutableBytes,
                                      derivedKey.length);
    if (result == kCCSuccess) {
        return derivedKey;
    }else{
        
        return nil;
    }
    
}

-(void)aesDecrypt:(NSDictionary *)jsonDict{
    NSString *srcFilePath=[jsonDict objectForKey:@"srcFilePath"];
    NSString *destinationFilePath=[jsonDict objectForKey:@"destFilePath"];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *appzillonAppFolder=[documentsDirectory stringByAppendingPathComponent:@"Assets/apps"];
    appzillonAppFolder=[appzillonAppFolder stringByAppendingPathComponent:self.viewController.appString];
    NSString *destinationFile = [appzillonAppFolder stringByAppendingPathComponent:destinationFilePath];
    if ([[NSFileManager defaultManager] fileExistsAtPath:srcFilePath]){
        NSString *destinationFolder =[destinationFile stringByDeletingLastPathComponent];
        NSError *error;
        if (![[NSFileManager defaultManager] fileExistsAtPath:destinationFolder])
            [[NSFileManager defaultManager] createDirectoryAtPath:destinationFolder withIntermediateDirectories:YES attributes:nil error:&error];
        if (error==nil) {
            NSMutableString *keyStr = [self checkKeyLength:[jsonDict objectForKey:STORAGE_AES_KEY]];
            NSData *keyy = [self AESKeyForPassword:keyStr];
            NSString * rIv=[self reverseString:keyStr];
            const char * rBytes= [rIv UTF8String];
            char *bufferB = malloc([rIv length]);
            if (bufferB) {
                for (int i=0;i<[rIv length]; i++) {
                    bufferB[i]=rBytes[i] >> 1;
                }
                const char *ivChars = bufferB;
                if (keyy!=nil) {
                    NSData *cyData=[NSData dataWithContentsOfFile:srcFilePath];
                    NSUInteger dataLength = [cyData length];
                    size_t bufferSize = dataLength + kCCBlockSizeAES128;
                    void *buffer = malloc(bufferSize);
                    if (buffer) {
                        size_t numBytesDecrypted = 0;
                        CCCryptorStatus ccResult = CCCrypt(kCCDecrypt, kCCAlgorithmAES128, kCCOptionPKCS7Padding,
                                                           [keyy bytes],kCCKeySizeAES128, ivChars,[cyData bytes], dataLength,buffer, bufferSize,&numBytesDecrypted);
                        NSData *decyptedFileData= [NSData dataWithBytesNoCopy:buffer length:numBytesDecrypted];
                        if (ccResult == kCCSuccess) {
//                            if ([decyptedFileData writeToFile:destinationFile atomically:YES]==YES) {
                            if ([MiscellaneousMethods writeData:decyptedFileData toFile:destinationFile]) {
                                NSArray * resultkeys=[NSArray arrayWithObjects:@"filePath",nil];
                                NSArray *resultMsg=[NSArray arrayWithObjects:destinationFile,nil];
                                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                                [Logger logger_Log:@"I" :@"APZFileDecryption--Success"];
                            }
                            else{
                                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                                NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_D_ERROR,nil];
                                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                                [Logger logger_Log:@"E" :@"APZFileDecryption--Failure"];
                                
                            }
                            
                        }else{
                            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                            NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_D_ERROR,nil];
                            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                            [Logger logger_Log:@"E" :@"APZFileDecryption--Failure"];
                            
                        }
                    }
                }else{
                    NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_D_ERROR,nil];
                    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                    [Logger logger_Log:@"E" :@"APZFileDecryption--Failure"];
                }
            }
        }else{
            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:DIRECTORY_CREATION_FAILED,nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
            [Logger logger_Log:@"E" :@"APZFileDecryption--Directory Creation Failed at Destination Path"];
        }
    }else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILE_NOT_FOUND_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZFileDecryption--File Not Found"];
    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZFileDecryption--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}


@end


