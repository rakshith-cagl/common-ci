//
//  CyptoUtility.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 05/02/18.
//

#import <Foundation/Foundation.h>

@interface CryptoUtility : NSObject
///////////AES Encryption//////////
+ (NSString *)RSAEncrypt:(id)dataToEncrypt :(NSString*)appId;
+ (NSString *)RSADecrypt:(id)dataToEncrypt :(NSString*)appId;

@end

