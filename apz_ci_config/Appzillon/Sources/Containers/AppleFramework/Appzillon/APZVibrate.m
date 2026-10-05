//
//  APZVibrate.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 07/10/14.
//
//

#import "APZVibrate.h"
#import <AudioToolbox/AudioToolbox.h>
#import "Constants.h"
#import "APZJsonUtil.h"
#import "Logger.h"

@interface APZVibrate()
@property(nonatomic,weak)WKWebView *webView;
@property (readwrite)CFURLRef soundFileURLRef;
@property (readonly)SystemSoundID soundFileObject;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZVibrate
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"I" :@"APZVibrate--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    NSString *deviceType = [UIDevice currentDevice].model;
    if([deviceType isEqualToString:@"iPhone"]){
        AudioServicesPlayAlertSound(1005);
        [Logger logger_Log:@"I" :@"APZVibrate--Vibrated"];
    }else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:VIBRATE_DEVICE_NOT_SUPPORT,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZVibrate--device not supported"];
    }
    [self cleanPlugin];
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"E" :@"APZVibrate--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end

