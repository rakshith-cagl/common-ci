//
//  APZProcessImage.m
//  Appzillon
//
//  Created by Pradeep Kumar Tiwari on 13/01/20.
//

#import "APZProcessImage.h"
#import "AppzillonViewController.h"
#import "Constants.h"
#import "APZJsonUtil.h"
#import "MiscellaneousMethods.h"


@interface APZProcessImage()
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@property(nonatomic,weak)AppzillonViewController *viewController;
@end


@implementation APZProcessImage
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
       if (self) {
           _webView = wbView;
           self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
       }
       return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    self.pluginId = [jsonDict objectForKey:PLUGINID];
    NSDictionary *inputFile = [jsonDict objectForKey:@"inputFile"];
    NSString *inputFileType = [inputFile objectForKey:@"type"];
    UIImage *originalImage;
    if ([inputFileType isEqualToString:@"base64"]) {
        NSData* data = [[NSData alloc] initWithBase64EncodedString:[inputFile objectForKey:@"data"] options:NSDataBase64DecodingIgnoreUnknownCharacters];
         originalImage = [UIImage imageWithData:data];
    }else{
        originalImage = [UIImage imageWithContentsOfFile:[inputFile objectForKey:@"data"]];
    }
    if (originalImage != nil) {
        UIImage *processedImage;
        NSString *imageAction = [jsonDict objectForKey:@"imageAction"];
        if ([imageAction isEqualToString:@"BW"]) {
            processedImage=[MiscellaneousMethods convertToBlackAndWhiteImage:[MiscellaneousMethods grayScaleImage:originalImage] :[[jsonDict objectForKey:@"outputFile"] objectForKey:@"threshold"]];
        }else{
            processedImage=[MiscellaneousMethods grayScaleImage:originalImage];
        }
        if (processedImage != nil) {
            NSData *dataFromProcessedImage;
            NSString *fileEncoding =[[jsonDict objectForKey:@"outputFile"]objectForKey:@"encodingType"];
            if ([fileEncoding isEqualToString:@"PNG"]) {
             dataFromProcessedImage = UIImagePNGRepresentation(processedImage);
            }else{
                NSString *quality = [[jsonDict objectForKey:@"outputFile"]objectForKey:@"quality"];
            CGFloat qualityFloat;
             if (quality) {
                    qualityFloat = [quality floatValue]/100;
            }else{
                    qualityFloat = 1.0f;
            }
            dataFromProcessedImage = UIImageJPEGRepresentation(processedImage, qualityFloat);
            }
            if (dataFromProcessedImage) {
                if ([[[jsonDict objectForKey:@"outputFile"]objectForKey:@"type"]isEqualToString:@"base64"]) {
                    NSString *imageBase64 = [dataFromProcessedImage base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
                    if (imageBase64) {
                         NSDictionary *outputFile = [[NSDictionary alloc] initWithObjectsAndKeys:@"base64",@"type",imageBase64,@"data", nil];
                    
                        NSArray *resultkeys=[NSArray arrayWithObjects:@"outputFile",@"imageAction",nil];
                        NSArray *result=[NSArray arrayWithObjects:outputFile,imageAction,nil];
                        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                    }else{
                        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                        NSArray *result=[NSArray arrayWithObjects:CAMERA_ENCODEFAILBASE64,nil];
                        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                        [Logger logger_Log:@"E" :@"APZProcessImage--could not process image"];
                        
                    }
                }else{
                    NSString  *pathToPhotos = [self.viewController.sandboxPath stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/apps/%@/photo",self.viewController.appString]];
                    [[NSFileManager defaultManager] createDirectoryAtPath:pathToPhotos withIntermediateDirectories:YES attributes:nil error:nil];
                    NSString *fileNameWithExtension = [NSString stringWithFormat:@"%@.%@",[[jsonDict objectForKey:@"outputFile"]objectForKey:@"fileName"],fileEncoding];
                    NSString *finalPath = [pathToPhotos stringByAppendingPathComponent:fileNameWithExtension];
//                    if([dataFromProcessedImage writeToFile:finalPath atomically:YES]){
                    if([MiscellaneousMethods writeData:dataFromProcessedImage toFile:finalPath]){
                        NSDictionary *outputFile = [[NSDictionary alloc] initWithObjectsAndKeys:@"file",@"type",finalPath,@"data", nil];
                        NSArray *resultkeys=[NSArray arrayWithObjects:@"outputFile",@"imageAction",nil];
                        NSArray *result=[NSArray arrayWithObjects:outputFile,imageAction,nil];
                        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                    }else{
                        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                        NSArray *result=[NSArray arrayWithObjects:DIRECTORY_CREATION_FAILED,nil];
                        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                        [Logger logger_Log:@"E" :@"APZProcessImage--could not create directory to store scanned image"];
                    }
                }
                
            }else{
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:CAMERA_IMAGE_CONVERSION_ERR,nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                [Logger logger_Log:@"E" :@"APZProcessImage--could not convert image"];
            
            }
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:@"APZ_CNT_COULDNOTPROCESSIMAGE",nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZProcessImage--could not process image"];
           
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:INVALID_SRC,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZProcessImage--Incorrect image source"];
    }
    [self cleanPlugin];
}

//-(void)executePlugin:(NSDictionary *)jsonDict{
//    self.pluginId = [jsonDict objectForKey:PLUGINID];
//    NSDictionary *inputFile = [jsonDict objectForKey:@"inputFile"];
//    NSString *inputFileType = [inputFile objectForKey:@"type"];
//    UIImage *originalImage;
//    if ([inputFileType isEqualToString:@"base64"]) {
//        NSData* data = [[NSData alloc] initWithBase64EncodedString:[inputFile objectForKey:@"data"] options:0];
//         originalImage = [UIImage imageWithData:data];
//    }else{
//        originalImage = [UIImage imageWithContentsOfFile:[inputFile objectForKey:@"data"]];
//    }
//    if (originalImage != nil) {
//        UIImage *processedImage;
//        NSString *imageAction = [jsonDict objectForKey:@"imageAction"];
//        if ([imageAction isEqualToString:@"BW"]) {
//            processedImage=[MiscellaneousMethods grayScaleImage:[MiscellaneousMethods convertToBlackAndWhiteImage:originalImage]];
//        }else{
//            processedImage=[MiscellaneousMethods grayScaleImage:originalImage];
//        }
//        if (processedImage != nil) {
//            NSData *dataFromProcessedImage;
//            NSString *fileEncoding =[[jsonDict objectForKey:@"outputFile"]objectForKey:@"encodingType"];
//            if ([fileEncoding isEqualToString:@"png"]) {
//             dataFromProcessedImage = UIImagePNGRepresentation(processedImage);
//            }else{
//            NSString *quality = [jsonDict objectForKey:@"quality"];
//            CGFloat qualityFloat;
//             if (quality) {
//                    qualityFloat = [quality floatValue];
//            }else{
//                    qualityFloat = 1.0f;
//            }
//            dataFromProcessedImage = UIImageJPEGRepresentation(processedImage, qualityFloat);
//            }
//            if (dataFromProcessedImage) {
//                if ([[[jsonDict objectForKey:@"outputFile"]objectForKey:@"type"]isEqualToString:@"base64"]) {
//                    NSString *imageBase64 = [dataFromProcessedImage base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
//                    if (imageBase64) {
//                         NSDictionary *outputFile = [[NSDictionary alloc] initWithObjectsAndKeys:@"base64",@"type",imageBase64,@"data", nil];
//
//                        NSArray *resultkeys=[NSArray arrayWithObjects:@"base64",@"imageAction",nil];
//                        NSArray *result=[NSArray arrayWithObjects:outputFile,imageAction,nil];
//                        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
//                    }else{
//                        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
//                        NSArray *result=[NSArray arrayWithObjects:@"Add an error code for could not process image",nil];
//                        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
//                        [Logger logger_Log:@"E" :@"APZProcessImage--could not process image"];
//
//                    }
//                }else{
//                    NSString  *pathToPhotos = [self.viewController.sandboxPath stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/apps/%@/photos",self.viewController.appString]];
//                    [[NSFileManager defaultManager] createDirectoryAtPath:pathToPhotos withIntermediateDirectories:YES attributes:nil error:nil];
//                    NSString *finalPath = [pathToPhotos stringByAppendingPathComponent:[jsonDict objectForKey:@"fileName"]];
//                    if([dataFromProcessedImage writeToFile:finalPath atomically:YES]){
//                        NSDictionary *outputFile = [[NSDictionary alloc] initWithObjectsAndKeys:@"file",@"type",finalPath,@"data", nil];
//
//                        NSArray *resultkeys=[NSArray arrayWithObjects:@"outputFile",@"imageAction",nil];
//                        NSArray *result=[NSArray arrayWithObjects:outputFile,imageAction,nil];
//                         [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
//                    }else{
//                        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
//                        NSArray *result=[NSArray arrayWithObjects:@"Add an error code for could not process image",nil];
//                        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
//                        [Logger logger_Log:@"E" :@"APZProcessImage--could not process image"];
//                    }
//                }
//
//            }else{
//                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
//                NSArray *result=[NSArray arrayWithObjects:@"Add an error code for could not process image",nil];
//                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
//                [Logger logger_Log:@"E" :@"APZProcessImage--could not process image"];
//
//            }
//        }else{
//            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
//            NSArray *result=[NSArray arrayWithObjects:@"Add an error code for could not process image",nil];
//            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
//            [Logger logger_Log:@"E" :@"APZProcessImage--could not process image"];
//
//        }
//    }else{
//        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
//        NSArray *result=[NSArray arrayWithObjects:INVALID_SRC,nil];
//        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
//        [Logger logger_Log:@"E" :@"APZProcessImage--Incorrect image source"];
//    }
//    [self cleanPlugin];
//}




#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZProcessImage--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end



