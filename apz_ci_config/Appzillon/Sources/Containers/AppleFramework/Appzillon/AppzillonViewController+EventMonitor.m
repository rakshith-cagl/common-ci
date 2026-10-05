//
//  AppzillonViewController+EventMonitor.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 29/07/16.
//
//

#import "AppzillonViewController+EventMonitor.h"

@implementation AppzillonViewController (EventMonitor)
-(void)initEventMonitoring{
    self.isBatteryMonitored = NO;
    self.isAppResumeMonitored = NO;
    self.isAppPausedMonitored = NO;
    self.isAppCallStartMonitored = NO;
    self.isAppCallEndMonitored = NO;
}

-(void)controlEvents:(NSDictionary *)jsonDict{
    NSString *appPausedEvent = nil;
    NSString *appResumedEvent = nil;
    NSString *callStartEvent = nil;
    NSString *callEndEvent = nil;
    NSString *allEvents = [jsonDict objectForKey:CONTROLEVENTS_ALLEVENTS];
    if([allEvents isEqualToString:@""] == NO){
        if([allEvents isEqualToString: CONTROLEVENTS_ON]){
            [self startAppResumedMonitoring];
            [self startAppPausedMonitoring];
            self.isAppCallStartMonitored = YES;
            self.isAppCallEndMonitored = YES;
        }
        else if([allEvents isEqualToString: CONTROLEVENTS_OFF]){
            [self stopAppResumedMonitoring];
            [self stopAppPausedMonitoring];
            self.isAppCallStartMonitored = NO;
            self.isAppCallEndMonitored = NO;
        }
    }
    else{
        appPausedEvent = [jsonDict objectForKey:CONTROLEVENTS_APPPAUSEDEVENT];
        if([appPausedEvent isEqualToString:CONTROLEVENTS_ON]){
            [self startAppPausedMonitoring];
        }
        else if([appPausedEvent isEqualToString:CONTROLEVENTS_OFF]){
            [self stopAppPausedMonitoring];
        }
        appResumedEvent = [jsonDict objectForKey:CONTROLEVENTS_APPPAUSEDEVENT];
        if([appResumedEvent isEqualToString:CONTROLEVENTS_ON]){
            [self startAppResumedMonitoring];
        }
        else if([appResumedEvent isEqualToString:CONTROLEVENTS_OFF]){
            [self stopAppResumedMonitoring];
        }
        callStartEvent = [jsonDict objectForKey:CONTROLEVENTS_APPCALLSTARTEVENT];
        if([callStartEvent isEqualToString:CONTROLEVENTS_ON]){
            self.isAppCallStartMonitored = YES;
        }
        else if([callStartEvent isEqualToString:CONTROLEVENTS_OFF]){
            self.isAppCallStartMonitored = NO;
        }
        callEndEvent = [jsonDict objectForKey:CONTROLEVENTS_APPCALLENDEVENT];
        if([callEndEvent isEqualToString:CONTROLEVENTS_ON]){
            self.isAppCallEndMonitored = YES;
        }
        else if([callStartEvent isEqualToString:CONTROLEVENTS_OFF]){
            self.isAppCallEndMonitored = NO;
        }
    }
}
#pragma mark -Events ,Application LifeCycle

-(void)startAppPausedMonitoring{
    if(self.isAppPausedMonitored == NO){
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(onAppPaused:)
                                                     name:UIApplicationDidEnterBackgroundNotification
                                                   object:nil];
        self.isAppPausedMonitored =YES;
    }
    else{
        NSLog(@"---Already Monitoring App Paused Event---");
    }
}

- (void)onAppPaused:(NSNotification*)notification
{
    NSArray *returnResultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
    NSArray *returnResult=[NSArray arrayWithObjects:@"appPaused",nil];
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.controlEventRequest objectForKey:PLUGINID] :true :true :returnResultkeys :returnResult]];
}

-(void)stopAppPausedMonitoring{
    if(self.isAppPausedMonitored == YES){
        [[NSNotificationCenter defaultCenter] removeObserver:self
                                                        name:UIApplicationDidEnterBackgroundNotification
                                                      object:nil];
        self.isAppPausedMonitored = NO;
    }
    else{
        NSLog(@"--Already Stopped App Pause Event--");
    }
}

-(void)startAppResumedMonitoring{
    if(self.isAppResumeMonitored == NO){
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(onAppWillResume:)
                                                     name:UIApplicationWillEnterForegroundNotification
                                                   object:nil];
        self.isAppResumeMonitored = YES;
    }
    else{
        NSLog(@"--Already Started AppResumedMonitoring--");
    }
}

- (void)onAppWillResume:(NSNotification*)notification
{
    NSArray *returnResultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
    NSArray *returnResult=[NSArray arrayWithObjects:@"appResumed",nil];
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.controlEventRequest objectForKey:PLUGINID] :true :true :returnResultkeys :returnResult]];
}

-(void)stopAppResumedMonitoring{
    if(self.isAppResumeMonitored == YES){
        [[NSNotificationCenter defaultCenter] removeObserver:self
                                                        name:UIApplicationWillEnterForegroundNotification
                                                      object:nil];
        self.isAppResumeMonitored = NO;
    }
    else{
        NSLog(@"---Already Stopped App Resume Monitoring---");
    }
}

#pragma mark - Call Handle Notification
-(void)handleCall:(NSDictionary*)result
{
    CXCallObserver *callObserver = [[CXCallObserver alloc] init];
    self.callObserver = callObserver;
    [self.callObserver setDelegate:self queue:nil];
    self.callPluginId = [result objectForKey:PLUGINID];
}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wobjc-protocol-method-implementation"

- (void)callObserver:(CXCallObserver *)callObserver callChanged:(CXCall *)call {

    __unsafe_unretained typeof(self) weakSelf = self;

    if (call.hasConnected) {
        if(weakSelf.isAppCallStartMonitored){
            NSArray *returnResultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            NSArray *returnResult=[NSArray arrayWithObjects:@"connected",nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[weakSelf createResponseJSONString:self.callPluginId :true :true :returnResultkeys :returnResult]];
        }
    }else if (call.hasEnded){
        NSArray *returnResultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:@"disconnected",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[weakSelf createResponseJSONString:self.callPluginId :true :false :returnResultkeys :returnResult]];
    }
    else if(call.outgoing){
        if(weakSelf.isAppCallStartMonitored){
            NSArray *returnResultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            NSArray *returnResult=[NSArray arrayWithObjects:@"typing",nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[weakSelf createResponseJSONString:self.callPluginId :true :true :returnResultkeys :returnResult]];
        }
    }else if (call.isOutgoing == false && call.hasConnected == false && call.hasEnded == false) {
        if(weakSelf.isAppCallStartMonitored){
            NSArray *returnResultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            NSArray *returnResult=[NSArray arrayWithObjects:@"incoming",nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[weakSelf createResponseJSONString:self.callPluginId :true :true :returnResultkeys :returnResult]];
        }
    }
}

#pragma clang diagnostic pop
@end
