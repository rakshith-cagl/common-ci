//
//  APZSpeechToText.m
//  Appzillon
//
//  Created by Aman Gupta on 15/11/19.
//

#import "APZSpeechToText.h"
#import "Logger.h"
#import "Constants.h"
#import "APZJsonUtil.h"
#import "AppzillonAppDelegate.h"
#import "AppzillonViewController.h"
#import <Speech/Speech.h>

@interface APZSpeechToText()<SFSpeechRecognizerDelegate,SFSpeechRecognitionTaskDelegate>
@property(nonatomic,strong)WKWebView *webView;
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(nonatomic,strong)NSString *pluginId;
@property(nonatomic,strong)SFSpeechRecognizer *speechRecognizer;
@property(nonatomic,strong)SFSpeechAudioBufferRecognitionRequest *recognitionRequest;
@property(nonatomic,strong)SFSpeechRecognitionTask *recognitionTask;
@property(nonatomic,strong)NSString *speechResult;
@property(nonatomic,strong)NSString *languageCode;
@property(nonatomic,strong)AVAudioEngine *audioEngine;
@property(nonatomic,strong)NSDictionary *json;
@property(nonatomic,strong)AVAudioInputNode *inputNode;
@property(nonatomic,strong) NSTimer *timer;
@property(nonatomic,assign) NSInteger timerDuration;
@property(nonatomic,assign) BOOL listenerStartSent;
@end

@implementation APZSpeechToText
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        AppzillonAppDelegate *appDelegate = (AppzillonAppDelegate*)[[UIApplication sharedApplication] delegate];
        self.viewController = [appDelegate viewController];
    }
    return self;
}
-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZSpeechToText--Execute"];
    if (jsonDict) {
        self.pluginId=[jsonDict objectForKey:PLUGINID];
        self.json = jsonDict;
        if([[jsonDict objectForKey:@"action"] isEqualToString:@"START"]||[[jsonDict objectForKey:@"action"] isEqualToString:@"RESUME"]){
            self.listenerStartSent = NO;
            self.timerDuration = [[jsonDict objectForKey:@"timerDuration"] integerValue];
            self.languageCode = [jsonDict objectForKey:@"languageCode"];
            [self callSpeechToTextEvent:jsonDict];
        }else if([[jsonDict objectForKey:@"action"] isEqualToString:@"STOP"]){
            [self stopListening];
        }
    }
}

-(void) callSpeechToTextEvent:(NSDictionary*)jsonDict{
    self.speechRecognizer = [[SFSpeechRecognizer alloc] initWithLocale:[[NSLocale alloc] initWithLocaleIdentifier:self.languageCode]];
   //MARK:- Do not remove this line, keep checking Apple documentation for onDeviceRecognition flag as till ios 14 this flag returns correct value only after 2nd access.
    BOOL onDeviceRecognition = [self checkForOnDeviceRecognition];
    NSLog(@"onDeviceRecognition%d",onDeviceRecognition);
    self.audioEngine = [[AVAudioEngine alloc] init];
    self.speechRecognizer.delegate = self;
    [SFSpeechRecognizer requestAuthorization:^(SFSpeechRecognizerAuthorizationStatus status) {
        switch (status) {
            case SFSpeechRecognizerAuthorizationStatusAuthorized:
                NSLog(@"Authorized");
                [self checkForLanguageSupport];
                break;
            case SFSpeechRecognizerAuthorizationStatusDenied:
                NSLog(@"Denied");
                [self permissionDeniedCallback];
                break;
            case SFSpeechRecognizerAuthorizationStatusNotDetermined:
                NSLog(@"Not Determined");
                break;
            case SFSpeechRecognizerAuthorizationStatusRestricted:
                NSLog(@"Restricted");
                break;
            default:
                break;
        }
    }];
}

-(void)checkForLanguageSupport{
    if(self.speechRecognizer == nil){
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-331"],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [self cleanPlugin];
    }
    else{
        if (self.audioEngine.isRunning) {
            [self.audioEngine stop];
            [self.recognitionRequest endAudio];
        } else {
            [self startListening];
        }
        
        if([[self.json objectForKey:@"action"] isEqualToString:@"RESUME"]){
            NSArray *resultkeys=[NSArray arrayWithObjects:@"action",@"text",nil];
            NSArray *result=[NSArray arrayWithObjects:@"RESUMED",@"",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
        }
    }
}

-(BOOL)checkForOnDeviceRecognition{
    BOOL onDeviceRecognition = false;
    if (@available(iOS 13, *)) {
        onDeviceRecognition = self.speechRecognizer.supportsOnDeviceRecognition;
        NSLog(@"supported---%d",onDeviceRecognition);
    }
    return onDeviceRecognition;
}

- (void)startListening{
    dispatch_async(dispatch_get_main_queue(), ^{
        if (self.recognitionTask) {
            [self.recognitionTask cancel];
            self.recognitionTask = nil;
            self.speechResult = nil;
        }
        NSError *error;
        AVAudioSession *audioSession = [AVAudioSession sharedInstance];
        [audioSession setCategory:AVAudioSessionCategoryPlayAndRecord error:&error];
        [audioSession setActive:YES withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation error:&error];
        self.recognitionRequest = [[SFSpeechAudioBufferRecognitionRequest alloc] init];
        self.inputNode = self.audioEngine.inputNode;
        self.recognitionRequest.shouldReportPartialResults = YES;
        if ([[self.json objectForKey:@"supportsOnDeviceRecognition"] isEqualToString:@"Y"]){
            if (@available(iOS 13, *)) {
                if ([self checkForOnDeviceRecognition]) {
                    self.recognitionRequest.requiresOnDeviceRecognition = YES;
                }else{
                    [self invalidateTimer];
                    [self cancelRegTask];
                    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,@"errorMessage",nil];
                    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-333"],@"OnDeviceRecognition is not supported",nil];
                    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                    [self cleanPlugin];
                }
            }else{
                [self invalidateTimer];
                [self cancelRegTask];
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,@"errorMessage",nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-333"],@"OnDeviceRecognition is not supported", nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                [self cleanPlugin];
            }
        }
        
        //Inform App that vioce listener started
        if (self.json != nil && [[self.json objectForKey:@"action"] isEqualToString:@"START"] && self.listenerStartSent == NO){
            NSArray *resultkeys=[NSArray arrayWithObjects:@"action",@"text",nil];
            NSArray *result=[NSArray arrayWithObjects:@"STARTED",@"",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
            self.listenerStartSent = YES;
        }
        
        //Do the voice recognition task if and only plugin is not cleaned and fail callback not sent above
        if (self.json != nil) {
            [self startTimer];
            self.recognitionTask = [self.speechRecognizer recognitionTaskWithRequest:self.recognitionRequest resultHandler:^(SFSpeechRecognitionResult * _Nullable result, NSError * _Nullable error) {
                BOOL isFinal = NO;
                if (result) {
                    NSLog(@"RESULT:%@",result.bestTranscription.formattedString);
                    NSString *currentResult = result.bestTranscription.formattedString;
                    isFinal = !result.isFinal;
                    if(![self.speechResult isEqualToString:currentResult]){
                        self.speechResult = result.bestTranscription.formattedString;
                        [self invalidateTimer];
                        [self startTimer];
                    }
                }
                if (error) {
                    if(error.code == 601){
                        [self invalidateTimer];
                        [self cancelRegTask];
                        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-333"],nil];
                        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                        [self cleanPlugin];
                    }
                    else if(error.code == 4){
                        NSError * connectionError = error.userInfo[@"NSUnderlyingError"];
                        if(connectionError.code == 16){
                            NSError *networkError = connectionError.userInfo[@"NSUnderlyingError"];
                            if(networkError.code == 50){
                                [self invalidateTimer];
                                [self cancelRegTask];
                                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-332"],nil];
                                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                                [self cleanPlugin];
                            }
                        }
                    }
                    if([[self.json objectForKey:@"pauseRecognize"] isEqualToString:@"Y"]){
                        [self invalidateTimer];
                        [self cancelRegTask];
                        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-203"],nil];
                        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                        [self cleanPlugin];
                    }
                }
            }];
            // Sets the recording format
            AVAudioFormat *recordingFormat = [self.inputNode outputFormatForBus:0];
            [self.inputNode removeTapOnBus:0];
            [self.inputNode installTapOnBus:0 bufferSize:1024 format:recordingFormat block:^(AVAudioPCMBuffer * _Nonnull buffer, AVAudioTime * _Nonnull when) {
                [self.recognitionRequest appendAudioPCMBuffer:buffer];
            }];
            // Starts the audio engine, i.e. it starts listening.
            [self.audioEngine prepare];
            [self.audioEngine startAndReturnError:&error];
            NSLog(@"Say Something, I'm listening");
        }
    });
}

-(void)cancelRegTask{
    dispatch_async(dispatch_get_main_queue(), ^{
        if (self.recognitionTask != nil) {
            [self.recognitionTask finish];
            [self.recognitionTask cancel];
            self.recognitionTask = nil;
            self.speechResult = nil;
        }
        [self stopAudio];
        [self.inputNode removeTapOnBus:0];
    });
}
-(void)permissionDeniedCallback{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-329"],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [self cleanPlugin];
}

-(void)stopListening{
    [self invalidateTimer];
    [self cancelRegTask];
    NSArray *resultkeys;
    NSArray * result;
    if (self.speechResult != nil && ![self.speechResult isEqualToString:@""]){
        resultkeys=[NSArray arrayWithObjects:@"action",@"text",nil];
        result=[NSArray arrayWithObjects:@"STOPPED",self.speechResult,nil];
    }else{
        resultkeys=[NSArray arrayWithObjects:@"action",@"text",nil];
        result=[NSArray arrayWithObjects:@"STOPPED",@"",nil];
    }
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [self cleanPlugin];
}

-(void)stopAudio{
    if (self.audioEngine.isRunning) {
        [self.audioEngine stop];
        [self.recognitionRequest endAudio];
    }
}
-(void)invalidateTimer{
    [self.timer invalidate];
    self.timer = nil;
}
-(void)startTimer{
    dispatch_async(dispatch_get_main_queue(), ^{
        if(self.timer == nil){
            if(self.timerDuration != 0){
                self.timer =  [NSTimer scheduledTimerWithTimeInterval:self.timerDuration target:self selector:@selector(timerAction) userInfo:nil repeats:NO];
            }
            else{
                self.timer =  [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(timerAction) userInfo:nil repeats:NO];
            }
        }
    });
}
-(void)timerAction{
    NSLog(@"Fired");
    if([[self.json objectForKey:@"pauseRecognize"] isEqualToString:@"Y"]){
        NSArray *resultkeys=[NSArray arrayWithObjects:@"action",@"text",nil];
        NSArray *result;
        if (self.speechResult == nil || [self.speechResult isEqualToString:@""]){
            result=[NSArray arrayWithObjects:@"PAUSED",@"",nil];
        }else{
            result=[NSArray arrayWithObjects:@"PAUSED",[NSString stringWithFormat:@"%@",self.speechResult],nil];
        }
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [self invalidateTimer];
        [self cancelRegTask];
        [self cleanPlugin];
    }
    else{
        if (self.speechResult != nil && ![self.speechResult isEqualToString:@""]){
            NSArray *resultkeys=[NSArray arrayWithObjects:@"action",@"text",nil];
            NSArray *result=[NSArray arrayWithObjects:@"PAUSED",[NSString stringWithFormat:@"%@",self.speechResult],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
        }
        [self cancelRegTask];
        [self startTimer];
        [self startListening];
    }
}

#pragma mark - SFSpeechRecognizerDelegate Delegate Methods
- (void)speechRecognizer:(SFSpeechRecognizer *)speechRecognizer availabilityDidChange:(BOOL)available {
    NSLog(@"Availability:%d",available);
}


- (void)speechRecognitionDidDetectSpeech:(SFSpeechRecognitionTask *)task{
    
}

// Called for all recognitions, including non-final hypothesis
- (void)speechRecognitionTask:(SFSpeechRecognitionTask *)task didHypothesizeTranscription:(SFTranscription *)transcription{
    
}

// Called only for final recognitions of utterances. No more about the utterance will be reported
- (void)speechRecognitionTask:(SFSpeechRecognitionTask *)task didFinishRecognition:(SFSpeechRecognitionResult *)recognitionResult{
    
}

// Called when the task is no longer accepting new audio but may be finishing final processing
- (void)speechRecognitionTaskFinishedReadingAudio:(SFSpeechRecognitionTask *)task{
    
}

// Called when the task has been cancelled, either by client app, the user, or the system
- (void)speechRecognitionTaskWasCancelled:(SFSpeechRecognitionTask *)task{
    NSLog(@"speechRecognitionTaskWasCancelled%@",task);
    [self startListening];
}

// Called when recognition of all requested utterances is finished.
// If successfully is false, the error property of the task will contain error information
- (void)speechRecognitionTask:(SFSpeechRecognitionTask *)task didFinishSuccessfully:(BOOL)successfully{
    
}

#pragma mark - Cleaning the Plugin
-(void)cleanPlugin{
    [self.delegate donePlugin:self];
    self.viewController = nil;
    self.webView = nil;
    self.speechRecognizer = nil;
    self.recognitionRequest = nil;
    self.recognitionTask = nil;
    self.audioEngine = nil;
    self.timer = nil;
    self.inputNode = nil;
    self.json = nil;
    self.listenerStartSent = nil;
}
@end

