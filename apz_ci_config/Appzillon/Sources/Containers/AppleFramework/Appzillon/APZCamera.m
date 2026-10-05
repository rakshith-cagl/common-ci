//
//  APZCamera.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZCamera.h"
#import <AVFoundation/AVFoundation.h>
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Base64.h"
#import "Logger.h"
#import "NLImageCropperView.h"


@interface APZCamera()<UIImagePickerControllerDelegate,UINavigationControllerDelegate>
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(nonatomic,strong)WKWebView *webView;
@property (strong, nonatomic) NSDictionary *cameraJsonDict;
@property (strong, nonatomic) NSString *cameraAction;
@property (strong, nonatomic) NSString *cameraHtmlID;
@property (strong, nonatomic) NSString *cameraFileOverwrite;
@property (strong,nonatomic) NLImageCropperView* imageCropper;
@property (strong,nonatomic) NSString *pluginId;
@property (strong, nonatomic) NSString *frontCamera;
@end

@implementation APZCamera
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZCamera--execute"];
    self.cameraJsonDict= jsonDict;
    if(self.cameraJsonDict !=nil){
        self.cameraAction=[self.cameraJsonDict objectForKey:CAMERA_ACTION];
        self.frontCamera =[self.cameraJsonDict objectForKey:FRONT_CAMERA];
        self.pluginId=[self.cameraJsonDict objectForKey:PLUGINID];
        BOOL isCameraPresent = [self camera];
        if (isCameraPresent == NO){
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",CAMERA_NOTFOUND],nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZCamera--device does not support"];
            [self cleanPlugin];
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
        UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
        imagePicker.delegate = self;
        imagePicker.allowsEditing = NO;
        imagePicker.editing = YES;
        
        NSString* sourceType=[self.cameraJsonDict objectForKey:@"sourceType"];
        if ([sourceType isEqualToString:@"album"]) {
            imagePicker.modalPresentationStyle = UIModalPresentationFormSheet;
            imagePicker.sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;
        } else if([sourceType isEqualToString:@"photo"]) {
            imagePicker.modalPresentationStyle = UIModalPresentationFormSheet;
            imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        }else{
            imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
            if([self.frontCamera isEqualToString:@"Y"]){
                imagePicker.cameraDevice =UIImagePickerControllerCameraDeviceFront;
            }
            else{
                imagePicker.cameraDevice =UIImagePickerControllerCameraDeviceRear;
            }
            if ([[self.cameraJsonDict objectForKey:@"flash"] isEqualToString:@"Y"]) {
                //                [self turnFlashOn:YES];
                imagePicker.cameraFlashMode=UIImagePickerControllerCameraFlashModeOn;
            }
        }
        [imagePicker setModalPresentationStyle:UIModalPresentationFullScreen];
        [self.viewController presentViewController:imagePicker animated:YES completion:nil];
        [Logger logger_Log:@"I" :@"APZCamera--camera open"];
        return YES;
    }else{
        return NO;
    }
}

-(void)imagePickerController:(UIImagePickerController *)picker
didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    [Logger logger_Log:@"I" :@"APZCamera-- image captured"];
    UIImage *image = [info objectForKey:@"UIImagePickerControllerOriginalImage"];
    image = [self  imageByScalingAndCroppingForSize:image];
    if ([[self.cameraJsonDict objectForKey:@"crop" ] isEqualToString:@"Y"]) {
        [picker dismissViewControllerAnimated:YES completion:^{
            UIButton *cropButton = [UIButton buttonWithType:UIButtonTypeCustom];
            cropButton.frame = CGRectMake (0,-20, 80, 80);
            [cropButton setTitle:@"Crop" forState:UIControlStateNormal];
            [cropButton addTarget:self action:@selector(cropMethod) forControlEvents:UIControlEventTouchUpInside];
            [cropButton setTag:50];
            UIButton *cancelButton = [UIButton buttonWithType:UIButtonTypeCustom];
            cancelButton.frame = CGRectMake(self.webView.bounds.size.width-85,-20, 80, 80);
            [cancelButton setTitle:@"Cancel" forState:UIControlStateNormal];
            [cancelButton addTarget:self action:@selector(CancelCroping) forControlEvents:UIControlEventTouchUpInside];
            [cancelButton setTag:555];
            UIButton *OriginalButton = [UIButton buttonWithType:UIButtonTypeCustom];
            OriginalButton.frame = CGRectMake(self.webView.bounds.size.width/2-42,-20, 80, 80);
            [OriginalButton setTitle:@"Original" forState:UIControlStateNormal];
            [OriginalButton addTarget:self action:@selector(saveOriginal) forControlEvents:UIControlEventTouchUpInside];
            [OriginalButton setTag:556];
            _imageCropper = [[NLImageCropperView alloc] initWithFrame:CGRectMake(0, 0, self.webView.bounds.size.width, self.webView.bounds.size.height)];
            [_imageCropper addSubview:cropButton];
            [_imageCropper addSubview:OriginalButton];
            [_imageCropper addSubview:cancelButton];
            [_imageCropper setImage:image];
            //            CGRect box = CGRectMake(_imageCropper.bounds.size.width/2 -120.0, _imageCropper.bounds.size.height/2 -120.0,240.0, 240.0);
            //            CGRect box = CGRectMake(image.size.width/2-image.size.width/4,image.size.height/2-image.size.height/4,image.size.width/2, image.size.height/2);
            CGRect box = CGRectMake(image.size.width/4,image.size.height/4,image.size.width/2, image.size.height/2);
            
            [_imageCropper setCropRegionRect:box];
            _imageCropper.cropBox=[_cameraJsonDict objectForKey:CROPBOXSHAPE];
            [self.webView addSubview:_imageCropper];
        }];
    }else{
        [self modifyImage:image];
        [picker dismissViewControllerAnimated:YES completion:nil];
        [self cleanPlugin];
    }
}

- (UIImage*)imageByScalingAndCroppingForSize:(UIImage*)sourceImage
{
    UIImage *newImage = nil;
    //    CGSize imageSize = sourceImage.size;
    //    CGFloat width = imageSize.width;
    //    CGFloat height = imageSize.height;
    //    CGFloat targetWidth = targetSize.width;
    //    CGFloat targetHeight = targetSize.height;
    //    CGFloat scaleFactor = 0.0;
    //    CGFloat scaledWidth = targetWidth;
    //    CGFloat scaledHeight = targetHeight;
    CGPoint thumbnailPoint = CGPointMake(0.0,0.0);
    //    if (CGSizeEqualToSize(imageSize, targetSize) == NO)
    //    {
    //        CGFloat widthFactor = targetWidth / width;
    //        CGFloat heightFactor = targetHeight / height;
    //        if (widthFactor > heightFactor)
    //            scaleFactor = widthFactor;
    //        else
    //            scaleFactor = heightFactor;
    //        scaledWidth  = width * scaleFactor;
    //        scaledHeight = height * scaleFactor;
    //        if (widthFactor > heightFactor)
    //        {
    //            thumbnailPoint.y = (targetHeight - scaledHeight) * 0.5;
    //        }
    //        else
    //            if (widthFactor < heightFactor)
    //            {
    //                thumbnailPoint.x = (targetWidth - scaledWidth) * 0.5;
    //            }
    //    }
    UIGraphicsBeginImageContext(sourceImage.size);
    CGRect thumbnailRect = CGRectZero;
    thumbnailRect.origin = thumbnailPoint;
    thumbnailRect.size.width  = sourceImage.size.width;
    thumbnailRect.size.height = sourceImage.size.height;
    [sourceImage drawInRect:thumbnailRect];
    newImage = UIGraphicsGetImageFromCurrentImageContext();
    if(newImage == nil)
        NSLog(@"could not scale image");
    UIGraphicsEndImageContext();
    return newImage;
}
-(void)cropMethod{
    UIImage *croppedImage=[_imageCropper getCroppedImage];
    [Logger logger_Log:@"I" :@"APZCamera--image cropped"];
    [self modifyImage:croppedImage];
    [_imageCropper removeFromSuperview];
    [self cleanPlugin];
}
-(void)saveOriginal{
    UIImage *imageFromCamera=[_imageCropper getImage];
    [self modifyImage:imageFromCamera];
    [_imageCropper removeFromSuperview];
    [self cleanPlugin];
}
-(void)CancelCroping{
    [Logger logger_Log:@"I" :@"APZCamera--image cancelled"];
    [_imageCropper removeObserverMethod];
    [_imageCropper removeFromSuperview];
    [self cleanPlugin];
}


-(void)modifyImage:(UIImage*)image{
    double targetHeight=[[self.cameraJsonDict objectForKey:@"targetHeight"] doubleValue];
    double targetWidth=[[self.cameraJsonDict objectForKey:@"targetWidth"] doubleValue];
    if ((targetHeight==0) && (targetWidth==0)) {
        targetWidth=612;
        targetHeight=816;
    }else if (targetWidth==0)
    {
        double ratio = image.size.width/image.size.height;
        targetWidth=ratio*targetHeight;
        
    }else if (targetHeight==0){
        double ratio = image.size.width/image.size.height;
        targetHeight=ratio*targetWidth;
    }
    UIImage *compressedImage = [self imageWithImage:image :targetWidth :targetHeight];
    NSString *quality=[self.cameraJsonDict objectForKey:@"quality"];
    if (quality==NULL || [quality isEqualToString:@""]) {
        quality =@"100";
    }
    CGFloat commpressionRate= ([quality floatValue])/100;
    NSString  *encodingType=[self.cameraJsonDict objectForKey:@"encodingType"];
    NSString  *fileName = [self.cameraJsonDict objectForKey:CAMERA_FILENAME];
    NSData *dataImg;
    if([[self.cameraJsonDict objectForKey:@"unCompressed"] isEqualToString:@"Y"]){
        if ([encodingType isEqualToString:@"png"]) {
            dataImg = [NSData dataWithData:UIImagePNGRepresentation(image)];
        } else {
            dataImg=[NSData dataWithData:UIImageJPEGRepresentation(image, 1.0f)];
        }
    }else{
        if ([encodingType isEqualToString:@"png"]) {
            dataImg = [NSData dataWithData:UIImagePNGRepresentation(compressedImage)];
        } else {
            dataImg=[NSData dataWithData:UIImageJPEGRepresentation(compressedImage, commpressionRate)];
        }
    }
    if (dataImg){
        if ( ([self.cameraAction isEqualToString:CAMERA_BASE64]) || ([self.cameraAction isEqualToString:CAMERA_BASE64_SAVE]))  {
            //        if ( ([self.cameraAction isEqualToString:CAMERA_BASE64]) || ([self.cameraAction isEqualToString:CAMERA_SRCBASE64]||([self.cameraAction isEqualToString:CAMERA_BASE64_SAVE])) ) {
            NSUInteger len = [dataImg length];
            Byte *byteData= (Byte*)malloc(len);
            if (byteData) {
                [dataImg getBytes:byteData length:len];
                [Base64 initialize];
                NSString * finalstr1 = [Base64 encode:dataImg];
                if ( ![finalstr1 isEqualToString:@""] && (finalstr1 != nil) ){
                    if ([self.cameraAction isEqualToString:CAMERA_BASE64]) {
                        NSArray *resultkeys=[NSArray arrayWithObjects:CAMERA_JSON_PATH,CAMERA_JSON_ENCODEDIMAGE,nil];
                        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@""],[NSString stringWithFormat:@"%@",finalstr1],nil];
                        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                        [Logger logger_Log:@"I" :@"APZCamera--base 64 success"];
                    }
                    else if ([self.cameraAction isEqualToString:CAMERA_BASE64_SAVE]){
                        NSString *urlPath=[[self pathFor:fileName forAction:self.cameraAction]stringByAppendingString:CAMERA_IMAGE_EXTENSION];
//                        NSError *writeError;
//                        BOOL writeResult= [dataImg writeToFile:urlPath options:NSDataWritingAtomic error:&writeError];
                        BOOL writeResult= [MiscellaneousMethods writeData:dataImg toFile:urlPath];
                        if (writeResult) {
                            NSArray *resultkeys=[NSArray arrayWithObjects:CAMERA_JSON_PATH,CAMERA_JSON_ENCODEDIMAGE,nil];
                            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",urlPath],[NSString stringWithFormat:@"%@",finalstr1],nil];
                            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                            [Logger logger_Log:@"I" :@"APZCamera--write success"];
                        }else{
                            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",FILE_COULD_NOT_CREATED],nil];
                            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                            [Logger logger_Log:@"E" :@"APZCamera--file could not be created"];
                            
                        }
                    }
                    else{
                        //                    self.cameraHtmlID = [self.cameraJsonDict objectForKey:CAMERA_HTMLID];
                        //                    if ([encodingType isEqualToString:@"png"]) {
                        //                        [self.webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"document.getElementById('%@').src= 'data:image/png;base64,%@'",self.cameraHtmlID, finalstr1]];
                        //                    }else
                        //                    {
                        //                        [self.webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"document.getElementById('%@').src= 'data:image/jpg;base64,%@'",self.cameraHtmlID, finalstr1]];
                        //                    }
                        //
                        //                    NSArray *resultkeys=[NSArray arrayWithObjects:CAMERA_JSON_PATH,CAMERA_JSON_ENCODEDIMAGE,nil];
                        //                    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@""],[NSString stringWithFormat:@"%@",@""],nil];
                        //                 [self jsLayerCall:JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                        //                     [Logger logger_Log:@"I" :@"APZCamera--scrbase64 success"];
                    }
                }
                else{
                    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",CAMERA_ENCODEFAILBASE64],nil];
                    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                    [Logger logger_Log:@"E" :@"APZCamera--Unable to encode Image in Base64"];
                }
                if(byteData != NULL){
                    free(byteData);
                }
            }
        }
        else {
//            NSError * writeError;
            //Move it
            NSString *urlPath=[[self pathFor:fileName forAction:self.cameraAction]stringByAppendingString:CAMERA_IMAGE_EXTENSION];
//            BOOL writeResult= [dataImg writeToFile:urlPath options:NSDataWritingAtomic error:&writeError];
            BOOL writeResult= [MiscellaneousMethods writeData:dataImg toFile:urlPath];
            if (writeResult) {
                //                if ([self.cameraAction isEqualToString:CAMERA_SRCURL]) {
                //                    self.cameraHtmlID = [self.cameraJsonDict objectForKey:CAMERA_HTMLID];
                //                    [self.webView stringByEvaluatingJavaScriptFromString: [NSString stringWithFormat:@"document.getElementById('%@').src= '%@?'+ new Date().getTime()", self.cameraHtmlID, urlPath]];
                //                    NSArray *resultkeys=[NSArray arrayWithObjects:CAMERA_JSON_PATH,CAMERA_JSON_ENCODEDIMAGE,nil];
                //                    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@""],[NSString stringWithFormat:@"%@",@""],nil];
                //                    [self jsLayerCall:JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                //                    [Logger logger_Log:@"E" :@"APZCamera--srcURL success"];
                //                }
                //                else{
                NSArray *resultkeys=[NSArray arrayWithObjects:CAMERA_JSON_PATH,CAMERA_JSON_ENCODEDIMAGE,nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",urlPath],[NSString stringWithFormat:@"%@",@""],nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                [Logger logger_Log:@"I" :@"APZCamera--write location success"];
                //                }
            }else{
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",FILE_COULD_NOT_CREATED],nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                [Logger logger_Log:@"E" :@"APZCamera--write location failure"];
            }
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",CAMERA_IMAGE_CONVERSION_ERR],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZCamera--Image Conversion Failure"];
    }
}


- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    [Logger logger_Log:@"I" :@"APZCamera--Image Conversion Failure"];
    [self cleanPlugin];
}

-(NSString *)pathFor:(NSString *)filename forAction:(NSString *)action{
    NSArray *paths;
    NSString *documentsDirectory;
    if ([action isEqualToString:CAMERA_SRCURL]) {
        documentsDirectory = NSTemporaryDirectory();
        documentsDirectory=[documentsDirectory stringByAppendingString:filename];
    }else if([action isEqualToString:CAMERA_SAVE] ||([action isEqualToString:CAMERA_BASE64_SAVE])){
        paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
        documentsDirectory = [[NSString alloc] initWithString:[paths objectAtIndex:0]];
        NSString *photosDir=[documentsDirectory stringByAppendingFormat:@"/Assets/apps/%@/%@/",self.viewController.appString,CAMERA_DIRNAME];
        //        NSString *photosDir = [documentsDirectory str:CAMERA_DIRNAME];
        [[NSFileManager defaultManager] createDirectoryAtPath:photosDir withIntermediateDirectories:YES attributes:nil error:NULL];
        documentsDirectory = [photosDir stringByAppendingPathComponent:filename];
    }else{
        documentsDirectory=@"";
    }
    //    self.cameraFileOverwrite = [self.cameraJsonDict objectForKey:CAMERA_FILEOVERWRITE];
    //    if([[NSFileManager defaultManager]fileExistsAtPath:[documentsDirectory stringByAppendingString:CAMERA_IMAGE_EXTENSION]]){
    //        if ([self.cameraFileOverwrite isEqualToString:@"N"]) {
    //            NSDate *currDate = [NSDate date];
    //            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
    //            [dateFormatter setDateFormat:DATEFORMAT];
    //            NSString *dateString = [dateFormatter stringFromDate:currDate];
    //            documentsDirectory=[documentsDirectory stringByAppendingString:dateString];
    //        }else{
    //            [Logger logger_Log:@"E" :@"APZCamera--Overwrite- File Does exists and OverWrite is No"];
    //        }
    //    }
    //    else{
    //        [Logger logger_Log:@"E" :@"APZCamera--Overwrite- File Does not exists"];
    //    }
    return documentsDirectory;
}



//- (void) turnFlashOn: (bool) on {
//    Class captureDeviceClass = NSClassFromString(@"AVCaptureDevice");
//    if (captureDeviceClass != nil) {
//        AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
//        if ([device hasTorch] && [device hasFlash]){
//            [device lockForConfiguration:nil];
//            if (on) {
//                [device setTorchMode:AVCaptureTorchModeOn];
//            } else {
//                [device setTorchMode:AVCaptureTorchModeOff];
//            }
//            [device unlockForConfiguration];
//        }
//    }
//}

-(UIImage*)imageWithImage:(UIImage*)image :(double)width :(double)height
{
    CGSize newSize=CGSizeMake(width, height);
    UIGraphicsBeginImageContext(newSize);
    [image drawInRect:CGRectMake(0,0,width,height)];
    UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZCamera--Done"];
    self.webView = nil;
    self.imageCropper=nil;
    [self.delegate donePlugin:self];
}

@end

