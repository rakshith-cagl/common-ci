//
//  VideoGalleryController.h
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>

@protocol VideoGalleryDoneDelegate <NSObject>
    -(void)doneVideoGallery:(id)plugin;
-(void)doneVideoGalleryWithOrientation:(UIInterfaceOrientation)orientation;
@end

@interface VideoGalleryController : NSObject
    @property(nonatomic,assign)id<VideoGalleryDoneDelegate>delegate;
    -(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict;
    -(void)executePlugin:(NSDictionary *)jsonDict;
@end


