//
//  APZPlugin.h
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>
#import "AZPluginDoneDelegate.h"

@interface APZPlugin : NSObject
@property(nonatomic,assign)id<AZPluginDoneDelegate>delegate;
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict;
-(void)executePlugin:(NSDictionary *)jsonDict;
-(void)stopPlugin:(NSDictionary *)jsonDict;
@end
