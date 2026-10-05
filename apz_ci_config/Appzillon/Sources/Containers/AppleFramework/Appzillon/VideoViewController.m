//
//  VideoViewController.m
//  Appzillon
//
//  Created by Manu N R on 08/08/17.
//

#import "VideoViewController.h"
#import "Logger.h"

@interface VideoViewController ()
@property(nonatomic,strong)NSString *videoPath;
@property (nonatomic)BOOL customCancelButton;



@end

@implementation VideoViewController

-(void)viewDidLoad {
    [super viewDidLoad];
    if (self.customCancelButton) {
        //        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelVideoView)];
    }
    [self playVideo:self.videoPath];
    
}


-(void)setupVideo:(NSString *)path withCustomNavigationItem: (BOOL) isCustomNavigationItemNeeded{
    self.videoPath = path;
    self.customCancelButton = isCustomNavigationItemNeeded;
    
}

-(void)cancelVideoView{
    [self stopPlayingVideo:nil];
    [self.moviePlayer.view removeFromSuperview];
    [self dismissViewControllerAnimated:YES completion:^{
        [Logger logger_Log:@"I" :@"VideoViewController Cancelled"];
        [self.delegate cancelVideoView:self.interfaceOrientation];
    }];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Video Player
-(void)playVideo:(NSString *)selectedFile{
    if (self.moviePlayer != nil){
        [self stopPlayingVideo:nil];
    }
    
    NSURL *videoURL = [NSURL fileURLWithPath:selectedFile];
    self.moviePlayer = [[MPMoviePlayerController alloc] initWithContentURL:videoURL];
    
    if (self.moviePlayer != nil){
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(videoHasFinishedPlaying:)
                                                     name:MPMoviePlayerPlaybackDidFinishNotification
                                                   object: self.moviePlayer];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(videoHasStateChanged:)
                                                     name:MPMoviePlayerNowPlayingMovieDidChangeNotification
                                                   object: self.moviePlayer];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(playbackHasStateChanged:)
                                                     name:MPMoviePlayerPlaybackStateDidChangeNotification
                                                   object: self.moviePlayer];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(videoHasChanged:)
                                                     name:MPMoviePlayerLoadStateDidChangeNotification
                                                   object: self.moviePlayer];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(MPMoviePlayerDidExitFullscreen:) name:MPMoviePlayerDidExitFullscreenNotification object:nil];
        
        
        //
        self.moviePlayer.view.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
        self.moviePlayer.scalingMode = MPMovieScalingModeAspectFit;
        self.moviePlayer.controlStyle = MPMovieControlStyleEmbedded;
        self.moviePlayer.shouldAutoplay = YES;
        [self.view addSubview: self.moviePlayer.view];
        [self.moviePlayer play];
        [self.moviePlayer setFullscreen:YES animated:YES];
    }
    else {
        [Logger logger_Log:@"I" :@"VideoViewController-Failed to instantiate the movie player"];
    }
}

- (void) stopPlayingVideo:(id)paramSender{
    if (self.moviePlayer != nil){
        [[NSNotificationCenter defaultCenter]
         removeObserver:self name:MPMoviePlayerPlaybackDidFinishNotification
         object:self.moviePlayer];
        
        [[NSNotificationCenter defaultCenter]
         removeObserver:self name:MPMoviePlayerNowPlayingMovieDidChangeNotification
         object:self.moviePlayer];
        
        [[NSNotificationCenter defaultCenter]
         removeObserver:self name:MPMoviePlayerLoadStateDidChangeNotification
         object:self.moviePlayer];
        
        [[NSNotificationCenter defaultCenter]
         removeObserver:self name:MPMoviePlayerPlaybackStateDidChangeNotification
         object:self.moviePlayer];
        
        [[NSNotificationCenter defaultCenter]
         removeObserver:self name:MPMoviePlayerDidExitFullscreenNotification
         object:self.moviePlayer];
        
        [self.moviePlayer stop];
        if ([self.moviePlayer.view.superview isEqual:self.view]){
            [self.moviePlayer.view removeFromSuperview];
            self.moviePlayer = nil;
        }
        else{
            [Logger logger_Log:@"I" :@"VideoViewController-No Removing movie View"];
        }
    }
}


#pragma mark - Movie player notifications
- (void) videoHasStateChanged:(NSNotification *)paramNotification{
    [self.moviePlayer setFullscreen:YES animated:YES];
    [Logger logger_Log:@"I" :@"VideoViewController-video has state changed"];
}

-(void)playbackHasStateChanged:(NSNotification *)paramNotification{
    if (self.moviePlayer.playbackState == MPMoviePlaybackStatePlaying)
    {
        [self.moviePlayer setFullscreen:YES animated:YES];
    }
}

- (void)videoHasChanged:(NSNotification *)paramNotification{
    [self.moviePlayer setFullscreen:YES animated:YES];
    [Logger logger_Log:@"I" :@"VideoViewController-video has changed"];
}

- (void) videoHasFinishedPlaying:(NSNotification *)paramNotification{
    NSNumber *reason = [paramNotification.userInfo
                        valueForKey:MPMoviePlayerPlaybackDidFinishReasonUserInfoKey];
    if (reason != nil){
        NSInteger reasonAsInteger = [reason integerValue];
        switch (reasonAsInteger){
            case MPMovieFinishReasonPlaybackEnded:{
                [Logger logger_Log:@"I" :@"VideoViewController-The movie ended normally"];
                break;
            }
            case MPMovieFinishReasonPlaybackError:{
                [Logger logger_Log:@"I" :@"VideoViewController-An error happened and the movie ended"];
                break;
            }
            case MPMovieFinishReasonUserExited:{
                [Logger logger_Log:@"I" :@"VideoViewController-The user exited the player"];
                break;
            }
        }
        [Logger logger_Log:@"I" :@"VideoViewController-The user exited the player"];
        [self stopPlayingVideo:nil];
    }
    else{
        NSLog(@"---Unknown reason for video to cancel");
    }
    [self cancelVideoView];
}

- (void)MPMoviePlayerDidExitFullscreen:(NSNotification *)notification
{
    [self cancelVideoView];
}

-(void) willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    [self.moviePlayer setFullscreen:YES animated:YES];
    self.moviePlayer.view.frame=CGRectMake(0, 0,self.view.frame.size.width, self.view.frame.size.height);
}

-(void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator
{
    [self.moviePlayer setFullscreen:YES animated:YES];
    self.moviePlayer.view.frame=CGRectMake(0, 0,self.view.frame.size.width, self.view.frame.size.height);
}

@end
