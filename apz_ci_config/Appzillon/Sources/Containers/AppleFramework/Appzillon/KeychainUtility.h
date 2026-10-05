//
//  KeychainUtility.h
//  Appzillon
//
//  Created by Aman Gupta on 03/08/18.
//

#import <Foundation/Foundation.h>

@interface KeychainUtility : NSObject

+(void)storeDataInKeychain:(NSString*)key value:(NSString*)value;
+(NSString *) getDataFromKeychain:(NSString *)key;
+(NSString*)generateSecureRandomString;
@end
