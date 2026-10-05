//
//  CallServer.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 09/11/17.
//

#import <Foundation/Foundation.h>
#import "AppzillonViewController.h"

@interface CallServer : NSObject
+(void)callServerWithRequest :(NSMutableDictionary*)requestDict :(NSString *)serverIP :(NSString*)appID :(AppzillonViewController*)APZViewController completionHandler:(void(^)(BOOL status, NSDictionary *responseDictionary)) completionHandler;
+(void)callServerForUploadWithRequest :(NSMutableDictionary*)requestDict :(NSString*)appID :(NSString*)serverIP :(NSString*)path :(NSString*)fileName :(NSString*)mimeType :(AppzillonViewController*)APZViewController completionHandler:(void(^)(BOOL status, NSDictionary *responseDictionary)) completionHandler;
+(void)callServerFromInfraWithRequest :(NSMutableDictionary*)requestJSON :(AppzillonViewController*)viewController :(WKWebView*)webView :(NSString*)appID;
@end
