//
//  FilesViewController.h
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//

#import <UIKit/UIKit.h>

@protocol FileSelectedDelegate <NSObject>
    -(void)selectedFile:(NSString *)fileName path:(NSString *)filePath;
    -(void)selectedSingleFile:(NSString *)fileName path:(NSString *)filePath;
@end

@interface FilesViewController : UITableViewController
    @property(assign,nonatomic)id<FileSelectedDelegate>delegate;
-(void)initFiles:(NSArray *)fileArr directory:(NSString *)directoryPath openType:(NSString *)type fileFilters:(NSString*)fileFilters;
-(void)initCreateFiles:(NSString *)fileStr directory:(NSString *)directoryPath openType:(NSString *)type fileFilters:(NSString*)fileFilters;

@end

