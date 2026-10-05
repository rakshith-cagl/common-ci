//
//  APZOpenFile.m
//  Appzillon
//
//  Created by Admin on 09/10/13.
//
//

#import "APZBrowser.h"
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "googleViewController.h"
#import "Logger.h"


@interface APZBrowser()< UINavigationControllerDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)UINavigationController *navigationController;
@property(strong,nonatomic)NSString*browserURL;
@property(strong,nonatomic)NSString* pluginId;

@end

@implementation APZBrowser

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
        
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZBrowser--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    self.browserURL=[jsonDict objectForKey:@"url"];
    [self openBrowser];
}

-(void)openBrowser {
    NSURL*broUrl = [NSURL URLWithString:self.browserURL];
    if(broUrl && broUrl.scheme && broUrl.host){
        [[UIApplication sharedApplication] openURL:broUrl options: @{} completionHandler:^(BOOL completed){
            if(completed){
                NSArray *resultkeys=nil;
                NSArray *result=nil;
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                [Logger logger_Log:@"I" :@"APZBrowser--URL Open Success"];
                [self cleanPlugin];
            }
            else{
                NSArray *resultkeys=nil;
                NSArray *result=nil;
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                [Logger logger_Log:@"I" :@"APZBrowser--URL Open Failed"];
                [self cleanPlugin];
            }
        }];
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:INVALID_URL,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZBrowser--URL Open Failure"];
        [self cleanPlugin];
    }
    
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZBrowser--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end


