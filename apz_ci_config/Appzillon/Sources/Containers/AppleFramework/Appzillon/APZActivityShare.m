//
//  APZActivityShare.m
//  Appzillon
//
//  Created by Manu Gowda N R on 5/28/18.
//

#import "APZActivityShare.h"
#import "Constants.h"
#import "Logger.h"
#import "APZJsonUtil.h"
#import "AppzillonViewController.h"


@interface APZActivityShare()
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@property(nonatomic,weak)AppzillonViewController *viewController;
@end
@implementation APZActivityShare

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if (jsonDict != nil ){
        NSArray* dataToShare;
        if ([jsonDict objectForKey:@"action"] != nil && ![[jsonDict objectForKey:@"action"] isEqualToString:@""]) {
            if ([[jsonDict objectForKey:@"action"] isEqualToString:@"text"]) {
                NSString *title = [jsonDict objectForKey:@"textToShare"];
                dataToShare = @[title];
                
            }else if([[jsonDict objectForKey:@"action"] isEqualToString:@"file"]){
                if ([jsonDict objectForKey:@"filePath"]) {
                    NSURL *data = [NSURL fileURLWithPath:[jsonDict objectForKey:@"filePath"]];
                    dataToShare = @[data];
                }else{
                    NSArray * resultkeys=[NSArray arrayWithObjects:@"error",nil];
                    NSArray * resultMsg=[NSArray arrayWithObjects:@"Native Share Failed - FilePath missing",nil];
                    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                    NSString *errorString=[NSString stringWithFormat:@"Native Share Failed - FilePath missing"];
                    [Logger logger_Log:@"E" :errorString];
                }
                
            }
            if(dataToShare != nil){
                UIActivityViewController* activityViewController =[[UIActivityViewController alloc] initWithActivityItems:dataToShare applicationActivities:nil];
                activityViewController.excludedActivityTypes = @[UIActivityTypeAirDrop,UIActivityTypePrint, UIActivityTypeCopyToPasteboard, UIActivityTypeAssignToContact, UIActivityTypeSaveToCameraRoll];
                [activityViewController setCompletionWithItemsHandler:
                    ^(NSString *activityType, BOOL completed, NSArray *returnedItems, NSError *activityError) {
                    if(completed){
                        NSArray * resultkeys=[NSArray arrayWithObjects:CBTEXT,nil];
                        NSArray * resultMsg=[NSArray arrayWithObjects:@"Native Share Success",nil];
                        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                        [Logger logger_Log:@"D" :@"Native Share Success"];
                    }else{
                        NSArray * resultkeys=[NSArray arrayWithObjects:@"error",nil];
                        NSArray * resultMsg=[NSArray arrayWithObjects:@"Native Share Failed",nil];
                        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                        NSString *errorString=[NSString stringWithFormat:@"Native Share Failed"];
                        [Logger logger_Log:@"E" :errorString];

                    }
                }];
                if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
                    [self.viewController presentViewController:activityViewController animated:YES completion:nil];
                }
                else {
                    UIPopoverPresentationController *popOverController =
                    [activityViewController popoverPresentationController];
                    popOverController.sourceRect = CGRectMake(self.viewController.view.frame.size.width/2, self.viewController.view.frame.size.height/2, 0, 0);
                    popOverController.sourceView = self.viewController.view;
                    popOverController.permittedArrowDirections = UIPopoverArrowDirectionAny;
                    activityViewController.modalPresentationStyle = UIModalPresentationPopover;
                    [self.viewController presentViewController:activityViewController animated: YES completion: nil];
                    
                }
                
            }else{
                NSArray * resultkeys=[NSArray arrayWithObjects:@"error",nil];
                NSArray * resultMsg=[NSArray arrayWithObjects:@"Native Share Failed",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                //                [self jsLayerCall:JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                NSString *errorString=[NSString stringWithFormat:@"Native Share Failed"];
                [Logger logger_Log:@"E" :errorString];
                
            }
        }else{
            NSArray * resultkeys=[NSArray arrayWithObjects:@"error",nil];
            NSArray * resultMsg=[NSArray arrayWithObjects:@"Native Share Failed - Action missing",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
            NSString *errorString=[NSString stringWithFormat:@"Native Share Failed - Action missing"];
            [Logger logger_Log:@"E" :errorString];
        }
        
    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"NativeShare Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}


@end

