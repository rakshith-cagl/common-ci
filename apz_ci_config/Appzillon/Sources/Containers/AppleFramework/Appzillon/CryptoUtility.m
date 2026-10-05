//
//  CyptoUtility.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 05/02/18.
//

#import "CryptoUtility.h"
#import <CommonCrypto/CommonCryptor.h>
#import <CommonCrypto/CommonKeyDerivation.h>
#import <CommonCrypto/CommonDigest.h>
#import "Base64.h"
#import "KeychainUtility.h"
#import "Constants.h"
#import "MiscellaneousMethods.h"



@implementation CryptoUtility
#pragma mark - AES Encryption
static NSData *publicKeyData;
const CCAlgorithm kAlgorithm = kCCAlgorithmAES128;
const NSUInteger kAlgorithmKeySize = kCCKeySizeAES128;
const NSUInteger kAlgorithmBlockSize = kCCBlockSizeAES128;
const NSUInteger kAlgorithmIVSize = kCCBlockSizeAES128;
const NSUInteger kPBKDFSaltSize = 16;
const NSUInteger kPBKDFRounds = 2;
const NSUInteger kCryptoBlockSize = 256;
const NSUInteger kPBKDFRoundsForServerCalls = 2;

+(NSString *) reverseStringEncrypt:(NSString *)string
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

+(NSMutableString *)checkKeyLengthEncrypt:(NSString *)keyStr{
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
    else if(keyLen >16){
        [finalKeyStr setString:[finalKeyStr substringToIndex:16]];
    }
    return finalKeyStr;
}

+(NSData *)AESKeyForPassword:(NSString *)passwordKey{
    NSMutableData *derivedKey = [NSMutableData dataWithLength:kAlgorithmKeySize];
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
                                      kPBKDFRounds,
                                      derivedKey.mutableBytes,
                                      derivedKey.length);
    if (result == kCCSuccess) {
        return derivedKey;
    }else{
        return nil;
    }
}


//New Encryption/Decryption changes
+(NSString*)getRandomString{
    int len = 32;
    NSString *letters = @"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    NSMutableString *randomString = [NSMutableString stringWithCapacity: len];
    for (int i=0; i<len; i++) {
        [randomString appendFormat: @"%C", [letters characterAtIndex: arc4random() % [letters length]]];
    }
    NSString *stringToReturn = [randomString uppercaseString];
    return stringToReturn;
    
}

+(NSData *)AESKeyForPasswordForServerCalls:(NSString *)passwordKey{
    NSMutableData *derivedKey = [NSMutableData dataWithLength:kAlgorithmKeySize];
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
                                      kPBKDFRoundsForServerCalls,
                                      derivedKey.mutableBytes,
                                      derivedKey.length);
    if (result == kCCSuccess) {
        return derivedKey;
    }else{
        return nil;
    }
}
+(NSData *)AESKeyForPasswordDecryptForServerCalls:(NSString *)passwordKey{
    NSMutableData *derivedKey = [NSMutableData dataWithLength:kDecryptAlgorithmKeySize];
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
                                      [passwordKey UTF8String],                                        passwordKey.length,
                                      saltcharBytes,
                                      saltLen,
                                      kCCPRFHmacAlgSHA1 ,
                                      kDecryptPBKDFRoundsForServerCalls,
                                      derivedKey.mutableBytes,
                                      derivedKey.length);
    
    if (result == kCCSuccess) {
        return derivedKey;
    }else{
        return nil;
    }
}
+(NSString*)getAESEncryptedStringForServerCalls:(id)dataToEncrypt :(NSString*)keyStr{
    NSString *base64String;
    NSString *stringValue;
    if ([dataToEncrypt isKindOfClass:[NSMutableDictionary class]]||[dataToEncrypt isKindOfClass:[NSDictionary class]]) {
        stringValue= [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dataToEncrypt options:0 error:nil] encoding:NSUTF8StringEncoding];
    }else{
        stringValue=dataToEncrypt;
    }
    keyStr = [CryptoUtility checkKeyLengthEncrypt:keyStr];
    NSData *keyy = [CryptoUtility AESKeyForPasswordForServerCalls:keyStr];
    if (keyy!=nil) {

        //        NSString * rIv=[CryptoUtility reverseStringEncrypt:keyStr];
      
        NSString * rIv= [CryptoUtility getRandomString];
        rIv = [CryptoUtility checkKeyLengthEncrypt:rIv];
        

        const char * rBytes= [rIv UTF8String];
        char *bufferB = malloc([rIv length]);
        if (bufferB) {
            for (int i=0;i<[rIv length]; i++) {
                bufferB[i]=rBytes[i] >> 1;
            }
//            const char *iiv=bufferB;
            const char *planeText  =[stringValue UTF8String];
            //            NSUInteger dataLength = [stringValue length] + 5;
            //            size_t bufferSize = dataLength + kCCBlockSizeAES128;
            size_t bufferSize = strlen(planeText) + kCCBlockSizeAES128 + kCryptoBlockSize;
            void *buffer = malloc(bufferSize);
            if (buffer) {
                size_t numBytesEncrypted = 0;
                
                NSData *ivData = [rIv dataUsingEncoding:NSUTF8StringEncoding];
            
                CCCryptorStatus ccResult = CCCrypt(kCCEncrypt, kCCAlgorithmAES128,kCCOptionPKCS7Padding,  [keyy bytes],kCCKeySizeAES128, [ivData bytes], planeText, strlen(planeText), buffer,bufferSize, &numBytesEncrypted);
                                
                NSData *actualPayload = [NSData dataWithBytes: buffer length:numBytesEncrypted];
                
                NSMutableData* ivAndActualPayloadData = [[NSMutableData alloc] init];
                [ivAndActualPayloadData appendData:ivData];
                [ivAndActualPayloadData appendData:actualPayload];
                
                [Base64 initialize];
                base64String=[Base64 encode: ivAndActualPayloadData];
                if (ccResult != kCCSuccess) {
                    base64String=nil;
                }
                free(buffer);
                free(bufferB);
            }
        }
    }
    
   
    return base64String;
}


+(NSString*)getAESDecryptedStringForServerCalls:(id)dataToEncrypt :(NSString*)keyStr{
    NSString *resultStr;
    NSString *stringValue;
    if ([dataToEncrypt isKindOfClass:[NSMutableDictionary class]]||[dataToEncrypt isKindOfClass:[NSDictionary class]]) {
        stringValue= [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dataToEncrypt options:0 error:nil] encoding:NSUTF8StringEncoding];
    }else{
        stringValue=dataToEncrypt;
    }
    [Base64 initialize];
    
    NSData *totalData=[Base64 decode:stringValue];
    NSData *finalIV = [totalData subdataWithRange:NSMakeRange(0, 16)];

    NSData *finalCyData = [totalData subdataWithRange:NSMakeRange(16, (totalData.length - 16))];
    
    keyStr = [self checkKeyLengthDecrypt:keyStr];
    
    NSData *keyy = [CryptoUtility AESKeyForPasswordDecryptForServerCalls:keyStr];
    if (keyy!=nil) {
            if (keyy!=nil) {
                NSUInteger dataLength = [finalCyData length];
                size_t bufferSize = dataLength + kCCBlockSizeAES128;
                void *buffer = malloc(bufferSize);
                if (buffer) {
                    size_t numBytesDecrypted = 0;
                    CCCryptorStatus ccResult = CCCrypt(kCCDecrypt, kCCAlgorithmAES128, kCCOptionPKCS7Padding,
                                                       [keyy bytes],kCCKeySizeAES128, [finalIV bytes],[finalCyData bytes], dataLength,buffer, bufferSize,&numBytesDecrypted);
                    
                    resultStr =[ [NSString alloc]initWithBytes:buffer length:numBytesDecrypted encoding:NSUTF8StringEncoding];
                    if (ccResult != kCCSuccess) {
                        resultStr=nil;
                    }
                    free(buffer);
                }
            }
    }
    return resultStr;
}

+(NSString*)getAESEncryptedString:(id)dataToEncrypt :(NSString*)keyStr{
    NSString *base64String;
    NSString *stringValue;
    if ([dataToEncrypt isKindOfClass:[NSMutableDictionary class]]||[dataToEncrypt isKindOfClass:[NSDictionary class]]) {
        stringValue= [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dataToEncrypt options:0 error:nil] encoding:NSUTF8StringEncoding];
    }else{
        stringValue=dataToEncrypt;
    }
    keyStr = [CryptoUtility checkKeyLengthEncrypt:keyStr];
    NSData *keyy = [CryptoUtility AESKeyForPassword:keyStr];
    if (keyy!=nil) {
        NSString * rIv=[CryptoUtility reverseStringEncrypt:keyStr];
        const char * rBytes= [rIv UTF8String];
        char *bufferB = malloc([rIv length]);
        if (bufferB) {
            for (int i=0;i<[rIv length]; i++) {
                bufferB[i]=rBytes[i] >> 1;
            }
            const char *iiv=bufferB;
            const char *planeText  =[stringValue UTF8String];
//            NSUInteger dataLength = [stringValue length] + 5;
//            size_t bufferSize = dataLength + kCCBlockSizeAES128;
            size_t bufferSize = strlen(planeText) + kCCBlockSizeAES128 + kCryptoBlockSize;
            void *buffer = malloc(bufferSize);
            if (buffer) {
                size_t numBytesEncrypted = 0;
                CCCryptorStatus ccResult = CCCrypt(kCCEncrypt, kCCAlgorithmAES128,kCCOptionPKCS7Padding,  [keyy bytes],kCCKeySizeAES128, iiv, planeText, strlen(planeText), buffer,bufferSize, &numBytesEncrypted);
                [Base64 initialize];
                base64String=[Base64 encode: [NSData dataWithBytes: buffer length:numBytesEncrypted]];
                if (ccResult != kCCSuccess) {
                    base64String=nil;
                }
                free(buffer);
                free(bufferB);
            }
        }
    }
    return base64String;
}

#pragma mark - AES Decryption


const CCAlgorithm kDecryptAlgorithm = kCCAlgorithmAES128;
const NSUInteger kDecryptAlgorithmKeySize = kCCKeySizeAES128;
const NSUInteger kDecryptAlgorithmBlockSize = kCCBlockSizeAES128;
const NSUInteger kDecryptAlgorithmIVSize = kCCBlockSizeAES128;
const NSUInteger kDecryptPBKDFSaltSize = 16;
const NSUInteger kDecryptPBKDFRounds = 2;
const NSUInteger kDecryptPBKDFRoundsForServerCalls = 2;

+(NSString *) reverseStringDecrypt:(NSString *)string
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

+(NSMutableString *)checkKeyLengthDecrypt:(NSString *)keyStr{
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
    else if(keyLen >16){
        [finalKeyStr setString:[finalKeyStr substringToIndex:16]];
    }
    return finalKeyStr;
}

+(NSData *)AESKeyForPasswordDecrypt:(NSString *)passwordKey{
    NSMutableData *derivedKey = [NSMutableData dataWithLength:kDecryptAlgorithmKeySize];
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
                                      [passwordKey UTF8String],                                        passwordKey.length,
                                      saltcharBytes,
                                      saltLen,
                                      kCCPRFHmacAlgSHA1 ,
                                      kDecryptPBKDFRounds,
                                      derivedKey.mutableBytes,
                                      derivedKey.length);
    
    if (result == kCCSuccess) {
        return derivedKey;
    }else{
        return nil;
    }
}

+(NSString*)getAESDecryptedString:(id)dataToEncrypt :(NSString*)keyStr{
    NSString *resultStr;
    NSString *stringValue;
    if ([dataToEncrypt isKindOfClass:[NSMutableDictionary class]]||[dataToEncrypt isKindOfClass:[NSDictionary class]]) {
        stringValue= [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dataToEncrypt options:0 error:nil] encoding:NSUTF8StringEncoding];
    }else{
        stringValue=dataToEncrypt;
    }
    keyStr = [self checkKeyLengthDecrypt:keyStr];
    NSData *keyy = [CryptoUtility AESKeyForPasswordDecrypt:keyStr];
    if (keyy!=nil) {
        NSString * rIv=[CryptoUtility reverseStringDecrypt:keyStr];
        const char * rBytes= [rIv UTF8String];
        char *bufferB = malloc([rIv length]);
        if (bufferB) {
            for (int i=0;i<[rIv length]; i++) {
                bufferB[i]=rBytes[i] >> 1;
            }
            const char *ivChars = bufferB;
            if (keyy!=nil) {
                [Base64 initialize];
                NSData *cyData=[Base64 decode:stringValue];
                NSUInteger dataLength = [cyData length];
                size_t bufferSize = dataLength + kCCBlockSizeAES128;
                void *buffer = malloc(bufferSize);
                if (buffer) {
                    size_t numBytesDecrypted = 0;
                    CCCryptorStatus ccResult = CCCrypt(kCCDecrypt, kCCAlgorithmAES128, kCCOptionPKCS7Padding,
                                                       [keyy bytes],kCCKeySizeAES128, ivChars,[cyData bytes], dataLength,buffer, bufferSize,&numBytesDecrypted);
                    
                    resultStr =[ [NSString alloc]initWithBytes:buffer length:numBytesDecrypted encoding:NSUTF8StringEncoding];
                    if (ccResult != kCCSuccess) {
                        resultStr=nil;
                    }
                    free(buffer);
                    free(bufferB);
                }
            }
        }
    }
    return resultStr;
}

#pragma mark - RSA Encryption/Decryption


+ (NSData *)stripPublicKeyHeader:(NSData *)d_key{
    if (d_key == nil) return(nil);
    unsigned long len = [d_key length];
    if (!len) return(nil);
    unsigned char *c_key = (unsigned char *)[d_key bytes];
    unsigned int  idx     = 0;
    if (c_key[idx++] != 0x30) return(nil);
    if (c_key[idx] > 0x80) idx += c_key[idx] - 0x80 + 1;
    else idx++;
    static unsigned char seqiod[] =
    { 0x30,   0x0d, 0x06, 0x09, 0x2a, 0x86, 0x48, 0x86, 0xf7, 0x0d, 0x01, 0x01,
        0x01, 0x05, 0x00 };
    if (memcmp(&c_key[idx], seqiod, 15)) return(nil);
    idx += 15;
    if (c_key[idx++] != 0x03) return(nil);
    if (c_key[idx] > 0x80) idx += c_key[idx] - 0x80 + 1;
    else idx++;
    if (c_key[idx++] != '\0') return(nil);
    return([NSData dataWithBytes:&c_key[idx] length:len - idx]);
}


+ (SecKeyRef)addPublicKey:(NSString*)appId{
    
    //    NSRange spos = [key rangeOfString:@"-----BEGIN PUBLIC KEY-----"];
    //    NSRange epos = [key rangeOfString:@"-----END PUBLIC KEY-----"];
    //    if(spos.location != NSNotFound && epos.location != NSNotFound){
    //        NSUInteger s = spos.location + spos.length;
    //        NSUInteger e = epos.location;
    //        NSRange range = NSMakeRange(s, e-s);
    //        key = [key substringWithRange:range];
    //    }
    //    key = [key stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    //    key = [key stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    //    key = [key stringByReplacingOccurrencesOfString:@"\t" withString:@""];
    //    key = [key stringByReplacingOccurrencesOfString:@" "  withString:@""];
    //    NSData *data = base64_decode(key);
    NSData *data=[self getPublicKeyData:appId];
    data = [self stripPublicKeyHeader:data];
    if(!data){
        return nil;
    }
    NSString *tag = @"RSAUtil_PubKey";
    NSData *d_tag = [NSData dataWithBytes:[tag UTF8String] length:[tag length]];
    NSMutableDictionary *publicKey = [[NSMutableDictionary alloc] init];
    [publicKey setObject:(__bridge id) kSecClassKey forKey:(__bridge id)kSecClass];
    [publicKey setObject:(__bridge id) kSecAttrKeyTypeRSA forKey:(__bridge id)kSecAttrKeyType];
    [publicKey setObject:d_tag forKey:(__bridge id)kSecAttrApplicationTag];
    SecItemDelete((__bridge CFDictionaryRef)publicKey);
    [publicKey setObject:data forKey:(__bridge id)kSecValueData];
    [publicKey setObject:(__bridge id) kSecAttrKeyClassPublic forKey:(__bridge id)
     kSecAttrKeyClass];
    [publicKey setObject:[NSNumber numberWithBool:YES] forKey:(__bridge id)
     kSecReturnPersistentRef];
    CFTypeRef persistKey = nil;
    OSStatus status = SecItemAdd((__bridge CFDictionaryRef)publicKey, &persistKey);
    if (persistKey != nil){
        CFRelease(persistKey);
    }
    if ((status != noErr) && (status != errSecDuplicateItem)) {
        return nil;
    }
    [publicKey removeObjectForKey:(__bridge id)kSecValueData];
    [publicKey removeObjectForKey:(__bridge id)kSecReturnPersistentRef];
    [publicKey setObject:[NSNumber numberWithBool:YES] forKey:(__bridge id)kSecReturnRef];
    [publicKey setObject:(__bridge id) kSecAttrKeyTypeRSA forKey:(__bridge id)kSecAttrKeyType];
    SecKeyRef keyRef = nil;
    status = SecItemCopyMatching((__bridge CFDictionaryRef)publicKey, (CFTypeRef *)&keyRef);
    if(status != noErr){
        return nil;
    }
    return keyRef;
}
+(NSData*)getPublicKeyData:(NSString*)appID{
    if (publicKeyData==nil) {
        NSString *publicKeyFilePaths=[[[MiscellaneousMethods getAppBundle] bundlePath] stringByAppendingPathComponent:[NSString stringWithFormat:@"/Assets/apps/%@/rsakey",appID]];
        NSFileManager *fileManager=[NSFileManager defaultManager];
        NSArray *publicKeyFile=[fileManager contentsOfDirectoryAtPath:publicKeyFilePaths error:nil];
        if ([publicKeyFile count]==1) {
            NSString *publicKeyFileCompletePath=[publicKeyFilePaths stringByAppendingPathComponent:publicKeyFile[0]];
            NSString *fileExtension=[publicKeyFileCompletePath pathExtension];
            if ([fileExtension isEqualToString:@"pem"]) {
                NSData *plublicKeyData=[fileManager contentsAtPath:publicKeyFileCompletePath];
                NSString *key = [[NSString alloc] initWithData:plublicKeyData encoding:NSUTF8StringEncoding];
                NSRange spos = [key rangeOfString:@"-----BEGIN PUBLIC KEY-----"];
                NSRange epos = [key rangeOfString:@"-----END PUBLIC KEY-----"];
                if(spos.location != NSNotFound && epos.location != NSNotFound){
                    NSUInteger s = spos.location + spos.length;
                    NSUInteger e = epos.location;
                    NSRange range = NSMakeRange(s, e-s);
                    key = [key substringWithRange:range];
                }
                key = [key stringByReplacingOccurrencesOfString:@"\r" withString:@""];
                key = [key stringByReplacingOccurrencesOfString:@"\n" withString:@""];
                key = [key stringByReplacingOccurrencesOfString:@"\t" withString:@""];
                key = [key stringByReplacingOccurrencesOfString:@" "  withString:@""];
                publicKeyData=base64_decode(key);
            }else if([fileExtension isEqualToString:@"der"]){
                publicKeyData=[NSData dataWithContentsOfFile:publicKeyFileCompletePath];
            }
        }
    }
    return publicKeyData;
}

+ (NSData *)encryptData:(NSData *)data withKeyRef:(SecKeyRef) keyRef isSign:(BOOL)isSign {
    const uint8_t *srcbuf = (const uint8_t *)[data bytes];
    size_t srclen = (size_t)data.length;
    size_t block_size = SecKeyGetBlockSize(keyRef) * sizeof(uint8_t);
    void *outbuf = malloc(block_size);
    NSMutableData *ret = [[NSMutableData alloc] init];
    if (outbuf) {
        size_t src_block_size = block_size - 11;
        for(int idx=0; idx<srclen; idx+=src_block_size){
            size_t data_len = srclen - idx;
            if(data_len > src_block_size){
                data_len = src_block_size;
            }
            size_t outlen = block_size;
            OSStatus status = noErr;
            if (isSign) {
                status = SecKeyRawSign(keyRef,
                                       kSecPaddingOAEP,
                                       srcbuf + idx,
                                       data_len,
                                       outbuf,
                                       &outlen
                                       );
            } else {
                status = SecKeyEncrypt(keyRef,
                                       kSecPaddingOAEP,
                                       srcbuf + idx,
                                       data_len,
                                       outbuf,
                                       &outlen
                                       );
            }
            if (status != 0) {
                ret = nil;
                break;
            }else{
                [ret appendBytes:outbuf length:outlen];
            }
        }
        free(outbuf);
        CFRelease(keyRef);
    }
    return ret;
}
+ (NSString *)RSAEncrypt:(id)dataToEncrypt :(NSString*)appId{
    NSString *stringValue;
    if ([dataToEncrypt isKindOfClass:[NSMutableDictionary class]]||[dataToEncrypt isKindOfClass:[NSDictionary class]]) {
        stringValue= [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dataToEncrypt options:0 error:nil] encoding:NSUTF8StringEncoding];
    }else{
        stringValue=dataToEncrypt;
    }
    NSData *data = [self encryptData:[stringValue dataUsingEncoding:NSUTF8StringEncoding] :appId];
    NSString *ret = base64_encode_data(data);
    return ret;
}

+ (NSData *)encryptData:(NSData *)data :(NSString*)appId{
    //    if(!data || !pubKey){
    //        return nil;
    //    }
    if(!data){
        return nil;
    }
    SecKeyRef keyRef = [self addPublicKey :appId];
    if(!keyRef){
        return nil;
    }
    return [self encryptData:data withKeyRef:keyRef isSign:NO];
}

+ (NSData *)decryptData:(NSData *)data withKeyRef:(SecKeyRef) keyRef{
    const uint8_t *srcbuf = (const uint8_t *)[data bytes];
    size_t srclen = (size_t)data.length;
    size_t block_size = SecKeyGetBlockSize(keyRef) * sizeof(uint8_t);
    NSMutableData *ret = [[NSMutableData alloc] init];
    UInt8 *outbuf = malloc(block_size);
    if (outbuf) {
        size_t src_block_size = block_size;
        for(int idx=0; idx<srclen; idx+=src_block_size){
            size_t data_len = srclen - idx;
            if(data_len > src_block_size){
                data_len = src_block_size;
            }
            size_t outlen = block_size;
            OSStatus status = noErr;
            status = SecKeyDecrypt(keyRef,
                                   kSecPaddingOAEP,
                                   srcbuf + idx,
                                   data_len,
                                   outbuf,
                                   &outlen
                                   );
            if (status != 0) {
                ret = nil;
                break;
            }else{
                int idxFirstZero = -1;
                int idxNextZero = (int)outlen;
                for ( int i = 0; i < outlen; i++ ) {
                    if ( outbuf[i] == 0 ) {
                        if ( idxFirstZero < 0 ) {
                            idxFirstZero = i;
                        } else {
                            idxNextZero = i;
                            break;
                        }
                    }
                }
                [ret appendBytes:&outbuf[idxFirstZero+1] length:idxNextZero-idxFirstZero-1];
            }
        }
        free(outbuf);
        CFRelease(keyRef);
    }
    return ret;
}


+ (NSString *)RSADecrypt:(id)dataToEncrypt :(NSString*)appId{
    NSString *stringValue;
    if ([dataToEncrypt isKindOfClass:[NSMutableDictionary class]]||[dataToEncrypt isKindOfClass:[NSDictionary class]]) {
        stringValue= [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dataToEncrypt options:0 error:nil] encoding:NSUTF8StringEncoding];
    }else{
        stringValue=dataToEncrypt;
    }
    NSData *data = [[NSData alloc] initWithBase64EncodedString:stringValue options:NSDataBase64DecodingIgnoreUnknownCharacters];
    data = [self decryptData:data :appId];
    NSString *ret = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    return ret;
}

+ (NSData *)decryptData:(NSData *)data :(NSString*)appId{
    //    if(!data || !pubKey){
    //        return nil;
    //    }
    if(!data){
        return nil;
    }
    SecKeyRef keyRef = [self addPublicKey:appId];
    if(!keyRef){
        return nil;
    }
    return [self decryptData:data withKeyRef:keyRef];
}
static NSString *base64_encode_data(NSData *data){
    data = [data base64EncodedDataWithOptions:0];
    NSString *ret = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    return ret;
}

static NSData *base64_decode(NSString *str){
    NSData *data = [[NSData alloc] initWithBase64EncodedString:str options:NSDataBase64DecodingIgnoreUnknownCharacters];
    return data;
}

//+(id)encryptPlistData:(id)dictionary{
//    NSMutableDictionary *dict = [[NSMutableDictionary alloc]init];
//    NSString *encryptPlistData = [KeychainUtility getDataFromKeychain:PLISTENCRYPTKEY];
//    NSArray *allKeys = [dictionary allKeys];
//    for(NSString *key in allKeys){
//        NSString *encryptedValue;
//        NSString *value = [dictionary valueForKey:key];
//        if([key isEqualToString:@"serverToken"]){
//            encryptedValue = value;
//        }else{
//            encryptedValue  = [CryptoUtility getAESEncryptedString:[NSString stringWithFormat:@"%@",value] :encryptPlistData];
//        }
//        [dict setObject:encryptedValue forKey:key];
//    }
//    NSDictionary *encryptedDict = dict;
//    return encryptedDict;
//}
//
//+(id)decryptPlistData:(id)dictionary{
//    NSMutableDictionary *dict = [[NSMutableDictionary alloc]init];
//    NSString *decryptPlistData = [KeychainUtility getDataFromKeychain:PLISTENCRYPTKEY];
//    NSArray *allKeys = [dictionary allKeys];
//    for(NSString *key in allKeys){
//        NSString *decryptedValue;
//        NSString *value = [dictionary valueForKey:key];
//        if([key isEqualToString:@"serverToken"]){
//            decryptedValue = value;
//        }else{
//            decryptedValue = [CryptoUtility getAESDecryptedString:[NSString stringWithFormat:@"%@",value] :decryptPlistData];
//        }
//        [dict setObject:decryptedValue forKey:key];
//    }
//    NSDictionary *decryptedDict = dict;
//    return decryptedDict;
//}
+(NSMutableDictionary *)encryptPlistData:(NSMutableDictionary *)dictionary{
    dictionary = [dictionary mutableCopy];
    NSString *encryptPlistData = [KeychainUtility getDataFromKeychain:@"PLISTENCRYPTKEY"];
    NSArray *allKeys = [dictionary allKeys];
    for(NSString *key in allKeys){
        if ([[dictionary valueForKey:key] isKindOfClass:[NSString class]]) {
            NSString *encryptedValue;
            NSString *value = [dictionary valueForKey:key];
            if([key isEqualToString:@"serverToken"] || [key isEqualToString:@"serverUrl"]){
                encryptedValue = value;
            }else{
                encryptedValue  = [CryptoUtility getAESEncryptedString:[NSString stringWithFormat:@"%@",value] :encryptPlistData];
            }
            [dictionary setObject:encryptedValue forKey:key];
        }else if ([[dictionary valueForKey:key] isKindOfClass:[NSDictionary class]]){
            NSMutableDictionary *masterDict = [[dictionary valueForKey:key] mutableCopy];
            NSArray *allKeys = [masterDict allKeys];
            for(NSString *key in allKeys){
                if ([[masterDict valueForKey:key] isKindOfClass:[NSString class]]) {
                    NSString *encryptedValue;
                    NSString *value = [masterDict valueForKey:key];
                    encryptedValue  = [CryptoUtility getAESEncryptedString:[NSString stringWithFormat:@"%@",value] :encryptPlistData];
                    [masterDict setObject:encryptedValue forKey:key];
                }else if ([[masterDict valueForKey:key] isKindOfClass:[NSDictionary class]]){
                    NSDictionary *dict =  [self encryptPlistData:[masterDict valueForKey:key]];
                    [masterDict setObject:dict forKey:key];
                }
            }
            [dictionary setObject:masterDict forKey:key];
        }else{
            NSString *encryptedValue;
            NSString *value = [dictionary valueForKey:key];
            encryptedValue  = [CryptoUtility getAESEncryptedString:[NSString stringWithFormat:@"%@",value] :encryptPlistData];
            [dictionary setObject:encryptedValue forKey:key];
        }
    }
    return dictionary;
}

+(NSMutableDictionary *)decryptPlistData:(NSMutableDictionary *)dictionary{
    dictionary = [dictionary mutableCopy];
    NSString *decryptPlistData = [KeychainUtility getDataFromKeychain:@"PLISTENCRYPTKEY"];
    NSArray *allKeys = [dictionary allKeys];
    for(NSString *key in allKeys){
        if ([[dictionary valueForKey:key] isKindOfClass:[NSString class]]) {
            NSString *decryptedValue;
            NSString *value = [dictionary valueForKey:key];
            if([key isEqualToString:@"serverToken"] || [key isEqualToString:@"serverUrl"]){
                decryptedValue = value;
            }else{
                decryptedValue  = [CryptoUtility getAESDecryptedString:[NSString stringWithFormat:@"%@",value] :decryptPlistData];
            }
            [dictionary setObject:decryptedValue forKey:key];
        }else if ([[dictionary valueForKey:key] isKindOfClass:[NSDictionary class]]){
            NSMutableDictionary *masterDict = [[dictionary valueForKey:key] mutableCopy];
            NSArray *allKeys = [masterDict allKeys];
            for(NSString *key in allKeys){
                if ([[masterDict valueForKey:key] isKindOfClass:[NSString class]]){
                    NSString *decryptedValue;
                    NSString *value = [masterDict valueForKey:key];
                    decryptedValue  = [CryptoUtility getAESDecryptedString:[NSString stringWithFormat:@"%@",value] :decryptPlistData];
                    [masterDict setObject:decryptedValue forKey:key];
                }else if ([[masterDict valueForKey:key] isKindOfClass:[NSDictionary class]]){
                    NSDictionary *dict =  [self decryptPlistData:[masterDict valueForKey:key]];
                    [masterDict setObject:dict forKey:key];
                }
            }
            [dictionary setObject:masterDict forKey:key];
        }else{
            NSString *decryptedValue;
            NSString *value = [dictionary valueForKey:key];
            decryptedValue  = [CryptoUtility getAESDecryptedString:[NSString stringWithFormat:@"%@",value] :decryptPlistData];
            [dictionary setObject:decryptedValue forKey:key];
        }
    }
    return dictionary;
}
+(NSString *)encryptSingleValue:(NSString *)value{
    NSString *encryptValue = [KeychainUtility getDataFromKeychain:PLISTENCRYPTKEY];
    NSString *encryptedValue = [CryptoUtility getAESEncryptedString:value :encryptValue];
    return encryptedValue;
}

+(NSString *)decryptSingleValue:(NSString *)key{
    NSString *decryptValue = [KeychainUtility getDataFromKeychain:PLISTENCRYPTKEY];
    NSString *decryptedValue = [CryptoUtility getAESDecryptedString:key :decryptValue];
    return decryptedValue;
}

@end


