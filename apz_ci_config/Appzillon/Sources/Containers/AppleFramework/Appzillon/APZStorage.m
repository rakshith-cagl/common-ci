//
//  APZStorage.m
//  Appzillon
//
//  Created by Admin on 26/09/13.
//
//

#import "APZStorage.h"
#import "APZJsonUtil.h"
#import "Constants.h"
#import "sqlite3.h"
#import "Logger.h"
#import "AppzillonViewController.h"
#import "CryptoUtility.h"


@interface APZStorage()
@property(nonatomic,weak)WKWebView *webView;
@property(nonatomic,strong)NSString *pluginId;
@property(nonatomic,weak)AppzillonViewController *viewController;
@end

@implementation APZStorage

-(id)initPlugin:(WKWebView *)wbView  :(NSDictionary *)jsonDict{
    self = [super init];
    if (self) {
        self.webView = wbView;
        self.viewController = (AppzillonViewController *)[MiscellaneousMethods getAppzillonViewController];
    }
    return self;
}

-(void)executePlugin:(NSDictionary *)jsonDict{
    [Logger logger_Log:@"D" :@"APZStorage--execute"];
    self.pluginId=[jsonDict objectForKey:PLUGINID];
    [self runSQL:jsonDict];
    [self cleanPlugin];
}

-(BOOL)runSQL:(NSDictionary *)sqlDetails{
    BOOL isSQLExecuted =NO;
    NSString *query =[sqlDetails objectForKey:STORAGE_QUERY_EXECUTEQUERY];
    NSString *queryType = [[query componentsSeparatedByString:@" "] objectAtIndex:0];
    queryType = [queryType lowercaseString];
    if([queryType isEqualToString:@"create"]){
        [Logger logger_Log:@"I" :@"APZStorage--createQuery"];
        isSQLExecuted = [self createQuery:sqlDetails];
    }
    else if([queryType isEqualToString:@"select"]){
        [Logger logger_Log:@"I" :@"APZStorage--selectQuery"];
        isSQLExecuted = [self selectQuery:sqlDetails];
    }
    else if([queryType isEqualToString:@"insert"]){
        [Logger logger_Log:@"I" :@"APZStorage--insertQuery"];
        isSQLExecuted = [self insertQuery:sqlDetails];
    }
    else if([queryType isEqualToString:@"update"]){
        [Logger logger_Log:@"I" :@"APZStorage--updateQuery"];
        isSQLExecuted = [self updateQuery:sqlDetails];
    }
    else if([queryType isEqualToString:@"delete"]){
        [Logger logger_Log:@"I" :@"APZStorage--deleteQuery"];
        isSQLExecuted = [self deleteQuery:sqlDetails];
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,  QUERY_ID,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:SYSTEM_QUERY_ERROR,[sqlDetails objectForKey:QUERY_ID],nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        
        [Logger logger_Log:@"E" :@"APZStorage--Invalid SQL query, please check query"];
        isSQLExecuted =NO;
    }
    return isSQLExecuted;
}

-(BOOL)createQuery:(NSDictionary *)sqlDetails{
    NSString *dbPathInDocumentsDirectory=[self retrunDatabsePath:sqlDetails];
    sqlite3 *databaseRunSQL = NULL;
    int sqlStatus = sqlite3_open([dbPathInDocumentsDirectory UTF8String],&databaseRunSQL);
    if(sqlStatus==SQLITE_OK)
    {
        const char *sql=(char *)[[sqlDetails objectForKey:STORAGE_QUERY_EXECUTEQUERY] UTF8String];
        sqlite3_stmt *createStatement;
        sqlStatus = sqlite3_prepare_v2(databaseRunSQL, sql, -1, &createStatement, NULL);
        if(sqlStatus == SQLITE_OK)
        {
            int sqlResp=sqlite3_step(createStatement);
            if(sqlResp == SQLITE_DONE){
                NSArray * resultkeys=[NSArray arrayWithObjects:QUERY_ID,  STORAGE_QUERY_JSONKEY_RESULT,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:[sqlDetails objectForKey:QUERY_ID],@"success",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                sqlite3_finalize(createStatement);
                sqlite3_close(databaseRunSQL);
                [Logger logger_Log:@"I" :@"APZStorage--create success"];
                return YES;
            }
            else{
                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,  QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:CREATE_QUERY_FAILED,[sqlDetails objectForKey:QUERY_ID],@"",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                sqlite3_finalize(createStatement);
                sqlite3_close(databaseRunSQL);
                [Logger logger_Log:@"E" :@"APZStorage--Create Table query failed"];
                return NO;
            }
        }
        else{
            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:SYSTEM_QUERY_ERROR,[sqlDetails objectForKey:QUERY_ID],@"",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
            sqlite3_close(databaseRunSQL);
            NSString *errorString=[NSString stringWithFormat:@"APZStorage--Create Table query failed%d",sqlStatus];
            [Logger logger_Log:@"E" :errorString];
            return NO;
        }
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:DATABASE_OPEN_ERROR,[sqlDetails objectForKey:QUERY_ID],@"",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        sqlite3_close(databaseRunSQL);
        [Logger logger_Log:@"E" :@"APZStorage--Unable to open DB file"];
        return NO;
    }
}

-(BOOL)selectQuery:(NSDictionary *)sqlDetails{
    NSString *dbPathInDocumentsDirectory=[self retrunDatabsePath:sqlDetails];
    sqlite3 *databaseRunSQL = NULL;
    int sqlStatus = sqlite3_open([dbPathInDocumentsDirectory UTF8String],&databaseRunSQL);
    if(sqlStatus == SQLITE_OK)
    {
        const char *sql=(char *)[[sqlDetails objectForKey:STORAGE_QUERY_EXECUTEQUERY] UTF8String];
        sqlite3_stmt *selectStatement;
        sqlStatus = sqlite3_prepare_v2(databaseRunSQL, sql, -1, &selectStatement, NULL);
        if(sqlStatus == SQLITE_OK)
        {
            int sqlResp=sqlite3_step(selectStatement);
            if(sqlResp == SQLITE_ROW ) {
                NSMutableDictionary *outputDic=[[NSMutableDictionary alloc] init];
                NSMutableArray *outputArray=[[NSMutableArray alloc] init];
                NSString *columnName;
                for (int column=0; column<sqlite3_column_count(selectStatement); column++)
                {
                    columnName= [NSString stringWithUTF8String:(char *)sqlite3_column_name(selectStatement, column)];
                    int dataType = sqlite3_column_type(selectStatement, column);
                    
                    if (dataType==SQLITE_INTEGER) {
                        [outputDic setObject:[NSString stringWithFormat:@"%d",sqlite3_column_int(selectStatement,column) ] forKey:columnName];
                    }
                    else if(dataType == SQLITE_FLOAT){
                        [outputDic setObject:[NSString stringWithFormat:@"%f",sqlite3_column_double(selectStatement,column) ] forKey:columnName];
                    }
                    else if(dataType == SQLITE_TEXT){
                        const unsigned char *textValue =sqlite3_column_text(selectStatement,column);
                        if(textValue != NULL){
                            [outputDic setObject:[NSString stringWithUTF8String:(char *)textValue]forKey:columnName];
                        }
                        else{
                            [outputDic setObject:@"" forKey:columnName];
                        }
                    }
                    else{
                        [outputDic setObject:@"" forKey:columnName];
                    }
                }
                [outputArray addObject:outputDic];
                while (sqlite3_step(selectStatement)== SQLITE_ROW ) {
                    NSMutableDictionary *outputRowDic=[[NSMutableDictionary alloc] init];
                    for (int column=0; column<sqlite3_column_count(selectStatement); column++)
                    {
                        columnName= [NSString stringWithUTF8String:(char *)sqlite3_column_name(selectStatement, column)];
                        int dataType = sqlite3_column_type(selectStatement, column);
                        if (dataType==SQLITE_INTEGER){
                            [outputRowDic setObject:[NSString stringWithFormat:@"%d",sqlite3_column_int(selectStatement,column) ] forKey:columnName];
                        }
                        else if(dataType == SQLITE_FLOAT){
                            [outputRowDic setObject:[NSString stringWithFormat:@"%f",sqlite3_column_double(selectStatement,column) ] forKey:columnName];
                        }
                        else if(dataType == SQLITE_TEXT){
                            const unsigned char *textValue =sqlite3_column_text(selectStatement,column);
                            if(textValue != NULL){
                                [outputRowDic setObject:[NSString stringWithUTF8String:(char *)textValue]forKey:columnName];
                            }
                            else{
                                [outputRowDic setObject:@"" forKey:columnName];
                            }
                        }
                        else{
                            [outputDic setObject:@"" forKey:columnName];
                        }
                    }
                    [outputArray addObject:outputRowDic];
                }
                
                //To decrypt the notification messages.
                NSString *queryString = [NSString stringWithFormat:@"%@",[sqlDetails objectForKey:STORAGE_QUERY_EXECUTEQUERY]];
                 if ([queryString containsString:@"tb_notifications"]) {
                     for (int i=0; i<[outputArray count]; i++) {
                         NSMutableDictionary *tempOutPutDict = [NSMutableDictionary new];
                        [tempOutPutDict setObject:[[outputArray objectAtIndex:i] objectForKey:@"id"] forKey:@"id"];
                         NSString *decryptedMessage=[CryptoUtility getAESDecryptedString:[[outputArray objectAtIndex:i] objectForKey:@"message"] :STORAGE_AES_KEY];
                         [tempOutPutDict setObject:decryptedMessage forKey:@"message"];
                         [tempOutPutDict setObject:[[outputArray objectAtIndex:i] objectForKey:@"readFlag"] forKey:@"readFlag"];
                         [tempOutPutDict setObject:[[outputArray objectAtIndex:i] objectForKey:@"timeStamp"] forKey:@"timeStamp"];
                         [outputArray replaceObjectAtIndex:i withObject:tempOutPutDict];
                    }
                }
                NSString *outputArrayString= [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:outputArray options:0 error:nil] encoding:NSUTF8StringEncoding];
                NSArray * resultkeys=[NSArray arrayWithObjects:QUERY_ID,  STORAGE_QUERY_JSONKEY_RESULT,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:[sqlDetails objectForKey:QUERY_ID],outputArrayString,nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                sqlite3_finalize(selectStatement);
                sqlite3_close(databaseRunSQL);
                [Logger logger_Log:@"I" :@"APZStorage--Select Query Success"];
                return YES;
            }
            else{
                [Logger logger_Log:@"I" :@"APZStorage--Select Query Success with no rows in table"];
                NSArray * resultkeys=[NSArray arrayWithObjects:QUERY_ID,  STORAGE_QUERY_JSONKEY_RESULT,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:[sqlDetails objectForKey:QUERY_ID],@"{}",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                sqlite3_finalize(selectStatement);
                sqlite3_close(databaseRunSQL);
                return YES;
            }
        }
        else{
            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:SYSTEM_QUERY_ERROR,[sqlDetails objectForKey:QUERY_ID],@"",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
            sqlite3_close(databaseRunSQL);
            NSString *errorString=[NSString stringWithFormat:@"APZStorage--select failed with error code%d",sqlStatus];
            [Logger logger_Log:@"E" :errorString];
            return NO;
        }
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:DATABASE_OPEN_ERROR,[sqlDetails objectForKey:QUERY_ID],@"",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        sqlite3_close(databaseRunSQL);
        [Logger logger_Log:@"E" :@"APZStorage--Select Unable to open DB file"];
        return NO;
    }
}

-(BOOL)insertQuery:(NSDictionary *)sqlDetails{
    NSString *dbPathInDocumentsDirectory=[self retrunDatabsePath:sqlDetails];
    sqlite3 *databaseRunSQL = NULL;
    int sqlStatus = sqlite3_open([dbPathInDocumentsDirectory UTF8String],&databaseRunSQL);
    if(sqlStatus == SQLITE_OK)
    {
        const char *sql=(char *)[[sqlDetails objectForKey:STORAGE_QUERY_EXECUTEQUERY] UTF8String];
        sqlite3_stmt *insertStatement;
        sqlStatus = sqlite3_prepare_v2(databaseRunSQL, sql, -1, &insertStatement, NULL);
        if(sqlStatus == SQLITE_OK)
        {
            int sqlResp=sqlite3_step(insertStatement);
            if(sqlResp == SQLITE_DONE){
                NSArray * resultkeys=[NSArray arrayWithObjects:QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:[sqlDetails objectForKey:QUERY_ID],@"success",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                sqlite3_finalize(insertStatement);
                sqlite3_close(databaseRunSQL);
                [Logger logger_Log:@"I" :@"APZStorage--Insert Query Success"];
                return YES;
            }
            else{
                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,  QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:INSERT_QUERY_FAILED,[sqlDetails objectForKey:QUERY_ID],"",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                sqlite3_finalize(insertStatement);
                sqlite3_close(databaseRunSQL);
                [Logger logger_Log:@"E" :@"APZStorage--Insert Query Failed"];
                return NO;
            }
        }
        else{
            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:SYSTEM_QUERY_ERROR,[sqlDetails objectForKey:QUERY_ID],@"",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
            sqlite3_close(databaseRunSQL);
            [Logger logger_Log:@"E" :@"APZStorage--Insert Query Failed"];
            return NO;
        }
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:DATABASE_OPEN_ERROR,[sqlDetails objectForKey:QUERY_ID],@"",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        sqlite3_close(databaseRunSQL);
        [Logger logger_Log:@"E" :@"APZStorage--Insert Unable to open DB file"];
        return NO;
    }
}

-(BOOL)updateQuery:(NSDictionary *)sqlDetails{
    NSString *dbPathInDocumentsDirectory=[self retrunDatabsePath:sqlDetails];
    sqlite3 *databaseRunSQL = NULL;
    int sqlStatus = sqlite3_open([dbPathInDocumentsDirectory UTF8String],&databaseRunSQL);
    if(sqlStatus == SQLITE_OK)
    {
        const char *sql=(char *)[[sqlDetails objectForKey:STORAGE_QUERY_EXECUTEQUERY] UTF8String];
        sqlite3_stmt *updateStatement;
        sqlStatus = sqlite3_prepare_v2(databaseRunSQL, sql, -1, &updateStatement, NULL);
        if(sqlStatus == SQLITE_OK )
        {
            int sqlResp=sqlite3_step(updateStatement);
            if(sqlResp == SQLITE_DONE){
                NSArray * resultkeys=[NSArray arrayWithObjects:QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:[sqlDetails objectForKey:QUERY_ID],@"success",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                sqlite3_finalize(updateStatement);
                sqlite3_close(databaseRunSQL);
                [Logger logger_Log:@"I" :@"APZStorage--Update Query Success"];
                return YES;
            }
            else{
                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:UPDATE_QUERY_FAILED,[sqlDetails objectForKey:QUERY_ID],@"",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                sqlite3_finalize(updateStatement);
                sqlite3_close(databaseRunSQL);
                [Logger logger_Log:@"I" :@"APZStorage--Update query failed"];
                return NO;
            }
        }
        else{
            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:SYSTEM_QUERY_ERROR,[sqlDetails objectForKey:QUERY_ID],@"",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
            sqlite3_close(databaseRunSQL);
            NSString *errorString=[NSString stringWithFormat:@"APZStorage--update failed with status code%d",sqlStatus];
            [Logger logger_Log:@"E" :errorString];
            return NO;
        }
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:DATABASE_OPEN_ERROR,[sqlDetails objectForKey:QUERY_ID],@"",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        sqlite3_close(databaseRunSQL);
        [Logger logger_Log:@"E" :@"APZStorage--Update Unable to open DB file"];
        return NO;
    }
}

-(BOOL)deleteQuery:(NSDictionary *)sqlDetails{
    NSString *dbPathInDocumentsDirectory=[self retrunDatabsePath:sqlDetails];
    sqlite3 *databaseRunSQL = NULL;
    int sqlStatus = sqlite3_open([dbPathInDocumentsDirectory UTF8String],&databaseRunSQL);
    if(sqlStatus==SQLITE_OK)
    {
        const char *sql=(char *)[[sqlDetails objectForKey:STORAGE_QUERY_EXECUTEQUERY] UTF8String];
        sqlite3_stmt *deleteStatement;
        sqlStatus = sqlite3_prepare_v2(databaseRunSQL, sql, -1, &deleteStatement, NULL);
        if(sqlStatus == SQLITE_OK)
        {
            int sqlResp=sqlite3_step(deleteStatement);
            if(sqlResp == SQLITE_DONE){
                NSArray * resultkeys=[NSArray arrayWithObjects:QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:[sqlDetails objectForKey:QUERY_ID],@"success",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :true :false :resultkeys :resultMsg]];
                sqlite3_finalize(deleteStatement);
                sqlite3_close(databaseRunSQL);
                [Logger logger_Log:@"I" :@"APZStorage- Delete Sussess"];
                return YES;
            }
            else{
                NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
                NSArray *resultMsg=[NSArray arrayWithObjects:DELETE_QUERY_FAILED,[sqlDetails objectForKey:QUERY_ID],@"",nil];
                [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
                sqlite3_finalize(deleteStatement);
                sqlite3_close(databaseRunSQL);
                [Logger logger_Log:@"E" :@"APZStorage--Delete query failed"];
                return NO;
            }
        }
        else{
            NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
            NSArray *resultMsg=[NSArray arrayWithObjects:SYSTEM_QUERY_ERROR,[sqlDetails objectForKey:QUERY_ID],@"",nil];
            [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
            NSString *errorString=[NSString stringWithFormat:@"APZStorage--delete failed with error code%d",sqlStatus];
            [Logger logger_Log:@"E" :errorString];
            return NO;
        }
    }
    else{
        NSArray * resultkeys=[NSArray arrayWithObjects:ERROR_CODE,QUERY_ID,STORAGE_QUERY_JSONKEY_RESULT,nil];
        NSArray *resultMsg=[NSArray arrayWithObjects:DATABASE_OPEN_ERROR,[sqlDetails objectForKey:QUERY_ID],@"",nil];
        [MiscellaneousMethods jsLayerCall:_webView :JSCALLBACKMEHTOD parameter:[APZJsonUtil createResponseJSONString:self.pluginId :false :false :resultkeys :resultMsg]];
        sqlite3_close(databaseRunSQL);
        [Logger logger_Log:@"E" :@"APZStorage--delete Unable to open Database file"];
        return NO;
    }
}

-(NSString *)retrunDatabsePath:(NSDictionary *)sqlDetails{
    NSString *dbPathInDocumentsDirectory =[[NSString alloc]initWithString:[[self dBDocumentsDirectory] stringByAppendingFormat:@"/Assets/apps/%@/sqlite/%@.sqlite3",self.viewController.appString,[sqlDetails objectForKey:STORAGE_DB_NAME]]];
    return dbPathInDocumentsDirectory;
}

-(NSString *)dBDocumentsDirectory{
    NSArray *paths = [[NSArray alloc] initWithArray:NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)];
    return [[NSString alloc] initWithString:[paths objectAtIndex:0]];
}


#pragma mark -Plugin Clean
-(void)cleanPlugin{
    self.webView = nil;
    self.viewController=nil;
    [self.delegate donePlugin:self];
    [Logger logger_Log:@"D" :@"APZStorage--Done"];
}

@end

