//
//  APZFileOperation.m
//  Appzillon
//
//  Created by Admin on 10/10/13.
//
//

#import "APZFileOperation.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
#import "AppzillonViewController.h"
#import <CoreText/CoreText.h>

#define kDefaultPageHeight 792
#define kDefaultPageWidth  612

@interface APZFileOperation()<UIActionSheetDelegate>
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(strong,nonatomic)NSString *fileCreateFileName;
@property(strong,nonatomic)NSString *fileCreateFileContent;
@property(strong,nonatomic)NSString *fileContentFileName;
@property(strong,nonatomic)NSString *fileDeleteFileName;
@property(strong,nonatomic)NSString *fileJsonPath;
@property(strong,nonatomic)NSString *pluginId;
@end

@implementation APZFileOperation
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    [Logger logger_Log:@"D" :@"APZFileOperation--Execute"];
    NSString *opnType = [jsonDict objectForKey:@"opnType"];
    opnType = [opnType lowercaseString];
    self.fileJsonPath =[jsonDict objectForKey:OPENFILE_FILEPATH];
    if ([opnType isEqualToString:@"create"]) {
        self.fileCreateFileName = [jsonDict objectForKey:FILEOPN_FILENAME];
        self.fileCreateFileContent= [jsonDict objectForKey:FILEOPN_FILECONTENT];
        if( [self.fileCreateFileName isEqualToString:@""] == NO){
            [self fileCreate];
        }
        else{
            [Logger logger_Log:@"E" :@"APZFileOperation--Create File Failed File name empty"];
        }
    }
    else if([opnType isEqualToString:@"delete"]){
        //        self.fileDeleteFileName = [jsonDict objectForKey:FILEOPN_FILENAME];
        //        if( [self.fileDeleteFileName isEqualToString:@""] == NO){
        [self fileDelete :[jsonDict objectForKey:@"filePath"]];
        //        }
        //        else{
        //            [Logger logger_Log:@"E" :@"APZFileOperation--delete File Failed File name empty"];
        //        }
    }
    else if([opnType isEqualToString:@"read"]) {
        //        self.fileContentFileName = [jsonDict objectForKey:FILEOPN_FILENAME];
        //        if([self.fileContentFileName isEqualToString:@""] == NO){
        [self fileContents :[jsonDict objectForKey:OPENFILE_FILEPATH]];
        //        }
        //        else{
        //            [Logger logger_Log:@"E" :@"APZFileOperation--Create File Failed File path empty"];
        //        }
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILEOPERATION_INVALID,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZFileOperation--Invalid File Operation"];
    }
    [self cleanPlugin];
}
#pragma mark FileCreate/Modify
-(void)fileCreate{
    NSString *filePath = [self fileOpnPath:self.fileCreateFileName];
    if([filePath isEqualToString:@""] == NO){
        if([[NSFileManager defaultManager] fileExistsAtPath:filePath] ){
            [self fileModify:filePath];
        }
        else{
            if ([[self.fileCreateFileName pathExtension]isEqualToString:@"text"]) {
                [self createTextFile:filePath];
            }
            else if ([[self.fileCreateFileName pathExtension]isEqualToString:@"pdf"])
            {
                [self createPDFFile:self.fileCreateFileContent :filePath];
            }
            else{
                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:INVALIDFILEEXT,nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                [Logger logger_Log:@"E" :@"APZFileOperation--Create Invalid File Extension"];
            }
        }
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILE_COULD_NOT_CREATED,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZFileOperation--Failed to create directory"];
        
    }
}

-(void)fileModify:(NSString *)filePath{
    NSMutableData *data = [[NSMutableData alloc]init];
    NSKeyedArchiver *archiver = [[NSKeyedArchiver alloc]initForWritingWithMutableData:data];
    [archiver encodeObject:self.fileCreateFileContent forKey:@"fileContent"];
    [archiver finishEncoding];
    BOOL isFileModified =[[NSFileManager defaultManager] createFileAtPath:filePath contents:data attributes:nil];
    if(isFileModified){
        NSArray *resultkeys=nil;
        NSArray *result=nil;
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZFileOperation--File modification success"];
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILEOP_MODIFY_FAIL,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZFileOperation--File modification Failed"];
    }
}

#pragma mark FileDelete
-(void)fileDelete :(NSString*)filePath{
    NSError *error = nil;
    //    NSString *filePath = [self fileOpnPath:self.fileDeleteFileName];
    if([filePath isEqualToString:@""] == NO ){
        if( [[NSFileManager defaultManager] removeItemAtPath:filePath error:&error]){
            NSArray *resultkeys=nil;
            NSArray *result=nil;
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
            [Logger logger_Log:@"I" :@"APZFileOperation--File Delete Success"];
        }
        else{
            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:FILEOP_DELETE_FAIL,nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
            [Logger logger_Log:@"E" :@"APZFileOperation--File Delete Failed"];
        }
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILE_NOT_FOUND_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZFileOperation--Failed to get Directory path"];
        
    }
}

#pragma mark FileContents
-(void)fileContents:(NSString*)filePath{
    //    NSString *filePath = [self fileOpnPath:self.fileContentFileName];
    //    if(![filePath isEqualToString:@""] ){
    if( [[NSFileManager defaultManager] fileExistsAtPath:filePath]){
        NSError *readError;
        NSString  *content = [NSString stringWithContentsOfFile:filePath
                                                       encoding:NSUTF8StringEncoding
                                                          error:&readError];
        NSArray* resultkeys=[NSArray arrayWithObjects:FILECONTENT_PATH_JSONKEY,FILECONTENT_CONTENT_JSONKEY,nil];
        NSArray* result =[NSArray arrayWithObjects:filePath,content,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZFileOperation--Failed to get fileContents success"];
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILE_NOT_FOUND_ERROR,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZFileOperation--File does not exists"];
    }
    //    }
    //    else{
    //        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    //        NSArray *resultMsg=[NSArray arrayWithObjects:FILE_NOT_FOUND_ERROR,nil];
    //        [self jsLayerCall:JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
    //        [Logger logger_Log:@"E" :@"APZFileOperation--File does not exists"];
    //    }
    [self cleanPlugin];
}
#pragma mark create File Method
-(void)createTextFile:(NSString*)filePath{
    NSString *fileContentString=[NSString stringWithFormat:@"%@", self.fileCreateFileContent];
    NSData * data = [fileContentString dataUsingEncoding:NSUTF8StringEncoding];
//    if ([data writeToFile:filePath atomically:YES]){
        if ([MiscellaneousMethods writeData:data toFile:filePath]){
        NSArray *resultkeys=[NSArray arrayWithObjects:@"filePath",nil];
        NSArray *result=[NSArray arrayWithObjects:filePath,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZFileOperation--Successfully Created File"];
    }else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:FILE_COULD_NOT_CREATED,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZFileOperation--create-- could not create file"];
    }
}
- (void)createPDFFile:(NSString *)content :(NSString *)filePath;
{
    CFAttributedStringRef currentText = CFAttributedStringCreate(NULL,
                                                                 (CFStringRef)content, NULL);
    CTFontRef font = CTFontCreateWithName((CFStringRef)@"Arial", 16.0f, nil);
    if (currentText) {
        CTFramesetterRef framesetter = CTFramesetterCreateWithAttributedString(currentText);
        if (framesetter) {
            UIGraphicsBeginPDFContextToFile(filePath, CGRectZero, nil);
            CFRange currentRange = CFRangeMake(0, 0);
            NSInteger currentPage = 0;
            BOOL done = NO;
            do {
                UIGraphicsBeginPDFPageWithInfo(CGRectMake(0, 0, kDefaultPageWidth,
                                                          kDefaultPageHeight), nil);
                currentPage++;
                [self drawPageNumber:currentPage];
                currentRange = [self renderPage:currentPage withTextRange:
                                currentRange andFramesetter:framesetter];
                if (currentRange.location == CFAttributedStringGetLength
                    ((CFAttributedStringRef)currentText))
                    done = YES;
            } while (!done);
            UIGraphicsEndPDFContext();
            CFRelease(framesetter);
            if (done) {
                NSArray *resultkeys=[NSArray arrayWithObjects:@"filePath",nil];
                NSArray *result=[NSArray arrayWithObjects:filePath,nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                [Logger logger_Log:@"I" :@"APZFileOperation--Successfully Created File"];
            }else{
                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:FILE_COULD_NOT_CREATED,nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                [Logger logger_Log:@"E" :@"APZFileOperation--create-- could not create file"];
            }
        } else {
            NSLog(@"Could not create the framesetter needed to lay out the atrributed string.");
        }
        CFRelease(currentText);
    } else {
        NSLog(@"Could not create the attributed string for the framesetter");
        
    }
}

- (CFRange)renderPage:(NSInteger)pageNum withTextRange:(CFRange)currentRange

       andFramesetter:(CTFramesetterRef)framesetter
{
    CGContextRef    currentContext = UIGraphicsGetCurrentContext();
    CGContextSetTextMatrix(currentContext, CGAffineTransformIdentity);
    CGRect    frameRect = CGRectMake(100, 100, 368, 648);
    CGMutablePathRef framePath = CGPathCreateMutable();
    CGPathAddRect(framePath, NULL, frameRect);
    CTFrameRef frameRef = CTFramesetterCreateFrame(framesetter, currentRange, framePath, NULL);
    CGPathRelease(framePath);
    CGContextTranslateCTM(currentContext, 0, kDefaultPageHeight);
    CGContextScaleCTM(currentContext, 1.0, -1.0);
    CTFrameDraw(frameRef, currentContext);
    currentRange = CTFrameGetVisibleStringRange(frameRef);
    currentRange.location += currentRange.length;
    currentRange.length = 0;
    CFRelease(frameRef);
    return currentRange;
    
}

- (void)drawPageNumber:(NSInteger)pageNum
{
    NSString* pageString = [NSString stringWithFormat:@"Page %ld", (long)pageNum];
    UIFont* theFont = [UIFont systemFontOfSize:25];
    CGSize pageStringSize = [pageString sizeWithAttributes:
                             @{NSFontAttributeName:
                                   theFont}];
    CGRect stringRect = CGRectMake(((kDefaultPageWidth - pageStringSize.width) / 2.0),
                                   720.0 + ((72.0 - pageStringSize.height) / 2.0) ,
                                   pageStringSize.width,
                                   pageStringSize.height);
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
    NSDictionary *attributes = @{NSFontAttributeName: [UIFont systemFontOfSize:15], NSParagraphStyleAttributeName: paragraphStyle};
    [pageString drawInRect:stringRect withAttributes:attributes];
    
}
#pragma mark File Utility methods
-(NSString *)fileOpnDirectory{
    NSArray *dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* appzillonAppSandbox=[[dirPaths objectAtIndex:0] stringByAppendingPathComponent:@"Assets/apps"];
    NSString* fileOpnDir;
    if([self.fileJsonPath isEqualToString:@""] ||self.fileJsonPath==NULL){
        fileOpnDir=[appzillonAppSandbox stringByAppendingFormat:@"/%@/",self.viewController.appString];
    }else{
        fileOpnDir=[appzillonAppSandbox stringByAppendingFormat:@"/%@/%@",self.viewController.appString,self.fileJsonPath];
    }
    
    [[NSFileManager defaultManager] createDirectoryAtPath:fileOpnDir withIntermediateDirectories:YES attributes:nil error:NULL];
    return fileOpnDir;
}

-(NSString *)fileOpnPath:(NSString *)fileName{
    NSString *fileOpnDir = [self fileOpnDirectory];
    NSString *filePath =nil;
    if( [fileOpnDir isEqualToString:@""] == NO){
        filePath = [fileOpnDir stringByAppendingPathComponent:fileName];
    }
    else{
        filePath = @"";
    }
    return filePath;
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    self.webView = nil;
    [self.delegate donePlugin:self];
    [Logger logger_Log:@"I" :@"APZFileOperation--Done"];
}

@end

