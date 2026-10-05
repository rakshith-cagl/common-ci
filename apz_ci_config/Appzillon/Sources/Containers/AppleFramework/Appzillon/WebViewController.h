//
//  googleViewController.h
//  Appzillon
//
//  Created by Created by Victor on 12/12/15.
//
//

#import <UIKit/UIKit.h>
#import <WebKit/WebKit.h>
#import "MiscellaneousMethods.h"
@protocol apzWebviewDelegate <NSObject>
-(void)viewCancelled:(UIInterfaceOrientation)interfaceOrientation;
@end
@interface WebViewController : UIViewController

{
    NSString *urlString;
    //    NSString*webSuccessCallBack;
    //    NSString*webFailiureCallBack;
    WKWebView *mainWeb;
    UIActivityIndicatorView *activityIndicator;
    UIBarButtonItem *backBarButtonItem;
    
}
@property(assign,nonatomic)id<apzWebviewDelegate>delegate;
@property(nonatomic, retain) NSString *urlString;
//@property(nonatomic, retain) NSString*webSuccessCallBack;
//@property(nonatomic, retain) NSString*webFailiureCallBack;
@property(nonatomic, retain) WKWebView *mainWeb;
@property(nonatomic, retain) UIBarButtonItem *backBarButtonItem;
@property(nonatomic,strong) NSString *pluginId;
@property(strong,nonatomic) NSDictionary *postData;
@property(nonatomic, retain) id successResponseForUrl;
@property(nonatomic, retain) NSString* cancelButtonRequired;
@end


