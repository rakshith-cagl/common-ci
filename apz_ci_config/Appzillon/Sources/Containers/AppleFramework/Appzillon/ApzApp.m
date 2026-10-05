//
//  ApzApp.m
//  Appzillon
//
//  Created by Manu Gowda N R on 4/2/20.
//  Copyright © 2020 i-exceed. All rights reserved.
//

#import "ApzApp.h"
#import "AppzillonViewController+ServerCalls.h"
#include <sys/stat.h>
#import "KeychainUtility.h"
#import "CryptoUtility.h"
#import "MiscellaneousMethods.h"
#import "HashUtility.h"

@implementation ApzApp

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


+(id)sharedManager {
    static ApzApp *sharedManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedManager = [[self alloc] init];
    });
    return sharedManager;
}

-(void)initWithContext:(id)appContext withApzConfig:(NSDictionary *)apzConfig withAppConfig:(NSDictionary *)appConfig andDelegate:(id)delegate{
    self.nativeApzConfig = apzConfig;
    self.nativeAppConfig = appConfig;
    self.nativeAppContext = appContext;
    self.delegate = delegate;
}

-(void)launch{
    //Setup Appzillon before presenting AppzillonViewController
    [self intialLaunch];
    
    UINavigationController *navController = [[UINavigationController alloc] initWithRootViewController:self.viewController];
    [navController setModalPresentationStyle:UIModalPresentationFullScreen];
    [self.nativeAppContext presentViewController:navController animated:YES completion:NULL];
}

-(void)close{
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
}

-(void)sendCallbackToUser:(NSDictionary *)jsonDict{
    [self.delegate onApzAppMessage:jsonDict];
}

-(void)intialLaunch{
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
//    BOOL jailBroken = [self checkForJailBrokenDevice];
//    if (!jailBroken) {
//        return ;
//    }
    //    NSString *isWiped;
    //    if ([fileManager fileExistsAtPath:str1]) {
    //        NSMutableDictionary *isolatedDictionary = [[NSMutableDictionary alloc] initWithContentsOfFile:str1];
    //        isWiped=[isolatedDictionary objectForKey:@"wipedOut"];
    //    }
    if ([[[NSUserDefaults standardUserDefaults]
          stringForKey:@"wipedOut"] isEqualToString:@"YES"] ) {
        UIAlertView *alertView=[[UIAlertView alloc] initWithTitle:nil message:@"Application has been Disabled , Reinstall the Application" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alertView show];
    }
    else if (isAppExpired){
        UIAlertView *alertView=[[UIAlertView alloc] initWithTitle:nil message:@"Application has been Disabled , Reinstall the Application" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alertView show];
    }
    else{
        //        if ([[containerPropsDictionary objectForKey:@"NOTIFICATION"] isEqualToString:@"Y"]) {
        //            [self checkIfNotifcationRecieved:application :launchOptions];
        //        }
        appPath = [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/apps/%@",mainAppId]];
        //        NSUserDefaults *defaults= [NSUserDefaults standardUserDefaults];
        
        //        BOOL launchedBefore= [[NSUserDefaults standardUserDefaults] boolForKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",[containerPropsDictionary objectForKey:@"MAINAPPID"]]];
        //        ////Patch for Older Appzillon key mismatch
        //        BOOL launchedOnOlderVersion=[[[defaults dictionaryRepresentation] allKeys] containsObject:@"HasLaunchedOnce"];
        //        BOOL launchedOnNewVersion=[NSString stringWithFormat:@"%@.HasLaunchedOnce",[containerPropsDictionary objectForKey:@"MAINAPPID"]];
        
        if (![MiscellaneousMethods getAppHasLaunchedBefore:[containerPropsDictionary objectForKey:@"MAINAPPID"]])
        {
            [self copyAppzillonAppFiles:[containerPropsDictionary objectForKey:@"OTAREQUIRED"]];
            // [self initPushNotification];
            
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
        
        //        self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
        isiPad = NO;
        [self getMainApp];
        //        self.window.rootViewController = self.viewController;
        //        [self.window makeKeyAndVisible];
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

-(BOOL)checkForJailBrokenDevice{
#if !(TARGET_IPHONE_SIMULATOR)
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:nil message:@"This Device is jail broken. Application cannot be run" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
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
        [alertView show];
        return NO;
    }
 
    //Symbolic link verification
    struct stat s;
    if(lstat("/Applications", &s) || lstat("/var/stash/Library/Ringtones", &s) || lstat("/var/stash/Library/Wallpaper", &s)
       || lstat("/var/stash/usr/include", &s) || lstat("/var/stash/usr/libexec", &s)  || lstat("/var/stash/usr/share", &s) || lstat("/var/stash/usr/arm-apple-darwin9", &s))
    {
        if(s.st_mode & S_IFLNK){
            [alertView show];
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
        [alertView show];
        return NO;
    }
    fclose(f);
    
    NSString *stringToBeWritten = @"Hello, MasBog Here!!!";
    [stringToBeWritten writeToFile:@"/private/masbog.txt" atomically:YES encoding:NSUTF8StringEncoding error:&error];
    [[NSFileManager defaultManager] removeItemAtPath:@"/private/masbog.txt" error:nil];
    if(error == nil)
    {
        [alertView show];
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
                        [alertView show];
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
                        [alertView show];
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
                        [alertView show];
                        return NO;
                    }
                }
            }
        }
    }
#endif
    return YES;
}

-(void)getMainApp
{
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        isiPad = YES;
        self.viewController = [[AppzillonViewController alloc] initWithNibName:@"AppzillonViewController" bundle:[MiscellaneousMethods getAppBundle]];
    }
    
    else if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        
        //        self.viewController = [[AppzillonViewController alloc] initWithNibName:@"Azuritei_phoneViewController" bundle:[NSBundle bundleForClass:NSClassFromString(@"AppzillonViewController")]];
        self.viewController = [[AppzillonViewController alloc] initWithNibName:@"Azuritei_phoneViewController" bundle:[MiscellaneousMethods getAppBundle]];
    }
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



