//
//  APZFileDownload.m
//  Appzillon
//
//  Created by Amuly Ranjan Mishra on 08/10/13.
//
//

#import "APZFileDownload.h"
#import "Constants.h"
#import "AFNetworking.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Base64.h"
#import "Logger.h"
#import "CallServer.h"
#import "AppzillonViewController.h"
@interface APZFileDownload ()
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSOperationQueue *fileUpDownloadQueue;
@property(nonatomic,copy)NSString *serverURL;
@property(nonatomic,copy)NSString *fileName;
@property(nonatomic,copy)NSString *destinationPath;
@property(nonatomic,copy)NSString *base64Enabled;
@property(nonatomic,copy)NSString *sessionReq;
@property(nonatomic,strong)NSString *pluginId;
@end
@implementation APZFileDownload
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict
{
    [Logger logger_Log:@"D" :@"APZFileDownload--execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    _base64Enabled=[jsonDict objectForKey:@"base64"];
//    NSString *serverURL=[self.viewController.appPropertyDictionary objectForKey:@"serverUrl"];
    NSString *serverURL = self.viewController.APZServerURL;
    self.fileName=[jsonDict objectForKey:FILEDOWNLOAD_RES_FILENAME];
    NSString *filePathJson=[jsonDict objectForKey:FILEDOWNLOAD_RES_FILEPATH];
    _sessionReq=[jsonDict objectForKey:@"sessionReq"];
    self.destinationPath=[jsonDict objectForKey:FILEDOWNLOAD_DESTINATION_DIR];
    NSMutableDictionary *apzBodyDictionary=[[NSMutableDictionary alloc]init];
    NSMutableDictionary *appzillonRequest=[[NSMutableDictionary alloc]init];
    if ([_sessionReq isEqualToString:@"Y"]) {
        NSMutableDictionary *appzillonFilePushServiceRequest=[[NSMutableDictionary alloc]init];
        [appzillonFilePushServiceRequest setObject:self.fileName forKey:@"fileName"];
        [appzillonFilePushServiceRequest setObject:filePathJson forKey:@"filePath"];
        [appzillonFilePushServiceRequest setObject:@"Y" forKey:@"base64"];
        [apzBodyDictionary setObject:appzillonFilePushServiceRequest forKey:@"appzillonFilePushServiceRequest"];
        
    }else{
        NSMutableDictionary *appzillonFilePushServiceWSRequest=[[NSMutableDictionary alloc]init];
        [appzillonFilePushServiceWSRequest setObject:self.fileName forKey:@"fileName"];
        [appzillonFilePushServiceWSRequest setObject:filePathJson forKey:@"filePath"];
        [appzillonFilePushServiceWSRequest setObject:@"Y" forKey:@"base64"];
        [apzBodyDictionary setObject:appzillonFilePushServiceWSRequest forKey:@"appzillonFilePushServiceWSRequest"];
    }
    NSDictionary* apzHeaderDictionary=[jsonDict objectForKey:@"appzillonHeader"];
    //    NSData *headerData = [apzHeaderString dataUsingEncoding:NSUTF8StringEncoding];
    //    NSDictionary* apzHeaderDictionary = [NSJSONSerialization JSONObjectWithData:headerData options:0 error:nil];
    [appzillonRequest setObject:apzBodyDictionary forKey:@"appzillonBody"];
    [appzillonRequest setObject:apzHeaderDictionary forKey:@"appzillonHeader"];
    //    NSData * jsonData = [NSJSONSerialization  dataWithJSONObject:appzillonRequest options:0 error:nil];
    //    NSString * appzillonRequestString = [[NSString alloc] initWithData:jsonData   encoding:NSUTF8StringEncoding];
    //    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:serverURL]];
    //    [request setHTTPBody:[appzillonRequestString dataUsingEncoding:NSUTF8StringEncoding]];
    [CallServer callServerWithRequest:appzillonRequest :serverURL :@"downloadRequestSuccess" :@"downloadRequestFail" :self :[[jsonDict objectForKey:@"appzillonHeader"]objectForKey:@"appId"] :self.viewController];
    [Logger logger_Log:@"I" :@"APZFileDownload--file Request is sent"];
}
-(void)downloadRequestSuccess:(NSMutableDictionary *)serverResponseDictionary{
    [Logger logger_Log:@"I" :@"APZFileDownload--response recieved"];
    BOOL status=[[[serverResponseDictionary objectForKey:FILEDOWNLOAD_RES_HEADER] objectForKey:FILEDOWNLOAD_RES_STATUS]boolValue];
    if (status) {
        serverResponseDictionary=[serverResponseDictionary objectForKey:FILEDOWNLOAD_RES_BODY];
        if([_sessionReq isEqualToString:@"Y"]){
            serverResponseDictionary=[serverResponseDictionary objectForKey:FILEDOWNLOAD_KET_PUSH];
        }
        else{
            serverResponseDictionary=[serverResponseDictionary objectForKey:FILEDOWNLOAD_KET_PUSHWS];
        }
        if ([_base64Enabled isEqualToString:@"Y"]) {
            NSArray *resultSuccessKeys=[NSArray arrayWithObjects:@"base64",nil];
            NSArray *resultSuccess=[NSArray arrayWithObjects:[serverResponseDictionary objectForKey:FILEDOWNLOAD_RES_FILE],nil];
            [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultSuccessKeys :resultSuccess]];
            [Logger logger_Log:@"I" :@"APZFileDownload--base64 success"];
        }else{
            NSData *fileData=[Base64 decode:[serverResponseDictionary objectForKey:FILEDOWNLOAD_RES_FILE]];
            NSArray *pathArray= [NSArray arrayWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
            NSString *documentsDirectory =[pathArray objectAtIndex:0];
            NSString *appzillonAppSandbox=[documentsDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/apps/%@",self.viewController.appString]];
            if ((self.destinationPath !=nil)&& (![self.destinationPath isEqualToString:@""])) {
                appzillonAppSandbox=[[appzillonAppSandbox stringByAppendingString:@"/"]stringByAppendingString:self.destinationPath];
                NSFileManager *fileManager=[NSFileManager defaultManager];
                if (![fileManager fileExistsAtPath:appzillonAppSandbox]) {
                    [fileManager createDirectoryAtPath:appzillonAppSandbox withIntermediateDirectories:YES attributes:nil error:nil];
                }
            }else{
                appzillonAppSandbox=[NSString stringWithFormat:@"%@/downloads",appzillonAppSandbox];
                NSFileManager *fileManager=[NSFileManager defaultManager];
                if (![fileManager fileExistsAtPath:appzillonAppSandbox]) {
                    [fileManager createDirectoryAtPath:appzillonAppSandbox withIntermediateDirectories:NO attributes:nil error:nil];
                }
            }
            appzillonAppSandbox=[NSString stringWithFormat:@"%@/%@",appzillonAppSandbox,self.fileName];
//            if ( [fileData writeToFile:appzillonAppSandbox atomically:YES]) {
            if ([MiscellaneousMethods writeData:fileData toFile:appzillonAppSandbox]) {
                [Logger logger_Log:@"I" :@"APZFileDownload--file store success"];
                NSArray *resultSuccessKeys=[NSArray arrayWithObjects:@"filePath",nil];
                NSArray *resultSuccess=[NSArray arrayWithObjects:appzillonAppSandbox,nil];
                [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultSuccessKeys :resultSuccess]];
            }
        }
    }else{
        NSArray *errorArray=[serverResponseDictionary objectForKey:FILEDOWNLOAD_RES_ERROR];
        serverResponseDictionary =[errorArray objectAtIndex:0];
        [self downloadRequestFail:[serverResponseDictionary objectForKey:FILEDOWNLOAD_ERRORMESSAGE]errorCode:[serverResponseDictionary objectForKey:FILEDOWNLOAD_ERRORCODE]];
        NSString *errorString=[NSString stringWithFormat:@"APZFileDownload--%@",errorArray];
        [Logger logger_Log:@"E" :errorString];
    }
    [self cleanPlugin];
}
-(void)downloadRequestFail:(NSMutableDictionary*)response{
    [self downloadRequestFail:@"netwrok error" errorCode:[response objectForKey:ERROR_CODE]];
}
-(void)downloadRequestFail:(NSString *)serverErrorMsg errorCode:(NSString *)errorCode{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:errorCode,nil];
    [MiscellaneousMethods jsLayerCall:self.webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    NSString *errorString=[NSString stringWithFormat:@"APZFileDownload--%@",serverErrorMsg];
    [Logger logger_Log:@"E" :errorString];
    [self cleanPlugin];
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZFileDownload--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end


