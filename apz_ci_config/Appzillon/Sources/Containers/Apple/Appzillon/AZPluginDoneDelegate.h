//Copyright (c) 2021 Appzillon. All rights reserved.

@protocol AZPluginDoneDelegate <NSObject>
-(void)donePlugin:(id)sender;
-(void)donePluginWithOrientaion:(id)sender :(UIInterfaceOrientation)interfaceOrientation;
@end
