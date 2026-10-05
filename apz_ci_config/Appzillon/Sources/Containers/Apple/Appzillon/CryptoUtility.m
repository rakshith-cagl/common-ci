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
#import "Constants.h"
#import <APPZILLONPRODUCTNAME-Swift.h>


@implementation CryptoUtility

static NSData *publicKeyData;

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
        NSString *publicKeyFilePaths=[[[MiscellaneousMethod.shared getAppBundle] bundlePath] stringByAppendingPathComponent:[NSString stringWithFormat:@"/Assets/apps/%@/rsakey",appID]];
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


@end
