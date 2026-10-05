//
//  APZBarcode.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZBarcodeGenerator.h"
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
#import "UIImage+MDQRCode.h"



@interface APZBarcodeGenerator()
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(strong,nonatomic) NSString *pluginId;
@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UIImage *barCodeImage;
@property(strong,nonatomic)NSString *fileJsonPath;
@property(strong,nonatomic)NSString *fileCreateFileName;

@end


@implementation APZBarcodeGenerator

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZBarcodeGenerator--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if(jsonDict !=nil && [jsonDict objectForKey:@"inputString"] != nil){
    NSString *inputString ;
        if([[jsonDict objectForKey:@"inputString"] isKindOfClass:[NSDictionary class]]){
            NSError * err;
            NSData * jsonData = [NSJSONSerialization  dataWithJSONObject:[jsonDict objectForKey:@"inputString"] options:0 error:&err];
            inputString = [[NSString alloc] initWithData:jsonData   encoding:NSUTF8StringEncoding];
        }else{
            inputString = [jsonDict objectForKey:@"inputString"];
        }
        CGFloat imageSize = ceilf(self.viewController.view.bounds.size.width * 0.6f);
        self.imageView = [[UIImageView alloc] initWithFrame:CGRectMake(floorf(self.viewController.view.bounds.size.width * 0.5f - imageSize * 0.5f), floorf(self.viewController.view.bounds.size.height * 0.5f - imageSize * 0.5f), imageSize, imageSize)];
        self.barCodeImage = [UIImage mdQRCodeForString:inputString size:self.imageView.bounds.size.width fillColor:[UIColor darkGrayColor]];
        
        if ([[jsonDict objectForKey:@"base64"] isEqualToString:@"N"]) {
            if ([[jsonDict objectForKey:@"destinationPath"] isEqualToString:@""]) {
                self.fileJsonPath = @"TempFolderQRCode";
            }else{
                self.fileJsonPath = [jsonDict objectForKey:@"destinationPath"];
            }
            if ([[jsonDict objectForKey:@"fileName"] isEqualToString:@""]) {
                NSDate *date = [NSDate date];
                NSDateFormatter * dateFormatter = [[NSDateFormatter alloc] init];
                [dateFormatter setDateFormat:@"yyyyMMdd_HHmmss"];
                self.fileCreateFileName = [dateFormatter stringFromDate:date];
            }else{
                self.fileCreateFileName = [jsonDict objectForKey:@"fileName"];
            }
            if (![self.fileJsonPath isEqualToString:@""] || ![self.fileCreateFileName isEqualToString:@""]) {
                [self fileCreate];
            }
        }else{
            NSData *imageData = UIImagePNGRepresentation(self.barCodeImage);
            NSString * barcodeBase64String = [imageData base64EncodedStringWithOptions:0];
            if (barcodeBase64String) {
                NSArray *resultkeys=[NSArray arrayWithObjects:CBTEXT,nil];
                NSArray *returnResult=[NSArray arrayWithObjects:barcodeBase64String,nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :returnResult]];
                [Logger logger_Log:@"I" :@"APZBarcodeGenerator -- Generator Success"];
                [self cleanPlugin];
            }else{
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *resultMess=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",FILE_COULD_NOT_CREATED],nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMess]];
                [Logger logger_Log:@"E" :@"APZBarcodeGenerator -- Base64 file couldnot created"];
                [self cleanPlugin];
            }
        }
    }else{
        [self cleanPlugin];
    }
    
}

#pragma mark FileCreate/Modify
-(void)fileCreate{
    NSString *filePath = [self fileOpnPath:self.fileCreateFileName];
    filePath = [filePath stringByAppendingString:@".jpeg"];
    if([filePath isEqualToString:@""] == NO){
        if([[NSFileManager defaultManager] fileExistsAtPath:filePath] ){
            [self fileModify:filePath];
        }else{
            [self createImageFile:filePath];
        }
    }
    
}

#pragma mark create File Method
-(void)createImageFile:(NSString*)filePath{
    NSData *imageData = UIImageJPEGRepresentation(self.barCodeImage, 1.0);
//    if ([imageData writeToFile:filePath atomically:YES]){
    if ([MiscellaneousMethods writeData:imageData toFile:filePath]){
        NSArray *resultkeys=[NSArray arrayWithObjects:CBTEXT,nil];
        NSArray *result=[NSArray arrayWithObjects:filePath,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZBarcodeGenerator--Successfully Created File"];
        [self cleanPlugin];
    }else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILE_COULD_NOT_CREATED,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZBarcodeGenerator--could not create file"];
        [self cleanPlugin];
    }
}
-(void)fileModify:(NSString *)filePath{
    NSMutableData *imageData = (NSMutableData *)UIImageJPEGRepresentation(self.barCodeImage, 1.0);
    NSKeyedArchiver *archiver = [[NSKeyedArchiver alloc]initForWritingWithMutableData:imageData];
    [archiver encodeObject:imageData forKey:@"filepath"];
    [archiver finishEncoding];
    BOOL isFileModified =[[NSFileManager defaultManager] createFileAtPath:filePath contents:imageData attributes:nil];
    if(isFileModified){
        NSArray *resultkeys=[NSArray arrayWithObjects:CBTEXT,nil];
        NSArray *result=[NSArray arrayWithObjects:filePath,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZBarcodeGenerator -- File modification success"];
        [self cleanPlugin];
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILEOP_MODIFY_FAIL,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZBarcodeGenerator -- File modification Failed"];
        [self cleanPlugin];
    }
}
-(NSString *)fileOpnPath:(NSString *)fileName{
    NSString *fileOpnDir = [self fileOpnDirectory];
    NSString *filePath =nil;
    if( [fileOpnDir isEqualToString:@""] == NO){
        NSError *error;
        //to keep only one recent file everytime
        bool isPreviousFilesDeleted =  [[NSFileManager defaultManager] removeItemAtPath:fileOpnDir error:&error];
        if (isPreviousFilesDeleted) {
            [[NSFileManager defaultManager] createDirectoryAtPath:fileOpnDir withIntermediateDirectories:YES attributes:nil error:NULL];
            filePath = [fileOpnDir stringByAppendingPathComponent:fileName];
        }
    }
    else{
        filePath = @"";
    }
    return filePath;
}

-(NSString *)fileOpnDirectory{
    NSArray *dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* appzillonAppSandbox=[[dirPaths objectAtIndex:0] stringByAppendingPathComponent:@"Assets/apps"];
    NSString* fileOpnDir;
    if([self.fileJsonPath isEqualToString:@""] ||self.fileJsonPath==NULL){
        fileOpnDir=[appzillonAppSandbox stringByAppendingFormat:@"/%@/",self.viewController.appString];
    }else{
        fileOpnDir=[appzillonAppSandbox stringByAppendingFormat:@"/%@/%@",self.viewController.appString,self.fileJsonPath];
    }
    [[NSFileManager defaultManager] createDirectoryAtPath:fileOpnDir withIntermediateDirectories:YES attributes:nil error:NULL];
    return fileOpnDir;
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"I" :@"APZBarcodeGenerator--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
    self.delegate = nil;
}
@end

