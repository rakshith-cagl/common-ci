//
//  BarCodeViewController.h
//  Appzillon
//
//  Created by Admin on 05/11/13.
//
//



/*
        BarCodeViewController - Native ios 7 Barcode Scanner
 */


#import <UIKit/UIKit.h>

@protocol BarCodeScannedDelegate <NSObject>
-(void)scannedBarCode:(NSString *)barcode;
-(void)callbackForIos7:(UIInterfaceOrientation)interfaceOrienation;
@end


@interface BarCodeViewController : UIView
@property (nonatomic, weak) BarCodeViewController *barCodeScanView;
@property(assign,nonatomic)id<BarCodeScannedDelegate>delegate;
-(void)startNativeScanner;
-(void)stopNativeScanner;
@end


