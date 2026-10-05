//
//  AppzillonViewController+Gesture_Timer.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 29/07/16.
//
//

#import "AppzillonViewController.h"

@interface AppzillonViewController (Gesture)
-(void)startGesture:(NSDictionary*)result webView:(WKWebView*)webView :(NSString*)callee;
-(void)stopGesture:(NSDictionary*)result webView:(WKWebView*)webView :(NSString *)callee;
- (void)resetIdleTimer;
@end
