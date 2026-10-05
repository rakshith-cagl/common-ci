
//
//  AppzillonViewController+PluginCalls.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 29/07/16.
//
//

#import "AppzillonViewController.h"

@interface AppzillonViewController (PluginCalls)
- (BOOL)execute:(NSString*)pluginName json:(NSDictionary *)jsonDict wbView:(WKWebView *)webView;
//-(void)missingPluginErrorMessage:(NSString *)plugin :(NSDictionary*)jsonDict;
-(NSString*)getClassNameForPlugin:(NSString*)command;
@end

