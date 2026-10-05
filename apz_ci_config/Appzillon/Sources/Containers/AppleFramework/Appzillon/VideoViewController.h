//
//  VideoViewController.h
//  Appzillon
//
//  Created by Manu N R on 08/08/17.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import <MediaPlayer/MediaPlayer.h>
@protocol videoViewDelegate

-(void)cancelVideoView :(UIInterfaceOrientation)interfaceOrientation;
@end
@interface VideoViewController : UIViewController <AVAudioPlayerDelegate>


@property (nonatomic, strong) MPMoviePlayerController *moviePlayer;
@property(assign,nonatomic)id<videoViewDelegate>delegate;

-(void)setupVideo:(NSString *)path withCustomNavigationItem: (BOOL) isCustomNavigationItemNeeded;
@end


