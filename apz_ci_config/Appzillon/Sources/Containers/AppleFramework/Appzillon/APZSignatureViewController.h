//
//  APZSignatureViewController.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 10/10/14.
//
//

#import <UIKit/UIKit.h>
@protocol singatureViewDelegate
-(void)callback:(NSString *)base64String :(UIInterfaceOrientation)interfaceOrientation;
-(void)cancelSignaturePad :(UIInterfaceOrientation)interfaceOrientation;
@end
@interface APZSignatureViewController : UIViewController
@property(assign,nonatomic)id<singatureViewDelegate>delegate;
@end

