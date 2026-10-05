//
//  APZSignatureView.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 10/10/14.
//
//

#import "APZSignatureView.h"

@implementation APZSignatureView
@synthesize pathView;

- (id)initWithFrame:(CGRect)frame {
    if ((self = [super initWithFrame:frame])) {
    }
    return self;
}

- (void)drawRect:(CGRect)rect {
    [[UIColor blackColor] setStroke];
    [pathView stroke];
}


@end

