//
//  APZSensor.m
//  Appzillon
//
//  Created by Admin on 16/10/13.
//
//

#import "APZLocationPermission.h"
#import <UIKit/UIKit.h>

@implementation APZLocationPermission

-(id)initWithDelegate:(id)delegate{
    self = [super init];
    if (self) {
        self.delegate = delegate;
    }
    return self;
}
-(void)askPermission{
    if(_locationManager == nil){
        _locationManager= [[CLLocationManager alloc] init];
        _locationManager.delegate=self;
    }
    
    NSArray *versionArray = [[[UIDevice currentDevice] systemVersion] componentsSeparatedByString:@"."];
    if ([[versionArray objectAtIndex:0] intValue] >= 8) {
        [_locationManager requestWhenInUseAuthorization];
        [_locationManager startUpdatingLocation];
    }else{
        [self.delegate locationPermission:YES];
    }
    
}
-(void)askAlwaysPermission{
    if(_locationManager == nil){
        _locationManager= [[CLLocationManager alloc] init];
        _locationManager.delegate=self;
    }
    
    NSArray *versionArray = [[[UIDevice currentDevice] systemVersion] componentsSeparatedByString:@"."];
    if ([[versionArray objectAtIndex:0] intValue] >= 8) {
        [_locationManager requestAlwaysAuthorization];
        [_locationManager startUpdatingLocation];
    }else{
        [self.delegate locationPermission:YES];
    }
    
}



- (void)locationManager:(CLLocationManager *)manager
     didUpdateLocations:(NSArray *)locations {
    
    if(_locationManager != nil){
        [_locationManager stopUpdatingLocation];
        _locationManager = nil;
        [self.delegate locationPermission:YES];
    }
    
    
}
- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    if(_locationManager != nil){
        [_locationManager stopUpdatingLocation];
        _locationManager = nil;
        [self.delegate locationPermission:NO];
    }
    
    
}


@end

