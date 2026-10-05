//
//  AppzillonViewController.h
//  Azuritei-tab
//
//  Created by Apple on 13/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "sqlite3.h"
#import <UIKit/UIPrintInteractionController.h>
#import <MessageUI/MessageUI.h>
#import <MobileCoreServices/MobileCoreServices.h>
#import <CoreLocation/CoreLocation.h>
#include <objc/message.h>
#import "Constants.h"
#import <CommonCrypto/CommonDigest.h>
#import "APZPlugin.h"
#import <WebKit/WebKit.h>
#import <UserNotifications/UserNotifications.h>

@interface AppzillonViewController : UIViewController <UIPrintInteractionControllerDelegate, UITextFieldDelegate,AZPluginDoneDelegate,UNUserNotificationCenterDelegate>{
    NSString *uniqueID;
    NSString *userID;
    NSString *UUID;
    UIPrintInteractionController *printController;
    NSString *appString;
    NSString *ipAddress;
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
@property (strong,nonatomic) NSMutableDictionary *webViewDictionary;
@property (strong,nonatomic) NSMutableArray *webViewPropDictionary;
@property(assign)BOOL isAppPausedMonitored;
@property(assign)BOOL isAppResumeMonitored;
@property(assign)BOOL isAppCallStartMonitored;
@property(assign)BOOL isAppCallEndMonitored;
@property(strong,nonatomic)NSMutableArray *logArray;
@property(assign,nonatomic)NSUInteger logIndex;
@property(nonatomic,strong)NSDictionary *appPropertyDictionary;
@property(nonatomic,strong)NSString* appPropertiesPath;
@property (nonatomic, strong)NSMutableDictionary *runTimeDict;
@property (nonatomic, strong)NSString *runTimeDictPath;
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

//New request
@property(nonatomic,strong)CLLocation *locationForNewRequest;
@property(nonatomic,strong)WKWebView *webView;
@property(nonatomic,strong)UIImageView *imageViewInBackground;
@property(nonatomic,strong)NSDictionary* keyBoardDictionary;
//Webview
@property(nonatomic,assign) IBOutlet UINavigationItem *navItem;
@property(nonatomic,assign) NSURL *url;
@property(nonatomic,assign) NSString *urlString;
@property(nonatomic,assign) NSString *pageName;
@property(nonatomic,strong)NSString *forceOrientation;
@property(nonatomic,strong)NSDictionary* NotificationDictionary;
@property(nonatomic,strong)NSDictionary *shortcutItemDictionary;
@property(nonatomic,strong)NSDictionary* orientationDictionary;
@property(assign)BOOL isForceOrientation;
@property(assign)BOOL deviceRegistrationCalled;
@property(nonatomic,strong)CLLocationManager *locationManager;
@property(nonatomic,assign)BOOL isFirstAppLaunch;
@property(nonatomic,strong)UIWindowScene *scenes;
@property(nonatomic,strong)NSDictionary *languageDataDictionary;
@property (nonatomic, assign) BOOL trustAllCetificates;
@property(nonatomic,strong)NSDictionary *universalLinkDictionary;

-(NSString *)createResponseJSONString:(NSString*)pluginId :(BOOL)status :(BOOL)keepAlive :(NSArray*)responseKeys :(NSArray*)responseValues ;
-(void)sendJS:(NSString *)message;
-(NSDictionary *)parseJSON:(NSString *)pluginCallString;
@end

