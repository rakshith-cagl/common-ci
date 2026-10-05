//
//  MusicViewController.m
//  Appzillon
//
//  Created by Manu N R on 16/08/17.
//

#import "MusicViewController.h"
#import "Logger.h"


@interface MusicViewController ()
@property (strong, nonatomic) UIImageView *albumImageView;
@property (strong, nonatomic) UILabel *albumTitleLabel;
@property (strong, nonatomic) UILabel *albumArtistLabel;
//@property (strong, nonatomic) UILabel *volumeLabel;
@property (strong, nonatomic) UILabel *albumNameLabel;
@property (strong, nonatomic) UIButton *playPauseButton;
@property (strong, nonatomic) UIButton *nextSongButton;
@property (strong, nonatomic) UIButton *previousSongButton;
//@property (strong, nonatomic) UISlider *volumeSlider;
@property (strong, nonatomic) UILabel *songSliderMaxValue;
@property (strong, nonatomic) UILabel *songSliderMinValue;
@property (strong, nonatomic) UISlider *songSlider;
@end


@implementation MusicViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.musicPlayer = [MPMusicPlayerController systemMusicPlayer];
    //[self.volumeSlider setValue:[self.musicPlayer volume]];
    
    [self getViewToBeDisplayed];
    
    if ([self.musicPlayer playbackState] == MPMusicPlaybackStatePlaying) {
        [self.playPauseButton setImage:[UIImage imageNamed:@"pauseIcon.png"] forState:UIControlStateNormal];
    } else {
        [self.playPauseButton setImage:[UIImage imageNamed:@"playIcon.png"] forState:UIControlStateNormal];
    }
    self.songSlider.value = 0;
    
    self.title = @"Song";
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelVideoView)];
    self.view.backgroundColor = [UIColor whiteColor];
    
    if (self.mediaItemCollection) {
        
        [self.musicPlayer setQueueWithItemCollection: self.mediaItemCollection];
        [self.musicPlayer prepareToPlay];
        [self registerMediaPlayerNotifications];
        NSNumber *duration = [self.musicPlayer.nowPlayingItem valueForProperty:MPMediaItemPropertyPlaybackDuration];
        float totalTime = [duration floatValue];
        self.songSlider.maximumValue = totalTime;
        self.songSlider.value = 00.00;
        self.songSliderMaxValue.text = [self convertSecondsToReadableTime:totalTime];
        
        [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(updateTime:) userInfo:nil repeats:YES];
        [self.musicPlayer play];
        [self.playPauseButton setImage:[UIImage imageNamed:@"pauseIcon.png"] forState:UIControlStateNormal];
        [self getSongInfo];
        
    }
}

- (void)updateTime:(NSTimer *)timer {
    self.songSlider.value = self.musicPlayer.currentPlaybackTime;
    self.songSliderMinValue.text = [self convertSecondsToReadableTime:self.musicPlayer.currentPlaybackTime];
    
}

- (void) registerMediaPlayerNotifications
{
    NSNotificationCenter *notificationCenter = [NSNotificationCenter defaultCenter];
    
    [notificationCenter addObserver: self
                           selector: @selector (handle_NowPlayingItemChanged:)
                               name: MPMusicPlayerControllerNowPlayingItemDidChangeNotification
                             object: self.musicPlayer];
    
    [notificationCenter addObserver: self
                           selector: @selector (handle_PlaybackStateChanged:)
                               name: MPMusicPlayerControllerPlaybackStateDidChangeNotification
                             object: self.musicPlayer];
    
    //    [notificationCenter addObserver: self
    //                           selector: @selector (handle_VolumeChanged:)
    //                               name: MPMusicPlayerControllerVolumeDidChangeNotification
    //                             object: self.musicPlayer];
    
    [self.musicPlayer beginGeneratingPlaybackNotifications];
}

- (void) handle_NowPlayingItemChanged: (id) notification
{
    [self getSongInfo];
}

- (void) handle_PlaybackStateChanged: (id) notification
{
    MPMusicPlaybackState playbackState = [self.musicPlayer playbackState];
    
    if (playbackState == MPMusicPlaybackStatePaused) {
        [self.playPauseButton setImage:[UIImage imageNamed:@"playIcon.png"] forState:UIControlStateNormal];
        
    } else if (playbackState == MPMusicPlaybackStatePlaying) {
        [self.playPauseButton setImage:[UIImage imageNamed:@"pauseIcon.png"] forState:UIControlStateNormal];
        
    } else if (playbackState == MPMusicPlaybackStateStopped) {
        
        [self.playPauseButton setImage:[UIImage imageNamed:@"playIcon.png"] forState:UIControlStateNormal];
        [self.musicPlayer stop];
        
    }
}

//- (void) handle_VolumeChanged: (id) notification
//{
//    [self.volumeSlider setValue:[self.musicPlayer volume]];
//}

-(void)cancelVideoView{
    
    [[NSNotificationCenter defaultCenter] removeObserver: self
                                                    name: MPMusicPlayerControllerNowPlayingItemDidChangeNotification
                                                  object: self.musicPlayer];
    
    [[NSNotificationCenter defaultCenter] removeObserver: self
                                                    name: MPMusicPlayerControllerPlaybackStateDidChangeNotification
                                                  object: self.musicPlayer];
    
    [[NSNotificationCenter defaultCenter] removeObserver: self
                                                    name: MPMusicPlayerControllerVolumeDidChangeNotification
                                                  object: self.musicPlayer];
    
    [self.musicPlayer endGeneratingPlaybackNotifications];
    [self.musicPlayer stop];
    [self dismissViewControllerAnimated:YES completion:^{
        [Logger logger_Log:@"I" :@"AudioViewController Cancelled"];
        [self.delegate cancelMusicView:self.interfaceOrientation];
    }];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

//- (void)volumeSliderAction{
//    [self.musicPlayer setVolume:[self.volumeSlider value]];
//}

- (void)songSliderAction {
    [self.musicPlayer setCurrentPlaybackTime: [self.songSlider value]];
}

-(void)playPauseAction{
    if ([self.musicPlayer playbackState] == MPMusicPlaybackStatePlaying) {
        [self.musicPlayer pause];
        [self.playPauseButton setImage:[UIImage imageNamed:@"playIcon.png"] forState:UIControlStateNormal];
        
    } else {
        [self.musicPlayer play];
        [self.playPauseButton setImage:[UIImage imageNamed:@"pauseIcon.png"] forState:UIControlStateNormal];
    }
}

- (void)previousSong {
    //    [self.musicPlayer skipToPreviousItem];
    //    NSNumber *duration = [self.musicPlayer.nowPlayingItem valueForProperty:MPMediaItemPropertyPlaybackDuration];
    //    float totalTime = [duration floatValue];
    //    self.songSlider.maximumValue = totalTime;
    //    self.songSlider.value = 0.0;
    //    self.songSliderMaxValue.text = [self convertSecondsToReadableTime:totalTime];
    //    [self getSongInfo];
}

- (void)nextSong {
    //    [self.musicPlayer skipToNextItem];
    //    NSNumber *duration = [self.musicPlayer.nowPlayingItem valueForProperty:MPMediaItemPropertyPlaybackDuration];
    //    float totalTime = [duration floatValue];
    //    self.songSlider.maximumValue = totalTime;
    //    self.songSlider.value = 0.0;
    //    self.songSliderMaxValue.text = [self convertSecondsToReadableTime:totalTime];
    //    [self getSongInfo];
}

-(NSString *) convertSecondsToReadableTime : (float) receivedSeconds{
    Float64 currentSeconds = receivedSeconds;
    int mins = currentSeconds/60.0;
    int secs = fmodf(currentSeconds, 60.0);
    NSString *minsString = mins < 10 ? [NSString stringWithFormat:@"0%d", mins] :      [NSString stringWithFormat:@"%d", mins];
    NSString *secsString = secs < 10 ? [NSString stringWithFormat:@"0%d", secs] : [NSString stringWithFormat:@"%d", secs];
    NSString *timeString = [NSString stringWithFormat:@"%@:%@",minsString,secsString];
    return timeString;
}

-(void) getSongInfo {
    MPMediaItem *currentItem = [self.musicPlayer nowPlayingItem];
    UIImage *artworkImage = [UIImage imageNamed:@"noPhoto.png"];
    MPMediaItemArtwork *artwork = [currentItem valueForProperty: MPMediaItemPropertyArtwork];
    if (artwork) {
        artworkImage = [artwork imageWithSize: CGSizeMake (240, 240)];
    }
    self.albumImageView.image = artworkImage;
    
    NSString *titleString = [currentItem valueForProperty:MPMediaItemPropertyTitle];
    if (titleString) {
        self.albumTitleLabel.text = [NSString stringWithFormat:@"Title: %@",titleString];
    } else {
        self.albumTitleLabel.text = @"Title: Unknown title";
    }
    
    NSString *artistString = [currentItem valueForProperty:MPMediaItemPropertyArtist];
    if (artistString) {
        self.albumArtistLabel.text = [NSString stringWithFormat:@"Artist: %@",artistString];
    } else {
        self.albumArtistLabel.text = @"Artist: Unknown artist";
    }
    
    NSString *albumString = [currentItem valueForProperty:MPMediaItemPropertyAlbumTitle];
    if (![albumString isEqualToString:@""]) {
        self.albumNameLabel.text = [NSString stringWithFormat:@"Album: %@",albumString];
    } else {
        self.albumNameLabel.text = @"Album: Unknown album";
    }
}

-(void)getViewToBeDisplayed{
    
    //self.volumeLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, self.view.frame.size.height - 20 - 30, 62, 30)];
    //self.volumeLabel.text = @"Volume:";
    // [self.view addSubview:self.volumeLabel];
    
    //    self.volumeSlider = [[UISlider alloc] initWithFrame:CGRectMake(self.volumeLabel.frame.origin.x + self.volumeLabel.frame.size.width + 10, self.view.frame.size.height - 20 - 30, self.view.frame.size.width - self.volumeLabel.frame.size.width - 20 - 20 - 10, 30)];
    //    [self.volumeSlider addTarget:self action:@selector(volumeSliderAction) forControlEvents:UIControlEventValueChanged];
    //    self.volumeSlider.continuous = YES;
    //    [self.volumeSlider setValue:[self.musicPlayer volume]];
    //    [self.view addSubview:self.volumeSlider];
    //
    self.songSliderMinValue = [[UILabel alloc] initWithFrame:CGRectMake(20, self.view.frame.size.height - 20 - 30, 50, 30)];
    self.songSliderMinValue.text = @"00:00";
    self.songSliderMinValue.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:self.songSliderMinValue];
    
    self.songSlider = [[UISlider alloc] initWithFrame:CGRectMake(self.songSliderMinValue.frame.origin.x + self.songSliderMinValue.frame.size.width + 10, self.view.frame.size.height - 20 - 30, self.view.frame.size.width - self.songSliderMinValue.frame.origin.x - self.songSliderMinValue.frame.size.width - 10 - self.songSliderMinValue.frame.origin.x - self.songSliderMinValue.frame.size.width - 10, 30)];
    [self.songSlider addTarget:self action:@selector(songSliderAction) forControlEvents:UIControlEventValueChanged];
    self.songSlider.continuous = YES;
    [self.view addSubview:self.songSlider];
    
    self.songSliderMaxValue = [[UILabel alloc] initWithFrame:CGRectMake(self.songSlider.frame.origin.x + self.songSlider.frame.size.width + 10, self.view.frame.size.height - 20 - 30, 50, 30)];
    self.songSliderMaxValue.text = @"00:00";
    self.songSliderMinValue.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:self.songSliderMaxValue];
    
    if([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone){
        
        //to check the device is in landscape mode
        if (self.view.frame.size.height < self.view.frame.size.width) {
            self.nextSongButton = [[UIButton alloc] initWithFrame:CGRectMake(self.view.frame.size.width - 20 - 40, 65, 40, 40)];
            self.playPauseButton = [[UIButton alloc] initWithFrame:CGRectMake(self.nextSongButton.frame.origin.x - 20 - 80, 40, 80, 80)];
            self.previousSongButton = [[UIButton alloc] initWithFrame:CGRectMake(self.playPauseButton.frame.origin.x - 20 - 40, 65 , 40, 40)];
            self.albumImageView = [[UIImageView alloc] initWithFrame:CGRectMake(20, 40, 180, 120)];;
            self.albumTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, self.albumImageView.frame.origin.y + self.albumImageView.frame.size.height + 20, self.view.frame.size.width - 40, 24)];
            self.albumArtistLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, self.albumTitleLabel.frame.origin.y + self.albumTitleLabel.frame.size.height + 20, self.view.frame.size.width - 40, 24)];
            self.albumNameLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, self.albumArtistLabel.frame.origin.y + self.albumArtistLabel .frame.size.height + 20, self.view.frame.size.width - 40, 24)];
            
        }else{
            self.playPauseButton = [[UIButton alloc] initWithFrame:CGRectMake(0, self.songSlider.frame.origin.y - 100, 80, 80)];
            self.playPauseButton.center = CGPointMake(CGRectGetMidX(self.view.bounds), self.playPauseButton.center.y);
            self.previousSongButton = [[UIButton alloc] initWithFrame:CGRectMake(self.playPauseButton.frame.origin.x - 20 - 40, self.songSlider.frame.origin.y - 100 + 25, 40, 40)];
            self.nextSongButton = [[UIButton alloc] initWithFrame:CGRectMake(self.playPauseButton.frame.origin.x + self.playPauseButton.frame.size.width +20, self.songSlider.frame.origin.y - 100 + 25, 40, 40)];
            self.albumImageView = [[UIImageView alloc] initWithFrame:CGRectMake(20, 85, 150, 150)];
            self.albumImageView.center = CGPointMake(CGRectGetMidX(self.view.bounds), self.albumImageView.center.y);
            self.albumTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, self.albumImageView.frame.origin.y + self.albumImageView.frame.size.height + 20, self.view.frame.size.width - 40, 24)];
            self.albumArtistLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, self.albumTitleLabel.frame.origin.y + self.albumTitleLabel.frame.size.height + 20, self.view.frame.size.width - 40, 24)];
            self.albumNameLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, self.albumArtistLabel.frame.origin.y + self.albumArtistLabel .frame.size.height + 20, self.view.frame.size.width - 40, 24)];
        }
    }else{
        self.playPauseButton = [[UIButton alloc] initWithFrame:CGRectMake(0, self.songSlider.frame.origin.y - 120, 100, 100)];
        self.playPauseButton.center = CGPointMake(CGRectGetMidX(self.view.bounds), self.playPauseButton.center.y);
        self.previousSongButton = [[UIButton alloc] initWithFrame:CGRectMake(self.playPauseButton.frame.origin.x - 20 - 50, self.songSlider.frame.origin.y - 120 + 25, 50, 50)];
        self.nextSongButton = [[UIButton alloc] initWithFrame:CGRectMake(self.playPauseButton.frame.origin.x + self.playPauseButton.frame.size.width +20, self.songSlider.frame.origin.y - 120 + 25, 50, 50)];
        self.albumImageView = [[UIImageView alloc] initWithFrame:CGRectMake(20, 85, 250, 250)];
        self.albumImageView.center = CGPointMake(CGRectGetMidX(self.view.bounds), self.albumImageView.center.y);
        self.albumTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, self.albumImageView.frame.origin.y + self.albumImageView.frame.size.height + 20, self.view.frame.size.width - 40, 24)];
        self.albumArtistLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, self.albumTitleLabel.frame.origin.y + self.albumTitleLabel.frame.size.height + 20, self.view.frame.size.width - 40, 24)];
        self.albumNameLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, self.albumArtistLabel.frame.origin.y + self.albumArtistLabel .frame.size.height + 20, self.view.frame.size.width - 40, 24)];
    }
    
    [self.playPauseButton addTarget:self action:@selector(playPauseAction) forControlEvents:UIControlEventTouchUpInside];
    [self.playPauseButton setImage:[UIImage imageNamed:@"playIcon.png"] forState:UIControlStateNormal];
    [self.view addSubview:self.playPauseButton];
    
    [self.previousSongButton addTarget:self action:@selector(previousSong) forControlEvents:UIControlEventTouchUpInside];
    [self.previousSongButton setImage:[UIImage imageNamed:@"previousSong.png"] forState:UIControlStateNormal];
    [self.view addSubview:self.previousSongButton];
    
    [self.nextSongButton addTarget:self action:@selector(nextSong) forControlEvents:UIControlEventTouchUpInside];
    [self.nextSongButton setImage:[UIImage imageNamed:@"nextSong.png"] forState:UIControlStateNormal];
    [self.view addSubview:self.nextSongButton];
    
    self.albumImageView.image = [UIImage imageNamed:@"noPhoto.png"];
    [self.view addSubview:self.albumImageView];
    
    self.albumTitleLabel.text = @"Title: Unknown title";
    self.albumTitleLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:self.albumTitleLabel];
    
    self.albumArtistLabel.text = @"Artist: Unknown artist";
    self.albumArtistLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:self.albumArtistLabel];
    
    self.albumNameLabel.text = @"Album: Unknown album";
    self.albumNameLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:self.albumNameLabel];
}

-(void) willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    
}
-(void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator
{
    //to check the device is in landscape mode or not and to display the view accordingly
    if (size.width > self.view.frame.size.width) {
        CGRect displayRect = CGRectMake(0, 0, size.width, size.height);
        //self.volumeLabel.frame  = CGRectMake(20, size.height - 20 - 30, 62, 30);
        //self.volumeSlider.frame = CGRectMake(self.volumeLabel.frame.origin.x + self.volumeLabel.frame.size.width + 10, size.height - 20 - 30, size.width - self.volumeLabel.frame.size.width - 20 - 20 - 10, 30);
        self.songSliderMinValue.frame = CGRectMake(20, size.height - 20 - 30, 50, 30);
        self.songSlider.frame = CGRectMake(self.songSliderMinValue.frame.origin.x + self.songSliderMinValue.frame.size.width + 10, size.height - 20 - 30, size.width - self.songSliderMinValue.frame.origin.x - self.songSliderMinValue.frame.size.width - 10 - self.songSliderMinValue.frame.origin.x - self.songSliderMinValue.frame.size.width - 10, 30);
        self.songSliderMaxValue.frame = CGRectMake(self.songSlider.frame.origin.x + self.songSlider.frame.size.width + 10, size.height - 20 - 30, 50, 30);
        if([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone){
            self.nextSongButton.frame = CGRectMake(size.width - 20 - 40, 65, 40, 40);
            self.playPauseButton.frame = CGRectMake(self.nextSongButton.frame.origin.x - 20 - 80, 40, 80, 80);
            self.previousSongButton.frame = CGRectMake(self.playPauseButton.frame.origin.x - 20 - 40, 65 , 40, 40);
            self.albumImageView.frame = CGRectMake(20, 40, 180, 120);
            
        }else{
            self.playPauseButton.frame = CGRectMake(0, self.songSlider.frame.origin.y - 120, 100, 100);
            self.playPauseButton.center = CGPointMake(CGRectGetMidX(displayRect), self.playPauseButton.center.y);
            self.previousSongButton.frame = CGRectMake(self.playPauseButton.frame.origin.x - 20 - 50, self.songSlider.frame.origin.y - 120 + 25, 50, 50);
            self.nextSongButton.frame = CGRectMake(self.playPauseButton.frame.origin.x + self.playPauseButton.frame.size.width +20, self.songSlider.frame.origin.y - 120 + 25, 50, 50);
            self.albumImageView.frame = CGRectMake(20, 85, 250, 250);
            self.albumImageView.center = CGPointMake(CGRectGetMidX(displayRect), self.albumImageView.center.y);
        }
        self.albumTitleLabel.frame = CGRectMake(20, self.albumImageView.frame.origin.y + self.albumImageView.frame.size.height + 20, size.width - 40, 24);
        self.albumArtistLabel.frame = CGRectMake(20, self.albumTitleLabel.frame.origin.y + self.albumTitleLabel.frame.size.height + 20, size.width - 40, 24);
        self.albumNameLabel.frame = CGRectMake(20, self.albumArtistLabel.frame.origin.y + self.albumArtistLabel .frame.size.height + 20, size.width - 40, 24);
    }else{
        CGRect displayRect = CGRectMake(0, 0, size.width, size.height);
        //self.volumeLabel.frame  = CGRectMake(20, size.height - 20 - 30, 62, 30);
        //self.volumeSlider.frame = CGRectMake(self.volumeLabel.frame.origin.x + self.volumeLabel.frame.size.width + 10, size.height - 20 - 30, size.width - self.volumeLabel.frame.size.width - 20 - 20 - 10, 30);
        self.songSliderMinValue.frame = CGRectMake(20, size.height - 20 - 30, 50, 30);
        self.songSlider.frame = CGRectMake(self.songSliderMinValue.frame.origin.x + self.songSliderMinValue.frame.size.width + 10, size.height - 20 - 30, size.width - self.songSliderMinValue.frame.origin.x - self.songSliderMinValue.frame.size.width - 10 - self.songSliderMinValue.frame.origin.x - self.songSliderMinValue.frame.size.width - 10, 30);
        self.songSliderMaxValue.frame = CGRectMake(self.songSlider.frame.origin.x + self.songSlider.frame.size.width + 10, size.height - 20 - 30, 50, 30);
        if([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone){
            self.playPauseButton.frame = CGRectMake(0, self.songSlider.frame.origin.y - 100, 80, 80);
            self.playPauseButton.center = CGPointMake(CGRectGetMidX(displayRect), self.playPauseButton.center.y);
            self.previousSongButton.frame = CGRectMake(self.playPauseButton.frame.origin.x - 20 - 40, self.songSlider.frame.origin.y - 100 + 25, 40, 40);
            self.nextSongButton.frame = CGRectMake(self.playPauseButton.frame.origin.x + self.playPauseButton.frame.size.width +20, self.songSlider.frame.origin.y - 100 + 25, 40, 40);
            self.albumImageView.frame = CGRectMake(20, 85, 150, 150);
            
        }else{
            self.playPauseButton.frame = CGRectMake(0, self.songSlider.frame.origin.y - 120, 100, 100);
            self.playPauseButton.center = CGPointMake(CGRectGetMidX(displayRect), self.playPauseButton.center.y);
            self.previousSongButton.frame = CGRectMake(self.playPauseButton.frame.origin.x - 20 - 50, self.songSlider.frame.origin.y - 120 + 25, 50, 50);
            self.nextSongButton.frame = CGRectMake(self.playPauseButton.frame.origin.x + self.playPauseButton.frame.size.width +20, self.songSlider.frame.origin.y - 120 + 25, 50, 50);
            self.albumImageView.frame = CGRectMake(20, 85, 250, 250);
        }
        self.albumImageView.center = CGPointMake(CGRectGetMidX(displayRect), self.albumImageView.center.y);
        self.albumTitleLabel.frame = CGRectMake(20, self.albumImageView.frame.origin.y + self.albumImageView.frame.size.height + 20, size.width - 40, 24);
        self.albumArtistLabel.frame = CGRectMake(20, self.albumTitleLabel.frame.origin.y + self.albumTitleLabel.frame.size.height + 20, size.width - 40, 24);
        self.albumNameLabel.frame = CGRectMake(20, self.albumArtistLabel.frame.origin.y + self.albumArtistLabel .frame.size.height + 20, size.width - 40, 24);
    }
}

@end
