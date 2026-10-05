//
//  AppzillonViewController+ViewMethods.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 29/07/16.
//
//

#import "AppzillonViewController+ViewMethods.h"

@implementation AppzillonViewController (ViewMethods)
-(void)initSize{
    int currentHeightP=0;
    int currentWidthP=0;
    int currentWidthL=0;
    int currentHeightL=0;
    int yp=0;
    int yl=0;
    int reduceHeightBy;
    
    UIInterfaceOrientation orientation;
    if ([[[UIDevice currentDevice] systemVersion]integerValue]>=8) {
        orientation= [UIApplication sharedApplication].statusBarOrientation;
        if  (UIInterfaceOrientationIsLandscape(orientation))
        {
            currentWidthL=[[UIScreen mainScreen] bounds].size.width;
            currentHeightL=[[UIScreen mainScreen] bounds].size.height;
            currentWidthP=[[UIScreen mainScreen] bounds].size.height;
            currentHeightP=[[UIScreen mainScreen] bounds].size.width;
        }
        else
        {
            currentWidthL=[[UIScreen mainScreen] bounds].size.height;
            currentHeightL=[[UIScreen mainScreen] bounds].size.width;
            currentWidthP=[[UIScreen mainScreen] bounds].size.width;
            currentHeightP=[[UIScreen mainScreen] bounds].size.height;
        }
    }else{
        currentHeightL=[[UIScreen mainScreen] bounds].size.width;
        currentWidthP=[[UIScreen mainScreen] bounds].size.width;
        currentWidthL=[[UIScreen mainScreen] bounds].size.height;
        currentHeightP=[[UIScreen mainScreen] bounds].size.height;
        orientation=self.interfaceOrientation;
    }
    
    //To change the status bar color of the App
    NSString *hexColorString = [self.appPropertyDictionary objectForKey:@"statusBarColor"];
    if (hexColorString != nil) {
        //        [[UIApplication sharedApplication] setStatusBarHidden:NO];
        //        [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleDefault];
        //        UIView *statusBar = [[[UIApplication sharedApplication] valueForKey:@"statusBarWindow"] valueForKey:@"statusBar"];
        //        if ([statusBar respondsToSelector:@selector(setBackgroundColor:)]) {
        //            statusBar.backgroundColor = [self colorWithHexString:hexColorString];
        //        }
        [self.view setBackgroundColor: [self colorWithHexString:hexColorString]];
    }
    
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        CGSize screenSize = [[UIScreen mainScreen] bounds].size;
        if (screenSize.height == 812){
            reduceHeightBy = 44;
            
            //To Change the background color of the view as user preferences in iPhone10 device
            NSString *hexColorString = [self.appPropertyDictionary objectForKey:@"statusBarColor"];
            if (hexColorString != nil) {
                [self.view setBackgroundColor: [self colorWithHexString:hexColorString]];
            }
        }else{
            reduceHeightBy = 20;
        }
    }else{
        reduceHeightBy = 20;
    }
    
    currentHeightL=currentHeightL-reduceHeightBy;
    currentHeightP=currentHeightP-reduceHeightBy;
    if ([[[UIDevice currentDevice]systemVersion] integerValue]>=7) {
        yp=reduceHeightBy;
        yl=reduceHeightBy;
    }
    else{
        
    }
    if  (UIInterfaceOrientationIsLandscape(orientation))
    {
        [webView setFrame:CGRectMake(0, yl, currentWidthL, currentHeightL)];
    }
    else
    {
        [webView setFrame:CGRectMake(0, yp, currentWidthP, currentHeightP)];
    }
}

-(UIColor*)colorWithHexString:(NSString*)hex
{
    NSString *cString = [[hex stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] uppercaseString];
    
    // String should be 6 or 8 characters
    if ([cString length] < 6) return [UIColor grayColor];
    
    // strip 0X if it appears
    if ([cString hasPrefix:@"0X"]) cString = [cString substringFromIndex:2];
    
    if ([cString length] != 6) return  [UIColor grayColor];
    
    // Separate into r, g, b substrings
    NSRange range;
    range.location = 0;
    range.length = 2;
    NSString *rString = [cString substringWithRange:range];
    
    range.location = 2;
    NSString *gString = [cString substringWithRange:range];
    
    range.location = 4;
    NSString *bString = [cString substringWithRange:range];
    
    // Scan values
    unsigned int r, g, b;
    [[NSScanner scannerWithString:rString] scanHexInt:&r];
    [[NSScanner scannerWithString:gString] scanHexInt:&g];
    [[NSScanner scannerWithString:bString] scanHexInt:&b];
    
    return [UIColor colorWithRed:((float) r / 255.0f)
                           green:((float) g / 255.0f)
                            blue:((float) b / 255.0f)
                           alpha:1.0f];
}


-(void)initMultiviewPlugin{
    [webView setTag:111] ;
    int currentHeightP=0;
    int currentWidthP=0;
    int currentWidthL=0;
    int currentHeightL=0;
    int yp=0;
    int yl=0;
    int reduceHeightBy;
    
    UIInterfaceOrientation orientation;
    if ([[[UIDevice currentDevice] systemVersion]integerValue]>=8) {
        orientation= [UIApplication sharedApplication].statusBarOrientation;
        if  (UIInterfaceOrientationIsLandscape(orientation))
        {
            currentWidthL=[[UIScreen mainScreen] bounds].size.width;
            currentHeightL=[[UIScreen mainScreen] bounds].size.height;
            currentWidthP=[[UIScreen mainScreen] bounds].size.height;
            currentHeightP=[[UIScreen mainScreen] bounds].size.width;
        }
        else
        {
            currentWidthL=[[UIScreen mainScreen] bounds].size.height;
            currentHeightL=[[UIScreen mainScreen] bounds].size.width;
            currentWidthP=[[UIScreen mainScreen] bounds].size.width;
            currentHeightP=[[UIScreen mainScreen] bounds].size.height;
        }
    }else{
        currentHeightL=[[UIScreen mainScreen] bounds].size.width;
        currentWidthP=[[UIScreen mainScreen] bounds].size.width;
        currentWidthL=[[UIScreen mainScreen] bounds].size.height;
        currentHeightP=[[UIScreen mainScreen] bounds].size.height;
        orientation=self.interfaceOrientation;
    }
    
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        CGSize screenSize = [[UIScreen mainScreen] bounds].size;
        if (screenSize.height == 812){
            reduceHeightBy = 44;
        }else{
            reduceHeightBy = 20;
        }
    }else{
        reduceHeightBy = 20;
    }
    
    //    currentHeightL=currentHeightL-20;
    currentHeightP=currentHeightP-reduceHeightBy;
    yp=reduceHeightBy;
    if([[UIDevice currentDevice]userInterfaceIdiom]==UIUserInterfaceIdiomPad){
        currentHeightL=currentHeightL-reduceHeightBy;
        yl=reduceHeightBy;
    }
    
    NSMutableDictionary *currentWebRecords= [[NSMutableDictionary alloc]initWithObjects:[NSArray arrayWithObjects:@"111",@"",[NSString stringWithFormat:@"%d",currentHeightP],[NSString stringWithFormat:@"%d",currentWidthP],[NSString stringWithFormat:@"%d",currentWidthL],[NSString stringWithFormat:@"%d",currentHeightL],[NSString stringWithFormat:@"0"],[NSString stringWithFormat:@"%d",yp],[NSString stringWithFormat:@"0"],[NSString stringWithFormat:@"%d",yl],nil] forKeys:[NSArray arrayWithObjects:@"tag",@"targetView",@"currentHeightP",@"currentWidthP",@"currentWidthL",@"currentHeightL",@"xp",@"yp",@"xl",@"yl",nil]];
    self.webViewPropDictionary=[[NSMutableArray alloc] init];
    if([self.webViewDictionary count]==0){
        self.webViewDictionary=[[NSMutableDictionary alloc] initWithObjects:[NSArray arrayWithObjects:currentWebRecords,nil] forKeys:[NSArray arrayWithObjects:@"MAIN",nil]];
    }else{
        [self.webViewDictionary setObject:currentWebRecords forKey:@"MAIN"];
    }
    
    if  (UIInterfaceOrientationIsLandscape(orientation))
    {
        [webView setFrame:CGRectMake(0, yl, currentWidthL, currentHeightL)];
    }
    else
    {
        [webView setFrame:CGRectMake(0, yp, currentWidthP, currentHeightP)];
    }
}

-(void)multiviewOpen:(NSDictionary *) result{
    self.targetView= [[result objectForKey:@"targetView"] uppercaseString];
    self.multiViewName = [[result objectForKey:VIEWID] uppercaseString];
    if (![[self.webViewDictionary allKeys] containsObject:self.targetView]){
        NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",TARGETVIEW_NOT_FOUND],nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :returnResultkeys :returnResult]];
        
    }
    else if([[self.webViewDictionary allKeys] containsObject:self.multiViewName]){
        NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-025"],nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :returnResultkeys :returnResult]];
    }
    else{
        CGRect frameCurrentView = CGRectZero;
        NSUInteger childsInWebViewDictionary =[self.webViewDictionary count];
        if(childsInWebViewDictionary<2){
            self.multiviewLocation = [[result objectForKey:@"location"] uppercaseString];
            self.multiViewID=[self.webViewDictionary count]+111;
            NSMutableDictionary *currentWebRecords= [[NSMutableDictionary alloc]initWithObjects:[NSArray arrayWithObjects:[NSString stringWithFormat:@"%ld",(long)self.multiViewID],self.targetView,nil] forKeys:[NSArray arrayWithObjects:@"tag",@"targetView",nil]];
            [self.webViewDictionary setValue:currentWebRecords forKey:[[result objectForKey:VIEWID]uppercaseString]];
            NSDictionary *targetRecords=[self.webViewDictionary objectForKey:self.targetView];
            WKWebView *targetWebview=(WKWebView *)  [self.view viewWithTag:[[targetRecords objectForKey:@"tag"] integerValue]];
            int targetH=targetWebview.frame.size.height;
            int targetW=targetWebview.frame.size.width;
            int currentH;
            int currentW;
            int targetX= targetWebview.frame.origin.x;
            int targetY= targetWebview.frame.origin.y;
            NSInteger parentsHeightP=[[targetRecords objectForKey:@"currentHeightP"] integerValue];
            NSInteger parentsWidthP=[[targetRecords objectForKey:@"currentWidthP"]integerValue];
            NSInteger parentsHeightL=[[targetRecords objectForKey:@"currentHeightL"]integerValue];
            NSInteger parentsWidthL=[[targetRecords objectForKey:@"currentWidthL"]integerValue];
            NSInteger parentsXl=[[targetRecords objectForKey:@"xl"] integerValue];
            NSInteger parentsYl=[[targetRecords objectForKey:@"yl"]integerValue];
            NSInteger parentsXp=[[targetRecords objectForKey:@"xp"]integerValue];
            NSInteger parentsYp=[[targetRecords objectForKey:@"yp"]integerValue];
            NSInteger currentHeightP=0;
            NSInteger currentWidthP=0;
            NSInteger currentWidthL=0;
            NSInteger currentHeightL=0;
            if ([self.multiviewLocation isEqualToString:@"VERTICAL"]) {
                currentHeightP=parentsHeightP;
                currentWidthP=[[result objectForKey:@"percentage"] integerValue]*parentsWidthP*.01;
                currentWidthL=[[result objectForKey:@"percentage"] integerValue]*parentsWidthL*.01;
                currentHeightL=parentsHeightL;
                parentsWidthP=parentsWidthP-currentWidthP;
                parentsWidthL=parentsWidthL-currentWidthL;
                [[self.webViewDictionary objectForKey:self.targetView]setObject:[NSString stringWithFormat:@"%ld",(long)parentsWidthP]forKey:@"currentWidthP"];
                [[self.webViewDictionary objectForKey:self.targetView]setObject:[NSString stringWithFormat:@"%ld",(long)parentsWidthL]forKey:@"currentWidthL"];
                currentW=[[result objectForKey:@"percentage"] integerValue]*targetW*.01;
                currentH=targetH;
                targetW=targetW-currentW;
                frameCurrentView=CGRectMake(targetX+targetW,targetY,currentW,currentH);
                parentsXl=parentsXl+parentsWidthL;
                parentsXp=parentsXp+parentsWidthP;
            }else if ([self.multiviewLocation isEqualToString:@"HORIZONTAL"]){
                currentWidthP=parentsWidthP;
                currentHeightP=[[result objectForKey:@"percentage"] integerValue]*parentsHeightP*.01;
                currentHeightL=[[result objectForKey:@"percentage"] integerValue]*parentsHeightL*.01;
                currentWidthL=parentsWidthL;
                parentsHeightP=parentsHeightP-currentHeightP;
                parentsHeightL=parentsHeightL-currentHeightL;
                [[self.webViewDictionary objectForKey:self.targetView]setObject:[NSString stringWithFormat:@"%ld",(long)parentsHeightP]forKey:@"currentHeightP"];
                [[self.webViewDictionary objectForKey:self.targetView]setObject:[NSString stringWithFormat:@"%ld",(long)parentsHeightL]forKey:@"currentHeightL"];
                parentsYl=parentsYl+parentsHeightL;
                parentsYp=parentsYp+parentsHeightP;
                currentH=[[result objectForKey:@"percentage"] integerValue]*targetH*.01;
                currentW=targetW;
                targetH=targetH-currentH;
                frameCurrentView=CGRectMake(targetX,targetH+targetY,currentW,currentH);
            }
            [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)currentHeightP]forKey:@"currentHeightP"];
            [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)currentWidthP]forKey:@"currentWidthP"];
            [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)currentWidthL]forKey:@"currentWidthL"];
            [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString  stringWithFormat:@"%ld",(long)currentHeightL]forKey:@"currentHeightL"];
            [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)parentsXl]forKey:@"xl"];
            [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)parentsYl]forKey:@"yl"];
            [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)parentsXp]forKey:@"xp"];
            [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)parentsYp]forKey:@"yp"];
            [UIView animateWithDuration:.5 animations:^{
                [targetWebview setFrame:CGRectMake(targetWebview.frame.origin.x,  targetWebview.frame.origin.y, targetW, targetH)];
            }];
            
            WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
            config.preferences = [[WKPreferences alloc] init];
            config.preferences.javaScriptEnabled = YES;
            config.preferences.javaScriptCanOpenWindowsAutomatically = NO;
            self.tempMultiview = [[WKWebView alloc] initWithFrame:frameCurrentView configuration:config];
            NSLog(@"self.multiviewLocation sub--%@---%@",self.multiviewLocation, NSStringFromCGRect(frameCurrentView));
            //            self.tempMultiview.navigationDelegate=self;
            //            [self.tempMultiview.configuration.preferences setValue:@YES forKey:@"allowFileAccessFromFileURLs"];
            [self.tempMultiview setTag:self.multiViewID];
            [self.view addSubview:self.tempMultiview];
            [self.tempMultiview loadRequest:[NSURLRequest requestWithURL: [NSURL URLWithString:[result objectForKey:MULTIVIEW_PAGE]]]];
            NSArray * resultkeys=nil;
            NSArray * resultMsg=nil;
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
        }else{
            NSArray * resultkeys=nil;
            NSArray * resultMsg=nil;
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :resultkeys :resultMsg]];
        }
    }
}

-(void)resizeMultiview:(NSDictionary *) result{
    [self initMultiviewPlugin];
    CGRect frameCurrentView = CGRectZero;
    if([self.webViewDictionary count]==2){
        NSMutableDictionary *currentWebRecords= [[NSMutableDictionary alloc]initWithObjects:[NSArray arrayWithObjects:[NSString stringWithFormat:@"%ld",(long)self.multiViewID],self.targetView,nil] forKeys:[NSArray arrayWithObjects:@"tag",@"targetView",nil]];
        [self.webViewDictionary setValue:currentWebRecords forKey:self.multiViewName];
        NSDictionary *targetRecords=[self.webViewDictionary objectForKey:self.targetView];
        WKWebView *targetWebview=(WKWebView *)  [self.view viewWithTag:[[targetRecords objectForKey:@"tag"] integerValue]];
        int targetH=targetWebview.frame.size.height;
        int targetW=targetWebview.frame.size.width;
        int currentH;
        int currentW;
        int targetX= targetWebview.frame.origin.x;
        int targetY= targetWebview.frame.origin.y;
        NSInteger parentsHeightP=[[targetRecords objectForKey:@"currentHeightP"] integerValue];
        NSInteger parentsWidthP=[[targetRecords objectForKey:@"currentWidthP"]integerValue];
        NSInteger parentsHeightL=[[targetRecords objectForKey:@"currentHeightL"]integerValue];
        NSInteger parentsWidthL=[[targetRecords objectForKey:@"currentWidthL"]integerValue];
        NSInteger parentsXl=[[targetRecords objectForKey:@"xl"] integerValue];
        NSInteger parentsYl=[[targetRecords objectForKey:@"yl"]integerValue];
        NSInteger parentsXp=[[targetRecords objectForKey:@"xp"]integerValue];
        NSInteger parentsYp=[[targetRecords objectForKey:@"yp"]integerValue];
        NSInteger currentHeightP=0;
        NSInteger currentWidthP=0;
        NSInteger currentWidthL=0;
        NSInteger currentHeightL=0;
        if ([self.multiviewLocation isEqualToString:@"VERTICAL"]) {
            currentHeightP=parentsHeightP;
            currentWidthP=[[result objectForKey:@"percentage"] integerValue]*parentsWidthP*.01;
            currentWidthL=[[result objectForKey:@"percentage"] integerValue]*parentsWidthL*.01;
            currentHeightL=parentsHeightL;
            parentsWidthP=parentsWidthP-currentWidthP;
            parentsWidthL=parentsWidthL-currentWidthL;
            [[self.webViewDictionary objectForKey:self.targetView]setObject:[NSString stringWithFormat:@"%ld",(long)parentsWidthP]forKey:@"currentWidthP"];
            [[self.webViewDictionary objectForKey:self.targetView]setObject:[NSString stringWithFormat:@"%ld",(long)parentsWidthL]forKey:@"currentWidthL"];
            currentW=[[result objectForKey:@"percentage"] integerValue]*targetW*.01;
            currentH=targetH;
            targetW=targetW-currentW;
            frameCurrentView=CGRectMake(targetX+targetW,targetY,currentW,currentH);
            parentsXl=parentsXl+parentsWidthL;
            parentsXp=parentsXp+parentsWidthP;
        }else if ([self.multiviewLocation isEqualToString:@"HORIZONTAL"]){
            currentWidthP=parentsWidthP;
            currentHeightP=[[result objectForKey:@"percentage"] integerValue]*parentsHeightP*.01;
            currentHeightL=[[result objectForKey:@"percentage"] integerValue]*parentsHeightL*.01;
            currentWidthL=parentsWidthL;
            parentsHeightP=parentsHeightP-currentHeightP;
            parentsHeightL=parentsHeightL-currentHeightL;
            [[self.webViewDictionary objectForKey:self.targetView]setObject:[NSString stringWithFormat:@"%ld",(long)parentsHeightP]forKey:@"currentHeightP"];
            [[self.webViewDictionary objectForKey:self.targetView]setObject:[NSString stringWithFormat:@"%ld",(long)parentsHeightL]forKey:@"currentHeightL"];
            parentsYl=parentsYl+parentsHeightL;
            parentsYp=parentsYp+parentsHeightP;
            currentH=[[result objectForKey:@"percentage"] integerValue]*targetH*.01;
            currentW=targetW;
            targetH=targetH-currentH;
            frameCurrentView=CGRectMake(targetX,targetH+targetY,currentW,currentH);
        }
        [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)currentHeightP]forKey:@"currentHeightP"];
        [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)currentWidthP]forKey:@"currentWidthP"];
        [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)currentWidthL]forKey:@"currentWidthL"];
        [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)currentHeightL]forKey:@"currentHeightL"];
        [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)parentsXl]forKey:@"xl"];
        [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)parentsYl]forKey:@"yl"];
        [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)parentsXp]forKey:@"xp"];
        [[self.webViewDictionary objectForKey:self.multiViewName]setObject:[NSString stringWithFormat:@"%ld",(long)parentsYp]forKey:@"yp"];
        [UIView animateWithDuration:.5 animations:^{
            [targetWebview setFrame:CGRectMake(targetWebview.frame.origin.x,  targetWebview.frame.origin.y, targetW, targetH)];
        }];
        self.tempMultiview.frame = frameCurrentView;
        [self.view setNeedsDisplay];
        NSArray * resultkeys=nil;
        NSArray * resultMsg=nil;
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
    }
}

-(void)multiviewClose:(NSDictionary *) result{
    if ([[[result objectForKey:VIEWID]uppercaseString]isEqualToString:@"MAIN"]) {
        NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-028"],nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :returnResultkeys :returnResult]];
    }else if(![[self.webViewDictionary allKeys] containsObject:[[result objectForKey:VIEWID]uppercaseString]]){
        NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-028"],nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :returnResultkeys :returnResult]];
    }
    if (![[[result objectForKey:VIEWID]uppercaseString]isEqualToString:@"MAIN"]&&result != nil ){
        BOOL canDelete=YES;
        for (NSString *childViews in [self.webViewDictionary allKeys]) {
            NSString *temp1=[[self.webViewDictionary objectForKey:childViews] objectForKey:@"targetView"];
            NSString *temp2=[[result objectForKey:VIEWID] uppercaseString];
            if ([temp1 isEqualToString:temp2]){
                canDelete=NO;
            }
        }
        if (canDelete) {
            NSDictionary *currentRecords=[self.webViewDictionary objectForKey:[[result objectForKey:VIEWID]uppercaseString]];
            NSDictionary *targetRecords=[self.webViewDictionary objectForKey:[[currentRecords objectForKey:@"targetView"]uppercaseString]];
            NSInteger tagCurrent =[[currentRecords objectForKey:@"tag" ]integerValue];
            NSInteger tagTarget =[[targetRecords objectForKey:@"tag" ]integerValue];
            WKWebView *currentWebview=(WKWebView *)  [self.view viewWithTag:tagCurrent];
            WKWebView *targetWebview=(WKWebView *)  [self.view viewWithTag:tagTarget];
            if (currentWebview&&targetWebview&&[currentWebview isKindOfClass:[WKWebView class]]) {
                if (targetWebview.frame.origin.y==currentWebview.frame.origin.y) {
                    [targetWebview setFrame:CGRectMake(targetWebview.frame.origin.x, targetWebview.frame.origin.y, targetWebview.frame.size.width+currentWebview.frame.size.width, targetWebview.frame.size.height)];
                    NSInteger a=  [[currentRecords objectForKey:@"currentWidthL"] integerValue];
                    NSInteger b=  [[currentRecords objectForKey:@"currentWidthP"] integerValue];
                    NSInteger c=  [[targetRecords objectForKey:@"currentWidthL"] integerValue];
                    NSInteger d=  [[targetRecords objectForKey:@"currentWidthP"] integerValue];
                    c=c+a;
                    d=b+d;
                    [targetRecords setValue:[NSString stringWithFormat:@"%ld",(long)c] forKey:@"currentWidthL"];
                    [targetRecords setValue:[NSString stringWithFormat:@"%ld",(long)d] forKey:@"currentWidthP"] ;
                }else if(targetWebview.frame.origin.x==currentWebview.frame.origin.x){
                    [targetWebview setFrame:CGRectMake(targetWebview.frame.origin.x, targetWebview.frame.origin.y, targetWebview.frame.size.width, targetWebview.frame.size.height+currentWebview.frame.size.height)];
                    NSInteger a=   [[currentRecords objectForKey:@"currentHeightL"] integerValue];
                    NSInteger b=  [[currentRecords objectForKey:@"currentHeightP"] integerValue];
                    NSInteger c=   [[targetRecords objectForKey:@"currentHeightL"] integerValue];
                    NSInteger d=  [[targetRecords objectForKey:@"currentHeightP"] integerValue];
                    c=c+a;
                    d=b+d;
                    [targetRecords setValue:[NSString stringWithFormat:@"%ld",(long)c] forKey:@"currentHeightL"];
                    [targetRecords setValue:[NSString stringWithFormat:@"%ld",(long)d] forKey:@"currentHeightP"] ;
                }
                [currentWebview removeFromSuperview];
                [self.webViewDictionary removeObjectForKey:[[result objectForKey:VIEWID]uppercaseString]];
                NSArray * resultkeys=nil;
                NSArray * resultMsg=nil;
                [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
                [targetWebview setNeedsDisplayInRect:targetWebview.frame];
            }
        }else{
            NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *returnResult=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",@"APZ-CNT-028"],nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[result objectForKey:PLUGINID] :false :false :returnResultkeys :returnResult]];
        }
    }
}

-(void)updateMultiView:(UIInterfaceOrientation)deviceOrientation{
    NSArray *keyArray= [self.webViewDictionary allKeys];
    for (NSString *key in keyArray) {
        NSMutableDictionary *records= [self.webViewDictionary objectForKey:key];
        WKWebView *targetWebview=(WKWebView *)  [self.view viewWithTag:[[records objectForKey:@"tag"] integerValue]];
        CGRect frame = CGRectZero;
        if (UIDeviceOrientationIsLandscape(deviceOrientation)){
            if ([[[UIDevice currentDevice]systemVersion] integerValue]>=7) {
                if([[records objectForKey:VIEWID] isEqualToString:@"MAIN"]){
                    CGFloat height = [[records objectForKey:@"currentHeightL"] integerValue];
                    frame = CGRectMake([[records objectForKey:@"xl"] integerValue], [[records objectForKey:@"yl"] integerValue], [[records objectForKey:@"currentWidthL"] integerValue], height);
                }
                else
                {
                    CGFloat height = [[records objectForKey:@"currentHeightL"] integerValue];
                    frame = CGRectMake([[records objectForKey:@"xl"] integerValue], [[records objectForKey:@"yl"] integerValue], [[records objectForKey:@"currentWidthL"] integerValue], height);
                }
            }
            else
            {
                frame = CGRectMake([[records objectForKey:@"xl"] integerValue], [[records objectForKey:@"yl"] integerValue], [[records objectForKey:@"currentWidthL"] integerValue], [[records objectForKey:@"currentHeightL"] integerValue]);
            }
            [targetWebview setFrame:frame];
        }
        else{
            frame = CGRectMake([[records objectForKey:@"xp"] integerValue], [[records objectForKey:@"yp"] integerValue], [[records objectForKey:@"currentWidthP"] integerValue], [[records objectForKey:@"currentHeightP"] integerValue]);
            [targetWebview setFrame:frame];
        }
    }
}

#pragma mark - WebView Bounce
-(void)disableBounce:(NSDictionary*)results{
    NSUInteger version = [[[UIDevice currentDevice] systemVersion]integerValue];
    if (results==nil) {
        if(version >= 7){
            [[webView scrollView] setBounces:NO];
        }
    }else{
        if ([[results objectForKey:@"disable"]isEqualToString:@"Y"]) {
            if(version >= 7){
                [[webView scrollView] setBounces:NO];
            }
        }else{
            if(version >= 7){
                [[webView scrollView] setBounces:YES];
            }
        }
    }
}
@end
