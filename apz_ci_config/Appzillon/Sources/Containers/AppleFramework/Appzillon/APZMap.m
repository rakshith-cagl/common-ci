//
//  APZMap.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZMap.h"
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "MapViewController.h"
#import <CoreLocation/CoreLocation.h>
#import "Logger.h"
@interface APZMap()<MapDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)MapViewController *mapController;
@property(nonatomic,strong)NSString *pluginId;
@end


@implementation APZMap

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZMap--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if (jsonDict) {
        [self showMap:[jsonDict objectForKey:MAP_MARKERINFO]];
    }
}

#pragma mark - Maps
-(void)showMap:(NSArray *)mapLocations{
    if([CLLocationManager locationServicesEnabled])
    {
        self.mapController =  [[MapViewController alloc]init];
        self.mapController.delegate = self;
        [self.mapController setMarkUpLocations:mapLocations];
        UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:self.mapController];
        [navigationController setModalPresentationStyle:UIModalPresentationFullScreen];
        [self.viewController presentViewController:navigationController animated:YES completion:nil];
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:GPS_SERVICES_OFF_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZMap--Enable Location Services"];
    }
}


-(void)mapCancelled:(UIInterfaceOrientation)interfaceOrientation{
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZMap--Cancelled"];
    [self cleanPlugin:interfaceOrientation];
}

-(void)getLocationCordinates:(NSString *)latitude longitude:(NSString *)longitude withInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation{
    [self.viewController dismissViewControllerAnimated:true completion:nil];
    NSArray *resultkeys=[NSArray arrayWithObjects:@"latitude",@"longitude",nil];
    NSArray *result=[NSArray arrayWithObjects:latitude,longitude,nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZMap--Success"];
    [self cleanPlugin:interfaceOrientation];
}

#pragma mark -Plugin Clean
-(void)cleanPlugin:(UIInterfaceOrientation)interfaceOrientation{
    [Logger logger_Log:@"D" :@"APZMap--Done"];
    [self.delegate donePluginWithOrientaion:self :interfaceOrientation];
    self.mapController = nil;
    self.mapController.delegate = nil;
    self.webView = nil;
}

@end



