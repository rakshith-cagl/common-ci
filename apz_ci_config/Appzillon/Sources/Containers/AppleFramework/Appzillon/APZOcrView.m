//
//  APZCamera.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZOcrView.h"
#import <AVFoundation/AVFoundation.h>
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
#import "NLImageCropperView.h"
#import <TesseractOCR/TesseractOCR.h>
#import "Base64.h"


@interface APZOcrView()<UIImagePickerControllerDelegate,UINavigationControllerDelegate>
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(nonatomic,strong)WKWebView *webView;
@property (strong, nonatomic) NSDictionary *cameraJsonDict;
@property (strong, nonatomic) NSString *cameraAction;
@property (strong, nonatomic) NSString *cameraFileOverwrite;
@property (strong,nonatomic) NLImageCropperView* imageCropper;
@property (strong,nonatomic) NSString *pluginId;
@property (strong, nonatomic) NSString *frontCamera;
@property (strong,nonatomic)UIActivityIndicatorView *indicator;
@end

@implementation APZOcrView
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZOcrView--execute"];
    self.cameraJsonDict= jsonDict;
    if(self.cameraJsonDict !=nil){
        self.cameraAction=[self.cameraJsonDict objectForKey:@"OCR"];
        self.frontCamera =[self.cameraJsonDict objectForKey:FRONT_CAMERA];
        self.pluginId=[self.cameraJsonDict objectForKey:PLUGINID];
        BOOL isCameraPresent = [self camera];
        if (isCameraPresent == NO){
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",CAMERA_NOTFOUND],nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"Your device does not have a camera"];
            [self cleanPlugin];
        }
    }
    else{
        [self cleanPlugin];
        NSLog(@"--Error in JSON parsing");
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
        }
        [imagePicker setModalPresentationStyle:UIModalPresentationFullScreen];
        [self.viewController presentViewController:imagePicker animated:YES completion:nil];
        [Logger logger_Log:@"I" :@"Camera Open Success"];
        
        return YES;
    }else{
        return NO;
    }
}

-(void)imagePickerController:(UIImagePickerController *)picker
didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    [Logger logger_Log:@"I" :@"Camera Image Captured"];
    UIImage *image = [info objectForKey:@"UIImagePickerControllerOriginalImage"];
    image = [self  imageByScalingAndCroppingForSize:self.webView.bounds.size :image];
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
            //             [self activityIndicateForCrop];
            //            NSArray *cropDimArray=[[self.cameraJsonDict objectForKey:@"crop" ] componentsSeparatedByString:@","];
            CGRect box = CGRectMake(self.viewController.view.bounds.size.width/2 -120.0, self.viewController.view.bounds.size.height/2 -120.0,240.0, 240.0);
            [_imageCropper setCropRegionRect:box];
            [self.webView addSubview:_imageCropper];        }];
        
    }else{
        
        [self modifyImage:image];
        //         [self activityIndicateForCrop];
        [picker dismissViewControllerAnimated:YES completion:nil];
    }
}
-(void)activityIndicateForCrop{
    
    _indicator= [[UIActivityIndicatorView alloc]
                 initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    _indicator.autoresizingMask = UIViewAutoresizingFlexibleBottomMargin|UIViewAutoresizingFlexibleTopMargin|UIViewAutoresizingFlexibleLeftMargin|UIViewAutoresizingFlexibleRightMargin;
    _indicator.center = self.viewController.view.center;
    [_indicator setHidesWhenStopped:YES];
    [_indicator startAnimating];
    
    [_indicator setCenter:self.viewController.view.center];
    [self.viewController.view addSubview:_indicator];
    
    
    
}

- (UIImage*)imageByScalingAndCroppingForSize:(CGSize)targetSize :(UIImage*)sourceImage
{
    UIImage *newImage = nil;
    CGSize imageSize = sourceImage.size;
    CGFloat width = imageSize.width;
    CGFloat height = imageSize.height;
    CGFloat targetWidth = targetSize.width;
    CGFloat targetHeight = targetSize.height;
    CGFloat scaleFactor = 0.0;
    CGFloat scaledWidth = targetWidth;
    CGFloat scaledHeight = targetHeight;
    CGPoint thumbnailPoint = CGPointMake(0.0,0.0);
    if (CGSizeEqualToSize(imageSize, targetSize) == NO)
    {
        CGFloat widthFactor = targetWidth / width;
        CGFloat heightFactor = targetHeight / height;
        if (widthFactor > heightFactor)
            scaleFactor = widthFactor;
        else
            scaleFactor = heightFactor;
        scaledWidth  = width * scaleFactor;
        scaledHeight = height * scaleFactor;
        if (widthFactor > heightFactor)
        {
            thumbnailPoint.y = (targetHeight - scaledHeight) * 0.5;
        }
        else
            if (widthFactor < heightFactor)
            {
                thumbnailPoint.x = (targetWidth - scaledWidth) * 0.5;
            }
    }
    UIGraphicsBeginImageContext(targetSize);
    CGRect thumbnailRect = CGRectZero;
    thumbnailRect.origin = thumbnailPoint;
    thumbnailRect.size.width  = scaledWidth;
    thumbnailRect.size.height = scaledHeight;
    [sourceImage drawInRect:thumbnailRect];
    newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}

-(void)cropMethod{
    UIImage *croppedImage=[_imageCropper getCroppedImage];
    [Logger logger_Log:@"I" :@"APZOcrView Image Cropped"];
    [_imageCropper removeFromSuperview];
    [self activityIndicateForCrop];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self modifyImage:croppedImage];
    });
}
-(void)saveOriginal{
    UIImage *imageFromCamera=[_imageCropper getImage];
    [Logger logger_Log:@"I" :@"APZOcrView Image Retuned"];
    [_imageCropper removeFromSuperview];
    [self activityIndicateForCrop];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self modifyImage:imageFromCamera];
    });
}
-(void)CancelCroping{
    [Logger logger_Log:@"I" :@"APZOcrView Image Cropping cancelled"];
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
    [self imageWithImage:image :targetWidth :targetHeight];
    CGFloat commpressionRate= [[self.cameraJsonDict objectForKey:@"quality"] floatValue];
    NSString  *encodingType=[self.cameraJsonDict objectForKey:@"encodingType"];
    NSString  *fileName = [NSString stringWithFormat:@"%@",@"Scanned_img"];
    NSData *dataImg;
    if ([encodingType isEqualToString:@"png"]) {
        dataImg=[NSData dataWithData:UIImagePNGRepresentation(image)];
    } else {
        dataImg=[NSData dataWithData:UIImageJPEGRepresentation(image, commpressionRate)];
        
    }
    
//    NSError * writeError;
    NSString *urlPath=[[self pathFor:fileName forAction:self.cameraAction]stringByAppendingString:CAMERA_IMAGE_EXTENSION];
//    BOOL writeResult= [dataImg writeToFile:urlPath options:NSDataWritingAtomic error:&writeError];
    BOOL writeResult= [MiscellaneousMethods writeData:dataImg toFile:urlPath];
    G8Tesseract* tesseract = [[G8Tesseract alloc] init];
    UIImage *scannedImage = [UIImage imageWithData:dataImg];
    [_indicator removeFromSuperview];
    if([self.cameraAction isEqualToString:@"Y"]){
        tesseract.language = @"eng+fra";
        tesseract.pageSegmentationMode = G8PageSegmentationModeAuto;
        tesseract.engineMode  = G8OCREngineModeTesseractCubeCombined;
        tesseract.maximumRecognitionTime = 60.0;
        tesseract.image = [scannedImage g8_blackAndWhite];
        tesseract.image = scannedImage;
        [tesseract recognize];
        NSArray *resultkeys=[NSArray arrayWithObjects:@"OCR_text",nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",tesseract.recognizedText],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    }
    //Image Scanning:
    else if ([self.cameraAction isEqualToString:@"N"]){
        
//        [UIImagePNGRepresentation([scannedImage g8_blackAndWhite]) writeToFile:urlPath atomically:YES];
        [MiscellaneousMethods writeData:UIImagePNGRepresentation([scannedImage g8_blackAndWhite]) toFile:urlPath];
        NSUInteger len = [dataImg length];
        Byte *byteData= (Byte*)malloc(len);
        [dataImg getBytes:byteData length:len];
        [Base64 initialize];
        NSString * finalBase64Str = [Base64 encode:dataImg];
        NSString *filePath;
        NSString *relativePath=[self.cameraJsonDict objectForKey:@"filePath"];
        if ([relativePath length]==0) {
            filePath=[self.viewController.sandboxPath stringByAppendingPathComponent:@"ScannedDocs"];
        }else{
            filePath=[self.viewController.sandboxPath stringByAppendingPathComponent:relativePath];
        }
        
        NSArray *resultkeys=[NSArray arrayWithObjects:@"encodedImage",nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",finalBase64Str],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",CAMERA_IMAGE_CONVERSION_ERR],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"Scanning option nor selected"];
    }
    [self cleanPlugin];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZOcrView Cancelled"];
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    [self cleanPlugin];
}

-(NSString *)pathFor:(NSString *)filename forAction:(NSString *)action{
    NSArray *paths;
    NSString *documentsDirectory;
    
    paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    documentsDirectory = [[NSString alloc] initWithString:[paths objectAtIndex:0]];
    NSString *photosDir=[documentsDirectory stringByAppendingFormat:@"/Assets/apps/%@/%@/",self.viewController.appString,CAMERA_DIRNAME];
    [[NSFileManager defaultManager] createDirectoryAtPath:photosDir withIntermediateDirectories:YES attributes:nil error:NULL];
    documentsDirectory = [photosDir stringByAppendingPathComponent:filename];
    return documentsDirectory;
}


-(UIImage*)imageWithImage:(UIImage*)image :(double)width :(double)height
{
    [Logger logger_Log:@"I" :@"OCR Resizing "];
    CGSize newSize=CGSizeMake(width, height);
    UIGraphicsBeginImageContext(newSize);
    [image drawInRect:CGRectMake(0,0,width,height)];
    UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZOCRView---Done"];
    self.indicator=nil;
    self.webView = nil;
    self.imageCropper=nil;
    [self.delegate donePlugin:self];
}

@end

