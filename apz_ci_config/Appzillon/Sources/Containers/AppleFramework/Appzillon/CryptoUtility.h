//
//  CyptoUtility.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 05/02/18.
//

#import <Foundation/Foundation.h>

@interface CryptoUtility : NSObject
//+(NSMutableString *)checkKeyLength:(NSString *)keyStr;
//+(NSData *)AESKeyForPassword:(NSString *)passwordKey;
//+(NSString *) reverseString:(NSString *)string;
+(NSString*)getAESEncryptedStringForServerCalls:(id)dataToEncrypt :(NSString*)keyStr;
+(NSString*)getAESDecryptedStringForServerCalls:(id)dataToEncrypt :(NSString*)keyStr;
+(NSString*)getAESEncryptedString:(id)dataToEncrypt :(NSString*)keyStr;
//+(NSString *) reverseStringDecrypt:(NSString *)string;
//+(NSMutableString *)checkKeyLengthDecrypt:(NSString *)keyStr;
//+(NSData *)AESKeyForPasswordDecrypt:(NSString *)passwordKey;
+(NSString*)getAESDecryptedString:(id)dataToEncrypt :(NSString*)keyStr;
///////////AES Encryption//////////
+ (NSString *)RSAEncrypt:(id)dataToEncrypt :(NSString*)appId;
+ (NSString *)RSADecrypt:(id)dataToEncrypt :(NSString*)appId;
+(id)encryptPlistData:(id)dictionary;
+(id)decryptPlistData:(id)dictionary;
+(NSString *)encryptSingleValue:(NSString *)value;
+(NSString *)decryptSingleValue:(NSString *)key;
@end
