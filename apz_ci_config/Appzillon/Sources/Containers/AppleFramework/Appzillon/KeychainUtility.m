//
//  KeychainUtility.m
//  Appzillon
//
//  Created by Aman Gupta on 03/08/18.
//

#import "KeychainUtility.h"
#import "CryptoUtility.h"

@implementation KeychainUtility


+(void)storeDataInKeychain:(NSString*)key value:(NSString*)value{
    NSString * base64Encoding = [CryptoUtility getAESEncryptedString:value :key];
    NSDictionary* dict = [NSDictionary dictionaryWithObjectsAndKeys:(__bridge id)(kSecClassInternetPassword),  kSecClass, key, kSecAttrServer, kCFBooleanTrue, kSecReturnAttributes, nil];
    OSStatus err = SecItemDelete((__bridge CFDictionaryRef) dict);
    NSData* passwordData = [base64Encoding dataUsingEncoding:NSUTF8StringEncoding];
    dict = [NSDictionary dictionaryWithObjectsAndKeys:(__bridge id)(kSecClassInternetPassword), (__bridge id)kSecClass, key, (__bridge id)kSecAttrServer, passwordData, (__bridge id)kSecValueData, (__bridge id)kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly, (__bridge id)kSecAttrAccessible, nil];
    err = SecItemAdd((__bridge CFDictionaryRef) dict, NULL);
    if(err){
        NSLog(@"error occured");
    }
}

+(NSString *) getDataFromKeychain:(NSString *)key{
    NSDictionary* dict = [NSDictionary dictionaryWithObjectsAndKeys:(__bridge id)(kSecClassInternetPassword),  kSecClass, key, kSecAttrServer, kCFBooleanTrue, kSecReturnAttributes, kCFBooleanTrue, kSecReturnData, nil];
    NSDictionary* found = nil;
    CFDictionaryRef foundCF;
    OSStatus err = SecItemCopyMatching((__bridge CFDictionaryRef) dict, (CFTypeRef*)&foundCF);
    NSString* pass;
    if(err == 0){
        found = (__bridge NSDictionary*)(foundCF);
        if (!found) return nil;
        pass = [[NSString alloc] initWithData:[found objectForKey:(__bridge id)(kSecValueData)] encoding:NSUTF8StringEncoding];
        NSString *decryptbase64String = [CryptoUtility getAESDecryptedString:pass :key];
        return  decryptbase64String;
    }
    return  pass;
}

+(NSString*)generateSecureRandomString{
    NSMutableData *data = [NSMutableData dataWithLength:24];
    int result = SecRandomCopyBytes(NULL, 24, data.mutableBytes);
    NSAssert(result == 0, @"Error generating random bytes: %d", errno);
    NSString *base64EncodedData = [data base64EncodedStringWithOptions:0];
    return base64EncodedData;
}

@end
