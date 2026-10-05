//
//  APZFileEncryption.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 26/11/14.
//
//

#import "APZFileEncryption.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Base64.h"
#import <CommonCrypto/CommonCryptor.h>
#import <CommonCrypto/CommonKeyDerivation.h>
#import <CommonCrypto/CommonDigest.h>
#import "Logger.h"
#import "AppzillonViewController.h"

@interface APZFileEncryption()
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZFileEncryption
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZFileEncryption--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if(jsonDict != nil){
        [self aesEncrypt:jsonDict];
        [self cleanPlugin];
    }
}

const CCAlgorithm kFileAlgorithm = kCCAlgorithmAES128;
const NSUInteger kFileAlgorithmKeySize = kCCKeySizeAES128;
const NSUInteger kFileAlgorithmBlockSize = kCCBlockSizeAES128;
const NSUInteger kFileAlgorithmIVSize = kCCBlockSizeAES128;
const NSUInteger kFilePBKDFSaltSize = 16;
const NSUInteger kFilePBKDFRounds = 2;

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
    else if (keyLen > 16){
        [finalKeyStr insertString:@"$" atIndex:16];
    }
    return finalKeyStr;
}

-(NSData *)AESKeyForPassword:(NSString *)passwordKey{
    NSMutableData *derivedKey = [NSMutableData dataWithLength:kFileAlgorithmKeySize];
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
                                      kFilePBKDFRounds,
                                      derivedKey.mutableBytes,
                                      derivedKey.length);
    if (result == kCCSuccess) {
        return derivedKey;
    }else{
        
        return nil;
    }
}

-(void)aesEncrypt:(NSDictionary *)jsonDict{
    NSError *error=nil;
    NSMutableString *keyStr = [self checkKeyLength:[jsonDict objectForKey:STORAGE_AES_KEY]];
    NSString *fileSourcePath=[jsonDict objectForKey:@"srcFilePath"];
    NSString *fileDestination=[jsonDict objectForKey:@"destFilePath"];
    NSString *destinationFileName=[fileDestination lastPathComponent];
    NSString* fileDestinationFolders=[fileDestination stringByDeletingLastPathComponent];
    NSData *keyy = [self AESKeyForPassword:keyStr];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *appzillonAppSandbox=[documentsDirectory stringByAppendingPathComponent:@"Assets/apps"];
    appzillonAppSandbox=[appzillonAppSandbox stringByAppendingPathComponent:self.viewController.appString];
    if (![[NSFileManager defaultManager] fileExistsAtPath:fileSourcePath]){
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILE_NOT_FOUND_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZFileEncryption--File Not Found"];
    }else{
        fileDestinationFolders = [appzillonAppSandbox stringByAppendingPathComponent:fileDestinationFolders];
        NSString *ActualPath;
        if (![[NSFileManager defaultManager] fileExistsAtPath:fileDestination])
            [[NSFileManager defaultManager] createDirectoryAtPath:fileDestinationFolders withIntermediateDirectories:YES attributes:nil error:&error];
        if (error==nil) {
            ActualPath=[fileDestinationFolders stringByAppendingPathComponent:destinationFileName];
            //        NSString *encryptedString= [NSString stringWithUTF8String:[keyy bytes]];
            if (keyy!=nil) {
                NSString * rIv=[self reverseString:keyStr];
                const char * rBytes= [rIv UTF8String];
                char *bufferB = malloc([rIv length]);
                if (bufferB) {
                    for (int i=0;i<[rIv length]; i++) {
                        bufferB[i]=rBytes[i] >> 1;
                    }
                    const char *iiv=bufferB;
                    NSData *data=[NSData dataWithContentsOfFile:fileSourcePath];
                    NSUInteger fileDataLength = [data length];
                    size_t fileBufferSize = fileDataLength + kCCBlockSizeAES128;
                    void *fileBuffer = malloc(fileBufferSize);
                    if (fileBuffer) {
                        size_t numBytesEncrypted = 0;
                        CCCryptorStatus ccResult = CCCrypt(kCCEncrypt, kCCAlgorithmAES128,kCCOptionPKCS7Padding,  [keyy bytes],kCCKeySizeAES128, iiv, [data bytes], fileDataLength, fileBuffer,fileBufferSize, &numBytesEncrypted);
                        if (ccResult == kCCSuccess) {
                            NSData *encrypetedFileData= [NSData dataWithBytesNoCopy:fileBuffer length:numBytesEncrypted];
//                            if ([encrypetedFileData writeToFile:ActualPath atomically:YES]==YES) {
                            if ([MiscellaneousMethods writeData:encrypetedFileData toFile:ActualPath]) {
                                NSArray * resultkeys=[NSArray arrayWithObjects:@"filePath",nil];
                                NSArray *resultMsg=[NSArray arrayWithObjects:ActualPath,nil];
                                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                                [Logger logger_Log:@"I" :@"APZFileEncryption--Success"];
                            }
                        }
                        else{
                            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                            NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_E_ERROR,nil];
                            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                            [Logger logger_Log:@"E" :@"APZFileEncryption--Failure"];
                        }
                        fileBuffer=nil;
                        bufferB=nil;
                    }
                }
            }else{
                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_E_ERROR,nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                [Logger logger_Log:@"E" :@"APZFileEncryption--Failure"];
            }
        }else{
            [Logger logger_Log:@"E" :@"APZFileEncryption--Could not Create File Directory for Addtional User Files"];
        }
    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    self.webView = nil;
    [self.delegate donePlugin:self];
}


@end

