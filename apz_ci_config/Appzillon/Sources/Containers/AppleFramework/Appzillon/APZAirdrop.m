//
//  APZAirdrop.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 24/06/14.
//
//

#import "APZAirdrop.h"
#import "Constants.h"
#import "APZJsonUtil.h"
#import "AppzillonViewController.h"
#import "APZAirdrop.h"

@interface APZAirdrop()
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZAirdrop
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZAirdrop--execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if (jsonDict != nil) {
        NSString *action=[jsonDict objectForKey:AIRDROP_ACTION];
        if ([action isEqualToString:@"DEVICE"]) {
            NSUInteger version = [[[UIDevice currentDevice] systemVersion]integerValue];
            if (version>=7) {
                [self invokeAirdrop:jsonDict];
            }else{
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:AIRDROP_NOT_SUPPORTED],nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                [Logger logger_Log:@"E" :@"APZAirdrop--Versions before IOS7 does not have Airdrop Facility"];
            }
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:AIRDROP_TAG_SUPPORTED],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZAirdrop--Tag is not supported in ios"];
        }
    }else{
        [self cleanPlugin];
    }
}

-(void)invokeAirdrop:(NSDictionary *)jsonDict{
    NSArray *objectsToShare;
    if([[jsonDict objectForKey:@"type"] isEqual:AIRDROPTYPE_FILE]){
        NSURL *fileUrl = [self fileToURL:[jsonDict objectForKey:@"content"]];
        objectsToShare=@[fileUrl];
        [self Airdrop:objectsToShare];
    }
    if([[jsonDict objectForKey:@"type"] isEqual:AIRDROPTYPE_MSG]){
        NSString *simpleText = [jsonDict objectForKey:@"content"];
        objectsToShare=@[simpleText];
        [self Airdrop:objectsToShare];
    }
    if([[jsonDict objectForKey:@"type"] isEqual:AIRDROPTYPE_URL]){
        NSString *urlString = [jsonDict objectForKey:@"content"];
        BOOL isValidURL=[self isValidUrl:urlString];
        if (isValidURL==YES) {
            NSString *escapedUrlString = [urlString stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]];
            NSURL* url = [NSURL URLWithString:escapedUrlString];
            objectsToShare=@[url];
            [self Airdrop:objectsToShare];
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:AIRDROP_URL_NOT_VALID],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZAirdrop--URL Passed is not Valid"];
        }
    }
}

-(void)Airdrop:(NSArray *)itemShared{
    UIActivityViewController *controller = [[UIActivityViewController alloc] initWithActivityItems:itemShared applicationActivities:nil];
    NSArray *excludedActivities = @[UIActivityTypePostToTwitter, UIActivityTypePostToFacebook,
                                    UIActivityTypePostToWeibo,
                                    UIActivityTypeMessage, UIActivityTypeMail,
                                    UIActivityTypePrint, UIActivityTypeCopyToPasteboard,
                                    UIActivityTypeAssignToContact, UIActivityTypeSaveToCameraRoll,
                                    UIActivityTypeAddToReadingList, UIActivityTypePostToFlickr,
                                    UIActivityTypePostToVimeo, UIActivityTypePostToTencentWeibo];
    controller.excludedActivityTypes = excludedActivities;
    [self.viewController presentViewController:controller animated:YES completion:nil];
}

- (NSURL *) fileToURL:(NSString*)filename
{
    NSArray *fileComponents = [filename componentsSeparatedByString:@"."];
    NSString *filePath = [[MiscellaneousMethods getAppBundle] pathForResource:[fileComponents objectAtIndex:0] ofType:[fileComponents objectAtIndex:1]];
    return [NSURL fileURLWithPath:filePath];
}

- (BOOL)isValidUrl:(NSString *)urlString{
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:urlString]];
    return [NSURLConnection canHandleRequest:request];
}



#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"E" :@"APZAirdrop--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}
@end


