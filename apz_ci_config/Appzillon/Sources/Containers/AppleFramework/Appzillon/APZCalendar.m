//
//  APZCalendar.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZCalendar.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import <EventKit/EventKit.h>
#import "Logger.h"

@interface APZCalendar()
@property(nonatomic,weak)WKWebView *webView;
@property(strong,nonatomic)EKEventStore *eventStore;
@property(strong,nonatomic) NSString *pluginId;
@property(strong,nonatomic) NSArray *eventsArray;
@property(assign) int eventCreatedCount;
@property(assign) int noOfTimesCreateEventExcuted;
@end

@implementation APZCalendar

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.eventStore = [[EKEventStore alloc] init];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    [Logger logger_Log:@"D" :@"APZCalender--execute"];
    NSString *actionType = [jsonDict objectForKey:CALENDAR_ACTION];
    if([actionType isEqualToString:CALENDAR_ACTION_CREATE])
    {
        self.eventsArray = [jsonDict objectForKey:@"events"];
        if (self.eventsArray != nil){
            self.eventCreatedCount = 0;
            self.noOfTimesCreateEventExcuted = 0;
            for (NSDictionary *eventJson in self.eventsArray) {
                if ((![[eventJson objectForKey:CALENDAR_TITLE]isEqualToString:@""])) {
                    [self calendarAddEvent:eventJson];
                }
            }
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"APZ-CNT-082"],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZCalender--Missing Events"];
            [self cleanPlugin];
        }
    }
    else if([actionType isEqualToString:CALENDAR_ACTION_EDIT])
    {
        if ([self checkForTimeFormat:jsonDict] && (![[jsonDict objectForKey:CALENDAR_TITLE]isEqualToString:@""])&&[self checkForEditTimeFormat:jsonDict]) {
            [self calendarEditEvent:jsonDict];
            
        }else{
            if ([[jsonDict objectForKey:CALENDAR_TITLE]isEqualToString:@""]) {
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDER_TITLE_MISSING],nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                
                [Logger logger_Log:@"E" :@"APZCalender--Missing Event Title"];
            }
        }
    }
    else if ([actionType isEqualToString:CALENDAR_ACTION_DELETE]) {
        if ([self checkForTimeFormat:jsonDict] && (![[jsonDict objectForKey:CALENDAR_TITLE]isEqualToString:@""])) {
            [self calendarDeleteEvent:jsonDict];
        }else{
            if ([[jsonDict objectForKey:CALENDAR_TITLE]isEqualToString:@""]) {
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDER_TITLE_MISSING],nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                [Logger logger_Log:@"E" :@"APZCalender--Missing Event Title"];
            }
        }
    }
    else{
        [Logger logger_Log:@"E" :@"APZCalender--No Calendar operation present for given opnTyp"];
    }
}

-(BOOL)checkForTimeFormat:(NSDictionary *)jsonDict{
    NSString *startDate= [NSString stringWithFormat:@"%@ %@" ,[jsonDict objectForKey:CALENDAR_START_DATE],[jsonDict objectForKey:CALENDAR_STARTTIME]] ;
    NSString *endDate= [NSString stringWithFormat:@"%@ %@" ,[jsonDict objectForKey:CALENDAR_END_DATE],[jsonDict objectForKey:CALENDAR_ENDTIME]] ;
    NSDateFormatter *dateFormatter1 = [[NSDateFormatter alloc]init];
    [dateFormatter1 setDateFormat:@"dd-MM-yyyy HH:mm:ss"];
    NSDateFormatter *dateFormatter2 = [[NSDateFormatter alloc]init];
    [dateFormatter2 setDateFormat:@"dd-MM-yyyy HH:mm"];
    if ( [dateFormatter1 dateFromString:startDate] &&[dateFormatter1 dateFromString:endDate]) {
        return YES;
    }else if ([dateFormatter2 dateFromString:startDate] &&[dateFormatter2 dateFromString:endDate]){
        return YES;
    }
    else{
        if ([dateFormatter1 dateFromString:startDate]||[dateFormatter2 dateFromString:startDate]) {
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDER_DATE_FORMATE],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZCalender--Wrong Time Fromat"];
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDER_DATE_FORMATE],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZCalender--Wrong Time Fromat"];
        }
        return NO;
    }
}

-(BOOL)checkForEditTimeFormat:(NSDictionary *)jsonDict{
    NSString *startDate= [NSString stringWithFormat:@"%@ %@" ,[jsonDict objectForKey:CALENDAR_START_DATE],[jsonDict objectForKey:CALENDAR_NEW_STARTTIME]] ;
    NSString *endDate= [NSString stringWithFormat:@"%@ %@" ,[jsonDict objectForKey:CALENDAR_END_DATE],[jsonDict objectForKey:CALENDAR_NEW_ENDTIME]] ;
    NSDateFormatter *dateFormatter1 = [[NSDateFormatter alloc]init];
    [dateFormatter1 setDateFormat:@"dd-MM-yyyy HH:mm:ss"];
    NSDateFormatter *dateFormatter2 = [[NSDateFormatter alloc]init];
    [dateFormatter2 setDateFormat:@"dd-MM-yyyy HH:mm"];
    if ( [dateFormatter1 dateFromString:startDate] &&[dateFormatter1 dateFromString:endDate]) {
        return YES;
    }else if ([dateFormatter2 dateFromString:startDate] &&[dateFormatter2 dateFromString:endDate]){
        return YES;
    }
    else{
        if ([dateFormatter1 dateFromString:startDate]||[dateFormatter2 dateFromString:startDate]) {
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDER_DATE_FORMATE],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZCalender--Wrong Time Fromat"];
        }else{
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDER_DATE_FORMATE],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZCalender--Wrong Time Fromat"];
        }
        return NO;
    }
}

#pragma mark - CALENDAR EVENTS
-(void)calendarAddEvent:(NSDictionary *)eventDetails{
    BOOL isIOS6 = [self.eventStore respondsToSelector:@selector(requestAccessToEntityType:completion:)];
    if(isIOS6){
        [self.eventStore requestAccessToEntityType:EKEntityTypeEvent completion:^(BOOL granted,     NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if(granted){
                    [self createEvent:eventDetails];
                }
                else{
                    [self noCalendarAccess:error];
                }
            });
        }];
    }
    else{
        [self createEvent:eventDetails];
    }
}

-(BOOL)checkForDuplicateEvents:(NSDictionary *)eventDetails{
    
    BOOL createEvent = NO;
    
    NSString *title = [eventDetails objectForKey:CALENDAR_TITLE];
    NSString *startDate= [NSString stringWithFormat:@"%@ %@" ,[eventDetails objectForKey:CALENDAR_START_DATE],[eventDetails objectForKey:CALENDAR_STARTTIME]] ;
    NSString *endDate= [NSString stringWithFormat:@"%@ %@" ,[eventDetails objectForKey:CALENDAR_END_DATE],[eventDetails objectForKey:CALENDAR_ENDTIME]] ;
    NSDate *sDate;
    NSDate *eDate;
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
    dateFormatter.dateFormat=@"dd-MM-yyyy HH:mm:ss";
    sDate = [dateFormatter dateFromString:startDate];
    eDate = [dateFormatter dateFromString:endDate];
    if (sDate&&eDate) {
        EKEvent *event = [self searchEvent:title startDate:sDate endDate:eDate];
        if (event == nil) {
            createEvent = YES;
        }
        else
            createEvent = NO;
    }else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDAR_EVENT_ADD_FAILED_CODE],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [self cleanPlugin];
    }
    
    return  createEvent;
}

-(void)createEvent:(NSDictionary *)eventDetails{
    if ([self checkForDuplicateEvents:eventDetails]) {
        self.noOfTimesCreateEventExcuted += 1;
        EKEvent *event = [EKEvent eventWithEventStore:self.eventStore];
        event.title = [eventDetails objectForKey:CALENDAR_TITLE];
        event.notes = [eventDetails objectForKey:CALENDAR_SUMMARY];
        NSString *startDate= [NSString stringWithFormat:@"%@ %@" ,[eventDetails objectForKey:CALENDAR_START_DATE],[eventDetails objectForKey:CALENDAR_STARTTIME]] ;
        NSString *endDate= [NSString stringWithFormat:@"%@ %@" ,[eventDetails objectForKey:CALENDAR_END_DATE],[eventDetails objectForKey:CALENDAR_ENDTIME]] ;
        BOOL fullDateTimeFormat;
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
        dateFormatter.dateFormat=@"dd-MM-yyyy HH:mm:ss";
        event.startDate = [dateFormatter dateFromString:startDate];
        event.endDate = [dateFormatter dateFromString:endDate];
        if ([dateFormatter dateFromString:startDate]&&[dateFormatter dateFromString:endDate]) {
            fullDateTimeFormat=YES;
        }else{
            dateFormatter.dateFormat=@"dd-MM-yyyy HH:mm";
            event.startDate = [dateFormatter dateFromString:startDate];
            event.endDate = [dateFormatter dateFromString:endDate];
            fullDateTimeFormat=NO;
        }
        
        event.location=[eventDetails objectForKey:CALENDAR_LOCATION];
        [event setCalendar:[self.eventStore defaultCalendarForNewEvents]];
        NSString *frequency = [eventDetails objectForKey:CALENDAR_FREQUENCY];
        frequency=[frequency lowercaseString];
        BOOL isRecurringOccurs = ([frequency isEqualToString:CALENDAR_FREQUENCY_DAILY] ||
                                  [frequency isEqualToString:CALENDAR_FREQUENCY_WEEKLY] ||
                                  [frequency isEqualToString:CALENDAR_FREQUENCY_MONTHLY]);
        if(isRecurringOccurs){
            EKRecurrenceFrequency eventFreq;
            if([frequency isEqualToString:CALENDAR_FREQUENCY_DAILY]){
                eventFreq =EKRecurrenceFrequencyDaily;
            }
            else if([frequency isEqualToString:CALENDAR_FREQUENCY_WEEKLY]){
                eventFreq =EKRecurrenceFrequencyWeekly;
            }
            else if([frequency isEqualToString:CALENDAR_FREQUENCY_MONTHLY]){
                eventFreq =EKRecurrenceFrequencyMonthly;
            }
            else{
                eventFreq = EKRecurrenceFrequencyYearly;
            }
            NSDate * recurrenceEndDate;
            if (fullDateTimeFormat==YES) {
                recurrenceEndDate=  [dateFormatter dateFromString:[NSString stringWithFormat:@"%@ 23:59:59",[eventDetails objectForKey:CALENDAR_RECURRENCE_END]]];
            }else{
                recurrenceEndDate=  [dateFormatter dateFromString:[NSString stringWithFormat:@"%@ 23:59",[eventDetails objectForKey:CALENDAR_RECURRENCE_END]]];
            }
            EKRecurrenceRule *recurringRule ;
            if (recurrenceEndDate) {
                EKRecurrenceEnd *recurringEnd =
                [EKRecurrenceEnd recurrenceEndWithEndDate:recurrenceEndDate];
                
                recurringRule =
                [[EKRecurrenceRule alloc] initRecurrenceWithFrequency:eventFreq interval:1
                                                                  end:recurringEnd];
            }else{
                recurringRule =
                [[EKRecurrenceRule alloc] initRecurrenceWithFrequency:eventFreq interval:1
                                                                  end:nil];
            }
            event.recurrenceRules = [[NSArray alloc] initWithObjects:recurringRule, nil];
        }
        else{
            NSLog(@"---No Recurrence present--");
        }
        //Check for ALARM
        if([[eventDetails objectForKey:CALENDAR_ALARM] isEqualToString:CALENDAR_ALARM_5_MINUTES]){
            event.alarms=[NSArray arrayWithObject:[EKAlarm alarmWithRelativeOffset:60.0f * -5.0f]] ;
        }else if([[eventDetails objectForKey:CALENDAR_ALARM] isEqualToString:CALENDAR_ALARM_15_MINUTES]){
            event.alarms=[NSArray arrayWithObject:[EKAlarm alarmWithRelativeOffset:60.0f * -15.0f]] ;
        }else if([[eventDetails objectForKey:CALENDAR_ALARM] isEqualToString:CALENDAR_ALARM_1_HOUR]){
            event.alarms=[NSArray arrayWithObject:[EKAlarm alarmWithRelativeOffset:60.0f * -60.0f]] ;
        }else if([[eventDetails objectForKey:CALENDAR_ALARM] isEqualToString:CALENDAR_ALARM_1_DAY]){
            event.alarms=[NSArray arrayWithObject:[EKAlarm alarmWithRelativeOffset:60.0f * -60.0f*24.0f]] ;
        }else{
            event.alarms=nil;
        }
        NSError *saveError = nil;
        BOOL result = [self.eventStore saveEvent:event
                                            span:EKSpanThisEvent error:&saveError];
        if (result == YES){
            self.eventCreatedCount += 1;
        }
        
        
        if (self.eventsArray.count == self.noOfTimesCreateEventExcuted) {
            if (self.eventCreatedCount != 0) {
                NSArray *resultkeys=[NSArray arrayWithObjects:@"success",nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%d events created", self.eventCreatedCount],nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
                [self cleanPlugin];
            }else{
                NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
                NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDAR_EVENT_ADD_FAILED_CODE],nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
                [self cleanPlugin];
            }
        }
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDAR_EVENT_ADD_FAILED_CODE],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [self cleanPlugin];
    }
}

-(void)calendarEditEvent:(NSDictionary *)eventDetails{
    BOOL isIOS6 = [self.eventStore respondsToSelector:@selector(requestAccessToEntityType:completion:)];
    if(isIOS6){
        [self.eventStore requestAccessToEntityType:EKEntityTypeEvent completion:^(BOOL granted,     NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if(granted){
                    [self editEvent:eventDetails];
                }
                else{
                    [self noCalendarAccess:error];
                }
            });
        }];
    }
    else{
        [self editEvent:eventDetails];
    }
}

-(void)editEvent:(NSDictionary *)eventDetails{
    NSString *title = [eventDetails objectForKey:CALENDAR_TITLE];
    NSString *startDate= [NSString stringWithFormat:@"%@ %@" ,[eventDetails objectForKey:CALENDAR_START_DATE],[eventDetails objectForKey:CALENDAR_STARTTIME]] ;
    NSString *endDate= [NSString stringWithFormat:@"%@ %@" ,[eventDetails objectForKey:CALENDAR_END_DATE],[eventDetails objectForKey:CALENDAR_ENDTIME]] ;
    BOOL fullDateTimeFormat;
    NSDate *sDate;
    NSDate *eDate;
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
    dateFormatter.dateFormat=@"dd-MM-yyyy HH:mm:ss";
    sDate =[dateFormatter dateFromString:startDate];
    eDate=[dateFormatter dateFromString:endDate];
    if (sDate&&eDate) {
        fullDateTimeFormat=YES;
    }else{
        dateFormatter.dateFormat=@"dd-MM-yyyy HH:mm";
        sDate =[dateFormatter dateFromString:startDate];
        eDate=[dateFormatter dateFromString:endDate];
        fullDateTimeFormat=NO;
    }
    EKEvent *event = [self searchEvent:title startDate:sDate endDate:eDate];
    if(event != nil){
        event.title = [eventDetails objectForKey:CALENDAR_TITLE];
        event.notes = [eventDetails objectForKey:CALENDAR_SUMMARY];
        NSString *newStartDate= [NSString stringWithFormat:@"%@ %@" ,[eventDetails objectForKey:CALENDAR_NEW_STARTDATE],[eventDetails objectForKey:CALENDAR_NEW_STARTTIME]] ;
        NSString *newEndDate= [NSString stringWithFormat:@"%@ %@" ,[eventDetails objectForKey:CALENDAR_NEW_ENDDATE],[eventDetails objectForKey:CALENDAR_NEW_ENDTIME]] ;
        NSDate *sNewStartDate=[dateFormatter dateFromString:newStartDate];
        [event setStartDate:sNewStartDate];
        NSDate *eNewEndDate=[dateFormatter dateFromString:newEndDate];
        [event setEndDate:eNewEndDate];
        event.location=[eventDetails objectForKey:CALENDAR_LOCATION];
        [event setCalendar:[self.eventStore defaultCalendarForNewEvents]];
        NSString *frequency = [eventDetails objectForKey:CALENDAR_FREQUENCY];
        frequency=[frequency lowercaseString];
        BOOL isRecurringOccurs = ([frequency isEqualToString:CALENDAR_FREQUENCY_DAILY] ||
                                  [frequency isEqualToString:CALENDAR_FREQUENCY_WEEKLY] ||
                                  [frequency isEqualToString:CALENDAR_FREQUENCY_MONTHLY]);
        if(isRecurringOccurs){
            EKRecurrenceFrequency eventFreq;
            if([frequency isEqualToString:CALENDAR_FREQUENCY_DAILY]){
                eventFreq =EKRecurrenceFrequencyDaily;
            }
            else if([frequency isEqualToString:CALENDAR_FREQUENCY_WEEKLY]){
                eventFreq =EKRecurrenceFrequencyWeekly;
            }
            else if([frequency isEqualToString:CALENDAR_FREQUENCY_MONTHLY]){
                eventFreq =EKRecurrenceFrequencyMonthly;
            }
            else{
                eventFreq = EKRecurrenceFrequencyYearly;
            }
            NSDate * recurrenceEndDate;
            if (fullDateTimeFormat==YES) {
                recurrenceEndDate=  [dateFormatter dateFromString:[NSString stringWithFormat:@"%@ 23:59:59",[eventDetails objectForKey:CALENDAR_RECURRENCE_END]]];
            }else{
                recurrenceEndDate=  [dateFormatter dateFromString:[NSString stringWithFormat:@"%@ 23:59",[eventDetails objectForKey:CALENDAR_RECURRENCE_END]]];
            }
            EKRecurrenceRule *recurringRule ;
            if (recurrenceEndDate) {
                EKRecurrenceEnd *recurringEnd =
                [EKRecurrenceEnd recurrenceEndWithEndDate:recurrenceEndDate];
                
                recurringRule =
                [[EKRecurrenceRule alloc] initRecurrenceWithFrequency:eventFreq interval:1
                                                                  end:recurringEnd];
            }else{
                recurringRule =
                [[EKRecurrenceRule alloc] initRecurrenceWithFrequency:eventFreq interval:1
                                                                  end:nil];
            }
            event.recurrenceRules = [[NSArray alloc] initWithObjects:recurringRule, nil];
        }
        else{
            [Logger logger_Log:@"I" :@"APZCalender--NoReourrence"];
        }
        if([[eventDetails objectForKey:CALENDAR_ALARM] isEqualToString:CALENDAR_ALARM_5_MINUTES]){
            event.alarms=[NSArray arrayWithObject:[EKAlarm alarmWithRelativeOffset:60.0f * -5.0f]] ;
        }else if([[eventDetails objectForKey:CALENDAR_ALARM] isEqualToString:CALENDAR_ALARM_15_MINUTES]){
            event.alarms=[NSArray arrayWithObject:[EKAlarm alarmWithRelativeOffset:60.0f * -15.0f]];
            [Logger logger_Log:@"I" :@"APZCalender--Creating Alarm CALENDAR_ALARM_15_MINUTES for given event"];
        }else if([[eventDetails objectForKey:CALENDAR_ALARM] isEqualToString:CALENDAR_ALARM_1_HOUR]){
            event.alarms=[NSArray arrayWithObject:[EKAlarm alarmWithRelativeOffset:60.0f * -60.0f]] ;
            [Logger logger_Log:@"I" :@"APZCalender--Creating Alarm CALENDAR_ALARM_1_HOUR for given event"];
        }else if([[eventDetails objectForKey:CALENDAR_ALARM] isEqualToString:CALENDAR_ALARM_1_DAY]){
            event.alarms=[NSArray arrayWithObject:[EKAlarm alarmWithRelativeOffset:60.0f * -60.0f*24.0f]] ;
            [Logger logger_Log:@"I" :@"APZCalender--Creating Alarm CALENDAR_ALARM_1_DAY for given event"];
        }else{
            event.alarms=nil;
        }
        NSError *saveError = nil;
        BOOL result=[self.eventStore saveEvent:event
                                          span:EKSpanFutureEvents error:&saveError];
        if (result == YES){
            NSArray *resultkeys=nil;
            NSArray *result=nil;
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
            
            [Logger logger_Log:@"I" :@"APZCalender--Event Modify Success"];
        }
        else{
            
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDAR_EVENT_EDIT_FAILED_CODE],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            NSString *errorString=[NSString stringWithFormat:@"APZCalender--%@",[saveError localizedDescription]];
            [Logger logger_Log:@"E" :errorString];
        }
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDAR_EVENT_NOTFOUND_FAILED_CODE],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZCalender--Event Not Found"];
    }
    [self cleanPlugin];
}

-(void)calendarDeleteEvent:(NSDictionary *)eventDetails{
    BOOL isIOS6 = [self.eventStore respondsToSelector:@selector(requestAccessToEntityType:completion:)];
    if(isIOS6){
        [self.eventStore requestAccessToEntityType:EKEntityTypeEvent completion:^(BOOL granted,     NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if(granted){
                    [self deleteEvent:eventDetails];
                }
                else{
                    [self noCalendarAccess:error];
                }
            });
        }];
    }
    else{
        [self deleteEvent:eventDetails];
    }
}

-(void)deleteEvent:(NSDictionary *)eventDetails{
    NSString *title = [eventDetails objectForKey:CALENDAR_TITLE];
    NSString *startDate= [NSString stringWithFormat:@"%@ %@" ,[eventDetails objectForKey:CALENDAR_START_DATE],[eventDetails objectForKey:CALENDAR_STARTTIME]] ;
    NSString *endDate= [NSString stringWithFormat:@"%@ %@" ,[eventDetails objectForKey:CALENDAR_END_DATE],[eventDetails objectForKey:CALENDAR_ENDTIME]] ;
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
    NSDate *sDate;
    NSDate *eDate;
    dateFormatter.dateFormat=@"dd-MM-yyyy HH:mm:ss";
    sDate =[dateFormatter dateFromString:startDate];
    eDate=[dateFormatter dateFromString:endDate];
    if (sDate&&eDate) {
    }else{
        dateFormatter.dateFormat=@"dd-MM-yyyy HH:mm";
        sDate =[dateFormatter dateFromString:startDate];
        eDate=[dateFormatter dateFromString:endDate];
    }
    NSError *deleteError = nil;
    EKEvent *event = [self searchEvent:title startDate:sDate endDate:eDate];
    if(event != nil){
        BOOL result;
        if ([[eventDetails objectForKey:CALENDAR_FUTUREEVENTS] isEqualToString:@"true"]) {
            result=[self.eventStore removeEvent:event span:EKSpanFutureEvents
                                         commit:YES error:&deleteError];
        }else{
            result=[self.eventStore removeEvent:event span:EKSpanThisEvent
                                         commit:YES error:&deleteError];
        }
        if (result){
            
            NSArray *resultkeys=nil;
            NSArray *result=nil;
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :result]];
            
            [Logger logger_Log:@"I" :@"APZCalender--Event Deleted Success"];
        }
        else{
            
            NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
            NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDAR_EVENT_DELETE_FAILED_CODE],nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
            [Logger logger_Log:@"E" :@"APZCalender--Event Deleted Failure"];
        }
    }
    else{
        NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
        NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDAR_EVENT_NOTFOUND_FAILED_CODE],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
        [Logger logger_Log:@"E" :@"APZCalender--No Event exists in Calendar"];
    }
    [self cleanPlugin];
}

-(EKEvent *)searchEvent:(NSString *)title startDate:(NSDate *)sDate endDate:(NSDate *)eDate{
    EKEvent *reqEvent = nil;
    NSPredicate *predicate =
    [self.eventStore predicateForEventsWithStartDate:sDate
                                             endDate:eDate calendars:nil];
    NSArray *events = [self.eventStore eventsMatchingPredicate:predicate];
    for (EKEvent *event in events){
        if([event.title isEqualToString:title]){
            reqEvent = event;
            break;
        }
    }
    return reqEvent;
}

-(void)noCalendarAccess:(NSError *)error{
    NSArray *resultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *result=[NSArray arrayWithObjects:[NSString stringWithFormat:CALENDAR_ACCESS_FAILED_CODE],nil];
    [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :result]];
    [Logger logger_Log:@"E" :@"APZCalender--Access Denied"];
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    self.webView = nil;
    self.eventStore = nil;
    self.eventsArray = nil;
    self.eventCreatedCount = 0;
    self.noOfTimesCreateEventExcuted = 0;
    [self.delegate donePlugin:self];
}
@end



