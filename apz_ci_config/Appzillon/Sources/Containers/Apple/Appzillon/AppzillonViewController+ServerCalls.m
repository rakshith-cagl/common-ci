//
//  AppzillonViewController+ServerCalls.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 28/07/16.
//
//

#import "AppzillonViewController+ServerCalls.h"
#import "CallServer.h"
#import "CryptoUtility.h"
#import <APPZILLONPRODUCTNAME-Swift.h>

@implementation AppzillonViewController (ServerCalls)

-(void)notificationRegWithToken{
    self.runTimeDictPath=[[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/Container.plist",appString]];
    self.runTimeDict = [[NSMutableDictionary alloc] initWithContentsOfFile:self.runTimeDictPath];
    self.tokenStored=[self.runTimeDict objectForKey:@"tokenStored"];
    NSString *serverRegDone=[self.runTimeDict objectForKey:@"serverRegistrationDone"];
    NSString *devRegDone = [self.runTimeDict objectForKey:@"deviceRegistrationDone"];
    
    NSString *regID = APZAppDelegateUtility.shared.appDelegate.regID;
    
    if(![devRegDone isEqualToString:@"YES"] && ![serverRegDone isEqualToString:@"YES"]){
        // first launch of the app
        if (![regID isEqualToString:@""] && regID != nil) {
            [self checkNotificationStatusAndSaveToken:NO];
        }
        [self appzillonRequestForFirstLaunch];
    }else if (![regID isEqualToString:@""] && regID != nil && [serverRegDone isEqualToString:@"YES"] && [devRegDone isEqualToString:@"YES"] && ![regID isEqualToString:self.tokenStored]){
        [self checkNotificationStatusAndSaveToken:YES];
    } else if (![regID isEqualToString:@""] && regID != nil && ![serverRegDone isEqualToString:@"YES"] && [devRegDone isEqualToString:@"YES"]) {
        // second launch and when notification was manually allowed from settings
        [self checkNotificationStatusAndSaveToken:YES];
    } else{
        NSLog(@"Device is already Registerd for notifications in Specified Server");
    }
}

-(void) checkNotificationStatusAndSaveToken: (BOOL) shouldRegisterAppzillonAPNS {
    NSString *NotificationStatus = [APZAppDelegateUtility.shared.appDelegate.containerPropsDictionary objectForKey:@"NOTIFICATION"];
    if ([NotificationStatus isEqualToString:@"Y"]) {
        [self.runTimeDict setObject:APZAppDelegateUtility.shared.appDelegate.regID forKey:@"tokenStored"];
        [self.runTimeDict writeToFile:self.runTimeDictPath atomically: YES];
        if (shouldRegisterAppzillonAPNS) {
            [self registerAppzillonAPNS];
        }
    }
}

#pragma mark Server Reg for Notifications request
-(void) registerAppzillonAPNS
{
    //    NSString *ipurl = [self.appPropertyDictionary objectForKey:@"serverUrl"];
    NSString *ipurl = self.APZServerURL;
    NSString *deviceID=[[NSUserDefaults standardUserDefaults] objectForKey:@"uniqueID"];
    NSUInteger versioninInt = [[[UIDevice currentDevice] systemVersion]integerValue];
    NSString *version = [NSString stringWithFormat: @"%d", (int)versioninInt];
    NSString *reg = [APZAppDelegateUtility.shared.appDelegate.regID mutableCopy];
    NSString *deviceName=[[UIDevice currentDevice] name];
    reg=[reg stringByReplacingOccurrencesOfString:@"<" withString:@""];
    reg=[reg stringByReplacingOccurrencesOfString:@">" withString:@""];
    reg=[reg stringByReplacingOccurrencesOfString:@" " withString:@""];
    NSMutableDictionary *appzillonHeader=[[NSMutableDictionary alloc] init];
    [appzillonHeader setObject:appString forKey:@"appId"];
    [appzillonHeader setObject:@"sessionId" forKey:@"sessionId"];
    [appzillonHeader setObject:uniqueID forKey:@"deviceId"];
    [appzillonHeader setObject:@"000NEW" forKey:@"requestKey"];
    [appzillonHeader setObject:@"IOS" forKey:@"userId"];
    [appzillonHeader setObject:@"login" forKey:@"screenId"];
    [appzillonHeader setObject:@"false" forKey:@"async"];
    [appzillonHeader setObject:@"" forKey:@"requestID"];
    [appzillonHeader setObject:[NSNumber numberWithBool:true] forKey:@"status"];
    [appzillonHeader setObject:@"appzillonNotificationRegistration" forKey:@"interfaceId"];
    
    NSMutableDictionary *appzillonBody=[[NSMutableDictionary alloc] init];
    [appzillonBody setObject:deviceID forKey:@"deviceId"];
    [appzillonBody setObject:@"APPZILLON" forKey:@"source"];
    [appzillonBody setObject:ipAddress forKey:@"origination"];
    [appzillonBody setObject:@"iOS" forKey:@"osId"];
    [appzillonBody setObject:reg forKey:@"regId"];
    [appzillonBody setObject:deviceName forKey:@"deviceName"];
    [appzillonBody setObject:version forKey:@"osVersion"];
    [appzillonBody setObject:appString forKey:@"appId"];
    NSMutableDictionary *appzillonNotifRegRequest=[[NSMutableDictionary alloc] init];
    [appzillonNotifRegRequest setObject:appzillonBody forKey:@"appzillonBody"];
    [appzillonNotifRegRequest setObject:appzillonHeader forKey:@"appzillonHeader"];
    [CallServer callServerWithRequest:appzillonNotifRegRequest :ipurl :appString :self completionHandler:^(BOOL status, NSDictionary *responseDictionary) {
        if (status) {
            [self afRequestSuccessful:responseDictionary];
        } else {
            [self afRequestFailure:responseDictionary];
        }
    }];
}

-(void)afRequestSuccessful:(NSDictionary *)jsonDict{
    NSMutableDictionary * jsonBody = (NSMutableDictionary *) [ jsonDict objectForKey:@"appzillonHeader"];
    BOOL val = [[jsonBody objectForKey:@"status"] boolValue];
    if (val) {
        self.runTimeDict = [[NSMutableDictionary alloc] initWithContentsOfFile:self.runTimeDictPath];
        [self.runTimeDict setObject:@"YES" forKey:@"serverRegistrationDone"];
        [self.runTimeDict writeToFile:self.runTimeDictPath atomically: YES];
    }else{
        NSLog(@"Registration Failed for Push Notification");
    }
}

-(void)afRequestFailure:(NSDictionary *)responseString{
    NSLog(@"----pushnotifircation registration failure----");
}
-(void)APNSRegistration{
    
#if __IPHONE_OS_VERSION_MIN_REQUIRED > __IPHONE_7_0 && __IPHONE_OS_VERSION_MIN_REQUIRED < __IPHONE_10_0
    
    [[UIApplication APZAppDelegateUtility.shared.appDelegateApplication] registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:(UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge) categories:nil]];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [[UIApplication APZAppDelegateUtility.shared.appDelegateApplication] registerForRemoteNotifications];
    });
    
#elif __IPHONE_OS_VERSION_MIN_REQUIRED > __IPHONE_10_0
    if (@available(iOS 10.0, *)) {
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
        UNAuthorizationOptions options = UNAuthorizationOptionAlert + UNAuthorizationOptionSound;
        center.delegate=APZAppDelegateUtility.shared.appDelegate.self;
        [center getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings * _Nonnull settings) {
//            if (settings.authorizationStatus != UNAuthorizationStatusAuthorized) {
                [center requestAuthorizationWithOptions:options
                                      completionHandler:^(BOOL granted, NSError * _Nullable error) {
                    if (granted) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [[UIApplication sharedApplication] registerForRemoteNotifications];
                        });
                    }else{
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [self notificationRegWithToken];
                        });
                    }
                }];
//            }
        }];
        //    [center removeAllDeliveredNotifications];
        //    [center removeAllPendingNotificationRequests];
        [self notificationRegister:center];
    }
#else
    [[UIApplication APZAppDelegateUtility.shared.appDelegateApplication] registerForRemoteNotificationTypes: (UIRemoteNotificationTypeNewsstandContentAvailability| UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
    
#endif
}

-(void)locationCheckBeforeMultifactorReg{
    if ([[self.appPropertyDictionary objectForKey:@"trackLocation"]isEqualToString:@"Y"])
    {
        [self locationPermission];
    }
    else
    {
        [self callDeviceRegistration:nil];
    }
    //    BOOL isFirstLaunch = [[NSUserDefaults standardUserDefaults] boolForKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",appString]];
    //    if (isFirstLaunch == NO)
    //    {
    //        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",appString]];
    //    }
    if (APZAppDelegateUtility.shared.appDelegate.pushMessege) {
        [self notifCallback];
    }
    self.calledFromPlugin=NO;
}
-(void)notificationRegister:(UNUserNotificationCenter *)center{
    NSString* domainPath=[[NSString alloc]initWithFormat:@"/Assets/apps/%@/screens/config/",appString];
    NSString *path = [[MiscellaneousMethod.shared getAppBundle] pathForResource:@"notif_details" ofType:@"json" inDirectory:domainPath];
    if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {
        NSData *data = [NSData dataWithContentsOfFile:path];
        NSArray*notifJSON=[NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
        NSSet *catagorySet =[[NSSet alloc]init];
        for (NSDictionary *notificationJSON in notifJSON) {
            UNMutableNotificationContent *content = [UNMutableNotificationContent new];
            content.categoryIdentifier =[notificationJSON objectForKey:@"notification_code"];
            NSArray *actions=[notificationJSON objectForKey:@"actions"];
            NSMutableArray *actionArray=[[NSMutableArray alloc]init];
            for (NSDictionary *actionDic in actions) {
                UNNotificationAction *notifAction = [UNNotificationAction actionWithIdentifier:[actionDic objectForKey:@"action_code"] title:[actionDic objectForKey:@"action_display"] options:UNNotificationActionOptionForeground];
                [actionArray addObject:notifAction];
            }
            
            UNNotificationCategory *catagory = [UNNotificationCategory categoryWithIdentifier:[notificationJSON objectForKey:@"notification_code"] actions:actionArray intentIdentifiers:@[] options:UNNotificationCategoryOptionNone];
            catagorySet= [catagorySet setByAddingObject:catagory];
        }
        [center setNotificationCategories:catagorySet];
    }
    
}
@end





