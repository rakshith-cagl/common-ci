//
//  APZSignatureViewController.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 10/10/14.
//
//

#import "APZSignatureViewController.h"
#import "APZSignatureView.h"
#import "Base64.h"
#import "Logger.h"

@interface APZSignatureViewController ()
@property(strong,nonatomic)APZSignatureView *singatureViewOnly;
@property(strong,nonatomic)UIBezierPath *path;
@end

BOOL EmptyView;
UIBarButtonItem *saveBarButtonItem;
UIBarButtonItem *refreshBarButtonItem;

@implementation APZSignatureViewController
- (void)viewDidLoad {
    [super viewDidLoad];
    EmptyView=YES;
    UIBarButtonItem *editBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancel)];
    
    saveBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemSave target:self action:@selector(save)];
    
    refreshBarButtonItem= [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    
    self.navigationItem.rightBarButtonItems = [[NSArray alloc] initWithObjects:editBarButtonItem, saveBarButtonItem ,refreshBarButtonItem, nil];
    
    self.navigationItem.rightBarButtonItems = [[NSArray alloc] initWithObjects:editBarButtonItem, saveBarButtonItem ,refreshBarButtonItem, nil];
    saveBarButtonItem.enabled=NO;
    refreshBarButtonItem.enabled=NO;
    self.singatureViewOnly=[[APZSignatureView alloc]initWithFrame:CGRectMake(0, 0,self.view.frame.size.width, self.view.frame.size.height)];
    self.singatureViewOnly.backgroundColor=[UIColor whiteColor];
    [self.singatureViewOnly.window makeKeyAndVisible ];
    [self.view addSubview:self.singatureViewOnly];
    self.path=[[UIBezierPath alloc]init];
}

-(void)cancel{
    [self dismissViewControllerAnimated:YES completion:^{
        [Logger logger_Log:@"I" :@"SignaturePad Cancelled"];
        [self.delegate cancelSignaturePad :self.interfaceOrientation];
    }];
}
-(void )save{
    if(EmptyView ==NO){
        UIImage *snapedImage=[self screenshot];
        NSString *encodedSignature;
        if([[[UIDevice currentDevice] systemVersion] floatValue]>=7.0){
            encodedSignature=[UIImagePNGRepresentation(snapedImage) base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
        }else{
            NSData *dataImg = [NSData dataWithData:UIImageJPEGRepresentation(snapedImage, 0.1f)];
            NSUInteger len = [dataImg length];
            Byte *byteData= (Byte*)malloc(len);
            [dataImg getBytes:byteData length:len];
            [Base64 initialize];
            encodedSignature = [Base64 encode:dataImg];
        }
        [self dismissViewControllerAnimated:YES completion:^{
            [self.delegate callback:encodedSignature :self.interfaceOrientation];
        }];
        
    }
}
-(void)refresh{
    EmptyView=YES;
    saveBarButtonItem.enabled=NO;
    refreshBarButtonItem.enabled=NO;
    self.singatureViewOnly=nil;
    self.path=nil;
    [self.singatureViewOnly removeFromSuperview];
    self.singatureViewOnly=[[APZSignatureView alloc]initWithFrame:CGRectMake(0, 0,self.view.frame.size.width, self.view.frame.size.height)];
    self.singatureViewOnly.backgroundColor=[UIColor whiteColor];
    self.path=[[UIBezierPath alloc]init];
    [self.view addSubview:self.singatureViewOnly];
    [Logger logger_Log:@"I" :@"SignaturePad Refreshed"];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    EmptyView=NO;
    refreshBarButtonItem.enabled=YES;
    saveBarButtonItem.enabled=YES;
    UITouch *touch = [touches anyObject];
    CGPoint p = [touch locationInView:self.view];
    self.singatureViewOnly.pathView=_path;
    [self.singatureViewOnly.pathView moveToPoint:p];
    [self.singatureViewOnly setNeedsDisplay];
}
- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    EmptyView=NO;
    saveBarButtonItem.enabled=YES;
    refreshBarButtonItem.enabled=YES;
    UITouch *touch = [touches anyObject];
    CGPoint p = [touch locationInView:self.view];
    self.singatureViewOnly.pathView=_path;
    [self.singatureViewOnly.pathView addLineToPoint:p];
    [self.singatureViewOnly setNeedsDisplay];
}
- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    [self touchesMoved:touches withEvent:event];
    [self.singatureViewOnly setNeedsDisplay];
    EmptyView =NO;
    saveBarButtonItem.enabled=YES;
    refreshBarButtonItem.enabled=YES;
}

-(UIImage *) screenshot
{
    CGRect rect;
    rect=CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context=UIGraphicsGetCurrentContext();
    [self.view.layer renderInContext:context];
    UIImage *image=UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    [Logger logger_Log:@"I" :@"Signature Captured"];
    return image;
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

-(void) willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    self.singatureViewOnly.frame=CGRectMake(0, 0,self.view.frame.size.width, self.view.frame.size.height);
    self.singatureViewOnly.backgroundColor=[UIColor whiteColor];
    [[UIColor blackColor] setStroke];
    [self.singatureViewOnly.pathView stroke];
    [self.singatureViewOnly setNeedsDisplay];
}
-(void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator
{
    self.singatureViewOnly.frame=CGRectMake(0, 0,size.width, size.height);
    self.singatureViewOnly.backgroundColor=[UIColor whiteColor];
    [[UIColor blackColor] setStroke];
    [self.singatureViewOnly.pathView stroke];
    [self.singatureViewOnly setNeedsDisplay];
}
@end
