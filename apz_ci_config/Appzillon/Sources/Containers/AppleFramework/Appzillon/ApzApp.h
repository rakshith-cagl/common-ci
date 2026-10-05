//
//  ApzApp.h
//  Appzillon
//
//  Created by Manu Gowda N R on 4/2/20.
//  Copyright © 2020 i-exceed. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
@class AppzillonViewController;

NS_ASSUME_NONNULL_BEGIN

@protocol ApzAppHandler
-(void)onApzAppMessage:(NSDictionary *)jsonDict;
@end

@interface ApzApp : NSObject
{
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
@property(assign,nonatomic)id<ApzAppHandler>delegate;
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
@property (nonatomic, strong) NSString *appPath;
@property (nonatomic, strong) NSMutableDictionary *OTAFileDict;
@property (nonatomic, strong)  NSString *OTAFileDictPath;
@property(nonatomic,strong)NSString *appIDNotes;
@property(nonatomic,strong) NSString *notificationCenter;

//Native Components
@property(nonatomic,strong) NSDictionary *nativeAppConfig, *nativeApzConfig;
@property(nonatomic,strong) id nativeAppContext;

//Methods to end user
+(id)sharedManager;
-(void)launch;
-(void)close;
-(void)initWithContext:(id)appContext withApzConfig:(NSDictionary *)apzConfig withAppConfig:(NSDictionary *)appConfig andDelegate:(id)delegate;


//This method is for internal use in framework, user need not to use this method
-(void)sendCallbackToUser:(NSDictionary *)jsonDict;

@end

NS_ASSUME_NONNULL_END
