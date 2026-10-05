//
//  MiscellaneousMethods.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 11/28/18.
//

#import "MiscellaneousMethods.h"
#import "ApzApp.h"


@implementation MiscellaneousMethods
#pragma mark FileBrowser
+(NSString*)getItemsImage:(NSString*)item{
    NSString *extension = [item pathExtension];
    if ([extension length]==0) {
        extension = @"folder";
    }else{
        if ([extension caseInsensitiveCompare:@"pdf"]==NSOrderedSame) {
            extension = @"pdf";
        }else if ([extension caseInsensitiveCompare:@"txt"]==NSOrderedSame || [extension caseInsensitiveCompare:@"text"]==NSOrderedSame) {
            extension = @"text";
        }else if ([extension caseInsensitiveCompare:@"doc"]==NSOrderedSame || [extension caseInsensitiveCompare:@"docx"]==NSOrderedSame) {
            extension = @"doc";
        }else if ([extension caseInsensitiveCompare:@"ppt"]==NSOrderedSame || [extension caseInsensitiveCompare:@"pptx"]==NSOrderedSame ||[extension caseInsensitiveCompare:@"pptm"]==NSOrderedSame) {
            extension = @"ppt";
        }else if ([extension caseInsensitiveCompare:@"war"]==NSOrderedSame || [extension caseInsensitiveCompare:@"jar"]==NSOrderedSame ||[extension caseInsensitiveCompare:@"zip"]==NSOrderedSame) {
            extension = @"archive";
        }else if ([extension caseInsensitiveCompare:@"html"]==NSOrderedSame || [extension caseInsensitiveCompare:@"htm"]==NSOrderedSame) {
            extension = @"html";
        }else if ([extension caseInsensitiveCompare:@"apk"]==NSOrderedSame) {
            extension = @"apk";
        }else if ([extension caseInsensitiveCompare:@"xls"]==NSOrderedSame || [extension caseInsensitiveCompare:@"xlsx"]==NSOrderedSame) {
            extension = @"xlsx";
        }else if ([extension caseInsensitiveCompare:@"png"]==NSOrderedSame || [extension caseInsensitiveCompare:@"jpeg"]==NSOrderedSame || [extension caseInsensitiveCompare:@"jpg"]==NSOrderedSame) {
            extension = @"image";
        }else if ([extension caseInsensitiveCompare:@"mp3"]==NSOrderedSame) {
            extension = @"audio";
        }else if ([extension caseInsensitiveCompare:@"mp4"]==NSOrderedSame) {
            extension = @"video";
        } else if ([extension caseInsensitiveCompare:@"xml"]==NSOrderedSame) {
            extension = @"xml";
        }else{
            extension = @"unknownfile";
        }
    }
    
    return extension;
}

#pragma mark appLaunchedCheck

+(BOOL)getAppHasLaunchedBefore:(NSString*)mainAppId{
    NSArray * allKeysInDefaultLocation = [[[NSUserDefaults standardUserDefaults] dictionaryRepresentation] allKeys];
    BOOL launchedBefore = NO;
    if ([allKeysInDefaultLocation containsObject:[NSString stringWithFormat:@"%@.HasLaunchedOnce",mainAppId]]) {
        launchedBefore =[[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",mainAppId]];
    }else if ([allKeysInDefaultLocation containsObject:@"HasLaunchedOnce"]){
        launchedBefore =[[NSUserDefaults standardUserDefaults] objectForKey:@"HasLaunchedOnce"];
    }else{
        return NO;
    }
    
    return launchedBefore;
}
+(BOOL)getAppHasLaunchedOnOlderVersion{
    return [[[[NSUserDefaults standardUserDefaults] dictionaryRepresentation] allKeys] containsObject:@"HasLaunchedOnce"];;
}
+(void)setAppHasLaunchedBefore:(NSString*)mainAppId{
    ///lanched on Older version
    BOOL launchedOnOlderVersion=[[[[NSUserDefaults standardUserDefaults] dictionaryRepresentation] allKeys] containsObject:@"HasLaunchedOnce"];
    if (launchedOnOlderVersion) {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"HasLaunchedOnce"];
    }
    [[NSUserDefaults standardUserDefaults] setBool:YES forKey:[NSString stringWithFormat:@"%@.HasLaunchedOnce",mainAppId]];
    
}

#pragma mark Grayscaling and BlackAndWhite Conversion
//- (UIImage *)imageBlackAndWhite1:(UIImage*)image
//{
//    CGImageRef cgImage = [image CGImage];
//    CIImage *beginImage = [CIImage imageWithCGImage:cgImage];
//
//    CIImage *output = [CIFilter filterWithName:@"CIColorMonochrome" keysAndValues:kCIInputImageKey, beginImage, @"inputIntensity", [NSNumber numberWithFloat:1.0], @"inputColor", [[CIColor alloc] initWithColor:[UIColor whiteColor]], nil].outputImage;
//
//    CIContext *context = [CIContext contextWithOptions:nil];
//    CGImageRef cgiimage = [context createCGImage:output fromRect:output.extent];
//    UIImage *newImage = [UIImage imageWithCGImage:cgiimage scale:image.scale orientation:image.imageOrientation];
//
//    CGImageRelease(cgiimage);
//
//    return newImage;
//}


+ (UIImage *)grayScaleImage:(UIImage*)image
{
    CGImageRef cgImage = [image CGImage];
    CIImage *beginImage = [CIImage imageWithCGImage:cgImage];

    CIImage *blackAndWhite = [CIFilter filterWithName:@"CIColorControls" keysAndValues:kCIInputImageKey, beginImage, @"inputBrightness", [NSNumber numberWithFloat:0.0], @"inputContrast", [NSNumber numberWithFloat:1.1], @"inputSaturation", [NSNumber numberWithFloat:0.0], nil].outputImage;
    
    CIImage *output = [CIFilter filterWithName:@"CIExposureAdjust" keysAndValues:kCIInputImageKey, blackAndWhite, @"inputEV", [NSNumber numberWithFloat:0.7], nil].outputImage;

    CIContext *context = [CIContext contextWithOptions:nil];
    CGImageRef cgiimage = [context createCGImage:output fromRect:output.extent];
    //UIImage *newImage = [UIImage imageWithCGImage:cgiimage];
    UIImage *newImage = [UIImage imageWithCGImage:cgiimage scale:image.scale orientation:image.imageOrientation];
    CGImageRelease(cgiimage);

    return newImage;
}
#pragma mark Bitmap Black and white

#define Mask8(x) ( (x) & 0xFF )
#define R(x) ( Mask8(x) )
#define G(x) ( Mask8(x >> 8 ) )
#define B(x) ( Mask8(x >> 16) )
#define A(x) ( Mask8(x >> 24) )
#define RGBAMake(r, g, b, a) ( Mask8(r) | Mask8(g) << 8 | Mask8(b) << 16 | Mask8(a) << 24 )
+ (UIImage *)convertToBlackAndWhiteImage:(UIImage*)inputImage :(NSString*)threshold{
  // 1. Get the raw pixels of the image
  UInt32 * inputPixels;
  CGContextRef context;
  CGImageRef inputCGImage = [inputImage CGImage];
  NSUInteger inputWidth = CGImageGetWidth(inputCGImage);
  NSUInteger inputHeight = CGImageGetHeight(inputCGImage);
  CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
  NSUInteger bytesPerPixel = 4;
  NSUInteger bitsPerComponent = 8;
  NSUInteger inputBytesPerRow = bytesPerPixel * inputWidth;
  inputPixels = (UInt32 *)calloc(inputHeight * inputWidth, sizeof(UInt32));
    if(inputPixels) {
         context = CGBitmapContextCreate(inputPixels, inputWidth, inputHeight,
                                                     bitsPerComponent, inputBytesPerRow, colorSpace,
                                                     kCGImageAlphaPremultipliedLast | kCGBitmapByteOrder32Big);
        CGContextDrawImage(context, CGRectMake(0, 0, inputWidth, inputHeight), inputCGImage);
        // 3. Convert the image to Black & White
        UInt32 * currentPixel = inputPixels;
        for (NSUInteger j = 0; j < inputHeight; j++) {
            for (NSUInteger i = 0; i < inputWidth; i++) {
                UInt32 color = *currentPixel;
                // Threshold logic needs to be implimented and requires some more effort
                UInt32 averageColor = (R(color) + G(color) + B(color)) / 3.0;
                UInt32 thresholdInteger;
                if (threshold) {
                    thresholdInteger = [threshold intValue];
                }else{
                    //Putting a default threshold value,can be updated based on requirement
                    thresholdInteger = 90;
                }
                if (averageColor<thresholdInteger) {
                    //        if (averageColor<90) {
                    averageColor = 0;
                }else{
                    averageColor =255;
                }
                *currentPixel = RGBAMake(averageColor, averageColor, averageColor, A(color));
                
                currentPixel++;
            }
        }
    }

  CGImageRef newCGImage = CGBitmapContextCreateImage(context);
  UIImage * processedImage = [UIImage imageWithCGImage:newCGImage];

  CGColorSpaceRelease(colorSpace);
  CGContextRelease(context);
  free(inputPixels);
  return processedImage;
}
#undef RGBAMake
#undef R
#undef G
#undef B
#undef A
#undef Mask8

#pragma mark - JS CALLBACK FUNCTION

+(void) jsLayerCall:(WKWebView *)webView :(NSString *)jsFunctionName parameter:(NSString *)param{
    @synchronized(self) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [webView evaluateJavaScript:[NSString stringWithFormat:@"%@(%@)",jsFunctionName,param] completionHandler:nil];
        });
    }
}

#pragma mark - Bundle related function
+(NSBundle *)getAppBundle{
    return [NSBundle bundleWithIdentifier:@"com.iexceed.Appzillon"];
}


#pragma mark - Get Main ViewController function
+(id)getAppzillonViewController{
    ApzApp *appDelegate = (ApzApp *)[ApzApp sharedManager];
    return [appDelegate viewController];
}

#pragma mark NSFileProtectionComplete
+(BOOL)writeData:(NSData*)data toFile:(NSString*)filePath{
    BOOL writeOperationSuccess = false;
    NSError *error;
    if([data writeToFile:filePath options:NSDataWritingFileProtectionComplete error:&error]){
        writeOperationSuccess = true;
    }else{
        NSLog(@"File Operation Failure with error-- %@", error.description);
    }
    return writeOperationSuccess;
}
@end

