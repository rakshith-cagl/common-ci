//
//  APZUserLocation.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 16/10/15.
//
//

#import "APZUserLocation.h"
#import "Constants.h"
#import "Logger.h"
#import "APZJsonUtil.h"
#import <CoreLocation/CoreLocation.h>
#import "APZLocationPermission.h"

@interface APZUserLocation()<CLLocationManagerDelegate>
@property(nonatomic,weak)WKWebView *webView;
@property(strong,nonatomic) CLLocationManager *locationManager;
@property(strong,nonatomic)NSString* pluginId;
@property (nonatomic,strong)APZLocationPermission *locationPermission;
@end
@implementation APZUserLocation
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}
-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZUserLocation execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    _locationPermission = [[APZLocationPermission alloc]initWithDelegate:self];
    [_locationPermission askPermission];
    //#if __IPHONE_OS_VERSION_MAX_ALLOWED <= 70000
    //    if([CLLocationManager locationServicesEnabled])
    //    {
    //#else
    //    if([CLLocationManager locationServicesEnabled]&&[CLLocationManager authorizationStatus] != kCLAuthorizationStatusDenied)
    //        {
    //#endif
    //    _locationManager = [[CLLocationManager alloc] init];
    //    _locationManager.delegate = self;
    //    _locationManager.distanceFilter = kCLDistanceFilterNone;
    //    _locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    //    [_locationManager startUpdatingLocation];
    //        }else{
    //            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    //            NSArray *result=[NSArray arrayWithObjects:GPS_SERVICES_OFF_ERROR,nil];
    //            [self jsLayerCall:JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    //            [Logger logger_Log:@"E" :@"APZUserLocation Location Services not enabled"];
    //            [self cleanPlugin];
    //        }
}
-(void)locationPermission:(BOOL)permission{
    if(permission){
        _locationManager = [[CLLocationManager alloc] init];
        _locationManager.delegate = self;
        _locationManager.distanceFilter = kCLDistanceFilterNone;
        _locationManager.desiredAccuracy = kCLLocationAccuracyBest;
        [_locationManager startUpdatingLocation];
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-057",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZGps--Enable Location Services"];
        [self cleanPlugin];
    }
}
- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation{
    NSString* latitude = [[NSString alloc] initWithFormat:@"%f", newLocation.coordinate.latitude];
    NSString* longitude = [[NSString alloc] initWithFormat:@"%f",newLocation.coordinate.longitude];
    NSArray *resultkeys=[NSArray arrayWithObjects:GPS_LATITUDE,GPS_LONGITUDE,nil];
    NSArray *result=[NSArray arrayWithObjects:latitude,longitude,nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZUserLocation sucsess"];
    [self cleanPlugin];
}


- (void)locationManager:(CLLocationManager *)manager
     didUpdateLocations:(NSArray *)locations {
    CLLocation *location = [locations lastObject];
    NSString* latitude = [[NSString alloc] initWithFormat:@"%f", location.coordinate.latitude];
    NSString* longitude = [[NSString alloc] initWithFormat:@"%f",location.coordinate.longitude];
    CLGeocoder *geocoder = [[CLGeocoder alloc] init];
    [geocoder reverseGeocodeLocation:location completionHandler:^(NSArray *placemarks, NSError *error) {
        if (error) {
            NSLog(@"Error %@", error.description);
        } else {
            CLPlacemark *placemark = [placemarks lastObject];
            NSString*subThoroughfare=placemark.subThoroughfare;
            if(subThoroughfare==nil){
                subThoroughfare=@"";
            }
            NSString* thoroughfare=placemark.thoroughfare;
            if(thoroughfare==nil){
                thoroughfare=@"";
            }
            NSString* subLocality=placemark.subLocality;
            if(subLocality==nil){
                subLocality=@"";
            }
            NSString *locality=placemark.locality;
            if(locality==nil){
                locality=@"";
            }
            NSString*administrativeArea=placemark.administrativeArea;
            if(administrativeArea==nil){
                administrativeArea=@"";
            }
            NSString *postalCode=placemark.postalCode;
            if(postalCode==nil){
                postalCode=@"";
            }
            NSString *country=placemark.country;
            if(country==nil){
                country=@"";
            }
            NSString* locationAddress = [NSString stringWithFormat:@"%@, %@, %@, %@, %@, %@, %@",subThoroughfare,thoroughfare,subLocality,locality,administrativeArea,postalCode,country];
            NSArray *resultkeys=[NSArray arrayWithObjects:GPS_LATITUDE,GPS_LONGITUDE,GPS_LOCATION_ADDRESS,nil];
            NSArray *result=[NSArray arrayWithObjects:latitude,longitude,locationAddress,nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
            [Logger logger_Log:@"I" :@"APZUserLocation sucsess"];
            [self cleanPlugin];
        }
    }];
    
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:GPS_NETWORK_OFF],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [_locationManager stopUpdatingLocation];
    _locationManager=nil;
    [Logger logger_Log:@"E" :@"APZUserLocation failure"];
    [self  cleanPlugin];
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    _locationManager=nil;
    _locationPermission=nil;
    [Logger logger_Log:@"E" :@"APZUserLocation done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end
