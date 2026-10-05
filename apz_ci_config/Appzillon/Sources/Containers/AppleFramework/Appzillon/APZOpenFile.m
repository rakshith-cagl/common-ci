//
//  APZOpenFile.m
//  Appzillon
//
//  Created by Admin on 09/10/13.
//
//

#import "APZOpenFile.h"
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import <QuickLook/QuickLook.h>
#import <MediaPlayer/MediaPlayer.h>

@interface APZOpenFile()<QLPreviewControllerDataSource,
QLPreviewControllerDelegate,UINavigationControllerDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong) NSString *openFileDir;
@property(nonatomic,strong) NSString *openFileName;
@property(nonatomic,strong) NSString *openFileType;
@property (nonatomic, strong) MPMoviePlayerController *moviePlayer;
@property (nonatomic, strong) NSMutableArray *documentURLs;
@property(nonatomic,strong)  QLPreviewController *previewController;
@property(nonatomic,strong) NSString *filePath;
@property(nonatomic,strong)NSString *pluginId;
@property(nonatomic,strong)AVAudioPlayer *audioPlayer;
@end

@implementation APZOpenFile

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZOpenFile--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    self.filePath = [jsonDict objectForKey:@"filePath"];
    if ([self.filePath isEqualToString:@""]||self.filePath==NULL) {
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",INVALID_SRC],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZOpenFile--No valid file path"];
    }else{
        [self viewDocument];
    }
}

#pragma mark - Document Viewer
-(void)viewDocument{
    [Logger logger_Log:@"I" :@"APZOpenFile--Opening Document"];
    _previewController = [[QLPreviewController alloc] init];
    _previewController.dataSource = self;
    _previewController.delegate = self;
    _previewController.currentPreviewItemIndex = 0;
    UIBarButtonItem *infoButton =  [[UIBarButtonItem alloc]
                                    initWithTitle:@"Cancel"
                                    style:UIBarButtonItemStylePlain
                                    target:self
                                    action:@selector(cancelDocViewer)];
    _previewController.navigationItem.leftBarButtonItem =infoButton;
    if ([QLPreviewController canPreviewItem:[NSURL fileURLWithPath:self.filePath]]) {
        UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:_previewController];
        navigationController.delegate = self;
        [navigationController setModalPresentationStyle:UIModalPresentationFullScreen];
        [self.viewController presentViewController:navigationController animated:YES completion:nil];
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",INVALID_FILE_FORMAT],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZOpenFile--File Not Supported"];
    }
}

#pragma mark -QLPreviewControllerDataSource
- (NSInteger)numberOfPreviewItemsInPreviewController:(QLPreviewController *)previewController
{
    return 1;
}

- (void)previewControllerDidDismiss:(QLPreviewController *)controller
{
    [controller dismissViewControllerAnimated:YES completion:nil];
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZOpenFile--Opening Document"];
    
}

- (id)previewController:(QLPreviewController *)previewController
     previewItemAtIndex:(NSInteger)idx
{
    NSURL *fileURL =[NSURL fileURLWithPath:self.filePath];
    return fileURL;
    
}

-(void)cancelDocViewer{
    [self.viewController dismissViewControllerAnimated:YES completion:^{
        NSArray *resultkeys=nil;
        NSArray *result=nil;
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [self cleanPlugin:self.previewController.interfaceOrientation];
    }];
    
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    self.webView = nil;
    [self.delegate donePlugin:self];
}
-(void)cleanPlugin:(UIInterfaceOrientation)orientation{
    self.webView = nil;
    [self.delegate donePluginWithOrientaion:self :orientation];
}
@end

