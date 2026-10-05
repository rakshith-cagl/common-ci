#import "APZFileUpload.h"
#import "Constants.h"
#import "AFNetworking.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
#import "CallServer.h"
#import "AppzillonViewController.h"

@interface APZFileUpload()
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSOperationQueue *fileUploadQueue;
@property(nonatomic,copy)NSString *filePath;
@property(nonatomic,copy)NSString *fileID;
@property(nonatomic,strong)NSString *sessionReq;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZFileUpload

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZFileUpload--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *path = [jsonDict objectForKey:FILEUPLOAD_FILENAME];
    NSString *fileName=[path lastPathComponent];
    NSString *overWrite=@"N";
    if ([[jsonDict objectForKey:@"overWrite"] isEqualToString:@"Y"]) {
        overWrite=[jsonDict objectForKey:@"overWrite"];
    }
    NSDictionary* jsonHeader=[jsonDict objectForKey:@"appzillonHeader"];
    self.sessionReq=[jsonDict objectForKey:@"sessionReq"];
//    NSString *urlString=[self.viewController.appPropertyDictionary objectForKey:@"serverUrl"];
    NSString *urlString = self.viewController.APZServerURL;
    NSString* serverUrl=[urlString stringByAppendingString:FILE_UPLOAD_SERVER];
    if ([fileManager fileExistsAtPath: path] == YES)
    {
        //        NSData *data = [headerString dataUsingEncoding:NSUTF8StringEncoding];
        //        NSDictionary* jsonHeader = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        NSMutableDictionary *jsonBody=[[NSMutableDictionary alloc]init];
        NSString *destDirectory=[jsonDict objectForKey:FILEUPLOAD_DESTINATION_DIR];
        [jsonBody setObject:destDirectory forKey:@"destination"];
        [jsonBody setObject:overWrite forKey:@"overWrite"];
        NSMutableArray *fileDetails=[[NSMutableArray alloc]init];
        NSMutableDictionary *fileDetailElement=[[NSMutableDictionary alloc]init];
        [fileDetailElement setObject:fileName forKey:@"fileName"];
        [fileDetailElement setObject:[NSNumber numberWithLong:[self getFileSize:path]] forKey:@"fileSize"];
        [fileDetailElement setObject:[NSNumber numberWithInteger:1] forKey:@"fileNo"];
        [fileDetailElement setObject:[self mimeTypeForFileAtPath:path] forKey:@"fileType"];
        [fileDetails addObject:fileDetailElement];
        [jsonBody setObject:fileDetails forKey:@"fileDetails"];
        NSMutableDictionary *appzillonRequest=[[NSMutableDictionary alloc]init];
        [appzillonRequest setObject:jsonHeader forKey:@"appzillonHeader"];
        [appzillonRequest setObject:jsonBody forKey:@"appzillonBody"];
        //        NSError *err;
        //        NSData *dataFromDict = [NSJSONSerialization dataWithJSONObject:appzillonRequest
        //                                                               options:NSJSONWritingPrettyPrinted
        //                                                                 error:&err];
        //     NSMutableURLRequest *request = [[AFHTTPRequestSerializer serializer] multipartFormRequestWithMethod:@"POST" URLString:serverUrl parameters:nil constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
        //        [formData appendPartWithFileURL:[NSURL fileURLWithPath:path] name:fileName fileName:fileName mimeType:[self mimeTypeForFileAtPath:path] error:nil];
        //        [formData appendPartWithFormData:dataFromDict name:@"appzillonRequest"];
        //    } error:nil];
        //        [request setHTTPMethod:@"POST"];
        //        [CallServer callServerForUploadWithRequest:request :@"uploadRequestSuccess" :@"uploadRequestFail" :self :[[jsonDict objectForKey:@"appzillonHeader"]objectForKey:@"appId"]];
        //         [Logger logger_Log:@"I" :@"APZFileUpload--upload request sent"];
        [CallServer callServerForUploadWithRequest:appzillonRequest :@"uploadRequestSuccess" :@"uploadRequestFail" :self :[[jsonDict objectForKey:@"appzillonHeader"]objectForKey:@"appId"] :serverUrl :path :fileName :[self mimeTypeForFileAtPath:path] :self.viewController];
    }
    
}
-(NSString*) mimeTypeForFileAtPath: (NSString *) path {
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        return nil;
    }
    CFStringRef UTI = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, (__bridge CFStringRef)[path pathExtension], NULL);
    CFStringRef mimeType = UTTypeCopyPreferredTagWithClass (UTI, kUTTagClassMIMEType);
    CFRelease(UTI);
    if (!mimeType) {
        return @"application/octet-stream";
    }
    return ((__bridge NSString *)mimeType) ;
}
-(long)getFileSize :(NSString*)filePath{
    NSError*attributesError;
    NSDictionary *fileAttributes = [[NSFileManager defaultManager] attributesOfItemAtPath:filePath error:&attributesError];
    NSNumber *fileSizeNumber = [fileAttributes objectForKey:NSFileSize];
    long long fileSize = [fileSizeNumber longLongValue];
    return fileSize;
}
-(void)uploadRequestSuccess:(NSMutableDictionary *)messageDict{
    [Logger logger_Log:@"I" :@"APZFileUpload--response arrived"];
    if ([[[messageDict objectForKey:FILEUPLOAD_RES_HEADER] objectForKey:FILEUPLOAD_RES_STATUS] boolValue]) {
        messageDict=[messageDict objectForKey:FILEUPLOAD_RES_BODY];
        if([self.sessionReq isEqualToString:@"Y"]){
            messageDict=[messageDict objectForKey:FILEUPLOAD_RES_F_RESPONSE];
        }else{
            messageDict=[messageDict objectForKey:FILEUPLOAD_RES_F_RESPONSEWS];
        }
        for (NSString * file in [messageDict allKeys]) {
            if([[messageDict objectForKey:file]isEqualToString:@"success"]){
                NSArray *resultSuccessKeys=nil;
                NSArray *resultSuccess=nil;
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultSuccessKeys :resultSuccess]];
                [Logger logger_Log:@"I" :@"APZFileUpload--response arrived"];
            }else{
                [self uploadRequestFail:FILEUPLOAD_FAILURE_MESSAGE errorCode:FILEUPLOAD_FAILURE_CODE];
            }
        }
    }else{
        NSArray *errorArray=[messageDict objectForKey:FILEDOWNLOAD_RES_ERROR];
        messageDict =[errorArray objectAtIndex:0];
        [self uploadRequestFail:[messageDict objectForKey:FILEUPLOAD_RES_ERROR_MESSAGE]errorCode:[messageDict objectForKey:FILEUPLOAD_RES_ERROR_CODE]];
    }
    [self cleanPlugin];
}
-(void)uploadRequestFail:(NSMutableDictionary*)response{
    [self uploadRequestFail:@"network error" errorCode:[response objectForKey:ERROR_CODE]];
}
-(void)uploadRequestFail:(NSString *)serverErrorMsg errorCode:(NSString *)errorCode{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:errorCode,nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    NSString *errorString=[NSString stringWithFormat:@"APZFileUpload--%@",serverErrorMsg];
    [Logger logger_Log:@"E" :errorString];
    [self cleanPlugin];
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZFileUpload--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end


