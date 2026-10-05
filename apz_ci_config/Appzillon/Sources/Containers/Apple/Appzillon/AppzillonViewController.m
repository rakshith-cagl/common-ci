//
//  AppzillonViewController.m
//  Azuritei-tab
//
//  Created by Apple on 13/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//
#import "AppzillonViewController.h"
#import "AppzillonViewController+ServerCalls.h"
#import <APPZILLONPRODUCTNAME-Swift.h>
#define UIColorFromRGB(rgbValue) [UIColor \
colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 \
green:((float)((rgbValue & 0xFF00) >> 8))/255.0 \
blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface AppzillonViewController ()<UIScrollViewDelegate>

@property(assign)BOOL isReIntializeEvent;
@property(nonatomic,strong)NSString *containerApp;
@property(nonatomic,strong)NSString *containerMenu;
@property(assign)BOOL OTACalled;
@property(assign)NSInteger totalApp;
@end

@implementation AppzillonViewController
@synthesize uniqueID;
@synthesize appString;
@synthesize ipAddress;
@synthesize splashViewP;
@synthesize splashViewL;
@synthesize isSplashScreenLaunched;
@synthesize splashScreenManual;
@synthesize splashScreenAuto;
@synthesize splashcount;
@synthesize isSplashLaunchedBefore;
@synthesize sandboxPath;
@synthesize currentVersion;
@synthesize tokenStored;
@synthesize appWiped;
@synthesize longPress;
@synthesize pinch;
@synthesize leftSwipe;
@synthesize rightSwipe;
@synthesize upSwipe;
@synthesize downSwipe;
@synthesize singleTap;
@synthesize doubleTap;
@synthesize tripleTap;
@synthesize swipeEnabled;
@synthesize STCalleeGest;
@synthesize DTCalleeGest;
@synthesize TTCalleeGest;
@synthesize LPCalleeGest;
@synthesize SWCalleeGest;
@synthesize PNCalleeGest;
@synthesize inputGestJSON;
@synthesize calleeGest;
@synthesize idleTimer;
@synthesize inputTimerJSON;
@synthesize calledFromPlugin;
@synthesize appIdleMaxTime;
@synthesize gestCalleeTimer;
@synthesize timerStop;
@synthesize plugin;
@synthesize webViewDictionary;
@synthesize webViewPropDictionary;
@synthesize isAppPausedMonitored;
@synthesize isAppResumeMonitored;
@synthesize isAppCallEndMonitored;
@synthesize isAppCallStartMonitored;
@synthesize deviceGroup;
@synthesize logArray;
@synthesize logIndex;
@synthesize appPropertyDictionary;
@synthesize controlEventRequest;
@synthesize OTAStatus;
@synthesize activePluginList;
@synthesize isAppExpired;
@synthesize APZSafeToken;
@synthesize APZServerNonce;
@synthesize APZSessionToken;
@synthesize APZServerToken;
@synthesize APZServerURL;
@synthesize isFirstPageLaunched;
@synthesize isServerNonceReceived;
@synthesize webView;
@synthesize navItem;
@synthesize url;
@synthesize urlString;
@synthesize pageName;
@synthesize deviceRegistrationCalled;
@synthesize locationManager;
@synthesize  universalLinkDictionary;
#pragma mark - View lifecycle
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.rotationPluginFlag=YES;
        sandboxPath = [[FileManagerUtility documentDirectory]path];
    }
    NSUInteger version = [[[UIDevice currentDevice] systemVersion]integerValue];
    if(version <= 5 && ([self.appOrientation isEqual:@"LANDSCAPE"])){
        [self setStatusBarOrientation];
    }
    return self;
}

-(void)scrollViewWillBeginZooming:(UIScrollView *)scrollView withView:(UIView *)view{
    scrollView.pinchGestureRecognizer.enabled = FALSE;
}

-(void)callGetServerNonce{
    [self getServerNonce];
}

#pragma mark - JSON PARSING
-(NSDictionary *)parseJSON:(NSString *)pluginCallString{
    NSString* jsonStr = [pluginCallString stringByRemovingPercentEncoding];
    NSError *error=nil;
    NSDictionary * jsonDictionary=[NSJSONSerialization JSONObjectWithData:[jsonStr  dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:&error];
    if (error==nil) {
        return jsonDictionary;
    }else{
        return nil;
    }
}

-(NSDictionary *)parsePluginJSON:(NSString *)jsonStr{
    jsonStr = [jsonStr stringByRemovingPercentEncoding];
    NSError *error=nil;
    NSDictionary * jsonDictionary=[NSJSONSerialization JSONObjectWithData:[jsonStr  dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:&error];
    if (error==nil) {
        return jsonDictionary;
    }else{
        return nil;
    }
}

#pragma mark - JS CALLBACK FUNCTION
-(void)sendJS:(NSString *)message{
    @synchronized(self){
        [webView evaluateJavaScript:message completionHandler:nil];
    }
}

#pragma mark - Plugin Done
-(void)donePlugin:(id)pluginName{
    self.plugin = nil;
    [activePluginList removeObjectForKey:NSStringFromClass([pluginName class])];
}
-(void)donePluginWithOrientaion:(id)plugin :(UIInterfaceOrientation)interfaceOrientation{
    self.plugin = nil;
    [activePluginList removeObjectForKey:NSStringFromClass([plugin class])];
    if ([self.appOrientation isEqualToString:@"ANY"]) {
        [self setOrientationAfterSubview:interfaceOrientation];
    }
}

#pragma mark - Memory Dealloc
-(void)dealloc{
    self.logArray = nil;
    self.webViewDictionary = nil;
    self.webViewPropDictionary = nil;
    self.plugin = nil;
    
}

@end
