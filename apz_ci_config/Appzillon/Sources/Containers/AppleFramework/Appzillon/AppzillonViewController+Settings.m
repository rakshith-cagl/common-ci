//
//  AppzillonViewController+Settings.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 29/07/16.
//
//

#import "AppzillonViewController+Settings.h"
#import "CryptoUtility.h"

@implementation AppzillonViewController (Settings)
-(void)deviceInfo:(NSDictionary*)result{
    NSArray*scrProperties=[self setScreenProperty];
    NSString *orientation;
    BOOL lockRotation=true;
    if ([self.appOrientation isEqualToString:@"ANY"]) {
        if (UIInterfaceOrientationIsPortrait(self.interfaceOrientation)) {
            orientation=@"PORTRAIT";
        }else if (UIInterfaceOrientationIsLandscape(self.interfaceOrientation)){
            orientation=@"LANDSCAPE";
        }
        lockRotation=false;
    }else{
        orientation=self.appOrientation;
    }
    NSArray *resultKeys=[[NSArray alloc]initWithObjects:@"deviceId",@"hashKey1",@"hashKey2",@"deviceGroup",@"deviceOs",@"screenPpi",@"screenSize",@"deviceType",@"otaRequired",@"orientation",@"lockRotation",PLUGINID,nil];
    NSArray *resultMsg=[[NSArray alloc]initWithObjects:uniqueID,uniqueID,@"",self.deviceGroup,@"iOS",scrProperties[0],scrProperties[1],@"IOS",[shared.containerPropsDictionary objectForKey:@"OTAREQUIRED"],orientation,[NSNumber numberWithBool:lockRotation],[result objectForKey:PLUGINID],nil];
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultKeys :resultMsg]];
}

-(NSArray*)setScreenProperty{
    CGSize resolutionSize=[UIScreen mainScreen].bounds.size ;
    UIInterfaceOrientation orientation;
    NSString *systemResolution;
    if ([[[UIDevice currentDevice] systemVersion]integerValue]>=8) {
        orientation= [UIApplication sharedApplication].statusBarOrientation;
        if  (UIInterfaceOrientationIsLandscape(orientation))
        {
            systemResolution=[NSString stringWithFormat:@"%iX%i",(int)ceil(resolutionSize.height),(int)ceil(resolutionSize.width)];
        }else{
            systemResolution=[NSString stringWithFormat:@"%iX%i",(int)ceil(resolutionSize.width),(int)ceil(resolutionSize.height)];
        }
    }else{
        systemResolution=[NSString stringWithFormat:@"%iX%i",(int)ceil(resolutionSize.width),(int)ceil(resolutionSize.height)];
    }
    float scale = 1;
    if ([[UIScreen mainScreen] respondsToSelector:@selector(scale)]) {
        scale = [[UIScreen mainScreen] scale];
    }
    float SCRPPI;
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
        SCRPPI = 132 * scale;
    } else if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        SCRPPI = 163 * scale;
    } else {
        SCRPPI = 160 * scale;
    }
    NSArray *screenProperties=@[[NSString stringWithFormat:@"%f",SCRPPI],systemResolution];
    return screenProperties;
}

-(void)selectURLWhiteListQuery{
    NSString *dbPathInDocumentsDirectory=[self retrunDatabsePath];
    sqlite3 *databaseRunSQL = NULL;
    int sqlStatus = sqlite3_open([dbPathInDocumentsDirectory UTF8String],&databaseRunSQL);
    if(sqlStatus == SQLITE_OK)
    {
        const char *sql=(char *)[@"SELECT *from TB_URL_WHITELIST" UTF8String];
        sqlite3_stmt *selectStatement;
        sqlStatus = sqlite3_prepare_v2(databaseRunSQL, sql, -1, &selectStatement, NULL);
        if(sqlStatus == SQLITE_OK)
        {
            int sqlResp=sqlite3_step(selectStatement);
            if(sqlResp == SQLITE_ROW ) {
                NSMutableDictionary *outputDic=[[NSMutableDictionary alloc] init];
                NSMutableArray *outputArray=[[NSMutableArray alloc] init];
                NSString *columnName;
                for (int column=0; column<sqlite3_column_count(selectStatement); column++)
                {
                    columnName= [NSString stringWithUTF8String:(char *)sqlite3_column_name(selectStatement, column)];
                    int dataType = sqlite3_column_type(selectStatement, column);
                    
                    if (dataType==SQLITE_INTEGER) {
                        [outputDic setObject:[NSString stringWithFormat:@"%d",sqlite3_column_int(selectStatement,column) ] forKey:columnName];
                    }
                    else if(dataType == SQLITE_FLOAT){
                        [outputDic setObject:[NSString stringWithFormat:@"%f",sqlite3_column_double(selectStatement,column) ] forKey:columnName];
                    }
                    else if(dataType == SQLITE_TEXT){
                        const unsigned char *textValue =sqlite3_column_text(selectStatement,column);
                        if(textValue != NULL){
                            [outputDic setObject:[NSString stringWithUTF8String:(char *)textValue]forKey:columnName];
                        }
                        else{
                            [outputDic setObject:@"" forKey:columnName];
                        }
                    }
                    else{
                        [outputDic setObject:@"" forKey:columnName];
                    }
                }
                [outputArray addObject:outputDic];
                while (sqlite3_step(selectStatement)== SQLITE_ROW ) {
                    NSMutableDictionary *outputRowDic=[[NSMutableDictionary alloc] init];
                    for (int column=0; column<sqlite3_column_count(selectStatement); column++)
                    {
                        columnName= [NSString stringWithUTF8String:(char *)sqlite3_column_name(selectStatement, column)];
                        int dataType = sqlite3_column_type(selectStatement, column);
                        if (dataType==SQLITE_INTEGER){
                            [outputRowDic setObject:[NSString stringWithFormat:@"%d",sqlite3_column_int(selectStatement,column) ] forKey:columnName];
                        }
                        else if(dataType == SQLITE_FLOAT){
                            [outputRowDic setObject:[NSString stringWithFormat:@"%f",sqlite3_column_double(selectStatement,column) ] forKey:columnName];
                        }
                        else if(dataType == SQLITE_TEXT){
                            const unsigned char *textValue =sqlite3_column_text(selectStatement,column);
                            if(textValue != NULL){
                                [outputRowDic setObject:[NSString stringWithUTF8String:(char *)textValue]forKey:columnName];
                            }
                            else{
                                [outputRowDic setObject:@"" forKey:columnName];
                            }
                        }
                        else{
                            [outputDic setObject:@"" forKey:columnName];
                        }
                    }
                    [outputArray addObject:outputRowDic];
                }
                NSData *jsonData = [NSJSONSerialization dataWithJSONObject:outputArray options:0 error:nil];
                NSString* urlWhiteString=[[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
                [webView evaluateJavaScript:[NSString stringWithFormat:@"appzillon.plugin.urlWhiteList=%@';",urlWhiteString] completionHandler:nil];
                sqlite3_finalize(selectStatement);
                sqlite3_close(databaseRunSQL);
            }
            else{
                sqlite3_finalize(selectStatement);
                sqlite3_close(databaseRunSQL);
            }
        }
        else{
            sqlite3_close(databaseRunSQL);
        }
    }
    else{
        sqlite3_close(databaseRunSQL);
    }
}


-(void)upgradeRequired:(NSDictionary *)result{
    NSString *appPropertyPath = [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",[result objectForKey:@"appId"]]];
    NSMutableDictionary* appDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:appPropertyPath];
    NSArray *resultkeys=[NSArray arrayWithObjects:@"upgradeRequired",nil];
    //    NSArray *resultMess=[NSArray arrayWithObjects:[appDictionary objectForKey:@"upgradeRequired"],nil];
    NSArray *resultMess=[NSArray arrayWithObjects:[CryptoUtility decryptSingleValue:[appDictionary objectForKey:@"upgradeRequired"]],nil];
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMess]];
}

-(void)getUpdateActionRequired:(NSDictionary*)result{
   NSString *appPropertyPath = [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",[shared.containerPropsDictionary objectForKey:@"MAINAPPID"]]];
   NSMutableDictionary* appDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:appPropertyPath];
   NSArray *resultkeys=[NSArray arrayWithObjects:@"updateAction",nil];
   NSArray *resultMess=[NSArray arrayWithObjects:[CryptoUtility decryptSingleValue:[appDictionary objectForKey:@"updateAction"]],nil];
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMess]];
}

-(void)getUserPrefs:(NSDictionary *)result{
    NSString *userSettingsPath = [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/UserSettings.plist",appString]];
    NSMutableDictionary *userSettingsDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:userSettingsPath];
    NSError *error;
    NSDictionary *finalResponse;
    if (userSettingsDictionary) {
        finalResponse=[[NSDictionary  alloc]initWithObjects:@[userSettingsDictionary,[result objectForKey:PLUGINID]] forKeys:@[@"userprefs",PLUGINID]];
    }else{
        finalResponse=[[NSDictionary  alloc]initWithObjects:@[@"",[result objectForKey:PLUGINID]] forKeys:@[@"userprefs",PLUGINID]];
    }
    NSData *userSettingsData = [NSJSONSerialization dataWithJSONObject:finalResponse options:NSJSONWritingPrettyPrinted error:&error];
    NSString *userSettingsJSON = [[NSString alloc] initWithData:userSettingsData encoding:NSUTF8StringEncoding];
    [webView evaluateJavaScript:[NSString stringWithFormat:@"%@(%@);",JSCALLBACKMEHTOD,userSettingsJSON] completionHandler:nil];
}

-(void)getUserPreference:(NSDictionary *)jsonDict{
    NSString *key = [jsonDict objectForKey:@"key"];
    NSString *path = [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/UserSettings.plist",appString]];
    NSMutableDictionary *dictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:path];
    NSDictionary *userSettings=[dictionary objectForKey:@"userprefs"];
    NSString *valueForTheKey;
    if(userSettings != nil){
        valueForTheKey  = [userSettings objectForKey:key];
        if (valueForTheKey != nil){
            NSArray * resultkeys=[NSArray arrayWithObjects:@"value",nil];
            NSArray * resultMsg=[NSArray arrayWithObjects:valueForTheKey,nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[jsonDict objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
        }else{
            NSArray * resultkeys=[NSArray arrayWithObjects:@"value",nil];
            NSArray * resultMsg=[NSArray arrayWithObjects:@"null",nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[jsonDict objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
        }
    }else
    {
        NSArray * resultkeys=[NSArray arrayWithObjects:@"value",nil];
        NSArray * resultMsg=[NSArray arrayWithObjects:@"null",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[jsonDict objectForKey:PLUGINID] :false :false :resultkeys :resultMsg]];
    }
}
-(void)setUserPrefs:(NSDictionary *)result{
    NSString *otaPath = [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/UserSettings.plist",appString]];
    NSMutableDictionary *dictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:otaPath];
    NSMutableDictionary *userSetttings = [[NSMutableDictionary alloc]init];
    for (int i=0; i <result.count; i++) {
        [userSetttings setObject:[result objectForKey:[result.allKeys objectAtIndex:i]] forKey:[result.allKeys objectAtIndex:i]];
    }
    [dictionary setObject:userSetttings forKey:@"userprefs"];
    [dictionary writeToFile:otaPath atomically:YES];
    NSArray *resultkeys=nil;
    NSArray *resultMess=nil;
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMess]];
}

-(void)setUserPreference:(NSDictionary *)jsonDict{
    NSString *key = [jsonDict objectForKey:@"key"];
    NSString *value = [jsonDict objectForKey:@"value"];
    NSString *path = [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/UserSettings.plist",appString]];
    NSMutableDictionary *dictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:path];
    if ([dictionary count]==0) {
        NSMutableDictionary *userprefsSubDict=[[NSMutableDictionary alloc]init];
        [dictionary setObject:userprefsSubDict forKey:@"userprefs"];
    }
    NSMutableDictionary *userSettings=[dictionary objectForKey:@"userprefs"];
    [userSettings setObject:value forKey:key];
    [dictionary setObject:userSettings forKey:@"userprefs"];
    [dictionary writeToFile:path atomically:YES];
    NSArray * resultkeys=nil;
    NSArray * resultMsg=nil;
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[jsonDict objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
}

#pragma mark- DeviceDateFormat
-(void)getDeviceDateFormat{
    NSDate *date = [NSDate date];
    NSDateFormatter *dateFormattter = [[NSDateFormatter alloc] init];
    [dateFormattter setDateStyle:NSDateFormatterMediumStyle];
    [dateFormattter setLocale:[NSLocale currentLocale]];
    NSString *Format= [dateFormattter dateFormat];
    NSString *dateString = [dateFormattter stringFromDate:date];
    [webView evaluateJavaScript:[NSString stringWithFormat:@"appzillon.plugin.widgetdateformat='%@';",Format] completionHandler:nil];
}

#pragma mark- Layout Mapping
-(void)getDeviceMapping{
    appString=[shared.containerPropsDictionary objectForKey:@"MAINAPPID"];
    NSString* pathSettingsJson;
    if ([[shared.containerPropsDictionary objectForKey:@"OTAREQUIRED"]isEqualToString:@"Y"]) {
        pathSettingsJson= [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/screens/config/devicegroups.json",appString]];
    }else{
        pathSettingsJson=[[MiscellaneousMethods getAppBundle] pathForResource:@"devicegroups" ofType:@"json" inDirectory:[NSString stringWithFormat:@"/Assets/apps/%@/screens/config",appString]];
    }
    if([[NSFileManager defaultManager] fileExistsAtPath:pathSettingsJson]){
        NSString *fileContent = [[NSString alloc] initWithContentsOfFile:pathSettingsJson encoding:NSUTF8StringEncoding error:nil];
        NSError *error=nil;
        NSDictionary * jsonDictionary=[NSJSONSerialization JSONObjectWithData:[fileContent  dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:&error];
        if (error==nil) {
            NSLog(@"---Copied JSON from Appzillon---");
        }else{
            NSLog(@"--No JSON File From Appzillon--");
        }
        NSDictionary *data = [jsonDictionary objectForKey:@"deviceGroups"];
        CGFloat width ;
        CGFloat height;
        if ([[[UIDevice currentDevice] systemVersion]integerValue]>=8) {
            UIInterfaceOrientation orientation= [UIApplication sharedApplication].statusBarOrientation;
            if  (UIInterfaceOrientationIsLandscape(orientation))
            {
                height=[[UIScreen mainScreen] bounds].size.width;
                width=[[UIScreen mainScreen] bounds].size.height;
            }
            else
            {
                height=[[UIScreen mainScreen] bounds].size.height;
                width=[[UIScreen mainScreen] bounds].size.width;
            }
        }else{
            width=[[UIScreen mainScreen] bounds].size.width;
            height=[[UIScreen mainScreen] bounds].size.width;
        }
        NSInteger temp=0,count=0;
        for (NSDictionary *device in data)
        {
            NSString *heightStr = [device objectForKey:@"height"];
            NSString *widthStr = [device objectForKey:@"width"];
            NSInteger jsonHeight=[heightStr intValue];
            NSInteger jsonWidth=[widthStr intValue];
            NSInteger deltaHeight=height-jsonHeight;
            NSInteger deltaWidth=width-jsonWidth;
            if (deltaWidth==0 && deltaHeight==0) {
                self.deviceGroup=[device objectForKey:@"name"];
                self.appOrientation=[device objectForKey:@"orientation"];
                break;
            }
            if (deltaWidth<0) {
                deltaWidth=0-deltaWidth;
            }
            if (deltaHeight<0) {
                deltaHeight=0-deltaHeight;
            }
            NSInteger deltaWidthHeight=deltaWidth+deltaHeight;
            if (deltaWidthHeight<0) {
                deltaWidthHeight=0-deltaWidthHeight;
            }
            if (count==0) {
                temp=deltaWidthHeight;
                count++;
                self.deviceGroup=[device objectForKey:@"name"];
                self.appOrientation=[device objectForKey:@"orientation"];
            }
            if (temp>deltaWidthHeight) {
                temp=deltaWidthHeight;
                self.deviceGroup=[device objectForKey:@"name"];
                self.appOrientation=[device objectForKey:@"orientation"];
            }
        }
    }
}

- (NSString *)getUUID
{
    UUID = [[NSUserDefaults standardUserDefaults] objectForKey:@"uniqueID"];
    if (!UUID)
    {
        CFUUIDRef theUUID = CFUUIDCreate(NULL);
        CFStringRef string = CFUUIDCreateString(NULL, theUUID);
        CFRelease(theUUID);
        UUID = [(__bridge NSString*)string stringByReplacingOccurrencesOfString:@"-"withString:@""];
        [[NSUserDefaults standardUserDefaults] setValue:UUID forKey:@"uniqueID"];
        if(string != NULL){
            CFRelease(string);
        }
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    else
    {
        NSLog(@"--Returning UUID stored--");
    }
    return UUID;
}


- (void)getIPAddress:(NSDictionary *)result
{
    ipAddress =[self getIPAddressCall];
    if ([ipAddress isEqualToString:@"error"]) {
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray * resultMsg=[NSArray arrayWithObjects:GPS_NETWORK_OFF,nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :resultkeys :resultMsg]];
    }else{
        NSArray * resultkeys=[NSArray arrayWithObjects:@"ip",nil];
        NSArray * resultMsg=[NSArray arrayWithObjects:ipAddress,nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
    }
}
- (NSString *)getIPAddressCall
{
    NSString *address = @"error";
    struct ifaddrs *interfaces = NULL;
    struct ifaddrs *temp_addr = NULL;
    int success = 0;
    success = getifaddrs(&interfaces);
    if (success == 0) {
        temp_addr = interfaces;
        while(temp_addr != NULL) {
            if(temp_addr->ifa_addr->sa_family == AF_INET) {
                if([[NSString stringWithUTF8String:temp_addr->ifa_name] isEqualToString:@"en0"]) {
                    address = [NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr)];
                }
            }
            temp_addr = temp_addr->ifa_next;
        }
    }
    freeifaddrs(interfaces);
    return address;
    
    
}
-(NSString *)retrunDatabsePath{
    //    NSString *offlineDB=[web stringByEvaluatingJavaScriptFromString:@"localStorage.getItem('OfflineDB');"];
    [webView evaluateJavaScript:@"localStorage.getItem('OfflineDB');" completionHandler:nil];
    
    NSString *offlineDB = @"";
    NSString *dbPathInDocumentsDirectory =[[NSString alloc]initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/sqlite/%@.sqlite3",self.appString,offlineDB]];
    return dbPathInDocumentsDirectory;
}

-(void)remoteDebug:(NSDictionary *)debugInfo{
    NSString *remoteDebugStatus=[debugInfo objectForKey:@"debug"];
    [webView evaluateJavaScript:[NSString stringWithFormat:@"appzillon.user.sendLog='%@'",remoteDebugStatus] completionHandler:nil];
    NSString *pathToDictionary= [[NSString alloc] initWithString:[self.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",appString]];
    NSMutableDictionary *settingsDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:pathToDictionary];
    [settingsDictionary setObject:[CryptoUtility encryptSingleValue:remoteDebugStatus] forKey:@"SENDLOG"];
    [settingsDictionary writeToFile:pathToDictionary atomically:NO];
}
@end




