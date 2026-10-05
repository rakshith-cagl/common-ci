//
//  MiscellaneousMethods.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 11/28/18.
//

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>
NS_ASSUME_NONNULL_BEGIN

@interface MiscellaneousMethods : NSObject
#pragma mark FileBrowser
+(NSString*)getItemsImage:(NSString*)item;

#pragma mark appLaunchedCheck
+(BOOL)getAppHasLaunchedBefore:(NSString*)mainAppId;
+(BOOL)getAppHasLaunchedOnOlderVersion;
+(void)setAppHasLaunchedBefore:(NSString*)mainAppId;

#pragma mark Grayscaling and BlackAndWhite Conversion
+ (UIImage *)grayScaleImage:(UIImage*)image;
+ (UIImage *)convertToBlackAndWhiteImage:(UIImage*)inputImage :(NSString*)threshold;


#pragma mark - JS CALLBACK FUNCTION
+(void) jsLayerCall:(WKWebView *)webView :(NSString *)jsFunctionName parameter:(NSString *)param;

#pragma mark - Bundle related function
+(NSBundle *)getAppBundle;

#pragma mark - Get Main ViewController function
+(id)getAppzillonViewController;


#pragma mark NSFileProtectionComplete
+(BOOL)writeData:(NSData*)data toFile:(NSString*)filePath;

@end


NS_ASSUME_NONNULL_END

