//
//  AppBrowserController.h
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//

#import <UIKit/UIKit.h>
@protocol AppBrowserDelegate <NSObject>
-(void)selectedFile:(NSString *)fileName :(UIInterfaceOrientation)interfaceOrientation;
-(void)cancelBrowser:(UIInterfaceOrientation)interfaceOrientation;
@end


@interface AppBrowserController : UITableViewController
{
    NSString *appID;
}
@property(nonatomic,retain)NSString*appID;

    @property(assign,nonatomic)id<AppBrowserDelegate>delegate;
-(void)initDirectories:(NSDictionary *)fileDict openType:(NSString *)type filters:(NSString*)fileFilters;
@end


