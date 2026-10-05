//
//  CallServer.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 09/11/17.
//

#import <Foundation/Foundation.h>
#import "AppzillonViewController.h"

@interface CallServer : NSObject
+(void)callServerWithRequest :(NSMutableDictionary*)requestDict :(NSString *)serverIP :(NSString*)successCB :(NSString*)failureCB :(id)className :(NSString*)appID :(AppzillonViewController*)APZViewController;
+(void)callServerForUploadWithRequest :(NSMutableDictionary*)requestDict :(NSString*)successCB :(NSString*)failureCB :(id)className :(NSString*)appID :(NSString*)serverIP :(NSString*)path :(NSString*)fileName :(NSString*)mimeType :(AppzillonViewController*)APZViewController;
+(void)callServerFromInfraWithRequest :(NSMutableDictionary*)requestJSON :(AppzillonViewController*)viewController :(WKWebView*)webView :(NSString*)appID;
+(void)nonAppzillonServerCallsWithRequest:(NSMutableDictionary *)requestDictionary webView:(WKWebView*)webView :(NSString *)appID;
+(NSString*)getCurrnetDateTime;
@end
