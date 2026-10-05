//
//  MusicGalleryController.m
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//

#import "MusicGalleryController.h"
#import "AppzillonViewController.h"
#import <MediaPlayer/MediaPlayer.h>
#import "Constants.h"
#import "APZJsonUtil.h"
#import "Logger.h"
#import "MusicViewController.h"


@interface MusicGalleryController()<MPMediaPickerControllerDelegate,audioViewDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
//@property(nonatomic, strong) MPMusicPlayerController *musicPlayer;
@property (strong, nonatomic) NSDictionary *jsonDetails;
@property(nonatomic,strong) NSString *fileBrowserOpenFile;
@property(nonatomic,strong) NSString *filterByExtensions;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation MusicGalleryController

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}



-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"MusicGalleryController--Execute"];
    if(jsonDict !=nil){
        self.pluginId=[jsonDict objectForKey:PLUGINID];
        self.fileBrowserOpenFile = [jsonDict objectForKey:FILEBROWSER_OPENFILE];
        self.filterByExtensions = [jsonDict objectForKey:FILEBROWSER_FILTER];
        [self musicGallery];
    }
    else{
        [self cleanUp];
    }
}

#pragma mark -Music Gallery Browser
-(void)musicGallery{
    MPMediaPickerController *mediaPickerController = [[MPMediaPickerController alloc]
                                                      initWithMediaTypes:MPMediaTypeAnyAudio];
    if (mediaPickerController != nil){
        mediaPickerController.delegate = self;
        mediaPickerController.allowsPickingMultipleItems = NO;
        [mediaPickerController setModalPresentationStyle:UIModalPresentationFullScreen];
        [self.viewController presentViewController:mediaPickerController animated:YES completion:nil];
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:MUSIC_PALYER_MISSING],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"MusicGalleryController--music player is missing"];
        [self.viewController dismissViewControllerAnimated:YES completion:nil];
        [self cleanUp];
    }
}

- (void)mediaPicker:(MPMediaPickerController *)mediaPicker didPickMediaItems:(MPMediaItemCollection *)mediaItemCollection
{
    MPMediaItem *thisItem =[mediaItemCollection.items objectAtIndex:0];
    NSURL *itemURL =[thisItem valueForProperty:MPMediaItemPropertyAssetURL];
    NSData *songData = [NSData dataWithContentsOfURL:itemURL];
    if (songData){
        NSString *documentsDirectory = NSTemporaryDirectory();
        NSString *fileName = [NSString stringWithFormat:@"Music"];
        fileName = [fileName stringByAppendingString:@".mp3"];
        documentsDirectory=[documentsDirectory stringByAppendingPathComponent:fileName];
//        BOOL savedAudio =[songData writeToFile:documentsDirectory atomically:YES];
        BOOL savedAudio = [MiscellaneousMethods writeData:songData toFile:documentsDirectory];
        if(savedAudio){
            [Logger logger_Log:@"I" :@"MusicGalleryController--Saved audio to tmp"];
        }
        else{
            [Logger logger_Log:@"I" :@"MusicGalleryController--Not able to save audio to tmp"];
        }
    }
    else{
        [Logger logger_Log:@"I" :@"MusicGalleryController--Unable to save image to url"];
    }
    if([self.fileBrowserOpenFile isEqualToString:@"Y"]){
        MusicViewController *musicController = [[MusicViewController alloc] initWithNibName:nil bundle:nil];
        musicController.delegate=self;
        musicController.mediaItemCollection = mediaItemCollection;
        UINavigationController *navigationController =
        [[UINavigationController alloc] initWithRootViewController:musicController];
        [self.viewController dismissViewControllerAnimated:YES completion:nil];
        [navigationController setModalPresentationStyle:UIModalPresentationFullScreen];
        [self.viewController presentViewController:navigationController animated:YES completion:nil];
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:FILEBROWSER_PATH,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",[itemURL absoluteString]], nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [self.viewController dismissViewControllerAnimated:YES completion:nil];
        [self cleanUp];
    }
}

- (void) mediaPickerDidCancel:(MPMediaPickerController *)mediaPicker{
    [mediaPicker dismissViewControllerAnimated:YES completion:^{
        [self cleanUp:[[UIApplication sharedApplication] statusBarOrientation]];
    }];
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"MusicGalleryController--cancelled"];
    [self cleanUp];
    
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
    [self.delegate doneMusicGallery:self];
}

-(void)cleanUp :(UIInterfaceOrientation)orientation{
    self.webView = nil;
    self.jsonDetails = nil;
    self.fileBrowserOpenFile = nil;
    self.filterByExtensions = nil;
    [self.delegate doneMusicGalleryWithOrientation:orientation];
}

-(void)cancelMusicView:(UIInterfaceOrientation)interfaceOrientation{
    [self cleanUp: interfaceOrientation];
}

@end

