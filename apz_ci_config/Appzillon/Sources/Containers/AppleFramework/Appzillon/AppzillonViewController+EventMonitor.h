//
//  AppzillonViewController+EventMonitor.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 29/07/16.
//
//

#import "AppzillonViewController.h"

@interface AppzillonViewController (EventMonitor)
-(void)initEventMonitoring;
-(void)handleCall:(NSDictionary *)result;
-(void)controlEvents:(NSDictionary *)jsonDict;
@end

