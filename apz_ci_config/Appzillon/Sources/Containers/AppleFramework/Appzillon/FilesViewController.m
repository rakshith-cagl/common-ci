//
//  FilesViewController.m
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//


#import "FilesViewController.h"
#import "Constants.h"
#import <QuickLook/QuickLook.h>
#import <MediaPlayer/MediaPlayer.h>
#import <CoreAudio/CoreAudioTypes.h>
#import <AVFoundation/AVFoundation.h>
#import "ImageViewController.h"
#import "Logger.h"
#import "MiscellaneousMethods.h"

@interface FilesViewController ()<QLPreviewControllerDataSource,
QLPreviewControllerDelegate,AVAudioPlayerDelegate,UIDocumentInteractionControllerDelegate>
@property(nonatomic,strong)NSIndexPath *prevCellIndexPath;
@property(nonatomic,strong)NSArray *filesArr;
@property(nonatomic,strong)NSString *filesStrr;
@property(nonatomic,strong)NSString *dirPath;
@property(nonatomic,strong)NSString *openType;
@property(nonatomic,strong)NSString *selectedFile;
@property(nonatomic,strong)NSString *selectedExtension;
@property (nonatomic, strong) NSMutableArray *documentURLs;
@property(nonatomic,strong)  QLPreviewController *previewController;
@property(strong,nonatomic)AVAudioPlayer *audioPlayer;
@property (nonatomic, strong) MPMoviePlayerController *moviePlayer;
@property(nonatomic,strong)NSString *fileFilters;
@property(nonatomic,strong)NSString *sandBoxPath;
@property(nonatomic, strong)UIDocumentInteractionController *documentInteractionController;

@end

@implementation FilesViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
    }
    return self;
}

-(void)initFiles:(NSArray *)fileArray directory:(NSString *)directoryPath openType:(NSString *)type fileFilters:(NSString*)fileFilters{
    self.filesArr = fileArray;
    self.dirPath = directoryPath;
    self.sandBoxPath=[directoryPath stringByDeletingLastPathComponent];
    if([type isEqualToString:@""]){
        self.openType = @"N";
    }
    else{
        self.openType = type;
    }
    self.fileFilters=fileFilters;
}

-(void)initCreateFiles:(NSString *)fileStr directory:(NSString *)directoryPath openType:(NSString *)type fileFilters:(NSString*)fileFilters{
    self.filesStrr = fileStr;
    self.dirPath = directoryPath;
    self.sandBoxPath=[directoryPath lastPathComponent];
    [self.delegate selectedSingleFile:self.sandBoxPath path:self.dirPath];
}

- (void)viewDidLoad
{
    self.title = @"Files";
    if([self.openType isEqualToString:@"N"]){
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(done)];
    }
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Back"
                                                                   style:UIBarButtonItemStylePlain
                                                                  target:self
                                                                  action:@selector(handleBack)];
    self.navigationItem.leftBarButtonItem = backButton;
    
    [super viewDidLoad];
}

-(void)handleBack{
    self.dirPath=[self.dirPath stringByDeletingLastPathComponent];
    if ([self.dirPath isEqualToString:self.sandBoxPath]) {
        [self.navigationController popViewControllerAnimated:YES];
    }else{
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^ {
                self.filesArr=[self getAllDirectoryPlusFiles];
                [self.tableView reloadData];
            });
        });
    }
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
        UIImageView *imgView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 48, 48)]; // your cell's height should be greater than 48 for this.
        imgView.tag = 1;
        [cell.contentView addSubview:imgView];
        imgView = nil;
        
        UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(50, 0, self.view.frame.size.width-50, 50)];
        
        lbl.backgroundColor = [UIColor clearColor];
        [lbl setTag:2];
        [cell.contentView addSubview:lbl];
        lbl = nil;
    }
    
    
    UILabel *_lbl = (UILabel *)[cell.contentView viewWithTag:2];
    _lbl.text =  [self.filesArr  objectAtIndex:indexPath.row];
    UIImageView *_imgView = (UIImageView *)[cell.contentView viewWithTag:1];
    _imgView.image = [UIImage imageNamed:[FILEBROWSER_ICON_FOLDER stringByAppendingPathComponent:[MiscellaneousMethods getItemsImage:[self.filesArr  objectAtIndex:indexPath.row]]]];
    //    cell.textLabel.text = [self.filesArr  objectAtIndex:indexPath.row];
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
    self.selectedFile = [self.filesArr objectAtIndex:indexPath.row];
    if([self.openType isEqualToString:@"N"]){
        BOOL isDirectory;
        [[NSFileManager defaultManager] fileExistsAtPath:[self.dirPath stringByAppendingPathComponent:self.selectedFile] isDirectory:&isDirectory];
        if (isDirectory) {
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                dispatch_async(dispatch_get_main_queue(), ^ {
                    self.dirPath=[self.dirPath stringByAppendingPathComponent:self.selectedFile];
                    self.selectedFile=nil;
                    self.filesArr=[self getAllDirectoryPlusFiles];
                    [self.tableView reloadData];
                });
            });
        } else {
            [tableView cellForRowAtIndexPath:indexPath].accessoryType = UITableViewCellAccessoryCheckmark;
        }
    }
    else{
        [self openFile:[self.filesArr objectAtIndex:indexPath.row]
                forRow:indexPath.row];
    }
    self.prevCellIndexPath = indexPath;
}

-(void)done{
    BOOL isDirectory;
    NSString *selectedElement;
    if([self.selectedFile length]>0){
        selectedElement=[self.dirPath stringByAppendingPathComponent:self.selectedFile];
    }else{
        selectedElement=self.dirPath;
    }
    [[NSFileManager defaultManager] fileExistsAtPath:selectedElement  isDirectory:&isDirectory];
    if (isDirectory) {
        self.selectedFile=[self.dirPath lastPathComponent];
    }else{
        self.dirPath=[self.dirPath stringByAppendingPathComponent:self.selectedFile];
    }
    [self.delegate selectedFile:self.selectedFile path:self.dirPath];
    [self.navigationController popViewControllerAnimated:YES];
    self.selectedFile = nil;
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
                    [selFileExtension isEqualToString:FILEBROWSER_VIDEO_EXTENSION_MPEG]||
                    [selFileExtension isEqualToString:FILEBROWSER_VIDEO_EXTENSION_AMR]
                    );
    BOOL isDirectory;
    [[NSFileManager defaultManager] fileExistsAtPath:[self.dirPath stringByAppendingPathComponent:selectedFile] isDirectory:&isDirectory];
    
    if(isAudio)
    {
        //[self playMusic:selectedFile];
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
                self.dirPath=[self.dirPath stringByAppendingPathComponent:self.selectedFile];
                self.filesArr=[self getAllDirectoryPlusFiles];
                [self.tableView reloadData];
            });
        });
    }else{
        [self viewDocument:row];
    }
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
        [Logger logger_Log:@"I" :@"FileViewController-Audio player is not Playing"];
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
            [Logger logger_Log:@"I" :@"FileViewController-Successfully started playing"];
        }
    }
    else{
        [Logger logger_Log:@"I" :@"FileViewController-Unable to create Audio Player"];
    }
}

#pragma mark-Audio Player Delegation Callbacks
-(void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag
{
    self.audioPlayer = nil;
}

-(void)audioPlayerBeginInterruption:(AVAudioPlayer *)player{
    
}

-(void)audioPlayerEndInterruption:(AVAudioPlayer *)player withOptions:(NSUInteger)flags{
    [Logger logger_Log:@"I" :@"FileViewController-Audio Player end interuption"];
}

- (void)audioPlayerDecodeErrorDidOccur:(AVAudioPlayer *)player error:(NSError *)error{
    self.audioPlayer = nil;
}

#pragma mark - Document Viewer
-(void)viewDocument:(NSUInteger)index{
    //    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
    self.documentURLs = [NSMutableArray array];
    [self initDocumentsURL:self.filesArr];
    QLPreviewController *previewController = [[QLPreviewController alloc] init];
    previewController.dataSource = self;
    previewController.delegate = self;
    previewController.currentPreviewItemIndex = index;
    [self.navigationController pushViewController:previewController animated:YES];
    
    //        NSIndexPath *selectedIndexPath = [self.tableView indexPathForSelectedRow];
    //        NSURL *fileURL = [NSURL URLWithString:@"ipod-library://item/item.m4a?id=809397921538132533"];
    //       // NSURL *fileURL =[NSURL fileURLWithPath:self.documentURLs[selectedIndexPath.row]];
    //
    //        // NSURL *URL = [[NSBundle mainBundle] URLForResource:@"sample" withExtension:@"pdf"];
    ////        dispatch_async(dispatch_get_main_queue(), ^{
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
    //        });
    //    });
    //
    
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
        self.moviePlayer.view.frame = self.view.bounds;
        self.moviePlayer.scalingMode = MPMovieScalingModeAspectFit;
        self.moviePlayer.controlStyle = MPMovieControlStyleEmbedded;
        self.moviePlayer.shouldAutoplay = YES;
        [self.view addSubview: self.moviePlayer.view];
        [ self.moviePlayer setFullscreen:YES animated:YES];
        [ self.moviePlayer play];
    }
    else {
        [Logger logger_Log:@"I" :@"FileViewController-Failed to instantiate the movie player"];
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
            [Logger logger_Log:@"I" :@"FileViewController-No Removing movie View"];
        }
    }
}


#pragma mark - Movie player notifications
- (void) videoHasStateChanged:(NSNotification *)paramNotification{
    [Logger logger_Log:@"I" :@"FileViewController-video has state changed"];
}

- (void) videoHasChanged:(NSNotification *)paramNotification{
    [Logger logger_Log:@"I" :@"FileViewController-video has changed"];
}

- (void) videoHasFinishedPlaying:(NSNotification *)paramNotification{
    NSNumber *reason = [paramNotification.userInfo
                        valueForKey:MPMoviePlayerPlaybackDidFinishReasonUserInfoKey];
    if (reason != nil){
        NSInteger reasonAsInteger = [reason integerValue];
        switch (reasonAsInteger){
            case MPMovieFinishReasonPlaybackEnded:{
                [Logger logger_Log:@"I" :@"FileViewController-The movie ended normally"];
                break;
            }
            case MPMovieFinishReasonPlaybackError:{
                [Logger logger_Log:@"I" :@"FileViewController-An error happened and the movie ended"];
                break;
            }
            case MPMovieFinishReasonUserExited:{
                [Logger logger_Log:@"I" :@"FileViewController-The user exited the player"];
                break;
            }
        }
        [Logger logger_Log:@"I" :@"FileViewController-The user exited the player"];
        [self stopPlayingVideo:nil];
    }
    else{
        NSLog(@"---Unknown reason for video to cancel");
    }
}
#pragma mark changes to traverse in directroies done in Appzillon3.2
- (NSArray *)getAllDirectoryPlusFiles{
    NSFileManager *fileManager =[NSFileManager defaultManager];
    NSArray *listOfFilesAndDirectories=[fileManager contentsOfDirectoryAtPath:self.dirPath error:nil];
    
    NSError *error;
    if ([listOfFilesAndDirectories count] > 0 && error == nil){
        if([self.fileFilters isEqualToString:@""] == NO)
        {
            NSArray *extensions = [self.fileFilters componentsSeparatedByString:FILEBROWSER_FILTER_SEPERATOR];
            NSMutableArray *subpredicates = [@[] mutableCopy];
            for (NSString *extension in extensions) {
                [subpredicates addObject:[NSPredicate predicateWithFormat:@"SELF ENDSWITH %@", extension]];
            }
            NSPredicate *filter = [NSCompoundPredicate orPredicateWithSubpredicates:subpredicates];
            listOfFilesAndDirectories= [listOfFilesAndDirectories filteredArrayUsingPredicate:filter];
        }
        else{
            [Logger logger_Log:@"I" :@"FileViewController-No Extension to filter by extensions"];
        }
    }
    return listOfFilesAndDirectories;
}



@end
