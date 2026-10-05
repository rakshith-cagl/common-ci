//
//  NotesPage.m
//  CameraPluginEx
//
//  Created by Kapil Gupta on 7/10/12.
//  Copyright (c) 2012 kapil.gupta@i-exceed.com. All rights reserved.
//

#import "NotesPage.h"
#import "AppzillonViewController.h"
#define UIColorFromRGB(rgbValue) [UIColor \
colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 \
green:((float)((rgbValue & 0xFF00) >> 8))/255.0 \
blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]
#define kMaxIdleTimeSeconds 90.0
@implementation NotesPage
@synthesize txnNo;
@synthesize readWrite;

- (void)resetIdleTimer {
    if (!idleTimer) {
        idleTimer = [NSTimer scheduledTimerWithTimeInterval:kMaxIdleTimeSeconds
                                                     target:self
                                                   selector:@selector(idleTimerExceeded)
                                                   userInfo:nil
                                                    repeats:NO];
    }
    else {
        if (fabs([idleTimer.fireDate timeIntervalSinceNow]) < kMaxIdleTimeSeconds-1.0) {
            [idleTimer setFireDate:[NSDate dateWithTimeIntervalSinceNow:kMaxIdleTimeSeconds]];
        }
    }
}
- (void) keyboardDidHide:(NSNotification*)notification{
    
}

- (void)idleTimerExceeded {
    idleTimer = nil;
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        AppzillonViewController * loginpage = [[AppzillonViewController alloc] initWithNibName:@"AppzillonViewController" bundle:nil];
        [loginpage setModalPresentationStyle:UIModalPresentationFullScreen];
        [self presentViewController:loginpage animated:YES completion:nil];
    }
    else if([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone){
        AppzillonViewController * loginpage = [[AppzillonViewController alloc] initWithNibName:@"Azuritei_phoneViewController" bundle:nil];
        [loginpage setModalPresentationStyle:UIModalPresentationFullScreen];
        [self presentViewController:loginpage animated:YES completion:nil];
    }
    
    [self resetIdleTimer];
}



- (UIResponder *)nextResponder {
    [self resetIdleTimer];
    return [super nextResponder];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - View lifecycle
-(NSString *)getDetaisFromDB:(NSString *)txn_No{
    NSString *temp;
    NSArray *array=NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *filePath=[array objectAtIndex:0];
    filePath =[filePath stringByAppendingString:[NSString stringWithFormat:@"/Assets/apps/%@/sqlite/Notes.sqlite3",shared.appIDNotes]];
    NSFileManager *manager=[NSFileManager defaultManager];
    BOOL success = NO;
    if ([manager fileExistsAtPath:filePath])
    {
        success =YES;
    }
    if (!success)
    {
        NSString *path2=[[[MiscellaneousMethods getAppBundle] resourcePath] stringByAppendingString:[NSString stringWithFormat:@"/Assets/apps/%@/sqlite/Notes.sqlite3",shared.appIDNotes]];
        [manager copyItemAtPath:path2 toPath:filePath error:nil];
    }
    sqlite3_stmt *statement = nil;
    NSString *query;
    if (sqlite3_open([filePath UTF8String], &database) == SQLITE_OK) {
        if (statement == nil) {
            query = [NSString stringWithFormat:@"SELECT txn_Details FROM tb_Notes WHERE txn_No=\"%@\"",txn_No];
        }
        if (sqlite3_prepare_v2(database, [query UTF8String], -1, &statement, NULL) == SQLITE_OK) {
            while (sqlite3_step(statement)== SQLITE_ROW) {
                temp=[[NSString alloc]initWithUTF8String:(const char *)sqlite3_column_text(statement, 0)];
            }
            sqlite3_finalize(statement);
            
            
        }else{
            return @"prepare";
        }
    }
    
    return temp;
    
}

-(NSMutableArray *)getTxnNoFromDB{
    NSString *temp;
    NSArray *array=NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *filePath=[array objectAtIndex:0];
    filePath =[filePath stringByAppendingString:[NSString stringWithFormat:@"/Assets/apps/%@/sqlite/Notes.sqlite3",shared.appIDNotes]];
    NSFileManager *manager=[NSFileManager defaultManager];
    BOOL success = NO;
    if ([manager fileExistsAtPath:filePath])
    {
        success =YES;
    }
    if (!success)
    {
        NSString *path2=[[[MiscellaneousMethods getAppBundle] resourcePath] stringByAppendingString:[NSString stringWithFormat:@"/Assets/apps/%@/sqlite/Notes.sqlite3",shared.appIDNotes]];
        [manager copyItemAtPath:path2 toPath:filePath error:nil];
    }
    NSMutableArray *returnArray = [[NSMutableArray alloc] init];
    sqlite3_stmt *statement = nil;
    NSString *query;
    if (sqlite3_open([filePath UTF8String], &database) == SQLITE_OK) {
        if (statement == nil) {
            query = [NSString stringWithFormat:@"SELECT txn_No FROM tb_Notes"];
        }
        
        if (sqlite3_prepare_v2(database, [query UTF8String], -1, &statement, NULL) == SQLITE_OK) {
            while (sqlite3_step(statement)== SQLITE_ROW) {
                temp=[[NSString alloc]initWithUTF8String:(const char *)sqlite3_column_text(statement, 0)];
                [returnArray addObject:temp];
            }
            sqlite3_finalize(statement);
            
            
        }
    }
    return returnArray;
    
}

- (void)viewDidLoad
{
//    shared = (AppzillonAppDelegate *)[[UIApplication sharedApplication] delegate];
    shared = (ApzApp *)[ApzApp sharedManager];
    navItem.titleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"landingLogo.png"]];
    navBar.tintColor = UIColorFromRGB(intColor);
    self.view.backgroundColor=[UIColor colorWithPatternImage:[UIImage imageNamed:@"/app/styles/default/images/notes.png"]];
    notesText.backgroundColor=[UIColor colorWithPatternImage:[UIImage imageNamed:@"/app/styles/default/images/notes.png"]];
    [super viewDidLoad];
    NSMutableArray *txn_No = [[NSMutableArray alloc] init];
    txn_No = [self getTxnNoFromDB];
    int count = 0;
    for (NSString *str in txn_No) {
        if ([str caseInsensitiveCompare:txnNo] == NSOrderedSame) {
            count = 1;
        }
    }
    if ([readWrite isEqualToString:@"r"]) {
        @try {
            if (count == 1) {
                [notesText setText:[NSString stringWithFormat:@"%@",[self getDetaisFromDB:txnNo] ]];
                [notesText setEditable:NO];
            }
            else {
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"No Data Found" message:@"There is no notes present for this Cheque number.Create Notes to read it." preferredStyle:UIAlertControllerStyleAlert];
                UIAlertAction * actionCancel = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}];
                [alert addAction:actionCancel];
                [self presentViewController:alert animated:YES completion:nil];
                
                [notesText setEditable:NO];
            }
        }
        @catch (NSException *exception) {
            NSLog(@"main: Caught %@: %@", [exception name], [exception reason]);
            
        }
        
        @finally {
            
            
            
        }
    }
    else if ([readWrite isEqualToString:@"w"]) {
        
        @try {
            if (count == 1) {
                [notesText setText:[NSString stringWithFormat:@"%@",[self getDetaisFromDB:txnNo] ]];
                [notesText setEditable:YES];
            }
        }
        
        @catch (NSException *exception) {
            
            NSLog(@"main: Caught %@: %@", [exception name], [exception reason]);
            
        }
        
        @finally {
            
            
            
        }
    }
}


- (void)textViewDidBeginEditing:(UITextView *)textView {
    notesText.frame = CGRectMake(0, 44, self.view.frame.size.width, self.view.frame.size.height/2.3);
}
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    return YES;
}

- (void)textViewDidEndEditing:(UITextView *)textView {
    notesText.frame = CGRectMake(0, 44, self.view.frame.size.width, self.view.frame.size.height);
    
}
- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        
        
    }
    
    return YES;
}



- (void)viewDidUnload
{
    navBar = nil;
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotate {
    
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad ) {
        
        
        
        return YES;
    }
    else
    {
        
        
        return YES;
    }
    
}

- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskAll;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        
        return YES;
    }
    else {
        return YES;
    }
}


-(BOOL)makeEntryInDB:(NSString *)txn_No theDetails:(NSString *)txn_Details{
    
    sqlite3_stmt *statement = nil;
    NSString *query;
    NSArray *array=NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *filePath=[array objectAtIndex:0];
    //    if ([OTAStatus isEqualToString:@"Y"]) {
    filePath =[filePath stringByAppendingString:[NSString stringWithFormat:@"/Assets/apps/%@/sqlite/Notes.sqlite3",shared.appIDNotes]];
    //    }else{
    //        filePath =[filePath stringByAppendingPathComponent:@"Notes.sqlite3"];
    //    }
    NSFileManager *manager=[NSFileManager defaultManager];
    BOOL success = NO;
    if ([manager fileExistsAtPath:filePath])
    {
        success =YES;
    }
    if (!success)
    {
        NSString *path2=[[[MiscellaneousMethods getAppBundle] resourcePath] stringByAppendingString:[NSString stringWithFormat:@"/Assets/apps/%@/sqlite/Notes.sqlite3",shared.appIDNotes]];
        [manager copyItemAtPath:path2 toPath:filePath error:nil];
    }
    if (sqlite3_open([filePath UTF8String], &database) == SQLITE_OK) {
        
        query = [NSString stringWithFormat:@"INSERT INTO tb_Notes VALUES ('%@', '%@')",txn_No,txn_Details];
        if (sqlite3_prepare_v2(database, [query UTF8String], -1, &statement, NULL) == SQLITE_OK) {
            sqlite3_exec(database, [query UTF8String], NULL, NULL, NULL);
        }
    }
    
    return YES;
}


-(BOOL)makeUpdateInDB:(NSString *)txn_No theDetails:(NSString *)txn_Details{
    
    sqlite3_stmt *statement = nil;
    NSString *query;
    NSArray *array=NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *filePath=[array objectAtIndex:0];
    //    if ([OTAStatus isEqualToString:@"Y"]) {
    filePath =[filePath stringByAppendingString:[NSString stringWithFormat:@"/Assets/apps/%@/sqlite/Notes.sqlite3",shared.appIDNotes]];
    //    }else{
    //        filePath =[filePath stringByAppendingPathComponent:@"Notes.sqlite3"];
    //    }
    NSFileManager *manager=[NSFileManager defaultManager];
    BOOL success = NO;
    if ([manager fileExistsAtPath:filePath])
    {
        success =YES;
    }
    if (!success)
    {
        NSString *path2=[[[MiscellaneousMethods getAppBundle] resourcePath] stringByAppendingString:[NSString stringWithFormat:@"/Assets/apps/%@/sqlite/Notes.sqlite3",shared.appIDNotes]];
        [manager copyItemAtPath:path2 toPath:filePath error:nil];
    }
    if (sqlite3_open([filePath UTF8String], &database) == SQLITE_OK) {
        query = [NSString stringWithFormat:@"UPDATE tb_Notes SET txn_Details = '%@' WHERE txn_No = '%@' ",txn_Details,txn_No];
        if (sqlite3_prepare_v2(database, [query UTF8String], -1, &statement, NULL) == SQLITE_OK) {
            sqlite3_exec(database, [query UTF8String], NULL, NULL, NULL);
        }
    }
    
    
    
    
    return YES;
}

-(IBAction)done{
    if ([readWrite isEqualToString:@"w"]) {
        if (![[[notesText text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]isEqualToString:@""]) {
            [self makeEntryInDB:txnNo theDetails:[notesText text]];
        }
        else{
            // printf(@"empty string");
        }
        NSMutableArray *txn_No = [[NSMutableArray alloc] initWithArray:[self getTxnNoFromDB]];
        
        int count=0;
        for (NSString *str in txn_No) {
            if ([str caseInsensitiveCompare:txnNo] == NSOrderedSame) {
                count = 1;
            }
        }
        if (count == 1) {
            [self makeUpdateInDB:txnNo theDetails:[notesText text]];
        }
    }
    
    [self dismissViewControllerAnimated:YES completion:nil];
    
}


@end


