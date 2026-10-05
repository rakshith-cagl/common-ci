//
//  VideoGalleryController.m
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//


#import "VideoGalleryController.h"
#import "AppzillonViewController.h"
#import "Constants.h"
#import "APZJsonUtil.h"
#import "VideoViewController.h"

@interface VideoGalleryController()<videoViewDelegate,UIImagePickerControllerDelegate,UINavigationControllerDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property (strong, nonatomic) NSDictionary *jsonDetails;
@property(nonatomic,strong) NSString *fileBrowserOpenFile;
@property(nonatomic,strong) NSString *filterByExtensions;
@property(nonatomic,strong)UIPopoverPresentationController *popOverController;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation VideoGalleryController

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}



-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"VideoGalleryController--Execute"];
    if(jsonDict !=nil){
        self.pluginId=[jsonDict objectForKey:PLUGINID];
        self.fileBrowserOpenFile = [jsonDict objectForKey:FILEBROWSER_OPENFILE];
        self.filterByExtensions = [jsonDict objectForKey:FILEBROWSER_FILTER];
        [self videoGallery];
    }
    else{
        [self cleanUp];
    }
}

#pragma mark - Video Gallery Permissions
- (BOOL) isPhotoLibraryAvailable{
    return [UIImagePickerController isSourceTypeAvailable:              UIImagePickerControllerSourceTypePhotoLibrary];
}

- (BOOL)cameraSupportsMedia:(NSString *)paramMediaType
                 sourceType:(UIImagePickerControllerSourceType)paramSourceType{
    __block BOOL result = NO;
    if ([paramMediaType length] == 0){
        [Logger logger_Log:@"I" :@"VideoGalleryController--Media Files not Present"];
        return NO;
    }
    NSArray *availableMediaTypes =
    [UIImagePickerController availableMediaTypesForSourceType:paramSourceType];
    [availableMediaTypes enumerateObjectsUsingBlock: ^(id obj, NSUInteger idx, BOOL *stop) {
        NSString *mediaType = (NSString *)obj;
        if ([mediaType isEqualToString:paramMediaType]){
            result = YES;
            *stop= YES; }
    }];
    return result;
}

- (BOOL) canUserPickVideosFromPhotoLibrary{
    return [self
            cameraSupportsMedia:(NSString *)kUTTypeMovie sourceType:UIImagePickerControllerSourceTypeSavedPhotosAlbum];
}

#pragma mark - Video Recordings
-(void)videoGallery{
    UIImagePickerController *imagePickerController = nil;
    NSMutableArray *mediaTypes = nil;
    if([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad){
        if ([self isPhotoLibraryAvailable] && [self canUserPickVideosFromPhotoLibrary])
        {
            imagePickerController = [[UIImagePickerController alloc] init];
            imagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
            mediaTypes = [[NSMutableArray alloc] init];
            [mediaTypes addObject:(NSString *)kUTTypeMovie];
            imagePickerController.mediaTypes = mediaTypes;
            imagePickerController.delegate = self;
            imagePickerController.modalPresentationStyle = UIModalPresentationPopover;
            UIPopoverPresentationController *popOverController =
                     [imagePickerController popoverPresentationController];
            self.popOverController = popOverController;
            self.popOverController.sourceRect = CGRectMake(0, 0, 400, 400);
            self.popOverController.sourceView = self.viewController.view;
            self.popOverController.permittedArrowDirections = UIPopoverArrowDirectionAny;
            [self.viewController presentViewController:imagePickerController animated: YES completion: nil];
        }
        else{
            [self noVideoGalleryAccess];
            [self.viewController dismissViewControllerAnimated:YES completion:nil];
            [self cleanUp];
        }
    }
    else{
        if ([self isPhotoLibraryAvailable] && [self canUserPickVideosFromPhotoLibrary]){
            imagePickerController = [[UIImagePickerController alloc] init];
            imagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
            mediaTypes = [[NSMutableArray alloc] init];
            [mediaTypes addObject:(NSString *)kUTTypeMovie];
            imagePickerController.mediaTypes = mediaTypes;
            imagePickerController.delegate = self;
            [imagePickerController setModalPresentationStyle:UIModalPresentationFullScreen];
            [self.viewController presentViewController:imagePickerController animated:YES completion:nil];
        }
        else{
            [self noVideoGalleryAccess];
            [self.viewController dismissViewControllerAnimated:YES completion:nil];
            [self cleanUp];
        }
    }
}

#pragma mark - Image Picker Delegate
-(void)imagePickerController:(UIImagePickerController *)picker
didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    NSArray *resultkeys = nil;
    NSArray *result = nil;
    NSURL *videoURL = [info objectForKey:UIImagePickerControllerMediaURL];
    NSData *videoData = [NSData dataWithContentsOfURL:videoURL];
    NSString *videoPath = [self saveVideoSelected:videoData];
    picker.allowsEditing = NO;
    picker.videoQuality = UIImagePickerControllerQualityTypeMedium;
    if([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad){
        if((videoPath != nil) && [self.fileBrowserOpenFile isEqualToString:@"N"]){
            resultkeys=[NSArray arrayWithObjects:FILEBROWSER_PATH,nil];
            result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",videoPath],nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        }else if ((videoPath != nil) && [self.fileBrowserOpenFile isEqualToString:@"Y"]){
            [self showVideo:videoPath];
        }
        else{
            [Logger logger_Log:@"I" :@"VideoGalleryController--Unable to save Video Selected to tmp location"];
        }
        [self.viewController dismissViewControllerAnimated:YES completion:nil];
    }
    else{
        if((videoPath != nil) && [self.fileBrowserOpenFile isEqualToString:@"N"]){
            resultkeys=[NSArray arrayWithObjects:FILEBROWSER_PATH,nil];
            result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",videoPath],nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        }else if((videoPath != nil) && [self.fileBrowserOpenFile isEqualToString:@"Y"]){
            [picker dismissViewControllerAnimated:YES completion:nil];
            [self showVideo:videoPath];
        }
        else{
            [Logger logger_Log:@"I" :@"VideoGalleryController--Unable to save Video Selected to tmp location"];
        }
        
        [picker dismissViewControllerAnimated:YES completion:nil];
    }
    [self cleanUp];
}

-(void)showVideo: (NSString *)videoPath{
    VideoViewController *videoController = [[VideoViewController alloc] init];
    [videoController setupVideo:videoPath withCustomNavigationItem:YES];
    videoController.delegate=self;
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    self.popOverController = nil;
    [videoController setModalPresentationStyle:UIModalPresentationFullScreen];
    [self.viewController presentViewController:videoController animated:NO completion:nil];
}

-(void)cancelVideoView:(UIInterfaceOrientation)interfaceOrientation{
    [self cleanUp:interfaceOrientation];
}
#pragma mark - Save Image
-(NSString *)saveVideoSelected:(NSData *)videoData{
    NSString *appSandboxPath = nil;
    NSArray *paths = nil;
    if (videoData != nil){
        NSDate *currDate = [NSDate date];
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
        [dateFormatter setDateFormat:DATEFORMAT];
        NSString *dateString = [dateFormatter stringFromDate:currDate];
        NSString *fileName = [NSString stringWithFormat:FILEBROWSER_VIDEOGALLERY_FILENAME];
        fileName = [fileName stringByAppendingString:dateString];
        fileName = [fileName stringByAppendingString:FILEBROWSER_VIDEOGALLERY_EXTENSION];
        paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
        NSString* docDirectory = [[NSString alloc] initWithString:[paths objectAtIndex:0]];
        appSandboxPath=[docDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/apps/%@/",self.viewController.appString]];
        NSString *photosDir = [appSandboxPath stringByAppendingPathComponent:FILEBROWSER_VIDEOGALLERY_DIRNAME];
        BOOL isImagesDirCreated = [[NSFileManager defaultManager] createDirectoryAtPath:photosDir withIntermediateDirectories:YES attributes:nil error:NULL];
        appSandboxPath = [photosDir stringByAppendingPathComponent:fileName];
//        BOOL savedImage =[videoData writeToFile:appSandboxPath atomically:YES];
        BOOL savedImage = [MiscellaneousMethods writeData:videoData toFile:appSandboxPath];
        if(savedImage){
            NSString *msgString=[NSString stringWithFormat:@"VedioGalleryController--Saved video to  path%@",appSandboxPath];
            [Logger logger_Log:@"E" :msgString];
        }
        else{
            [Logger logger_Log:@"I" :@"Not able to save images to sandbox"];
            appSandboxPath = nil;
        }
    }
    else{
        [Logger logger_Log:@"I" :@"Unable to save video to url"];
        appSandboxPath = nil;
    }
    return appSandboxPath;
}
-(void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    [picker dismissViewControllerAnimated:YES completion:^{
        [self cleanUp:[[UIApplication sharedApplication] statusBarOrientation]];
    }];
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"VidoGalleryController-Cancelled"];
    [self cleanUp];
}
-(void)noVideoGalleryAccess{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:VIDEO_ACCESS_DENIED],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"VidoGalleryController-No Accees to video gallery"];
}

#pragma mark  JSON CREATION
-(NSString *) createJSONString:(NSArray *)keys withValues:(NSArray *)values{
    NSMutableDictionary * resultDic=[[NSMutableDictionary alloc] initWithObjects:values forKeys:keys];
    NSString *result= [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:resultDic options:0 error:nil] encoding:NSUTF8StringEncoding];
    return result;
}

-(void)cleanUp{
    self.webView = nil;
    self.jsonDetails = nil;
    self.fileBrowserOpenFile = nil;
    self.filterByExtensions = nil;
    self.popOverController = nil;
    [self.delegate doneVideoGallery:self];
}
-(void)cleanUp :(UIInterfaceOrientation)orientation{
    self.webView = nil;
    self.jsonDetails = nil;
    self.fileBrowserOpenFile = nil;
    self.filterByExtensions = nil;
    self.popOverController = nil;
    [self.delegate doneVideoGalleryWithOrientation:orientation];
}
@end

