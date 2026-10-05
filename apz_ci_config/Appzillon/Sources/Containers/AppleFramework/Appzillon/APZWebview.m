//
//  APZWebview.m
//  Appzillon
//
//  Created by Victor on 12/12/15.
//
//

#import "APZWebview.h"
#import "AppzillonViewController.h"
#import "WebViewController.h"
#import "Constants.h"
#import "googleViewController.h"
#import "Logger.h"
#import "APZJsonUtil.h"

@interface APZWebview ()<UINavigationControllerDelegate,apzWebviewDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)UINavigationController *navigationController;
@property(strong,nonatomic)WebViewController *webViewController;
@property(strong,nonatomic)  NSString *urlStr;
@property(strong,nonatomic) NSString*pluginId;
@property(strong,nonatomic) NSString*cancelButtonRequired;
@property(strong,nonatomic)  id successResponseForUrl;
@property(strong,nonatomic)  NSDictionary *postData;
@end
@implementation APZWebview

@synthesize webView;

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
        
    }
    return self;
}

-(NSString*)documentDirectory{
    NSArray *paths = NSSearchPathForDirectoriesInDomains
    (NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    return documentsDirectory;
}
-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZWebView--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    self.urlStr = [jsonDict objectForKey:@"URL"];
    self.postData = [jsonDict objectForKey:@"postData"];
    self.successResponseForUrl = [jsonDict objectForKey:@"trackURL"];
    self.cancelButtonRequired = [jsonDict objectForKey:@"cancelButton"];
    [self callWebView];
}

-(void)stopPlugin:(NSDictionary *)jsonDict{
    self.pluginId = [jsonDict objectForKey:PLUGINID];
    [self closeWebView];
}
#pragma Google SignIn

-(void)callWebView {
    self.webViewController=[[WebViewController alloc]init];
    self.webViewController.urlString=self.urlStr;
    self.webViewController.pluginId=self.pluginId;
    self.webViewController.mainWeb=self.webView;
    self.webViewController.delegate=self;
    self.webViewController.successResponseForUrl=self.successResponseForUrl;
    self.webViewController.cancelButtonRequired = self.cancelButtonRequired;
    if(self.postData != nil){
        self.webViewController.postData = self.postData;
    }
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:self.webViewController];
    [navigationController setModalPresentationStyle:UIModalPresentationFullScreen];
    [self.viewController presentViewController:navigationController animated:YES completion:nil];
}

-(void)closeWebView{
    if (self.webViewController != nil) {
        [self.webViewController dismissViewControllerAnimated:true completion:^{
            self.webViewController = nil;
            NSArray *resultkeys = nil;
            NSArray *result = nil;
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
            [self cleanPlugin];
        }];
    }
    else{
        NSArray *resultkeys=nil;
        NSArray *result=nil;
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    }
}

-(void)viewCancelled:(UIInterfaceOrientation)interfaceOrientation{
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZWebView--Cancelled"];
    [self cleanPlugin:interfaceOrientation];
}



#pragma mark -Plugin Clean
-(void)cleanPlugin:(UIInterfaceOrientation)interfaceOrienation{
    self.webView = nil;
    self.webViewController.mainWeb=nil;
    self.webViewController=nil;
    [self.delegate donePluginWithOrientaion:self :interfaceOrienation];
    [Logger logger_Log:@"I" :@"APZWebView--Done"];
}

-(void)cleanPlugin{
    self.webView = nil;
    self.viewController = nil;
    self.webViewController.mainWeb = nil;
    self.webViewController = nil;
    [Logger logger_Log:@"I" :@"APZWebView--Done"];
    [self.delegate donePlugin:self];
}


@end





