//
//  MusicGalleryController.h
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>

@protocol MusicGalleryDoneDelegate <NSObject>
-(void)doneMusicGallery:(id)sender;
-(void)doneMusicGalleryWithOrientation:(UIInterfaceOrientation)orientation;
@end

@interface MusicGalleryController : NSObject
    @property(nonatomic,assign)id<MusicGalleryDoneDelegate>delegate;
    -(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict;
    -(void)executePlugin:(NSDictionary *)jsonDict;
@end

