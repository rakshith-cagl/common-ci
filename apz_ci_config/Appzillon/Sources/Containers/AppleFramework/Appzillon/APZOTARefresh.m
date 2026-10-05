//
//  APZOTARefresh.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 31/12/14.
//
//

#import "APZOTARefresh.h"
#import "APZJsonUtil.h"
#import "AFNetworking/AFNetworking.h"
#import "AppzillonViewController.h"
#import "Constants.h"
#import "Logger.h"
#import "CallServer.h"
#import "CryptoUtility.h"
#import "Base64.h"
@interface APZOTARefresh()
@property(nonatomic,strong)NSString *OTAStatus;
@property(nonatomic,strong)NSString *containerApp;
@property(nonatomic,strong)NSString *containerMenu;
@property(nonatomic,strong)NSString *OTAFileDictPath;
@property(nonatomic,strong)NSMutableDictionary *settingsDictionary;
@property(nonatomic,strong)NSMutableDictionary *OTAFileDict;
@property(nonatomic,strong)NSString *path;
@property(assign)BOOL OTACalled;
@property(nonatomic,weak)WKWebView *web;
@property(strong,nonatomic) NSString *upgradedVersion;
@property(strong,nonatomic)NSString *APPID;
@property(strong,nonatomic)NSMutableArray *filePathArray;
@property(strong,nonatomic)NSMutableArray *deleteFilePathArray;
@property(strong,nonatomic)NSString *pluginId;
@property(strong,nonatomic)NSMutableDictionary* containerPropsDictionary;
@property (strong, nonatomic) AppzillonViewController *viewController;
@property(assign) BOOL globalFileCopySuccess;


@end
int counter=0;
@implementation APZOTARefresh
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.web = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZOTARefresh--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    _APPID=[jsonDict objectForKey:@"appId"];
    NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    NSString *documentsDirectory = [[NSString alloc] initWithString:[paths objectAtIndex:0]];
    self.path= [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",_APPID]];
    self.settingsDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:self.path];
    NSString *installedVersion=[CryptoUtility decryptSingleValue:[self.settingsDictionary objectForKey:@"appVersion"]];
    NSString* containerPropsPath = [[MiscellaneousMethods getAppBundle] pathForResource:@"containerprops" ofType:@"plist"];
    self.containerPropsDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:containerPropsPath];
    NSString *parentAppID=[self.containerPropsDictionary objectForKey:@"MAINAPPID"];
    [self checkForUpdates:_APPID :parentAppID :installedVersion];
}

-(void)checkForUpdates:(NSString *)appId :(NSString*)parentAppId :(NSString*)appVersion{
    self.settingsDictionary = [[NSMutableDictionary alloc] initWithContentsOfFile:self.path];
    NSString * sessionID= @" ";
    NSString * deviceID= @"IOS";
    NSString* containerPropsPath = [[MiscellaneousMethods getAppBundle] pathForResource:@"containerprops" ofType:@"plist"];
    self.containerPropsDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:containerPropsPath];
    NSString* mainAppId=[self.containerPropsDictionary objectForKey:@"MAINAPPID"];
    if (!appVersion) {
        appVersion=@"0";
    }
    if (!parentAppId) {
        parentAppId=mainAppId;
    }
    NSString *ipAddress=self.viewController.ipAddress;
    NSMutableDictionary *appzillonHeader=[[NSMutableDictionary alloc] init];
    [appzillonHeader setObject:@"true" forKey:@"preLogin"];
    [appzillonHeader setObject:parentAppId forKey:@"appId"];
    [appzillonHeader setObject:deviceID forKey:@"deviceId"];
    [appzillonHeader setObject:ipAddress forKey:@"origination"];
    [appzillonHeader setObject:@"APPZILLON" forKey:@"source"];
    [appzillonHeader setObject:sessionID forKey:@"sessionId"];
    [appzillonHeader setObject:@"IOS" forKey:@"userId"];
    [appzillonHeader setObject:[NSNumber numberWithBool:true] forKey:@"status"];
    [appzillonHeader setObject:@"dsd" forKey:@"requestKey"];
    [appzillonHeader setObject:@"appzillonGetAppFile" forKey:@"interfaceId"];
    [appzillonHeader setObject:@"login" forKey:@"screenId"];
    NSMutableDictionary *appzillonAppFilesRequest=[[NSMutableDictionary alloc] init];
    [appzillonAppFilesRequest setObject:appId forKey:@"appId"];
    [appzillonAppFilesRequest setObject:appVersion forKey:@"appVersion"];
    [appzillonAppFilesRequest setObject:@"IOS" forKey:@"os"];
    NSMutableDictionary *appzillonBody=[[NSMutableDictionary alloc] init];
    [appzillonBody setObject:appzillonAppFilesRequest forKey:@"appzillonAppFilesRequest"];
    
    NSMutableDictionary *appzillonRequest=[[NSMutableDictionary alloc] init];
    [appzillonRequest setObject:appzillonHeader forKey:@"appzillonHeader"];
    [appzillonRequest setObject:appzillonBody forKey:@"appzillonBody"];
    
//    NSString *ipurl = [CryptoUtility decryptSingleValue:[self.settingsDictionary objectForKey:@"serverUrl"]];
    NSString *ipurl = self.viewController.APZServerURL;
    
    [CallServer callServerWithRequest:appzillonRequest :ipurl :@"afRequestSuccessfulForUpdate" :@"afRequestFailureForUpdate" :self :_APPID :self.viewController];
    [Logger logger_Log:@"I" :@"APZOTARefresh--appzillonAppFilesRequest sent"];
}


-(void)afRequestSuccessfulForUpdate:(NSMutableDictionary *)jsonDict
{
    NSMutableDictionary * jsonHeader = (NSMutableDictionary *) [ jsonDict objectForKey:@"appzillonHeader"];
    BOOL statusValue = [[jsonHeader objectForKey:@"status"] boolValue];
    if (statusValue) {
        NSDictionary *jsonBody=[jsonDict objectForKey:@"appzillonBody"];
        NSString *appID=[jsonBody objectForKey:@"appId"];
        NSArray *updateFileListDict=[jsonBody objectForKey:appID];
        for(id fileName_Path in updateFileListDict) {
            NSMutableDictionary *fileNameAndPath=[[NSMutableDictionary alloc]initWithDictionary:fileName_Path];
            NSString *appVersion=[fileNameAndPath objectForKey:@"appVersion"];
            self.upgradedVersion=appVersion;
            if (appVersion) {
                NSString *filePath=[fileNameAndPath objectForKey:@"filepath"];
                [fileNameAndPath setObject:@"NO" forKey:@"requested"];
                [fileNameAndPath setObject:appID forKey:@"appID"];
                NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
                NSString *documentsDirectory = [[NSString alloc] initWithString:[paths objectAtIndex:0]];
                self.OTAFileDictPath= [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/apps/%@/plist/OTAFiles.plist",appID]];
                NSFileManager *fileManager = [NSFileManager defaultManager];
                if (![fileManager fileExistsAtPath:self.OTAFileDictPath]) {
                    self.OTAFileDictPath= [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/OTAFiles.plist"]];
                }
                self.OTAFileDict = [[NSMutableDictionary alloc] initWithContentsOfFile:self.OTAFileDictPath];
                [self.OTAFileDict setObject:fileNameAndPath forKey:filePath];
                [self.OTAFileDict writeToFile:self.OTAFileDictPath atomically: YES];
            }
        }
        _filePathArray=[[NSMutableArray alloc]initWithArray:[self.OTAFileDict  allKeys]];
        _deleteFilePathArray = [[NSMutableArray alloc] init];
        self.globalFileCopySuccess=YES;
        [self callToOTADownload];
    }
}

-(void)afRequestFailureForUpdate:(NSMutableDictionary *)responseString{
    NSString *errorString=[NSString stringWithFormat:@"APZOTARefresh--%@",responseString];
    [Logger logger_Log:@"E" :errorString];
}

-(void)callToOTADownload{
    if ([_filePathArray count]!=0) {
        NSString* filePathKey=[_filePathArray lastObject];
        NSMutableDictionary *fileDetails=[self.OTAFileDict objectForKey:filePathKey];
        NSString *fileName=[fileDetails objectForKey:@"filename"];
        NSString *appVersion=[fileDetails objectForKey:@"appVersion"];
        NSString *osType=[fileDetails objectForKey:@"os"];
        NSString *appID=[fileDetails objectForKey:@"appID"];
        NSString *action=[fileDetails objectForKey:@"action"];
        if ([osType isEqualToString:@"IOS"]||[osType isEqualToString:@"ALL"]) {
            if ([action isEqualToString:@"DELETE"]) {
                //                    [self deleteTheFileLocation:filePathKey];
                [_deleteFilePathArray addObject:filePathKey];
                [_filePathArray removeLastObject];
                if ([_filePathArray count]!=0){
                    [self callToOTADownload];
                }else{
                    NSString *documentsDirectory = [[NSString alloc] initWithString:[self documentDirectory]];
                    [self moveContentFromOTATEMP:[documentsDirectory stringByAppendingPathComponent:@"/Assets/apps/OTATEMP"]];
                    if (self.globalFileCopySuccess) {
                        if (_deleteFilePathArray.count != 0) {
                            [self deleteTheFileLocation:[_deleteFilePathArray lastObject]];
                        }
                        [self removeOtaTempAndUpdateAppVersion];
                    }
                }
            }else{
                [self downloadOTA:fileName:appVersion:appID:osType:filePathKey];
            }
        }
    }
}


-(void)downloadOTA:(NSString*)fileName :(NSString *)appVersion :(NSString*)appId :(NSString*)osType :(NSString *)filePath
{
    NSString* containerPropsPath = [[MiscellaneousMethods getAppBundle] pathForResource:@"containerprops" ofType:@"plist"];
    self.containerPropsDictionary=[[NSMutableDictionary alloc]initWithContentsOfFile:containerPropsPath];
    NSString *parentAppId=[self.containerPropsDictionary objectForKey:@"MAINAPPID"];
    NSMutableDictionary *appzillonHeader=[[NSMutableDictionary alloc] init];
    [appzillonHeader setObject:@"true" forKey:@"preLogin"];
    [appzillonHeader setObject:parentAppId forKey:@"appId"];
    [appzillonHeader setObject:@"IOS" forKey:@"deviceId"];
    [appzillonHeader setObject:@"" forKey:@"sessionId"];
    [appzillonHeader setObject:@"IOS" forKey:@"userId"];
    [appzillonHeader setObject:[NSNumber numberWithBool:true] forKey:@"status"];
    [appzillonHeader setObject:@"" forKey:@"requestKey"];
    [appzillonHeader setObject:@"appzillonOTAFileDownloadReq" forKey:@"interfaceId"];
    [appzillonHeader setObject:@"true" forKey:@"async"];
    NSMutableDictionary *appzillonOTAFileDownloadReq=[[NSMutableDictionary alloc] init];
    [appzillonOTAFileDownloadReq setObject:appId forKey:@"appId"];
    [appzillonOTAFileDownloadReq setObject:fileName forKey:@"fileName"];
    [appzillonOTAFileDownloadReq setObject:osType forKey:@"os"];
    [appzillonOTAFileDownloadReq setObject:appVersion forKey:@"appVersion"];
    [appzillonOTAFileDownloadReq setObject:filePath forKey:@"filepath"];
    NSMutableDictionary *appzillonBody=[[NSMutableDictionary alloc] init];
    [appzillonBody setObject:appzillonOTAFileDownloadReq forKey:@"appzillonOTAFileDownloadReq"];
    NSMutableDictionary *appzillonRequest=[[NSMutableDictionary alloc] init];
    [appzillonRequest setObject:appzillonHeader forKey:@"appzillonHeader"];
    [appzillonRequest setObject:appzillonBody forKey:@"appzillonBody"];
    
//    NSString *serverURL = [CryptoUtility decryptSingleValue:[self.settingsDictionary objectForKey:@"serverUrl"]];
    NSString *serverURL = self.viewController.APZServerURL;
    
    [CallServer callServerWithRequest:appzillonRequest :serverURL :@"downloadRequestSuccess" :@"downloadRequestFail" :self :_APPID :self.viewController];
}

-(void)downloadRequestSuccess:(NSMutableDictionary *)serverResponseDictionary{
    BOOL status=[[[serverResponseDictionary objectForKey:FILEDOWNLOAD_RES_HEADER] objectForKey:FILEDOWNLOAD_RES_STATUS]boolValue];
    if (status) {
        serverResponseDictionary=[serverResponseDictionary objectForKey:FILEDOWNLOAD_RES_BODY];
        serverResponseDictionary=[serverResponseDictionary objectForKey:@"appzillonOTAFileDownloadResponse"];
        NSData *fileData=[Base64 decode:[serverResponseDictionary objectForKey:FILEDOWNLOAD_RES_FILE]];
        NSString *fileName=[serverResponseDictionary objectForKey:FILEDOWNLOAD_RES_FILENAME];
        NSArray *pathArray= [NSArray arrayWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
        NSString *docPath;
        NSString *filePath=[serverResponseDictionary objectForKey:@"filePath"];
        if (filePath) {
            docPath =[pathArray objectAtIndex:0];
            filePath=[filePath stringByDeletingLastPathComponent];
            docPath=[[docPath stringByAppendingString:@"/Assets/apps/OTATEMP/"]stringByAppendingString:filePath];
            NSFileManager *fileManager=[NSFileManager defaultManager];
            if (![fileManager fileExistsAtPath:docPath]) {
                [fileManager createDirectoryAtPath:docPath withIntermediateDirectories:YES attributes:nil error:nil];
            }
        }else{
            [Logger logger_Log:@"E" :@"APZOTARefresh--path did not come from server"];
        }
        docPath=[NSString stringWithFormat:@"%@/%@",docPath,fileName];
//        if ([fileData writeToFile:docPath atomically:YES]) {
        if ([MiscellaneousMethods writeData:fileData toFile:docPath]) {
            filePath=[filePath stringByAppendingPathComponent:fileName];
            [_filePathArray removeLastObject];
            if ([_filePathArray count]!=0) {
                [self callToOTADownload];
            }
            else{
                NSString *documentsDirectory = [[NSString alloc] initWithString:[self documentDirectory]];
                [self moveContentFromOTATEMP:[documentsDirectory stringByAppendingPathComponent:@"/Assets/apps/OTATEMP/"]];
                if (_globalFileCopySuccess) {
                    if (_deleteFilePathArray.count != 0) {
                        [self deleteTheFileLocation:[_deleteFilePathArray lastObject]];
                    }
                    [self removeOtaTempAndUpdateAppVersion];
                }
            }
        }else{
            [self downloadRequestFail:@"File Download error"errorCode:FILE_COULD_NOT_CREATED];
            NSString *errorString=[NSString stringWithFormat:@"APZOTARefresh--%@",FILE_COULD_NOT_CREATED];
            [Logger logger_Log:@"E" :errorString];
        }
    }else{
        NSString *documentsDirectory = [[NSString alloc] initWithString:[self documentDirectory]];
        if([[NSFileManager defaultManager] fileExistsAtPath: [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/apps/OTATEMP"]]] == YES){
            [[NSFileManager defaultManager] removeItemAtPath:[[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/apps/OTATEMP"]] error:nil];
        }
        [self downloadRequestFail:@"File Download error"errorCode:[[serverResponseDictionary objectForKey:@"appzillonErrors"][0]objectForKey:@"errorCode"]];
    }
}

-(void)downloadRequestFail:(NSString *)serverErrorMsg errorCode:(NSString *)errorCode{
    NSString *errorString=[NSString stringWithFormat:@"APZOTARefresh--%@",serverErrorMsg];
    [Logger logger_Log:@"E" :errorString];
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:errorCode,nil];
    [MiscellaneousMethods jsLayerCall:self.web :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [self cleanPlugin];
}


-(void)removeOtaTempAndUpdateAppVersion{
    NSString *documentsDirectory = [[NSString alloc] initWithString:[self documentDirectory]];
    NSFileManager *fileManager =[NSFileManager defaultManager];
    if ([fileManager fileExistsAtPath: [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/apps/OTATEMP"]]] == YES){
        [fileManager removeItemAtPath:[[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/apps/OTATEMP"]] error:nil];
    }
    NSString *path = [[NSString alloc] initWithString:[documentsDirectory stringByAppendingFormat:@"/Assets/apps/%@/plist/AppProperties.plist",_APPID]];
    NSMutableDictionary* appPropDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:path];
    //    [self.web stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"appzillon.plugin.store('upgradeRequired','N');"]];
    [appPropDictionary setObject:[CryptoUtility encryptSingleValue:@"N"] forKey:@"upgradeRequired"];
    [appPropDictionary setObject:[CryptoUtility encryptSingleValue:self.upgradedVersion] forKey:@"appVersion"];
    [appPropDictionary writeToFile:path atomically:YES];
    self.OTAFileDict = [[NSMutableDictionary alloc] initWithContentsOfFile:self.OTAFileDictPath];
    [self.OTAFileDict removeAllObjects];
    
    NSArray *resultkeys=[NSArray arrayWithObjects:@"message",nil];
    NSArray *result=[NSArray arrayWithObjects:@"success",nil];
    [MiscellaneousMethods jsLayerCall:self.web :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [self cleanPlugin];
}

-(void)moveContentFromOTATEMP:(NSString*)tempDirectory{
    NSFileManager *fileManager =[NSFileManager defaultManager];
    if ([fileManager fileExistsAtPath:tempDirectory]) {
        NSArray *contentAtContentPath=[fileManager contentsOfDirectoryAtPath:tempDirectory error:nil];
        if ([contentAtContentPath count]==0) {
            NSArray *paths = [tempDirectory componentsSeparatedByString:@"OTATEMP"];
            NSString *originalFilePath = [[paths objectAtIndex:0] stringByAppendingPathComponent:[paths objectAtIndex:1]];
            NSError *removeError;
            NSError *copyError;
            [fileManager removeItemAtPath:originalFilePath error:&removeError];
            [fileManager copyItemAtPath:tempDirectory toPath:originalFilePath error:&copyError];
            BOOL localFileCopySuccess;
            if (removeError||copyError ) {
                if ([[removeError localizedDescription] containsString:@"couldn’t be removed."]) {
                    //item is not there in the path, its a newly downloaded item
                }else{
                    localFileCopySuccess = NO;
                    self.globalFileCopySuccess = self.globalFileCopySuccess && localFileCopySuccess;
                    return;
                }
            }else{
                localFileCopySuccess =YES;
            }
        }else{
            NSArray* tempDirectoryContent = [fileManager contentsOfDirectoryAtPath:tempDirectory error:nil];
            for (NSString* content in tempDirectoryContent) {
                NSString *contentPath =[tempDirectory stringByAppendingPathComponent:content];
                [self moveContentFromOTATEMP:contentPath];
            }
        }
    }
}
-(void)deleteTheFileLocation:(NSString *)filePathKay
{
    NSArray *pathArray= [NSArray arrayWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    NSString *docPath;
    docPath =[pathArray objectAtIndex:0];
    docPath=[[docPath stringByAppendingString:@"/Assets/apps/"]stringByAppendingString:filePathKay];
    NSFileManager *fileManager=[NSFileManager defaultManager];
    NSError *error;
    if ([fileManager fileExistsAtPath:docPath]) {
        [fileManager removeItemAtPath:docPath error:&error];
        if (error==NULL) {
            NSLog(@"fileDeleted");
            [_deleteFilePathArray removeLastObject];
            if (_deleteFilePathArray.count != 0) {
                [self deleteTheFileLocation:[_deleteFilePathArray lastObject]];
            }
        }else{
            NSLog(@"somthingWent Wrong");
        }
    }
}

-(NSString*)documentDirectory{
    NSArray *paths = NSSearchPathForDirectoriesInDomains
    (NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    return documentsDirectory;
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZOTARefresh--Done"];
    self.web = nil;
    self.OTAFileDict=nil;
    self.settingsDictionary=nil;
    self.filePathArray = nil;
    self.deleteFilePathArray = nil;
    [self.delegate donePlugin:self];
}
@end



