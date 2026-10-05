//
//  AppzillonAppDelegate.h
//  Azuritei-tab
//
//  Created by Apple on 13/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import <UserNotifications/UserNotifications.h>
@class AppzillonViewController;

@interface AppzillonAppDelegate : UIResponder <UIApplicationDelegate,UNUserNotificationCenterDelegate> {
    NSMutableDictionary *containerPropsDictionary;
    NSDate *date;
    BOOL isiPad;
    NSString *regComplete;
    NSString * regID;
    BOOL isAppActive;
    BOOL isAppExpired;
    NSString *appPath;
    UIBackgroundTaskIdentifier bgTask;
    NSString *appIDNotes;
    NSString *notificationCenter;
}
@property (nonatomic, strong) NSString *regComplete;
@property (nonatomic, strong) NSString *regID;
@property (nonatomic, assign) BOOL isAppActive;
@property (nonatomic, assign)BOOL isAppExpired;
@property (nonatomic, strong) NSMutableDictionary *containerPropsDictionary;
@property (strong, nonatomic) AppzillonViewController *viewController;
@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) NSDate *date;
@property BOOL isiPad;
@property(nonatomic,assign)UIBackgroundTaskIdentifier bgTask;
@property(nonatomic,strong)NSString *pushTitle;
@property(nonatomic,strong)NSString *pushSubTitle;
@property(nonatomic,strong)NSString *pushMessege;
@property(nonatomic,strong)NSString *pushImgURL;
@property(nonatomic,strong)NSString *pushCategory;
@property(nonatomic,strong)NSString *pushParams;
@property(nonatomic,strong)NSString *pushActionType;
@property(nonatomic,strong)NSString *shortcutID;
@property(nonatomic,strong)NSString *shortcutTitle;
@property(nonatomic,strong)NSString *shortcutSubTitle;
@property (nonatomic, strong) NSString *appPath;
@property (nonatomic, strong) NSMutableDictionary *OTAFileDict;
@property (nonatomic, strong)  NSString *OTAFileDictPath;
@property(nonatomic,strong)NSString *appIDNotes;
@property(nonatomic,strong) NSString *notificationCenter;
@end

