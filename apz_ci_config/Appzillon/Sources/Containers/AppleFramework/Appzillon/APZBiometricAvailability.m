//
//  APZBiometricAvailability.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 24/01/18.
//

#import "APZBiometricAvailability.h"
#import "AppzillonViewController.h"
#import <LocalAuthentication/LocalAuthentication.h>
#import "APZJsonUtil.h"
#import "Constants.h"

@interface APZBiometricAvailability()
@property(nonatomic,strong)WKWebView *webView;
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(strong,nonatomic)LAContext *context;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZBiometricAvailability

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    NSString *availableBiometrics=@"none";
    NSError *error;
    self.context=[[LAContext alloc] init];
    if ([self.context canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:&error]) {
        if (@available(iOS 11.0, *)) {
            LABiometryType biometricType=self.context.biometryType;
            if (biometricType==LABiometryTypeTouchID) {
                availableBiometrics=@"TOUCHID";
            }else if(biometricType==LABiometryTypeFaceID){
                availableBiometrics=@"FACEID";
            }
        }
        else{
            availableBiometrics=@"TOUCHID";
        }
    }
    else{
        if ([error.localizedDescription isEqualToString:@"Biometry is not available on this device."]) {
            availableBiometrics=@"NOTSUPPORTED";
        }else{
            availableBiometrics=@"NOTCONFIGURED";
        }
    }
    NSArray *returnResultkeys=[NSArray arrayWithObjects:@"biometricStatus",nil];
    NSArray *returnResult=[NSArray arrayWithObjects:availableBiometrics,nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :returnResultkeys :returnResult]];
    
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"E" :@"APZBiometricAvailability--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end


