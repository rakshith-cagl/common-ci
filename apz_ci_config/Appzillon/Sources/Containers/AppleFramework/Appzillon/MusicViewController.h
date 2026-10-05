//
//  MusicViewController.h
//  Appzillon
//
//  Created by Manu N R on 16/08/17.
//

#import <UIKit/UIKit.h>
#import <MediaPlayer/MediaPlayer.h>

@protocol audioViewDelegate

-(void)cancelMusicView :(UIInterfaceOrientation)interfaceOrientation;
@end

@interface MusicViewController : UIViewController<MPMediaPickerControllerDelegate>

@property (nonatomic, strong) MPMusicPlayerController *musicPlayer;
@property (nonatomic, strong) MPMediaItemCollection *mediaItemCollection;
@property(assign,nonatomic)id<audioViewDelegate>delegate;

-(void)setupAudioMediaItem:(MPMediaItemCollection *)mediaItemCollection;
@end

