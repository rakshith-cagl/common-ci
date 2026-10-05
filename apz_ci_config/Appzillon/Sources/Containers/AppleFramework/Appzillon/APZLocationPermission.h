//
//  APZSensor.h
//  Appzillon
//
//  Created by Admin on 16/10/13.
//
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@protocol APZLocationDelegate<NSObject>
    @required
    -(void)locationPermission:(BOOL)permission;
@end


@interface APZLocationPermission:NSObject<CLLocationManagerDelegate>

    @property(nonatomic,assign)id<APZLocationDelegate>delegate;
    @property (strong, nonatomic)  CLLocationManager *locationManager;

    -(id)initWithDelegate:(id)delegate;

    -(void)askPermission;
    -(void)askAlwaysPermission;

@end

