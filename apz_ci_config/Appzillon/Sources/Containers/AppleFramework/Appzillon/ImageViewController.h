//
//  ImageViewController.h
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//

#import <UIKit/UIKit.h>
@protocol imageViewDelegate
    
-(void)cancelImageView:(UIInterfaceOrientation)interfaceOrientation;
-(void)imageCropperCallback:(NSString *)imageCropResult;
@end

@interface ImageViewController : UIViewController
    
@property(assign,nonatomic)id<imageViewDelegate>delegate;
    
-(void)setupImage:(NSString *)path;
-(void)setupImage:(NSString *)path withCustomNavigationItem: (BOOL) isCustomNavigationItemNeeded withJson:(NSDictionary *)jsonDict;
    
@end
