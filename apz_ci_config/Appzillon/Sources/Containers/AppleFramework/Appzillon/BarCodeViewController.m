//
//  BarCodeViewController.m
//  Appzillon
//
//  Created by Admin on 05/11/13.
//
//
/*
 BarCodeViewController - Native ios 7 Barcode Scanner
 */


#import "BarCodeViewController.h"
#import <AVFoundation/AVFoundation.h>

@interface BarCodeViewController ()<AVCaptureMetadataOutputObjectsDelegate>
@property (nonatomic, strong) CAShapeLayer *boundingBoxLayer;
@property (nonatomic, strong) UIButton *flashButton;
@end

@implementation BarCodeViewController{
    AVCaptureSession *_captureSession;
    AVCaptureDevice *_videoDevice;
    AVCaptureDeviceInput *_videoInput;
    AVCaptureMetadataOutput *_metadataOutput;
    AVCaptureVideoPreviewLayer *_previewLayer;
    BOOL _running;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

#pragma mark - Native BarCode Scanner
-(void)startNativeScanner{
    _flashButton = [[UIButton alloc]initWithFrame:CGRectMake(self.barCodeScanView.frame.size.width/2-17,10,35, 35)];
    [_flashButton setImage:[UIImage imageNamed:@"BarcodeFlashImages/flashOn.png"]  forState:UIControlStateNormal];
    _flashButton.userInteractionEnabled = true;
    [_flashButton addTarget:self action:@selector(turnOnFlash:) forControlEvents:UIControlEventTouchUpInside];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(restartNativeScanner) name:UIApplicationDidEnterBackgroundNotification object:nil];
    _captureSession = [[AVCaptureSession alloc] init];
    _videoDevice = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    NSError *error = nil;
    _videoInput = [AVCaptureDeviceInput deviceInputWithDevice:_videoDevice error:&error];
    if (_videoInput) {
        [_captureSession addInput:_videoInput];
    } else {
        NSLog(@"Error: %@", error);
    }
    _metadataOutput = [[AVCaptureMetadataOutput alloc] init];
    [_metadataOutput setMetadataObjectsDelegate:self queue:dispatch_get_main_queue()];
    [_captureSession addOutput:_metadataOutput];
    _metadataOutput.metadataObjectTypes = [_metadataOutput availableMetadataObjectTypes];
    _previewLayer = [AVCaptureVideoPreviewLayer layerWithSession:_captureSession];
    _previewLayer.frame = _barCodeScanView.bounds;
    _previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill;
    [_barCodeScanView.layer addSublayer:_previewLayer];
    self.boundingBoxLayer = [CAShapeLayer new];
    NSInteger width = _barCodeScanView.bounds.size.width-100;
    NSInteger height = _barCodeScanView.bounds.size.height-100;
    CGRect box = CGRectMake(_barCodeScanView.bounds.size.width/2 -width/2, _barCodeScanView.bounds.size.height/2 -height/2,width,height);
    UIBezierPath *boundingBoxPath =[UIBezierPath bezierPathWithRect:box];
    self.boundingBoxLayer.path = boundingBoxPath.CGPath;
    self.boundingBoxLayer.lineWidth = 2.0f;
    self.boundingBoxLayer.strokeColor = [UIColor whiteColor].CGColor;
    self.boundingBoxLayer.fillColor = [UIColor clearColor].CGColor;
    [_barCodeScanView.layer addSublayer:self.boundingBoxLayer];
    [_barCodeScanView addSubview:_flashButton];
    [_captureSession startRunning];
    _running = YES;
}

- (void)turnOnFlash:(UIButton*)sender{
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    if([device hasFlash]){
        if(![sender isSelected]){
            [(UIButton *)sender setSelected:true];
            [_flashButton setImage:[UIImage imageNamed:@"BarcodeFlashImages/flashOff.png"]  forState:UIControlStateSelected];
            Class captureDeviceClass = NSClassFromString(@"AVCaptureDevice");
            if (captureDeviceClass != nil) {
                [device lockForConfiguration:nil];
                [device setTorchMode:AVCaptureTorchModeOn];
                [device unlockForConfiguration];
            }
        } else{
            [(UIButton *)sender setSelected:false];
            [_flashButton setImage:[UIImage imageNamed:@"BarcodeFlashImages/flashOn.png"]  forState:UIControlStateNormal];
            Class captureDeviceClass = NSClassFromString(@"AVCaptureDevice");
            if (captureDeviceClass != nil) {
                [device lockForConfiguration:nil];
                [device setTorchMode:AVCaptureTorchModeOff];
                [device unlockForConfiguration];
            }
        }
    }
}

- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputMetadataObjects:(NSArray *)metadataObjects fromConnection:(AVCaptureConnection *)connection
{
    double delayInSeconds = 1.5;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        NSString *detectionString;
        AVMetadataMachineReadableCodeObject *barCodeObject;
        CGRect highlightViewRect = CGRectZero;
        
        NSArray *barCodeTypes = @[AVMetadataObjectTypeUPCECode, AVMetadataObjectTypeCode39Code,AVMetadataObjectTypeCode39Mod43Code,AVMetadataObjectTypeEAN13Code,AVMetadataObjectTypeEAN8Code , AVMetadataObjectTypeCode93Code,AVMetadataObjectTypeCode128Code,AVMetadataObjectTypePDF417Code , AVMetadataObjectTypeQRCode,AVMetadataObjectTypeAztecCode,AVMetadataObjectTypeInterleaved2of5Code,AVMetadataObjectTypeITF14Code,AVMetadataObjectTypeDataMatrixCode];
        for (AVMetadataObject *metadata in metadataObjects) {
            if(![[metadata type] isEqual:AVMetadataObjectTypeFace]){
                for (NSString *type in barCodeTypes) {
                    if ([metadata.type isEqualToString:type])
                    {
                        if (metadata!=nil) {
                            barCodeObject = (AVMetadataMachineReadableCodeObject *)[_previewLayer transformedMetadataObjectForMetadataObject:(AVMetadataMachineReadableCodeObject *)metadata];
                            highlightViewRect = barCodeObject.bounds;
                            detectionString = [(AVMetadataMachineReadableCodeObject *)metadata stringValue];
                            break;
                        }
                    }
                }
                if (detectionString != nil)
                {
                    NSLog(@"--Barcode Detected--");
                    [self sendBackBarcodeData:detectionString];
                    break;
                }
                //            else{
                //                detectionString = @"Unable To Detect Barcode";
                //                [self sendBackBarcodeData:detectionString];
                //            }
            }
        }
    });
}
-(void)sendBackBarcodeData:(NSString*)detectionString{
    [self.delegate scannedBarCode:detectionString];
    self.delegate = nil;
}
-(void) willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    _previewLayer.frame = _barCodeScanView.bounds;
    if ([_previewLayer.connection isVideoOrientationSupported])
    {
        if (toInterfaceOrientation==UIInterfaceOrientationLandscapeLeft) {
            [_previewLayer.connection setVideoOrientation:AVCaptureVideoOrientationLandscapeLeft];
        }else if(toInterfaceOrientation==UIInterfaceOrientationLandscapeRight){
            [_previewLayer.connection setVideoOrientation:AVCaptureVideoOrientationLandscapeRight];
        }else if(toInterfaceOrientation==UIInterfaceOrientationPortraitUpsideDown){
            [_previewLayer.connection setVideoOrientation:AVCaptureVideoOrientationPortraitUpsideDown];
        }else{
            [_previewLayer.connection setVideoOrientation:AVCaptureVideoOrientationPortrait];
        }
    }
    
    NSInteger width = _barCodeScanView.bounds.size.width-100;
    NSInteger height = _barCodeScanView.bounds.size.height-100;
    CGRect box = CGRectMake(_barCodeScanView.bounds.size.width/2 -width/2, _barCodeScanView.bounds.size.height/2 -height/2,width,height);
    UIBezierPath *boundingBoxPath =[UIBezierPath bezierPathWithRect:box];
    self.boundingBoxLayer.path = boundingBoxPath.CGPath;
    self.boundingBoxLayer.lineWidth = 2.0f;
    self.boundingBoxLayer.strokeColor = [UIColor greenColor].CGColor;
    self.boundingBoxLayer.fillColor =[UIColor clearColor].CGColor;
}
-(void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator
{
    UIInterfaceOrientation toInterfaceOrientation   = (UIInterfaceOrientation)[[UIDevice currentDevice] orientation];
    CGRect toRect={ {0., 0.}, size };
    _previewLayer.frame=toRect;
    if ([_previewLayer.connection isVideoOrientationSupported])
    {
        if (toInterfaceOrientation==UIInterfaceOrientationLandscapeLeft) {
            [_previewLayer.connection setVideoOrientation:AVCaptureVideoOrientationLandscapeLeft];
        }else if(toInterfaceOrientation==UIInterfaceOrientationLandscapeRight){
            [_previewLayer.connection setVideoOrientation:AVCaptureVideoOrientationLandscapeRight];
        }else if(toInterfaceOrientation==UIInterfaceOrientationPortraitUpsideDown){
            [_previewLayer.connection setVideoOrientation:AVCaptureVideoOrientationPortraitUpsideDown];
        }else{
            [_previewLayer.connection setVideoOrientation:AVCaptureVideoOrientationPortrait];
        }
    }
    NSInteger width = size.width-100;
    NSInteger height = size.height-100;
    CGRect box = CGRectMake(size.width/2 -width/2, size.height/2 -height/2,width,height);
    UIBezierPath *boundingBoxPath =[UIBezierPath bezierPathWithRect:box];
    self.boundingBoxLayer.path = boundingBoxPath.CGPath;
    self.boundingBoxLayer.lineWidth = 2.0f;
    self.boundingBoxLayer.strokeColor = [UIColor greenColor].CGColor;
    self.boundingBoxLayer.fillColor = [UIColor clearColor].CGColor;
}

-(void)restartNativeScanner{
    if(self.barCodeScanView != nil){
        [[NSNotificationCenter defaultCenter] removeObserver:self];
        [self startNativeScanner];
    }
}

-(void)stopNativeScanner{
    [_captureSession stopRunning];
    _captureSession = nil;
    _videoDevice = nil;
    _videoInput = nil;
    _metadataOutput = nil;
    _previewLayer  = nil;
    _running = NO;
    self.delegate = nil;
    self.barCodeScanView = nil;
    self.boundingBoxLayer = nil;
}

@end


