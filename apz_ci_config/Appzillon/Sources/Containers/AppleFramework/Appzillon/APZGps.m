//
//  APZGps.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZGps.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import <CoreLocation/CoreLocation.h>
#import "Logger.h"
#import "APZLocationPermission.h"
#define IS_OS_8_OR_LATER ([[[UIDevice currentDevice] systemVersion] floatValue] >= 8.0)

@interface APZGps()<CLLocationManagerDelegate>
@property (nonatomic,weak)WKWebView *webView;
@property (strong, nonatomic)CLLocationManager *locationManagerGPS;
@property (nonatomic, assign)BOOL onChangeGPS;
@property (nonatomic, assign)BOOL gpsRunning;
@property (strong, nonatomic)NSNumber *TimeAcc;
@property (nonatomic,assign)BOOL timeBasedGPSActivated;
@property (nonatomic,strong)NSString *pluginId;
@property (nonatomic,strong)NSDictionary *jsonDictionary;
@property (nonatomic,strong)APZLocationPermission *locationPermission;
@end
@implementation APZGps

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)startTracking{
    if (self.gpsRunning == true) {
        [self.locationManagerGPS stopUpdatingLocation];
        self.gpsRunning = false;
    } else {
        NSArray *versionArray = [[[UIDevice currentDevice] systemVersion] componentsSeparatedByString:@"."];
        if ([[versionArray objectAtIndex:0] intValue] >= 8) {
            [self.locationManagerGPS requestAlwaysAuthorization];
            [self.locationManagerGPS startUpdatingLocation];
        }
        else{
            [self.locationManagerGPS startUpdatingLocation];
        }
        self.gpsRunning = true;
    }
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZGps--startExecute"];
    _jsonDictionary=jsonDict;
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    _locationPermission = [[APZLocationPermission alloc]initWithDelegate:self];
    [_locationPermission askPermission];
    
}
-(void)locationPermission:(BOOL)permission{
    if(permission){
        [self callGPS];
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-057",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZGps--Enable Location Services"];
        [self cleanPlugin];
    }
}
-(void)callGPS{
    NSInteger tempDistanceAcc=[[_jsonDictionary objectForKey:GPS_Distance_Accuracy] integerValue];
    NSInteger tempTimeAcc = [[_jsonDictionary objectForKey:GPS_Time_Accuracy] integerValue];
    if ([CLLocationManager locationServicesEnabled])  {
        if (self.locationManagerGPS==nil) {
            self.gpsRunning = false;
            self.locationManagerGPS=[[CLLocationManager alloc] init];
            self.locationManagerGPS.delegate=self;
            if ([[_jsonDictionary objectForKey:GPS_PERIODICITY]isEqualToString:GPS_ONCHANGE]){
                self.locationManagerGPS.desiredAccuracy=kCLLocationAccuracyBestForNavigation;
                self.onChangeGPS=YES;
                NSArray *versionArray = [[[UIDevice currentDevice] systemVersion] componentsSeparatedByString:@"."];
                if ([[versionArray objectAtIndex:0] intValue] >= 8) {
                    [self.locationManagerGPS requestAlwaysAuthorization];
                    [self.locationManagerGPS startUpdatingLocation];
                }else{
                    [self.locationManagerGPS startUpdatingLocation];
                }
            }else if([[_jsonDictionary objectForKey:GPS_PERIODICITY]isEqualToString:GPS_NONE]){
                self.locationManagerGPS.desiredAccuracy=kCLLocationAccuracyBest;
                self.onChangeGPS=NO;
                NSArray *versionArray = [[[UIDevice currentDevice] systemVersion] componentsSeparatedByString:@"."];
                if ([[versionArray objectAtIndex:0] intValue] >= 8) {
                    [self.locationManagerGPS requestAlwaysAuthorization];
                    [self.locationManagerGPS startUpdatingLocation];
                }else{
                    [self.locationManagerGPS startUpdatingLocation];
                }
            }else{
                if(tempDistanceAcc == 0 && tempTimeAcc == 0){
                    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                    NSArray *result=[NSArray arrayWithObjects:INVALID_TIME_INTERVAL,nil];
                    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                    [Logger logger_Log:@"E" :@"APZGps--Atleast one Entry is required from Time or Distance"];
                    [self  cleanPlugin];
                }else{
                    self.onChangeGPS=YES;
                    if ([[_jsonDictionary objectForKey:GPS_Time_Accuracy] isEqualToString:@""]) {
                        self.timeBasedGPSActivated = false;
                    }else{
                        self.timeBasedGPSActivated = true;
                        NSInteger timeInt =tempTimeAcc;
                        timeInt = timeInt/1000;
                        NSTimeInterval tempTimeInterval = timeInt;
                        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_LOW, 0), ^{
                            NSTimer* t = [NSTimer scheduledTimerWithTimeInterval:tempTimeInterval target:self selector:@selector(startTracking) userInfo:nil repeats:YES];
                            [[NSRunLoop currentRunLoop] addTimer:t forMode:NSDefaultRunLoopMode];
                            [[NSRunLoop currentRunLoop] run];
                        });
                    }
                    if ([[_jsonDictionary objectForKey:GPS_PERIODICITY]isEqualToString:GPS_ONCHANGE]){
                        self.locationManagerGPS.desiredAccuracy=kCLLocationAccuracyBestForNavigation;
                        self.onChangeGPS=YES;
                    }else if([[_jsonDictionary objectForKey:GPS_PERIODICITY]isEqualToString:GPS_NONE]){
                        self.locationManagerGPS.desiredAccuracy=kCLLocationAccuracyBest;
                        self.onChangeGPS=NO;
                    }else if(tempDistanceAcc==0){
                        self.onChangeGPS=YES;
                        self.locationManagerGPS.desiredAccuracy=kCLLocationAccuracyBest;
                    }else if (tempDistanceAcc<10) {
                        self.onChangeGPS=YES;
                        self.locationManagerGPS.desiredAccuracy=kCLLocationAccuracyBestForNavigation;
                    }else if(tempDistanceAcc==10){
                        self.onChangeGPS=YES;
                        self.locationManagerGPS.desiredAccuracy=kCLLocationAccuracyNearestTenMeters;
                    }else if(tempDistanceAcc<=100){
                        self.onChangeGPS=YES;
                        self.locationManagerGPS.desiredAccuracy=kCLLocationAccuracyHundredMeters;
                    }else if(tempDistanceAcc<=1000){
                        self.onChangeGPS=YES;
                        self.locationManagerGPS.desiredAccuracy=kCLLocationAccuracyKilometer;
                    }else {
                        self.onChangeGPS=YES;
                        self.locationManagerGPS.desiredAccuracy=kCLLocationAccuracyThreeKilometers;
                    }
                    NSArray *versionArray = [[[UIDevice currentDevice] systemVersion] componentsSeparatedByString:@"."];
                    if ([[versionArray objectAtIndex:0] intValue] >= 8) {
                        [self.locationManagerGPS requestAlwaysAuthorization];
                        [self.locationManagerGPS startUpdatingLocation];
                    }
                    else{
                        [self.locationManagerGPS startUpdatingLocation];
                    }
                }
            }
        }
        else{
            self.gpsRunning = true;
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:GPS_IS_RUNNING_ERROR,nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :true :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZGps--GPS is running already"];
            [self  cleanPlugin];
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:GPS_SERVICES_OFF_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZGps--Application don't have access to run GPS"];
        [self  cleanPlugin];
    }
    
}
-(void)stopPlugin:(NSDictionary *)jsonDict{
    if (self.timeBasedGPSActivated == true) {
        CFRunLoopStop(CFRunLoopGetCurrent());
    }
    if ( self.locationManagerGPS ==nil) {
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:GPS_IS_STOPPED_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :returnResult]];
        [Logger logger_Log:@"E" :@"APZGps--GPS is not running"];
    }else{
        [self.locationManagerGPS stopUpdatingLocation];
        NSArray *resultkeys=nil;
        NSArray *returnResult=nil;
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :returnResult]];
        [Logger logger_Log:@"I" :@"APZGps--GPS stopped"];
        self.locationManagerGPS =nil;
    }
    [self cleanPlugin];
}

#pragma mark -Location Manager
- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation{
    NSArray *resultkeys=[NSArray arrayWithObjects:GPS_LATITUDE,GPS_LONGITUDE,GPS_ALTITUDE,GPS_ACCURACY,GPS_ALTITUDEACCURACY,GPS_SPEED,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%f" ,newLocation.coordinate.latitude],[NSString stringWithFormat:@"%f" , newLocation.coordinate.longitude ],[NSString stringWithFormat:@"%f" , newLocation.altitude],[NSString stringWithFormat:@"%f" , newLocation.horizontalAccuracy ],[NSString stringWithFormat:@"%f" , newLocation.verticalAccuracy ],[NSString stringWithFormat:@"%f" , newLocation.speed ],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
    if (!self.onChangeGPS) {
        [manager stopUpdatingLocation];
        [self cleanPlugin];
    }
}


- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:GPS_NETWORK_OFF],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [self.locationManagerGPS stopUpdatingLocation];
    NSString *errorString=[NSString stringWithFormat:@"APZGps--%@",[error localizedFailureReason]];
    [Logger logger_Log:@"E" :errorString];
    [self  cleanPlugin];
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"E" :@"APZGPS--Done"];
    self.locationManagerGPS=nil;
    self.locationPermission=nil;
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end

