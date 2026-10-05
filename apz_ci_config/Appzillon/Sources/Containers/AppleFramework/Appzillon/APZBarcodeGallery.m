//
//  APZBarcodeGallery.m
//  Appzillon
//
//  Created by Manu Gowda N R on 6/5/18.
//

#import "APZBarcodeGallery.h"
#import <CoreImage/CoreImage.h>
#import <ImageIO/ImageIO.h>
#import "Logger.h"
#import "Constants.h"
#import "APZJsonUtil.h"
#import "AppzillonViewController.h"
#import "AppzillonAppDelegate.h"


@interface APZBarcodeGallery()<UIImagePickerControllerDelegate, UINavigationControllerDelegate>
@property(nonatomic,weak)WKWebView *webView;
@property(strong,nonatomic) NSString *pluginId;
@property(nonatomic,strong) NSDictionary *jsonDict;
@property(nonatomic,weak) AppzillonViewController *viewController;
@end
@implementation APZBarcodeGallery

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    AppzillonAppDelegate *appDelegate = (AppzillonAppDelegate*)[[UIApplication sharedApplication] delegate];
    self.viewController = [appDelegate viewController];
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    self.jsonDict = jsonDict;
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if ([[jsonDict objectForKey:@"action"] isEqual:@"photoGallery"]) {
        [self openPhotoGallery];
    }else {
        [self scanImageUsingImagePath:jsonDict];
    }
}

- (void)openPhotoGallery{
    UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
    imagePicker.delegate = self;
    imagePicker.allowsEditing = NO;
    imagePicker.editing = YES;
    imagePicker.modalPresentationStyle = UIModalPresentationFormSheet;
    imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    [imagePicker setModalPresentationStyle:UIModalPresentationFullScreen];
    [self.viewController presentViewController:imagePicker animated:YES completion:nil];
    [Logger logger_Log:@"I" :@"APZQRGallery--gallery open"];
}

-(void)imagePickerController:(UIImagePickerController *)picker
didFinishPickingMediaWithInfo:(NSDictionary *)info{
    UIImage *image = [info objectForKey:@"UIImagePickerControllerOriginalImage"];
    [picker dismissViewControllerAnimated:YES completion:^{
        if (image!=nil) {
            [self getBarcodeDataUsingImage:image];
        }
    }];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    NSArray *resultkeys=[NSArray arrayWithObjects:@"errorMessage",nil];
    NSArray *result=[NSArray arrayWithObjects:@"user cancelled the photo gallery",nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    [self cleanPlugin];
}

-(void)getBarcodeDataUsingImage:(UIImage *)image{
    NSArray *decodedData = [self detectQRCode:image];
    if (decodedData != nil && decodedData.count > 0) {
        for (CIQRCodeFeature* qrFeature in decodedData) {
            NSArray *resultkeys=[NSArray arrayWithObjects:CBTEXT,nil];
            NSArray *returnResult=[NSArray arrayWithObjects:qrFeature.messageString,nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :returnResult]];
            [self cleanPlugin];
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:@"errorMessage",nil];
        NSArray *returnResult=[NSArray arrayWithObjects:@"Failed to scan the image",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :returnResult]];
        [self cleanPlugin];
    }
}

-(void)scanImageUsingImagePath:(NSDictionary *)jsonDict{
    if (jsonDict !=nil) {
        NSString *imagePath = [jsonDict objectForKey:@"filePath"];
        if(imagePath !=nil && ![imagePath isEqualToString:@""]){
            NSData* resData = [[NSData alloc] initWithContentsOfFile:imagePath];
            UIImage *image;
            if (resData) {
                image = [[UIImage alloc] initWithData:resData];
                NSArray *decodedData = [self detectQRCode:image];
                if (decodedData != nil && decodedData.count > 0) {
                    for (CIQRCodeFeature* qrFeature in decodedData) {
                        NSArray *resultkeys=[NSArray arrayWithObjects:CBTEXT,nil];
                        NSArray *returnResult=[NSArray arrayWithObjects:qrFeature.messageString,nil];
                        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :returnResult]];
                        [self cleanPlugin];
                    }
                }else{
                    NSArray *resultkeys=[NSArray arrayWithObjects:@"errorMessage",nil];
                    NSArray *returnResult=[NSArray arrayWithObjects:@"Failed to scan the image",nil];
                    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :returnResult]];
                    [self cleanPlugin];
                }
            }
            else{
                NSArray *resultkeys=[NSArray arrayWithObjects:@"errorMessage",nil];
                NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-070",nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                [self cleanPlugin];
            }
        }
        
    }else{
        [self cleanPlugin];
    }
}

-(NSArray *)detectQRCode:(UIImage *) image
{
    @autoreleasepool {
        NSCAssert(image != nil, @"**Assertion Error** detectQRCode : image is nil");
        CIImage* ciImage = [[CIImage alloc] initWithCGImage:image.CGImage];
        NSDictionary* options;
        CIContext* context = [CIContext context];
        options = @{ CIDetectorAccuracy : CIDetectorAccuracyHigh };
        CIDetector* qrDetector = [CIDetector detectorOfType:CIDetectorTypeQRCode context:context options:options];
        if ([[ciImage properties] valueForKey:(NSString*) kCGImagePropertyOrientation] == nil) {
            options = @{ CIDetectorImageOrientation : @1};
        } else {
            options = @{ CIDetectorImageOrientation : [[ciImage properties] valueForKey:(NSString*) kCGImagePropertyOrientation]};
        }
        NSArray * features = [qrDetector featuresInImage:ciImage options:options];
        return features;
    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"I" :@"APZBarcode--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
    self.delegate = nil;
    self.viewController = nil;
    self.jsonDict = nil;
}

@end

