//
//  FileLocationController.m
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//

#import "FileLocationController.h"
#import "Constants.h"
#import <QuickLook/QuickLook.h>
#import <MediaPlayer/MediaPlayer.h>
#import <CoreAudio/CoreAudioTypes.h>
#import <AVFoundation/AVFoundation.h>
#import "ImageViewController.h"
#import "Logger.h"

@interface FileLocationController ()<QLPreviewControllerDataSource,
QLPreviewControllerDelegate,AVAudioPlayerDelegate, UIDocumentInteractionControllerDelegate>
@property(nonatomic,strong)NSIndexPath *prevCellIndexPath;
@property(nonatomic,strong)NSArray *filesArr;
@property(nonatomic,strong)NSString *dirPath;
@property(nonatomic,strong)NSString *openType;
@property(nonatomic,strong)NSString *selectedFile;
@property(nonatomic,strong)NSString *selectedExtension;
@property (nonatomic, strong) NSMutableArray *documentURLs;
@property(nonatomic,strong)  QLPreviewController *previewController;
@property(strong,nonatomic)AVAudioPlayer *audioPlayer;
@property (nonatomic, strong) MPMoviePlayerController *moviePlayer;
@property(nonatomic,strong)NSString *fileFilters;
@property(nonatomic, strong)UIDocumentInteractionController *documentInteractionController;
@end

@implementation FileLocationController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
    }
    return self;
}

-(void)initFiles:(NSArray *)fileArray directory:(NSString *)directoryPath openType:(NSString *)type{
    self.filesArr = fileArray;
    self.dirPath = directoryPath;
    if([type isEqualToString:@""]){
        self.openType = @"N";
    }
    else{
        self.openType = type;
    }
}

- (void)viewDidLoad
{
    self.title = @"Files";
    if([self.openType isEqualToString:@"N"]){
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(done)];
    }
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancel)];
    //    self.tableView.delegate = self;
    //    self.tableView.dataSource = self;
    [super viewDidLoad];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - Table view data source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.filesArr.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentifier = @"CellIdentifier";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier];
    }
    cell.textLabel.text = [self.filesArr  objectAtIndex:indexPath.row];
    //    cell.userInteractionEnabled = YES;
    if([self.openType isEqualToString:@"Y"]){
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    else{
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    return cell;
}

#pragma mark - Table view delegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if(self.prevCellIndexPath != nil){
        UITableViewCell *oldCell = [tableView cellForRowAtIndexPath:self.prevCellIndexPath];
        if (oldCell.accessoryType == UITableViewCellAccessoryCheckmark) {
            oldCell.accessoryType = UITableViewCellAccessoryNone;
        }
    }
    if([self.openType isEqualToString:@"N"]){
        [tableView cellForRowAtIndexPath:indexPath].accessoryType = UITableViewCellAccessoryCheckmark;
        BOOL isDirectory;
        [[NSFileManager defaultManager] fileExistsAtPath:[self.dirPath stringByAppendingPathComponent:[self.filesArr objectAtIndex:indexPath.row]] isDirectory:&isDirectory];
        if(isDirectory){
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                dispatch_async(dispatch_get_main_queue(), ^ {
                    self.dirPath=[self.dirPath stringByAppendingPathComponent:[self.filesArr objectAtIndex:indexPath.row]];
                    self.filesArr=[self getAllDirectoryPlusFiles];
                    [self.tableView reloadData];
                });
            });
        }else{
            self.selectedFile = [self.filesArr objectAtIndex:indexPath.row];
        }
    }
    else{
        [self openFile:[self.filesArr objectAtIndex:indexPath.row]
                forRow:indexPath.row];
    }
    self.prevCellIndexPath = indexPath;
}

#pragma mark - Done LocationBrowser
-(void)done{
    NSString *finalPath =[self.dirPath stringByAppendingPathComponent:self.selectedFile];
    NSString *fileName=self.selectedFile;
    [self dismissViewControllerAnimated:YES completion:^{
        [self.delegate selectedFile:fileName path:finalPath :self.interfaceOrientation];
    }];
    self.selectedFile = nil;
    //[self cancel];
}

#pragma mark - Cancel LocationBrowser
-(void)cancel{
    [self dismissViewControllerAnimated:YES completion:^{
        [self.delegate cancelBrowser:self.interfaceOrientation];
    }];
}

#pragma mark - Detail File Viewer
-(void)openFile:(NSString *)selectedFile forRow:(NSInteger)row{
    NSString *selFileExtension = @".";
    selFileExtension = [selFileExtension stringByAppendingString:[selectedFile pathExtension]];
    
    BOOL isAudio = (
                    [selFileExtension isEqualToString:FILEBROWSER_AUDIO_EXTENSION_MP3]
                    || [selFileExtension isEqualToString:FILEBROWSER_AUDIO_EXTENSION_AMR]
                    || [selFileExtension isEqualToString:FILEBROWSER_AUDIO_EXTENSION_CAF]
                    );
    BOOL isImage = (
                    [selFileExtension isEqualToString:FILEBROWSER_IMAGE_EXTENSION_JPG]||
                    [selFileExtension isEqualToString:FILEBROWSER_IMAGE_EXTENSION_PNG]
                    );
    BOOL isDocs = (
                   [selFileExtension isEqualToString:FILEBROWSER_DOCUMENTS_EXTENSION_DOCX] ||
                   [selFileExtension isEqualToString:FILEBROWSER_DOCUMENTS_EXTENSION_XLSX] ||
                   [selFileExtension isEqualToString:FILEBROWSER_DOCUMENTS_EXTENSION_PPT] ||
                   [selFileExtension isEqualToString:FILEBROWSER_DOCUMENTS_EXTENSION_PDF]
                   );
    BOOL isVideo = (
                    [selFileExtension isEqualToString:FILEBROWSER_VIDEO_EXTENSION_MP4]||
                    [selFileExtension isEqualToString:FILEBROWSER_VIDEO_EXTENSION_MPG]||
                    [selFileExtension isEqualToString:FILEBROWSER_VIDEO_EXTENSION_MPEG] ||
                    [selFileExtension isEqualToString:FILEBROWSER_VIDEO_EXTENSION_AMR]
                    );
    
    BOOL isDirectory;
    [[NSFileManager defaultManager] fileExistsAtPath:[self.dirPath stringByAppendingPathComponent:selectedFile] isDirectory:&isDirectory];
    
    if(isAudio)
    {
        // [self playMusic:selectedFile];
        [self viewDocument:row];
    }
    else if (isDocs)
    {
        [self viewDocument:row];
    }
    else if(isImage)
    {
        [self showImage:selectedFile];
    }
    else if(isVideo)
    {
        [self playVideo:selectedFile];
    }
    else if(isDirectory){
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^ {
                self.dirPath=[self.dirPath stringByAppendingPathComponent:selectedFile];
                self.filesArr=[self getAllDirectoryPlusFiles];
                [self.tableView reloadData];
            });
        });
    }else{
        [self viewDocument:row];
    }
}
- (NSArray *)getAllDirectoryPlusFiles{
    NSFileManager *fileManager =[NSFileManager defaultManager];
    NSArray *listOfFilesAndDirectories=[fileManager contentsOfDirectoryAtPath:self.dirPath error:nil];
    
    //    NSError *error;
    //    if ([listOfFilesAndDirectories count] > 0 && error == nil){
    //        if([self.fileFilters isEqualToString:@""] == NO)
    //        {
    //            NSArray *extensions = [self.fileFilters componentsSeparatedByString:FILEBROWSER_FILTER_SEPERATOR];
    //            NSMutableArray *subpredicates = [@[] mutableCopy];
    //            for (NSString *extension in extensions) {
    //                [subpredicates addObject:[NSPredicate predicateWithFormat:@"SELF ENDSWITH %@", extension]];
    //            }
    //            NSPredicate *filter = [NSCompoundPredicate orPredicateWithSubpredicates:subpredicates];
    //            listOfFilesAndDirectories= [listOfFilesAndDirectories filteredArrayUsingPredicate:filter];
    //        }
    //        else{
    //            [Logger logger_Log:@"I" :@"FileViewController-No Extension to filter by extensions"];
    //        }
    //    }
    return listOfFilesAndDirectories;
}


#pragma mark - ImageViewer
-(void)showImage:(NSString *)selectedImage{
    NSString *finalPath =[self.dirPath stringByAppendingPathComponent:selectedImage];
    ImageViewController *imgViewController = [[ImageViewController alloc]init];
    [imgViewController setupImage:finalPath];
    [self.navigationController pushViewController:imgViewController animated:YES];
    
}

#pragma mark - Music Player
-(void)playMusic:(NSString *)selectedFile{
    if([self.audioPlayer isPlaying]){
        [self.audioPlayer stop];
        self.audioPlayer = nil;
    }
    else{
        [Logger logger_Log:@"I" :@"FilelocationController-No Audio Playing"];
    }
    NSString *finalPath =[self.dirPath stringByAppendingPathComponent:selectedFile];
    NSURL *audioURL = [[NSURL alloc] initFileURLWithPath:finalPath];
    NSError *error;
    self.audioPlayer = [[AVAudioPlayer alloc]
                        initWithContentsOfURL:audioURL
                        error:&error];
    
    if (self.audioPlayer != nil)
    {
        self.audioPlayer.delegate = self;
        if ([self.audioPlayer prepareToPlay] &&
            [self.audioPlayer play])
        {
            [Logger logger_Log:@"I" :@"FilelocationController-Successfully started playing"];
            
        }
    }
    else{
        [Logger logger_Log:@"I" :@"FilelocationController-Unable to create Audio Player-"];
    }
}

#pragma mark-Audio Player Delegation Callbacks
-(void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag
{
    self.audioPlayer = nil;
}

-(void)audioPlayerBeginInterruption:(AVAudioPlayer *)player{
    [Logger logger_Log:@"I" :@"FilelocationController-Audio Player is interupted here-"];
}

-(void)audioPlayerEndInterruption:(AVAudioPlayer *)player withOptions:(NSUInteger)flags{
    [Logger logger_Log:@"I" :@"FilelocationController-Audio Player end interuption-"];
}

- (void)audioPlayerDecodeErrorDidOccur:(AVAudioPlayer *)player error:(NSError *)error{
    self.audioPlayer = nil;
    
}

#pragma mark - Document Viewer
-(void)viewDocument:(NSUInteger)index{
    
    self.documentURLs = [NSMutableArray array];
    [self initDocumentsURL:self.filesArr];
    QLPreviewController *previewController = [[QLPreviewController alloc] init];
    previewController.dataSource = self;
    previewController.delegate = self;
    previewController.currentPreviewItemIndex = index;
    [self.navigationController pushViewController:previewController animated:YES];
    
    //        NSIndexPath *selectedIndexPath = [self.tableView indexPathForSelectedRow];
    //        NSURL *fileURL =[NSURL fileURLWithPath:self.documentURLs[selectedIndexPath.row]];
    //        NSURL *fileURL = [NSURL URLWithString:@"ipod-library://item/item.m4a?id=809397921538132533"];
    //
    //        // NSURL *URL = [[NSBundle mainBundle] URLForResource:@"sample" withExtension:@"pdf"];
    //
    //            if (fileURL) {
    //                // Initialize Document Interaction Controller
    //                self.documentInteractionController = [UIDocumentInteractionController interactionControllerWithURL:fileURL];
    //
    //                // Configure Document Interaction Controller
    //                [self.documentInteractionController setDelegate:self];
    //
    //                // Preview PDF
    //                [self.documentInteractionController presentPreviewAnimated:YES];
    //            }
    
}

-(void)initDocumentsURL:(NSArray *)fileArr{
    NSUInteger count = fileArr.count;
    for(int i=0;i<count;i++){
        NSString *filePath =[self.dirPath stringByAppendingPathComponent:fileArr[i]];
        [self.documentURLs addObject:filePath];
    }
}

- (UIViewController *) documentInteractionControllerViewControllerForPreview: (UIDocumentInteractionController *) controller {
    return self;
}

#pragma mark -QLPreviewControllerDataSource
- (NSInteger)numberOfPreviewItemsInPreviewController:(QLPreviewController *)previewController
{
    return self.documentURLs.count;
}

- (void)previewControllerDidDismiss:(QLPreviewController *)controller
{
    [controller dismissViewControllerAnimated:YES completion:nil];
}

- (id)previewController:(QLPreviewController *)previewController
     previewItemAtIndex:(NSInteger)idx
{
    NSIndexPath *selectedIndexPath = [self.tableView indexPathForSelectedRow];
    NSURL *fileURL =[NSURL fileURLWithPath:self.documentURLs[selectedIndexPath.row]];
    return fileURL;
}

#pragma mark - Video Player
-(void)playVideo:(NSString *)selectedFile{
    if (self.moviePlayer != nil){
        [self stopPlayingVideo:nil];
    }
    NSString *videoPath = [self.dirPath stringByAppendingPathComponent:selectedFile];
    NSURL *videoURL = [NSURL fileURLWithPath:videoPath];
    self.moviePlayer = [[MPMoviePlayerController alloc] initWithContentURL:videoURL];
    if ( self.moviePlayer != nil){
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(videoHasFinishedPlaying:)
                                                     name:MPMoviePlayerPlaybackDidFinishNotification
                                                   object: self.moviePlayer];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(videoHasStateChanged:)
                                                     name:MPMoviePlayerNowPlayingMovieDidChangeNotification
                                                   object: self.moviePlayer];
        
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(videoHasChanged:)
                                                     name:MPMoviePlayerLoadStateDidChangeNotification
                                                   object: self.moviePlayer];
        self.moviePlayer.scalingMode = MPMovieScalingModeAspectFit;
        self.moviePlayer.controlStyle = MPMovieControlStyleEmbedded;
        self.moviePlayer.shouldAutoplay = YES;
        [self.view addSubview: self.moviePlayer.view];
        [ self.moviePlayer setFullscreen:YES animated:YES];
        [ self.moviePlayer play];
    }
    else {
        [Logger logger_Log:@"I" :@"Failed to instantiate the movie player"];
    }
}

- (void) stopPlayingVideo:(id)paramSender{
    if (self.moviePlayer != nil){
        [[NSNotificationCenter defaultCenter]
         removeObserver:self name:MPMoviePlayerPlaybackDidFinishNotification
         object:self.moviePlayer];
        
        [[NSNotificationCenter defaultCenter]
         removeObserver:self name:MPMoviePlayerPlaybackStateDidChangeNotification
         object:self.moviePlayer];
        
        [[NSNotificationCenter defaultCenter]
         removeObserver:self name:MPMoviePlayerLoadStateDidChangeNotification
         object:self.moviePlayer];
        
        [self.moviePlayer stop];
        if ([self.moviePlayer.view.superview isEqual:self.view]){
            [self.moviePlayer.view removeFromSuperview];
            self.moviePlayer = nil;
        }
        else{
            [Logger logger_Log:@"I" :@"No Removing movie View"];
        }
    }
}


#pragma mark - Movie player notifications
- (void) videoHasStateChanged:(NSNotification *)paramNotification{
    [Logger logger_Log:@"I" :@"video has state changed"];
}

- (void) videoHasChanged:(NSNotification *)paramNotification{
    [Logger logger_Log:@"I" :@"video has changed"];
}

- (void) videoHasFinishedPlaying:(NSNotification *)paramNotification{
    NSNumber *reason = [paramNotification.userInfo
                        valueForKey:MPMoviePlayerPlaybackDidFinishReasonUserInfoKey];
    if (reason != nil){
        NSInteger reasonAsInteger = [reason integerValue];
        switch (reasonAsInteger){
            case MPMovieFinishReasonPlaybackEnded:{
                [Logger logger_Log:@"I" :@"The movie ended normally"];
                break;
            }
            case MPMovieFinishReasonPlaybackError:{
                [Logger logger_Log:@"I" :@"An error happened and the movie ended"];
                break;
            }
            case MPMovieFinishReasonUserExited:{
                [Logger logger_Log:@"I" :@"The user exited the player"];
                break;
            }
        }
        [self stopPlayingVideo:nil];
    }
    else{
        [Logger logger_Log:@"I" :@"Unknown reason for video to cancel"];
    }
}

@end
