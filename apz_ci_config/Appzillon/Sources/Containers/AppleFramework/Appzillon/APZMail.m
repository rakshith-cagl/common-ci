//
//  APZMail.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZMail.h"
#import <MessageUI/MFMailComposeViewController.h>
#import "AppzillonViewController.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
@interface APZMail()<MFMailComposeViewControllerDelegate>
@property(nonatomic,weak)AppzillonViewController *viewController;
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@end

@implementation APZMail

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZMail--execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    if(jsonDict !=nil){
        if([MFMailComposeViewController canSendMail]){
            [self showMail:jsonDict];
        }
        else{
            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:EMAIL_CLIENT_ERROR,nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            
            [Logger logger_Log:@"E" :@"APZMail--Device Unable to Send Mail"];
            [self cleanPlugin];
        }
    }
    else
    {
        [self cleanPlugin];
    }
}

-(void)showMail:(NSDictionary *)jsonDict{
    NSString *message= [jsonDict objectForKey:EMAILSUBJECT];
    NSString *toRecipients=[jsonDict objectForKey:EMAILRECIPIENTID];
    NSString *body =[jsonDict objectForKey:EMAILBODY];
    unsigned long long maxFileSize=([[jsonDict objectForKey:@"maxAttachmentSize"] intValue]*1024*1024);
    NSArray *emailCcList =[[jsonDict objectForKey:EMAIL_CC_IDLIST] componentsSeparatedByString:@"," ];
    NSArray* jsonKeys=[jsonDict allKeys];
    BOOL filePathExists= [jsonKeys containsObject:@"filePaths"];
    if (filePathExists) {
        NSFileManager *fileManager=[NSFileManager defaultManager];
        unsigned long long fileSizeCount=0;
        NSArray *files=[jsonDict objectForKey:@"filePaths"];
        MFMailComposeViewController *mailer=[self getMailObject:message :toRecipients :emailCcList :body];
        BOOL attachmentSuccess=true;
        for (NSString *filePath in files) {
            if ([fileManager fileExistsAtPath:filePath]) {
                fileSizeCount=fileSizeCount+[self getFileSize:filePath];
                if (fileSizeCount>maxFileSize) {
                    NSArray* resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                    NSArray* resultMsg=[NSArray arrayWithObjects:@"APZ-CNT-322",nil];
                    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                    
                    [Logger logger_Log:@"I" :@"APZMail--FileSize is greater than "];
                    attachmentSuccess=false;
                    break;
                }
                else{
                    NSData *fileData=[NSData dataWithContentsOfFile:filePath];
                    if (fileData) {
                        [mailer addAttachmentData:fileData mimeType:[self mimeTypeForFileAtPath:filePath] fileName:[filePath lastPathComponent]];
                    }else{
                        NSArray* resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                        NSArray* resultMsg=[NSArray arrayWithObjects:FILE_NOT_FOUND_ERROR,nil];
                        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                        [Logger logger_Log:@"I" :@"APZMail--invalid file location"];
                        attachmentSuccess=false;
                        break;
                    }
                }
            }else{
                NSArray* resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray* resultMsg=[NSArray arrayWithObjects:FILE_NOT_FOUND_ERROR,nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                [Logger logger_Log:@"I" :@"APZMail--invalid file location"];
                attachmentSuccess=false;
                break;
            }
        }
        if (attachmentSuccess) {
            mailer.modalPresentationStyle = UIModalPresentationFormSheet;
            [self.viewController presentViewController:mailer animated:YES completion:nil];
            [Logger logger_Log:@"I" :@"APZMail--Mail View Opened"];
        }
    }else{
        MFMailComposeViewController *mailer=[self getMailObject:message :toRecipients :emailCcList :body];
        mailer.modalPresentationStyle = UIModalPresentationFormSheet;
        [self.viewController presentViewController:mailer animated:YES completion:nil];
        [Logger logger_Log:@"I" :@"APZMail--Mail View Opened"];
    }
}
-(MFMailComposeViewController*)getMailObject :(NSString*)subject :(NSString*)toRecipients :(NSArray*)ccRecipient :(NSString*)messageBody{
    MFMailComposeViewController * mailer = [[MFMailComposeViewController alloc]init];
    mailer.mailComposeDelegate = self;
    [mailer setSubject:subject];
    NSArray * toval = [NSArray arrayWithObject:toRecipients];
    [mailer setToRecipients:toval];
    [mailer setCcRecipients:ccRecipient];
    [mailer setMessageBody:messageBody isHTML:NO];
    return mailer;
}
#pragma mark - Mail
-(void)mailComposeController:(MFMailComposeViewController *)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError *)error
{
    [controller dismissViewControllerAnimated:YES completion:nil];
    [Logger logger_Log:@"I" :@"APZMail--Mail View Closed"];
    NSArray *resultkeys;
    NSArray *resultMsg;
    switch (result) {
        case MFMailComposeResultSent:{
            resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            resultMsg=[NSArray arrayWithObjects:@"sent",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
            [Logger logger_Log:@"I" :@"APZMail--Mail Sent"];
            break;
        }
        case MFMailComposeResultSaved:{
            resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            resultMsg=[NSArray arrayWithObjects:@"saved",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
            [Logger logger_Log:@"I" :@"APZMail--Mail Saved"];
            break;
        }
        case MFMailComposeResultCancelled:{
            resultkeys=[NSArray arrayWithObjects:CBEVENT,nil];
            resultMsg=[NSArray arrayWithObjects:@"cancel",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
            [Logger logger_Log:@"I" :@"APZMail--Mail Cancelled"];
            break;
        }
        case MFMailComposeResultFailed:{
            resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            resultMsg=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",EMAIL_CLIENT_ERROR],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
            NSString *errorString=[NSString stringWithFormat:@"APZMail--%@",[error localizedDescription]];
            [Logger logger_Log:@"E" :errorString];
            break;
        }
        default:
            break;
    }
    [self cleanPlugin];
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
-(unsigned long long)getFileSize:(NSString *)filePath{
    NSError* error;
    unsigned long long fileSizeinBytes=[[[NSFileManager defaultManager]attributesOfItemAtPath:filePath error:&error] fileSize];
    //    int fileSize=(int)fileSizeinBytes/(1024*1024);
    return fileSizeinBytes;
}

#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"D" :@"APZMail--Done"];
    self.webView = nil;
    [self.delegate donePlugin:self];
}

@end

