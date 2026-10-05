//
//  APZFileBrowser.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZFileBrowser.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "AppzillonViewController.h"
#import "ImageGalleryController.h"
#import "MusicGalleryController.h"
#import "VideoGalleryController.h"
#import "AppBrowserController.h"
#import "FileLocationController.h"


@interface APZFileBrowser()<ImageGalleryDoneDelegate,MusicGalleryDoneDelegate,VideoGalleryDoneDelegate,AppBrowserDelegate,FileLocationSelectedDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong) NSString *fileBrowserOpenFile;
@property(nonatomic,strong) NSString *filterByExtensions;
@property(nonatomic,strong)UIImagePickerController *imagePickerController;
@property(nonatomic,strong)UINavigationController *navigationController;
@property(nonatomic,strong)NSMutableDictionary *appBrowserDict;
@property(nonatomic,strong) ImageGalleryController *imageGallery;
@property(nonatomic,strong) MusicGalleryController *musicGallery;
@property(nonatomic,strong)VideoGalleryController *videoGallery;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZFileBrowser

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"FileBrowser--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    [self processFileBrowser:jsonDict];
}

#pragma mark - FILEBROWSER
-(void)processFileBrowser:(NSDictionary *)jsonDict{
    NSString *fileCategory = [jsonDict objectForKey:FILEBROWSER_FILECATEGORY];
    if([fileCategory isEqualToString:FILEBROWSER_FILECATEGORY_IMAGE]){
        self.imageGallery = [[ImageGalleryController alloc] initPlugin:self.webView :jsonDict];
        [self.imageGallery executePlugin:jsonDict];
        self.imageGallery.delegate = self;
    }
    else if([fileCategory isEqualToString:FILEBROWSER_FILECATEGORY_AUDIO]){
        self.musicGallery = [[MusicGalleryController alloc] initPlugin:self.webView :jsonDict];
        [self.musicGallery executePlugin:jsonDict];
        self.musicGallery.delegate = self;
    }
    else if([fileCategory isEqualToString:FILEBROWSER_FILECATEGORY_VIDEO]){
        self.videoGallery = [[VideoGalleryController alloc] initPlugin:self.webView :jsonDict];
        [self.videoGallery executePlugin:jsonDict];
        self.videoGallery.delegate = self;
    }
    else if([fileCategory isEqualToString:FILEBROWSER_FILECATEGORY_DEFAULT]
            || ([fileCategory isEqualToString:@""]))
    {
        if ([fileCategory isEqualToString:FILEBROWSER_FILECATEGORY_DEFAULT]) {
            [jsonDict setValue:@"" forKey:@"location"];
            [self appBrowser:jsonDict];
        }else{
            [self appBrowser:jsonDict];
        }
    }
    else{
        [self fileBrowserError:INVALID_FILE_CATAGORY errMessage:[NSString stringWithFormat:@"Invalid FileCategory"]];
    }
}

#pragma mark-Image Gallery Browser
-(void)doneImageGallery:(id)plugin{
    NSString *msgString=[NSString stringWithFormat:@"FileBrowser--Done--%@",NSStringFromClass([plugin class])];
    [Logger logger_Log:@"D" :msgString];
}
-(void)doneImageGalleryWithOrientation:(UIInterfaceOrientation)orientation{
    [self cleanPlugin:orientation];
}

#pragma mark-Music Gallery Browser
-(void)doneMusicGallery:(id)plugin{
    NSString *msgString=[NSString stringWithFormat:@"FileBrowser--Done--%@",NSStringFromClass([plugin class])];
    [Logger logger_Log:@"D" :msgString];
}
-(void)doneMusicGalleryWithOrientation:(UIInterfaceOrientation)orientation{
    [self cleanPlugin:orientation];
}


#pragma mark-Video Gallery Browser
-(void)doneVideoGallery:(id)plugin{
    NSString *errorString=[NSString stringWithFormat:@"FileBrowser--Done--%@",NSStringFromClass([plugin class])];
    [Logger logger_Log:@"D" :errorString];
}
-(void)doneVideoGalleryWithOrientation :(UIInterfaceOrientation)orientation{
    [self cleanPlugin:orientation];
}

-(NSString *)DocumentsDirectory{
    NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    return [[NSString alloc] initWithString:[paths objectAtIndex:0]];
}

#pragma mark-SandBox Browsing
-(void)appBrowser:(NSDictionary *)jsonDict{
    NSString *documentsDirectory = nil;
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *fileOpenType = [jsonDict objectForKey:FILEBROWSER_OPENFILE];
    if([fileOpenType isEqualToString:@""]){
        fileOpenType = @"N";
    }
    NSString *fileFilters = [jsonDict objectForKey:FILEBROWSER_FILTER];
    NSString *fileLocation = [jsonDict objectForKey:FILEBROWSER_LOCATION];
    NSArray *fileLocArr = [fileLocation componentsSeparatedByString:FILEBROWSER_LOCATION_SEPERATOR];
    BOOL isRootLocation = NO;
    for(NSString *path in fileLocArr){
        if([path hasPrefix:FILEBROWSER_LOCATION_IOS]){
            if([path isEqualToString:FILEBROWSER_LOCATION_IOS]){
                isRootLocation = YES;
            }
            else{
                fileLocation = [path lastPathComponent];
                isRootLocation = NO;
            }
            break;
        }
    }
    if([fileLocation isEqualToString:@""] || isRootLocation || [fileLocation isEqualToString:FILEBROWSER_LOCATION_IOS])
    {
        self.appBrowserDict = [self getAllDirectory:fileFilters];
        if(self.appBrowserDict.count > 0)
        {
            AppBrowserController *browserController = [[AppBrowserController alloc]initWithStyle:UITableViewStyleGrouped];
            browserController.delegate = self;
            [browserController initDirectories:self.appBrowserDict openType:fileOpenType filters:fileFilters];
            browserController.appID=self.viewController.appString;
            UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:browserController];
            [navigationController setModalPresentationStyle:UIModalPresentationFullScreen];
            [self.viewController presentViewController:navigationController animated:YES completion:nil];
        }
        else
        {
            [self fileBrowserError:FILEBROWSER_APP_FILES_EMPTY errMessage:[NSString stringWithFormat:@"No Files Present in Sandbox"]];
        }
    }
    else{
        
        documentsDirectory = [self appDocumentsDirectory];
        documentsDirectory = [documentsDirectory stringByAppendingPathComponent:fileLocation];
        BOOL isDir;
        BOOL isLocationPresent = [fileManager fileExistsAtPath:documentsDirectory isDirectory:&isDir];
        if (isDir && isLocationPresent)
        {
            NSArray *locationFiles = [self getLocationFiles:documentsDirectory filters:fileFilters];
            if(locationFiles.count > 0){
                FileLocationController *fileController = [[FileLocationController alloc]init];
                fileController.delegate = self;
                [fileController initFiles:locationFiles directory:documentsDirectory openType:fileOpenType];
                UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:fileController];
                [navigationController setModalPresentationStyle:UIModalPresentationFullScreen];
                [self.viewController presentViewController:navigationController animated:YES completion:nil];
            }
            else{
                [self fileBrowserError:FILE_NOT_FOUND_ERROR errMessage:[NSString stringWithFormat:@"No Files Present in Location = %@",fileLocation]];
            }
        }
        else
        {
            [self fileBrowserError:FILE_NOT_FOUND_ERROR errMessage:[NSString stringWithFormat:@"No Location %@ Present in Sandbox",fileLocation]];
        }
    }
}

#pragma mark-AppBrowser Delegates
-(void)selectedFile:(NSString *)fileName path:(NSString *)filePath :(UIInterfaceOrientation)interfaceOrienation{
    NSArray *resultkeys=[NSArray arrayWithObjects:FILEBROWSER_PATH,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",filePath],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [self cleanPlugin:interfaceOrienation];
}

-(void)selectedFile:(NSString *)fileName :(UIInterfaceOrientation)interfaceOrientation{
    NSArray *resultkeys=[NSArray arrayWithObjects:FILEBROWSER_PATH,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",fileName],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [self cleanPlugin:interfaceOrientation];
}

-(void)cancelBrowser:(UIInterfaceOrientation)interfaceOrientation{
    //    [self fileBrowserError:nil errMessage:[NSString stringWithFormat:@"File Browser Cancelled"]];
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    NSString *errorString=[NSString stringWithFormat:@"FileBrowser--Cancelled"];
    [Logger logger_Log:@"E" :errorString];
    [self cleanPlugin:interfaceOrientation];
}
#pragma mark-FileBrowser Delegates
-(void)selectedFile:(NSString *)fileName path:(NSString *)filePath{
    NSArray *resultkeys=[NSArray arrayWithObjects:FILEBROWSER_PATH,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",filePath],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
}

#pragma mark-FileBrowser Error
-(void)fileBrowserError:(NSString *)errorCode errMessage:(NSString *)errorMessage{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:errorCode,nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    NSString *errorString=[NSString stringWithFormat:@"FileBrowser--%@",errorMessage];
    [Logger logger_Log:@"E" :errorString];
}


#pragma mark-FileBrowser Utilities
-(NSString *)appDocumentsDirectory{
    NSArray *dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    //         NSString *appID=[self.webView  stringByEvaluatingJavaScriptFromString:@" appzillon.data.appid;"];
    NSString *fileOpnDir= [[dirPaths objectAtIndex:0] stringByAppendingFormat:@"/Assets/apps/%@/",self.viewController.appString];
    return fileOpnDir;
    
}

- (NSMutableDictionary *)getAllDirectory:(NSString *)filters{
    NSFileManager *fileManager =[NSFileManager defaultManager];
    NSString *appdocumentsDirectory = [self appDocumentsDirectory];
    NSMutableArray *appDirectoryContent = (NSMutableArray*)[fileManager contentsOfDirectoryAtPath: appdocumentsDirectory error:nil];
    [appDirectoryContent removeObjectsInArray:@[@"scripts",@"screens",@"plist",@"sqlite",@"styles",@"sslCertificates"]];
    NSMutableDictionary *finalFiles = [@{} mutableCopy];
    BOOL isDir = NO;
    for(NSString *file in appDirectoryContent) {
        isDir = NO;
        NSString *path = [appdocumentsDirectory stringByAppendingPathComponent:file];
        [finalFiles setObject:@"createFile" forKey:file];
        [fileManager fileExistsAtPath:path isDirectory:&isDir];
        if(isDir){
            NSArray *subDirContent = [fileManager contentsOfDirectoryAtPath:path error:nil];
            if(subDirContent.count >0){
                NSArray *filteredFiles = [self getFilteredFiles:path withFilters:filters];
                if(filteredFiles.count > 0){
                    [finalFiles setObject:filteredFiles forKey:file];
                }
                else{
                    NSString *msgString=[NSString stringWithFormat:@"FileBrowser--nofiles in directory%@",file];
                    [Logger logger_Log:@"I" :msgString];
                }
            }
            else{
                NSString *msgString=[NSString stringWithFormat:@"FileBrowser--sub Directory is empty%@",file];
                [Logger logger_Log:@"I" :msgString];
            }
        }
    }
    return finalFiles;
}

- (NSArray *)getLocationFiles:(NSString *)location
                      filters:(NSString *)filters
{
    NSFileManager *fileManager =[NSFileManager defaultManager];
    NSArray *filteredFiles = nil;
    NSMutableArray *directoryContent = (NSMutableArray*)[fileManager contentsOfDirectoryAtPath:location error:nil];
    if(directoryContent.count >0){
        filteredFiles = [self getFilteredFiles:location withFilters:filters];
        if(filteredFiles.count > 0){
            NSString *msgString=[NSString stringWithFormat:@"FileBrowser--Filtered Files in SubDirectory%@",filteredFiles];
            [Logger logger_Log:@"I" :msgString];
        }
        else{
            NSString *msgString=[NSString stringWithFormat:@"FileBrowser--No files in SubDirectory%@",filteredFiles];
            [Logger logger_Log:@"I" :msgString];
            filteredFiles = nil;
        }
    }
    else{
        [Logger logger_Log:@"I" :@"SubDirectory  is empty"];
        filteredFiles = nil;
    }
    return filteredFiles;
}

-(NSArray *)getFilteredFiles:(NSString *)location
                 withFilters:(NSString *)filters
{
    NSFileManager *fileManager = [[NSFileManager alloc] init];
    NSArray *filesArr = nil;
    NSError *error = nil;
    if([location isEqualToString:@""] == NO){
        filesArr = [fileManager contentsOfDirectoryAtPath:location
                                                    error:&error];
        if ([filesArr count] > 0 && error == nil){
            if([filters isEqualToString:@""] == NO)
            {
                NSArray *extensions = [filters componentsSeparatedByString:FILEBROWSER_FILTER_SEPERATOR];
                NSMutableArray *subpredicates = [@[] mutableCopy];
                for (NSString *extension in extensions) {
                    [subpredicates addObject:[NSPredicate predicateWithFormat:@"SELF ENDSWITH %@", extension]];
                }
                NSPredicate *filter = [NSCompoundPredicate orPredicateWithSubpredicates:subpredicates];
                filesArr= [filesArr filteredArrayUsingPredicate:filter];
            }
            else{
                [Logger logger_Log:@"I" :@"No Extension to filter by extensions"];
            }
        }
        else{
            NSString *msgString=[NSString stringWithFormat:@"FileBrowser--An error happened =%@",error];
            [Logger logger_Log:@"E" :msgString];
            filesArr = nil;
        }
    }
    else{
        filesArr = nil;
    }
    return filesArr;
}

#pragma mark -Plugin Clean
-(void)cleanPlugin : (UIInterfaceOrientation)interfaceOrientation{
    self.webView = nil;
    [self.delegate donePluginWithOrientaion:self :interfaceOrientation];
}

@end

