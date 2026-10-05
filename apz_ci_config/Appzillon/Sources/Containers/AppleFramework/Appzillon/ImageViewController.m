//
//  ImageViewController.m
//  Appzillon
//
//  Created by Manu on 24/07/18.
//
//


#import "ImageViewController.h"
#import "Logger.h"
#import "NLImageCropperView.h"
#import "Constants.h"
#import "MiscellaneousMethods.h"



@interface ImageViewController () <UIScrollViewDelegate>
@property(nonatomic,strong)UIImageView *imageView;
@property(nonatomic,strong)NSString *imagePath;
@property(nonatomic,strong)NSDictionary *jsonDict;
@property (nonatomic)BOOL customCancelButton;
@property (nonatomic, strong) UIScrollView *scrollView;
@property (strong,nonatomic) NLImageCropperView* imageCropper;
@end

@implementation ImageViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.title = @"Image";
    
    if (self.customCancelButton) {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelImageView)];
    }
    
    if([[self.jsonDict objectForKey:@"crop"] isEqualToString:@"Y"] || [[self.jsonDict objectForKey:@"crop"] isEqualToString:@"y"]){
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Crop" style:UIBarButtonItemStylePlain target:self action:@selector(cropMethod)];
        UIImage *image = [UIImage imageWithContentsOfFile:self.imagePath];
        _imageCropper = [[NLImageCropperView alloc] initWithFrame:CGRectMake(0, 64, self.view.frame.size.width, self.view.frame.size.height)];
        [_imageCropper setImage:image];
        CGRect box = CGRectMake(_imageCropper.bounds.size.width/2 -120.0, _imageCropper.bounds.size.height/2 -120.0,240.0, 240.0);
        [_imageCropper setCropRegionRect:box];
        _imageCropper.cropBox=[self.jsonDict objectForKey:CROPBOXSHAPE];
        [self.view addSubview:_imageCropper];
    }else{
        self.scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
        self.imageView = [[UIImageView alloc] initWithImage:[UIImage imageWithContentsOfFile:self.imagePath]];
        self.scrollView.maximumZoomScale = 1;
        self.scrollView.minimumZoomScale = 2;
        self.scrollView.clipsToBounds = YES;
        self.scrollView.delegate = self;
        
        //to check the device is in landscape mode
        if (self.view.frame.size.height < self.view.frame.size.width){
            self.imageView.frame = CGRectMake(100, 0, self.view.frame.size.width - 200, self.view.frame.size.height);
            self.imageView.center = CGPointMake(self.view.frame.size.width  / 2, self.view.frame.size.height / 2);
        }else{
            self.imageView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
        }
        self.scrollView.contentSize = CGSizeMake(self.imageView.frame.size.width , self.imageView.frame.size.height);
        [self.scrollView addSubview:self.imageView];
        [self.view addSubview:self.scrollView];
    }
}

-(void)cancelImageView{
    [self.navigationController dismissViewControllerAnimated:YES completion:^{
        //        [Logger logger_Log:@"I" :@"ImageViewController Cancelled"];
        [self.delegate cancelImageView:self.interfaceOrientation];
    }];
}

-(void)setupImage:(NSString *)path{
    self.imagePath = path;
}

-(void)setupImage:(NSString *)path withCustomNavigationItem: (BOOL) isCustomNavigationItemNeeded withJson:(NSDictionary *)jsonDict{
    self.imagePath = path;
    self.customCancelButton = isCustomNavigationItemNeeded;
    self.jsonDict = jsonDict;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator
{
    if (size.width > self.view.frame.size.width) {
        self.scrollView.frame =  CGRectMake(0, 0, size.width, size.height);
        self.imageView.frame = CGRectMake(100, 0, size.width - 200, size.height);
        self.imageView.center = CGPointMake(size.width  / 2, size.height / 2);
    }else{
        self.scrollView.frame =  CGRectMake(0, 0, size.width, size.height);
        self.imageView.frame = CGRectMake(0, 0, size.width, size.height);
        //self.imageView.center = CGPointMake(size.width  / 2, size.height / 2);
    }
    self.scrollView.contentSize = CGSizeMake(self.imageView.frame.size.width , self.imageView.frame.size.height);
    
}
-(UIView *) viewForZoomingInScrollView:(UIScrollView *)scrollView
{
    return self.imageView;
}

-(void)cropMethod{
    UIImage *croppedImage=[_imageCropper getCroppedImage];
    [self modifyImage:croppedImage];
}

-(void)modifyImage:(UIImage*)image{
    NSData *dataImg;
    NSString *urlPath;
    dataImg=[NSData dataWithData:UIImageJPEGRepresentation(image, 1.0)];
    if (dataImg){
        NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
        NSString *documentsDirectory = [[NSString alloc] initWithString:[paths objectAtIndex:0]];
        NSString *photosDir = [documentsDirectory stringByAppendingPathComponent:CAMERA_DIRNAME];
        BOOL isDir;
        if(![[NSFileManager defaultManager] fileExistsAtPath:photosDir isDirectory:&isDir]){
            [[NSFileManager defaultManager] createDirectoryAtPath:photosDir withIntermediateDirectories:YES attributes:nil error:NULL];
        }
        NSString * timestamp = [NSString stringWithFormat:@"%f",[[NSDate date] timeIntervalSince1970] * 1000];
        photosDir = [photosDir stringByAppendingPathComponent:timestamp];
        urlPath = [photosDir stringByAppendingString:CAMERA_IMAGE_EXTENSION];
//        NSError * writeError;
//        BOOL writeResult= [dataImg writeToFile:urlPath options:NSDataWritingAtomic error:&writeError];
        BOOL writeResult= [MiscellaneousMethods writeData:dataImg toFile:urlPath];
        if(writeResult){
            [self.navigationController dismissViewControllerAnimated:YES completion:^{
                [self.delegate imageCropperCallback:urlPath];
            }];
        }else{
            [self.navigationController dismissViewControllerAnimated:YES completion:^{
                [self.delegate imageCropperCallback:@"fail"];
            }];
        }
    }
}


@end
