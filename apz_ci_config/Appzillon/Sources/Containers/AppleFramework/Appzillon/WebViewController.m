
//
//  googleViewController.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 09/10/15.
//
//

#import "WebViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import <QuartzCore/QuartzCore.h>

@interface WebViewController ()<WKNavigationDelegate,NSURLConnectionDelegate,UIDocumentInteractionControllerDelegate>
@property(strong,nonatomic)WKWebView *webview;
@property(nonatomic,strong)NSMutableData *receivedData;
@property(nonatomic,strong)NSString *webViewUrl;
@property(nonatomic,strong)NSString* verifier;
@property (nonatomic, strong) UIDocumentInteractionController *documentInteractionController;
@end

@implementation WebViewController

@synthesize urlString;
@synthesize mainWeb,backBarButtonItem;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _webview=[[WKWebView alloc]initWithFrame:CGRectMake(0, 0,self.view.frame.size.width, self.view.frame.size.height)];
    _webview.navigationDelegate=self;
    [_webview setContentMode:UIViewContentModeScaleAspectFit];
    
    if(self.postData != nil && [self.postData isKindOfClass:[NSDictionary class]]){
        [self setUrlWithPostData:self.postData];
    }else{
        [self setPlainUrl:urlString];
    }
    
    [self.view addSubview:_webview];
    
    activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    [activityIndicator setActivityIndicatorViewStyle:UIActivityIndicatorViewStyleGray];
    activityIndicator.autoresizingMask = UIViewAutoresizingFlexibleBottomMargin|UIViewAutoresizingFlexibleTopMargin|UIViewAutoresizingFlexibleLeftMargin|UIViewAutoresizingFlexibleRightMargin;
    activityIndicator.center = self.view.center;
    [activityIndicator setHidesWhenStopped:YES];
    [_webview addSubview:activityIndicator];
    
    
    if ([self.cancelButtonRequired isEqualToString:@"Y"] || [self.cancelButtonRequired isEqualToString:@"y"]) {
        UIBarButtonItem *cancelBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Cancel"style:UIBarButtonItemStylePlain target:self action:@selector(cancel)];
        
        self.navigationItem.rightBarButtonItems = [[NSArray alloc] initWithObjects:cancelBarButtonItem, nil];
        self.navigationItem.leftBarButtonItems = [[NSArray alloc] initWithObjects:backBarButtonItem, nil];
    }
}

-(void)cancel{
    [self dismissViewControllerAnimated:YES completion:^{
        [self.delegate viewCancelled:self.interfaceOrientation];
    }];
}

-(void)setPlainUrl:(NSString *)urlString{
    NSURL *targetURL = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]]];
    NSURLRequest *request = [NSURLRequest requestWithURL:targetURL];
    [_webview loadRequest:request];
}
-(void)setUrlWithPostData:(NSDictionary *)postData{
    NSMutableArray *keyValues = [NSMutableArray array];
    NSArray *allKeys = [postData allKeys];
    for (NSString *key in allKeys) {
        [keyValues addObject:[NSString stringWithFormat:@"%@=%@", key, postData[key]]];
    }
    NSString *paramsString = [keyValues componentsJoinedByString:@"&"];
    NSString *finalUrlString = [self.urlString stringByAppendingString:paramsString];
    NSURL *targetURL = [NSURL URLWithString:finalUrlString];
    NSURLRequest *request = [NSURLRequest requestWithURL:targetURL];
    [_webview loadRequest:request];
}

-(void)goBack{
    [self.webview goBack];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(null_unspecified WKNavigation *)navigation{
    [activityIndicator startAnimating];
}
- (void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)navigationAction decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler{
    [activityIndicator startAnimating];
    
    if(self.successResponseForUrl != nil){
        if ([self.successResponseForUrl isKindOfClass:[NSString class]]) {
            NSArray *requestUrlArray =  [[[navigationAction.request URL] absoluteString] componentsSeparatedByString: @"?"];
            NSArray *urlToTrackArray =  [self.successResponseForUrl componentsSeparatedByString: @"?"];
            NSString* requestUrl = [requestUrlArray objectAtIndex:0];
            NSString* urlToTrack = [urlToTrackArray objectAtIndex:0];
            BOOL containsUrl = [requestUrl containsString:urlToTrack];
            if(containsUrl){
                NSArray *resultkeys=[NSArray arrayWithObjects:WEBVIEW_URL,nil];
                NSArray *result=[NSArray arrayWithObjects:[[navigationAction.request URL] absoluteString],nil];
                [MiscellaneousMethods jsLayerCall:self.mainWeb :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
            }
        }else if ([self.successResponseForUrl isKindOfClass:[NSArray class]] || [self.successResponseForUrl isKindOfClass:[NSMutableArray class]]){
            for (NSString *successUrl in self.successResponseForUrl) {
                NSArray *requestUrlArray = [[[navigationAction.request URL] absoluteString] componentsSeparatedByString: @"?"];
                NSArray *urlToTrackArray = [successUrl componentsSeparatedByString: @"?"];
                NSString* requestUrl = [requestUrlArray objectAtIndex:0];
                NSString* urlToTrack = [urlToTrackArray objectAtIndex:0];
                BOOL containsUrl = [requestUrl containsString:urlToTrack];
                if(containsUrl){
                    NSArray *resultkeys = [NSArray arrayWithObjects:WEBVIEW_URL,nil];
                    NSArray *result = [NSArray arrayWithObjects:[[navigationAction.request URL] absoluteString],nil];
                    [MiscellaneousMethods jsLayerCall:self.mainWeb :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :resultkeys :result]];
                }
            }
        }
    }
    return decisionHandler(WKNavigationActionPolicyAllow);
}

- (void)webView:(WKWebView *)webView didFinishNavigation:(null_unspecified WKNavigation *)navigation{
    [activityIndicator stopAnimating];
    if ([webView canGoBack]){
        [backBarButtonItem setEnabled:YES];
        backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Back"style:UIBarButtonItemStylePlain target:self action:@selector(goBack)];
        self.navigationItem.backBarButtonItem=backBarButtonItem;
        
        self.navigationItem.leftBarButtonItems = [[NSArray alloc] initWithObjects:backBarButtonItem, nil];
    }
    else{
        [backBarButtonItem setEnabled:NO];
        [backBarButtonItem setTintColor:nil];
        backBarButtonItem.style = UIBarButtonItemStylePlain;
        backBarButtonItem.title = nil;
    }
}

-(NSString *)DocumentsDirectory{
    NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    return [[NSString alloc] initWithString:[paths objectAtIndex:0]];
}

-(void) willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    self.webview.frame=CGRectMake(0, 0,self.view.frame.size.width, self.view.frame.size.height);
    [self.webview setNeedsDisplay];
}
-(void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator
{
    self.webview.frame=CGRectMake(0, 0,size.width, size.height);
    self.webview.backgroundColor=[UIColor whiteColor];
    [self.webview setNeedsDisplay];
}

@end

