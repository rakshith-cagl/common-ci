//
//  AppzillonViewController+ServerCalls.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 28/07/16.
//
//

#import "AppzillonViewController.h"

@interface AppzillonViewController (ServerCalls)
-(void)CallDeviceRegistration:(CLLocation *)location;
-(void)EmptySandbox;
-(void)notificationRegWithToken;
-(void)APNSRegistration;
-(void)getServerNonce;
-(void)startNetworkRechabilityListener;
-(void)stopNetworkReachabilityListener;
@end
