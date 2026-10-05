//
//  APZSaveReport.m
//  Appzillon
//
//  Created by Amuly Ranjan Mishra on 08/10/13.
//
//

#import "APZSaveReport.h"
#import "Constants.h"
#import "AFNetworking.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Base64.h"
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import <QuickLook/QuickLook.h>
#import "Logger.h"

@interface APZSaveReport()<QLPreviewControllerDataSource,
QLPreviewControllerDelegate,UINavigationControllerDelegate>
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,strong)NSOperationQueue *fileUpDownloadQueue;
@property(nonatomic,copy)NSString *serverURL;
@property(nonatomic,copy)NSString *filePath;
@property(nonatomic,copy)NSString *fileID;
@property (nonatomic, strong) NSMutableArray *documentURLs;
@property(nonatomic,strong)  QLPreviewController *previewController;
@property(nonatomic,strong)NSString *pluginId;
@end


@implementation APZSaveReport
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
        
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict
{
    [Logger logger_Log:@"D" :@"APZSaveReport--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    NSString *base64Data = [jsonDict objectForKey:@"base64"];
    NSString *fileExtension =[jsonDict objectForKey:@"extension"];
    NSString *fileName = @"Statement";
    if([fileExtension isEqualToString:@"pdf"]){
        fileName = [fileName stringByAppendingString:@".pdf"];
    }
    else{
        fileName = [fileName stringByAppendingString:@".xls"];
    }
    NSData *fileData=[Base64 decode:base64Data];
    NSArray *pathArray= [NSArray arrayWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    NSString *path=[NSString stringWithFormat:@"%@/Downloads",[pathArray objectAtIndex:0]];
    NSFileManager *fileManager=[NSFileManager defaultManager];
    if (![fileManager fileExistsAtPath:path]) {
        [fileManager createDirectoryAtPath:path withIntermediateDirectories:NO attributes:nil error:nil];
    }
    path=[NSString stringWithFormat:@"%@/%@",path,fileName];
    self.filePath = path;
//    [fileData writeToFile:path atomically:YES];
    [MiscellaneousMethods writeData:fileData toFile:path];
    [self viewDocument];
}





#pragma mark - Document Viewer


-(void)viewDocument{
    [Logger logger_Log:@"I" :@"APZSaveReport--Document Controller"];
    QLPreviewController *previewController = [[QLPreviewController alloc] init];
    previewController.dataSource = self;
    previewController.delegate = self;
    previewController.currentPreviewItemIndex = 0;
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:previewController];
    navigationController.delegate = self;
    [self.viewController presentViewController:navigationController animated:YES completion:nil];
}





#pragma mark -QLPreviewControllerDataSource


// Returns the number of items that the preview controller should preview
- (NSInteger)numberOfPreviewItemsInPreviewController:(QLPreviewController *)previewController
{
    return 1;
}



- (void)previewControllerDidDismiss:(QLPreviewController *)controller
{
    [controller dismissViewControllerAnimated:YES completion:nil];
    //    [self callToLogProcess:@"Save Report Document Preview Cancelled" :YES];
}

// returns the item that the preview controller should preview
- (id)previewController:(QLPreviewController *)previewController
     previewItemAtIndex:(NSInteger)idx
{
    NSURL *fileURL =[NSURL fileURLWithPath:self.filePath];
    return fileURL;
}



- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated{
    
    UIBarButtonItem *infoButton =  [[UIBarButtonItem alloc]
                                    initWithTitle:@"Cancel"
                                    style:UIBarButtonItemStylePlain
                                    target:self
                                    action:@selector(cancelDocViewer)];
    viewController.navigationItem.leftBarButtonItem =infoButton;
    
}


-(void)cancelDocViewer{
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    NSArray * resultkeys=nil;
    NSArray *resultMsg=nil;
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
    
    [self cleanPlugin];
    [Logger logger_Log:@"I" :@"APZSaveReport--Cancelled"];
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZSaveReport--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end

