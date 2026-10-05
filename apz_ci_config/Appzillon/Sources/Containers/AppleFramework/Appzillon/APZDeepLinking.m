//
//  APZWhatsapp.m
//  Appzillon
//
//  Created by victor on 09/10/16.
//
//

#import "APZDeepLinking.h"
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"


@interface APZDeepLinking()< UINavigationControllerDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)UINavigationController *navigationController;
@property(strong,nonatomic)NSString*packageScheme;
@property(strong,nonatomic)NSString *appStoreLink;
@property(strong,nonatomic)NSString *pluginId;

@end

@implementation APZDeepLinking

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZDeepLinking--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    self.packageScheme = [jsonDict objectForKey:@"packageName"];
    self.appStoreLink = [jsonDict objectForKey:@"appStoreLink"];
    if(![self.packageScheme isEqualToString:@""]){
        [self navigateToApp];
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",ERROR_CODE],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    }
}

-(void)navigateToApp {
    
    NSString *customURL = [NSString stringWithFormat:@"%@://",self.packageScheme];
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:customURL]]){
        NSURL *myurl=[[NSURL alloc] initWithString:customURL];
        [[UIApplication sharedApplication] openURL:myurl options: @{} completionHandler:nil];
        
        NSArray *resultkeys=[NSArray arrayWithObjects: @"Package Scheme",nil];
        NSArray *result=[NSArray arrayWithObjects:self.packageScheme,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"Navigated To app"];
        
    }
    else{
        NSURL *appStoreURL = [NSURL URLWithString:self.appStoreLink];
        [[UIApplication sharedApplication] openURL:appStoreURL options: @{} completionHandler:nil];
        
        NSArray *resultkeys=[NSArray arrayWithObjects: WEBVIEW_URL,nil];
        NSArray *result=[NSArray arrayWithObjects:self.packageScheme,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"Navigated To AppStore"];
        
    }
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end

