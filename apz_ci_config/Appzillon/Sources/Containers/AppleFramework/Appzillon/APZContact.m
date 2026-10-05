//
//  APZContact.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZContact.h"
#import <AddressBook/AddressBook.h>
#import "APZJsonUtil.h"
#import "Constants.h"
#import "Logger.h"
#import <AddressBookUI/AddressBookUI.h>
#import "AppzillonViewController.h"
#import <UIKit/UIKit.h>
@interface APZContact() <ABPeoplePickerNavigationControllerDelegate>
@property(nonatomic,strong)WKWebView *webView;
@property(nonatomic,weak)AppzillonViewController *viewController;
@property (nonatomic, strong) ABPeoplePickerNavigationController *addressBookController;
@property (nonatomic, strong) NSMutableArray *arrContactsData;
@property(nonatomic,strong)NSString* pluginId;
@end

@implementation APZContact

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
        
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZContact--execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    NSString *opnType = [jsonDict objectForKey:@"opnType"];
    opnType = [opnType lowercaseString];
    if ([opnType isEqualToString:@"add"]) {
        [self addContact:[jsonDict objectForKey:CONTACT_DETAILS]];
    }
    else  if ([opnType isEqualToString:@"edit"]){
        [self editContact:[jsonDict objectForKey:CONTACT_DETAILS] searchCriteria:[jsonDict objectForKey:CONTACT_SEARCHCRITERIA]];
    }
    else  if ([opnType isEqualToString:@"delete"]) {
        [self deleteContact:[jsonDict objectForKey:CONTACTS_DELETECRITERIA]];
    }
    else  if ([opnType isEqualToString:@"search"]) {
        [self contactsSearch:[jsonDict objectForKey:CONTACT_SEARCHCRITERIA]];
    }
    else  if ([opnType isEqualToString:@"fetch"]) {
        [self contatsFetch:[jsonDict objectForKey:CONTACT_SEARCHCRITERIA]];
    }
    else{
        [Logger logger_Log:@"E" :@"APZContact--Suceess"];
    }
}

#pragma mark - Contacts
#pragma mark Contacts ios 6 Contacts Privacy Settings
-(BOOL)isABAddressBookCreateWithOptionsAvailable {
    return &ABAddressBookCreateWithOptions != NULL;
}

#pragma mark - Contacts ADD
-(void)addContact:(NSDictionary *)details{
    ABAddressBookRef addressBook;
    if ([self isABAddressBookCreateWithOptionsAvailable]){
        CFErrorRef error = nil;
        addressBook = ABAddressBookCreateWithOptions(NULL,&error);
        if(ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusNotDetermined){
            ABAddressBookRequestAccessWithCompletion(addressBook, ^(bool granted, CFErrorRef error) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    if(granted){
                        [self addContact:details withAddressBook:addressBook];
                        if(addressBook != NULL){
                            CFRelease(addressBook);
                        }
                    }
                    else{
                        [self noAddressBookFoundSearch];
                    }
                });
            });
        }
        else if(ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusAuthorized){
            [self addContact:details withAddressBook:addressBook];
            if(addressBook != NULL){
                CFRelease(addressBook);
            }
        }
        else{
            [self noAddressBookFoundSearch];
        }
    }
    else{
        addressBook = ABAddressBookCreate();
        [self addContact:details withAddressBook:addressBook];
        if(addressBook != NULL){
            CFRelease(addressBook);
        }
    }
}


-(void)addContact:(NSDictionary *)details withAddressBook:(ABAddressBookRef)addrBook{
    ABAddressBookRef addressBook = addrBook;
    if(addressBook == NULL){
        [self noAddressBookFoundAdd];
        return;
    }
    ABRecordRef contact = ABPersonCreate();
    if (contact == NULL){
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_CREATION_FAILED_CODE],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        
        [Logger logger_Log:@"E" :@"APZContact--Unable to Create New Contacts"];
        return;
    }
    BOOL couldSetFirstName = NO;
    BOOL couldSetLastName = NO;
    BOOL couldSetNickName = NO;
    CFErrorRef setFirstNameError = NULL;
    CFErrorRef setLastNameError = NULL;
    CFErrorRef setNickNameError = NULL;
    couldSetFirstName = ABRecordSetValue(contact,kABPersonFirstNameProperty,
                                         (__bridge CFTypeRef)[details objectForKey:CONTACT_DETAILS_FIRSTNAME],
                                         &setFirstNameError);
    couldSetLastName = ABRecordSetValue(contact,kABPersonLastNameProperty,
                                        (__bridge CFTypeRef)[details objectForKey:CONTACT_DETAILS_LASTNAME],&setLastNameError);
    couldSetNickName = ABRecordSetValue(contact,kABPersonNicknameProperty,
                                        (__bridge CFTypeRef)[details objectForKey:CONTACT_DETAILS_NICKNAME],&setNickNameError);
    BOOL hasNoPhoneHome = [[details objectForKey:CONTACT_DETAILS_PHONEHOME] isEqualToString:@""];
    BOOL hasNoPhoneWork = [[details objectForKey:CONTACT_DETAILS_PHONEWORK] isEqualToString:@""];
    BOOL hasNoPhoneMobile =[[details objectForKey:CONTACT_DETAILS_PHONEMOBILE] isEqualToString:@""];
    BOOL hasNoPhone = (hasNoPhoneWork && hasNoPhoneHome && hasNoPhoneMobile);
    if(hasNoPhone == NO){
        ABMutableMultiValueRef phoneRef = ABMultiValueCreateMutable(kABMultiStringPropertyType);
        ABMultiValueIdentifier phoneValueIdentifier;
        if(hasNoPhoneHome == NO){
            ABMultiValueAddValueAndLabel(phoneRef,(__bridge CFTypeRef)([details objectForKey:CONTACT_DETAILS_PHONEHOME]),kABHomeLabel, &phoneValueIdentifier);
        }
        if(hasNoPhoneWork == NO){
            ABMultiValueAddValueAndLabel(phoneRef,(__bridge CFTypeRef)([details objectForKey:CONTACT_DETAILS_PHONEWORK]),kABWorkLabel, &phoneValueIdentifier);
        }
        if(hasNoPhoneMobile == NO){
            ABMultiValueAddValueAndLabel(phoneRef,(__bridge CFTypeRef)([details objectForKey:CONTACT_DETAILS_PHONEMOBILE]),kABPersonPhoneMobileLabel, &phoneValueIdentifier);
        }
        CFErrorRef phoneError = NULL;
        ABRecordSetValue(contact, kABPersonPhoneProperty, phoneRef, &phoneError);
        if (phoneRef!=NULL) {
            CFRelease(phoneRef);
        }
        
    }
    else{
        [Logger logger_Log:@"E" :@"APZContact--Phone is empty"];
    }
    BOOL hasNoMail = [[details objectForKey:CONTACT_DETAILS_MAIL] isEqualToString:@""];
    if(hasNoMail == NO){
        ABMutableMultiValueRef mailRef = ABMultiValueCreateMutable(kABMultiStringPropertyType);
        ABMultiValueIdentifier mailValueIdentifier;
        CFErrorRef anError = NULL;
        ABMultiValueAddValueAndLabel(mailRef, (__bridge CFTypeRef)([details objectForKey:CONTACT_DETAILS_MAIL]),
                                     kABWorkLabel, &mailValueIdentifier);
        ABRecordSetValue(contact, kABPersonEmailProperty, mailRef, &anError);
        if (mailRef!=NULL) {
            CFRelease(mailRef);
        }
    }
    else{
        [Logger logger_Log:@"E" :@"APZContact--No Mail present"];
    }
    BOOL hasNoAddress = [[details objectForKey:CONTACT_DETAILS_ADDRESS] isEqualToString:@""];
    if(hasNoAddress == NO){
        ABMutableMultiValueRef addrRef = ABMultiValueCreateMutable(kABMultiDictionaryPropertyType);
        NSMutableDictionary *addrDict = [[NSMutableDictionary alloc] init];
        [addrDict setObject:[details objectForKey:CONTACT_DETAILS_ADDRESS] forKey:(NSString *)kABPersonAddressStreetKey];
        ABMultiValueAddValueAndLabel(addrRef, (__bridge CFTypeRef)(addrDict), kABWorkLabel, NULL);
        ABRecordSetValue(contact, kABPersonAddressProperty,addrRef , NULL);
        
        if ((addrRef)!=NULL) {
            CFRelease(addrRef);
        }
    }
    else{
        [Logger logger_Log:@"E" :@"APZContact--No Address present"];
    }
    BOOL hasNoWebsite = [[details objectForKey:CONTACT_DETAILS_WEBSITE] isEqualToString:@""];
    if(hasNoWebsite == NO){
        ABMutableMultiValueRef websiteRef = ABMultiValueCreateMutable(kABMultiStringPropertyType);
        ABMultiValueIdentifier websiteIdentifier;
        CFErrorRef anError = NULL;
        ABMultiValueAddValueAndLabel(websiteRef, (__bridge CFTypeRef)([details objectForKey:CONTACT_DETAILS_WEBSITE]),kABWorkLabel, &websiteIdentifier);
        ABRecordSetValue(contact, kABPersonURLProperty, websiteRef, &anError);
        
        if ((websiteRef)!=NULL) {
            CFRelease(websiteRef);
        }
    }
    else{
        [Logger logger_Log:@"E" :@"APZContact--No Website present"];
    }
    BOOL hasNoBirthday = [[details objectForKey:CONTACT_DETAILS_BIRTHDAY] isEqualToString:@""];
    if(hasNoBirthday == NO){
        NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:[self getUserDateFormat]];
        CFErrorRef anError = NULL;
        NSDate *date = [formatter dateFromString:[NSString stringWithFormat:@"%@",[details objectForKey:CONTACT_DETAILS_BIRTHDAY]]];
        ABRecordSetValue(contact, kABPersonBirthdayProperty,(__bridge CFDateRef)date, &anError);
    }
    else{
        [Logger logger_Log:@"E" :@"APZContact--No Birthday present"];
    }
    BOOL hasImagePath = [[details objectForKey:CONTACT_IMAGE_PATH] isEqualToString:@""];
    if(hasImagePath == NO){
        UIImage *image = [UIImage imageWithContentsOfFile:[details objectForKey:CONTACT_IMAGE_PATH]];
        NSData *data = UIImageJPEGRepresentation(image, 1);
        ABPersonSetImageData(contact,(__bridge CFDataRef)(data), nil);
    }
    else{
        [Logger logger_Log:@"E" :@"APZContact--No Website present"];
    }
    CFErrorRef couldAddPersonError = NULL;
    BOOL couldAddPerson = ABAddressBookAddRecord(addressBook,contact,&couldAddPersonError);
    if (!couldAddPerson){
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_CREATION_FAILED_CODE],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZContact--Unable to add contact"];
    }
    if (ABAddressBookHasUnsavedChanges(addressBook)){
        CFErrorRef couldSaveAddressBookError = NULL;
        if (ABAddressBookSave(addressBook,&couldSaveAddressBookError)){
            NSArray *resultkeys=nil;
            NSArray *result=nil;
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId : true :false :resultkeys :result]];
            [Logger logger_Log:@"I" :@"APZContact--Succesfully Added Contact"];
        }
        else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_CREATION_FAILED_CODE],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZContact--Unable to add contact"];
        }
        
    }
    else{
        [Logger logger_Log:@"E" :@"APZContact--No changes in contact"];
    }
    if ((contact)!=NULL) {
        CFRelease(contact);
    }
    contact = NULL;
}

#pragma mark - Contacts DELETE
-(void)deleteContact:(NSDictionary *)details{
    ABAddressBookRef addressBook;
    if ([self isABAddressBookCreateWithOptionsAvailable]){
        CFErrorRef error = nil;
        addressBook = ABAddressBookCreateWithOptions(NULL,&error);
        if(ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusNotDetermined){
            ABAddressBookRequestAccessWithCompletion(addressBook, ^(bool granted, CFErrorRef error) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    if(granted){
                        [self deleteContact:details withAddressBook:addressBook];
                        if(addressBook != NULL){
                            CFRelease(addressBook);
                        }
                    }
                    else{
                        [self noAddressBookFoundSearch];
                    }
                });
            });
        }
        else if(ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusAuthorized){
            [self deleteContact:details withAddressBook:addressBook];
            if(addressBook != NULL){
                CFRelease(addressBook);
            }
        }
        else{
            [self noAddressBookFoundSearch];
        }
    }
    else{
        addressBook = ABAddressBookCreate();
        [self deleteContact:details withAddressBook:addressBook];
        if(addressBook != NULL){
            CFRelease(addressBook);
        }
    }
}

-(void)deleteContact:(NSDictionary *)details  withAddressBook:(ABAddressBookRef)addrBook{
    ABAddressBookRef addressBook = addrBook;
    if (addressBook == NULL){
        [self noAddressBookFoundDelete];
        return;
    }
    NSMutableArray *filteredContacts = [self searchContact:details inAddressBook:addressBook];
    NSUInteger nPeople = [filteredContacts count];
    if(nPeople == 1){
        CFErrorRef couldRemovePersonError = NULL;
        ABRecordRef contact = (__bridge ABRecordRef)([filteredContacts objectAtIndex:0]);
        BOOL couldRemovePerson = ABAddressBookRemoveRecord(addressBook,contact,&couldRemovePersonError);
        if(couldRemovePerson){
            if (ABAddressBookHasUnsavedChanges(addressBook)){
                CFErrorRef couldSaveAddressBookError = NULL;
                BOOL couldSaveAddressBook = ABAddressBookSave(addressBook,&couldSaveAddressBookError);
                if (couldSaveAddressBook){
                    NSArray *resultkeys=nil;
                    NSArray *result=nil;
                    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                    [Logger logger_Log:@"I" :@"APZContact--Succesfully Deleted Contact"];
                }
                else{
                    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_DELETE_FAILED_CODE],nil];
                    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                    [Logger logger_Log:@"E" :@"APZContact--Unable to delete contact"];
                }
            }
        }
        else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_DELETE_FAILED_CODE],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZContact--Unable to delete contact"];
            
        }
    }
    else if(nPeople > 1){
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_SEARCH_MULTIPLECONTACTS_FAILED_CODE],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZContact--Multiple Contacts Found, kindly refine your search criteria"];
    }
    else{
        [self noContactFoundDelete];
    }
}

#pragma mark -Contacts Updation
-(void)editContact:(NSDictionary *)details searchCriteria:(NSDictionary *)searchDetails{
    ABAddressBookRef addressBook;
    if ([self isABAddressBookCreateWithOptionsAvailable]){
        CFErrorRef error = nil;
        addressBook = ABAddressBookCreateWithOptions(NULL,&error);
        if(ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusNotDetermined){
            ABAddressBookRequestAccessWithCompletion(addressBook, ^(bool granted, CFErrorRef error) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    if(granted){
                        [self editContact:details searchCriteria:searchDetails withAddressBook:addressBook];
                        if(addressBook != NULL){
                            CFRelease(addressBook);
                        }
                    }
                    else{
                        [self noAddressBookFoundSearch];
                    }
                });
            });
        }
        else if(ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusAuthorized){
            [self editContact:details searchCriteria:searchDetails withAddressBook:addressBook];
            if(addressBook != NULL){
                CFRelease(addressBook);
            }
        }
        else{
            [self noAddressBookFoundSearch];
        }
    }
    else{
        addressBook = ABAddressBookCreate();
        [self editContact:details searchCriteria:searchDetails withAddressBook:addressBook];
        if(addressBook != NULL){
            CFRelease(addressBook);
        }
    }
}

-(void)editContact:(NSDictionary *)details searchCriteria:(NSDictionary *)searchDetails withAddressBook:(ABAddressBookRef)addrBook
{
    ABAddressBookRef addressBook = addrBook;
    if (addressBook == NULL){
        [self noAddressBookFoundEdit];
        return;
    }
    NSMutableArray *filteredContacts = [self searchContact:searchDetails inAddressBook:addressBook];
    NSUInteger nPeople = [filteredContacts count];
    if(nPeople == 1){
        ABRecordRef contact = (__bridge ABRecordRef)([filteredContacts objectAtIndex:0]);
        [self updateContact:contact withDetails:details inAddressBook:addressBook];
    }
    else if(nPeople > 1){
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_SEARCH_MULTIPLECONTACTS_FAILED_CODE],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"I" :@"APZContact--Multiple Contacts Found, kindly refine your search criteria"];
    }
    else{
        [self noContactFoundEdit];
    }
}

-(void)updateContact:(ABRecordRef)contact
         withDetails:(NSDictionary *)details
       inAddressBook:(ABAddressBookRef)addressBook
{
    CFErrorRef setFirstNameError = NULL;
    if([[details objectForKey:CONTACT_DETAILS_FIRSTNAME] isEqualToString:@""] == NO){
        ABRecordSetValue(contact,kABPersonFirstNameProperty,
                         (__bridge CFTypeRef)([details objectForKey:CONTACT_DETAILS_FIRSTNAME]),
                         &setFirstNameError);
    }
    else{
        [Logger logger_Log:@"I" :@"APZContact--First Name to be updated is empty"];
    }
    CFErrorRef setLastNameError = NULL;
    if([[details objectForKey:CONTACT_DETAILS_LASTNAME] isEqualToString:@""] == NO){
        ABRecordSetValue(contact,kABPersonLastNameProperty,
                         (__bridge CFTypeRef)([details objectForKey:CONTACT_DETAILS_LASTNAME]),&setLastNameError);
    }
    else{
        [Logger logger_Log:@"I" :@"APZContact--Last Name to be updated is empty"];
    }
    CFErrorRef setNickNameError = NULL;
    if([[details objectForKey:CONTACT_DETAILS_NICKNAME] isEqualToString:@""] == NO){
        ABRecordSetValue(contact,kABPersonNicknameProperty,
                         (__bridge CFTypeRef)([details objectForKey:CONTACT_DETAILS_NICKNAME]),&setNickNameError);
    }
    else{
        [Logger logger_Log:@"I" :@"APZContact--Nick Name to be updated is empty"];
    }
    CFErrorRef setMobileError = NULL;
    NSString *phHomeNew = [details objectForKey:CONTACT_DETAILS_PHONEHOME];
    NSString *phMobileNew = [details objectForKey:CONTACT_DETAILS_PHONEMOBILE];
    NSString *phWorkNew =  [details objectForKey:CONTACT_DETAILS_PHONEWORK];
    BOOL isPhoneEmpty =([phHomeNew isEqualToString:@""] &&
                        [phMobileNew isEqualToString:@""] &&
                        [phWorkNew isEqualToString:@""]);
    if(isPhoneEmpty == NO){
        ABMultiValueRef phones = ABRecordCopyValue(contact, kABPersonPhoneProperty);
        NSMutableDictionary *phoneDict = [[NSMutableDictionary alloc]init];
        [self updateContactNumber:phones phoneMobile:phMobileNew phoneHome:phHomeNew phoneWork:phWorkNew inDictionary:phoneDict];
        ABMutableMultiValueRef phoneNumRef = ABMultiValueCreateMutable(kABMultiStringPropertyType);
        CFStringRef mobileLabel = ABAddressBookCopyLocalizedLabel(kABPersonPhoneMobileLabel);
        CFStringRef workLabel =  ABAddressBookCopyLocalizedLabel(kABWorkLabel);
        CFStringRef homeLabel = ABAddressBookCopyLocalizedLabel(kABHomeLabel);
        for(id phoneLabel in phoneDict){
            if([phoneLabel isEqualToString:(__bridge NSString *)mobileLabel]){
                NSMutableArray *multiNumbers = [phoneDict objectForKey:phoneLabel];
                for (id phoneNum in multiNumbers){
                    ABMultiValueAddValueAndLabel(phoneNumRef,(__bridge CFTypeRef)(phoneNum), (__bridge CFStringRef)phoneLabel, nil);
                }
            }
            else if([phoneLabel isEqualToString:(__bridge NSString *)workLabel]){
                NSMutableArray *multiNumbers = [phoneDict objectForKey:phoneLabel];
                for (id phoneNum in multiNumbers){
                    ABMultiValueAddValueAndLabel(phoneNumRef,(__bridge CFTypeRef)(phoneNum), (__bridge CFStringRef)phoneLabel, nil);
                }
            }
            else if([phoneLabel isEqualToString:(__bridge NSString *)homeLabel]){
                NSMutableArray *multiNumbers = [phoneDict objectForKey:phoneLabel];
                for (id phoneNum in multiNumbers){
                    ABMultiValueAddValueAndLabel(phoneNumRef,(__bridge CFTypeRef)(phoneNum), (__bridge CFStringRef)phoneLabel, nil);
                }
            }
            else{
                id phoneNum = [phoneDict objectForKey:phoneLabel];
                ABMultiValueAddValueAndLabel(phoneNumRef,(__bridge CFTypeRef)(phoneNum), (__bridge CFStringRef)phoneLabel, nil);
            }
        }
        if(mobileLabel != NULL){
            CFRelease(mobileLabel);
        }
        if(homeLabel != NULL){
            CFRelease(homeLabel);
        }
        if(workLabel != NULL){
            CFRelease(workLabel);
        }
        
        ABRecordSetValue(contact, kABPersonPhoneProperty, phoneNumRef, &setMobileError);
        
        if(phoneNumRef != NULL){
            CFRelease(phoneNumRef);
        }
        if(phones != NULL){
            CFRelease(phones);
        }
        
    }
    else{
        [Logger logger_Log:@"I" :@"APZContact--Contact Number to be updated is empty"];
    }
    if([[details objectForKey:CONTACT_DETAILS_MAIL] isEqualToString:@""] == NO){
        ABMutableMultiValueRef mailRef = ABMultiValueCreateMutable(kABMultiStringPropertyType);
        ABMultiValueIdentifier mailValueIdentifier;
        CFErrorRef anError = NULL;
        ABMultiValueAddValueAndLabel(mailRef, (__bridge CFTypeRef)([details objectForKey:CONTACT_DETAILS_MAIL]),
                                     kABWorkLabel, &mailValueIdentifier);
        ABRecordSetValue(contact, kABPersonEmailProperty, mailRef, &anError);
        if(mailRef != NULL){
            CFRelease(mailRef);
        }
        
    }
    else{
        [Logger logger_Log:@"I" :@"APZContact--EmailID to be updated is empty"];
    }
    if([[details objectForKey:CONTACT_DETAILS_ADDRESS] isEqualToString:@""] == NO){
        ABMutableMultiValueRef addrRef = ABMultiValueCreateMutable(kABMultiDictionaryPropertyType);
        NSMutableDictionary *addrDict = [[NSMutableDictionary alloc] init];
        [addrDict setObject:[details objectForKey:CONTACT_DETAILS_ADDRESS] forKey:(NSString *)kABPersonAddressStreetKey];
        ABMultiValueAddValueAndLabel(addrRef, (__bridge CFTypeRef)(addrDict), kABWorkLabel, NULL);
        ABRecordSetValue(contact, kABPersonAddressProperty,addrRef , NULL);
        if(addrRef != NULL){
            CFRelease(addrRef);
        }
    }
    else{
        [Logger logger_Log:@"I" :@"APZContact--Contact address to be updated is empty"];
    }
    NSString *webAddr = [details objectForKey:CONTACT_DETAILS_WEBSITE];
    if([webAddr isEqualToString:@""] == NO){
        ABMutableMultiValueRef websiteRef = ABMultiValueCreateMutable(kABMultiStringPropertyType);
        ABMultiValueIdentifier websiteIdentifier;
        CFErrorRef anError = NULL;
        ABMultiValueAddValueAndLabel(websiteRef, (__bridge CFTypeRef)([details objectForKey:CONTACT_DETAILS_WEBSITE]),kABWorkLabel, &websiteIdentifier);
        ABRecordSetValue(contact, kABPersonURLProperty, websiteRef, &anError);
        if(websiteRef != NULL){
            CFRelease(websiteRef);
        }
        
        
    }
    else{
        [Logger logger_Log:@"I" :@"APZContact--Website address to be updated is empty"];
    }
    NSString *imagePath = [details objectForKey:CONTACT_IMAGE_PATH];
    if([imagePath isEqualToString:@""] == NO){
        UIImage *image = [UIImage imageWithContentsOfFile:imagePath];
        NSData *data = UIImageJPEGRepresentation(image, 1);
        ABPersonRemoveImageData(contact, NULL);
        ABPersonSetImageData(contact,(__bridge CFDataRef)(data), nil);
    }
    else{
        [Logger logger_Log:@"I" :@"APZContact--image to be updated is empty"];
    }
    NSString *birthDay = [details objectForKey:CONTACT_DETAILS_BIRTHDAY];
    if([birthDay isEqualToString:@""] == NO){
        NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:[self getUserDateFormat]];
        CFErrorRef anError = NULL;
        NSDate *date = [formatter dateFromString:[NSString stringWithFormat:@"%@",birthDay]];
        ABRecordSetValue(contact, kABPersonBirthdayProperty,(__bridge CFDateRef)date, &anError);
    }
    else{
        [Logger logger_Log:@"I" :@"APZContact--birthday to be updated is empty"];
    }
    if (ABAddressBookHasUnsavedChanges(addressBook))
    {
        CFErrorRef couldSaveAddressBookError = NULL;
        BOOL couldSaveAddressBook = ABAddressBookSave(addressBook,&couldSaveAddressBookError);
        if (couldSaveAddressBook){
            NSArray *resultkeys=nil;
            NSArray *result=nil;
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
            [Logger logger_Log:@"I" :@"APZContact--Succesfully Updated Contact"];
        }
        else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_EDIT_FAILED_CODE],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZContact--Succesfully Updated Contact"];
        }
    }
}

-(NSMutableDictionary *)updateContactNumber:(ABMultiValueRef)phones
                                phoneMobile:(NSString *)phMobile
                                  phoneHome:(NSString *)phHome
                                  phoneWork:(NSString *)phWork
                               inDictionary:(NSMutableDictionary *)phoneNumbers{
    NSCharacterSet *toExclude = [NSCharacterSet characterSetWithCharactersInString:@"/.()-+ "];
    phMobile = [[phMobile componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
    phHome = [[phHome componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
    phWork = [[phWork componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
    CFStringRef mobileLabel = ABAddressBookCopyLocalizedLabel(kABPersonPhoneMobileLabel);
    CFStringRef workLabel =  ABAddressBookCopyLocalizedLabel(kABWorkLabel);
    CFStringRef homeLabel = ABAddressBookCopyLocalizedLabel(kABHomeLabel);
    CFIndex len = ABMultiValueGetCount(phones);
    NSMutableArray *mobileArr  = [@[] mutableCopy];
    NSMutableArray *workArr = [@[] mutableCopy];
    NSMutableArray *homeArr = [@[] mutableCopy];
    if(len > 0)
    {
        for(CFIndex j = 0; j < len; j++)
        {
            CFStringRef phoneNumberRef = ABMultiValueCopyValueAtIndex(phones, j);
            CFStringRef locLabel = ABMultiValueCopyLabelAtIndex(phones, j);
            CFStringRef  phoneLabel = ABAddressBookCopyLocalizedLabel(locLabel);
            NSString *phoneNumber = (__bridge NSString *)phoneNumberRef;
            phoneNumber = [[phoneNumber componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
            if([(__bridge NSString *)phoneLabel isEqualToString:(__bridge NSString *)mobileLabel]){
                if([phMobile isEqualToString:@""] == NO){
                    [mobileArr addObject:phMobile];
                }
                else{
                    [mobileArr addObject:phoneNumber];
                }
            }
            else if([(__bridge NSString *)phoneLabel isEqualToString:(__bridge NSString *)workLabel]){
                if([phWork isEqualToString:@""] == NO){
                    [workArr addObject:phWork];
                }
                else{
                    [workArr addObject:phoneNumber];
                }
            }
            else if([(__bridge NSString *)phoneLabel isEqualToString:(__bridge NSString *)homeLabel]){
                if([phHome isEqualToString:@""] == NO){
                    [homeArr addObject:phHome];
                }
                else{
                    [homeArr addObject:phoneNumber];
                }
            }
            if(phoneNumberRef != NULL){
                CFRelease(phoneNumberRef);
            }
            if(locLabel != NULL){
                CFRelease(locLabel);
            }
            if(phoneLabel != NULL){
                CFRelease(phoneLabel);
            }
            
        }
        [phoneNumbers setObject:mobileArr forKey:(__bridge NSString *)mobileLabel];
        [phoneNumbers setObject:workArr forKey:(__bridge NSString *)workLabel];
        [phoneNumbers setObject:homeArr forKey:(__bridge NSString *)homeLabel];
    }
    else{
        if([phMobile isEqualToString:@""] == NO){
            [mobileArr addObject:phMobile];
            [phoneNumbers setObject:mobileArr forKey:(__bridge NSString *)mobileLabel];
        }
        if([phHome isEqualToString:@""] == NO){
            [homeArr addObject:phHome];
            [phoneNumbers setObject:homeArr forKey:(__bridge NSString *)homeLabel];
        }
        if([phWork isEqualToString:@""] == NO){
            [workArr addObject:phWork];
            [phoneNumbers setObject:workArr forKey:(__bridge NSString *)workLabel];
        }
    }
    
    if(mobileLabel != NULL){
        CFRelease(mobileLabel);
    }
    if(homeLabel != NULL){
        CFRelease(homeLabel);
    }
    if(workLabel != NULL){
        CFRelease(workLabel);
    }
    return phoneNumbers;
}
#pragma mark- Contact Search for Update and Delete
-(NSMutableArray *)searchContact:(NSDictionary *)searchDetails
                   inAddressBook:(ABAddressBookRef)addressBook
{
    CFArrayRef allContacts = NULL;
    NSMutableArray *filteredContacts = nil;
    CFStringRef name = NULL;
    NSString *phoneMobile = nil;
    NSString *phoneWork =  nil;
    NSString *phoneHome =  nil;
    NSString *firstName = [searchDetails objectForKey:CONTACT_DETAILS_FIRSTNAME];
    NSString *lastName = [searchDetails objectForKey:CONTACT_DETAILS_LASTNAME];
    BOOL isFirstNameEmpty = [firstName isEqualToString:@""];
    BOOL isSecondNameEmpty = [lastName isEqualToString:@""];
    if(isFirstNameEmpty && isSecondNameEmpty){
        allContacts = ABAddressBookCopyArrayOfAllPeople(addressBook);
    }
    else{
        if(isSecondNameEmpty){
            allContacts =ABAddressBookCopyPeopleWithName(addressBook,(__bridge CFStringRef)firstName);
        }
        else if(isFirstNameEmpty){
            allContacts =ABAddressBookCopyPeopleWithName(addressBook,(__bridge CFStringRef)lastName);
        }
        else{
            name =  (__bridge CFStringRef)[NSString stringWithFormat:@"%@ %@",firstName,lastName];
            allContacts =ABAddressBookCopyPeopleWithName(addressBook,name);
        }
    }
    CFIndex nPeople = CFArrayGetCount(allContacts);
    if(nPeople == 1){
        filteredContacts =  [(__bridge NSArray *) allContacts mutableCopy];
    }
    else if(nPeople > 1){
        phoneHome = [searchDetails objectForKey:CONTACT_DETAILS_PHONEHOME];
        phoneMobile = [searchDetails objectForKey:CONTACT_DETAILS_PHONEMOBILE];
        phoneWork = [searchDetails objectForKey:CONTACT_DETAILS_PHONEWORK];
        BOOL isPhoneEmpty = ([phoneHome isEqualToString:@""] &&
                             [phoneMobile isEqualToString:@""] &&
                             [phoneWork isEqualToString:@""]);
        if(isPhoneEmpty == NO){
            filteredContacts = [self filterContactsForSearch:allContacts withPhMobile:phoneMobile withPhWork:phoneWork withPhHome:phoneHome];
        }
        else{
            filteredContacts =  [(__bridge NSArray *) allContacts mutableCopy];
        }
    }
    else
    {
        [Logger logger_Log:@"I" :@"APZContact--No Contacts there or does not match filter condition"];
        filteredContacts = nil;
    }
    
    if(allContacts != NULL){
        CFRelease(allContacts);
    }
    
    return filteredContacts;
}

-(NSMutableArray *)filterContactsForSearch:(CFArrayRef)allContacts withPhMobile:(NSString *)phMobile withPhWork:(NSString *)phWork withPhHome:(NSString *)phHome
{
    ABRecordRef contact = NULL;
    NSCharacterSet *toExclude = nil;
    CFIndex len;
    CFStringRef mobileLabel = ABAddressBookCopyLocalizedLabel(kABPersonPhoneMobileLabel);
    CFStringRef workLabel =  ABAddressBookCopyLocalizedLabel(kABWorkLabel);
    CFStringRef homeLabel = ABAddressBookCopyLocalizedLabel(kABHomeLabel);
    CFIndex numOfContacts = CFArrayGetCount(allContacts);
    NSMutableArray *contactArr = [@[] mutableCopy];
    for (int i=0;i<numOfContacts;i++){
        contact = CFArrayGetValueAtIndex(allContacts,i);
        ABMultiValueRef phones = ABRecordCopyValue(contact, kABPersonPhoneProperty);
        toExclude = [NSCharacterSet characterSetWithCharactersInString:@"/.()-+ "];
        phMobile = [[phMobile componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
        phWork = [[phWork componentsSeparatedByCharactersInSet:toExclude]   componentsJoinedByString: @""];
        phHome = [[phHome componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
        len = ABMultiValueGetCount(phones);
        for(CFIndex j = 0; j < len; j++)
        {
            CFStringRef phoneRef = ABMultiValueCopyValueAtIndex(phones, j);
            CFStringRef locLabel = ABMultiValueCopyLabelAtIndex(phones, j);
            CFStringRef  phoneLabel = ABAddressBookCopyLocalizedLabel(locLabel);
            NSString *phoneNumber = (__bridge NSString *)phoneRef;
            phoneNumber = [[phoneNumber componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
            phoneNumber = [phoneNumber stringByReplacingOccurrencesOfString:@"[^0-9]" withString:@"" options:NSRegularExpressionSearch range:NSMakeRange(0, phoneNumber.length)];
            if([(__bridge NSString *)phoneLabel isEqualToString:(__bridge NSString *)mobileLabel]){
                if([phMobile isEqualToString: phoneNumber] == YES){
                    [contactArr addObject:(__bridge id)(contact)];
                    if(phoneRef != NULL){
                        CFRelease(phoneRef);
                    }
                    if(locLabel != NULL){
                        CFRelease(locLabel);
                    }
                    if(phoneLabel != NULL){
                        CFRelease(phoneLabel);
                    }
                    
                    break;
                }
            }
            else if([(__bridge NSString *)phoneLabel isEqualToString:(__bridge NSString *)workLabel]){
                if([phWork isEqualToString: phoneNumber] == YES){
                    [contactArr addObject:(__bridge id)(contact)];
                    if(phoneRef != NULL){
                        CFRelease(phoneRef);
                    }
                    if(locLabel != NULL){
                        CFRelease(locLabel);
                    }
                    if(phoneLabel != NULL){
                        CFRelease(phoneLabel);
                    }
                    
                    break;
                }
            }
            else if([(__bridge NSString *)phoneLabel isEqualToString:(__bridge NSString *)homeLabel]){
                if([phHome isEqualToString: phoneNumber] == YES){
                    [contactArr addObject:(__bridge id)(contact)];
                    if(phoneRef != NULL){
                        CFRelease(phoneRef);
                    }
                    if(locLabel != NULL){
                        CFRelease(locLabel);
                    }
                    if(phoneLabel != NULL){
                        CFRelease(phoneLabel);
                    }
                    break;
                }
            }
            
        }
        if(phones != NULL){
            CFRelease(phones);
        }
        
    }
    if(mobileLabel != NULL){
        CFRelease(mobileLabel);
    }
    if(homeLabel != NULL){
        CFRelease(homeLabel);
    }
    if(workLabel != NULL){
        CFRelease(workLabel);
    }
    return contactArr;
}

#pragma mark Contact Fetch API
-(void)contatsFetch:(NSDictionary*)FetchDetails{
    ABAddressBookRef addressBook;
    CFErrorRef error = nil;
    addressBook = ABAddressBookCreateWithOptions(NULL,&error);
    if(ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusNotDetermined){
        ABAddressBookRequestAccessWithCompletion(addressBook, ^(bool granted, CFErrorRef error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if(granted){
                    _addressBookController = [[ABPeoplePickerNavigationController alloc] init];
                    [_addressBookController setPeoplePickerDelegate:self];
                    [_addressBookController setModalPresentationStyle:UIModalPresentationFullScreen];
                    [self.viewController presentViewController:_addressBookController animated:YES completion:nil];
                    if(addressBook != NULL){
                        CFRelease(addressBook);
                    }
                }
                else{
                    [self noAddressBookFoundEdit];
                }
            });
        });
    }
    else if(ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusAuthorized){
        _addressBookController = [[ABPeoplePickerNavigationController alloc] init];
        [_addressBookController setPeoplePickerDelegate:self];
        [_addressBookController setModalPresentationStyle:UIModalPresentationFullScreen];
        [self.viewController presentViewController:_addressBookController animated:YES completion:nil];
        if(addressBook != NULL){
            CFRelease(addressBook);
        }
    }
    else{
        [self noAddressBookFoundSearch];
    }
}


- (void)peoplePickerNavigationControllerDidCancel:
(ABPeoplePickerNavigationController *)peoplePicker
{
    NSArray *resultkeys=nil;
    NSArray *result=nil;
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    [Logger logger_Log:@"I" :@"APZContact--Cancelled"];
    [self cleanPlugin];
}


- (void)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker didSelectPerson:(ABRecordRef)person  {
    
    NSString *contactName = CFBridgingRelease(ABRecordCopyCompositeName(person));
    NSString* nameField = [NSString stringWithFormat:@"%@", contactName ? contactName : @"No Name"];
    NSString *phoneNumber;
    NSString*emailID;
    NSMutableArray *arrContactsPhone = [[NSMutableArray alloc] init];
    NSMutableArray *arrContactsEmail = [[NSMutableArray alloc] init];
    ABMultiValueRef phones = ABRecordCopyValue(person, kABPersonPhoneProperty);
    for(CFIndex j = 0; j < ABMultiValueGetCount(phones); j++)
    {
        CFStringRef phoneNumberRef = ABMultiValueCopyValueAtIndex(phones, j);
        phoneNumber = (__bridge NSString *)phoneNumberRef;
        if(phoneNumberRef != NULL){
            CFRelease(phoneNumberRef);
        }
        [arrContactsPhone addObject:phoneNumber];
    }
    ABMultiValueRef email = ABRecordCopyValue(person, kABPersonEmailProperty);
    for(CFIndex j = 0; j < ABMultiValueGetCount(email); j++)
    {
        CFStringRef emailNumberRef = ABMultiValueCopyValueAtIndex(email, j);
        emailID = (__bridge_transfer NSString *)emailNumberRef;
        if(emailNumberRef != NULL){
            CFRelease(emailNumberRef);
        }
        [arrContactsEmail addObject:emailID];
    }
    CFDataRef  photo = ABPersonCopyImageData(person);
    NSString* base64String;
    if (photo) {
        base64String= [(__bridge NSData*)photo base64Encoding];
    }else{
        base64String=@"";
    }
    NSDate* birthDate = (__bridge_transfer NSDate*)ABRecordCopyValue(person, kABPersonBirthdayProperty);
    NSString *birthday;
    if (birthDate) {
        NSDateFormatter *dateFormate = [[NSDateFormatter alloc] init];
        [dateFormate setDateFormat:[self getUserDateFormat]];
        birthday= [dateFormate stringFromDate:birthDate];
    }
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    if(nameField==Nil)
        nameField=@"";
    else if(arrContactsPhone==Nil)
        arrContactsPhone=(NSMutableArray*)@[];
    else if (arrContactsEmail==Nil)
        arrContactsEmail=(NSMutableArray*)@[];
    else if(birthday==Nil)
        birthday=@"";
    NSArray *resultkeys=[NSArray arrayWithObjects:SOCIAL_USER_NAME,@"phoneno",SOCIAL_USER_EMAIL,@"birthday",@"encodedImage",nil];
    NSArray *result=[NSArray arrayWithObjects:nameField,arrContactsPhone,arrContactsEmail,birthday,base64String,nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
    [Logger logger_Log:@"I" :@"APZContact--Contact Fetched SuccessFully"];
}


-(BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker
     shouldContinueAfterSelectingPerson:(ABRecordRef)person property:(ABPropertyID)property
                             identifier:(ABMultiValueIdentifier)identifier
{
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    [self cleanPlugin];
    return NO;
}
#pragma mark Contacts Search API
-(void)contactsSearch:(NSDictionary*)searchDetails{
    ABAddressBookRef addressBook;
    if ([self isABAddressBookCreateWithOptionsAvailable]){
        CFErrorRef error = nil;
        addressBook = ABAddressBookCreateWithOptions(NULL,&error);
        if(ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusNotDetermined){
            ABAddressBookRequestAccessWithCompletion(addressBook, ^(bool granted, CFErrorRef error) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    if(granted){
                        [self contactsSearch:searchDetails withAddressBook:addressBook];
                        if(addressBook != NULL){
                            CFRelease(addressBook);
                        }
                    }
                    else{
                        [self noAddressBookFoundSearch];
                    }
                });
            });
        }
        else if(ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusAuthorized){
            [self contactsSearch:searchDetails withAddressBook:addressBook];
            if(addressBook != NULL){
                CFRelease(addressBook);
            }
        }
        else{
            [self noAddressBookFoundSearch];
        }
    }
    else{
        addressBook = ABAddressBookCreate();
        [self contactsSearch:searchDetails withAddressBook:addressBook];
        if(addressBook != NULL){
            CFRelease(addressBook);
        }
    }
}
-(void)contactsSearch:(NSDictionary*)searchDetails withAddressBook:(ABAddressBookRef)addrBook;
{
    CFArrayRef allContacts = NULL;
    ABRecordRef contact;
    CFStringRef name;
    NSString *firstName = nil;
    NSString *lastName = nil;
    NSString *contactNumber = nil;
    ABAddressBookRef addressBook = addrBook;
    if (addressBook == NULL){
        [self noAddressBookFoundSearch];
        return;
    }
    allContacts = ABAddressBookCopyArrayOfAllPeople(addressBook);
    CFIndex nPeople = CFArrayGetCount(allContacts);
    firstName = [searchDetails objectForKey:CONTACT_DETAILS_FIRSTNAME];
    lastName = [searchDetails objectForKey:CONTACT_DETAILS_LASTNAME];
    BOOL isFirstNameEmpty= [firstName isEqualToString:@""];
    BOOL isLastNameEmpty= [lastName isEqualToString:@""];
    if( (isFirstNameEmpty == YES) && (isLastNameEmpty == YES))
    {
        if(nPeople == 1){
            ABRecordRef contact = CFArrayGetValueAtIndex(allContacts,0);
            [self sendSingleContact:contact];
        }
        else if(nPeople > 1){
            contactNumber = [searchDetails objectForKey:CONTACT_DETAILS_PHONEMOBILE];
            BOOL isPhoneEmpty = ([contactNumber isEqualToString:@""]);
            if(isPhoneEmpty == NO){
                NSMutableArray *filteredContacts = [self filterContacts:allContacts withPhMobile:contactNumber];
                if(filteredContacts.count > 0){
                    [self sendMultipleContacts:(CFArrayRef)filteredContacts];
                }
                else{
                    [self noContactFoundSearch];
                }
            }
            else{
                [self sendMultipleContacts:allContacts];
            }
        }
        else{
            [self noContactFoundSearch];
        }
    }
    else if( (isFirstNameEmpty == NO) && (isLastNameEmpty == NO)){
        NSMutableArray *contactArr = [@[] mutableCopy];
        CFStringRef mobileLabel = ABAddressBookCopyLocalizedLabel(kABPersonPhoneMobileLabel);
        CFStringRef workLabel =  ABAddressBookCopyLocalizedLabel(kABWorkLabel);
        CFStringRef homeLabel = ABAddressBookCopyLocalizedLabel(kABHomeLabel);
        for(int i = 0;i<nPeople;i++){
            contact = CFArrayGetValueAtIndex(allContacts,i);
            CFStringRef fName = ABRecordCopyValue(contact, kABPersonFirstNameProperty);
            CFStringRef lName = ABRecordCopyValue(contact, kABPersonLastNameProperty);
            firstName = [firstName lowercaseString];
            lastName = [lastName lowercaseString];
            NSString *contactFirstName = [(__bridge NSString *)fName lowercaseString];
            NSString *contactLastName = [(__bridge NSString *)lName lowercaseString];
            BOOL isFirstNameMatch = NO;
            if([contactFirstName isEqualToString:@""]){
                isFirstNameMatch = NO;
            }
            else{
                isFirstNameMatch = ([contactFirstName hasPrefix:firstName] ||[contactFirstName isEqualToString:firstName]);
            }
            BOOL isLastNameMatch = NO;
            if([contactLastName isEqualToString:@""]){
                isLastNameMatch = NO;
            }
            else{
                isLastNameMatch = ([contactLastName hasPrefix:lastName] ||[contactLastName isEqualToString:lastName]);
            }
            if(isFirstNameMatch && isLastNameMatch){
                ABMultiValueRef phones = ABRecordCopyValue(contact, kABPersonPhoneProperty);
                contactNumber = [searchDetails objectForKey:CONTACT_DETAILS_PHONEMOBILE];
                BOOL isPhoneEmpty =([contactNumber isEqualToString:@""]);
                if(isPhoneEmpty == NO){
                    NSCharacterSet *toExclude = [NSCharacterSet characterSetWithCharactersInString:@"/.()-+ "];
                    contactNumber = [[contactNumber componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
                    NSUInteger len = ABMultiValueGetCount(phones);
                    for(CFIndex j = 0; j < len; j++)
                    {
                        CFStringRef phoneRef = ABMultiValueCopyValueAtIndex(phones, j);
                        CFStringRef locLabel = ABMultiValueCopyLabelAtIndex(phones, j);
                        CFStringRef  phoneLabel = ABAddressBookCopyLocalizedLabel(locLabel);
                        NSString *phoneNumber = (__bridge NSString *)phoneRef;
                        phoneNumber = [[phoneNumber componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
                        phoneNumber = [phoneNumber stringByReplacingOccurrencesOfString:@"[^0-9]" withString:@"" options:NSRegularExpressionSearch range:NSMakeRange(0, phoneNumber.length)];
                        if([contactNumber isEqualToString: phoneNumber] == YES){
                            [contactArr addObject:(__bridge id)(contact)];
                            if(phoneRef != NULL){
                                CFRelease(phoneRef);
                            }
                            if(locLabel != NULL){
                                CFRelease(locLabel);
                            }
                            if(phoneLabel != NULL){
                                CFRelease(phoneLabel);
                            }
                            break;
                        }
                        else{
                            if(phoneRef != NULL){
                                CFRelease(phoneRef);
                            }
                            if(locLabel != NULL){
                                CFRelease(locLabel);
                            }
                            if(phoneLabel != NULL){
                                CFRelease(phoneLabel);
                            }
                            break;
                        }
                    }
                    if(phones != NULL){
                        CFRelease(phones);
                    }
                }
                else{
                    [contactArr addObject:(__bridge id)(contact)];
                }
            }
            else{
                [Logger logger_Log:@"I" :@"APZContact--Contact Does not match"];
            }
            if(fName != NULL){
                CFRelease(fName);
            }
            if(lName != NULL){
                CFRelease(lName);
            }
        }
        if(mobileLabel != NULL){
            CFRelease(mobileLabel);
        }
        if(homeLabel != NULL){
            CFRelease(homeLabel);
        }
        if(workLabel != NULL){
            CFRelease(workLabel);
        }
        if(contactArr.count > 0){
            [self sendMultipleContacts:(CFArrayRef)contactArr];
        }
        else{
            [self noContactFoundSearch];
        }
    }
    else{
        name =  (__bridge CFStringRef)[NSString stringWithFormat:@"%@ %@",firstName,lastName];
        CFStringRef mobileLabel = ABAddressBookCopyLocalizedLabel(kABPersonPhoneMobileLabel);
        CFStringRef workLabel =  ABAddressBookCopyLocalizedLabel(kABWorkLabel);
        CFStringRef homeLabel = ABAddressBookCopyLocalizedLabel(kABHomeLabel);
        NSMutableArray *contactArr = [@[] mutableCopy];
        for(int i = 0;i<nPeople;i++){
            contact = CFArrayGetValueAtIndex(allContacts,i);
            CFStringRef fName = ABRecordCopyValue(contact, kABPersonFirstNameProperty);
            CFStringRef lName = ABRecordCopyValue(contact, kABPersonLastNameProperty);
            firstName = [firstName lowercaseString];
            lastName = [lastName lowercaseString];
            NSString *contactFirstName = [(__bridge NSString *)fName lowercaseString];
            NSString *contactLastName = [(__bridge NSString *)lName lowercaseString];
            BOOL isFirstNameMatch = NO;
            if([contactFirstName isEqualToString:@""]){
                isFirstNameMatch = NO;
            }
            else{
                isFirstNameMatch = ([contactFirstName hasPrefix:firstName] ||[contactFirstName isEqualToString:firstName]);
            }
            BOOL isLastNameMatch = NO;
            if([contactLastName isEqualToString:@""]){
                isLastNameMatch = NO;
            }
            else{
                isLastNameMatch = ([contactLastName hasPrefix:lastName] ||[contactLastName isEqualToString:lastName]);
            }
            if(isFirstNameMatch || isLastNameMatch){
                ABMultiValueRef phones = ABRecordCopyValue(contact, kABPersonPhoneProperty);
                contactNumber = [searchDetails objectForKey:CONTACT_DETAILS_PHONEMOBILE];
                BOOL isPhoneEmpty =([contactNumber isEqualToString:@""]);
                if(isPhoneEmpty == NO){
                    NSCharacterSet *toExclude = [NSCharacterSet characterSetWithCharactersInString:@"/.()-+ "];
                    contactNumber = [[contactNumber componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
                    NSUInteger len = ABMultiValueGetCount(phones);
                    for(CFIndex j = 0; j < len; j++)
                    {
                        CFStringRef phoneRef = ABMultiValueCopyValueAtIndex(phones, j);
                        CFStringRef locLabel = ABMultiValueCopyLabelAtIndex(phones, j);
                        CFStringRef  phoneLabel = ABAddressBookCopyLocalizedLabel(locLabel);
                        NSString *phoneNumber = (__bridge NSString *)phoneRef;
                        phoneNumber = [[phoneNumber componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
                        phoneNumber = [phoneNumber stringByReplacingOccurrencesOfString:@"[^0-9]" withString:@"" options:NSRegularExpressionSearch range:NSMakeRange(0, phoneNumber.length)];
                        if([contactNumber isEqualToString: phoneNumber] == YES){
                            [contactArr addObject:(__bridge id)(contact)];
                            if(phoneRef != NULL){
                                CFRelease(phoneRef);
                            }
                            if(locLabel != NULL){
                                CFRelease(locLabel);
                            }
                            if(phoneLabel != NULL){
                                CFRelease(phoneLabel);
                            }
                            
                            break;
                        }
                        else {
                            if(phoneRef != NULL){
                                CFRelease(phoneRef);
                            }
                            if(locLabel != NULL){
                                CFRelease(locLabel);
                            }
                            if(phoneLabel != NULL){
                                CFRelease(phoneLabel);
                            }
                            break;
                        }
                    }
                    if(phones != NULL){
                        CFRelease(phones);
                    }
                    
                }
                else{
                    [contactArr addObject:(__bridge id)(contact)];
                }
            }
            else{
                [Logger logger_Log:@"I" :@"APZContact--Contact Does not match"];
            }
            if(fName != NULL){
                CFRelease(fName);
            }
            if(lName != NULL){
                CFRelease(lName);
            }
        }
        if(mobileLabel != NULL){
            CFRelease(mobileLabel);
        }
        if(homeLabel != NULL){
            CFRelease(homeLabel);
        }
        if(workLabel != NULL){
            CFRelease(workLabel);
        }
        
        if(contactArr.count > 0){
            [self sendMultipleContacts:(CFArrayRef)contactArr];
        }
        else{
            [self noContactFoundSearch];
        }
    }
    if(allContacts != NULL){
        CFRelease(allContacts);
    }
}

-(NSMutableArray *)filterContacts:(CFArrayRef)allContacts withPhMobile:(NSString *)contactNumber
{
    ABRecordRef contact = NULL;
    NSCharacterSet *toExclude = nil;
    CFIndex len;
    CFIndex numOfContacts = CFArrayGetCount(allContacts);
    NSMutableArray *contactArr = [@[] mutableCopy];
    for (int i=0;i<numOfContacts;i++){
        contact = CFArrayGetValueAtIndex(allContacts,i);
        ABMultiValueRef phones = ABRecordCopyValue(contact, kABPersonPhoneProperty);
        toExclude = [NSCharacterSet characterSetWithCharactersInString:@"/.()-+ "];
        contactNumber = [[contactNumber componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
        len = ABMultiValueGetCount(phones);
        for(CFIndex j = 0; j < len; j++)
        {
            CFStringRef phoneRef = ABMultiValueCopyValueAtIndex(phones, j);
            CFStringRef locLabel = ABMultiValueCopyLabelAtIndex(phones, j);
            CFStringRef  phoneLabel = ABAddressBookCopyLocalizedLabel(locLabel);
            NSString *phoneNumber = (__bridge NSString *)phoneRef;
            phoneNumber = [[phoneNumber componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
            phoneNumber = [phoneNumber stringByReplacingOccurrencesOfString:@"[^0-9]" withString:@"" options:NSRegularExpressionSearch range:NSMakeRange(0, phoneNumber.length)];
            if([contactNumber isEqualToString: phoneNumber] == YES){
                [contactArr addObject:(__bridge id)(contact)];
                if(phoneRef != NULL){
                    CFRelease(phoneRef);
                }
                if(locLabel != NULL){
                    CFRelease(locLabel);
                }
                if(phoneLabel != NULL){
                    CFRelease(phoneLabel);
                }
                
                break;
            }else{
                if(phoneRef != NULL){
                    CFRelease(phoneRef);
                }
                if(locLabel != NULL){
                    CFRelease(locLabel);
                }
                if(phoneLabel != NULL){
                    CFRelease(phoneLabel);
                }
            }
        }
        if(phones != NULL){
            CFRelease(phones);
        }
        
    }
    return contactArr;
}

-(void)sendSingleContact:(ABRecordRef)contact{
    NSMutableDictionary *multiContactDics = [@{} mutableCopy];
    NSMutableDictionary *contactDict = [@{} mutableCopy];
    NSMutableArray *multiContactArr = [@[] mutableCopy];
    CFStringRef firstName = ABRecordCopyValue(contact, kABPersonFirstNameProperty);
    if(firstName != NULL){
        [contactDict setObject:(__bridge NSString *)firstName forKey:CONTACT_DETAILS_FIRSTNAME];
        if(firstName != NULL){
            CFRelease(firstName);
        }
    }
    CFStringRef lastName = ABRecordCopyValue(contact, kABPersonLastNameProperty);
    if(lastName != NULL){
        [contactDict setObject:(__bridge NSString *)lastName forKey:CONTACT_DETAILS_LASTNAME];
        if(lastName != NULL){
            CFRelease(lastName);
        }
        
    }
    ABMultiValueRef phones = ABRecordCopyValue(contact, kABPersonPhoneProperty);
    CFIndex phonesLen = ABMultiValueGetCount(phones);
    CFStringRef mobileLabel = ABAddressBookCopyLocalizedLabel(kABPersonPhoneMobileLabel);
    CFStringRef workLabel =  ABAddressBookCopyLocalizedLabel(kABWorkLabel);
    CFStringRef homeLabel = ABAddressBookCopyLocalizedLabel(kABHomeLabel);
    NSCharacterSet *toExclude = [NSCharacterSet characterSetWithCharactersInString:@"/.()-+ "];
    NSString *phNumber = nil;
    for(CFIndex j = 0; j < phonesLen; j++)
    {
        CFStringRef phoneNumber = ABMultiValueCopyValueAtIndex(phones, j);
        CFStringRef locLabel = ABMultiValueCopyLabelAtIndex(phones, j);
        CFStringRef phoneLabel = ABAddressBookCopyLocalizedLabel(locLabel);
        phNumber = [[(__bridge NSString *)phoneNumber componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
        [contactDict setObject:phNumber forKey:(__bridge NSString *)phoneLabel];
        if(phoneNumber != NULL){
            CFRelease(phoneNumber);
        }
        if(locLabel != NULL){
            CFRelease(locLabel);
        }
        if(phoneLabel != NULL){
            CFRelease(phoneLabel);
        }
    }
    if(mobileLabel != NULL){
        CFRelease(mobileLabel);
    }
    if(homeLabel != NULL){
        CFRelease(homeLabel);
    }
    if(workLabel != NULL){
        CFRelease(workLabel);
    }
    if(phones != NULL){
        CFRelease(phones);
    }
    ABMultiValueRef emails = ABRecordCopyValue(contact, kABPersonEmailProperty);
    CFIndex emailsLen = ABMultiValueGetCount(emails);
    CFStringRef workEmailLabel = ABAddressBookCopyLocalizedLabel(kABWorkLabel);
    for(CFIndex k = 0; k < emailsLen; k++){
        CFStringRef emailID = ABMultiValueCopyValueAtIndex(emails, k);
        CFStringRef locLabel = ABMultiValueCopyLabelAtIndex(emails, k);
        CFStringRef emailLabel = ABAddressBookCopyLocalizedLabel(locLabel);
        if([(__bridge NSString *)emailLabel isEqualToString:(__bridge NSString *)workEmailLabel]){
            [contactDict setObject:(__bridge NSString *)emailID forKey:CONTACT_DETAILS_MAIL];
        }
        if(emailID != NULL){
            CFRelease(emailID);
        }
        if(locLabel != NULL){
            CFRelease(locLabel);
        }
        if(emailLabel != NULL){
            CFRelease(emailLabel);
        }
    }
    if(workEmailLabel != NULL){
        CFRelease(workEmailLabel);
    }
    if(emails != NULL){
        CFRelease(emails);
    }
    
    CFTypeRef adressesReference = ABRecordCopyValue(contact, kABPersonAddressProperty);
    CFIndex mvCount = ABMultiValueGetCount(adressesReference);
    CFStringRef workAddrLabel = ABAddressBookCopyLocalizedLabel(kABWorkLabel);
    if (mvCount > 0) {
        int i =0;
        NSString *streetAddress = nil;
        for (i=0; i < mvCount; i++) {
            CFTypeRef addrValues = ABMultiValueCopyValueAtIndex(adressesReference, i);
            NSDictionary *values = (__bridge NSDictionary *)addrValues;
            streetAddress = [values valueForKey:(NSString *)kABPersonAddressStreetKey];
            if([(__bridge NSString *)workAddrLabel isEqualToString:(__bridge NSString *)workAddrLabel]){
                if(streetAddress != nil){
                    [contactDict setObject:streetAddress forKey:CONTACT_DETAILS_ADDRESS];
                }
            }
            if(addrValues != NULL){
                CFRelease(addrValues);
            }
            
        }
    }
    else{
        [Logger logger_Log:@"I" :@"APZContact--Contact has no Address"];
    }
    if(workAddrLabel != NULL){
        CFRelease(workAddrLabel);
    }
    if(adressesReference != NULL){
        CFRelease(adressesReference);
    }
    ABMultiValueRef websites = ABRecordCopyValue(contact, kABPersonURLProperty);
    CFIndex noURL = ABMultiValueGetCount(websites);
    CFStringRef workWebSiteLabel = ABAddressBookCopyLocalizedLabel(kABWorkLabel);
    for(CFIndex k = 0; k < noURL; k++){
        CFStringRef website = ABMultiValueCopyValueAtIndex(websites, k);
        CFStringRef webLabel = ABMultiValueCopyLabelAtIndex(websites, k);
        CFStringRef localLabel = ABAddressBookCopyLocalizedLabel(webLabel);
        if([(__bridge NSString *)localLabel isEqualToString:(__bridge NSString *)workWebSiteLabel]){
            [contactDict setObject:(__bridge NSString *)website forKey:CONTACT_DETAILS_WEBSITE];
        }
        if(localLabel != NULL){
            CFRelease(localLabel);
        }
        if(webLabel != NULL){
            CFRelease(webLabel);
        }
        if(website != NULL){
            CFRelease(website);
        }
        
    }
    if(workWebSiteLabel != NULL){
        CFRelease(workWebSiteLabel);
    }
    if(websites != NULL){
        CFRelease(websites);
    }
    CFDataRef  photo = ABPersonCopyImageData(contact);
    NSString* base64String;
    if (photo) {
        base64String= [(__bridge NSData*)photo base64Encoding];
    }else{
        base64String=@"";
    }
    if(base64String != nil){
        [contactDict setObject:base64String forKey:@"encodedImage"];
    }
    [multiContactArr addObject:contactDict];
    [multiContactDics setObject:multiContactArr forKey:CONTACT_SEARCH_JSON_ROOT];
    NSArray *resultkeys=[NSArray arrayWithObjects:@"contactDetails",nil];
    NSArray *resultValue=[NSArray arrayWithObjects:multiContactDics,nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultValue]];
    
}


-(void)sendMultipleContacts:(CFArrayRef)allContacts{
    ABRecordRef contact;
    NSMutableDictionary *multiContactDics = [@{} mutableCopy];
    NSMutableArray *multiContactArr = [@[] mutableCopy];
    CFIndex numOfContacts = CFArrayGetCount(allContacts);
    int i;
    for (i=0;i<numOfContacts;i++){
        contact = CFArrayGetValueAtIndex(allContacts,i);
        NSMutableDictionary *contactDict = [@{} mutableCopy];
        CFStringRef firstName = ABRecordCopyValue(contact, kABPersonFirstNameProperty);
        if(firstName != NULL){
            [contactDict setObject:(__bridge NSString *)firstName forKey:CONTACT_DETAILS_FIRSTNAME];
            
            if(firstName != NULL){
                CFRelease(firstName);
            }
        }
        CFStringRef lastName = ABRecordCopyValue(contact, kABPersonLastNameProperty);
        if(lastName != NULL){
            [contactDict setObject:(__bridge NSString *)lastName forKey:CONTACT_DETAILS_LASTNAME];
            if(lastName != NULL){
                CFRelease(lastName);
            }
        }
        ABMultiValueRef phones = ABRecordCopyValue(contact, kABPersonPhoneProperty);
        CFIndex phonesLen = ABMultiValueGetCount(phones);
        CFStringRef mobileLabel = ABAddressBookCopyLocalizedLabel(kABPersonPhoneMobileLabel);
        CFStringRef workLabel =  ABAddressBookCopyLocalizedLabel(kABWorkLabel);
        CFStringRef homeLabel = ABAddressBookCopyLocalizedLabel(kABHomeLabel);
        NSCharacterSet *toExclude = [NSCharacterSet characterSetWithCharactersInString:@"/.()-+ "];
        NSString *phNumber = nil;
        for(CFIndex j = 0; j < phonesLen; j++)
        {
            CFStringRef phoneNumber = ABMultiValueCopyValueAtIndex(phones, j);
            CFStringRef locLabel = ABMultiValueCopyLabelAtIndex(phones, j);
            CFStringRef phoneLabel = ABAddressBookCopyLocalizedLabel(locLabel);
            phNumber = [[(__bridge NSString *)phoneNumber componentsSeparatedByCharactersInSet:toExclude] componentsJoinedByString: @""];
            [contactDict setObject:phNumber forKey:(__bridge NSString *)phoneLabel];
            if(phoneNumber != NULL){
                CFRelease(phoneNumber);
            }
            if(locLabel != NULL){
                CFRelease(locLabel);
            }
            if(phoneLabel != NULL){
                CFRelease(phoneLabel);
            }
            
        }
        if(mobileLabel != NULL){
            CFRelease(mobileLabel);
        }
        if(homeLabel != NULL){
            CFRelease(homeLabel);
        }
        if(workLabel != NULL){
            CFRelease(workLabel);
        }
        if(phones != NULL){
            CFRelease(phones);
        }
        
        ABMultiValueRef emails = ABRecordCopyValue(contact, kABPersonEmailProperty);
        CFIndex emailsLen = ABMultiValueGetCount(emails);
        CFStringRef workEmailLabel = ABAddressBookCopyLocalizedLabel(kABWorkLabel);
        for(CFIndex k = 0; k < emailsLen; k++){
            CFStringRef emailID = ABMultiValueCopyValueAtIndex(emails, k);
            CFStringRef locLabel = ABMultiValueCopyLabelAtIndex(emails, k);
            CFStringRef emailLabel = ABAddressBookCopyLocalizedLabel(locLabel);
            if([(__bridge NSString *)emailLabel isEqualToString:(__bridge NSString *)workEmailLabel]){
                [contactDict setObject:(__bridge NSString *)emailID forKey:CONTACT_DETAILS_MAIL];
            }
            if(emailID != NULL){
                CFRelease(emailID);
            }
            if(locLabel != NULL){
                CFRelease(locLabel);
            }
            if(emailLabel != NULL){
                CFRelease(emailLabel);
            }
            
        }
        if(workEmailLabel != NULL){
            CFRelease(workEmailLabel);
        }
        if(emails != NULL){
            CFRelease(emails);
        }
        CFTypeRef adressesReference = ABRecordCopyValue(contact, kABPersonAddressProperty);
        CFIndex mvCount = ABMultiValueGetCount(adressesReference);
        CFStringRef workAddrLabel = ABAddressBookCopyLocalizedLabel(kABWorkLabel);
        if (mvCount > 0) {
            int i =0;
            NSString *streetAddress = nil;
            for (i=0; i < mvCount; i++) {
                CFTypeRef addrValues = ABMultiValueCopyValueAtIndex(adressesReference, i);
                NSDictionary *values = (__bridge NSDictionary *)addrValues;
                streetAddress = [values valueForKey:(NSString *)kABPersonAddressStreetKey];
                if([(__bridge NSString *)workAddrLabel isEqualToString:(__bridge NSString *)workAddrLabel]){
                    if(streetAddress != nil){
                        [contactDict setObject:streetAddress forKey:CONTACT_DETAILS_ADDRESS];
                    }
                }
                if(addrValues != NULL){
                    CFRelease(addrValues);
                }
                
            }
        }
        else{
            [Logger logger_Log:@"I" :@"APZContact--Contact has no Address"];
        }
        
        if(workAddrLabel != NULL){
            CFRelease(workAddrLabel);
        }
        if(adressesReference != NULL){
            CFRelease(adressesReference);
        }
        ABMultiValueRef websites = ABRecordCopyValue(contact, kABPersonURLProperty);
        CFIndex noURL = ABMultiValueGetCount(websites);
        CFStringRef workWebSiteLabel = ABAddressBookCopyLocalizedLabel(kABWorkLabel);
        for(CFIndex k = 0; k < noURL; k++){
            CFStringRef website = ABMultiValueCopyValueAtIndex(websites, k);
            CFStringRef webLabel = ABMultiValueCopyLabelAtIndex(websites, k);
            CFStringRef localLabel = ABAddressBookCopyLocalizedLabel(webLabel);
            if([(__bridge NSString *)localLabel isEqualToString:(__bridge NSString *)workWebSiteLabel]){
                [contactDict setObject:(__bridge NSString *)website forKey:CONTACT_DETAILS_WEBSITE];
            }
            if(localLabel != NULL){
                CFRelease(localLabel);
            }
            if(webLabel != NULL){
                CFRelease(webLabel);
            }
            if(website != NULL){
                CFRelease(website);
            }
        }
        if(workWebSiteLabel != NULL){
            CFRelease(workWebSiteLabel);
        }
        if(websites != NULL){
            CFRelease(websites);
        }
        
        CFDataRef  photo = ABPersonCopyImageData(contact);
        NSString* base64String;
        if (photo) {
            base64String= [(__bridge NSData*)photo base64Encoding];
        }else{
            base64String=@"";
        }
        if(base64String != nil){
            [contactDict setObject:base64String forKey:@"encodedImage"];
        }
        [multiContactArr addObject:contactDict];
        [multiContactDics setObject:multiContactArr forKey:CONTACT_SEARCH_JSON_ROOT];
    }
    if (multiContactDics!=nil) {
        NSArray *resultkeys=[NSArray arrayWithObjects:@"contactDetails",nil];
        NSArray *resultValue=[NSArray arrayWithObjects:multiContactDics,nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultValue]];
    }
}
-(NSString*)getUserDateFormat{
    NSString *path = [[NSString alloc] initWithString:[self.viewController.sandboxPath stringByAppendingFormat:@"/Assets/apps/%@/plist/UserSettings.plist",self.viewController.appString]];
    NSMutableDictionary* settingsDictionary=[[NSMutableDictionary alloc] initWithContentsOfFile:path];
    return [settingsDictionary objectForKey:@"dateFormat"];
}
#pragma mark CONTACTS ADD ERROR MESSAGE
-(void)noAddressBookFoundAdd{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_ACCESS_FAILED_CODE],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    
    [Logger logger_Log:@"E" :@"APZContact--Contact Address Book Permission Denied"];
}

-(void)noContactFoundAdd{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_NOTFOUND_FAILED_CODE],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZContact--No Contacts Found"];
}

#pragma mark CONTACTS EDIT ERROR MESSAGE
-(void)noAddressBookFoundEdit{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_ACCESS_FAILED_CODE],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZContact--Permission denied to access Contacts"];
}

-(void)noContactFoundEdit{
    [Logger logger_Log:@"E" :@"APZContact--No Contacts Found"];
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_NOTFOUND_FAILED_CODE],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
}

#pragma mark - Contacts Delete Error Messages
-(void)noAddressBookFoundDelete{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_ACCESS_FAILED_CODE],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZContact--Permission denied to access Contacts"];
}

-(void)noContactFoundDelete{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_NOTFOUND_FAILED_CODE],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZContact--No Contacts Found"];
}

#pragma mark Contacts Search Error Message
-(void)noAddressBookFoundSearch{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_ACCESS_FAILED_CODE],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZContact--Permission denied to access Contacts"];
}

-(void)noContactFoundSearch{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CONTACT_NOTFOUND_FAILED_CODE],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZContact--No Contacts Found"];
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    [Logger logger_Log:@"E" :@"APZContact--Done"];
}

-(void)dealloc{
    
}


@end


