//
//  APZPulldown.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZPullDown.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Reachability.h"
#import "Logger.h"

@interface APZPullDown()
@property(nonatomic,weak)WKWebView *webView;
@property (strong,nonatomic)NSString *screenIdstr;
@property(strong,nonatomic)NSString *pluginId;
@property(strong,nonatomic) NSString *callIdstr;
@property(strong,nonatomic) UIRefreshControl *refreshControl;
@property(strong,nonatomic)UISwipeGestureRecognizer *swipedown;
@end

@implementation APZPullDown
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    NSString *refreshStr = [jsonDict objectForKey:@"refreshStatus"];
    if([refreshStr isEqualToString:@"enable"]){
        self.screenIdstr = [jsonDict objectForKey:@"screenId"];
        self.callIdstr = [jsonDict objectForKey:@"callId"];
        if(self.refreshControl == nil){
            self.refreshControl = [[UIRefreshControl alloc] init];
        }
        [self.refreshControl addTarget:self
                                action:@selector(enablePullDown)
                      forControlEvents:UIControlEventValueChanged];
        [self.webView.scrollView addSubview:self.refreshControl];
        [self.webView.scrollView setBounces:YES];
        NSArray *resultkeys=[NSArray arrayWithObjects:[NSString stringWithFormat:CBEVENT],nil];
        NSArray *result=[NSArray arrayWithObjects:@"started",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
    }
    else if([refreshStr isEqualToString:@"HideRefresh"]){
        [self.refreshControl endRefreshing];
        [self.webView setNeedsDisplay];
        NSArray *resultkeys=nil;
        NSArray *result=nil;
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
    }
    else{
        [self.refreshControl removeFromSuperview];
        [self.refreshControl endRefreshing];
        [self.webView.scrollView setBounces:NO];
        self.refreshControl=nil;
        NSArray *resultkeys=[NSArray arrayWithObjects:[NSString stringWithFormat:CBEVENT],nil];
        NSArray *result=[NSArray arrayWithObjects:@"stopped",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [self cleanPlugin];
    }
}

-(void)enablePullDown{
    self.refreshControl.attributedTitle = [[NSAttributedString alloc]initWithString:@"refreshing.."];
    [self.refreshControl beginRefreshing];
    NSArray *resultkeys=[NSArray arrayWithObjects:[NSString stringWithFormat:@"screenId"],[NSString stringWithFormat:@"callId"],CBEVENT,nil];
    NSArray *result=[NSArray arrayWithObjects:self.screenIdstr,self.callIdstr,@"pullDown",nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
}



#pragma mark -Plugin Clean
-(void)cleanPlugin{
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end
