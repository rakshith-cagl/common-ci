//
//  APZSignaturePad.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 09/10/14.
//
//

#import "APZSignaturePad.h"
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "APZSignatureViewController.h"
#import "Logger.h"
@interface APZSignaturePad()<UINavigationControllerDelegate,singatureViewDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,strong)WKWebView *webView;
@property(strong,nonatomic)APZSignatureViewController *singnaturePadView;
@property(strong,nonatomic)NSString *pluginId;
@end

@implementation APZSignaturePad


-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZSignaturePad--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if (jsonDict) {
        self.singnaturePadView = [[APZSignatureViewController alloc]init];
        self.singnaturePadView.view.backgroundColor = [UIColor grayColor];
        [self.singnaturePadView.view.window makeKeyAndVisible];
        self.singnaturePadView.delegate = self;
        UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:self.singnaturePadView];
        [navigationController setModalPresentationStyle:UIModalPresentationFullScreen];
        [self.viewController presentViewController:navigationController animated:NO completion:nil];
    }
}

#pragma mark -SignatureCallback
-(void)callback:(NSString *)base64String :(UIInterfaceOrientation)interfaceOrientation{
    NSArray *resultkeys=[NSArray arrayWithObjects:CAMERA_JSON_PATH,CAMERA_JSON_ENCODEDIMAGE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@""],[NSString stringWithFormat:@"%@",base64String],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZSignaturePad--Done"];
    [self cleanPlugin :interfaceOrientation];
}

-(void)cancelSignaturePad :(UIInterfaceOrientation)interfaceOrientation{
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZSignaturePad--Cancelled"];
    [self cleanPlugin :interfaceOrientation];
}

#pragma mark -Plugin Clean
-(void)cleanPlugin :(UIInterfaceOrientation)interfaceOrientation{
    [Logger logger_Log:@"D" :@"APZSignaturePad--Done"];
    self.webView = nil;
    [self.delegate donePluginWithOrientaion:self :interfaceOrientation];
}


@end

