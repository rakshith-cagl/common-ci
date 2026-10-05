//
//  APZBioAuthentication.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 06/10/14.
//
//

#import "APZBioAuthentication.h"
#import <LocalAuthentication/LocalAuthentication.h>
#import "Constants.h"
#import "APZJsonUtil.h"
#import "Logger.h"
@interface APZBioAuthentication()
@property(nonatomic,weak)WKWebView *webView;
@property(strong,nonatomic)LAContext *context;
@property (strong, nonatomic) NSString *pluginId;
@end

@implementation APZBioAuthentication
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    [Logger logger_Log:@"I" :@"APZBioAuthentication--Execute"];
    self.context=[[LAContext alloc] init];
    NSError *error = nil;
    if ([self.context canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:&error]) {
        [self.context evaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics
                     localizedReason:@"Are you the device owner?"
                               reply:^(BOOL success, NSError *error) {
            if (error) {
                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:BIOAUTH_ERROR,nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :true :resultkeys :resultMsg]];
                
                [Logger logger_Log:@"E" :@"APZBioAuthentication--Error in Varifying Identity"];
                return;
            }
            if (success) {
                NSArray * resultkeys=nil;
                NSArray *resultMsg=nil;
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                
                [Logger logger_Log:@"I" :@"APZBioAuthentication--Authentication Successfull"];
            } else {
                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:BIOAUTH_ERROR,nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                [Logger logger_Log:@"E" :@"APZBioAuthentication--Authentication Failed"];
            }
        }];
    } else {
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:BIOAUTH_NOT_ENROLLED,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZBioAuthentication--Device Does Not Support Bio-Metrics Authentication"];
    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"I" :@"APZBioAuthentication--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end

