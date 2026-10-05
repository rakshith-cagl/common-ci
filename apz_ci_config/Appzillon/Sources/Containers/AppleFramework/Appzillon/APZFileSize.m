//
//  APZFileSize.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 25/05/16.
//
//

#import "APZFileSize.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
@interface APZFileSize()
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@end
@implementation APZFileSize
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if(jsonDict !=nil){
        [Logger logger_Log:@"I" :@"fileSize Called"];
        NSString *filePath=[jsonDict objectForKey:@"filePath"];
        if (filePath==NULL||[filePath isEqualToString:@""]||[filePath isEqualToString:@" "]) {
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:INVALID_SRC,nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        }else{
            NSError* error;
            unsigned long long fileSize=[[[NSFileManager defaultManager]attributesOfItemAtPath:filePath error:&error] fileSize];
            unsigned long long FileSizeKB= fileSize /1024;
            if (error!=NULL) {
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:FILE_SIZE_ERROR,nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                NSString *errorString=[NSString stringWithFormat:@"FileSize--%@",error];
                [Logger logger_Log:@"E" :errorString];
            }else{
                NSArray *resultkeys=[NSArray arrayWithObjects:@"fileSize",nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%llu",FileSizeKB],nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                [Logger logger_Log:@"D" :@"FileSize Success"];
            }
            
        }
        [self cleanPlugin];
    }
    else{
        [self cleanPlugin];
    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"FileSize Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}
@end
