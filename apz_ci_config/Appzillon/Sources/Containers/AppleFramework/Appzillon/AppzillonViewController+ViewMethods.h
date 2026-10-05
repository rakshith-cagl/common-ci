//
//  AppzillonViewController+ViewMethods.h
//  Appzillon
//
//  Created by Pradeep Tiwari on 29/07/16.
//
//

#import "AppzillonViewController.h"

@interface AppzillonViewController (ViewMethods)
-(void)initSize;
-(void)initMultiviewPlugin;
-(void)multiviewOpen:(NSDictionary *) result;
-(void)multiviewClose:(NSDictionary *) result;
-(void)updateMultiView:(UIInterfaceOrientation)deviceOrientation;
-(void)resizeMultiview:(NSDictionary *) result;
-(void)disableBounce:(NSDictionary*)results;
@end

