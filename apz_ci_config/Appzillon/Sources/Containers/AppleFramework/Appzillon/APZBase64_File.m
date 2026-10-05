//
//  APZBase64_File.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 03/02/17.
//
//

#import "APZBase64_File.h"
#import "AppzillonViewController.h"
#import "Logger.h"
#import "APZJsonUtil.h"

@interface APZBase64_File()
@property(nonatomic,strong)WKWebView *webView;
@property(nonatomic,strong)AppzillonViewController *viewController;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZBase64_File
-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZBase64_File--Execute"];
    if (jsonDict) {
        self.pluginId=[jsonDict objectForKey:PLUGINID];
        if ([[jsonDict objectForKey:@"action"]isEqualToString:@"filetoB64"]) {
            [self convertFiletoBase64:jsonDict];
        }else{
            [self convertBase64toFile:jsonDict];
        }
    }
}
-(void)convertBase64toFile:(NSDictionary*)jsonDict{
    [Logger logger_Log:@"I" :@"APZBase64_File--convertBase64toFile"];
    NSData *fileData=[Base64 decode:[jsonDict objectForKey:@"base64"]];
    NSString * filePath=[self.viewController.sandboxPath stringByAppendingPathComponent:[NSString stringWithFormat:@"Assets/apps/%@",self.viewController.appString]];
    NSString *relativePath=[jsonDict objectForKey:@"filePath"];
    if ([relativePath length]>0) {
        filePath=[filePath stringByAppendingPathComponent:relativePath];
    }
    NSFileManager *fileManager=[NSFileManager defaultManager];
    NSError* directoryError;
    [fileManager createDirectoryAtPath:filePath withIntermediateDirectories:YES attributes:nil error:&directoryError];
    if (directoryError) {
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-072",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        
        [Logger logger_Log:@"E" :@"APZBase64_File--could not create intermediate direcory"];
        [self cleanPlugin];
    }else{
        NSString*fileName=[jsonDict objectForKey:@"fileName"];
        if ([fileName length]>0) {
            filePath=[filePath stringByAppendingPathComponent:fileName];
//            if ([fileData writeToFile:filePath atomically:YES]) {
            if ([MiscellaneousMethods writeData:fileData toFile:filePath]) {
                NSArray *resultkeys=[NSArray arrayWithObjects:@"filePath",nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",filePath],nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                [Logger logger_Log:@"I" :@"APZBase64_File--file has been created successfully"];
                [self cleanPlugin];
            }else{
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-271",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                [Logger logger_Log:@"E" :@"APZBase64_File--write Error"];
                [self cleanPlugin];
            }
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:@"APZ-CNT-169",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZBase64_File--incorrect file name"];
            [self cleanPlugin];
        }
    }
}

-(void)convertFiletoBase64:(NSDictionary*) jsonDict{
    [Logger logger_Log:@"I" :@"APZBase64_File--convertFiletoBase64"];
    NSData *fileData = [NSData dataWithContentsOfFile:[jsonDict objectForKey:@"filePath"]];
    NSString *base64String = [Base64 encode:fileData];
    if (base64String) {
        NSArray *returnResultkeys=[NSArray arrayWithObjects:CBTEXT,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:base64String,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :returnResultkeys :returnResult]];
        [Logger logger_Log:@"I" :@"APZBase64_File--convertFiletoBase64--Success"];
    }else{
        NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *returnResult=[NSArray arrayWithObjects:@"APZ-CNT-270",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :returnResultkeys :returnResult]];
        [Logger logger_Log:@"E" :@"APZBase64_File--convertFiletoBase64--Could not convert file into  Base64"];
        
    }
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZBase64_File--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}
@end

