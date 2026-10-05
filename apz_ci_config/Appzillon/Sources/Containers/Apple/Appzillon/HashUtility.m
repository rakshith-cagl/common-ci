//
//  HashUtility.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 03/02/18.
//

#import "HashUtility.h"
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonCrypto.h>
#import <APPZILLONPRODUCTNAME-Swift.h>
@implementation HashUtility
#pragma mark - login
+(int) fromHex: (char) c {
    if (c >= '0' && c <= '9') {
        return c - '0';
    }
    if (c >= 'A' && c <= 'F') {
        return c - 'A' + 10;
    }
    if (c >= 'a' && c <= 'f') {
        return c - 'a' + 10;
    }
    else
    {
        return c;
    }
}

+(char) toHex : (int) nybble {
    if(nybble < 0 || nybble > 15)
    {
        return 'R';
    }
    NSString * str1= @"0123456789ABCDEF";
    char  c= [str1 characterAtIndex:nybble];
    return c;
}

+(void) xorHex:(NSString *) a : (NSString * ) b {
    NSUInteger i=[a length];
    char chars[i] ;
    for(int j=0;j<i;j++)
    {
        char f=[a characterAtIndex:j];
        char m=[b characterAtIndex:j];
        chars[j]= [self toHex:[self fromHex:f ] ^ [self fromHex:m]];
    }
    chars[i]='\0';
}

+(NSString *) genHash:(NSString *) pin : (NSString * ) salt
{
    NSMutableString *pTextStr = [NSMutableString stringWithString:@""];
    [pTextStr appendString:pin];
    [pTextStr appendString:salt];
    NSData *pTextData = [pTextStr dataUsingEncoding:NSASCIIStringEncoding];
    NSMutableData * macOut = [NSMutableData dataWithLength:CC_SHA256_DIGEST_LENGTH];
    CC_SHA256(pTextData.bytes,(CC_LONG)pTextData.length,macOut.mutableBytes);
    NSString * strin1 = [HashUtility getHashStringFromData:macOut];
//    NSString * strin1 = [macOut description];
//    strin1= [strin1 stringByReplacingOccurrencesOfString:@" " withString:@""];
//    strin1= [strin1 stringByReplacingOccurrencesOfString:@"<" withString:@""];
//    strin1= [strin1 stringByReplacingOccurrencesOfString:@">" withString:@""];
    return strin1;
}
+(NSString *) genHashforJSON:(NSString *) pin : (NSString * ) salt
{
    NSMutableString *pTextStr = [NSMutableString stringWithString:@""];
    [pTextStr appendString:pin];
    [pTextStr appendString:salt];
    NSData *pTextData = [pTextStr dataUsingEncoding:NSUTF8StringEncoding];
    NSMutableData * macOut = [NSMutableData dataWithLength:CC_SHA256_DIGEST_LENGTH];
    CC_SHA256(pTextData.bytes,(CC_LONG)pTextData.length,macOut.mutableBytes);
    NSString * strin1 = [HashUtility getHashStringFromData:macOut];
//    NSString * strin1 = [macOut description];
//    strin1= [strin1 stringByReplacingOccurrencesOfString:@" " withString:@""];
//    strin1= [strin1 stringByReplacingOccurrencesOfString:@"<" withString:@""];
//    strin1= [strin1 stringByReplacingOccurrencesOfString:@">" withString:@""];
    return strin1;
}
+(NSString *) genHex:(NSString *) pusername : (NSString * ) ppin :(NSString *) puid : (NSString * ) pdtstr {
    NSString * uname = pusername;
    NSMutableString * inpin = [ppin mutableCopy];
    NSString * imie = puid;
    NSString * dtstr  = pdtstr;
    NSString * day = [dtstr substringToIndex:3];
    NSString * hr = [dtstr substringWithRange:NSMakeRange(16, 2)];
    NSString * min = [dtstr substringWithRange:NSMakeRange(19, 2)];
    NSString * sec = [dtstr substringWithRange:NSMakeRange(22, 2)];
    NSString * yr = [dtstr substringWithRange:NSMakeRange(13, 2)];
    NSString * dd = [dtstr substringWithRange:NSMakeRange(5, 2)];
    NSString * mm = [dtstr substringWithRange:NSMakeRange(8, 2)];
    NSMutableString * concatstr=[hr mutableCopy];
    [concatstr appendString:min];
    [concatstr appendString:day];
    [concatstr appendString:yr];
    [concatstr appendString:dd];
    [concatstr appendString:mm];
    [concatstr appendString:sec];
    [concatstr appendString:uname];
    [concatstr appendString:imie];
    NSData * dataIn =[concatstr dataUsingEncoding:NSASCIIStringEncoding];
    NSMutableData * macOut = [NSMutableData dataWithLength:CC_SHA256_DIGEST_LENGTH];
    CC_SHA256(dataIn.bytes,(CC_LONG)dataIn.length,macOut.mutableBytes);
    NSData * dataIn1 =[inpin dataUsingEncoding:NSASCIIStringEncoding];
    NSMutableData * macOut1 = [NSMutableData dataWithLength:CC_SHA256_DIGEST_LENGTH];
    CC_SHA256(dataIn1.bytes,(CC_LONG)dataIn1.length,macOut1.mutableBytes);
    NSString * strin1 = [HashUtility getHashStringFromData:macOut];
//    NSString * strin1 = [macOut description];
//    strin1= [strin1 stringByReplacingOccurrencesOfString:@" " withString:@""];
//    strin1= [strin1 stringByReplacingOccurrencesOfString:@"<" withString:@""];
//    strin1= [strin1 stringByReplacingOccurrencesOfString:@">" withString:@""];
    NSString * strin2 = [HashUtility getHashStringFromData:macOut1];
//    NSString * strin2 = [macOut1 description];
//    strin2= [strin2 stringByReplacingOccurrencesOfString:@" " withString:@""];
//    strin2= [strin2 stringByReplacingOccurrencesOfString:@"<" withString:@""];
//    strin2= [strin2 stringByReplacingOccurrencesOfString:@">" withString:@""];
    NSUInteger i1=[strin1 length];
    char chars1[i1] ;
    for(int j=0;j<i1;j++)
    {
        char f=[strin1 characterAtIndex:j];
        char m=[strin2 characterAtIndex:j];
        chars1[j]= [self toHex:[self fromHex:f ] ^ [self fromHex:m]];
    }
    chars1[i1]='\0';
    NSString * strout = [NSString stringWithUTF8String:chars1];
    strout = [strout substringToIndex:64];
    NSString * fbyte = [strout substringToIndex:16];
    NSString * lbyte = [strout substringFromIndex:48];
    NSUInteger i2=[fbyte length];
    char chars2[i2] ;
    for(int j=0;j<i2;j++)
    {
        char f=[fbyte characterAtIndex:j];
        char m=[lbyte characterAtIndex:j];
        chars2[j]= [self toHex:[self fromHex:f ] ^ [self fromHex:m]];
    }
    chars2[i2]='\0';
    NSString * fblbxor = [NSString stringWithUTF8String:chars2];
    fblbxor=[fblbxor substringToIndex:16];
    NSUInteger i3=[fblbxor length];
    char chars3[i3] ;
    for(int j=0;j<i3;j++)
    {
        char f=[fblbxor characterAtIndex:j];
        char m=[strin2 characterAtIndex:j];
        chars3[j]= [self toHex:[self fromHex:f ] ^ [self fromHex:m]];
    }
    chars3[i3]='\0';
    NSString * hshxor2 = [NSString stringWithUTF8String:chars3];
    hshxor2=[hshxor2 substringToIndex:16];
    NSString * fbyte2 = [hshxor2 substringToIndex:8];
    NSString * lbyte2 = [hshxor2 substringFromIndex:8];
    NSUInteger i4=[fbyte2 length];
    char chars4[i4] ;
    for(int j=0;j<i4;j++)
    {
        char f=[fbyte2 characterAtIndex:j];
        char m=[lbyte2 characterAtIndex:j];
        chars4[j]= [self toHex:[self fromHex:f ] ^ [self fromHex:m]];
    }
    chars4[i4]='\0';
    NSString * fblbxor2 = [NSString stringWithUTF8String:chars4];
    fblbxor2=[fblbxor2 substringToIndex:8];
    NSUInteger i5=[fblbxor2 length];
    char chars5[i5] ;
    for(int j=0;j<i5;j++)
    {
        char f=[fblbxor2 characterAtIndex:j];
        char m=[strin2 characterAtIndex:j];
        chars5[j]= [self toHex:[self fromHex:f ] ^ [self fromHex:m]];
    }
    chars5[i5]='\0';
    NSString * finalstr = [NSString stringWithUTF8String:chars5];
    finalstr=[finalstr substringToIndex:8];
    [Base64 initialize];
    NSData * finalData = [finalstr dataUsingEncoding:NSUTF8StringEncoding];
    NSString * finalstr1 = [Base64 encode:finalData];
    finalstr1=[finalstr1 substringToIndex:12];
//    NSString * newImie = [imie stringByAppendingFormat:@"00000000000000000"];
//    NSData * finalData1 = [newImie dataUsingEncoding:NSUTF8StringEncoding];
//    NSString * baseenc = [Base64 encode:finalData1];
//    NSData * finalData2 = [Base64 decode:baseenc];
//    NSString * basedec = [[NSString alloc] initWithData:finalData2 encoding:NSASCIIStringEncoding];
    return finalstr1;
}

+(NSString *)getHashStringFromData :(NSData *)data{
    NSUInteger capacity = data.length * 2;
    NSMutableString *sbuf = [NSMutableString stringWithCapacity:capacity];
    const unsigned char *buf = data.bytes;
    NSInteger i;
    for (i=0; i<data.length; ++i) {
        [sbuf appendFormat:@"%02lx", (unsigned long)buf[i]];
    }
    NSString* strin1 = [NSString stringWithString:sbuf];
    
    strin1= [strin1 stringByReplacingOccurrencesOfString:@" " withString:@""];
    strin1= [strin1 stringByReplacingOccurrencesOfString:@"<" withString:@""];
    strin1= [strin1 stringByReplacingOccurrencesOfString:@">" withString:@""];
//    NSLog(@"String 1 %@",strin1);
    return  strin1;
}

@end
