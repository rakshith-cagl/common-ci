//
//  AppzillonAppDelegate.m
//  Azuritei-tab
//
//  Created by Apple on 13/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "AppzillonAppDelegate.h"
#import "AppzillonViewController+ServerCalls.h"
#include <sys/stat.h>
#import "KeychainUtility.h"
#import "CryptoUtility.h"
#import "MiscellaneousMethods.h"
#import "HashUtility.h"


@interface AppzillonViewController ()
-(void)notifCallback;
-(void)hideScreenInBackground;
-(void)revealTheScreen;
@end


@implementation AppzillonAppDelegate
@synthesize containerPropsDictionary;
@synthesize viewController = _viewController;
@synthesize window = _window;
@synthesize date;
@synthesize isiPad;
@synthesize regComplete;
@synthesize regID;
@synthesize isAppActive;
@synthesize isAppExpired;
@synthesize bgTask;
@synthesize pushTitle;
@synthesize pushSubTitle;
@synthesize pushMessege;
@synthesize pushImgURL;
@synthesize pushCategory;
@synthesize pushParams;
@synthesize appPath;
@synthesize OTAFileDict;
@synthesize OTAFileDictPath;
@synthesize appIDNotes;
@synthesize notificationCenter;
@synthesize pushActionType;


- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler{
    pushTitle = [[[userInfo valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"title"];
    pushSubTitle = [[[userInfo valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"subtitle"];
    pushMessege = [[[userInfo valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"body"];
    pushCategory = [[userInfo valueForKey:@"aps"] valueForKey:@"category"];
    pushImgURL = [userInfo valueForKey:@"image_url"];
    pushParams=[userInfo valueForKey:@"message_param"];
    if (![notificationCenter isEqualToString:@"fromCenter"]) {
        [self.viewController notifCallback];
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:@"updateRoot" object:nil];
    
}

-(void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions options))completionHandler{
    notificationCenter=@"appFG";
    completionHandler(UNAuthorizationOptionSound | UNAuthorizationOptionAlert | UNAuthorizationOptionBadge);
}


-(void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void(^)(void))completionHandler{
    pushActionType=response.actionIdentifier;
    NSDictionary *userInfo=response.notification.request.content.userInfo;
    pushTitle = [[[userInfo valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"title"];
    pushSubTitle = [[[userInfo valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"subtitle"];
    pushMessege = [[[userInfo valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"body"];
    pushCategory = [[userInfo valueForKey:@"aps"] valueForKey:@"category"];
    pushImgURL = [userInfo valueForKey:@"image_url"];
    pushParams=[userInfo valueForKey:@"message_param"];
    completionHandler();
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([notificationCenter isEqualToString:@"appFG"]) {
            [self.viewController notifCallback];
        }
    });
    
}
-(void)application:(UIApplication *)application handleActionWithIdentifier:(NSString *)identifier forRemoteNotification:(NSDictionary *)userInfo completionHandler:(void (^)(void))completionHandler{
    pushActionType=identifier;
    pushTitle = [[[userInfo valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"title"];
    pushSubTitle = [[[userInfo valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"subtitle"];
    pushMessege = [[[userInfo valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"body"];
    pushCategory = [[userInfo valueForKey:@"aps"] valueForKey:@"category"];
    pushImgURL = [userInfo  valueForKey:@"image_url"];
    pushParams=[userInfo valueForKey:@"message_param"];
}
-(void)application:(UIApplication *)app didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    //    regID = [deviceToken description];
    regID = [HashUtility getHashStringFromData:deviceToken];
    if([regComplete isEqualToString:@"serverReg"])
    {
        
    }
    else
    {
        regComplete = @"deviceReg";
    }
    //    NSString *token = [[deviceToken description] stringByTrimmingCharactersInSet: [NSCharacterSet characterSetWithCharactersInString:@"<>"]];
    //    token = [token stringByReplacingOccurrencesOfString:@" " withString:@""];
    [self.viewController notificationRegWithToken];
}

-(void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    regComplete = @"deviceReg";
    regID = @"IOSSIM";
}

-(void)alertNotice:(NSString *)title withMSG:(NSString *)msg cancleButtonTitle:(NSString *)cancleTitle otherButtonTitle:(NSString *)otherTitle{
    UIAlertController *alert;

    if([otherTitle isEqualToString:@""]){
        alert = [UIAlertController alertControllerWithTitle:title message:msg preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:cancleTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
    [alert addAction:actionCancel];
        
    }

    else{
        alert = [UIAlertController alertControllerWithTitle:title message:msg preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:cancleTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
        UIAlertAction * otherButton = [UIAlertAction actionWithTitle:otherTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
        [alert addAction:actionCancel];
        [alert addAction:otherButton];
    }

    [self.viewController presentViewController:alert animated:YES completion:nil];
    
}

-(void)application:(UIApplication *)application performActionForShortcutItem:(UIApplicationShortcutItem *)shortcutItem completionHandler:(void(^)(BOOL succeeded))completionHandler API_AVAILABLE(ios(9.0)) API_UNAVAILABLE(tvos){
    NSLog(@"performActionForShortcutItem = %@, %@, %@",shortcutItem.type, shortcutItem.localizedTitle, shortcutItem.localizedSubtitle);
    _shortcutID = shortcutItem.type;
    _shortcutTitle = shortcutItem.localizedTitle;
    _shortcutSubTitle = shortcutItem.localizedSubtitle;
    //    [self.viewController shortcutActionCallback];
}

-(void)initializeWindowForAlert{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    [self.window makeKeyAndVisible];
    self.window.rootViewController = [[UIViewController alloc] init];
    
}

-(BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    //    UIApplicationShortcutItem *item = [launchOptions valueForKey:UIApplicationLaunchOptionsShortcutItemKey];
    //    if (item) {
    //        NSLog(@"didFinishLaunchingWithOptions, We've launched from shortcut item: %@", item.localizedTitle);
    //        [self.viewController shortcutActionCallback];
    //    } else {
    //        NSLog(@"didFinishLaunchingWithOptions, We've launched properly.");
    //    }
    if (![[NSUserDefaults standardUserDefaults] objectForKey:@"FirstRun"]) {
        // Delete values from keychain here
        [self removeOldDataFromKeychain];
        [[NSUserDefaults standardUserDefaults] setValue:@"1strun" forKey:@"FirstRun"];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    NSString *documentsDirectory = [[NSString alloc] initWithString:[paths objectAtIndex:0]];
    //    NSFileManager *fileManager = [NSFileManager defaultManager];
    isAppActive =YES;
    NSString* containerPropsPath = [[MiscellaneousMethods getAppBundle] pathForResource:@"containerprops" ofType:@"plist"];
    containerPropsDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:containerPropsPath];
    NSString* mainAppId=[containerPropsDictionary objectForKey:@"MAINAPPID"];
    //    NSString *str1 = [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Container.plist"]];
    
    //jail broken device condition
    BOOL jailBroken = [self checkForJailBrokenDevice];
    if (!jailBroken) {
        return NO;
    }
    //    NSString *isWiped;
    //    if ([fileManager fileExistsAtPath:str1]) {
    //        NSMutableDictionary *isolatedDictionary = [[NSMutableDictionary alloc] initWithContentsOfFile:str1];
    //        isWiped=[isolatedDictionary objectForKey:@"wipedOut"];
    //    }
    if ([[[NSUserDefaults standardUserDefaults]
          stringForKey:@"wipedOut"] isEqualToString:@"YES"] ) {
        UIAlertController *alertView = [UIAlertController alertControllerWithTitle:nil message:@"Application has been Disabled , Reinstall the Application"  preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
        [alertView addAction:actionCancel];
        [self initializeWindowForAlert];
        [self.window.rootViewController presentViewController:alertView animated:YES completion:nil];
    }
    else if (isAppExpired){
        UIAlertController *alertView = [UIAlertController alertControllerWithTitle:nil message:@"Application has been Disabled , Reinstall the Application"  preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
        [alertView addAction:actionCancel];
        [self initializeWindowForAlert];
        [self.window.rootViewController presentViewController:alertView animated:YES completion:nil];
    }
    else{
        if ([[containerPropsDictionary objectForKey:@"NOTIFICATION"] isEqualToString:@"Y"]) {
            [self checkIfNotifcationRecieved:application :launchOptions];
        }
        appPath = [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/apps/%@",mainAppId]];
        //        NSUserDefaults *defaults= [NSUserDefaults standardUserDefaults];
        
        //        BOOL launchedBefore= [[NSUserDefaults standardUserDefaults] boolForKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",[containerPropsDictionary objectForKey:@"MAINAPPID"]]];
        //        ////Patch for Older Appzillon key mismatch
        //        BOOL launchedOnOlderVersion=[[[defaults dictionaryRepresentation] allKeys] containsObject:@"HasLaunchedOnce"];
        //        BOOL launchedOnNewVersion=[NSString stringWithFormat:@"%@.HasLaunchedOnce",[containerPropsDictionary objectForKey:@"MAINAPPID"]];
        
        if (![MiscellaneousMethods getAppHasLaunchedBefore:[containerPropsDictionary objectForKey:@"MAINAPPID"]])
        {
            [self copyAppzillonAppFiles:[containerPropsDictionary objectForKey:@"OTAREQUIRED"]];
            [self initPushNotification];
            
            //Generate a random key for to encrypt the plist values, and store it in keychain
            NSString *encryptKey = [KeychainUtility generateSecureRandomString];
            [KeychainUtility storeDataInKeychain:PLISTENCRYPTKEY value:encryptKey];
            
            //AppVersion check from the Appzillon-Info.plist(Bundle)
            NSDictionary* infoDictionary = [[MiscellaneousMethods getAppBundle] infoDictionary];
            NSString * storedBundleShortVersion = infoDictionary[@"CFBundleShortVersionString"];
            NSString * storedBundleVersion = infoDictionary[@"CFBundleVersion"];
            [[NSUserDefaults standardUserDefaults] setValue:storedBundleVersion forKey:@"storedBundleVersion"];
            [[NSUserDefaults standardUserDefaults] setValue:storedBundleShortVersion forKey:@"storedBundleShortVersion"];
            [[NSUserDefaults standardUserDefaults] setObject:@"N" forKey:@"updateAppVersion"];
            [[NSUserDefaults standardUserDefaults] synchronize];
        }
        else{
            if ([MiscellaneousMethods getAppHasLaunchedOnOlderVersion]) {
                NSString *encryptKey = [KeychainUtility generateSecureRandomString];
                [KeychainUtility storeDataInKeychain:PLISTENCRYPTKEY value:encryptKey];
            }
            [self storePropertyValuesToPlist];
        }
        self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
        isiPad = NO;
        [self getMainApp];
        self.window.rootViewController = self.viewController;
        [self.window makeKeyAndVisible];
    }
    return YES;
}

-(void)storePropertyValuesToPlist{
    NSDictionary* infoDictionary = [[MiscellaneousMethods getAppBundle] infoDictionary];
    NSString * currentBundleShortVersion = infoDictionary[@"CFBundleShortVersionString"];
    NSString * currentBundleVersion = infoDictionary[@"CFBundleVersion"];
    NSString *oldBundleVersion = [[NSUserDefaults standardUserDefaults] objectForKey:@"storedBundleVersion"];
    NSString *oldBundleShortVersion = [[NSUserDefaults standardUserDefaults] objectForKey:@"storedBundleShortVersion"];
    if(![oldBundleVersion isEqualToString:currentBundleVersion] || ![oldBundleShortVersion isEqualToString:currentBundleShortVersion]){
        ///in case of update, refresh appzillon files. then encrypt and store plist files in sandbox,
        if ([MiscellaneousMethods getAppHasLaunchedBefore:[containerPropsDictionary objectForKey:@"MAINAPPID"]]){
            [self copyAppzillonAppFiles:[containerPropsDictionary objectForKey:@"OTAREQUIRED"]];
        }
        ///
        NSString *settingsPath = [[MiscellaneousMethods getAppBundle] pathForResource:@"appprops" ofType:@"json" inDirectory:[NSString stringWithFormat:@"Assets/apps/%@/screens/config",[containerPropsDictionary objectForKey:@"MAINAPPID"]]];
        if([[NSFileManager defaultManager] fileExistsAtPath:settingsPath]){
            NSString *fileContent = [[NSString alloc] initWithContentsOfFile:settingsPath encoding:NSUTF8StringEncoding error:nil];
            NSError *error=nil;
            NSDictionary *appPropsDict = [NSJSONSerialization JSONObjectWithData:[fileContent  dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:&error];
            
            appPropsDict = [CryptoUtility encryptPlistData:appPropsDict];
            NSString *appPropertiesPath = [[NSString alloc] initWithString:[[self documentDirectory] stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",[containerPropsDictionary objectForKey:@"MAINAPPID"]]];
            [appPropsDict writeToFile:appPropertiesPath atomically:YES];
        }
        [[NSUserDefaults standardUserDefaults] setValue:currentBundleVersion forKey:@"storedBundleVersion"];
        [[NSUserDefaults standardUserDefaults] setValue:currentBundleShortVersion forKey:@"storedBundleShortVersion"];
        [[NSUserDefaults standardUserDefaults] setObject:@"Y" forKey:@"updateAppVersion"];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }else{
        [[NSUserDefaults standardUserDefaults] setObject:@"N" forKey:@"updateAppVersion"];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

-(BOOL)checkForJailBrokenDevice{
#if !(TARGET_IPHONE_SIMULATOR)
    
    UIAlertController *alertView = [UIAlertController alertControllerWithTitle:nil message:@"This Device is jail broken. Application cannot be run" preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
    [alertView addAction:actionCancel];
    
    NSString *isJB = @"76f6243716d4029726022224a43796220237960256d616e40247365746f627050237968645";
    NSMutableString *a = [NSMutableString new];
    while([isJB length] != [a length]){
        NSRange range = NSMakeRange([isJB length]-[a length]-1,1);
        [a appendString:[isJB substringWithRange:range]];
    }
    NSMutableString *b = [[NSMutableString alloc]init];
    int c = 0;
    while(c < [a length]){
        NSString *d = [a substringWithRange:NSMakeRange(c,2)];
        int e = 0;
        sscanf([d cStringUsingEncoding:NSASCIIStringEncoding],"%x",&e);
        [b appendFormat:@"%c",(char)e];
        c += 2;
    }
    BOOL isDirectory;
    if ([[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/Cydia.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Library/MobileSubstrate/MobileSubstrate.dylib"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/bin/bash"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/etc/clutch.conf"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/cache/clutch.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/etc/clutch_cracked.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/cache/clutch_cracked.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/lib/clutch/overdrive.dylib"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/root/Documents/Cracked/"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Library/MobileSubstrate/DynamicLibraries/Veency.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/sbin/sshd"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/etc/apt"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/private/var/lib/apt/"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Cydia/Substrate"] ||
        [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"ryleyangus.com/repo/"]] ||
        [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"cydia://package/com.example.package"]] || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Cyd", @"ia.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"bla", @"ckra1n.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Fake", @"Carrier.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Liberty", @"Lite.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Liberty.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Lite.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"ex", @"con.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"fl", @"ex3.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"excon", @"flex3.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Ic", @"y.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Inte", @"lliScreen.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"MxT", @"ube.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Roc", @"kApp.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"SBSet", @"ttings.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Wint", @"erBoard.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"pr", @"iva",@"te/v", @"ar/l", @"ib/a", @"pt/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"pr", @"iva",@"te/v", @"ar/l", @"ib/c", @"ydia/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"pr", @"iva",@"te/v", @"ar/mobile", @"Library/SBSettings", @"Themes/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"pr", @"iva",@"te/v", @"ar/t", @"mp/cyd", @"ia.log"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"pr", @"iva",@"te/v", @"ar/s", @"tash/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"us", @"r/l",@"ibe", @"xe", @"c/cy", @"dia/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"us", @"r/b",@"in", @"s", @"shd"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"us", @"r/sb",@"in", @"s", @"shd"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"us", @"r/l",@"ibe", @"xe", @"c/cy", @"dia/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"us", @"r/l",@"ibe", @"xe", @"c/sftp-", @"server"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@",@"Syste",@"tem/Lib",@"rary/Lau",@"nchDae",@"mons/com.ike",@"y.bbot.plist"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@%@",@"Sy",@"stem/Lib",@"rary/Laun",@"chDae",@"mons/com.saur",@"ik.Cy",@"@dia.Star",@"tup.plist"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"Libr",@"ary/Mo",@"bileSubstra",@"te/MobileSubs",@"trate.dylib"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"va",@"r/c",@"ach",@"e/a",@"pt/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"va",@"r/l",@"ib",@"/apt/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"va",@"r/l",@"ib/c",@"ydia/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"va",@"r/l",@"og/s",@"yslog"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"private/va",@"r/c",@"ach",@"e/a",@"pt/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"private/va",@"r/l",@"ib",@"/apt/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"private/va",@"r/l",@"ib/c",@"ydia/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"private/va",@"r/l",@"og/s",@"yslog"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"bi",@"n/b",@"ash"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"b",@"in/",@"sh"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"private/et",@"c/a",@"pt/"]isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"et",@"c/a",@"pt/"]isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"private/etc/s",@"sh/s",@"shd_config"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"etc/s",@"sh/s",@"shd_config"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"us",@"r/li",@"bexe",@"c/ssh-k",@"eysign"]]
        || [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"cydia://package/com.masbog.com"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Snoop-it", @" Config.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/", @" xCon.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/", @" excon.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/", @" flex3.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/", @" Liberty.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/", @" Lite.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/",@"Liberty", @"Lite.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/",@" Liberty",@" Lite.", @"dylib"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"priv",@"ate/etc/dpkg/",@"origins/debian"]])  {
        [self initializeWindowForAlert];
        [self.window.rootViewController presentViewController:alertView animated:YES completion:nil];
        return NO;
    }    
    
    //Symbolic link verification
    struct stat s;
    if(lstat("/Applications", &s) || lstat("/var/stash/Library/Ringtones", &s) || lstat("/var/stash/Library/Wallpaper", &s)
       || lstat("/var/stash/usr/include", &s) || lstat("/var/stash/usr/libexec", &s)  || lstat("/var/stash/usr/share", &s) || lstat("/var/stash/usr/arm-apple-darwin9", &s))
    {
        if(s.st_mode & S_IFLNK){
            [self initializeWindowForAlert];
            [self.window.rootViewController presentViewController:alertView animated:YES completion:nil];
            return NO;
        }
    }
    
    //Try to write file in private
    NSError *error;
    FILE *f = NULL ;
    if ((f = fopen("/bin/bash", "r")) ||
        (f = fopen("/Applications/Cydia.app", "r")) ||
        (f = fopen("/Library/MobileSubstrate/MobileSubstrate.dylib", "r")) ||
        (f = fopen("/usr/sbin/sshd", "r")) ||
        (f = fopen("/etc/apt", "r")))  {
        fclose(f);
        [self initializeWindowForAlert];
        [self.window.rootViewController presentViewController:alertView animated:YES completion:nil];
        return NO;
    }
    fclose(f);
    NSError *error1;
    NSString *stringToBeWritten = @"Hello, MasBog Here!!!";
    NSString *encryptKey = [KeychainUtility generateSecureRandomString];
       [[CryptoUtility getAESEncryptedString:stringToBeWritten : encryptKey] writeToFile:@"/private/masbog.txt" atomically:YES
                             encoding:NSUTF8StringEncoding error:&error1];
    [[NSFileManager defaultManager] removeItemAtPath:@"/private/masbog.txt" error:nil];
    if(error1 == nil)
    {
        [self initializeWindowForAlert];
        [self.window.rootViewController presentViewController:alertView animated:YES completion:nil];
        return NO;
    }
    
    NSArray *blah = [NSArray arrayWithObjects:@"f28637164737f2271667f2", @"f28637164737f2271667f256471667962707f2", @"f28637164737f22646f2271667f256471667962707f2", nil];
    NSMutableString *hihi = [NSMutableString new];
    
    while ([blah[0] length]!=[hihi length]) {
        NSRange range = NSMakeRange([blah[0] length]-[hihi length]-1, 1);
        [hihi appendString: [blah[0] substringWithRange:range]];
    }
    
    NSMutableString *haha = [[NSMutableString alloc] init];
    int i = 0;
    while (i < [hihi length])
    {
        NSString *hehe = [hihi substringWithRange: NSMakeRange(i, 2)];
        int value = 0;
        sscanf([hehe cStringUsingEncoding:NSASCIIStringEncoding], "%x", &value);
        [haha appendFormat:@"%c", (char)value];
        i+=2;
    }
    
    NSArray *hahaList = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:haha error:nil];
    if (hahaList.count > 0) {
        for (NSString *fufufufu in hahaList){
            if (![fufufufu containsString:@"lnk"]) {
                NSArray *hahaListSub = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:[NSString stringWithFormat:@"%@%@/DynamicLibraries", haha, fufufufu] error:nil];
                for (NSString *wkwkwkwk in hahaListSub){
                    if ([wkwkwkwk containsString:@".dylib"] || [wkwkwkwk containsString:@".plist"]) {
                        [self initializeWindowForAlert];
                        [self.window.rootViewController presentViewController:alertView animated:YES completion:nil];
                        return NO;
                    }
                }
            }
        }
    }
    
    //============== array index 1 ===========//
    hihi = [NSMutableString new];
    while ([blah[1] length]!=[hihi length]) {
        NSRange range = NSMakeRange([blah[1] length]-[hihi length]-1, 1);
        [hihi appendString: [blah[1] substringWithRange:range]];
    }
    
    haha = [[NSMutableString alloc] init];
    i = 0;
    while (i < [hihi length])
    {
        NSString *hehe = [hihi substringWithRange: NSMakeRange(i, 2)];
        int value = 0;
        sscanf([hehe cStringUsingEncoding:NSASCIIStringEncoding], "%x", &value);
        [haha appendFormat:@"%c", (char)value];
        i+=2;
    }
    
    hahaList = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:haha error:nil];
    if (hahaList.count > 0) {
        for (NSString *fufufufu in hahaList){
            if (![fufufufu containsString:@"lnk"]) {
                NSArray *hahaListSub = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:[NSString stringWithFormat:@"%@%@/DynamicLibraries", haha, fufufufu] error:nil];
                for (NSString *wkwkwkwk in hahaListSub){
                    if ([wkwkwkwk containsString:@".dylib"] || [wkwkwkwk containsString:@".plist"]) {
                        [self initializeWindowForAlert];
                        [self.window.rootViewController presentViewController:alertView animated:YES completion:nil];
                        return NO;
                    }
                }
            }
        }
    }
    
    
    //============== array index 2 ===========//
    hihi = [NSMutableString new];
    while ([blah[2] length]!=[hihi length]) {
        NSRange range = NSMakeRange([blah[2] length]-[hihi length]-1, 1);
        [hihi appendString: [blah[2] substringWithRange:range]];
    }
    
    haha = [[NSMutableString alloc] init];
    i = 0;
    while (i < [hihi length])
    {
        NSString *hehe = [hihi substringWithRange: NSMakeRange(i, 2)];
        int value = 0;
        sscanf([hehe cStringUsingEncoding:NSASCIIStringEncoding], "%x", &value);
        [haha appendFormat:@"%c", (char)value];
        i+=2;
    }
    
    hahaList = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:haha error:nil];
    if (hahaList.count > 0) {
        for (NSString *fufufufu in hahaList){
            if (![fufufufu containsString:@"lnk"]) {
                NSArray *hahaListSub = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:[NSString stringWithFormat:@"%@%@/DynamicLibraries", haha, fufufufu] error:nil];
                for (NSString *wkwkwkwk in hahaListSub){
                    if ([wkwkwkwk containsString:@".dylib"] || [wkwkwkwk containsString:@".plist"]) {
                        [self initializeWindowForAlert];
                        [self.window.rootViewController presentViewController:alertView animated:YES completion:nil];
                        return NO;
                    }
                }
            }
        }
    }
#endif
    return YES;
}

-(void)initPushNotification{
    NSString *appzillonAppDocumentDirectory = [[self documentDirectory] stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/apps/%@/sqlite/APPSDB.sqlite3",[self.containerPropsDictionary objectForKey:@"MAINAPPID"]]];
    sqlite3 * dbRef;
    int result=sqlite3_open([appzillonAppDocumentDirectory UTF8String],&dbRef);
    if (result==0) {
        sqlite3_stmt *createTable;
        sqlite3_prepare(dbRef, [@"CREATE TABLE tb_notifications(id integer primary key autoincrement, message varchar(500),timeStamp varchar(8),readFlag varchar(1))" UTF8String], -1, &createTable, NULL);
        if(sqlite3_step(createTable)==SQLITE_DONE){
            sqlite3_close(dbRef);
        }else{
            NSLog(@"----error in creating table tb_notifications");
        }
    }
    else{
        NSLog(@"----error in creating notification DB %d",result);
    }
}

-(void)getMainApp
{
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        isiPad = YES;
        self.viewController = [[AppzillonViewController alloc] initWithNibName:@"AppzillonViewController" bundle:nil];
    }
    
    else if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        
        self.viewController = [[AppzillonViewController alloc] initWithNibName:@"Azuritei_phoneViewController" bundle:nil];
        
    }
}
- (void)applicationWillResignActive:(UIApplication *)application
{
    
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    NSString *AppScreenshot = [containerPropsDictionary objectForKey:@"PREVENTSCREENSHOT"];
    if ([AppScreenshot isEqualToString:@"Y"]) {
        if(self.viewController.view.window){
            [self.viewController hideScreenInBackground];
        }
    }
    self.bgTask = [application beginBackgroundTaskWithExpirationHandler:^{
        [application endBackgroundTask:bgTask];
        self.bgTask = UIBackgroundTaskInvalid;
    }];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSError *error = nil;
        NSString *tempDir = NSTemporaryDirectory();
        NSArray *tempFileArr =[[NSFileManager defaultManager] contentsOfDirectoryAtPath:tempDir error:&error];
        [tempFileArr enumerateObjectsUsingBlock:^(id fileName, NSUInteger i, BOOL *stop) {
            NSLog(@"%@ isFileRemoved = %d",fileName,[[NSFileManager defaultManager] removeItemAtPath:[NSTemporaryDirectory() stringByAppendingPathComponent:fileName] error:nil]);
        }];
        [application endBackgroundTask:bgTask];
        self.bgTask = UIBackgroundTaskInvalid;
    });
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    if(self.viewController.view.window){
        [self.viewController revealTheScreen];
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        
        //        if (pushMessege != nil && ![pushMessege isEqualToString:@""]) {
        if( pushMessege.length != 0){
            [self.viewController notifCallback];
        }
    });
}


- (void) applicationProtectedDataDidBecomeAvailable:(UIApplication *)application {
    
}

- (void) applicationProtectedDataWillBecomeUnavailable:(UIApplication *)application {
    
}


- (void)applicationWillTerminate:(UIApplication *)application
{
    
}
-(void)checkIfNotifcationRecieved:(UIApplication*)application :(NSDictionary*)launchOptions{
    UNNotificationRequest *notification = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
    if (notification) {
        pushTitle = [[[notification valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"title"];
        pushSubTitle = [[[notification valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"subtitle"];
        pushMessege = [[[notification valueForKey:@"aps"] valueForKey:@"alert"] valueForKey:@"body"];
        pushCategory = [[notification valueForKey:@"aps"] valueForKey:@"category"];
        pushImgURL = [notification  valueForKey:@"image_url"];
        pushParams=[notification valueForKey:@"message_param"];
        notificationCenter= @"fromCenter";
        [self application:application didReceiveRemoteNotification:(NSDictionary*)notification fetchCompletionHandler:^(UIBackgroundFetchResult result){}];
    }
    
}
#pragma mark -CopyingAppzillonFiles
-(void)copyAppzillonAppFiles:(NSString*)otaStatus{
    NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    NSString *documentsDirectory = [[NSString alloc] initWithString:[paths objectAtIndex:0]];
    NSString* sandBoxFolderAssestFolder = [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/"]];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *bundlePathToAssetFolder=[[[MiscellaneousMethods getAppBundle] resourcePath] stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/"]];
    [self loop:sandBoxFolderAssestFolder :bundlePathToAssetFolder :fileManager :otaStatus];
    NSArray *listOfApps=[fileManager contentsOfDirectoryAtPath:[bundlePathToAssetFolder stringByAppendingPathComponent:@"apps"] error:nil];
    for (NSString *eachAppID in listOfApps) {
        [self copyUserFiles:eachAppID];
    }
}
-(void)loop:(NSString*)destination :(NSString*)source :(NSFileManager*)fileManager :(NSString*)otaStatus{
    BOOL isDir;
    //in case OTA is disabled do not copy content from these folders to sandbox
    NSArray *nonOTAExceptionPath=@[@"screens",@"scripts",@"styles",@"appzillon",@"staticfiles",@"sslCertificates"];
    //in case new updates are availabe do not copy folders below to sandbox
    NSArray *onUpdateExceptionPath=@[@"sqlite",@"plist"];
    NSError *contentError;
    NSArray *sourceContent=[fileManager contentsOfDirectoryAtPath:source error:&contentError];
    for (NSString* sourceElement in sourceContent) {
        if ([fileManager fileExistsAtPath:[source stringByAppendingPathComponent:sourceElement]isDirectory:&isDir]) {
            if (isDir) {
                if([sourceElement isEqualToString:@"staticfiles"]){
                    continue;
                }
                
                if ([MiscellaneousMethods getAppHasLaunchedBefore:[containerPropsDictionary objectForKey:@"MAINAPPID"]]) {
                    if ([onUpdateExceptionPath containsObject:sourceElement]) {
                        continue;
                    }
                }
                
                if ([otaStatus isEqualToString:@"N"]){
                    if ([nonOTAExceptionPath containsObject:sourceElement]) {
                        continue;
                    }
                }
                [self loop:[destination stringByAppendingPathComponent:sourceElement] :[source stringByAppendingPathComponent:sourceElement] :fileManager :otaStatus];
            }else{
                NSError*copyError;
                //removing first in becouse in case of update copyitemAtPath a
                [fileManager removeItemAtPath:[destination stringByAppendingPathComponent:sourceElement] error:nil];
                [fileManager createDirectoryAtPath:destination withIntermediateDirectories:YES attributes:nil error:&copyError];
                [fileManager copyItemAtPath:[source stringByAppendingPathComponent:sourceElement] toPath:[destination stringByAppendingPathComponent:sourceElement] error:&copyError];
            }
        }
    }
    
}
-(void)copyUserFiles :(NSString*)appString{
    NSString *dataPath;
    NSString *pathFromJson;
    NSString* domainPath=[[NSString alloc]initWithFormat:@"/Assets/apps/%@/screens/config/",appString];
    NSString *appzillonAppDocumentDirectory = [[self documentDirectory] stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/apps/%@/",appString]];
    NSString* staticFilePath=[[NSString alloc]initWithFormat:@"/Assets/apps/%@/staticfiles",appString];
    NSArray *docsPath= [[MiscellaneousMethods getAppBundle] pathsForResourcesOfType:nil inDirectory:staticFilePath];
    NSError *error=nil;
    NSFileManager *fileManager= [NSFileManager defaultManager];
    NSString * pathSettingsJson=[[MiscellaneousMethods getAppBundle] pathForResource:@"Files" ofType:@"json" inDirectory:domainPath];
    if([[NSFileManager defaultManager] fileExistsAtPath:pathSettingsJson]){
        NSString *fileContent = [[NSString alloc] initWithContentsOfFile:pathSettingsJson encoding:NSUTF8StringEncoding error:nil];
        NSDictionary * jsonDictionary=[NSJSONSerialization JSONObjectWithData:[fileContent  dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:&error];
        if (error==nil) {
        }else{
            NSLog(@"--No JSON File From Appzillon  for Additional UserFiles");
        }
        NSDictionary *FileAndPath=[jsonDictionary objectForKey:@"Files"];
        NSArray *FileNames=[FileAndPath allKeys];
        for (NSString*fileName in FileNames){
            pathFromJson=[FileAndPath objectForKey:fileName];
            NSString *ActualFolder = [appzillonAppDocumentDirectory stringByAppendingPathComponent:pathFromJson];
            if (![[NSFileManager defaultManager] fileExistsAtPath:ActualFolder])
                [[NSFileManager defaultManager] createDirectoryAtPath:ActualFolder withIntermediateDirectories:YES attributes:nil error:&error];
            if (error) {
                NSLog(@"-Could not Create File Directory for Addtional User Files --");
            }
            for (int i=0; i<docsPath.count; i++) {
                NSString *filenameStatic=[docsPath[i] lastPathComponent];
                if ([fileName isEqualToString:filenameStatic]) {
                    dataPath=docsPath[i];
                    NSString *ActualPath=[ActualFolder stringByAppendingPathComponent:fileName];
                    //added this line to remove static files then re-write them in case of update
                    [fileManager removeItemAtPath:ActualPath error:nil];
                    //
                    if ([fileManager copyItemAtPath:dataPath  toPath:ActualPath error: NULL]  == YES) {
                        NSLog (@"Copy successful");
                    } else {
                        NSLog (@"Copy failed");
                    }
                }
            }
        }
    }
}

-(void) removeOldDataFromKeychain{
    // Create dictionary of search parameters
    NSDictionary* dict = [NSDictionary dictionaryWithObjectsAndKeys:(__bridge id)(kSecClassInternetPassword),  kSecClass, kCFBooleanTrue, kSecReturnAttributes, kCFBooleanTrue, kSecReturnData, nil];
    // Remove any old values from the keychain
    OSStatus err = SecItemDelete((__bridge CFDictionaryRef) dict);
}

-(NSString*)documentDirectory{
    NSArray *paths = NSSearchPathForDirectoriesInDomains
    (NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    return documentsDirectory;
}
@end



