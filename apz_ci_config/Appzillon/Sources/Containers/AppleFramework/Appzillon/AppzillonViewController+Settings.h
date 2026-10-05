//
//  AppzillonViewController+Settings.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 29/07/16.
//
//

#import "AppzillonViewController.h"

@interface AppzillonViewController (Settings)
-(void)deviceInfo:(NSDictionary*)result;
-(void)getUserPrefs:(NSDictionary*)result;
-(void)getUserPreference:(NSDictionary *)result;
-(void)setUserPrefs:(NSDictionary*)result;
-(void)setUserPreference:(NSDictionary *)jsonDict;
-(void)upgradeRequired:(NSDictionary*)result;
-(void)getUpdateActionRequired:(NSDictionary*)result;
-(void)getDeviceDateFormat;
-(void)getDeviceMapping;
-(void)selectURLWhiteListQuery;
-(NSString *)getUUID;
-(NSString *)getIPAddressCall;
-(void)getIPAddress:(NSDictionary *)result;
-(NSArray*)setScreenProperty;
-(void)remoteDebug:(NSDictionary *)debugInfo;
@end



