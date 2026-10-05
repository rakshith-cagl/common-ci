//
//  AppzillonViewController.h
//  Azuritei-tab
//
//  Created by Apple on 13/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ApzApp.h"
#import <QuartzCore/QuartzCore.h>
#import "sqlite3.h"
#import <UIKit/UIPrintInteractionController.h>
#import <MessageUI/MessageUI.h>
#import <MobileCoreServices/MobileCoreServices.h>
#include <sys/socket.h>
#include <sys/sysctl.h>
#include <net/if.h>
#include <net/if_dl.h>
#import <CoreLocation/CoreLocation.h>
#import "AFNetworking.h"
#include <objc/message.h>
#import "Base64.h"
#import "Constants.h"
#import <CommonCrypto/CommonDigest.h>
#import "NotesPage.h"
#import "Reachability.h"
#import "NativeService.h"
#import "Logger.h"
#import <ifaddrs.h>
#import <arpa/inet.h>
#import "APZPlugin.h"
#import <CallKit/CallKit.h>
#import <WebKit/WebKit.h>
#import <UserNotifications/UserNotifications.h>

@interface AppzillonViewController : UIViewController <UIPrintInteractionControllerDelegate, UIGestureRecognizerDelegate,MFMessageComposeViewControllerDelegate, UITextFieldDelegate,AZPluginDoneDelegate,UNUserNotificationCenterDelegate, WKNavigationDelegate, WKUIDelegate, WKScriptMessageHandler>{
    ApzApp *shared;
    NSString *uniqueID;
    NSString *userID;
    NSString *UUID;
    NSURL *url;
    NSString *urlString;
    NSString *pageName;
    IBOutlet UINavigationItem *navItem;
    UIPrintInteractionController *printController;
    NSString *appString;
    NSString *ipAddress;
    //    UIAlertController *networkAlert;
    NSString * APZServerNonce;
    NSString * APZSafeToken;
    NSString * APZSessionToken;
    NSString * APZServerToken;
    NSString * APZServerURL;
    
    //WKWebView
    WKWebView *webView;
}

@property(nonatomic,strong)NSString *sandboxPath;
@property(nonatomic,strong) NSString *uniqueID;
@property(nonatomic,strong) NSString *appString;
@property(nonatomic,strong) NSString *ipAddress;
@property(nonatomic,strong)NSString *appOrientation;
@property(nonatomic,strong)NSString *deviceGroup;
@property(nonatomic,strong)UIImageView *splashViewP;
@property(nonatomic,strong)UIImageView *splashViewL;
@property(assign)BOOL isSplashScreenLaunched;
@property(assign)BOOL  splashScreenManual;
@property(assign)BOOL splashScreenAuto;
@property(assign)NSInteger splashcount;
@property(assign)BOOL isSplashLaunchedBefore;
@property(nonatomic,strong)NSString *currentVersion;
@property(nonatomic,strong)NSString *tokenStored;
@property(assign)BOOL appWiped;
@property(nonatomic, strong)UILongPressGestureRecognizer *longPress;
@property(nonatomic, strong)UIPinchGestureRecognizer *pinch;
@property(nonatomic, strong)UISwipeGestureRecognizer *leftSwipe;
@property(nonatomic, strong)UISwipeGestureRecognizer *rightSwipe;
@property(nonatomic, strong)UISwipeGestureRecognizer *upSwipe;
@property(nonatomic, strong)UISwipeGestureRecognizer *downSwipe;
@property(nonatomic, strong)UITapGestureRecognizer *singleTap;
@property(nonatomic, strong)UITapGestureRecognizer *doubleTap;
@property(nonatomic, strong)UITapGestureRecognizer *tripleTap;
@property(assign)BOOL swipeEnabled;
@property(assign)BOOL STCalleeGest;
@property(assign)BOOL DTCalleeGest;
@property(assign)BOOL TTCalleeGest;
@property(assign)BOOL LPCalleeGest;
@property(assign)BOOL SWCalleeGest;
@property(assign)BOOL PNCalleeGest;
@property(assign)BOOL calleeGest;
@property(nonatomic,strong)NSDictionary *inputGestJSON;
@property(nonatomic,strong)NSDictionary *inputTimerJSON;
@property(assign)NSTimer *idleTimer;
@property(assign)BOOL calledFromPlugin;
@property(assign)NSTimeInterval appIdleMaxTime;
@property(assign)BOOL gestCalleeTimer;
@property(assign)BOOL timerStop;
@property(nonatomic,strong)APZPlugin *plugin;
@property(nonatomic,strong)APZPlugin *pluginReload;
@property (strong,nonatomic) NSMutableDictionary *webViewDictionary;
@property (strong,nonatomic) NSMutableArray *webViewPropDictionary;
@property(assign)BOOL isBatteryMonitored;
@property(assign)BOOL isAppPausedMonitored;
@property(assign)BOOL isAppResumeMonitored;
@property(assign)BOOL isAppCallStartMonitored;
@property(assign)BOOL isAppCallEndMonitored;
@property(strong,nonatomic)NSMutableArray *logArray;
@property(assign,nonatomic)NSUInteger logIndex;
@property(nonatomic,strong)NSMutableDictionary *appPropertyDictionary;
@property(nonatomic,strong)NSString* appPropertiesPath;
@property (nonatomic, strong)NSMutableDictionary *runTimeDict;
@property (nonatomic, strong)NSString *runTimeDictPath;
@property (nonatomic, strong)CXCallObserver *callObserver;
@property (nonatomic, strong)NSString* callPluginId;
@property(nonatomic,strong)NSString *OTAStatus;
@property(nonatomic,strong)NSMutableArray *pluginCallArray;
@property(nonatomic,strong)NSDictionary *controlEventRequest;
@property(nonatomic,strong)NSDictionary *serverNonceJSON ;
@property(nonatomic,strong)NSMutableDictionary *activePluginList;
@property(nonatomic,assign)BOOL isAppExpired;
@property(nonatomic,strong)NSString *APZServerNonce;
@property(nonatomic,strong)NSString *APZSafeToken;
@property(nonatomic,strong)NSString *APZSessionToken;
@property(nonatomic,strong)NSString *APZServerToken;
@property(nonatomic,strong)NSString *APZServerURL;
@property (assign) BOOL  rotationPluginFlag;
@property(nonatomic,strong)NSDictionary *nwListenerDict;
////Offline Support
@property(assign)BOOL isFirstPageLaunched;
@property(nonatomic,strong)NSString *isServerNonceReceived;
///Multiview Resize
@property(nonatomic,strong)WKWebView *tempMultiview;
@property(nonatomic,assign)NSInteger multiViewID;
@property(nonatomic,strong)NSString *multiViewName;
@property(nonatomic,strong) NSString *targetView;
@property(nonatomic,strong)NSString *multiviewLocation;


-(NSString *)createResponseJSONString:(NSString*)pluginId :(BOOL)status :(BOOL)keepAlive :(NSArray*)responseKeys :(NSArray*)responseValues ;
-(void)sendJS:(NSString *)message;
-(void)setOrientationAfterSubview:(UIInterfaceOrientation)interfaceOrientation;
-(void)locationPermission;
-(void)notifCallback;
-(void)injectHtmlInWebview;
@end

static BOOL trustAllCetificates;

