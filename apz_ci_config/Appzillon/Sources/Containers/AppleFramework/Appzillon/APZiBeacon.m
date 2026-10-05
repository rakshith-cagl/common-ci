//
//  APZiBeacon.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 15/08/15.
//
//

#import "APZiBeacon.h"
#import <CoreLocation/CoreLocation.h>
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
#import "APZLocationPermission.h"
#import <CoreBluetooth/CoreBluetooth.h>

#define IS_OS_8_OR_LATER ([[[UIDevice currentDevice] systemVersion] floatValue] >= 8.0)

@interface APZiBeacon() <CLLocationManagerDelegate,CBCentralManagerDelegate, APZLocationDelegate>
@property(nonatomic,weak)WKWebView *webView;
@property (strong, nonatomic)  CLLocationManager *locationManageriBeacon;
@property (strong, nonatomic)  CLLocationManager *locationManager;
@property(assign)BOOL beaconInRange;
@property (strong, nonatomic) CLBeaconRegion *myBeaconRegion;
@property CLProximity lastProximity;
@property (nonatomic, strong) NSArray *locations;
@property(nonatomic,strong)CBCentralManager *bluetoothManager;
@property(nonatomic,strong)NSDictionary *jsonDictionary;
@property(nonatomic,strong)NSString *pluginId;
@property(nonatomic,strong)APZLocationPermission *locationPermission;
@end

@implementation APZiBeacon
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZiBeacon--startExecute"];
    _jsonDictionary=jsonDict;
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    _locationPermission = [[APZLocationPermission alloc]initWithDelegate:self];
    [_locationPermission askPermission];
    
}

-(void)locationPermission:(BOOL)permission{
    if(permission){
        [self detectBluetooth];
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-057",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZiBeacon--startExecute"];
        [self cleanPlugin];
    }
}

- (void)detectBluetooth
{
    if(!self.bluetoothManager)
    {
        self.bluetoothManager = [[CBCentralManager alloc] initWithDelegate:self queue:dispatch_get_main_queue()] ;
    }
    [self centralManagerDidUpdateState:self.bluetoothManager];
}

- (void)centralManagerDidUpdateState:(CBCentralManager *)central
{
    switch(_bluetoothManager.state)
    {
        case CBManagerStateResetting:
            break;
        case CBManagerStateUnsupported:
            [self deviceNotSupported];
            break;
        case CBManagerStateUnauthorized:
            break;
        case CBManagerStatePoweredOff:
            [self cleanPlugin];
            break;
        case CBManagerStatePoweredOn:
            [self startBeacon];
            break;
        default:
            break;
    }
}

-(void)startBeacon{
    _beaconInRange=NO;
    NSUUID *beaconUUID = [[NSUUID alloc] initWithUUIDString:[_jsonDictionary objectForKey:@"uuid"]];
    NSString* regionIdentifier = [[[MiscellaneousMethods getAppBundle] infoDictionary] objectForKey:@"CFBundleIdentifier"];
    if (beaconUUID) {
        if (!_beaconInRange) {
            CLBeaconRegion *beaconRegion = [[CLBeaconRegion alloc]
                                            initWithProximityUUID:beaconUUID identifier:regionIdentifier];
            beaconRegion.notifyOnEntry=YES;
            beaconRegion.notifyOnExit=YES;
            _locationManageriBeacon= [[CLLocationManager alloc] init];
            _locationManageriBeacon.delegate=self;
            _locationManageriBeacon.pausesLocationUpdatesAutomatically = NO;
            [_locationManageriBeacon startMonitoringForRegion:beaconRegion];
            [_locationManageriBeacon startRangingBeaconsInRegion:beaconRegion];
            [_locationManageriBeacon startUpdatingLocation];
            NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            NSArray *result=[NSArray arrayWithObjects:@"started",nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
            [Logger logger_Log:@"I" :@"APZiBeacon--started"];
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:INVALID_UUID,nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZiBeacon--Invalid UUID"];
        [self cleanPlugin];
        
    }
}

-(void)locationManager:(CLLocationManager *)manager didRangeBeacons:
(NSArray *)beacons inRegion:(CLBeaconRegion *)region {
    if(beacons.count > 0) {
        CLBeacon *nearestBeacon = beacons.firstObject;
        [nearestBeacon major];
        if(nearestBeacon.proximity == self.lastProximity ||
           nearestBeacon.proximity == CLProximityUnknown) {
            return;
        }
        self.lastProximity = nearestBeacon.proximity;
        switch(nearestBeacon.proximity) {
            case CLProximityFar:
                _beaconInRange=NO;
                break;
            case CLProximityNear:
                
                
            case CLProximityImmediate:
                if (!_beaconInRange) {
                    _beaconInRange=YES;
                    
                    NSNumber *major=[nearestBeacon major];
                    NSNumber *minor=[nearestBeacon minor];
                    NSString *proximityUUID=[[nearestBeacon proximityUUID] UUIDString];
                    CLProximity clProximity=[nearestBeacon proximity];
                    NSString *proximity;
                    if (clProximity ==CLProximityFar) {
                        proximity=@"far";
                    }else if (clProximity==CLProximityNear){
                        proximity=@"near";
                    }else if (clProximity==CLProximityImmediate){
                        proximity=@"immediate";
                    }else{
                        proximity=@"unknown";
                    }
                    NSString *accuracy=[NSString stringWithFormat:@"%f",[nearestBeacon accuracy]];
                    NSString *rssi=[NSString stringWithFormat:@"%ld",(long)[nearestBeacon rssi]];
                    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,@"major",@"minor",@"beaconUuid",@"proximity",@"distance",@"rssi",@"bluetoothAddress",@"typeCode",@"extraDataFields",@"manufactures",@"serviceUuid",@"txPower",nil];
                    NSArray *result=[NSArray arrayWithObjects:@"beaconDetected",major,minor,proximityUUID,proximity,accuracy,rssi,@"",@"",@"",@"",@"",@"",nil];
                    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
                    [Logger logger_Log:@"I" :@"APZiBeacon--deacon detected"];
                    break;
                    
                }
            case CLProximityUnknown:
                _beaconInRange=NO;
                break;
        }
    } else {
        
    }
}
-(void)locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region {
    [manager startRangingBeaconsInRegion:(CLBeaconRegion*)region];
    [_locationManageriBeacon startUpdatingLocation];
}


-(void)locationManager:(CLLocationManager *)manager
         didExitRegion:(CLRegion *)region {
    [manager stopRangingBeaconsInRegion:(CLBeaconRegion*)region];
    [_locationManageriBeacon stopUpdatingLocation];
    _beaconInRange=NO;
}

-(void)stopPlugin:(NSDictionary *)jsonDict{
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [_locationManageriBeacon stopUpdatingLocation];
    [Logger logger_Log:@"D" :@"APZiBeacon--beacon has stopped"];
    _beaconInRange=NO;
    [self cleanPlugin];
}
-(void)deviceNotSupported{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:PLUGIN_NOT_SUPPORTED,nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZiBeacon--Device Does Not Support Beacon"];
    [self cleanPlugin];
}
-(void)cleanPlugin{
    self.locationManageriBeacon=nil;
    self.locationPermission=nil;
    self.myBeaconRegion=nil;
    self.locations=nil;
    self.webView=nil;
    [self.delegate donePlugin:self];
}

@end


