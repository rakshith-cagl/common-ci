//
//  NotesPage.h
//  Azurite
//
//  Created by Shyam Bahadur Singh on 13/08/12.
//  Copyright (c) 2012 kapil.gupta@i-exceed.com. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "sqlite3.h"
#import "ApzApp.h"

@interface NotesPage : UIViewController
{
    ApzApp *shared;
    IBOutlet UITextView *notesText;
    IBOutlet UINavigationBar *navBar;
    IBOutlet UINavigationItem *navItem;
    NSString *txnNo;
    NSString *readwrite;
    sqlite3 *contactDB;
    sqlite3 *database;
    NSTimer *idleTimer;
    unsigned intColor;
//    NSString *OTAStatus;
}

-(IBAction)done;

@property (nonatomic,strong) NSString *txnNo;
@property (nonatomic,strong) NSString *readWrite;
//@property (nonatomic,strong) NSString *OTAStatus;
@end

