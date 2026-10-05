//Copyright (c) 2021 Appzillon. All rights reserved.

#import <Foundation/Foundation.h>
#import "APZJBDetector.h"
#import "CryptoUtility.h"
#import <APPZILLONPRODUCTNAME-Swift.h>
#include <sys/stat.h>

@implementation APZJBDetector : NSObject

-(BOOL)checkForJailBrokenDevice{
#if !(TARGET_IPHONE_SIMULATOR)
    
    NSString *isJB = @"76f6243716d4029726022224a43796220237960256d616e40247365746f627050237968645";
    NSMutableString *a = [NSMutableString new];
    while([isJB length] != [a length]){
        NSRange range = NSMakeRange([isJB length]-[a length]-1,1);
        [a appendString:[isJB substringWithRange:range]];
    }
    NSMutableString *b = [[NSMutableString alloc]init];
    int c = 0;
    while(c < [a length]){
        NSString *d = [a substringWithRange:NSMakeRange(c,2)];
        int e = 0;
        sscanf([d cStringUsingEncoding:NSASCIIStringEncoding],"%x",&e);
        [b appendFormat:@"%c",(char)e];
        c += 2;
    }
    BOOL isDirectory;
    if ([[NSFileManager defaultManager] fileExistsAtPath:@"/Library/MobileSubstrate/CydiaSubstrate.dylib"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/private/var/log/syslog"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/private/var/cache/apt/"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/var/log/apt"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/private/var/Users/"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/libexec/cydia/firmware.sh"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/jb/libjailbreak.dylib"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/jb/amfid_payload.dylib"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/jb/jailbreakd.plist"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/var/lib/dpkg/info/mobilesubstrate.md5sums"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/etc/apt/undecimus/undecimus.list"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/share/jailbreak/injectme.plist"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/jb/offsets.plist"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/.installed_unc0ver"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/.cydia_no_stash"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/jb/lzma"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/lib/libjailbreak.dylib"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/.bootstrapped_electra"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/etc/apt/sources.list.d/sileo.sources"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/etc/apt/sources.list.d/electra.list"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/private/var/lib/apt/"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/var/lib/apt"] ||  [[NSFileManager defaultManager] fileExistsAtPath:@"/var/tmp/cydia.log"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/var/lib/cydia"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/bin/sshd"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/bin/ssh"] || [[NSFileManager defaultManager] fileExistsAtPath:@"/bin/sh"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/bin.sh"]||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/private/etc/apt"]||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/private/etc/ssh/sshd_config"]||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/SBSetttings.app"]||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/libexec/cydia/"]||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/checkra1n.dmg"]||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/binpack"]||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/System/Library/LaunchDaemons/com.saurik.Cy@dia.Startup.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/SBSetttings.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/Cydia.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/RockApp.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/blackra1n.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/FakeCarrier.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/IntelliScreen.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/Icy.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/WinterBoard.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/SBSettings.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Applications/MxTube.app"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Library/MobileSubstrate/MobileSubstrate.dylib"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Library/PreferenceBundles/ShadowPreferences.bundle"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Library/PreferenceBundles/LibertyPref.bundle"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Library/PreferenceBundles/HideJBPrefs.bundle"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Library/PreferenceBundles/FlyJBPrefs.bundle"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Library/PreferenceBundles/ABypassPrefs.bundle"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/bin/bash"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/etc/clutch.conf"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/cache/clutch.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/etc/clutch_cracked.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/cache/clutch_cracked.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/lib/clutch/overdrive.dylib"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/root/Documents/Cracked/"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/cache/apt"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/private/var/stash"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/var/log/syslog"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/libexec/ssh-keysign"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/bin/cycript"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/sbin/frida-server"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/local/bin/cycript"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/lib/libcycript.dylib"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/System/Library/LaunchDaemons/com.saurik.Cydia.Startup.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/System/Library/LaunchDaemons/com.ikey.bbot.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Library/MobileSubstrate/DynamicLibraries/Veency.plist"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/libexec/sftp-server"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/usr/sbin/sshd"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/etc/apt"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/private/var/tmp/cydia.log"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/private/var/lib/cydia"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/private/var/lib/apt"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Cydia/Substrate"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/etc/ssh/sshd_config"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/private/var/mobile/Library/SBSettings/Themes"] ||
        [[NSFileManager defaultManager] fileExistsAtPath:@"/Library/MobileSubstrate/DynamicLibraries/LiveClock.plist"] ||
        [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"ryleyangus.com/repo/"]] ||
        [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"cydia://package/com.example.package"]] || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Cyd", @"ia.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"bla", @"ckra1n.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Fake", @"Carrier.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Liberty", @"Lite.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Liberty.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Lite.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"ex", @"con.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"fl", @"ex3.a", @"pp"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"excon", @"flex3.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Ic", @"y.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Inte", @"lliScreen.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"MxT", @"ube.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Roc", @"kApp.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"SBSet", @"ttings.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Wint", @"erBoard.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"pr", @"iva",@"te/v", @"ar/l", @"ib/a", @"pt/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"pr", @"iva",@"te/v", @"ar/l", @"ib/c", @"ydia/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"pr", @"iva",@"te/v", @"ar/mobile", @"Library/SBSettings", @"Themes/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"pr", @"iva",@"te/v", @"ar/t", @"mp/cyd", @"ia.log"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"pr", @"iva",@"te/v", @"ar/s", @"tash/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"us", @"r/l",@"ibe", @"xe", @"c/cy", @"dia/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"us", @"r/b",@"in", @"s", @"shd"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"us", @"r/sb",@"in", @"s", @"shd"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"us", @"r/l",@"ibe", @"xe", @"c/cy", @"dia/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@", @"us", @"r/l",@"ibe", @"xe", @"c/sftp-", @"server"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@",@"Syste",@"tem/Lib",@"rary/Lau",@"nchDae",@"mons/com.ike",@"y.bbot.plist"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@%@",@"Sy",@"stem/Lib",@"rary/Laun",@"chDae",@"mons/com.saur",@"ik.Cy",@"@dia.Star",@"tup.plist"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"Libr",@"ary/Mo",@"bileSubstra",@"te/MobileSubs",@"trate.dylib"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"va",@"r/c",@"ach",@"e/a",@"pt/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"va",@"r/l",@"ib",@"/apt/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"va",@"r/l",@"ib/c",@"ydia/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"va",@"r/l",@"og/s",@"yslog"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"private/va",@"r/c",@"ach",@"e/a",@"pt/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"private/va",@"r/l",@"ib",@"/apt/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"private/va",@"r/l",@"ib/c",@"ydia/"] isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@", @"private/va",@"r/l",@"og/s",@"yslog"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"bi",@"n/b",@"ash"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"b",@"in/",@"sh"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"private/et",@"c/a",@"pt/"]isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"et",@"c/a",@"pt/"]isDirectory:&isDirectory]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"private/etc/s",@"sh/s",@"shd_config"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"etc/s",@"sh/s",@"shd_config"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@", @"us",@"r/li",@"bexe",@"c/ssh-k",@"eysign"]]
        || [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"cydia://package/com.masbog.com"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"App", @"lic",@"ati", @"ons/", @"Snoop-it", @" Config.a", @"pp"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/", @" xCon.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/", @" excon.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/", @" flex3.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/", @" Liberty.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/", @" Lite.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/",@"Liberty", @"Lite.", @"dylib"]]
        ||[[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@%@%@%@%@%@", @"Library/MobileS", @"ubstrate/Dy",@"nami", @"cLi", @"braries/",@" Liberty",@" Lite.", @"dylib"]]
        || [[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithFormat:@"/%@%@%@", @"priv",@"ate/etc/dpkg/",@"origins/debian"]])  {
        return YES;
    }
    
    //Try file is readable in respective paths
    if ([[NSFileManager defaultManager] isReadableFileAtPath:@"/.installed_unc0ver"] || [[NSFileManager defaultManager] isReadableFileAtPath:@"/.bootstrapped_electra"] || [[NSFileManager defaultManager] isReadableFileAtPath:@"/Applications/Cydia.app"] || [[NSFileManager defaultManager] isReadableFileAtPath:@"/Library/MobileSubstrate/MobileSubstrate.dylib"] || [[NSFileManager defaultManager] isReadableFileAtPath:@"/etc/apt"] || [[NSFileManager defaultManager] isReadableFileAtPath:@"/var/log/apt"] || [[NSFileManager defaultManager] isReadableFileAtPath:@"/bin/bash"] || [[NSFileManager defaultManager] isReadableFileAtPath:@"/usr/sbin/sshd"] || [[NSFileManager defaultManager] isReadableFileAtPath:@"/usr/bin/ssh"]) {
        return YES;
    }
    
    //Symbolic link verification
    struct stat s;
    if(lstat("/Applications", &s) || lstat("/var/stash/Library/Ringtones", &s) || lstat("/var/stash/Library/Wallpaper", &s)
       || lstat("/var/stash/usr/include", &s) || lstat("/var/stash/usr/libexec", &s)  || lstat("/var/stash/usr/share", &s) || lstat("/var/stash/usr/arm-apple-darwin9", &s) || lstat("/var/lib/undecimus/apt", &s))
    {
        if(s.st_mode & S_IFLNK){
            return YES;
        }
    }
    
    //Try to write file in private
    NSError *error;
    FILE *f = NULL ;
    if ((f = fopen("/bin/bash", "r")) ||
        (f = fopen("/bin/sh", "r")) ||
        (f = fopen("/Applications/Cydia.app", "r")) ||
        (f = fopen("/Library/MobileSubstrate/MobileSubstrate.dylib", "r")) ||
        (f = fopen("/usr/sbin/sshd", "r")) ||
        (f = fopen("/usr/bin/sshd", "r")) ||
        (f = fopen("/etc/apt", "r")))  {
        fclose(f);
        return YES;
    }
    fclose(f);
    
    NSError *error1;
    NSString *stringToBeWritten = @"This is a test.";
    NSString *encryptKey = [KeychainUtility.shared generateRandomString];
    [[CryptoSwiftManager.shared encryptPlainStringWithKey:encryptKey value:stringToBeWritten]
     writeToFile:@"/private/jailbreak.txt" atomically:YES
                          encoding:NSUTF8StringEncoding error:&error1];
    if(error1==nil){
        return YES;
    } else {
        //Device is not jailbroken
        [[NSFileManager defaultManager] removeItemAtPath:@"/private/jailbreak.txt" error:nil];
    }
    
    NSArray *blah = [NSArray arrayWithObjects:@"f28637164737f2271667f2", @"f28637164737f2271667f256471667962707f2", @"f28637164737f22646f2271667f256471667962707f2", nil];
    NSMutableString *hihi = [NSMutableString new];
    
    while ([blah[0] length]!=[hihi length]) {
        NSRange range = NSMakeRange([blah[0] length]-[hihi length]-1, 1);
        [hihi appendString: [blah[0] substringWithRange:range]];
    }
    
    NSMutableString *haha = [[NSMutableString alloc] init];
    int i = 0;
    while (i < [hihi length])
    {
        NSString *hehe = [hihi substringWithRange: NSMakeRange(i, 2)];
        int value = 0;
        sscanf([hehe cStringUsingEncoding:NSASCIIStringEncoding], "%x", &value);
        [haha appendFormat:@"%c", (char)value];
        i+=2;
    }
    
    NSArray *hahaList = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:haha error:nil];
    if (hahaList.count > 0) {
        for (NSString *fufufufu in hahaList){
            if (![fufufufu containsString:@"lnk"]) {
                NSArray *hahaListSub = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:[NSString stringWithFormat:@"%@%@/DynamicLibraries", haha, fufufufu] error:nil];
                for (NSString *wkwkwkwk in hahaListSub){
                    if ([wkwkwkwk containsString:@".dylib"] || [wkwkwkwk containsString:@".plist"]) {
                        return YES;
                    }
                }
            }
        }
    }
    
    //============== array index 1 ===========//
    hihi = [NSMutableString new];
    while ([blah[1] length]!=[hihi length]) {
        NSRange range = NSMakeRange([blah[1] length]-[hihi length]-1, 1);
        [hihi appendString: [blah[1] substringWithRange:range]];
    }
    
    haha = [[NSMutableString alloc] init];
    i = 0;
    while (i < [hihi length])
    {
        NSString *hehe = [hihi substringWithRange: NSMakeRange(i, 2)];
        int value = 0;
        sscanf([hehe cStringUsingEncoding:NSASCIIStringEncoding], "%x", &value);
        [haha appendFormat:@"%c", (char)value];
        i+=2;
    }
    
    hahaList = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:haha error:nil];
    if (hahaList.count > 0) {
        for (NSString *fufufufu in hahaList){
            if (![fufufufu containsString:@"lnk"]) {
                NSArray *hahaListSub = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:[NSString stringWithFormat:@"%@%@/DynamicLibraries", haha, fufufufu] error:nil];
                for (NSString *wkwkwkwk in hahaListSub){
                    if ([wkwkwkwk containsString:@".dylib"] || [wkwkwkwk containsString:@".plist"]) {
                        return YES;
                    }
                }
            }
        }
    }
    
    
    //============== array index 2 ===========//
    hihi = [NSMutableString new];
    while ([blah[2] length]!=[hihi length]) {
        NSRange range = NSMakeRange([blah[2] length]-[hihi length]-1, 1);
        [hihi appendString: [blah[2] substringWithRange:range]];
    }
    
    haha = [[NSMutableString alloc] init];
    i = 0;
    while (i < [hihi length])
    {
        NSString *hehe = [hihi substringWithRange: NSMakeRange(i, 2)];
        int value = 0;
        sscanf([hehe cStringUsingEncoding:NSASCIIStringEncoding], "%x", &value);
        [haha appendFormat:@"%c", (char)value];
        i+=2;
    }
    
    hahaList = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:haha error:nil];
    if (hahaList.count > 0) {
        for (NSString *fufufufu in hahaList){
            if (![fufufufu containsString:@"lnk"]) {
                NSArray *hahaListSub = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:[NSString stringWithFormat:@"%@%@/DynamicLibraries", haha, fufufufu] error:nil];
                for (NSString *wkwkwkwk in hahaListSub){
                    if ([wkwkwkwk containsString:@".dylib"] || [wkwkwkwk containsString:@".plist"]) {
                        return YES;
                    }
                }
            }
        }
    }
    
    // Check if the app can open a Cydia's URL scheme
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"cydia://package/com.example.package"]]||[[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"cydia://"]]||[[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"undecimus://"]]||[[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"sileo://"]]||[[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"zbra://"]]||[[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"filza://"]]||[[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"activator://"]]){
        return YES;
    }
    // Check cydia URL
    if([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"cydia://package/com.avl.com"]]) {
        return YES;
    }
#endif
    return NO;
}

@end
