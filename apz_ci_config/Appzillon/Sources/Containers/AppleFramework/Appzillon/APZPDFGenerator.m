//
//  APZPDFGenerator.m
//  Appzillon
//
//  Created by Admin on 07/08/17.
//

#import "APZPDFGenerator.h"
#import "Logger.h"
#import "Constants.h"
#import "APZJsonUtil.h"
#import <CoreText/CoreText.h>
#import "AppzillonViewController.h"
#import "Base64.h"

@interface APZPDFGenerator()
@property (nonatomic,weak)WKWebView *webView;
@property (nonatomic,strong)NSString *pluginId;
@property (nonatomic,strong)AppzillonViewController *viewController;
@property (nonatomic,strong)NSMutableData *contentData;
@property (nonatomic,strong)NSString *tempPDF;
@property (assign) NSInteger currentPage;
@property (assign) CGFloat kDefaultPageHeight;
@property (assign) CGFloat kDefaultPageWidth;
@property (assign) CGFloat kOriginX;
@property (assign) CGFloat KOriginY;
@end

@implementation APZPDFGenerator
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
        //            if ([[jsonDict objectForKey:@"action"] isEqualToString:@"intialize"]) {
        self.pluginId=[jsonDict objectForKey:PLUGINID];
        self.tempPDF=[self getSandboxPath:@"temp.pdf"];
        self.kDefaultPageWidth=800;
        self.kDefaultPageHeight=1934;
        _kOriginX=30;
        _KOriginY=30;
        //        [self calculatePageHeight_Width];
        UIGraphicsBeginPDFContextToFile(self.tempPDF, CGRectZero, nil);
        _currentPage = 0;
        //                NSArray* keyArray=[[NSArray alloc]initWithObjects:CBEVENT,nil];
        //                NSArray *valueArray=[[NSArray alloc] initWithObjects:@"pdfInitialized",nil];
        //                [self jsLayerCall:JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :keyArray :valueArray]];
        //            }
    }
    return self;
}
-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZPDFGenerator--execute"];
    [self pdfOperation:jsonDict];
}

#pragma mark plugin recall
-(void)pdfOperation:(NSDictionary *)jsonDict{
    
    if ([[jsonDict objectForKey:@"action"] isEqualToString:@"createPdf"]) {
        [self generatePDF :jsonDict];
        NSArray* keyArray=[[NSArray alloc]initWithObjects:CBEVENT,nil];
        NSArray *valueArray=[[NSArray alloc] initWithObjects:@"pdfGenerated",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :keyArray :valueArray]];
    }
    else if ([[jsonDict objectForKey:@"action"] isEqualToString:@"addContent"]) {
        NSString *contentType=[jsonDict objectForKey:@"contentType"];
        if ([contentType isEqualToString:@"image"]) {
            [self drawImage:jsonDict];
        } else if([contentType isEqualToString:@"text"]) {
            [self appendTextContentToPDF:jsonDict];
        }
        NSArray* keyArray=[[NSArray alloc]initWithObjects:CBEVENT,nil];
        NSArray *valueArray=[[NSArray alloc] initWithObjects:@"ContentAdded",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :keyArray :valueArray]];
    }
}
#pragma mark String Insertion
- (void)appendTextContentToPDF:(NSDictionary *)jsonDict
{
    NSString *content=[jsonDict objectForKey:@"text"];
    NSString *fontType=[jsonDict objectForKey:@"fontType"];
    if (fontType.length==0) {
        fontType=@"Arial";
    }
    fontType = [NSString stringWithFormat:@"%@%@",[[fontType substringToIndex:1] uppercaseString],[fontType substringFromIndex:1] ];
    const char *fontTypeInChar = [fontType UTF8String];
    NSString *fontSize=[jsonDict objectForKey:@"fontSize"];
    CGFloat fontSizeInFloat=(CGFloat)[fontSize floatValue];
    if(fontSizeInFloat==0){
        fontSizeInFloat=13.0;
    }
    CGColorRef fontColor;
    NSString *hexdecimalColor=[jsonDict objectForKey:@"fontColor"];
    if (hexdecimalColor.length==0) {
        fontColor=[UIColor blackColor].CGColor;
    }else{
        UIColor *colorfromHexValue=[self getFontColorFromHexString:hexdecimalColor alpha:1];
        fontColor=colorfromHexValue.CGColor;
    }
    CFStringRef font_name = CFStringCreateWithCString(NULL, fontTypeInChar, kCFStringEncodingMacRoman);
    CTFontRef fontDetails = CTFontCreateWithName(font_name, fontSizeInFloat, NULL);
    CFStringRef keys[] = { kCTFontAttributeName,kCTForegroundColorAttributeName };
    CFTypeRef values[] = { fontDetails,fontColor};
    CFDictionaryRef font_attributes = CFDictionaryCreate(kCFAllocatorDefault, (const void **)&keys, (const void **)&values, sizeof(keys) / sizeof(keys[0]), &kCFTypeDictionaryKeyCallBacks, &kCFTypeDictionaryValueCallBacks);
    CFAttributedStringRef currentText = CFAttributedStringCreate(NULL,
                                                                 (CFStringRef)content, font_attributes);
    if (currentText) {
        CTFramesetterRef framesetter = CTFramesetterCreateWithAttributedString(currentText);
        framesetter = CTFramesetterCreateWithAttributedString(currentText);
        if (framesetter) {
            CFRange currentRange = CFRangeMake(0, 0);
            BOOL done = NO;
            do {
                UIGraphicsBeginPDFPageWithInfo(CGRectMake(0, 0, _kDefaultPageWidth, _kDefaultPageHeight), nil);
                [self drawPageNumber];
                currentRange = [self renderPage:_currentPage withTextRange:
                                currentRange andFramesetter:framesetter];
                if (currentRange.location == CFAttributedStringGetLength
                    ((CFAttributedStringRef)currentText))
                    done = YES;
            } while (!done);
            if (done) {
                NSArray* keyArray=[[NSArray alloc]initWithObjects:CBEVENT,nil];
                NSArray *valueArray=[[NSArray alloc] initWithObjects:@"contentAdded",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :keyArray :valueArray]];
            }
            CFRelease(framesetter);
        }
        CFRelease(currentText);
    }
}

- (CFRange)renderPage:(NSInteger)pageNum withTextRange:(CFRange)currentRange

       andFramesetter:(CTFramesetterRef)framesetter
{
    CGContextRef  currentContext = UIGraphicsGetCurrentContext();
    CGContextSetTextMatrix(currentContext, CGAffineTransformIdentity);
    
    CGRect    frameRect = CGRectMake(_kOriginX, _KOriginY, _kDefaultPageWidth-_kOriginX, _kDefaultPageHeight-_KOriginY);
    CGMutablePathRef framePath = CGPathCreateMutable();
    CGPathAddRect(framePath, NULL, frameRect);
    CTFrameRef frameRef = CTFramesetterCreateFrame(framesetter, currentRange, framePath, NULL);
    CGPathRelease(framePath);
    CGContextTranslateCTM(currentContext, 0, _kDefaultPageHeight);
    CGContextScaleCTM(currentContext, 1.0, -1.0);
    CTFrameDraw(frameRef, currentContext);
    currentRange = CTFrameGetVisibleStringRange(frameRef);
    currentRange.location += currentRange.length;
    currentRange.length = 0;
    CFRelease(frameRef);
    return currentRange;
    
}


-(void)drawImage :(NSDictionary *)jsonDict
{
    UIImage* image = [[UIImage alloc] initWithContentsOfFile:[jsonDict objectForKey:@"imagePath"]];
    if (image) {
        CGFloat imgHeight=[[jsonDict objectForKey:@"imageHeight"] floatValue];
        CGFloat imgWidth=[[jsonDict objectForKey:@"imageWidth"] floatValue];
        if (imgHeight==0) {
            imgHeight=image.size.height;
        }
        if (imgWidth==0) {
            imgWidth=image.size.width;
        }
        UIGraphicsBeginPDFPageWithInfo(CGRectMake(0, 0, _kDefaultPageWidth, _kDefaultPageHeight), nil);
        [self drawPageNumber];
        [self drawImage:image inRect:CGRectMake(_kOriginX, _KOriginY, imgWidth, imgHeight)];
        NSArray* keyArray=[[NSArray alloc]initWithObjects:CBEVENT,nil];
        NSArray *valueArray=[[NSArray alloc] initWithObjects:@"contentAdded",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :true :keyArray :valueArray]];
    }else{
        NSArray* keyArray=[[NSArray alloc] initWithObjects:ERROR_CODE,nil];
        NSArray *valueArray=[[NSArray alloc] initWithObjects:FILE_NOT_FOUND_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :true :keyArray :valueArray]];
    }
}
- (UIColor *)getFontColorFromHexString:(NSString *)str_HEX  alpha:(CGFloat)alpha_range{
    int red = 0;
    int green = 0;
    int blue = 0;
    sscanf([str_HEX UTF8String], "#%02X%02X%02X", &red, &green, &blue);
    return  [UIColor colorWithRed:red/255.0 green:green/255.0 blue:blue/255.0 alpha:alpha_range];
}

-(void)drawImage:(UIImage*)image inRect:(CGRect)rect
{
    [image drawInRect:rect];
}
- (void)drawPageNumber
{
    _currentPage++;
    NSString* pageString = [NSString stringWithFormat:@"%ld", (long)_currentPage];
    UIFont* theFont = [UIFont systemFontOfSize:13];
    CGSize pageStringSize = [pageString sizeWithAttributes:
                             @{NSFontAttributeName:
                                   theFont}];
    CGRect stringRect = CGRectMake(((_kDefaultPageWidth - pageStringSize.width) / 2.0),
                                   _kDefaultPageHeight-_KOriginY-5 ,
                                   pageStringSize.width,
                                   pageStringSize.height);
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
    NSDictionary *attributes = @{NSFontAttributeName: [UIFont systemFontOfSize:15], NSParagraphStyleAttributeName: paragraphStyle};
    [pageString drawInRect:stringRect withAttributes:attributes];
    
}
#pragma mark File Utility methods
-(NSString *)getSandboxPath:(NSString*)relativePath{
    NSString *fileName=[relativePath lastPathComponent];
    NSArray *dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* appzillonAppSandbox=[[dirPaths objectAtIndex:0] stringByAppendingPathComponent:@"Assets/apps"];
    NSString* fileOpnDir;
    if([relativePath isEqualToString:@""] ||relativePath==NULL){
        fileOpnDir=[appzillonAppSandbox stringByAppendingFormat:@"/%@/",self.viewController.appString];
    }else{
        if([relativePath stringByDeletingLastPathComponent]!=NULL){
            fileOpnDir=[appzillonAppSandbox stringByAppendingFormat:@"/%@/%@",self.viewController.appString,[relativePath stringByDeletingLastPathComponent]];
        }else{
            fileOpnDir=appzillonAppSandbox;
        }
    }
    [[NSFileManager defaultManager] createDirectoryAtPath:fileOpnDir withIntermediateDirectories:YES attributes:nil error:NULL];
    return [fileOpnDir stringByAppendingPathComponent:fileName];
}

-(void)generatePDF :(NSDictionary*)jsonDict{
    UIGraphicsEndPDFContext();
    NSFileManager *fileManager=[NSFileManager defaultManager];
    NSString *base64Required=[jsonDict objectForKey:@"base64"];
    if([base64Required isEqualToString:@"Y"]){
        NSData *fileData=[[NSData alloc]initWithContentsOfFile:self.tempPDF];
        NSString *fileBase64 = [Base64 encode:fileData];
        if(fileBase64!=NULL){
            NSArray* keyArray=[[NSArray alloc]initWithObjects:CBTEXT,nil];
            NSArray *valueArray=[[NSArray alloc] initWithObjects:fileBase64,nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :keyArray :valueArray]];
        }else{
            NSArray* keyArray=[[NSArray alloc]initWithObjects:ERROR_CODE,nil];
            NSArray *valueArray=[[NSArray alloc] initWithObjects:@"APZCNTERR",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :keyArray :valueArray]];
        }
    }else{
        NSString *filePath=[jsonDict objectForKey:@"filePath"];
        
        if(filePath.length==0){
            filePath=[[self getDateTime] stringByAppendingPathExtension:@"pdf"];
        }
        filePath=[self getSandboxPath:filePath];
        if([fileManager copyItemAtPath:self.tempPDF toPath:filePath error:nil]){
            NSArray* keyArray=[[NSArray alloc]initWithObjects:CBTEXT,nil];
            NSArray *valueArray=[[NSArray alloc] initWithObjects:filePath,nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :keyArray :valueArray]];
        }else{
            NSArray* keyArray=[[NSArray alloc]initWithObjects:ERROR_CODE,nil];
            NSArray *valueArray=[[NSArray alloc] initWithObjects:@"APZCNTERR",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :keyArray :valueArray]];
        }
    }
    [fileManager removeItemAtPath:self.tempPDF error:nil];
    [self cleanPlugin];
}
-(NSString*)getDateTime{
    NSDate *date = [NSDate date];
    NSDateFormatter * dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyyMMdd_HHmmss"] ;
    return [dateFormatter stringFromDate:date];
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    self.webView = nil;
    self.viewController=nil;
    [self.delegate donePlugin:self];
    [Logger logger_Log:@"E" :@"APZPDFGenerator--Done"];
}
@end

