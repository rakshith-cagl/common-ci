/*
 File: APZBattery.m
 Abstract: Receives battery status change notifications. Queries the battery status and presents it in a UITableView. Enables and disables battery status updates.
 Version: 1.2
 
 
 */

#import "APZBattery.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
#import "AppzillonViewController.h"

@interface APZBattery ()
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(strong,nonatomic)NSString *batteryState;
@property(strong,nonatomic)NSString *batteryLevel;
@property(strong,nonatomic)NSString *batteryStateVal;
@property(strong,nonatomic)NSString *batteryLevelVal;
@property(strong,nonatomic) NSString *batteryThersholdVal;
@property(strong,nonatomic) NSTimer *batteryTimer;
@property(strong,nonatomic)NSString *batteryAction;
//@property(assign,nonatomic)BOOL timerIndicate;
@property(assign)NSTimeInterval batteryTimerval;
@property(strong,nonatomic)NSString *pluginId;
@end

@implementation APZBattery

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZBattery--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    self.batteryAction = [jsonDict objectForKey:@"actionType"];
    self.batteryAction = [self.batteryAction lowercaseString];
    self.batteryState = [jsonDict objectForKey:@"state"];
    self.batteryLevel = [jsonDict objectForKey:@"level"];
    self.batteryTimerval = [[jsonDict objectForKey:@"time"]intValue];
    self.batteryThersholdVal =[jsonDict objectForKey:@"threshold"];
    self.batteryLevelVal=@"";
    self.batteryStateVal=@"";
    if ([self.batteryAction isEqualToString:@"start"]) {
        [UIDevice currentDevice].batteryMonitoringEnabled = YES;
        //Initial Calculation
        UIDeviceBatteryState currentState = [UIDevice currentDevice].batteryState;
        if (UIDeviceBatteryStateCharging == currentState) {
            self.batteryStateVal = @"plugged";
        }
        else {
            self.batteryStateVal = @"unplugged";
        }
        float batteryLevel = [UIDevice currentDevice].batteryLevel;
        if (batteryLevel > 0.0) {
            int batteryValue= (int) round(batteryLevel *100);
            self.batteryLevelVal =[NSString stringWithFormat:@"%i",batteryValue];
        }
        //Set Up notification
        if ([self.batteryState isEqualToString:@"Y"]){
            [[NSNotificationCenter defaultCenter] addObserver:self
                                                     selector:@selector(batteryStateChanged:)
                                                         name:UIDeviceBatteryStateDidChangeNotification object:nil];
            
        }
        if([self.batteryLevel isEqualToString:@"Y"]){
            [[NSNotificationCenter defaultCenter] addObserver:self
                                                     selector:@selector(batteryLevelChanged:)
                                                         name:UIDeviceBatteryLevelDidChangeNotification object:nil];
            
        }
        if (self.batteryTimerval !=0){
            [self startBatteryTimer];
            
        }
        NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
        NSArray *result=[NSArray arrayWithObjects:@"started", nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZBattery--Battery Monitor"];
        
    }
    else {
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-241",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZBattery--Invalid Action"];
        
    }
}
//Timer
-(void)startBatteryTimer{
    if (self.batteryTimerval !=0) {
        dispatch_async(dispatch_get_main_queue(), ^{
            _batteryTimer = [NSTimer scheduledTimerWithTimeInterval:self.batteryTimerval
                                                             target:self
                                                           selector:@selector(timerSuccesscallback)
                                                           userInfo:nil
                                                            repeats:YES];
        });
    }
}

-(void)stopTimer{
    dispatch_async(dispatch_get_main_queue(), ^{
        [_batteryTimer invalidate];
        _batteryTimer=nil;
    });
}

-(void)timerSuccesscallback{
    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,@"state",@"level",nil];
    NSArray *result=[NSArray arrayWithObjects:@"TIME",self.batteryStateVal,self.batteryLevelVal, nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZBattery--Timer Callback"];
}

//Notifications
- (void)batteryStateChanged:(NSNotification *)notification
{
    if(notification){
        [self updateBatteryState];
    }
    else{
        [UIDevice currentDevice].batteryMonitoringEnabled = NO;
    }
}

- (void)batteryLevelChanged:(NSNotification *)notification
{
    [self updateBatteryLevel];
}
- (void)updateBatteryState
{
    UIDeviceBatteryState currentState = [UIDevice currentDevice].batteryState;
    if (UIDeviceBatteryStateCharging == currentState) {
        self.batteryStateVal = @"plugged";
    }
    else {
        self.batteryStateVal = @"unplugged";
    }
    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,@"state",@"level",nil];
    NSArray *result=[NSArray arrayWithObjects:@"STATE",self.batteryStateVal,self.batteryLevelVal, nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZBattery--State Callback"];
}
- (void)updateBatteryLevel
{
    float batteryLevel = [UIDevice currentDevice].batteryLevel;
    if (batteryLevel > 0.0) {
        int batteryValue= (int) round(batteryLevel *100);
        self.batteryLevelVal =[NSString stringWithFormat:@"%i",batteryValue];
        if([self.batteryLevelVal isEqualToString:self.batteryThersholdVal])
        {
            NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,@"state",@"level",nil];
            NSArray *result=[NSArray arrayWithObjects:@"THRESHOLD",self.batteryStateVal,self.batteryLevelVal, nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
            [Logger logger_Log:@"I" :@"APZBattery--Threshold Callback"];
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,@"state",@"level",nil];
            NSArray *result=[NSArray arrayWithObjects:@"LEVEL",self.batteryStateVal,self.batteryLevelVal, nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
            [Logger logger_Log:@"I" :@"APZBattery--Level Callback"];
        }
    }
    //    }
}
-(void)stopPlugin:(NSDictionary *)jsonDict{
    [self stopTimer];
    [[NSNotificationCenter defaultCenter]removeObserver:self];
    [UIDevice currentDevice].batteryMonitoringEnabled = NO;
    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
    NSArray *result=[NSArray arrayWithObjects:@"stopped", nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZBattery--Battery Monitor Stopped"];
    [self cleanPlugin];
    
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"I" :@"APZBattery--Battery Monitor cleanup"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end


