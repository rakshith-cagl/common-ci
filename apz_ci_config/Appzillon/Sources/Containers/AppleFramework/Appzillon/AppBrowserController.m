//
//  AppBrowserController.m
//  Appzillon
//
//  Created by Admin on 10/09/13.
//
//

#import "AppBrowserController.h"
#import "FilesViewController.h"
#import "Constants.h"
#import "MiscellaneousMethods.h"

@interface AppBrowserController()<FileSelectedDelegate>
@property(nonatomic,strong)NSString *selectedDir;
@property(nonatomic,strong)NSDictionary *filedict;
@property(nonatomic,strong)NSString *openType;
@property(nonatomic,strong)NSString *selectedFilePath;
@property(nonatomic,strong)NSString *selectedFile;
@property(nonatomic,strong)NSString *fileFilters;
@end

@implementation AppBrowserController
@synthesize appID;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void)initDirectories:(NSDictionary *)fileDictionaries openType:(NSString *)type filters:(NSString*)fileFilters{
    self.filedict = fileDictionaries;
    self.openType = type;
    self.fileFilters=fileFilters;
}

- (void)viewDidLoad
{
    self.title = @"App Browser";
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancel)];
    self.selectedFile = @"-";
    if([self.openType isEqualToString:@"N"]){
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(done)];
    }
    [super viewDidLoad];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - Table view data source
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    if([self.openType isEqualToString:@"N"])
    {
        if(section == 0){
            return @"Selected File";
        }
        else{
            return @"Directories";
        }
    }
    else{
        return @"Directories";
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if([self.openType isEqualToString:@"N"]){
        return 2;
    }
    else{
        return 1;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if([self.openType isEqualToString:@"N"])
    {
        if(section == 0)
        {
            return 1;
        }
        else{
            return self.filedict.count;
        }
    }
    else
    {
        return self.filedict.count;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;
    NSString *cellText;
    if([self.openType isEqualToString:@"N"])
    {
        if(indexPath.section == 0){
            static NSString *selectedIdentifier = @"SelectedIdentifier";
            cell = [tableView dequeueReusableCellWithIdentifier:selectedIdentifier];
            if (cell == nil) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:selectedIdentifier];
            }
            //            cell.textLabel.text = self.selectedFile;
            cellText=self.selectedFile;
        }
        else{
            static NSString *cellIdentifier = @"CellIdentifier";
            cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
            if (cell == nil) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier];
            }
            //            cell.textLabel.text = [[self.filedict allKeys] objectAtIndex:indexPath.row];
            cellText =[[self.filedict allKeys] objectAtIndex:indexPath.row];
            //            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        }
    }
    else
    {
        static NSString *cellIdentifier = @"CellIdentifier";
        cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
        if (cell == nil) {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier];
        }
        //        cell.textLabel.text = [[self.filedict allKeys] objectAtIndex:indexPath.row];
        //        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cellText=[[self.filedict allKeys] objectAtIndex:indexPath.row];
    }
    UIImageView *imgView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 48, 48)]; // your cell's height should be greater than 48 for this.
    imgView.tag = 1;
    [cell.contentView addSubview:imgView];
    imgView = nil;
    
    UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(50, 0, self.view.frame.size.width-50, 50)];
    
    lbl.backgroundColor = [UIColor clearColor];
    [lbl setTag:2];
    [cell.contentView addSubview:lbl];
    lbl = nil;
    UILabel *_lbl = (UILabel *)[cell.contentView viewWithTag:2];
    _lbl.text =  cellText;
    UIImageView *_imgView = (UIImageView *)[cell.contentView viewWithTag:1];
    _imgView.image = [UIImage imageNamed:[FILEBROWSER_ICON_FOLDER stringByAppendingPathComponent:[MiscellaneousMethods getItemsImage:cellText]]];
    return cell;
}

#pragma mark - TableView Delegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    self.selectedDir = [[self.filedict allKeys]objectAtIndex:indexPath.row];
    NSArray *filesArr = [self.filedict valueForKey:self.selectedDir];
    NSString *finalDir = [self appDocumentsDirectory];
    finalDir = [finalDir stringByAppendingPathComponent: self.selectedDir];
    FilesViewController *fileController = [[FilesViewController alloc]init];
    fileController.delegate = self;
    if([filesArr isEqual:@"createFile"]){
        NSString*selectedFile = [self.filedict valueForKey:self.selectedDir];
        [fileController initCreateFiles:selectedFile directory:finalDir openType:self.openType fileFilters:self.fileFilters];
    }
    else
    {
        [fileController initFiles:filesArr directory:finalDir openType:self.openType fileFilters:self.fileFilters];
    }
    [fileController initFiles:filesArr directory:finalDir openType:self.openType fileFilters:self.fileFilters];
    [self.navigationController pushViewController:fileController animated:YES];
}

#pragma mark - Selected File Display
-(void)selectedFile:(NSString *)fileName path:(NSString *)filePath{
    self.selectedFile = fileName;
    self.selectedFilePath = filePath;
    [self.tableView reloadData];
}
-(void)selectedSingleFile:(NSString *)fileName path:(NSString *)filePath{
    self.selectedFile = fileName;
    self.selectedFilePath = filePath;
    [self.tableView reloadData];
    [self done];
    
}
#pragma mark - Done AppBrowser
-(void)done{
    [self dismissViewControllerAnimated:YES completion:^{
        [self.delegate selectedFile:self.selectedFilePath :self.interfaceOrientation];
    }];
}

#pragma mark - Cancel AppBrowser
-(void)cancel{
    [self dismissViewControllerAnimated:YES completion:^{
        [self.delegate cancelBrowser:self.interfaceOrientation];
    }];
}

-(NSString *)DocumentsDirectory{
    NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    return [[NSString alloc] initWithString:[paths objectAtIndex:0]];
}
-(NSString *)appDocumentsDirectory{
    NSArray *dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *fileOpnDir= [[dirPaths objectAtIndex:0] stringByAppendingFormat:@"/Assets/apps/%@/",appID];
    return fileOpnDir;
    
}


@end
