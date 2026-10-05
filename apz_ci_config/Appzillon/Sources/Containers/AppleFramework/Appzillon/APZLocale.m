//
//  APZLocale.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZLocale.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
@interface APZLocale()
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZLocale

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZLocale--execute"];
    if(jsonDict !=nil){
        self.pluginId=[jsonDict objectForKey:PLUGINID];
        [self getLocale];
    }
    else{
        [self cleanPlugin];
    }
}

#pragma mark - LOCALE
-(void)getLocale{
    NSString *locale = nil;
    locale = [[NSLocale currentLocale] localeIdentifier];
    if( (locale == nil) || ([locale isEqualToString:@""]) ){
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",FAILED_GET_LOCALE],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        
        [Logger logger_Log:@"E" :@"APZLocale--Failed to get Locale"];
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:LOCALE_JSON,nil];
        NSArray *result=[NSArray arrayWithObjects:locale,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZLocale--success"];
    }
    [self cleanPlugin];
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"I" :@"APZLocale--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end

