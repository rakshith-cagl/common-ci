//
//  CustomAnnotationView.m
//  Appzillon
//
//  Created by Admin on 20/08/13.
//
//

#import "CustomAnnotationView.h"

@implementation CustomAnnotationView

- (id)initWithAnnotation:(id <MKAnnotation>)annotation reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithAnnotation:annotation reuseIdentifier:reuseIdentifier];
    return self;
}


-(void)dealloc{
}

@end

