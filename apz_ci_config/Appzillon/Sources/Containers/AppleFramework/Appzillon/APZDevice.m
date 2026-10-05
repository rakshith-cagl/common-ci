//
//  APZDevice.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZDevice.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Reachability.h"
#import "Logger.h"
#import <CoreTelephony/CTTelephonyNetworkInfo.h>
#import <CoreTelephony/CTCarrier.h>

@interface APZDevice()
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@property(nonatomic,strong)CTTelephonyNetworkInfo *networkInfo;
@property(nonatomic,strong)CTCarrier *carrier;
@end

@implementation APZDevice
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZDevice--execute"];
    if (jsonDict != nil) {
        self.pluginId=[jsonDict objectForKey:PLUGINID];
        [self devicePluginGetSysParam];
        
    }else{
        [self cleanPlugin];
    }
}

#pragma mark - DEVICE
-(void)devicePluginGetSysParam{
    UIDevice *device = [UIDevice currentDevice];
    CGRect screenBounds = [[UIScreen mainScreen] bounds];
    CGFloat screenScale = [[UIScreen mainScreen] scale];
    CGSize screenSize = CGSizeMake(screenBounds.size.width * screenScale, screenBounds.size.height * screenScale);
    device.batteryMonitoringEnabled = YES;
    float batteryLevel = [device batteryLevel];
    batteryLevel = (batteryLevel * 100);
    device.batteryMonitoringEnabled = NO;
    NSString *connType = [self connectionType];
    self.networkInfo = [CTTelephonyNetworkInfo new];
    self.carrier = [self.networkInfo subscriberCellularProvider];
    //    BOOL isSimAvailable = [self hasCellularCoverage];
    //    NSArray *simDetails=[[NSArray alloc]init];
    //    if(isSimAvailable==YES){
    //        simDetails=[self carrierDetails];
    //    }else{
    //        simDetails=@[];
    //    }
    NSArray *resultkeys=[NSArray arrayWithObjects:DEVICE_JSON_OSNAME,DEVICE_JSON_OSVERSION,DEVICE_JSON_DEVTYPE,DEVICE_JSON_SCREENRESOLUTION,DEVICE_JSON_CONNECECTIONTYPE,DEVICE_JSON_BATTERYSTATUS,nil];
    NSArray *result=[NSArray arrayWithObjects:[device systemName],[device systemVersion],[device model],[NSString stringWithFormat:@"%.0fx%.0f",screenSize.width,screenSize.height],connType,[NSString stringWithFormat:@"%.0f%%",batteryLevel],nil];
    [Logger logger_Log:@"I" :@"APZDevice--Suceess"];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    
    [self cleanPlugin];
}

-(NSString*)connectionType{
    Reachability *reachability = [Reachability reachabilityForInternetConnection];
    [reachability startNotifier];
    NSString *connectionType;
    NetworkStatus status = [reachability currentReachabilityStatus];
    if(status == NotReachable)
    {
        connectionType=@"none";
    }
    else if (status == ReachableViaWiFi)
    {
        connectionType=@"wifi";
    }
    else if (status == ReachableViaWWAN)
    {
        CTTelephonyNetworkInfo *netinfo = [[CTTelephonyNetworkInfo alloc] init];
        if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyGPRS]) {
            connectionType=@"2G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyEdge]) {
            connectionType=@"2G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyWCDMA]) {
            connectionType=@"3G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyHSDPA]) {
            connectionType=@"3G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyHSUPA]) {
            connectionType=@"3G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyCDMA1x]) {
            connectionType=@"2G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:
                    CTRadioAccessTechnologyCDMAEVDORev0]) {
            connectionType=@"3G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyCDMAEVDORevA]) {
            connectionType=@"3G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyCDMAEVDORevB]) {
            connectionType=@"3G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyeHRPD]) {
            connectionType=@"3G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyLTE]) {
            connectionType=@"4G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyNRNSA]) {
            connectionType=@"5G";
        } else if ([netinfo.currentRadioAccessTechnology isEqualToString:CTRadioAccessTechnologyNR]) {
            connectionType=@"5G";
        }
    }
    return connectionType;
}

-(BOOL)hasCellularCoverage
{
    
    if (!self.carrier.isoCountryCode) {
        [Logger logger_Log:@"I" :@"No sim present Or No cellular coverage or phone is on airplane mode"];
        return NO;
    }
    //    [self carrierDetails];
    return YES;
    
}
-(NSArray*)carrierDetails{
    
    NSString *carrierName = self.carrier.carrierName;
    NSString *mobilenetworkCode = self.carrier.mobileNetworkCode;
    NSString *mobilecountryCode = self.carrier.mobileCountryCode;
    NSString *ISOcountryCode = self.carrier.isoCountryCode;
    NSArray *resultkeys=[NSArray arrayWithObjects:DEVICE_CARRIER_NAME,DEVICE_CARRIER_CCID,DEVICE_CARRIER_SLOT,@"MOBILE NETWORK CODE",@"MOBILE COUNTRY CODE",@"ISO COUNTRY CODE",nil];
    NSArray *result=[NSArray arrayWithObjects:carrierName,@"NOT AVAILABLE",[NSNumber numberWithInt:1],mobilenetworkCode,mobilecountryCode,ISOcountryCode,nil];
    NSDictionary *simdetailsDict=[[NSDictionary alloc]initWithObjects:result forKeys:resultkeys];
    NSArray *simdetailsArray=[[NSArray alloc]initWithObjects:simdetailsDict,nil];
    return simdetailsArray;
}



#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZDevice--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end
