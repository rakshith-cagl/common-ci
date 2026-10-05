//
//  FileLocationController.h
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//

#import <UIKit/UIKit.h>

@protocol FileLocationSelectedDelegate <NSObject>
-(void)selectedFile:(NSString *)fileName path:(NSString *)filePath :(UIInterfaceOrientation)interfaceOrienation;
-(void)cancelBrowser:(UIInterfaceOrientation)interfaceOrientation;
@end

@interface FileLocationController : UITableViewController
    @property(assign,nonatomic)id<FileLocationSelectedDelegate>delegate;
    -(void)initFiles:(NSArray *)fileArr directory:(NSString *)directoryPath openType:(NSString *)type;
@end

