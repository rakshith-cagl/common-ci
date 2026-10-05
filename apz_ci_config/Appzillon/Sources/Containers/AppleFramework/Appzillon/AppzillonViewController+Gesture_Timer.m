//
//  AppzillonViewController+Gesture_Timer.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 29/07/16.
//
//

#import "AppzillonViewController+Gesture_Timer.h"

@implementation AppzillonViewController (Gesture)
#pragma mark - Gesture Support Handler
-(void)startGesture:(NSDictionary*)result webView:(WKWebView*)webView :(NSString*)callee{
    NSString *SINGLETAP = [result objectForKey:@"singletap"];
    NSString *DOUBLETAP = [result objectForKey:@"doubletap"];
    NSString *TRIPLETAP = [result objectForKey:@"tripletap"];
    NSString *PINCH = [result objectForKey:@"pinch"];
    NSString *LONGPRESS = [result objectForKey:@"longpress"];
    NSString *SWIPE = [result objectForKey:@"swipe"];
    if ([callee isEqualToString:@"Timer"]) {
        self.gestCalleeTimer=YES;
    }else{
        self.calleeGest=YES;
    }
    if ([SINGLETAP isEqualToString:@"Y"]) {
        if ([callee isEqualToString:@"gesture"]) {
            self.STCalleeGest=YES;
        }
        if (!self.singleTap) {
            [webView setUserInteractionEnabled:YES];
            self.singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(singleTapHandler:)];
            self.singleTap.delegate = self;
            self.singleTap.numberOfTapsRequired = 1;
            [webView addGestureRecognizer:self.singleTap];
            [webView.scrollView setBounces:NO];
        }
        
    }
    if ([DOUBLETAP isEqualToString:@"Y"]) {
        if ([callee isEqualToString:@"gesture"]) {
            self.DTCalleeGest=YES;
        }
        if (!self.doubleTap) {
            [webView setUserInteractionEnabled:YES];
            self.doubleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(doubleTapHandler:)];
            self.doubleTap.delegate = self;
            self.doubleTap.numberOfTapsRequired = 2;
            [webView addGestureRecognizer:self.doubleTap];
            [self.singleTap requireGestureRecognizerToFail:self.doubleTap];
            [webView.scrollView setBounces:NO];
        }
    }
    if ([TRIPLETAP isEqualToString:@"Y"]) {
        if ([callee isEqualToString:@"gesture"]) {
            self.TTCalleeGest=YES;
        }
        if (!self.tripleTap) {
            [webView setUserInteractionEnabled:YES];
            self.tripleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tripleTapHandler:)];
            self.tripleTap.delegate = self;
            self.tripleTap.numberOfTapsRequired = 3;
            [webView addGestureRecognizer:self.tripleTap];
            [self.singleTap requireGestureRecognizerToFail:self.tripleTap];
            [self.doubleTap requireGestureRecognizerToFail:self.tripleTap];
            [webView.scrollView setBounces:NO];
        }
    }
    if ([PINCH isEqualToString:@"Y"]) {
        if ([callee isEqualToString:@"gesture"]) {
            self.PNCalleeGest=YES;
        }
        if (!self.pinch) {
            self.pinch = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(pinchHandler:)];
            [webView addGestureRecognizer:self.pinch];
            [webView.scrollView setBounces:NO];
        }
    }
    if ([LONGPRESS isEqualToString:@"Y"]) {
        if ([callee isEqualToString:@"gesture"]) {
            self.LPCalleeGest=YES;
        }
        if (!self.longPress) {
            self.longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressHandler:)];
            [webView addGestureRecognizer:self.longPress];
            [self.singleTap requireGestureRecognizerToFail:self.longPress];
            [webView.scrollView setBounces:NO];
        }
    }
    if ([SWIPE isEqualToString:@"Y"]) {
        //        if ([callee isEqualToString:@"gesture"]) {
        //            self.SWCalleeGest=YES;
        //        }
        //        if (!self.leftSwipe) {
        //            self.leftSwipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(leftSwipeHandler:)];
        //            self.rightSwipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(rightSwipeHandler:)];
        //            self.upSwipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(upSwipeHandler:)];
        //            self.downSwipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(downSwipeHandler:)];
        //            [self.leftSwipe setDirection:UISwipeGestureRecognizerDirectionLeft];
        //            [self.rightSwipe setDirection:UISwipeGestureRecognizerDirectionRight];
        //            [self.upSwipe setDirection:UISwipeGestureRecognizerDirectionUp];
        //            [self.downSwipe setDirection:UISwipeGestureRecognizerDirectionDown];
        //            [webView addGestureRecognizer:self.leftSwipe];
        //            [webView addGestureRecognizer:self.rightSwipe];
        //            [webView addGestureRecognizer:self.upSwipe];
        //            [webView addGestureRecognizer:self.downSwipe];
        //            [webView.scrollView.panGestureRecognizer requireGestureRecognizerToFail:self.leftSwipe];
        //            [webView.scrollView.panGestureRecognizer requireGestureRecognizerToFail:self.rightSwipe];
        //            [webView.scrollView.panGestureRecognizer requireGestureRecognizerToFail:self.upSwipe];
        //            [webView.scrollView.panGestureRecognizer requireGestureRecognizerToFail:self.downSwipe];
        //            [webView.scrollView setBounces:NO];
        //            self.swipeEnabled=YES;
        //        }
        if ([callee isEqualToString:@"gesture"]) {
            self.SWCalleeGest=YES;
            webView.scrollView.panGestureRecognizer.cancelsTouchesInView = NO;
            self.upSwipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(upSwipeHandler:)];
            self.downSwipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(downSwipeHandler:)];
            self.upSwipe.direction = UISwipeGestureRecognizerDirectionUp;
            self.upSwipe.cancelsTouchesInView = NO;
            self.upSwipe.delegate = self;
            [webView.scrollView addGestureRecognizer:self.upSwipe];
            self.downSwipe.direction = UISwipeGestureRecognizerDirectionDown;
            self.downSwipe.cancelsTouchesInView = NO;
            self.downSwipe.delegate = self;
            [webView.scrollView addGestureRecognizer:self.downSwipe];
        }
        //        if (!self.leftSwipe) {
        self.leftSwipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(leftSwipeHandler:)];
        self.rightSwipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(rightSwipeHandler:)];
        webView.scrollView.panGestureRecognizer.cancelsTouchesInView = NO;
        self.leftSwipe.direction = UISwipeGestureRecognizerDirectionLeft;
        self.leftSwipe.cancelsTouchesInView = NO;
        self.leftSwipe.delegate = self;
        [webView.scrollView addGestureRecognizer:self.leftSwipe];
        self.rightSwipe.direction = UISwipeGestureRecognizerDirectionRight;
        self.rightSwipe.cancelsTouchesInView = NO;
        self.rightSwipe.delegate = self;
        [webView.scrollView addGestureRecognizer:self.rightSwipe];
        [webView.scrollView setBounces:NO];
        
        //        }
        self.swipeEnabled=YES;
    }
}
-(void)stopGesture:(NSDictionary*)result webView:(WKWebView*)webView :(NSString *)callee{
    NSString *SINGLETAP = [result objectForKey:@"singletap"];
    NSString *DOUBLETAP = [result objectForKey:@"doubletap"];
    NSString *TRIPLETAP = [result objectForKey:@"tripletap"];
    NSString *PINCH = [result objectForKey:@"pinch"];
    NSString *LONGPRESS = [result objectForKey:@"longpress"];
    NSString *SWIPE = [result objectForKey:@"swipe"];
    if ([callee isEqualToString:@"gesture"]) {
        self.STCalleeGest=NO;
        self.DTCalleeGest=NO;
        self.TTCalleeGest=NO;
        self.PNCalleeGest=NO;
        self.LPCalleeGest=NO;
        self.SWCalleeGest=NO;
        self.calleeGest=NO;
    }
    if (!self.gestCalleeTimer && !self.calleeGest) {
        [webView.scrollView setBounces:NO];
        if (SINGLETAP == nil) {
            if (self.singleTap) {
                [webView removeGestureRecognizer:self.singleTap];
                self.singleTap=nil;
            }
        }
        if (DOUBLETAP == nil) {
            if (self.doubleTap) {
                [webView removeGestureRecognizer:self.doubleTap];
                self.doubleTap=nil;
            }
        }
        if (TRIPLETAP == nil) {
            if (self.tripleTap) {
                [webView removeGestureRecognizer:self.tripleTap];
                self.tripleTap=nil;
            }
        }
        if (PINCH == nil) {
            if (self.pinch) {
                [webView removeGestureRecognizer:self.pinch];
                self.pinch=nil;
            }
            
        }
        if (LONGPRESS == nil) {
            if (self.longPress) {
                [webView removeGestureRecognizer:self.longPress];
                self.longPress=nil;
            }
        }
        if (SWIPE == nil) {
            if (self.swipeEnabled) {
                [webView removeGestureRecognizer:self.leftSwipe];
                [webView removeGestureRecognizer:self.rightSwipe];
                [webView removeGestureRecognizer:self.upSwipe];
                [webView removeGestureRecognizer:self.downSwipe];
                self.leftSwipe=nil;
                self.rightSwipe=nil;
                self.upSwipe=nil;
                self.downSwipe=nil;
                self.swipeEnabled=NO;
            }
        }
    }
    if (self.calleeGest) {
        NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
        NSArray *returnMsg=[NSArray arrayWithObjects:@"stopped",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputGestJSON objectForKey:PLUGINID] :true :false :resultkeys :returnMsg]];
    }
}
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}
-(void)longPressHandler:(UILongPressGestureRecognizer *)sender{
    if (sender.state == UIGestureRecognizerStateBegan) {
        if (self.LPCalleeGest) {
            NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:@"longPress",nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputGestJSON objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
        }
        if (self.gestCalleeTimer){
            [self resetIdleTimer];
        }
    }
}
-(void)pinchHandler:(UIPinchGestureRecognizer *)sender{
    if (sender.state == UIGestureRecognizerStateRecognized) {
        if (self.PNCalleeGest) {
            NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:@"pinch",nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputGestJSON objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
        }
        if (self.gestCalleeTimer){
            [self resetIdleTimer];
        }
    }
}
-(void)leftSwipeHandler:(UISwipeGestureRecognizer *)sender{
    if (self.SWCalleeGest) {
        NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:@"swipeLeft",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputGestJSON objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
    }
    if (self.gestCalleeTimer){
        [self resetIdleTimer];
    }
}
-(void)rightSwipeHandler:(UISwipeGestureRecognizer *)sender{
    if (self.SWCalleeGest) {
        NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:@"swipeRight",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputGestJSON objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
    }
    if (self.gestCalleeTimer){
        [self resetIdleTimer];
    }
}
-(void)upSwipeHandler:(UISwipeGestureRecognizer *)sender{
    if (self.SWCalleeGest) {
        NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:@"swipeUp",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputGestJSON objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
    }
    if (self.gestCalleeTimer){
        [self resetIdleTimer];
    }
}
-(void)downSwipeHandler:(UISwipeGestureRecognizer *)sender{
    if (self.SWCalleeGest) {
        NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:@"swipeDown",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputGestJSON objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
    }
    if (self.gestCalleeTimer){
        [self resetIdleTimer];
    }
}
-(void)singleTapHandler:(UITapGestureRecognizer *)sender{
    if (sender.state == UIGestureRecognizerStateRecognized) {
        if (self.STCalleeGest) {
            NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:@"singleTap",nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputGestJSON objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
        }
        if (self.gestCalleeTimer){
            [self resetIdleTimer];
        }
    }
}
-(void)doubleTapHandler:(UITapGestureRecognizer *)sender{
    if (sender.state == UIGestureRecognizerStateRecognized) {
        if (self.DTCalleeGest) {
            NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:@"doubleTap",nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputGestJSON objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
        }
        if (self.gestCalleeTimer){
            [self resetIdleTimer];
        }
    }
}
-(void)tripleTapHandler:(UITapGestureRecognizer *)sender{
    if (sender.state == UIGestureRecognizerStateRecognized) {
        if (self.TTCalleeGest) {
            NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:@"tripleTap",nil];
            [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputGestJSON objectForKey:PLUGINID] :true :true :resultkeys :resultMsg]];
        }
        if (self.gestCalleeTimer){
            [self resetIdleTimer];
        }
    }
}
#pragma mark Handling idle timeout
- (void)resetIdleTimer {
    if (!self.idleTimer &&self.calledFromPlugin ==NO) {
        self.idleTimer = [NSTimer scheduledTimerWithTimeInterval:self.appIdleMaxTime
                                                          target:self
                                                        selector:@selector(idleTimerExceeded)
                                                        userInfo:nil
                                                         repeats:NO ];
        self.calledFromPlugin=YES;
        NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
        NSArray *returnMsg=[NSArray arrayWithObjects:@"started",nil];
        [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputTimerJSON objectForKey:PLUGINID] :true :true :resultkeys :returnMsg]];
    }
    else {
        if (fabs([self.idleTimer.fireDate timeIntervalSinceNow]) < self.appIdleMaxTime-1.0) {
            [self.idleTimer setFireDate:[NSDate dateWithTimeIntervalSinceNow:self.appIdleMaxTime]];
        }
    }
}

- (void)idleTimerExceeded {
    self.idleTimer = nil;
    self.timerStop=YES;
    self.gestCalleeTimer=NO;
    [self stopGesture:self.inputTimerJSON webView:webView :@"Timer"];
    NSArray *resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
    NSArray *resultMsg=[NSArray arrayWithObjects:@"timerExceeds",nil];
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[self.inputTimerJSON objectForKey:PLUGINID] :true :false :resultkeys :resultMsg]];
    self.inputTimerJSON=nil;
    
}
@end

