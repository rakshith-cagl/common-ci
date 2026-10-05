//
//  HashUtility.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 03/02/18.
//

#import <Foundation/Foundation.h>

@interface HashUtility : NSObject
+(NSString *) genHash:(NSString *) pin : (NSString * ) salt;
+(NSString *) genHashforJSON:(NSString *) pin : (NSString * ) salt;
+(NSString *) genHex:(NSString *) pusername : (NSString * ) ppin :(NSString *) puid : (NSString * ) pdtstr;
+(NSString *)getHashStringFromData :(NSData *)data;
@end
