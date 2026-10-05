//
//  AppzillonViewController.m
//  Azuritei-tab
//
//  Created by Apple on 13/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//
#import "AppzillonViewController.h"
#import "AppzillonViewController+ServerCalls.h"
#import "AppzillonViewController+SplashImage.h"
#import "AppzillonViewController+Gesture_Timer.h"
#import "AppzillonViewController+PluginCalls.h"
#import "AppzillonViewController+ViewMethods.h"
#import "AppzillonViewController+EventMonitor.h"
#import "AppzillonViewController+Settings.h"
#import "CallServer.h"
#import "APZJsNotificationHandler.h"
#import "AppzillonLogin.h"
#import "CryptoUtility.h"
#import "AFNetworking/AFNetworking.h"
#import "MiscellaneousMethods.h"

#define UIColorFromRGB(rgbValue) [UIColor \
colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 \
green:((float)((rgbValue & 0xFF00) >> 8))/255.0 \
blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

//@implementation NSURLRequest(DataController)
//+ (BOOL)allowsAnyHTTPSCertificateForHost:(NSString *)host
//{
//    return trustAllCetificates;
//}
//@end

@interface AppzillonViewController ()<CLLocationManagerDelegate,UIScrollViewDelegate,CXCallObserverDelegate>
//@property (nonatomic,strong) UIActivityIndicatorView *m_activity;
//@property(assign,nonatomic)UIInterfaceOrientation prevOrientation;
//@property(nonatomic,strong)APZSensor *acclerometer;
//@property(nonatomic,strong)APZSensor *compass;
//@property(nonatomic,strong)APZSensor *gps;
//@property(nonatomic,strong)APZSensor *geoFencing;
//@property(nonatomic,strong)APZSensor *iBeacon;
//@property(nonatomic,strong)APZSensor *batteryMonitor;
//@property(nonatomic,strong)APZMedia *audio;
@property(assign)BOOL isForceOrientation;
@property(nonatomic,strong)NSString *forceOrientation;
@property(nonatomic,assign)BOOL isFirstAppLaunch;

@property(assign)BOOL isReIntializeEvent;
@property(nonatomic,strong)NSString *containerApp;
@property(nonatomic,strong)NSString *containerMenu;
@property(assign)BOOL OTACalled;
@property(assign)NSInteger totalApp;
@property(nonatomic,strong)UIImageView *imageViewInBackground;
@property(nonatomic,strong)CLLocationManager *locationManager;
@property(assign)BOOL deviceRegistrationCalled;
@property(nonatomic,strong)NSString *SMSPluginId;
@property(nonatomic,strong)NSDictionary* keyBoardDictionary;
@property(nonatomic,strong)NSDictionary* NotificationDictionary;
@property(nonatomic,strong)NSDictionary* orientationDictionary;
@property(nonatomic,strong)APZJsNotificationHandler *apzJsNotificationHandler;
@property(nonatomic,strong)NativeService *nativeService;
@end

@implementation AppzillonViewController
//@synthesize m_activity;
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
@synthesize pluginReload;
@synthesize webViewDictionary;
@synthesize webViewPropDictionary;
@synthesize isBatteryMonitored;
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

#pragma mark - View lifecycle
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.rotationPluginFlag=YES;
        sandboxPath=[self documentDirectory];
    }
    NSUInteger version = [[[UIDevice currentDevice] systemVersion]integerValue];
    if(version <= 5 && ([self.appOrientation isEqual:@"LANDSCAPE"])){
        [self setStatusBarOrientation];
    }
    return self;
}

- (void)viewDidLoad {
    
    [super viewDidLoad];
    [self setupWebview];
    self.isFirstAppLaunch = YES;
    self.isAppExpired=NO;
//    shared = (AppzillonAppDelegate *)[[UIApplication sharedApplication] delegate];
    //The below changes are for Appzillon ios Framework
    shared = (ApzApp *)[ApzApp sharedManager];
    [self.navigationController setNavigationBarHidden:YES animated:YES];
    
    [self getDeviceMapping];
    self.splashcount=0;
    self.splashScreenAuto=YES;
    [self showSplashScreen];
    if ([[[UIDevice currentDevice] systemVersion]integerValue]>=8) {
        if(![self.appOrientation isEqualToString:@"ANY"]){
            NSNumber *value;
            if  (self.interfaceOrientation==UIInterfaceOrientationLandscapeLeft)
            {
                value= [NSNumber numberWithInt:UIDeviceOrientationLandscapeRight];
            }else if (self.interfaceOrientation==UIInterfaceOrientationLandscapeRight)
            {
                value= [NSNumber numberWithInt:UIDeviceOrientationLandscapeLeft];
            }else {
                value= [NSNumber numberWithInt:self.interfaceOrientation];
            }
            [[UIDevice currentDevice] setValue:value forKey:@"orientation"];
        }
        [[UIApplication sharedApplication] setStatusBarOrientation:self.interfaceOrientation                                   animated:YES];
    }
    uniqueID = [self getUUID];
    ipAddress =[self getIPAddressCall];
    ////
    self.runTimeDictPath=[[NSString alloc] initWithString:[sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/Container.plist",appString]];
    [self loadAppProperties];
    [self appExpiryforOTA:appString];
    if([[self.appPropertyDictionary objectForKey:@"trustAllCertificates"] isEqualToString:@"Y"]){
        trustAllCetificates=YES;
    }
    self.isServerNonceReceived = @"N";
    if ([appPropertyDictionary objectForKey:@"serverUrl"] != nil && ![[appPropertyDictionary objectForKey:@"serverUrl"] isEqualToString:@""]){
            APZServerURL = [CryptoUtility getAESDecryptedString:[self.appPropertyDictionary objectForKey:@"serverUrl"] :SERVERTOKENDECRYPTKEY];
    //        NSLog(@"serverUrl = %@", APZServerURL);
        }
    NSString *serverEnabled=[appPropertyDictionary objectForKey:@"enableServer"];
    bool mockServer=[[appPropertyDictionary objectForKey:@"enableMockServer"] boolValue];
    if ([serverEnabled isEqualToString:@"Y"]&&(mockServer==false)) {
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(callGetServerNonce) name:UIApplicationWillEnterForegroundNotification object:nil];
        [self getServerNonce];
    }else{
        //        self.isFirstPageLaunched = YES;
        [self injectHtmlInWebview];
    }
    self.logArray = [@[] mutableCopy];
    self.logIndex = 0;
    [self initEventMonitoring];
    [self initMultiviewPlugin];
    [self disableBounce:nil];
    self.isForceOrientation = NO;
    //    m_activity.hidden= TRUE;
    
    //keyboard handle in webview
    //    web.keyboardDisplayRequiresUserAction=false;
    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
    [center addObserver:self selector:@selector(handleKeyboardWillShowAtContainer:)
                   name:UIKeyboardWillShowNotification object:nil];
    [center addObserver:self selector:@selector(handleKeyboardWillHideAtContainer:)
                   name:UIKeyboardWillHideNotification object:nil];
    
    //For Framework Extensions JS notifications
    self.apzJsNotificationHandler = [[APZJsNotificationHandler alloc] init];
    [self.apzJsNotificationHandler registerForCallBackNotification:webView];
}

- (void)handleKeyboardWillShowAtContainer:(NSNotification *)paramNotification{
    if (@available(iOS 11.0, *)) {
      webView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentAutomatic;
    }
}

- (void)handleKeyboardWillHideAtContainer:(NSNotification *)paramNotification{
    if (@available(iOS 11.0, *)) {
      webView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    }
}

-(void)setupWebview{
    WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
    config.preferences = [[WKPreferences alloc] init];
    config.preferences.minimumFontSize = 10;
    config.preferences.javaScriptEnabled = YES;
    config.preferences.javaScriptCanOpenWindowsAutomatically = NO;
    
    webView = [[WKWebView alloc] initWithFrame:self.view.frame configuration:config];
    webView.UIDelegate = self;
    webView.navigationDelegate = self;
    webView.scrollView.delegate = self;
    webView.allowsBackForwardNavigationGestures = YES;
    if (@available(iOS 11.0, *)) {
      webView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    }
    [webView.configuration.userContentController addScriptMessageHandler:self name:@"JavaScriptObserver"];
    [webView.configuration.preferences setValue:@YES forKey:@"allowFileAccessFromFileURLs"];
    webView.allowsBackForwardNavigationGestures=YES;
    [self.view addSubview:webView];
}

-(void)scrollViewWillBeginZooming:(UIScrollView *)scrollView withView:(UIView *)view{
    scrollView.pinchGestureRecognizer.enabled = FALSE;
}

-(void)callGetServerNonce{
    [self getServerNonce];
}
-(void)loadAppProperties{
    self.appPropertiesPath = [[NSString alloc] initWithString:[sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",appString]];
    NSString *settingsPath;
    //    BOOL isFirstLaunch = [[NSUserDefaults standardUserDefaults] boolForKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",appString]];
    if (![MiscellaneousMethods getAppHasLaunchedBefore:appString]) {
        if ([[shared.containerPropsDictionary objectForKey:@"OTAREQUIRED"]isEqualToString:@"Y"]) {
            settingsPath = [[NSString alloc] initWithFormat:@"/Assets/apps/%@/screens/config/appprops.json",appString];
            settingsPath=[sandboxPath stringByAppendingPathComponent:settingsPath];
        }else{
            settingsPath=[[MiscellaneousMethods getAppBundle] pathForResource:@"appprops" ofType:@"json" inDirectory:[NSString stringWithFormat:@"/Assets/apps/%@/screens/config",appString]];
        }
        if([[NSFileManager defaultManager] fileExistsAtPath:settingsPath]){
            NSString *fileContent = [[NSString alloc] initWithContentsOfFile:settingsPath encoding:NSUTF8StringEncoding error:nil];
            NSError *error=nil;
            NSDictionary * jsonDictionary=[NSJSONSerialization JSONObjectWithData:[fileContent  dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:&error];
            if (error==nil) {
                NSLog(@"---Reterived JSON---");
            }else{
                NSLog(@"--Unable to Write to AppProperties File");
            }
            NSDictionary *encryptedDict = [CryptoUtility encryptPlistData:jsonDictionary];
            [encryptedDict writeToFile:self.appPropertiesPath atomically:YES];
            
            //             [[NSUserDefaults standardUserDefaults] setBool:YES forKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",appString]];
            [MiscellaneousMethods setAppHasLaunchedBefore:appString];
            
        }
    }
    if ([MiscellaneousMethods getAppHasLaunchedOnOlderVersion]) {
        [MiscellaneousMethods setAppHasLaunchedBefore:appString];
    }
    
    NSDictionary *appPropDict = [[NSMutableDictionary alloc] initWithContentsOfFile:self.appPropertiesPath];
    self.appPropertyDictionary = (NSMutableDictionary *)[CryptoUtility decryptPlistData:appPropDict];
}

-(void)locationPermission{
    _locationManager= [[CLLocationManager alloc] init];
    _locationManager.delegate=self;
    NSArray *versionArray = [[[UIDevice currentDevice] systemVersion] componentsSeparatedByString:@"."];
    if ([[versionArray objectAtIndex:0] intValue] >= 8) {
        [_locationManager requestWhenInUseAuthorization];
        [_locationManager startUpdatingLocation];
    }else{
        [_locationManager startUpdatingLocation];
    }
    
}
-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
}

- (void)viewWillLayoutSubviews {
    [super viewWillLayoutSubviews];
    //count check is added to avoid resizing while in multiview plugins
    if ([self.webViewDictionary count]==1) {
        [webView setFrame:CGRectMake(webView.frame.origin.x, webView.frame.origin.y,webView.frame.size.width ,self.view.frame.size.height - webView.frame.origin.y)];
    }
}

- (void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    if(self.isFirstAppLaunch == YES){
        self.splashcount++;
        [self.splashViewL removeFromSuperview];
        [self.splashViewP removeFromSuperview];
        [self showSplashScreen];
        [self initSize];
        self.isFirstAppLaunch = NO;
    }
}

-(void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
}

- (void)viewDidUnload {
    [super viewDidUnload];
}

-(void) willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    if(self.rotationPluginFlag == YES){
        if ([self.appOrientation isEqualToString:@"ANY"]) {
            if (self.orientationDictionary) {
                [self sendNotification:toInterfaceOrientation];
            }
            [self updateMultiView:toInterfaceOrientation];
        }
        if (self.splashcount !=0) {
            if((self.splashScreenManual==YES)||(self.splashScreenAuto==YES)){
                [self showSplashScreenWithRotation];
            }
        }
    }
    else{
        NSLog(@"--Ignoring Orientation---");
    }
}
- (void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator
{
    [coordinator animateAlongsideTransition:^(id<UIViewControllerTransitionCoordinatorContext> context)
     {
        UIInterfaceOrientation toInterfaceOrientation = [[UIApplication sharedApplication] statusBarOrientation];
        if(self.rotationPluginFlag == YES){
            if ([self.appOrientation isEqualToString:@"ANY"]) {
                if (self.orientationDictionary) {
                    [self sendNotification:toInterfaceOrientation];
                }
                [self updateMultiView:toInterfaceOrientation];
            }
            if (self.splashcount !=0) {
                if((self.splashScreenManual==YES)||(self.splashScreenAuto==YES)){
                    [self showSplashScreenWithRotation];
                }
            }
        }
        else{
            NSLog(@"--Ignoring Orientation---");
        }
    } completion:^(id<UIViewControllerTransitionCoordinatorContext> context)
     {
        
    }];
    [super viewWillTransitionToSize:size withTransitionCoordinator:coordinator];
}


-(void)sendNotification:(UIInterfaceOrientation)toOrientation{
    NSString *orientation = @"";
    UIInterfaceOrientation deviceOrientation = toOrientation;
    BOOL isCurOrientationPortrait =(deviceOrientation == UIDeviceOrientationPortrait || deviceOrientation == UIDeviceOrientationPortraitUpsideDown);
    BOOL isCurOrientationLandscape =(deviceOrientation == UIDeviceOrientationLandscapeLeft || deviceOrientation == UIDeviceOrientationLandscapeRight);
    BOOL isCallbackRequired = (isCurOrientationPortrait || isCurOrientationLandscape);
    if(isCallbackRequired){
        if(deviceOrientation == UIDeviceOrientationPortrait || deviceOrientation == UIDeviceOrientationPortraitUpsideDown)
        {
            orientation = EVENT_ORIENTATION_PORTRAIT;
        }
        else if(deviceOrientation == UIDeviceOrientationLandscapeLeft || deviceOrientation  == UIDeviceOrientationLandscapeRight)
        {
            orientation = EVENT_ORIENTATION_LANDSCAPE;
        }
        //        self.prevOrientation = deviceOrientation;
        NSArray * resultkeys=[NSArray arrayWithObjects:ORIENTATION_KEY,CBEVENT,nil];
        NSArray * resultMsg=[NSArray arrayWithObjects:orientation,@"orientation_change",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.orientationDictionary objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
    }
    else{
        NSLog(@"--Notification callback not required--");
    }
    //    self.prevOrientation = deviceOrientation;
}

#pragma mark rotation
- (BOOL)shouldAutorotate {
    return self.rotationPluginFlag;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
    if([self.appOrientation isEqualToString:@"PORTRAIT"]){
        return (UIInterfaceOrientationMaskPortrait | UIInterfaceOrientationMaskPortraitUpsideDown);
    }
    else if([self.appOrientation isEqualToString:@"LANDSCAPE"]){
        return UIInterfaceOrientationMaskLandscape;
    }
    else{
        return UIInterfaceOrientationMaskAll;
    }
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    if([self.appOrientation isEqualToString:@"PORTRAIT"]){
        return UIInterfaceOrientationIsPortrait(interfaceOrientation);
    }
    else if([self.appOrientation isEqualToString:@"LANDSCAPE"]){
        return UIInterfaceOrientationIsLandscape(interfaceOrientation);
    }
    else{
        return UIInterfaceOrientationMaskAll;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - Webview Related Function
-(void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)navigationAction decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler{
    if (!appWiped) {
        [navItem.leftBarButtonItem setEnabled:YES];
        url = [[NSURL alloc] init];
        url = [navigationAction.request URL];
        urlString = url.absoluteString;
        pageName = [url.absoluteString lastPathComponent];
        if([urlString containsString:@"command"]) {
            NSDictionary *result=[self parseJSON:pageName];
            NSString *commandString=[self getClassNameForPlugin:[result objectForKey:@"command"]];
            if ([commandString isEqualToString:@"callServerforInfra"]) {
                NSDictionary *callServerDictionary=[self parseJSON:pageName];
                NSMutableDictionary *requestJSON=[[NSMutableDictionary alloc] initWithDictionary:callServerDictionary];
                [CallServer callServerFromInfraWithRequest:requestJSON :self :webView :appString];
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"getDeviceInfo"]){
                
                //setting appSecToken once infra is ready because webViewDidFinishLoad is uncertain
                @synchronized(self){
                    [webView evaluateJavaScript:[NSString stringWithFormat:@"apz.server.setAppSecToken('%@');",self.isServerNonceReceived] completionHandler:nil];
                }

                //This code required to set main app params sent by the user while using AppzillonFramework
                @synchronized(self){
                NSString *json = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:shared.nativeAppConfig options:0 error:nil] encoding:NSUTF8StringEncoding];
                    [webView evaluateJavaScript:[NSString stringWithFormat:@"%@(%@);",@"apz.ns.setMainAppParams",json] completionHandler:nil];
                    }
                
                [self deviceInfo:result];
                return decisionHandler(WKNavigationActionPolicyCancel);
            }else if ([commandString isEqualToString:@"getServerNonce"]){
                self.serverNonceJSON=result;
                [self getServerNonce];
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"GetUserPrefs"]) {
                [self getUserPrefs:result];
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"SetUserPrefs"]) {
                if (result != nil) {
                    [self setUserPrefs:result];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }else if ([commandString  isEqualToString:@"customHTTPRequest"]){
                [CallServer nonAppzillonServerCallsWithRequest:[result mutableCopy] webView:webView :appString];
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString hasPrefix:@"upgradeRequired"]){
                if (result != nil) {
                    [self upgradeRequired:result];
                }
                else{
                    NSLog(@"--APZ json is empty--");
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString hasPrefix:@"getUpdateActionRequired"]){
                if (result != nil) {
                    [self getUpdateActionRequired:result];
                }
                else{
                    NSLog(@"--APZ json is empty--");
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else  if ([commandString isEqualToString:@"multiViewOpen"]){
                if (result != nil) {
                    [self multiviewOpen:result];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"multiviewClose"]){
                if (result!=nil) {
                    [self multiviewClose:result];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }else if ([commandString isEqualToString:@"resizeMultiview"]){
                if (result!=nil) {
                    [self resizeMultiview:result];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else  if ([commandString isEqualToString:@"lockRotation"]){
                if (result != nil ){
                    self.rotationPluginFlag=NO;
                    NSArray * resultkeys=nil;
                    NSArray * resultMsg=nil;
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else  if ([commandString isEqualToString:@"unlockRotation"]){
                if (result != nil ){
                    self.rotationPluginFlag=YES;
                    self.appOrientation=@"ANY";
                    self.forceOrientation = NULL;
                    NSArray * resultkeys=nil;
                    NSArray * resultMsg=nil;
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString  isEqualToString:@"showSplash"]){
                self.splashScreenManual=YES;
                [self showSplashScreen];
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else  if ([commandString isEqualToString:@"controlEvents"]){
                if (result != nil){
                    self.controlEventRequest=result;
                    [self controlEvents:result];
                }
                else{
                    NSLog(@"--error in parsing json--");
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString isEqualToString:@"nativeService"]){
                if (result != nil){
                    if (_nativeService==nil) {
                        _nativeService= [[NativeService alloc] init];
                    }
                    [_nativeService execute:result wbView:webView];
                }
                else{
                    NSLog(@"--error in parsing json--");
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([urlString rangeOfString:@"notes.html"].location!=NSNotFound){
                shared.appIDNotes=appString;
                if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad){
                    NotesPage *page=[[NotesPage alloc] initWithNibName:@"NotesPage" bundle:nil];
                    page.txnNo=[[[[[urlString lastPathComponent] componentsSeparatedByString:@"="]objectAtIndex:1]componentsSeparatedByString:@","]objectAtIndex:0];
                    page.readWrite=[[[urlString lastPathComponent] componentsSeparatedByString:@"="]objectAtIndex:2];
                    [page setModalPresentationStyle:UIModalPresentationFullScreen];
                    [self presentViewController:page animated:YES completion:nil];
                }
                else if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
                    NotesPage *page=[[NotesPage alloc] initWithNibName:@"NotesPage_iphone" bundle:nil];
                    page.txnNo=[[[[[urlString lastPathComponent] componentsSeparatedByString:@"="]objectAtIndex:1]componentsSeparatedByString:@","]objectAtIndex:0];
                    page.readWrite=[[[urlString lastPathComponent] componentsSeparatedByString:@"="]objectAtIndex:2];
                    [page setModalPresentationStyle:UIModalPresentationFullScreen];
                    [self presentViewController:page animated:YES completion:nil];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            //            else if ([[urlString lastPathComponent] isEqualToString:@"startloader"]) {
            //                m_activity.center = CGPointMake(self.view.bounds.size.width/2.0f, self.view.bounds.size.height/2.0f);
            //                m_activity.autoresizingMask=(UIViewAutoresizingFlexibleRightMargin|UIViewAutoresizingFlexibleLeftMargin|UIViewAutoresizingFlexibleBottomMargin|UIViewAutoresizingFlexibleTopMargin);
            //                [m_activity startAnimating];
            //                m_activity.hidden= TRUE;
            //                return NO;
            //            }
            //            else if ([[urlString lastPathComponent] isEqualToString:@"endloader"]) {
            //                [m_activity stopAnimating];
            //                m_activity.hidden= TRUE;
            //                return  NO;
            //            }
            else if([commandString isEqualToString:@"MakePhoneCall"]){
                if ([[UIDevice currentDevice].model isEqualToString:@"iPhone"]) {
                    [self handleCall:result];
                    NSString *phoneNo=[result objectForKey:@"phoneNo"];
                    [[UIApplication sharedApplication] openURL: [NSURL URLWithString:[@"telprompt://" stringByAppendingString:phoneNo]] options: @{} completionHandler:nil];
                }else{
                    
                    NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                    NSArray *returnResult=[NSArray arrayWithObjects:@"APZ-CNT-262",nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :returnResultkeys :returnResult]];
                    
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString isEqualToString:@"sendSMS"]){
                if (result != nil) {
                    NSString *phoneNo=[result objectForKey:@"phoneNo"];
                    //                    NSString *smsType=[result objectForKey:@"type"];
                    NSString *message=[result objectForKey:@"message"];
                    //                    if([smsType isEqualToString:@"BG"]){
                    //                        NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                    //                        NSArray *returnResult=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-245"],nil];
                    //                        [self jsLayerCall:JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :returnResultkeys :returnResult]];
                    //                    }
                    //                    else if (!([[UIDevice currentDevice].model isEqualToString:@"iPhone"])) {
                    if (!([[UIDevice currentDevice].model isEqualToString:@"iPhone"])) {
                        NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                        NSArray *returnResult=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-246"],nil];
                        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :returnResultkeys :returnResult]];
                    }else{
                        _SMSPluginId=[result objectForKey:PLUGINID];
                        MFMessageComposeViewController * smsMessenger=[[MFMessageComposeViewController alloc] init];
                        smsMessenger.messageComposeDelegate=self;
                        NSArray *phoneNoArray=[[NSArray alloc] initWithObjects:phoneNo, nil];
                        [smsMessenger setRecipients:phoneNoArray];
                        [smsMessenger setBody:message];
                        smsMessenger.modalPresentationStyle = UIModalPresentationFormSheet;
                        [self presentViewController:smsMessenger animated:YES completion:nil];
                    }
                }
                
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            //            else if ([commandString isEqualToString:@"generateOTP"]) {
            //                if (result != nil){
            //                    [self generateOTP:result];
            //                }
            //                else{
            //                    NSLog(@"--error in parsing GenerateOTP json--");
            //                }
            //                return NO;
            //            }
            else if ([commandString isEqualToString:@"chgPassword"]){
                if (result != nil){
                    [AppzillonLogin changePassword:result viewController:self webView:webView];
                }
                else{
                    NSLog(@"--error in parsing GenerateOTP changePassword json--");
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
                
            }
            else if ([commandString isEqualToString:@"getPref"]){
                if (result!=nil) {
                    [self getUserPreference:result];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"setPref"]){
                if (result!=nil) {
                    [self setUserPreference:result];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString isEqualToString:@"setOrientation"]){
                if (result != nil){
                    [self setOrientation:result];
                }
                else{
                    NSLog(@"--error in parsing json--");
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }else  if ([commandString isEqualToString:@"clearAppData"]) {
                [self EmptySandbox];
                NSArray *resultkeys=nil;
                NSArray *resultMess=nil;
                [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMess]];
                return decisionHandler(WKNavigationActionPolicyCancel);
            }else if([commandString isEqualToString:@"appIdleTimeOut"]){
                if (result) {
                    self.appIdleMaxTime =[[self.appPropertyDictionary objectForKey:@"idleTimeOut"] doubleValue];
                    if (self.appIdleMaxTime) {
                        calledFromPlugin=NO;
                        self.inputTimerJSON =result;
                        [self startGesture:self.inputTimerJSON webView:webView :@"Timer"];
                        [self resetIdleTimer];
                    }else{
                        NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                        NSArray *returnResult=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-132"],nil];
                        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :returnResultkeys :returnResult]];
                    }
                }
            }
            else if ([commandString isEqualToString:@"GestureSupportStart"]){
                if (result != nil){
                    self.inputGestJSON=result;
                    [self startGesture:result webView:webView :@"gesture"];
                    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:@"started",nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
                }else{
                    NSLog(@"--JSON Parsing Error--");
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"GestureSupportStop"]){
                if (result != nil){
                    [self stopGesture:result webView:webView :@"gesture"];
                    NSArray *resultkeys=nil;
                    NSArray *resultMsg=nil;
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
                }else{
                    NSLog(@"--JSON Parsing Error--");
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString isEqualToString:@"launchApp"]){
                if (result != nil) {
                    [self appExpiryforOTA:[result objectForKey:@"appId"]];
                    if (!self.isAppExpired) {
                        [self launchApp:result];
                    }
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString isEqualToString:@"remoteDebug"]){
                if (result != nil) {
                    [self remoteDebug:result];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString isEqualToString:@"appVersion"]){
                NSString *appName =[result objectForKey:@"appId"];
                if (appName != nil) {
                    [self appVersion:result];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString isEqualToString:@"getIP"])
            {
                if (result != nil) {
                    [self getIPAddress:result];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString isEqualToString:@"AGReloadCoordinates"]){
                NSString* methodName = [NSString stringWithFormat:@"reloadAGCoordinate:"];
                SEL normalSelector = NSSelectorFromString(methodName);
                self.pluginReload.delegate=self;
                if ([self.pluginReload respondsToSelector:normalSelector])
                {
                    [self.pluginReload performSelector:normalSelector withObject:result];
                    
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if([commandString isEqualToString:@"disableBounce"]){
                if (result != nil) {
                    [self disableBounce:result];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"updateWhiteList"]){
                [self selectURLWhiteListQuery];
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"KBListStart"]){
                if (result!=nil) {
                    self.keyBoardDictionary=result;
                    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
                    [center addObserver:self selector:@selector(handleKeyboardWillShow:)
                                   name:UIKeyboardWillShowNotification object:nil];
                    [center addObserver:self selector:@selector(handleKeyboardWillHide:)
                                   name:UIKeyboardWillHideNotification object:nil];
                    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:@"started",nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"KBListStop"]){
                if (result!=nil) {
                    self.keyBoardDictionary=nil;
                    [[NSNotificationCenter defaultCenter]removeObserver:self];
                    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:@"stopped",nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"notifListStart"]){
                if (result!=nil) {
                    self.NotificationDictionary=result;
                    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:@"started",nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
                    if (shared.pushMessege) {
                        [self notifCallback];
                    }
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"notifListStop"]){
                if (result!=nil) {
                    self.NotificationDictionary=nil;
                    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:@"stopped",nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }else if ([commandString isEqualToString:@"nwListStart"]){
                if (result!=nil) {
                    self.nwListenerDict=result;
                    [self startNetworkRechabilityListener];
                    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:@"started",nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"nwListStop"]){
                if (result!=nil) {
                    self.nwListenerDict=nil;
                    [self stopNetworkReachabilityListener];
                    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:@"stopped",nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString isEqualToString:@"startOrientList"]){
                if (result!=nil) {
                    self.orientationDictionary=result;
                    NSString *orientation = @"";
                    UIInterfaceOrientation deviceOrientation = self.interfaceOrientation;
                    BOOL isCurOrientationPortrait =(deviceOrientation == UIDeviceOrientationPortrait || deviceOrientation == UIDeviceOrientationPortraitUpsideDown);
                    BOOL isCurOrientationLandscape =(deviceOrientation == UIDeviceOrientationLandscapeLeft || deviceOrientation == UIDeviceOrientationLandscapeRight);
                    BOOL isCallbackRequired = (isCurOrientationPortrait || isCurOrientationLandscape);
                    if(isCallbackRequired){
                        if(deviceOrientation == UIDeviceOrientationPortrait || deviceOrientation == UIDeviceOrientationPortraitUpsideDown)
                        {
                            orientation = EVENT_ORIENTATION_PORTRAIT;
                        }
                        else if(deviceOrientation == UIDeviceOrientationLandscapeLeft || deviceOrientation  == UIDeviceOrientationLandscapeRight)
                        {
                            orientation = EVENT_ORIENTATION_LANDSCAPE;
                        }
                    }
                    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,ORIENTATION_KEY,nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:@"started",orientation,nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            } else if ([commandString isEqualToString:@"stopOrientList"]){
                if (result!=nil) {
                    self.orientationDictionary=NULL;
                    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:@"stopped",nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
                }
            }
            else if ([commandString  isEqualToString:@"hideSplashScreen"]){
                self.splashScreenManual=NO;
                [self hideSplashScreen];
                return decisionHandler(WKNavigationActionPolicyCancel);
            }else if ([commandString  isEqualToString:@"sendCallbackFromSDK"]){
                if (result!=nil) {
                    [shared sendCallbackToUser:result];
                    NSArray *resultkeys=[NSArray arrayWithObjects:@"",nil];
                    NSArray *resultMsg=[NSArray arrayWithObjects:@"",nil];
                    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else if ([commandString  isEqualToString:@"closeAppzillonSDK"]){
                if (result!=nil) {
                    [shared close];
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
            }
            else{
                if (result != nil) {
                    BOOL isExecuted = [self execute:commandString json:result wbView:webView];
                    if(isExecuted){
                        NSLog(@"--Plugin executed---");
                    }
                    else{
                        NSLog(@"--Plugin not executed---");
                    }
                }
                else{
                    NSLog(@"--APZ json is empty--");
                }
                return decisionHandler(WKNavigationActionPolicyCancel);
                
            }
        }
        else{
            if ([pageName isEqualToString:[NSString stringWithFormat:@"%@.html",appString]]) {
                //                self.runTimeDictPath=[[NSString alloc] initWithString:[sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/Container.plist",appString]];
                //                [self loadAppProperties];
                //                [self appExpiryforOTA:appString];
                //                if([[self.appPropertyDictionary objectForKey:@"trustAllCertificates"] isEqualToString:@"Y"]){
                //                    trustAllCetificates=YES;
                //                }
                //                if (!self.isAppExpired) {
                //                    if ([[self.appPropertyDictionary objectForKey:@"trackLocation"]isEqualToString:@"Y"])
                //                    {
                //                        [self locationPermission];
                //                    }
                //                    else
                //                    {
                //                        [self CallDeviceRegistration:nil];
                //                    }
                //                    BOOL isFirstLaunch = [[NSUserDefaults standardUserDefaults] boolForKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",appString]];
                //                    if (isFirstLaunch == NO)
                //                    {
                //                        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"HasLaunchedOnce"];
                //                    }
                //                    if (shared.pushMessege) {
                //                        [self notifCallback];
                //                    }
                //                    calledFromPlugin=NO;
                //
                //                }
                return decisionHandler(WKNavigationActionPolicyAllow);
            }
            else {
                
                return decisionHandler(WKNavigationActionPolicyAllow);
            }
        }
        return decisionHandler(WKNavigationActionPolicyAllow);
    }else{
        return decisionHandler(WKNavigationActionPolicyAllow);
    }
}

- (void)webView:(WKWebView *)webView runJavaScriptAlertPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(void))completionHandler
{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:message
                                                                             message:nil
                                                                      preferredStyle:UIAlertControllerStyleAlert];
    [alertController addAction:[UIAlertAction actionWithTitle:@"OK"
                                                        style:UIAlertActionStyleCancel
                                                      handler:^(UIAlertAction *action) {
        completionHandler();
    }]];
    [self presentViewController:alertController animated:YES completion:^{}];
}

#pragma mark - WKWebviewScriptMessageHandler

- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    
    // Callback from JavaScript:
    // window.webkit.messageHandlers.JavaScriptObserver.postMessage(message)
    NSString *text = message.body;
    UIAlertController *alertController = [UIAlertController
                                          alertControllerWithTitle:@"Message from JavaScript"
                                          message:text
                                          preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action){
        NSLog(@"OK");
    }];
    [alertController addAction:okAction];
    [self presentViewController:alertController animated:YES completion:nil];
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

#pragma mark  JSON CREATION
-(NSString *)createResponseJSONString:(NSString*)pluginId :(BOOL)status :(BOOL)keepAlive :(NSArray*)responseKeys :(NSArray*)responseValues {
    NSMutableDictionary *pluginResponse=[[NSMutableDictionary alloc]initWithObjects:responseValues forKeys:responseKeys];
    [pluginResponse setObject:pluginId forKey:PLUGINID];
    [pluginResponse setObject:[NSNumber numberWithInt:status] forKey:@"status"];
    [pluginResponse setObject:[NSNumber numberWithInt:keepAlive] forKey:@"keepAlive"];
    return [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:pluginResponse options:NSJSONWritingPrettyPrinted error:nil] encoding:NSUTF8StringEncoding];
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
-(void)setOrientationAfterSubview:(UIInterfaceOrientation)interfaceOrientation{
    self.isForceOrientation = YES;
    if(interfaceOrientation==UIInterfaceOrientationPortrait || interfaceOrientation==UIInterfaceOrientationPortraitUpsideDown){
        self.forceOrientation = @"Portrait";
        [self updateMultiView:interfaceOrientation];
        NSNumber *value= [NSNumber numberWithInt:interfaceOrientation];
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"orientation"];
        
        [[UIDevice currentDevice] setValue:value forKey:@"orientation"];
        [[UIApplication sharedApplication] setStatusBarOrientation:interfaceOrientation  animated:YES];
        //            self.prevOrientation = interfaceOrientation;
    }
    else if(interfaceOrientation==UIInterfaceOrientationLandscapeLeft ||interfaceOrientation==UIInterfaceOrientationLandscapeRight){
        self.forceOrientation = @"Landscape";
        [self updateMultiView:interfaceOrientation];
        NSNumber *value= [NSNumber numberWithInt:interfaceOrientation];
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"orientation"];
        [[UIDevice currentDevice] setValue:value forKey:@"orientation"];
        [[UIApplication sharedApplication] setStatusBarOrientation:UIInterfaceOrientationLandscapeLeft  animated:YES];
        //            self.prevOrientation = interfaceOrientation;
    }
    else{
        self.appOrientation=@"ANY";
        self.rotationPluginFlag = YES;
    }
}
#pragma mark - Sensor Plugin Done
//-(void)sensorDone:(id)pluginName{
//    NSString *sensorName = NSStringFromClass([pluginName class]);
//    NSLog(@"---Sensor Plugin = %@ finished",sensorName);
//    if([sensorName isEqualToString:@"APZGps"]){
//        self.gps = nil;
//    }
//    else  if([sensorName isEqualToString:@"APZAccelerometer"]){
//        self.acclerometer = nil;
//    }
//    else  if([sensorName isEqualToString:@"APZCompass"]){
//        self.compass = nil;
//    }
//    else  if([sensorName isEqualToString:@"APZGeofencing"]){
//        self.geoFencing = nil;
//    }
//    else  if([sensorName isEqualToString:@"APZiBeacon"]){
//        self.iBeacon = nil;
//    }
//    if([sensorName isEqualToString:@"APZBattery"]){
//        self.batteryMonitor = nil;
//    }
//    else{
//        NSLog(@"--Unknown Sensor--");
//    }
//}

//#pragma mark - Media Plugin Done
//-(void)mediaDone:(id)pluginName{
//    self.audio = nil;
//}


#pragma mark - SetOrientation
-(void)setOrientation:(NSDictionary *)orientation{
    NSString *userOrientation = [orientation objectForKey:@"orientation"];
    NSString *orient = @"";
    self.isForceOrientation = YES;
    self.rotationPluginFlag = YES;
    if([userOrientation isEqualToString:@"PORTRAIT"]){
        self.forceOrientation = @"Portrait";
        self.appOrientation=@"PORTRAIT";
        if (UIInterfaceOrientationIsLandscape(self.interfaceOrientation))
        {
            NSNumber *value= [NSNumber numberWithInt:UIDeviceOrientationPortrait];
            [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"orientation"];
            [[UIDevice currentDevice] setValue:value forKey:@"orientation"];
            [[UIApplication sharedApplication] setStatusBarOrientation:UIInterfaceOrientationPortrait  animated:YES];
            [self updateMultiView:UIInterfaceOrientationPortrait];
            [self sendNotification:UIInterfaceOrientationPortrait];
            orient = EVENT_ORIENTATION_PORTRAIT;
            //            self.prevOrientation = UIInterfaceOrientationPortrait;
            NSArray * resultkeys=[NSArray arrayWithObjects:ORIENTATION_KEY,nil];
            NSArray * resultMsg=[NSArray arrayWithObjects:orient,nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.orientationDictionary objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
            self.rotationPluginFlag = NO;
        }
    }
    else if([userOrientation isEqualToString:@"LANDSCAPE"]){
        if (UIInterfaceOrientationIsPortrait(self.interfaceOrientation))
            self.appOrientation=@"LANDSCAPE";
        self.forceOrientation = @"Landscape";
        {
            NSNumber *value= [NSNumber numberWithInt:UIDeviceOrientationLandscapeLeft];
            [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"orientation"];
            [[UIDevice currentDevice] setValue:value forKey:@"orientation"];
            [[UIApplication sharedApplication] setStatusBarOrientation:UIInterfaceOrientationLandscapeLeft  animated:YES];
            [self updateMultiView:UIInterfaceOrientationLandscapeLeft];
            [self sendNotification:UIInterfaceOrientationLandscapeLeft];
            orient = EVENT_ORIENTATION_LANDSCAPE;
            //            self.prevOrientation = UIInterfaceOrientationLandscapeLeft;
            NSArray * resultkeys=[NSArray arrayWithObjects:ORIENTATION_KEY,nil];
            NSArray * resultMsg=[NSArray arrayWithObjects:orient,nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.orientationDictionary objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
            self.rotationPluginFlag = NO;
        }
    }
    else{
        self.appOrientation=@"ANY";
        self.rotationPluginFlag = YES;
    }
    
}

-(void)setStatusBarOrientation{
    [[UIApplication sharedApplication] setStatusBarOrientation:UIInterfaceOrientationLandscapeLeft];
    int currentWidthL=0;
    int currentHeightL=0;
    currentHeightL=[[UIScreen mainScreen] bounds].size.width;
    currentWidthL=[[UIScreen mainScreen] bounds].size.height;
    [webView setFrame:CGRectMake(0, 0,currentWidthL ,currentHeightL)];
}

#pragma mark - SMS
- (void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result {
    NSArray *resultkeys=nil;
    NSArray *resultMsg=nil;
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:_SMSPluginId :false :true :resultkeys :resultMsg]];
    _SMSPluginId=nil;
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - KeyBoard
- (void)handleKeyboardWillShow:(NSNotification *)paramNotification{
    if (_keyBoardDictionary) {
        NSArray *returnResultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:@"show",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[_keyBoardDictionary objectForKey:PLUGINID] :true :true :returnResultkeys :returnResult]];
    }
}

- (void) handleKeyboardWillHide:(NSNotification *)paramSender{
    if (_keyBoardDictionary) {
        NSArray *returnResultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:@"hide",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[_keyBoardDictionary objectForKey:PLUGINID] :true :true :returnResultkeys :returnResult]];
    }
}


#pragma mark - Notification Storage
-(void)notifCallback{
    if (!shared.pushMessege) {
        shared.pushMessege=@"";
    } if (!shared.pushCategory){
        shared.pushCategory=@"";
    } if (!shared.pushActionType){
        shared.pushActionType=@"";
    } if (!shared.pushParams){
        shared.pushParams=@"";
    } if (!shared.pushTitle){
        shared.pushTitle=@"";
    } if (!shared.pushSubTitle){
        shared.pushSubTitle=@"";
    } if (!shared.pushImgURL){
        shared.pushImgURL=@"";
    }
    if (self.NotificationDictionary) {
        NSArray *returnResultkeys=[NSArray arrayWithObjects:@"message",@"title",@"subtitle",@"image_url",@"notification_code",@"action_code",@"params",CBEVENT,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:shared.pushMessege,shared.pushTitle,shared.pushSubTitle,shared.pushImgURL,shared.pushCategory,shared.pushActionType,shared.pushParams,@"notification",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.NotificationDictionary objectForKey:PLUGINID] :true :true :returnResultkeys :returnResult]];
        [self addNotificationToDB:shared.pushMessege];
        shared.pushMessege=nil;
        shared.notificationCenter=nil;
        shared.pushCategory=nil;
        shared.pushActionType=nil;
        shared.pushParams=nil;
        shared.pushTitle=nil;
        shared.pushSubTitle=nil;
        shared.pushImgURL=nil;
        
    }
}

-(void)addNotificationToDB:(NSString *)message{
    NSString *dbPath=[NSString stringWithFormat:@"%@/Assets/apps/%@/sqlite/APPSDB.sqlite3",sandboxPath,[shared.containerPropsDictionary objectForKey:@"MAINAPPID"]];
    sqlite3 * dbRef;
    int result=sqlite3_open([dbPath UTF8String],&dbRef);
    if (result==0) {
        sqlite3_stmt *insertToTable;
        NSDate *cDate =[NSDate date] ;
        NSDateFormatter *dFormatter=[[NSDateFormatter alloc] init];
        [dFormatter setDateFormat:@"hh:mm a"];
        NSString *query=[NSString stringWithFormat:@"insert into tb_notifications(message ,timeStamp,readFlag) values('%@','%@','N')",message,[dFormatter stringFromDate:cDate]];
        sqlite3_prepare(dbRef, [query UTF8String], -1, &insertToTable, NULL);
        if(sqlite3_step(insertToTable)==SQLITE_DONE){
            sqlite3_close(dbRef);
        }else{
            NSLog(@"----error in creating table tb_notifications");
        }
    }else{
        NSLog(@"----error in Opening notification DB %d",result);
    }
}

-(void)launchApp:(NSDictionary *)appJson{
    NSString *documentsDirectory = [[NSString alloc] initWithString:sandboxPath];
    NSString *apptoLaunch=[appJson objectForKey:@"appId"];
    if (apptoLaunch!=nil) {
        NSString *appzillonFirstPagePath = [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/apps/%@/screens/appzillonfirstpage.html",apptoLaunch]];
        BOOL appExists = [[NSFileManager defaultManager] fileExistsAtPath:appzillonFirstPagePath];
        if (appExists) {
            [webView loadRequest:[NSURLRequest requestWithURL:[NSURL fileURLWithPath:appzillonFirstPagePath]]];
        }
    }
}


-(void)appExpiryforOTA:(NSString*)appStr
{
    NSString *appDirpath= [[NSString alloc] initWithString:[sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",appStr]];
    NSMutableDictionary *dictplist =[[NSMutableDictionary alloc] initWithContentsOfFile:appDirpath];
    //    if ([[dictplist objectForKey:@"Appstatus"]isEqualToString:@"Expired"])
    if ([[CryptoUtility decryptSingleValue:[dictplist objectForKey:@"Appstatus"]]isEqualToString:@"Expired"]){
//        UIAlertController *alertView = [UIAlertController alertControllerWithTitle:nil message:@"This Application has been Expired"    preferredStyle:UIAlertControllerStyleAlert];
//        UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
//        [alertView addAction:actionCancel];
//        dispatch_async(dispatch_get_main_queue(), ^{
//            [self presentViewController:alertView animated:YES completion:nil];
//        });
        isAppExpired=YES;
    }else{
        //        NSString* dateStringFromPlist=[dictplist objectForKey:@"expiryDate"];
        NSString* dateStringFromPlist=[CryptoUtility decryptSingleValue:[dictplist objectForKey:@"expiryDate"]];
        if (dateStringFromPlist) {
            if (![dateStringFromPlist isEqualToString:@""]) {
                NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                [dateFormatter setDateFormat:@"dd/MM/yyyy"];
                [dateFormatter setLocale:[NSLocale localeWithLocaleIdentifier:@"en_US"]];
                NSDate *today = [NSDate date];
                NSString *currentDate = [dateFormatter stringFromDate:today];
                NSLog(@"%@ " , currentDate);
                currentDate = [NSString stringWithFormat:@"%@" ,currentDate ] ;
                today=[dateFormatter dateFromString:currentDate];
                NSDate *dateFromPlist;
                if (dateStringFromPlist) {
                    if (![dateStringFromPlist isEqualToString:@""]) {
                        dateFromPlist= [dateFormatter dateFromString:dateStringFromPlist];
                    }
                }
//                UIAlertController *alertView = [UIAlertController alertControllerWithTitle:nil message:@"This Application has been Expired"    preferredStyle:UIAlertControllerStyleAlert];
//                UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
//                [alertView addAction:actionCancel];
                if ([dateFromPlist compare:today] == NSOrderedAscending) {
                    isAppExpired=YES;
                    //                    [dictplist setObject:@"Expired" forKey:@"Appstatus"];
                    [dictplist setObject:[CryptoUtility encryptSingleValue:@"Expired"] forKey:@"Appstatus"];
                    [dictplist writeToFile:appDirpath atomically:YES];
//                    dispatch_async(dispatch_get_main_queue(), ^{
//                        [self presentViewController:alertView animated:YES completion:nil];
//                    });
                }
            }
        }
    }
}

-(void)appVersion:(NSDictionary *)result{
    NSString *appName=[result objectForKey:@"appId"];
    if (appName!=nil) {
        NSString *settingsDictionaryPath= [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",appName]];
        NSDictionary *settingsDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:settingsDictionaryPath];
        //        NSString *installedVersion=[settingsDictionary objectForKey:@"appVersion"];
        NSString *installedVersion = [CryptoUtility decryptSingleValue:[settingsDictionary objectForKey:@"appVersion"]];
        NSArray *returnResultkeys=[NSArray arrayWithObjects:@"appVersion",nil];
        NSArray *returnResult=[NSArray arrayWithObjects:installedVersion,nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :returnResultkeys :returnResult]];
    }
}

#pragma mark -HideScreenInPreviewMode
-(void)hideScreenInBackground{
    NSInteger yp=0;
    NSInteger yl=0;
    NSInteger currentWidth=[[UIScreen mainScreen] bounds].size.width;
    NSInteger currentHeight=[[UIScreen mainScreen] bounds].size.height;
    UIInterfaceOrientation orientation=self.interfaceOrientation;
    UIImage *imageToDraw=[[UIImage alloc]init];
    self.imageViewInBackground=[[UIImageView alloc]initWithImage:imageToDraw];
    self.imageViewInBackground.backgroundColor = [UIColor blackColor];
    if (UIInterfaceOrientationIsLandscape(orientation)) {
        [self.imageViewInBackground setFrame:CGRectMake(0, yl, currentWidth, currentHeight)];
    }else
    {
        [self.imageViewInBackground setFrame:CGRectMake(0, yp, currentWidth, currentHeight)];
    }
    [self.view addSubview:self.imageViewInBackground];
}
-(NSString*)documentDirectory{
    NSArray *paths = NSSearchPathForDirectoriesInDomains
    (NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    return documentsDirectory;
}

-(void)revealTheScreen{
    [self.imageViewInBackground removeFromSuperview];
    self.imageViewInBackground =nil;
}
#pragma mark -Location Services
- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation{
    if (!_deviceRegistrationCalled) {
        _deviceRegistrationCalled=YES;
        NSString *pathToDictionary= [[NSString alloc] initWithString:[sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",appString]];
        NSMutableDictionary *settingsDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:pathToDictionary];
        //        if ([[settingsDictionary objectForKey:@"trackLocation"]isEqualToString:@"Y"])
        if ([[CryptoUtility decryptSingleValue:[settingsDictionary objectForKey:@"trackLocation"]]isEqualToString:@"Y"]){
            [self CallDeviceRegistration:newLocation];
        }else{
            [self CallDeviceRegistration:nil];
        }
        [_locationManager stopUpdatingLocation];
        _locationManager=nil;
    }
}


- (void)locationManager:(CLLocationManager *)manager
     didUpdateLocations:(NSArray *)locations {
    CLLocation *newLocation = [locations lastObject];
    if (!_deviceRegistrationCalled) {
        _deviceRegistrationCalled=YES;
        NSString *pathToDictionary= [[NSString alloc] initWithString:[sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",appString]];
        NSMutableDictionary *settingsDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:pathToDictionary];
        //        if ([[settingsDictionary objectForKey:@"trackLocation"]isEqualToString:@"Y"])
        if ([[CryptoUtility decryptSingleValue:[settingsDictionary objectForKey:@"trackLocation"]]isEqualToString:@"Y"]){
            [self CallDeviceRegistration:newLocation];
        }else{
            [self CallDeviceRegistration:nil];
        }
        [_locationManager stopUpdatingLocation];
        _locationManager=nil;
    }
    
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    NSLog(@"----locationManager manager didFailWithError %@----",[error localizedFailureReason]);
    if (!_deviceRegistrationCalled) {
        _deviceRegistrationCalled=YES;
        [self CallDeviceRegistration:nil];
    }
    [_locationManager stopUpdatingLocation];
    _locationManager=nil;
    
}

-(void)injectHtmlInWebview{
    if (!self.isFirstPageLaunched){
        self.OTAStatus=[shared.containerPropsDictionary objectForKey:@"OTAREQUIRED"];
        if (self.isAppExpired==NO) {
            if ([[shared.containerPropsDictionary objectForKey:@"OTAREQUIRED"]isEqualToString:@"Y"]) {
                NSString *appzillonFirstPagePath = [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/%@.html",appString]];
                //            [web loadRequest:[NSURLRequest requestWithURL:[NSURL fileURLWithPath:appzillonFirstPagePath]]];
                NSURL *filePathURL = [NSURL fileURLWithPath:appzillonFirstPagePath];
                NSURL *fileDirectoryURL = [filePathURL URLByDeletingLastPathComponent];
                [webView loadFileURL:filePathURL allowingReadAccessToURL:fileDirectoryURL];
            }else{
                NSURL *filePathURL = [NSURL fileURLWithPath:[[MiscellaneousMethods getAppBundle] pathForResource:[NSString stringWithFormat:@"%@",appString] ofType:@"html"  inDirectory:@"Assets"]];
                NSURL *fileDirectoryURL = [filePathURL URLByDeletingLastPathComponent];
                [webView loadFileURL:filePathURL allowingReadAccessToURL:fileDirectoryURL];
                
            }
            
        }
        self.isFirstPageLaunched = YES;
    }
}

#pragma mark - Memory Dealloc
-(void)dealloc{
    self.logArray = nil;
    self.webViewDictionary = nil;
    self.webViewPropDictionary = nil;
    self.plugin = nil;
    //    self.acclerometer = nil;
    //    self.compass = nil;
    //    self.gps = nil;
    //    self.audio = nil;
    //    self.geoFencing=nil;
}

@end




