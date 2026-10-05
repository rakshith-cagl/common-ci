//
//  APZCompass.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZCompass.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import <CoreLocation/CoreLocation.h>
#import "Logger.h"
#import "APZLocationPermission.h"
@interface APZCompass()<CLLocationManagerDelegate>
@property(nonatomic,weak)WKWebView *webView;
@property (strong, nonatomic)  NSString *successCallbackCompass;
@property (strong, nonatomic)  NSString *failureCallbackCompass ;
@property (strong, nonatomic)  CLHeading * currentCompassHeading;
@property (strong, nonatomic) NSNumber *compassFreq;
@property(assign)NSInteger count;
@property (assign) BOOL compassFlag;
@property(strong,nonatomic) CLLocationManager *locationManagerCompass;
@property (strong, nonatomic)  NSString *compassPeriodicity;
@property(assign,nonatomic)CLLocationDirection appzillonMagneticHeading;
@property(assign,nonatomic)CLLocationDirection  appzillonTrueHeading;
@property(nonatomic,strong)NSString *pluginId;
@property(nonatomic,strong)NSDictionary *jsonDictionary;
@property(nonatomic,strong)APZLocationPermission *locationPermission;

@end


@implementation APZCompass
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.count=0;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZCompass--startExecute"];
    _jsonDictionary=jsonDict;
    self.compassFlag = YES;
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    _locationPermission = [[APZLocationPermission alloc]initWithDelegate:self];
    [_locationPermission askPermission];
}

-(void)locationPermission:(BOOL)permission{
    if(permission){
        [self compassActivity];
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-057",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [self cleanPlugin];
    }
}
-(void)compassActivity{
    self.compassFreq=[_jsonDictionary objectForKey:COMPASS_FREQ];
    if ([CLLocationManager headingAvailable]) {
        if ([CLLocationManager locationServicesEnabled]) {
            if (self.locationManagerCompass==nil) {
                self.compassPeriodicity=[_jsonDictionary objectForKey:COMPASS_PERIODICITY];
                if([self.compassPeriodicity isEqualToString:COMPASS_TIMED]){
                    NSInteger time=[self.compassFreq integerValue];
                    if (time==0) {
                        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",INVALID_TIME_INTERVAL],nil];
                        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                        [Logger logger_Log:@"E" :@"APZCompass--Invalid Time Interval"];
                        [self cleanPlugin];
                    }else{
                        self.locationManagerCompass=[[CLLocationManager alloc] init];
                        self.locationManagerCompass.delegate=self;
                        [self.locationManagerCompass  startUpdatingHeading];
                        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                            double temp= [self.compassFreq doubleValue];
                            temp=temp/1000;
                            while (self.compassFlag) {
                                [self returnMagneticnorth];
                                if (self.count==0) {
                                    self.count++;
                                }else{
                                    [NSThread sleepForTimeInterval:temp];
                                }
                            }
                        });
                    }
                }else {
                    self.locationManagerCompass=[[CLLocationManager alloc] init];
                    self.locationManagerCompass.delegate=self;
                    [self.locationManagerCompass  startUpdatingHeading];
                }
            }else{
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",COMPASS_RUNNING],nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :true :resultkeys :result]];
                [Logger logger_Log:@"E" :@"APZCompass--Compass already running"];
            }
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:GPS_SERVICES_OFF_ERROR,nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [self cleanPlugin];
            [Logger logger_Log:@"E" :@"APZCompass--Enable Location Services"];
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:COMPASS_NOT_SUPPORTED,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZCompass--Device does not have compass"];
        [self cleanPlugin];
    }
}

-(void)stopPlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZCompass--stopExecute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if ([CLLocationManager headingAvailable]) {
        if (self.locationManagerCompass ==nil) {
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *returnResult=[NSArray arrayWithObjects:COMPASS_IS_STOPPED_ERROR,nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :returnResult]];
            [Logger logger_Log:@"E" :@"APZCompass--compass is running already"];
        }
        else{
            self.compassFlag=NO;
            [self.locationManagerCompass stopUpdatingHeading];
            NSArray *resultkeys=nil;
            NSArray *returnResult=nil;
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :returnResult]];
            [Logger logger_Log:@"E" :@"APZCompass--compass stopped"];
            self.successCallbackCompass=nil;
            self.failureCallbackCompass= nil;
        }
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:COMPASS_NOT_SUPPORTED,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :returnResult]];
        [Logger logger_Log:@"E" :@"APZCompass--Device does not have compass"];
    }
    [self cleanPlugin];
}

- (void)locationManager:(CLLocationManager *)manager didUpdateHeading:(CLHeading *)newHeading{
    self.appzillonMagneticHeading= newHeading.magneticHeading;
    self.appzillonTrueHeading=newHeading.trueHeading;
    if ( [self.compassPeriodicity isEqualToString:COMPASS_ONCHANGE]) {
        NSArray *resultkeys=[NSArray arrayWithObjects:COMPASS_MAGNETICNORTH,COMPASS_TRUENORTH,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%f" ,self.appzillonMagneticHeading],[NSString stringWithFormat:@"%f" , self.appzillonTrueHeading ],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
    }else if([self.compassPeriodicity isEqualToString:COMPASS_NONE]){
        NSArray *resultkeys=[NSArray arrayWithObjects:COMPASS_MAGNETICNORTH,COMPASS_TRUENORTH,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%f" ,self.appzillonMagneticHeading],[NSString stringWithFormat:@"%f" ,self.appzillonTrueHeading ],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [manager stopUpdatingHeading];
        self.compassFlag=NO;
        self.successCallbackCompass=nil;
        self.failureCallbackCompass= nil;
        [self cleanPlugin];
    }
    else if([self.compassPeriodicity isEqualToString:COMPASS_TIMED]){
        if (self.count<=2) {
            self.count++;
            NSArray *resultkeys=[NSArray arrayWithObjects:COMPASS_MAGNETICNORTH,COMPASS_TRUENORTH,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%f" ,self.appzillonMagneticHeading],[NSString stringWithFormat:@"%f" ,self.appzillonTrueHeading ],nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
        }
        @synchronized(self) {
            self.currentCompassHeading=newHeading;
        }
    }
}




- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%ld",(long)[error code]],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :true :resultkeys :result]];
    NSString *errorString=[NSString stringWithFormat:@"APZCompasss--%@",[error localizedDescription]];
    [Logger logger_Log:@"E" :errorString];
    [manager stopUpdatingHeading];
    self.locationManagerCompass =nil;
    
}

-(void)returnMagneticnorth{
    if (self.count==0) {
        self.count++;
    }else{
        __block CLLocationDirection appzillonMagneticHeading;
        __block CLLocationDirection appzillontrueHeading;
        appzillonMagneticHeading= self.currentCompassHeading.magneticHeading;
        appzillontrueHeading= self.currentCompassHeading.trueHeading;
        @synchronized(self) {
            appzillonMagneticHeading= self.currentCompassHeading.magneticHeading;
            appzillontrueHeading= self.currentCompassHeading.trueHeading;
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            if (self.count>=2) {
                NSArray *resultkeys=[NSArray arrayWithObjects:COMPASS_MAGNETICNORTH,COMPASS_TRUENORTH,nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%f" ,appzillonMagneticHeading],[NSString stringWithFormat:@"%f" , appzillontrueHeading ],nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
            }
        });
    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZCompass--Done"];
    self.webView = nil;
    self.locationManagerCompass =nil;
    self.locationPermission=nil;
    [self.delegate donePlugin:self];
    
}

@end

