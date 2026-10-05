//
//  AppzillonViewController+SplashImage.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 28/07/16.
//
//

#import "AppzillonViewController+SplashImage.h"


@implementation AppzillonViewController (SplashImage)

-(void)showSplashScreen{
    NSString* imageNamePortrait;
    NSString* imageNameLandscape;
    self.isSplashScreenLaunched = NO;
    UIInterfaceOrientation orientation= self.interfaceOrientation;
    @try{
        NSInteger yp=0;
        NSInteger yl=0;
        NSInteger currentHeightL;
        NSInteger currentWidthP;
        NSInteger currentWidthL;
        NSInteger currentHeightP;
        if ([[[UIDevice currentDevice] systemVersion]integerValue]>=8 && [[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
            if (orientation==UIInterfaceOrientationLandscapeLeft||orientation==UIInterfaceOrientationLandscapeRight) {
                currentHeightL=[[UIScreen mainScreen] bounds].size.height;
                currentWidthP=currentHeightL;
                currentWidthL=[[UIScreen mainScreen] bounds].size.width;
                currentHeightP=currentWidthL;
            }else{
                currentHeightL=[[UIScreen mainScreen] bounds].size.width;
                currentWidthP=currentHeightL;
                currentWidthL=[[UIScreen mainScreen] bounds].size.height;
                currentHeightP=currentWidthL;
            }
        }else{
            currentHeightL=[[UIScreen mainScreen] bounds].size.width;
            currentWidthP=[[UIScreen mainScreen] bounds].size.width;
            currentWidthL=[[UIScreen mainScreen] bounds].size.height;
            currentHeightP=[[UIScreen mainScreen] bounds].size.height;
        }
        if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
            CGFloat scaleFactor=[UIScreen mainScreen].scale;
            if ([UIApplication sharedApplication].statusBarHidden==true) {
                if (scaleFactor==1.0) {
                    imageNamePortrait=@"LaunchImages-700-Portrait~ipad.png";
                    imageNameLandscape=@"LaunchImages-700-Landscape~ipad.png";
                }else if(scaleFactor==2.0){
                    imageNamePortrait=@"LaunchImages-Portrait@2x~ipad.png";
                    imageNameLandscape=@"LaunchImages-Landscape@2x~ipad.png";
                }
            }else{
                if (scaleFactor==1.0) {
                    imageNamePortrait=@"LaunchImages-700-Portrait~ipad.png";
                    imageNameLandscape=@"LaunchImages-700-Landscape~ipad.png";
                }else if(scaleFactor==2.0){
                    imageNamePortrait=@"LaunchImages-Portrait@2x~ipad.png";
                    imageNameLandscape=@"LaunchImages-Landscape@2x~ipad.png";
                }
            }
        }
        if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone){
            CGFloat screenheight= [[UIScreen mainScreen] bounds].size.height;
            if(screenheight==736.0){
                imageNamePortrait=@"LaunchImages-800-Portrait-736h.png";
                imageNameLandscape=@"LaunchImages-800-Landscape-736h@3x.png";
            }else if (screenheight==812.0){
                imageNamePortrait=@"LaunchImages-1100-Portrait-2436h@3x.png";
                imageNameLandscape=@"LaunchImages-1100-Landscape-2436h@3x.png";
            }else if (screenheight==667.0){
                imageNamePortrait=@"LaunchImages-800-667h@2x.png";
                imageNameLandscape=@"ContainerImages/Default-Landscape-667h@2x~iphone.png";
            }else if (screenheight==568.0){
                imageNamePortrait=@"LaunchImages-700-568h@2x.png";
                imageNameLandscape=@"ContainerImages/Default-Landscape-568h@2x~iphone.png";
            }else if (screenheight==480.0){
                CGFloat scaleFactor=[UIScreen mainScreen].scale;
                if (scaleFactor==2.0) {
                    imageNamePortrait=@"LaunchImages@2x.png";
                    imageNameLandscape=@"ContainerImages/Default-Landscape@2x~iphone.png";
                }else if(scaleFactor==1.0){
                    imageNamePortrait=@"LaunchImages.png";
                    imageNameLandscape=@"ContainerImages/Default-Landscape~iphone.png";
                }
            }
        }
        self.splashViewL.backgroundColor = [UIColor blackColor];
        self.splashViewL=[[UIImageView alloc] initWithImage:[UIImage imageNamed:imageNameLandscape]];
        [self.splashViewL setHidden:NO];
        [self.splashViewL setFrame:CGRectMake(0, yl, currentWidthL, currentHeightL)];
        self.splashViewP.backgroundColor = [UIColor blackColor];
        self.splashViewP=[[UIImageView alloc] initWithImage:[UIImage imageNamed:imageNamePortrait]];
        [self.splashViewP setHidden:NO];
        [self.splashViewP setFrame:CGRectMake(0, yp, currentWidthP, currentHeightP)];
        if  (UIInterfaceOrientationIsLandscape(orientation))
        {
            if (self.splashViewL) {
                [self.view addSubview:self.splashViewL];
            }
        }else{
            if (self.splashViewP) {
                [self.view addSubview:self.splashViewP];
            }
        }
        if (self.isSplashLaunchedBefore==NO) {
            self.isSplashLaunchedBefore=YES;
        }
    }
    @catch (NSException *exception) {
        NSLog(@"-- Splash Screen Exception occured --%@",exception);
    }
    @finally {
        
    }
}



-(void)showSplashScreenWithRotation{
    if  (UIInterfaceOrientationIsLandscape(self.interfaceOrientation)){
        [self.splashViewP  removeFromSuperview];
        [self.view addSubview:self.splashViewL];
    }else{
        [self.splashViewL removeFromSuperview];
        [self.view addSubview:self.splashViewP];
    }
}

-(void)hideSplashScreen{
    if(self.splashScreenAuto==YES){
        self.splashScreenAuto=NO;
    }
    if(self.splashScreenManual==YES){
        self.splashScreenManual=NO;
    }
    if(self.isSplashScreenLaunched == NO){
        [self.splashViewP  removeFromSuperview];
        [self.splashViewL removeFromSuperview];
        self.splashViewL=nil;
        self.splashViewP =nil;
        self.isSplashScreenLaunched = YES;
    }
}
@end


