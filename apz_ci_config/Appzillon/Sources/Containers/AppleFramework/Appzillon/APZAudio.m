//
//  APZAudio.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//
#import "APZAudio.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import <CoreAudio/CoreAudioTypes.h>
#import <AVFoundation/AVFoundation.h>
#import "Logger.h"
#import "Base64.h"
#import "AppzillonViewController.h"

@interface APZAudio()<AVAudioPlayerDelegate,AVAudioRecorderDelegate>
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property (strong, nonatomic) NSString *audioSaveFormat;
@property (strong, nonatomic) NSString *audioFunction;
@property (strong, nonatomic) NSString *audioFileName;
@property (strong, nonatomic) NSString *audioBase64;
@property(strong,nonatomic)AVAudioRecorder *audioRecorder;
@property (strong,nonatomic) AVAudioSession *audioSession;
@property(strong,nonatomic)AVAudioPlayer *audioPlayer;
@property(assign,nonatomic) BOOL isPlayerPaused;
@property(assign,nonatomic) BOOL isRecorderPaused;
@property (strong,nonatomic) NSString *pluginId;

////////////////Audio Settings Properties
@property(assign,nonatomic) BOOL isTimerEnabled;
@property (strong,nonatomic) NSString *audioFileType;
@property (strong,nonatomic) NSString *audioChannel;
@property (strong,nonatomic) NSString *audioSampleRate;
@property (strong,nonatomic) NSString *audioBitRate;
@property (strong, nonatomic) NSString *audioTimerBase64;
@property (strong, nonatomic) NSString *audioTimerCount;
@end

@implementation APZAudio

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZAudio--execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    [self processAudioFunction:jsonDict];
}

#pragma mark - Custom Audio Processing
-(void)processAudioFunction:(NSDictionary *)jsonDict{
    NSString *location = nil;
     self.audioFunction = [jsonDict objectForKey:AUDIO_ACTION];
     self.audioFileName = [jsonDict objectForKey:AUDIO_FILENAME];
     [self setAudioFileExtension:jsonDict];
     if([self.audioFunction isEqualToString:AUDIO_PLAY]){
        location=[[jsonDict objectForKey:AUDIO_LOCATION] uppercaseString] ;
        BOOL locationValidation=[location isEqualToString:AUDIO_LO_DEFAULT]||[location  isEqualToString:AUDIO_LO_EXTERNAL];
        if(locationValidation){
            [self playAudio];
        }
        else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",FILE_NOT_FOUND_ERROR],nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZAudio--Invalid Location"];
        }
    }
    else if([self.audioFunction isEqualToString:AUDIO_RECORD]){
        NSString *location=[[jsonDict objectForKey:AUDIO_LOCATION] uppercaseString] ;
        BOOL locationValidation=[location isEqualToString:AUDIO_LO_DEFAULT]||[location  isEqualToString:AUDIO_LO_EXTERNAL];
           if(locationValidation){
                    if ([[jsonDict objectForKey:@"channel"] isEqualToString:@"stereo"]){
                        self.audioChannel =@"2";
                    }else{
                         self.audioChannel =@"1";
                    }
                    if ([jsonDict objectForKey:@"samplingRate"]&&![[jsonDict objectForKey:@"samplingRate"] isEqual:@""]){
                        self.audioSampleRate = [jsonDict objectForKey:@"samplingRate"];
                     }else{
                        self.audioSampleRate = @"44100.0";
                     }
                    if ([jsonDict objectForKey:@"bitRate"]&&![[jsonDict objectForKey:@"bitRate"] isEqual:@""]){
                       self.audioBitRate = [jsonDict objectForKey:@"samplingRate"];
                     }else{
                       self.audioBitRate = @"16";
                     }
                    if ([jsonDict objectForKey:@"timeDuration"]&&![[jsonDict objectForKey:@"timeDuration"] isEqual:@""]) {
                        self.audioTimerCount = [jsonDict objectForKey:@"timeDuration"];
                        self.isTimerEnabled = YES;
                        if ([jsonDict objectForKey:@"base64"]) {
                            self.audioBase64=[jsonDict objectForKey:@"base64"];
                        }
                     }else{
                        self.isTimerEnabled = NO;
                    }
             [self startRecording];
        }
        else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",FILE_NOT_FOUND_ERROR],nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZAudio--Invalid Location"];
        }
    }
    else if([self.audioFunction isEqualToString:AUDIO_PAUSE]){
        [self pauseAudio];
    }
    else if([self.audioFunction isEqualToString:AUDIO_SAVE]){
        self.audioBase64=[jsonDict objectForKey:@"base64"];
        [self saveAudio];
    }
    else{
        [Logger logger_Log:@"E" :@"APZAudio--Invalid Action"];
    }
}

#pragma mark Audio Record
-(void)startRecording{
    if (self.audioPlayer.playing || self.isPlayerPaused == YES) {
        self.isPlayerPaused = NO;
        [self.audioPlayer stop];
    }
    else {
        [Logger logger_Log:@"I" :@"APZAudio--Audio Player is not playing"];
    }
    
    if(self.audioRecorder != nil){
        self.isRecorderPaused = NO;
        [self.audioRecorder record];
        NSArray *resultkeys=nil;
        NSArray *result=nil;
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZAudio--Media Record Resumed"];
    }
    else{
        [self startNewRecording];
    }
}

-(void)startNewRecording{
    NSDictionary *audioSettings = nil;
    NSString *audioPath = nil;
    NSURL *audioURL = nil;
    audioSettings = [NSDictionary dictionaryWithObjectsAndKeys:
                     [NSNumber numberWithInt:kAudioFormatLinearPCM],
                     AVFormatIDKey,
                     [NSNumber numberWithInt: [self.audioChannel intValue]],
                     AVNumberOfChannelsKey,
                     [NSNumber numberWithFloat:[self.audioSampleRate floatValue]],
                     AVSampleRateKey,
                     [NSNumber numberWithInt:AVAudioQualityMin],
                     AVEncoderAudioQualityKey,
                     [NSNumber numberWithInt:[self.audioBitRate intValue]],
                     AVEncoderBitRateKey,
                     nil];
    NSError *error = nil;
    NSString *docsDir = [self audioDirectory];
    [[NSFileManager defaultManager] createDirectoryAtPath:docsDir withIntermediateDirectories:YES attributes:nil error:nil];
    NSString *soundFileName = self.audioFileName;
    soundFileName = [soundFileName stringByAppendingString:[NSString stringWithFormat:@".%@",self.audioFileType]];
    audioPath = [docsDir stringByAppendingPathComponent:soundFileName];
    audioURL = [[NSURL alloc] initFileURLWithPath:audioPath];
    if ([[NSFileManager defaultManager] fileExistsAtPath:audioPath]) {
        [Logger logger_Log:@"E" :@"APZAudio--Invalid Location"];
    }
    else {
        [Logger logger_Log:@"E" :@"APZAudio--Invalid Location"];
    }
    self.audioRecorder = [[AVAudioRecorder alloc]
                          initWithURL:audioURL
                          settings:audioSettings
                          error:&error];
    
    if (self.audioRecorder != nil){
        self.audioRecorder.delegate = self;
        self.audioSession = [AVAudioSession sharedInstance];
        [self.audioSession setCategory:AVAudioSessionCategoryPlayAndRecord error:nil];
        [self.audioSession setActive:YES error:nil];
        if (self.isTimerEnabled) {
            [self.audioRecorder recordForDuration:(NSTimeInterval) [self.audioTimerCount intValue]];
        }
        if ([self.audioRecorder prepareToRecord] && [self.audioRecorder record]){
            NSArray *resultkeys=nil;
            NSArray *result=nil;
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
            [Logger logger_Log:@"I" :@"APZAudio--Media Record Started"];
        }
        else {
            self.audioRecorder = nil;
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",AUDIO_EXCEPTION_RECORDFAIL],nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZAudio--Media Record Failed"];
        }
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",AUDIO_EXCEPTION_RECORDFAIL],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZAudio--Failed to create an instance of the audio recorder"];
        [self cleanPlugin];
    }
}

#pragma mark Audio Recorder Delegation Callbacks
- (void)audioRecorderDidFinishRecording:(AVAudioRecorder *)recorder successfully:(BOOL)flag{
    if (flag){
        if (!self.isTimerEnabled) {
            NSArray *resultkeys=nil;
            NSArray *result=nil;
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
            [Logger logger_Log:@"I" :@"APZAudio--Audio Recorded Saved succesfully"];
        }else{
            [self saveAudio];
        }
    }
    else if([self.audioBase64 isEqualToString:@"Y"]){
        
        
        
    }
    else {
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",AUDIO_EXCEPTION_RECORDFAIL],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZAudio--Media Record Failed"];
    }
    [self cleanPlugin];
}

- (void)audioRecorderBeginInterruption:(AVAudioRecorder *)recorder{
}

- (void)audioRecorderEndInterruption:(AVAudioRecorder *)recorder
                           withFlags:(NSUInteger)flags{
    if ( (flags == AVAudioSessionInterruptionOptionShouldResume) && (recorder != nil)){
        UIAlertController *alertRecordInterruption = [UIAlertController alertControllerWithTitle:@"" message:@"Resumed Audio Recording" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
        [alertRecordInterruption addAction:actionCancel];
        [self.viewController presentViewController:alertRecordInterruption animated:YES completion:nil];
        [recorder record];
    }
}

- (void)audioRecorderEncodeErrorDidOccur:(AVAudioRecorder *)recorder error:(NSError *)error{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",AUDIO_EXCEPTION_ENCODEFAIL],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZAudio--Media Record Encoding Error"];
    [self cleanPlugin];
}

#pragma mark Audio play
-(void) playAudio
{
    if (self.audioRecorder.recording || self.isRecorderPaused == YES) {
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",AUDIO_EXCEPTION_RECBUSY],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :true :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZAudio--recording is happening,stop it for recording"];
    }
    else {
        if(self.audioPlayer != nil){
            self.isPlayerPaused = NO;
            [self.audioPlayer play];
            NSArray *resultkeys=nil;
            NSArray *result=nil;
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
            [Logger logger_Log:@"I" :@"APZAudio--Audio Player Resumed "];
        }
        else{
            [self playNewAudio];
        }
    }
}

-(void)playNewAudio{
    NSString *audioPath = nil;
    NSURL *audioURL = nil;
    NSString *docsDir = [self audioDirectory];
    NSString *soundFileName = self.audioFileName;
    soundFileName = [soundFileName stringByAppendingString:[NSString stringWithFormat:@".%@",self.audioFileType]];
    audioPath = [docsDir stringByAppendingPathComponent:soundFileName];
    if ([[NSFileManager defaultManager]fileExistsAtPath:audioPath]){
        audioURL = [[NSURL alloc] initFileURLWithPath:audioPath];
        NSError *error;
        self.audioPlayer = [[AVAudioPlayer alloc]
                            initWithContentsOfURL:audioURL
                            error:&error];
        self.isPlayerPaused = NO;
        self.isRecorderPaused = NO;
        if (self.audioPlayer != nil){
            self.audioPlayer.delegate = self;
            if ([self.audioPlayer prepareToPlay] &&
                [self.audioPlayer play]){
            }
            else {
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",AUDIO_EXCEPTION_PLAYFAIL],nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                [Logger logger_Log:@"E" :@"APZAudio--Failed to play Audio"];
                [self cleanPlugin];
            }
        }
        else {
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",AUDIO_EXCEPTION_PLAYFAIL],nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZAudio--Media play instance creation failed"];
            [self cleanPlugin];
            
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:FILE_NOT_FOUND_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZAudio--Media Play File not Found"];
        [self cleanPlugin];
    }
}

#pragma mark Audio Player Delegation Callbacks
-(void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag
{
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZAudio--Successfully Played Audio"];
    [self cleanPlugin];
}

-(void)audioPlayerBeginInterruption:(AVAudioPlayer *)player{
    [Logger logger_Log:@"E" :@"APZAudio--Audio Player is interupted here"];
}

-(void)audioPlayerEndInterruption:(AVAudioPlayer *)player withOptions:(NSUInteger)flags{
    if ( (flags == AVAudioSessionInterruptionOptionShouldResume) && (player != nil)){
        UIAlertController *alertPlayInterruption = [UIAlertController alertControllerWithTitle:@"" message:@"Resumed Audio Playing" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
        [alertPlayInterruption addAction:actionCancel];
        [self.viewController presentViewController:alertPlayInterruption animated:YES completion:nil];
        [player play];
    }
}

- (void)audioPlayerDecodeErrorDidOccur:(AVAudioPlayer *)player error:(NSError *)error{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",AUDIO_EXCEPTION_DECODEFAIL],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZAudio--Media player Decode Error"];
    [self cleanPlugin];
}

#pragma mark Audio Save
-(void)saveAudio
{
    if (self.audioRecorder.recording || self.isRecorderPaused == YES){
        self.isRecorderPaused = NO;
        [self.audioRecorder stop];
        [self.audioSession setActive:NO error:nil];
        if([self.audioBase64 isEqualToString:@"Y"]){
            [self convertAudioBase64];
        }
    }
    else if( self.audioPlayer.playing || self.isPlayerPaused == YES){
        self.isPlayerPaused =NO;
        [self.audioPlayer stop];
        if([self.audioBase64 isEqualToString:@"Y"]){
            [self convertAudioBase64];
        }        }
    else if (self.isTimerEnabled){
        [self customTimerMethod];
    }
    else{
        self.isRecorderPaused =NO;
        self.isPlayerPaused = NO;
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",AUDIO_EXCEPTION_NO_REC_PLAY],nil];;
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZAudio--No Recording or Playing occuring"];
        [self cleanPlugin];
    }
}

-(void)convertAudioBase64{
    NSString *docsDir = [self audioDirectory];
    NSString *soundFileName = self.audioFileName;
    soundFileName = [soundFileName stringByAppendingString:[NSString stringWithFormat:@".%@",self.audioFileType]];
    NSString *audioPath = [docsDir stringByAppendingPathComponent:soundFileName];
    NSData *data = [NSData dataWithContentsOfFile:audioPath];
    NSUInteger len = [data length];
    Byte *byteData = (Byte*)malloc(len);
    [data getBytes:byteData length:len];
    [Base64 initialize];
    NSString * finalstr1 = [Base64 encode:data];
    if ( ![finalstr1 isEqualToString:@""] && (finalstr1 != nil) ){
        NSArray *resultkeys=[NSArray arrayWithObjects:CAMERA_BASE64,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",finalstr1],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    }
}

-(void)customTimerMethod{
    self.isPlayerPaused =NO;
    [self.audioSession setActive:NO error:nil];
    [self.audioPlayer stop];
    self.isTimerEnabled = NO;
    if([self.audioBase64 isEqualToString:@"Y"]){
        [self convertAudioBase64];
    }
    else{
        self.audioFunction = nil;
        NSArray *resultkeys=[NSArray arrayWithObjects:@"timeDuration",nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"success"],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    }
}


#pragma mark Audio Pause
-(void)pauseAudio{
    if([self.audioPlayer isPlaying]){
        self.isPlayerPaused =YES;
        [self.audioPlayer pause];
        NSArray *resultkeys=nil;
        NSArray *result=nil;
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZAudio--Media Playing Pasue"];
    }
    else if([self.audioRecorder isRecording]){
        self.isRecorderPaused =YES;
        [self.audioRecorder pause];
        NSArray *resultkeys=nil;
        NSArray *result=nil;
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZAudio--Audio Recorder Paused"];
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",AUDIO_EXCEPTION_NO_REC_PLAY],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZAudio--Audio is Neither Recording or Playing"];
    }
}

#pragma mark FileType
-(void)setAudioFileExtension:(NSDictionary *)requestJson{
    if ([[requestJson objectForKey:@"wavFileFormat"] isEqualToString:@"Y"]) {
        self.audioFileType = @"wav";
    }else{
        self.audioFileType = @"caf";
    }
}

#pragma mark Audio Stop
-(void)stop {
    if (self.audioPlayer.playing) {
        [self.audioPlayer stop];
    }
}


-(NSString *)audioDirectory{
    NSArray *dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *fileOpnDir;
    fileOpnDir= [[dirPaths objectAtIndex:0] stringByAppendingFormat:@"/Assets/apps/%@",self.viewController.appString];
    return  [fileOpnDir stringByAppendingPathComponent:AUDIO_DIRNAME];
    
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"I" :@"APZAudio--Done"];
    self.webView = nil;
    self.audioRecorder = nil;
    self.audioPlayer = nil;
    self.audioSession=nil;
    [self.delegate donePlugin:self];
}

@end

