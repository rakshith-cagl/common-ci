//
//  APZCamera.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//
#import "APZVideo.h"
#import <AVFoundation/AVFoundation.h>
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"

@interface APZVideo()<UIImagePickerControllerDelegate,UINavigationControllerDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(strong, nonatomic) NSDictionary *cameraJsonDict;
@property(strong, nonatomic) NSString *cameraAction;
@property(strong, nonatomic) NSString *videoFileName;
@property(strong, nonatomic) NSString *cameraHtmlID;
@property(strong, nonatomic)  NSString *cameraFileOverwrite;
@property(strong,nonatomic) NSString *pluginId;
@end

@implementation APZVideo
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZVideo--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    self.videoFileName =[jsonDict objectForKey:@"fileName"];
    if(jsonDict !=nil){
        self.cameraAction=[jsonDict objectForKey:CAMERA_ACTION];
        self.cameraJsonDict = jsonDict;
        if([self.videoFileName isEqualToString:@""]){
            UIAlertController *videoRecAlert = [UIAlertController alertControllerWithTitle:@"" message:@"File Name should be entered" preferredStyle:UIAlertControllerStyleAlert];
            UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
            [videoRecAlert addAction:actionCancel];
            [self.viewController presentViewController:videoRecAlert animated:YES completion:nil];
        }
        else{
            BOOL isCameraPresent = [self camera];
            if (isCameraPresent == NO){
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",CAMERA_NOTFOUND],nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                
                [Logger logger_Log:@"E" :@"APZVideo--device does not have a camera"];
                [self cleanPlugin];
            }
        }
    }
    else{
        [self cleanPlugin];
    }
}

#pragma mark - Camera Functionality
- (BOOL) camera{
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera])
    {
        UIImagePickerController *videoRecorder = [[UIImagePickerController alloc] init];
        videoRecorder.delegate = self;
        videoRecorder.sourceType = UIImagePickerControllerSourceTypeCamera;
        NSArray *mediaTypes = [UIImagePickerController availableMediaTypesForSourceType:UIImagePickerControllerSourceTypeCamera];
        NSArray *videoMediaTypesOnly = [mediaTypes filteredArrayUsingPredicate:[NSPredicate predicateWithFormat:@"(SELF contains %@)", @"movie"]];
        
        if ([UIImagePickerController isCameraDeviceAvailable:UIImagePickerControllerCameraDeviceFront])
            videoRecorder.cameraDevice = UIImagePickerControllerCameraDeviceRear;
        videoRecorder.mediaTypes = videoMediaTypesOnly;
        videoRecorder.videoQuality = UIImagePickerControllerQualityTypeMedium;
        [videoRecorder setModalPresentationStyle:UIModalPresentationFullScreen];
        [self.viewController presentViewController:videoRecorder animated:YES completion:nil];
        [Logger logger_Log:@"I" :@"APZVideo--camera opened"];
        NSString *flashLight=[self.cameraJsonDict objectForKey:@"Flash"];
        if ([flashLight isEqualToString:@"Y"]) {
            [self turnFlashOn:YES controller:videoRecorder];
        }
        return YES;
    }else{
        return NO;
    }
}
-(NSString *)DocumentsDirectory{
    NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    return [[NSString alloc] initWithString:[paths objectAtIndex:0]];
}

-(NSString *)appDocumentsDirectory{
    NSArray *dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    //     NSString *OTAStatus=[self.webView stringByEvaluatingJavaScriptFromString:@" appzillon.data.otarequired;"];
    NSString *fileOpnDir;
    //    if([OTAStatus isEqualToString:@"Y"]){
    ////    NSString* appID=[self.webView stringByEvaluatingJavaScriptFromString:@"appzillon.data.appid;"];
    fileOpnDir= [[dirPaths objectAtIndex:0] stringByAppendingFormat:@"/Assets/apps/%@",self.viewController.appString];
    //    }
    //    else{
    //        fileOpnDir= [dirPaths objectAtIndex:0];
    //    }
    
    return  [fileOpnDir stringByAppendingPathComponent:@"video"];
    
}
-(void)imagePickerController:(UIImagePickerController *)picker
didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    NSURL *videoURL = [info objectForKey:UIImagePickerControllerMediaURL];
    NSData *videoData = [NSData dataWithContentsOfURL:videoURL];
    NSString *videoDirectory=[self appDocumentsDirectory];
    [[NSFileManager defaultManager] createDirectoryAtPath:videoDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    NSString *tempPath;
    tempPath = [videoDirectory stringByAppendingFormat:@"/%@.mp4",self.videoFileName];
    NSFileManager *fileManager =[NSFileManager defaultManager];
    NSArray *subDirContent = [fileManager contentsOfDirectoryAtPath:videoDirectory error:nil];
    if([subDirContent containsObject:self.videoFileName]){
        [Logger logger_Log:@"I" :@"APZVideo--video file has same name"];
    }
//    NSError * writeError;
//    BOOL success = [videoData writeToFile:tempPath options:NSDataWritingAtomic error:&writeError];
    BOOL success = [MiscellaneousMethods writeData:videoData toFile:tempPath];
    
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    if(success){
        NSArray *resultkeys=[NSArray arrayWithObjects:[NSString stringWithFormat:@"filePath"],nil];
        NSArray *result=[NSArray arrayWithObjects:tempPath,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZVideo--video saved"];
    }
    [self cleanPlugin];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZVideo--camera cancelled"];
    [self cleanPlugin];
}

-(NSString *)pathFor:(NSString *)filename forAction:(NSString *)action{
    NSArray *paths;
    NSString *documentsDirectory;
    if ([action isEqualToString:CAMERA_SRCURL]) {
        documentsDirectory = NSTemporaryDirectory();
        documentsDirectory=[documentsDirectory stringByAppendingString:filename];
    }else if([action isEqualToString:CAMERA_SAVE]){
        paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
        documentsDirectory =  [self appDocumentsDirectory];
        NSString *photosDir = [documentsDirectory stringByAppendingPathComponent:CAMERA_DIRNAME];
        [[NSFileManager defaultManager] createDirectoryAtPath:photosDir withIntermediateDirectories:YES attributes:nil error:NULL];
        documentsDirectory = [photosDir stringByAppendingPathComponent:filename];
    }else{
        documentsDirectory=@"";
    }
    self.cameraFileOverwrite = [self.cameraJsonDict objectForKey:CAMERA_FILEOVERWRITE];
    if([[NSFileManager defaultManager]fileExistsAtPath:[documentsDirectory stringByAppendingString:CAMERA_IMAGE_EXTENSION]]){
        if ([self.cameraFileOverwrite isEqualToString:@"N"]) {
            NSDate *currDate = [NSDate date];
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
            [dateFormatter setDateFormat:DATEFORMAT];
            NSString *dateString = [dateFormatter stringFromDate:currDate];
            documentsDirectory=[documentsDirectory stringByAppendingString:dateString];
        }else{
            [Logger logger_Log:@"E" :@"APZVideo--File Does exists and OverWrite is No"];
        }
    }
    else{
        [Logger logger_Log:@"E" :@"APZVideo--File Does exists "];
    }
    return documentsDirectory;
}

- (void) turnFlashOn: (bool) on controller: (UIImagePickerController *)imageController{
    Class captureDeviceClass = NSClassFromString(@"AVCaptureDevice");
    if (captureDeviceClass != nil) {
        AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
        if ([device hasTorch] && [device hasFlash]){
           
            [device lockForConfiguration:nil];
            if (on) {
                [device setTorchMode:AVCaptureTorchModeOn];
                imageController.cameraFlashMode = UIImagePickerControllerCameraFlashModeOn;
            } else {
                [device setTorchMode:AVCaptureTorchModeOff];
                imageController.cameraFlashMode = UIImagePickerControllerCameraFlashModeOff;
            }
            [device unlockForConfiguration];
        }
    }
}

-(UIImage*)imageWithImage:(UIImage*)image :(NSString *)Size
{
    NSArray *dimesionArray=[Size componentsSeparatedByString:@","];
    double width=[[dimesionArray objectAtIndex:0]doubleValue];
    double height=[[dimesionArray objectAtIndex:1]doubleValue];
    CGSize newSize=CGSizeMake(width, height);
    UIGraphicsBeginImageContext(newSize);
    [image drawInRect:CGRectMake(0,0,width,height)];
    UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}

- (void)RecordVideo
{
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera])
    {
        UIImagePickerController *videoRecorder = [[UIImagePickerController alloc] init];
        videoRecorder.sourceType = UIImagePickerControllerSourceTypeCamera;
        videoRecorder.delegate = self;
        
        NSArray *mediaTypes = [UIImagePickerController availableMediaTypesForSourceType:UIImagePickerControllerSourceTypeCamera];
        NSArray *videoMediaTypesOnly = [mediaTypes filteredArrayUsingPredicate:[NSPredicate predicateWithFormat:@"(SELF contains %@)", @"movie"]];
        
        if ([videoMediaTypesOnly count] == 0)
        {
            UIAlertController *actionAlert = [UIAlertController alertControllerWithTitle:@"" message:@"Sorry but your device does not support video recording" preferredStyle:UIAlertControllerStyleAlert];
            UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
            [actionAlert addAction:actionCancel];
            [self.viewController presentViewController:actionAlert animated:YES completion:nil];
            
           
        }
        else
        {
            if ([UIImagePickerController isCameraDeviceAvailable:UIImagePickerControllerCameraDeviceFront])
                videoRecorder.cameraDevice = UIImagePickerControllerCameraDeviceFront;
            
            videoRecorder.mediaTypes = videoMediaTypesOnly;
            videoRecorder.videoQuality = UIImagePickerControllerQualityTypeMedium;
            videoRecorder.videoMaximumDuration = 180;
        }
        
    }
}
-(NSString *)audioDirectory{
    NSArray *dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *fileOpnDir;
    fileOpnDir= [[dirPaths objectAtIndex:0] stringByAppendingFormat:@"/Assets/apps/%@",self.viewController.appString];
    return  [fileOpnDir stringByAppendingPathComponent:AUDIO_DIRNAME];
    
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"E" :@"APZVideo--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}



@end

