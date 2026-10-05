//
//  APZChildWipeout.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 13/01/15.
//
//

#import "APZChildWipeout.h"
#import "Constants.h"
#import "APZJsonUtil.h"
#import "Logger.h"
@interface APZChildWipeout()
@property(nonatomic,weak)WKWebView *web;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZChildWipeout

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.web = wbView;
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZChildWipeout--Execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    NSString *appName=[jsonDict objectForKey:@"appId"];
    if (appName !=NULL) {
        [self deleteSubApp:appName];
    }else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:INVALIDAPPID,nil];
        [MiscellaneousMethods jsLayerCall:self.web :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZChildWipeout--pass a valid appID"];
    }
}

-(void)deleteSubApp:(NSString *)appName{
    NSFileManager *fileManager = [[NSFileManager alloc] init];
    NSError *error = nil;
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *appPath=[documentsDirectory stringByAppendingFormat:@"/Assets/apps/%@/",appName];
    BOOL isDir=YES;
    BOOL fileExist=[fileManager fileExistsAtPath:appPath isDirectory:&isDir];
    if(fileExist) {
        [fileManager removeItemAtPath:appPath error:&error];
        if (error==nil) {
            NSArray * resultkeys=nil;
            NSArray *resultMsg=nil;
            [MiscellaneousMethods jsLayerCall:self.web :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
            [Logger logger_Log:@"I" :@"APZChildWipeout--deleted"];
        }else{
            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:APP_WIPE_OUT_FAILURE,nil];
            [MiscellaneousMethods jsLayerCall:self.web :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
            NSString *errorString=[NSString stringWithFormat:@"APZChildWipeout--%@",[error localizedDescription]];
            [Logger logger_Log:@"E" :errorString];
        }
    }else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:APP_WIPE_OUT_FAILURE,nil];
        [MiscellaneousMethods jsLayerCall:self.web :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        [Logger logger_Log:@"E" :@"APZChildWipeout--filePath Does not Exist"];
    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZChildWipeout--Done"];
    self.web = nil;
    [self.delegate donePlugin:self];
}
@end

