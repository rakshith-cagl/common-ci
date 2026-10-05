#import "BarCodeViewController.h"
#import "APZBarcode.h"
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"



@interface APZBarcode()<UIImagePickerControllerDelegate,BarCodeScannedDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(strong,nonatomic) NSString *pluginId;
@property(nonatomic,strong)BarCodeViewController *nativeReader;
@property (assign) BOOL rotationFlag;
@end


@implementation APZBarcode

-(id)initPlugin:(WKWebView *)wbView :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZBarcode--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    
    // Barcode scan will support only for Portrait mode
    self.rotationFlag = self.viewController.rotationPluginFlag;
    self.viewController.rotationPluginFlag = NO;
    
    [self stratScanBarcode:jsonDict];
}

-(void)stopPlugin:(NSDictionary *)jsonDict{
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    [self stopScanBarcode];
}

-(void)stratScanBarcode:(NSDictionary *)jsonDict{
    BOOL isCameraPresent = [UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera];
    if (isCameraPresent==YES) {
        if(jsonDict !=nil){
            self.nativeReader = [[BarCodeViewController alloc]init];
            self.nativeReader.delegate = self;
            if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
                self.nativeReader.frame = CGRectMake(0,self.viewController.view.bounds.size.height/2 -self.viewController.view.bounds.size.width/2,self.viewController.view.bounds.size.width, self.viewController.view.bounds.size.height * 0.6);
            }else{
                self.nativeReader.frame = CGRectMake(0,self.viewController.view.bounds.size.height/2 -self.viewController.view.bounds.size.width/2,self.viewController.view.bounds.size.width, self.viewController.view.bounds.size.width);
            }
            self.nativeReader.center = CGPointMake(self.viewController.view.frame.size.width  / 2,
                                                   self.viewController.view.frame.size.height / 2);
            self.nativeReader.barCodeScanView = self.nativeReader;
            [self.viewController.view addSubview:self.nativeReader];
            [self.nativeReader startNativeScanner];
        }
        else{
            [self cleanPlugin];
        }
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMess=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",CAMERA_NOTFOUND],nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMess]];
        [Logger logger_Log:@"E" :@"APZBarcode--Device does not have Camera to Scan"];
        [self cleanPlugin];
    }
}

-(void)stopScanBarcode{
    
    if(self.nativeReader != nil){
        [self.nativeReader stopNativeScanner];
        [self.nativeReader removeFromSuperview];
        self.nativeReader = nil;
        NSArray *resultkeys=[NSArray arrayWithObjects:CBTEXT,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:@"Camera closed",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :returnResult]];
        [self cleanPlugin];
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:@"errorMessage",nil];
        NSArray *resultMess=[NSArray arrayWithObjects:@"Failed to close the camera.",nil];
        [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMess]];
        [self cleanPlugin];
    }
}

-(void)callbackForIos7:(UIInterfaceOrientation)interfaceOrienation{
    NSArray *resultkeys=nil;
    NSArray *resultMess=nil;
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMess]];
    [Logger logger_Log:@"E" :@"APZBarcode--Scanner Cancelled"];
    [self cleanPlugin:interfaceOrienation];
    
}

#pragma mark - Barcode Scanned Native Scanner
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= 70000
-(void)scannedBarCode:(NSString *)barcode{
    NSArray *resultkeys=[NSArray arrayWithObjects:CBTEXT,nil];
    NSArray *returnResult=[NSArray arrayWithObjects:barcode,nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :returnResult]];
    [Logger logger_Log:@"I" :@"APZBarcode--Scanner Success"];
}
#endif


#pragma mark -Plugin Clean
-(void)cleanPlugin:(UIInterfaceOrientation)interfaceOrientation{
    
    // Barcode scan will support only for Portrait mode
    self.viewController.rotationPluginFlag = self.rotationFlag;
    
    [Logger logger_Log:@"I" :@"APZBarcode--Done"];
    self.webView = nil;
    [self.delegate donePluginWithOrientaion:self :interfaceOrientation];
    self.delegate = nil;
    
}
-(void)cleanPlugin{
    // Barcode scan will support only for Portrait mode
    self.viewController.rotationPluginFlag = self.rotationFlag;
    
    [Logger logger_Log:@"I" :@"APZBarcode--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
    self.delegate = nil;
    self.viewController = nil;
}


@end

