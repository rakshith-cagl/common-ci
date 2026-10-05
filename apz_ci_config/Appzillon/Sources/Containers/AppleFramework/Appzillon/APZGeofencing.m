//
//  APZGeofencing.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 10/12/14.
//
//

#import "APZGeofencing.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import <CoreLocation/CoreLocation.h>
#import "APZLocationPermission.h"
#import "Logger.h"
#define IS_OS_8_OR_LATER ([[[UIDevice currentDevice] systemVersion] floatValue] >= 8.0)

@interface APZGeofencing()<CLLocationManagerDelegate>
@property(nonatomic,weak)WKWebView *webView;
@property (strong, nonatomic) CLLocationManager *locationManager;
@property(strong,nonatomic) CLGeocoder *geocoder;
@property(strong,nonatomic) CLPlacemark *placemark;
@property (strong, nonatomic) NSString *region ;
@property (strong, nonatomic) CLLocation *locationToBound;
@property (strong, nonatomic)  NSArray *countryList ;
@property(assign)BOOL firstLaunch;
@property(strong,nonatomic)NSString *pluginId;
@property(nonatomic,strong)APZLocationPermission *locationPermission;
@property(nonatomic,strong)NSDictionary *jsonDictionary;
@end


double distanceThreshold;

@implementation APZGeofencing

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZGeofencing--startExecute"];
    _jsonDictionary=jsonDict;
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    _locationPermission = [[APZLocationPermission alloc]initWithDelegate:self];
    [_locationPermission askPermission];
    
}
-(void)locationPermission:(BOOL)permission{
    if(permission){
        [self initGeofencing];
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:GPS_SERVICES_OFF_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZGeofencing--Turn on Location Services"];
        [self  cleanPlugin];
    }
}

-(void)initGeofencing{
    self.firstLaunch=NO;
    double setRadius=[[_jsonDictionary objectForKey:@"radius"] doubleValue];
    if (setRadius==0 ) {
        distanceThreshold=10;
    }else{
        distanceThreshold=setRadius;
    }
    
    self.region=[_jsonDictionary objectForKey:@"region"];
    if ([self.region isEqualToString:@"LATLONG"]) {
        NSString *coOrdinate=[_jsonDictionary objectForKey:@"coordinates"];
        NSArray *lattLong=[coOrdinate componentsSeparatedByString:@","];
        double lattitude=[[lattLong objectAtIndex:0] doubleValue];
        double longitude=[[lattLong objectAtIndex:1]doubleValue];
        self.locationToBound= [[CLLocation alloc] initWithLatitude:lattitude longitude:longitude];
        [self runGeoFencing];
        //        [self locationPermission:YES];
    }else if([self.region isEqualToString:@"COUNTRY"]){
        self.countryList=[_jsonDictionary objectForKey:@"CountryList"];
        [self runGeoFencing];
        //        [self locationPermission:YES];
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:GEOFENC_INVALID_REGION,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZGeofencing--Invalid Region"];
        [self  cleanPlugin];
    }
}
-(void)runGeoFencing{
    if ([CLLocationManager locationServicesEnabled])  {
        if (self.locationManager==nil) {
            self.locationManager=[[CLLocationManager alloc] init];
            self.geocoder = [[CLGeocoder alloc] init];
            self.locationManager.delegate=self;
            NSArray *versionArray = [[[UIDevice currentDevice] systemVersion] componentsSeparatedByString:@"."];
            if ([[versionArray objectAtIndex:0] intValue] >= 8) {
                [self.locationManager requestAlwaysAuthorization];
                [self.locationManager startUpdatingLocation];
            }else{
                [self.locationManager startUpdatingLocation];
            }
        }
        else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:GPS_IS_RUNNING_ERROR,nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :true :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZGeofencing--Geofencing is running already"];
            [self  cleanPlugin];
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:GPS_SERVICES_OFF_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZGeofencing--Turn on Location Services"];
        [self  cleanPlugin];
        
    }
    
}

-(void)stopPlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZGeofencing--stopExecute"];
    if ( self.locationManager ==nil) {
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:GPS_IS_STOPPED_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :returnResult]];
        [Logger logger_Log:@"E" :@"APZGeofencing--Geofencing not running"];
    }else{
        [self.locationManager stopUpdatingLocation];
        NSArray *resultkeys=nil;
        NSArray *returnResult=nil;
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :returnResult]];
        [Logger logger_Log:@"I" :@"APZGeofencing--Geofencing not stopped"];
        self.locationManager =nil;
    }
    [self cleanPlugin];
}


#pragma mark -Location Manager
- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation{
    if (!self.firstLaunch) {
        self.firstLaunch=YES;
        NSArray *resultkeys=nil;
        NSArray *result=nil;
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZGeofencing--Geofencing Activated Successfully"];
    }
    
    if ([self.region isEqualToString:@"LATLONG"]) {
        double deltaDistance= [newLocation distanceFromLocation:self.locationToBound];
        deltaDistance=deltaDistance/1000;
        if (( deltaDistance< distanceThreshold)&&(deltaDistance>0))
        {
            [self jsLayerCallFrGeoFencing :false];
            [self cleanPlugin];
        }else{
            if (deltaDistance>0) {
                [self jsLayerCallFrGeoFencing :true];
                [self cleanPlugin];
            }
            
        }
    }else{
        [self.geocoder reverseGeocodeLocation:newLocation completionHandler:^(NSArray *placemarks, NSError *error) {
            if (error == nil && [placemarks count] > 0) {
                self.placemark = [placemarks lastObject];
                NSString *currentISOCountryCode = self.placemark.ISOcountryCode;
                for (NSString *countryCode in self.countryList) {
                    if ([countryCode isEqualToString:currentISOCountryCode]) {
                        [self jsLayerCallFrGeoFencing :false];
                        [self cleanPlugin];
                    }else{
                        
                        [self jsLayerCallFrGeoFencing :true];
                        [self cleanPlugin];
                    }
                }
            } else {
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-274",error.debugDescription,nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                NSString *errorString=[NSString stringWithFormat:@"APZGeofencing--%@",error.debugDescription];
                [Logger logger_Log:@"E" :errorString];
            }
        } ];
    }
}

-(void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    if (!self.firstLaunch) {
        self.firstLaunch=YES;
        NSArray *resultkeys=nil;
        NSArray *result=nil;
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZGeofencing--Geofencing Activated Successfully"];
    }
    CLLocation* location = [locations lastObject];
    if ([self.region isEqualToString:@"LATLONG"]) {
        double deltaDistance= [location distanceFromLocation:self.locationToBound];
        deltaDistance=deltaDistance/1000;
        if (( deltaDistance< distanceThreshold)&&(deltaDistance>0))
        {
            [self jsLayerCallFrGeoFencing :false];
            [self cleanPlugin];
        }
        else{
            if (deltaDistance>0) {
                [self jsLayerCallFrGeoFencing :true];
                [self cleanPlugin];
            }
            
        }
    }else{
        [self.geocoder reverseGeocodeLocation:location completionHandler:^(NSArray *placemarks, NSError *error) {
            if (error == nil && [placemarks count] > 0) {
                self.placemark = [placemarks lastObject];
                NSString *currentISOCountryCode = self.placemark.ISOcountryCode;
                for (NSString *countryCode in self.countryList) {
                    if ([countryCode isEqualToString:currentISOCountryCode]) {
                        [self jsLayerCallFrGeoFencing :false];
                        [self cleanPlugin];
                        
                    }else{
                        [self jsLayerCallFrGeoFencing :true];
                        [self cleanPlugin];
                        
                    }
                    
                }
            } else {
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-274",nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                NSString *errorString=[NSString stringWithFormat:@"APZGeofencing--%@",error.debugDescription];
                [Logger logger_Log:@"E" :errorString];
            }
        } ];
    }
}


- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:GPS_NETWORK_OFF],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :true :resultkeys :result]];
    [self.locationManager stopUpdatingLocation];
    self.locationManager=nil;
    [Logger logger_Log:@"E" :@"APZGeofencing--didfaildelegete"];
    [self  cleanPlugin];
}

#pragma mark -JS CALLBACK
-(void)jsLayerCallFrGeoFencing :(BOOL)GeoFencingStatus{
    NSArray *resultkeys=[NSArray arrayWithObjects:@"allowLocation",nil];
    NSArray *resultMsg=[NSArray arrayWithObjects:[NSNumber numberWithBool:GeoFencingStatus],nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
}
#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZGeofencing--Done"];
    self.locationManager=nil;
    self.locationPermission=nil;
    self.webView = nil;
    self.placemark=nil;
    self.geocoder=nil;
    self.locationToBound=nil;
    [self.delegate donePlugin:self];
}

@end

