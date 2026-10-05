//
//  APZOpenFile.m
//  Appzillon
//
//  Created by Admin on 09/10/13.
//
//

#import "APZZip.h"
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "ZipArchive.h"
#import "Logger.h"

@interface APZZip()<ZipArchiveDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong) NSString *fileJsonPath;
@property(nonatomic,strong) NSString *openFileName;
@property(nonatomic,strong) NSString *openFileType;
@property(nonatomic,strong) NSString *filePath;
@property(nonatomic,strong) NSString *appString;
@property(nonatomic,strong) NSString *pluginId;
@end

@implementation APZZip

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}


-(void)executePlugin:(NSDictionary *)jsonDict{
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    NSString *actionType = [jsonDict objectForKey:@"actionType"];
    if ([actionType isEqualToString:@"Zip"]) {
        [Logger logger_Log:@"D" :@"APZZip--Excecute Zip"];
        self.fileJsonPath =[jsonDict objectForKey:OPENFILE_FILEPATH];
        [self compressFile:jsonDict];
        
    }
    else if ([actionType isEqualToString:@"unZip"]){
        [Logger logger_Log:@"D" :@"APZZip--Excecute UnZip"];
        self.fileJsonPath =[jsonDict objectForKey:OPENFILE_FILEPATH];
        [self unZipFiles:jsonDict];
    }
    [self cleanPlugin];
    
}

-(void)compressFile:(NSDictionary *)jsonDict{
    NSError *error=nil;
    NSString *srcFilePath=[jsonDict objectForKey:@"srcFilePath"];
    NSString *destFilePath=[jsonDict objectForKey:@"destFilePath"];
    NSString*documentsDirectory = [self documentDirectory];
    NSFileManager *fileManager=[NSFileManager defaultManager];
    if (![fileManager fileExistsAtPath:srcFilePath]){
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILE_NOT_FOUND_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZZip--File not Found"];
    }else{
        NSString *actualDestFilePath = [[documentsDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/apps/%@",self.viewController.appString]] stringByAppendingPathComponent:destFilePath];
        BOOL isDir;
        if (![fileManager fileExistsAtPath:actualDestFilePath isDirectory:&isDir])
            [fileManager createDirectoryAtPath:actualDestFilePath withIntermediateDirectories:YES attributes:nil error:&error];
        if (error==nil) {
            NSString *archivedFile = [actualDestFilePath stringByAppendingString:@".zip"];
            ZipArchive *archiver = [[ZipArchive alloc] init];
            [archiver CreateZipFile2:archivedFile];
            BOOL sourceIsDir;
            [fileManager fileExistsAtPath:srcFilePath isDirectory:&sourceIsDir];
            if (sourceIsDir) {
                NSString *appzillonAppDirectory=[self.viewController.sandboxPath stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/apps/%@/",self.viewController.appString]];
                [self traverseDirectoryInsertFilesIntoZip:fileManager :srcFilePath :archiver :appzillonAppDirectory];
            }else{
                [archiver addFileToZip:srcFilePath newname:[srcFilePath lastPathComponent]];
            }
            BOOL success = [archiver CloseZipFile2];
            [fileManager removeItemAtPath:actualDestFilePath error:nil];
            if (success) {
                NSArray * resultkeys=[NSArray arrayWithObjects:FILEBROWSER_PATH,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:archivedFile,nil];
                [Logger logger_Log:@"I" :@"APZZip--Zip File SuccessFull"];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                
            } else{
                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_E_ERROR,nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                [Logger logger_Log:@"I" :@"APZZip--Zip File Failure"];
            }
            
        }
    }
}
-(void)traverseDirectoryInsertFilesIntoZip:(NSFileManager *)fileManager :(NSString*)srcFilePath :(ZipArchive*)archiver :(NSString*)appzillonAppDirectory{
    NSArray *dirContent=[fileManager contentsOfDirectoryAtPath:srcFilePath error:nil];
    if([dirContent count]==0){
        [archiver addFileToZip:srcFilePath newname:[[srcFilePath stringByReplacingOccurrencesOfString:appzillonAppDirectory withString:@""] stringByAppendingPathComponent:@"apzzipdummy"]];
    }else{
        for (NSString *element in dirContent) {
            NSString *elementFilePath=[srcFilePath stringByAppendingPathComponent:element];
            BOOL isDir;
            [fileManager fileExistsAtPath:elementFilePath isDirectory:&isDir];
            if (isDir) {
                [self traverseDirectoryInsertFilesIntoZip:fileManager :elementFilePath :archiver :appzillonAppDirectory];
            }else{
                [archiver addFileToZip:elementFilePath newname:[elementFilePath stringByReplacingOccurrencesOfString:appzillonAppDirectory withString:@""]];
                
            }
        }
    }
}
#pragma mark -zip and unzip files
-(void)unZipFiles:(NSDictionary *)jsonDict{
    NSError *error=nil;
    NSString *srcFilePath=[jsonDict objectForKey:@"srcFilePath"];
    NSString *destFilePath=[jsonDict objectForKey:@"destFilePath"];
    NSString*documentsDirectory = [self documentDirectory];
    //    if (![[NSFileManager defaultManager] fileExistsAtPath:srcFilePath]){
    NSString *actualDestFilePath = [[documentsDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/apps/%@",self.viewController.appString]] stringByAppendingPathComponent:destFilePath];
    if (![[NSFileManager defaultManager] fileExistsAtPath:actualDestFilePath])
        [[NSFileManager defaultManager] createDirectoryAtPath:actualDestFilePath withIntermediateDirectories:YES attributes:nil error:&error];
    if (error==nil) {
        ZipArchive *archiver = [[ZipArchive alloc] init];
        [archiver UnzipOpenFile:srcFilePath];
        BOOL success=[archiver UnzipFileTo:actualDestFilePath overWrite:YES];
        [archiver CloseZipFile2];
        if (success) {
            NSArray * resultkeys=[NSArray arrayWithObjects:FILEBROWSER_PATH,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:actualDestFilePath,nil];
            [Logger logger_Log:@"I" :@"APZZip--UnzipSuccessFull File SuccessFull"];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
        }else{
            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_E_ERROR,nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        }
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:STORAGE_AES_E_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
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
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end


