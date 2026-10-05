/* App Details */
DELETE FROM TB_ASMI_APP_OS_VERSION WHERE (APP_ID='Admin'AND OS='WEB' );
INSERT INTO TB_ASMI_APP_OS_VERSION (APP_ID,OS,APP_VERSION,CREATE_USER_ID,CREATE_TS,VERSION_NO)
 VALUES('Admin','WEB','1.0.0.0','Admin',sysdate,1);
DELETE FROM TB_ASMI_APP_ID_VERSION WHERE (APP_ID='Admin'AND OS='WEB' );
INSERT INTO TB_ASMI_APP_ID_VERSION (APP_ID,OS,APP_ID_VERSION,CREATE_USER_ID,CREATE_TS,VERSION_NO)
 VALUES('Admin','WEB','1.0.0.0','Admin',sysdate,1);
/* LOV Details  */
DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchAllApps');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchAllApps','SELECT APP_ID FROM TB_ASMI_APP_MASTER','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','','','',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchAllowApp_ID');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchAllowApp_ID','select APP_ID from TB_ASMI_SECURITY_PARAMETERS where APP_ID IN (select ALLOWED_APP_ID from TB_ASMI_USER_APP_ACCESS
where USER_ID=? AND APP_ACCESS = ''A'' AND APP_ID=?)','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0|B1','STRING|STRING','',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchAuthRole_ID');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchAuthRole_ID','select ROLE_ID from TB_ASMI_ROLE_MASTER where APP_ID=? and AUTH_STATUS=''A''','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0','STRING','ROLE_ID',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchControl_ID');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchControl_ID','Select CONTROL_ID,CONTROL_DESC from TB_ASMI_CONTROLS_MASTER where APP_ID=?
','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0','STRING','CONTROL_ID|CONTROL_DESC',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchConv_ID');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchConv_ID','Select CNVUI_ID from TB_ASMI_CNVUI_MASTER where APP_ID=?','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0','STRING','CNVUI_ID',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchDevice_ID');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchDevice_ID','Select DEVICE_ID from TB_ASNF_DEVICES_MASTER where APP_ID=? and STATUS=''Y''
','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0','STRING','DEVICE_ID',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchDlgScreens');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchDlgScreens','select SCREEN_ID,SCREEN_DESC from TB_ASMI_CNVUI_SCR where APP_ID=?','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0','STRING','',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchGroup_ID');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchGroup_ID','select GROUP_ID,GROUP_DESC from TB_ASNF_GROUP_MASTER
','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','','','GROUP_ID|GROUP_DESC',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchInterface_ID');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchInterface_ID','Select INTERFACE_ID,DESCRIPTION from TB_ASMI_INTF_MASTER where APP_ID=? and AUTHRZ_REQ = ''Y''
','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0','STRING','INTERFACE_ID|DESCRIPTION',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchNextDlgId');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchNextDlgId','Select DLG_ID from TB_ASMI_DLG_MASTER where APP_ID=? AND CNVUI_ID=?','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0|B1','STRING|STRING','DLG_ID',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchOperations');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchOperations','Select INTERFACE_ID,DESCRIPTION from TB_ASMI_INTF_MASTER where APP_ID=?
','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0','STRING','INTERFACE_ID|DESCRIPTION',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchRole_ID');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchRole_ID','select ROLE_ID from TB_ASMI_ROLE_MASTER where APP_ID=?','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0','STRING','ROLE_ID',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchScreen_ID');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchScreen_ID','select SCREEN_ID,SCREEN_DESC from TB_ASMI_SCR_MASTER where APP_ID=?','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0','STRING','SCREEN_ID|SCREEN_DESC',null,null,sysdate,sysdate,'Admin',1);

DELETE FROM TB_ASMI_LOV_QUERIES WHERE (APP_ID = 'Admin' AND QUERY_ID='Admin__FetchUser_ID');
 INSERT INTO TB_ASMI_LOV_QUERIES(APP_ID,QUERY_ID,QUERY_STRING,DB_JNDI_NAME,SESSION_REQD,QUERY_TYPE,BINDVAR_COLS,BINDVAR_DATA_TYPES,FILTER_COLS,ORDERBY_COL,ORDERBY_TYPE,CREATE_TS,UPDATE_TS,CREATE_BY,VERSION_NO) VALUES('Admin','Admin__FetchUser_ID','Select USER_ID,USER_NAME from TB_ASMI_USER where APP_ID=?','java:comp/env/jdbc/AppzillonServerDS','Y','SQL','B0','STRING','USER_ID|USER_NAME',null,null,sysdate,sysdate,'Admin',1);

/* Admin Details */
DELETE FROM TB_ASMI_SECURITY_PARAMETERS WHERE APP_ID='Admin' AND CREATE_USER_ID='System';

/*Internal Interface Generation Begin*/
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUploadFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUploadFile','Admin','INTERNAL','INTERNAL','appzillonUploadFile','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUploadFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUploadFile','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUploadFileWS' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUploadFileWS','Admin','INTERNAL','INTERNAL','appzillonUploadFileWS','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUploadFileWS' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUploadFileWS','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUploadFileAuth' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUploadFileAuth','Admin','INTERNAL','INTERNAL','appzillonUploadFileAuth','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUploadFileAuth' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUploadFileAuth','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFlushCacheReq' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFlushCacheReq','Admin','INTERNAL','INTERNAL','appzillonFlushCacheReq','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonFlushCacheReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonFlushCacheReq','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAppUsageReport' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonAppUsageReport','Admin','INTERNAL','INTERNAL','appzillonAppUsageReport','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonAppUsageReport' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonAppUsageReport','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateDevice' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateDevice','Admin','INTERNAL','INTERNAL','appzillonCreateDevice','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateDevice','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateGroup' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateGroup','Admin','INTERNAL','INTERNAL','appzillonCreateGroup','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateGroup' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateGroup','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateRoleMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateRoleMaster','Admin','INTERNAL','INTERNAL','appzillonCreateRoleMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateRoleMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateScreen' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateScreen','Admin','INTERNAL','INTERNAL','appzillonCreateScreen','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateScreen' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateScreen','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateUser' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateUser','Admin','INTERNAL','INTERNAL','appzillonCreateUser','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateUser','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUserAuthorization' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUserAuthorization','Admin','INTERNAL','INTERNAL','appzillonUserAuthorization','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUserAuthorization' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUserAuthorization','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteDevice' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteDevice','Admin','INTERNAL','INTERNAL','appzillonDeleteDevice','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteDevice','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteFile','Admin','INTERNAL','INTERNAL','appzillonDeleteFile','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteFile','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteGroup' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteGroup','Admin','INTERNAL','INTERNAL','appzillonDeleteGroup','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteGroup' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteGroup','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteScreen' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteScreen','Admin','INTERNAL','INTERNAL','appzillonDeleteScreen','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteScreen' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteScreen','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFilePushService' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFilePushService','Admin','INTERNAL','INTERNAL','appzillonFilePushService','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonFilePushService' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonFilePushService','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFilePushServiceAuth' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFilePushServiceAuth','Admin','INTERNAL','INTERNAL','appzillonFilePushServiceAuth','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonFilePushServiceAuth' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonFilePushServiceAuth','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFilePushServiceWS' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFilePushServiceWS','Admin','INTERNAL','INTERNAL','appzillonFilePushServiceWS','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonFilePushServiceWS' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonFilePushServiceWS','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetGroupDetail' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetGroupDetail','Admin','INTERNAL','INTERNAL','appzillonGetGroupDetail','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetGroupDetail' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetGroupDetail','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetReqResp' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetReqResp','Admin','INTERNAL','INTERNAL','appzillonGetReqResp','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetReqResp' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetReqResp','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonLoginReport' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonLoginReport','Admin','INTERNAL','INTERNAL','appzillonLoginReport','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonLoginReport' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonLoginReport','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotificationAppDetail' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonNotificationAppDetail','Admin','INTERNAL','INTERNAL','appzillonNotificationAppDetail','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonNotificationAppDetail' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonNotificationAppDetail','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonPushNotification' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonPushNotification','Admin','INTERNAL','INTERNAL','appzillonPushNotification','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonPushNotification' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonPushNotification','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchDevice' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchDevice','Admin','INTERNAL','INTERNAL','appzillonSearchDevice','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchDevice','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchFile','Admin','INTERNAL','INTERNAL','appzillonSearchFile','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchFile','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchGroup' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchGroup','Admin','INTERNAL','INTERNAL','appzillonSearchGroup','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchGroup' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchGroup','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchScreen' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchScreen','Admin','INTERNAL','INTERNAL','appzillonSearchScreen','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchScreen' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchScreen','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchTxnLogging' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchTxnLogging','Admin','INTERNAL','INTERNAL','appzillonSearchTxnLogging','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchTxnLogging' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchTxnLogging','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateDevice' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateDevice','Admin','INTERNAL','INTERNAL','appzillonUpdateDevice','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateDevice','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateGroup' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateGroup','Admin','INTERNAL','INTERNAL','appzillonUpdateGroup','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateGroup' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateGroup','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateScreen' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateScreen','Admin','INTERNAL','INTERNAL','appzillonUpdateScreen','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateScreen' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateScreen','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDecrypt' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDecrypt','Admin','INTERNAL','INTERNAL','appzillonDecrypt','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDecrypt' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDecrypt','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonLoggingRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonLoggingRequest','Admin','INTERNAL','INTERNAL','appzillonLoggingRequest','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonLoggingRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonLoggingRequest','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonWorkflowDashboardQuery' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonWorkflowDashboardQuery','Admin','INTERNAL','INTERNAL','appzillonWorkflowDashboardQuery','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonWorkflowDashboardQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonWorkflowDashboardQuery','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonWorkflowPersist' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonWorkflowPersist','Admin','INTERNAL','INTERNAL','appzillonWorkflowPersist','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonWorkflowPersist' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonWorkflowPersist','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonWorkflowQuery' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonWorkflowQuery','Admin','INTERNAL','INTERNAL','appzillonWorkflowQuery','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonWorkflowQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonWorkflowQuery','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonWorkflowQueryDb' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonWorkflowQueryDb','Admin','INTERNAL','INTERNAL','appzillonWorkflowQueryDb','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonWorkflowQueryDb' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonWorkflowQueryDb','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonWorkflowQueryRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonWorkflowQueryRequest','Admin','INTERNAL','INTERNAL','appzillonWorkflowQueryRequest','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonWorkflowQueryRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonWorkflowQueryRequest','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreatePasswordRules' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreatePasswordRules','Admin','INTERNAL','INTERNAL','appzillonCreatePasswordRules','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreatePasswordRules' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreatePasswordRules','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeletePasswordRules' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeletePasswordRules','Admin','INTERNAL','INTERNAL','appzillonDeletePasswordRules','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeletePasswordRules' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeletePasswordRules','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetPasswordRules' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetPasswordRules','Admin','INTERNAL','INTERNAL','appzillonGetPasswordRules','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetPasswordRules' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetPasswordRules','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetPasswordByRules' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetPasswordByRules','Admin','INTERNAL','INTERNAL','appzillonGetPasswordByRules','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetPasswordByRules' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetPasswordByRules','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdatePasswordRules' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdatePasswordRules','Admin','INTERNAL','INTERNAL','appzillonUpdatePasswordRules','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdatePasswordRules' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdatePasswordRules','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateUser' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateUser','Admin','INTERNAL','INTERNAL','appzillonUpdateUser','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateUser','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteUser' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteUser','Admin','INTERNAL','INTERNAL','appzillonDeleteUser','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteUser','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchUser' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchUser','Admin','INTERNAL','INTERNAL','appzillonSearchUser','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchUser','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetRolesByAppIDUserID' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetRolesByAppIDUserID','Admin','INTERNAL','INTERNAL','appzillonGetRolesByAppIDUserID','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetRolesByAppIDUserID' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetRolesByAppIDUserID','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetRolesByAppID' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetRolesByAppID','Admin','INTERNAL','INTERNAL','appzillonGetRolesByAppID','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetRolesByAppID' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetRolesByAppID','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonPasswordReset' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonPasswordReset','Admin','INTERNAL','INTERNAL','appzillonPasswordReset','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonPasswordReset' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonPasswordReset','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonForgotPassword' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonForgotPassword','Admin','INTERNAL','INTERNAL','appzillonForgotPassword','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonForgotPassword' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonForgotPassword','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUnlockUser' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUnlockUser','Admin','INTERNAL','INTERNAL','appzillonUnlockUser','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUnlockUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUnlockUser','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetUser' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetUser','Admin','INTERNAL','INTERNAL','appzillonGetUser','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetUser','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateRoleMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateRoleMaster','Admin','INTERNAL','INTERNAL','appzillonUpdateRoleMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateRoleMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteRoleMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteRoleMaster','Admin','INTERNAL','INTERNAL','appzillonDeleteRoleMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteRoleMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetRoleMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetRoleMaster','Admin','INTERNAL','INTERNAL','appzillonGetRoleMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetRoleMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetScreensIntfByAppID' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetScreensIntfByAppID','Admin','INTERNAL','INTERNAL','appzillonGetScreensIntfByAppID','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetScreensIntfByAppID' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetScreensIntfByAppID','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetIntfScrByAppIDRoleID' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetIntfScrByAppIDRoleID','Admin','INTERNAL','INTERNAL','appzillonGetIntfScrByAppIDRoleID','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetIntfScrByAppIDRoleID' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetIntfScrByAppIDRoleID','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchIntfMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchIntfMaster','Admin','INTERNAL','INTERNAL','appzillonSearchIntfMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchIntfMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateIntfMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateIntfMaster','Admin','INTERNAL','INTERNAL','appzillonCreateIntfMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateIntfMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteIntfMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteIntfMaster','Admin','INTERNAL','INTERNAL','appzillonDeleteIntfMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteIntfMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateIntfMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateIntfMaster','Admin','INTERNAL','INTERNAL','appzillonUpdateIntfMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateIntfMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonPasswordValidate' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonPasswordValidate','Admin','INTERNAL','INTERNAL','appzillonPasswordValidate','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonPasswordValidate' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonPasswordValidate','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'JMSRespFetchReq' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('JMSRespFetchReq','Admin','INTERNAL','INTERNAL','JMSRespFetchReq','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='JMSRespFetchReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','JMSRespFetchReq','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonMessageStatistics' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonMessageStatistics','Admin','INTERNAL','INTERNAL','appzillonMessageStatistics','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonMessageStatistics' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonMessageStatistics','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDashBoard' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDashBoard','Admin','INTERNAL','INTERNAL','appzillonDashBoard','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDashBoard' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDashBoard','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonErrorLogging' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonErrorLogging','Admin','INTERNAL','INTERNAL','appzillonErrorLogging','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonErrorLogging' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonErrorLogging','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'deviceStatus_Req' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('deviceStatus_Req','Admin','INTERNAL','INTERNAL','deviceStatus_Req','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='deviceStatus_Req' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','deviceStatus_Req','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetChildAppDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetChildAppDetails','Admin','INTERNAL','INTERNAL','appzillonGetChildAppDetails','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetChildAppDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetChildAppDetails','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateAppMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateAppMaster','Admin','INTERNAL','INTERNAL','appzillonCreateAppMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateAppMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateAppMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateAppMaster','Admin','INTERNAL','INTERNAL','appzillonUpdateAppMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateAppMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchAppMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchAppMaster','Admin','INTERNAL','INTERNAL','appzillonSearchAppMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchAppMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteAppMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteAppMaster','Admin','INTERNAL','INTERNAL','appzillonDeleteAppMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteAppMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateAppFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateAppFile','Admin','INTERNAL','INTERNAL','appzillonCreateAppFile','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateAppFile','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateAppFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateAppFile','Admin','INTERNAL','INTERNAL','appzillonUpdateAppFile','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateAppFile','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchAppFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchAppFile','Admin','INTERNAL','INTERNAL','appzillonSearchAppFile','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchAppFile','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteAppFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteAppFile','Admin','INTERNAL','INTERNAL','appzillonDeleteAppFile','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteAppFile','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchDeviceMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchDeviceMaster','Admin','INTERNAL','INTERNAL','appzillonSearchDeviceMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchDeviceMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchDeviceMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteDeviceMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteDeviceMaster','Admin','INTERNAL','INTERNAL','appzillonDeleteDeviceMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteDeviceMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteDeviceMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateDeviceMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateDeviceMaster','Admin','INTERNAL','INTERNAL','appzillonUpdateDeviceMaster','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateDeviceMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateDeviceMaster','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'searchTaskRepair' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('searchTaskRepair','Admin','INTERNAL','INTERNAL','searchTaskRepair','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='searchTaskRepair' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','searchTaskRepair','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'updateTaskRepair' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('updateTaskRepair','Admin','INTERNAL','INTERNAL','updateTaskRepair','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='updateTaskRepair' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','updateTaskRepair','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotifyMobileNumber' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonNotifyMobileNumber','Admin','INTERNAL','INTERNAL','appzillonNotifyMobileNumber','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonNotifyMobileNumber' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonNotifyMobileNumber','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotifyDevice' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonNotifyDevice','Admin','INTERNAL','INTERNAL','appzillonNotifyDevice','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonNotifyDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonNotifyDevice','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonInsertBeacon' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonInsertBeacon','Admin','INTERNAL','INTERNAL','appzillonInsertBeacon','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonInsertBeacon' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonInsertBeacon','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFetchBeaconDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFetchBeaconDetails','Admin','INTERNAL','INTERNAL','appzillonFetchBeaconDetails','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonFetchBeaconDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonFetchBeaconDetails','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateBeaconDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateBeaconDetails','Admin','INTERNAL','INTERNAL','appzillonUpdateBeaconDetails','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateBeaconDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateBeaconDetails','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFetchARDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFetchARDetails','Admin','INTERNAL','INTERNAL','appzillonFetchARDetails','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonFetchARDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonFetchARDetails','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonInsertDragDrop' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonInsertDragDrop','Admin','INTERNAL','INTERNAL','appzillonInsertDragDrop','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonInsertDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonInsertDragDrop','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateDragDrop' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateDragDrop','Admin','INTERNAL','INTERNAL','appzillonUpdateDragDrop','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateDragDrop','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteDragDrop' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteDragDrop','Admin','INTERNAL','INTERNAL','appzillonDeleteDragDrop','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteDragDrop','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchDragDrop' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchDragDrop','Admin','INTERNAL','INTERNAL','appzillonSearchDragDrop','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchDragDrop','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCustomer' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCustomer','Admin','INTERNAL','INTERNAL','appzillonCustomer','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCustomer' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCustomer','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCustomerLocation' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCustomerLocation','Admin','INTERNAL','INTERNAL','appzillonCustomerLocation','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCustomerLocation' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCustomerLocation','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCustomerDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCustomerDetails','Admin','INTERNAL','INTERNAL','appzillonCustomerDetails','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCustomerDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCustomerDetails','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeviceGrpQuery' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeviceGrpQuery','Admin','INTERNAL','INTERNAL','appzillonDeviceGrpQuery','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeviceGrpQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeviceGrpQuery','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAppScreensQuery' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonAppScreensQuery','Admin','INTERNAL','INTERNAL','appzillonAppScreensQuery','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonAppScreensQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonAppScreensQuery','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSaveCustomizationData' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSaveCustomizationData','Admin','INTERNAL','INTERNAL','appzillonSaveCustomizationData','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSaveCustomizationData' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSaveCustomizationData','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetCustomizerDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetCustomizerDetails','Admin','INTERNAL','INTERNAL','appzillonGetCustomizerDetails','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetCustomizerDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetCustomizerDetails','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetQueryDesignerData' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetQueryDesignerData','Admin','INTERNAL','INTERNAL','appzillonGetQueryDesignerData','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetQueryDesignerData' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetQueryDesignerData','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSaveAppAccess' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSaveAppAccess','Admin','INTERNAL','INTERNAL','create and update user app access','Y','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSaveAppAccess' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSaveAppAccess','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAuthenticationRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonAuthenticationRequest','Admin','INTERNAL','INTERNAL','appzillonAuthenticationRequest','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonAuthenticationRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonAuthenticationRequest','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonChangePassword' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonChangePassword','Admin','INTERNAL','INTERNAL','appzillonChangePassword','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonChangePassword' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonChangePassword','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotificationRegistration' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonNotificationRegistration','Admin','INTERNAL','INTERNAL','appzillonNotificationRegistration','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonNotificationRegistration' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonNotificationRegistration','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonReLoginRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonReLoginRequest','Admin','INTERNAL','INTERNAL','appzillonReLoginRequest','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonReLoginRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonReLoginRequest','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAuditLog' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonAuditLog','Admin','INTERNAL','INTERNAL','appzillonAuditLog','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonAuditLog' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonAuditLog','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonOTAFileDownloadReq' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonOTAFileDownloadReq','Admin','INTERNAL','INTERNAL','appzillonOTAFileDownloadReq','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonOTAFileDownloadReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonOTAFileDownloadReq','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetAppFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonGetAppFile','Admin','INTERNAL','INTERNAL','appzillonGetAppFile','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetAppFile','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonMailRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonMailRequest','Admin','INTERNAL','INTERNAL','appzillonMailRequest','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonMailRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonMailRequest','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUserRegistration' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonUserRegistration','Admin','INTERNAL','INTERNAL','appzillonUserRegistration','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUserRegistration' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUserRegistration','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonInterfaceAuthRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonInterfaceAuthRequest','Admin','INTERNAL','INTERNAL','appzillonInterfaceAuthRequest','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonInterfaceAuthRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonInterfaceAuthRequest','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonScreenAuthRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonScreenAuthRequest','Admin','INTERNAL','INTERNAL','appzillonScreenAuthRequest','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonScreenAuthRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonScreenAuthRequest','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFetchPrivilegeService' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonFetchPrivilegeService','Admin','INTERNAL','INTERNAL','appzillonFetchPrivilegeService','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonFetchPrivilegeService' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonFetchPrivilegeService','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonLogoutRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonLogoutRequest','Admin','INTERNAL','INTERNAL','appzillonLogoutRequest','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonLogoutRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonLogoutRequest','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonLOVReq' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonLOVReq','Admin','INTERNAL','INTERNAL','appzillonLOVReq','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonLOVReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonLOVReq','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonValNProcessIface' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonValNProcessIface','Admin','INTERNAL','INTERNAL','appzillonValNProcessIface','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonValNProcessIface' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonValNProcessIface','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeviceRegistration' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonDeviceRegistration','Admin','INTERNAL','INTERNAL','appzillonDeviceRegistration','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeviceRegistration' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeviceRegistration','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUserDeviceRegistration' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonUserDeviceRegistration','Admin','INTERNAL','INTERNAL','appzillonUserDeviceRegistration','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUserDeviceRegistration' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUserDeviceRegistration','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonValidateOTP' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonValidateOTP','Admin','INTERNAL','INTERNAL','appzillonValidateOTP','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonValidateOTP' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonValidateOTP','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonReGenerateOtp' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonReGenerateOtp','Admin','INTERNAL','INTERNAL','appzillonReGenerateOtp','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonReGenerateOtp' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonReGenerateOtp','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSendSMS' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonSendSMS','Admin','INTERNAL','INTERNAL','appzillonSendSMS','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSendSMS' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSendSMS','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonScheduler' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonScheduler','Admin','INTERNAL','INTERNAL','appzillonScheduler','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonScheduler' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonScheduler','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSmsLogTxn' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonSmsLogTxn','Admin','INTERNAL','INTERNAL','appzillonSmsLogTxn','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSmsLogTxn' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSmsLogTxn','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUssdLogTxn' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonUssdLogTxn','Admin','INTERNAL','INTERNAL','appzillonUssdLogTxn','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUssdLogTxn' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUssdLogTxn','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonTxtMslgLog' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonTxtMslgLog','Admin','INTERNAL','INTERNAL','appzillonTxtMslgLog','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonTxtMslgLog' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonTxtMslgLog','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSmsUser' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonSmsUser','Admin','INTERNAL','INTERNAL','appzillonSmsUser','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSmsUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSmsUser','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGenerateCaptcha' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonGenerateCaptcha','Admin','INTERNAL','INTERNAL','appzillonGenerateCaptcha','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGenerateCaptcha' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGenerateCaptcha','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonTrackLocation' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonTrackLocation','Admin','INTERNAL','INTERNAL','appzillonTrackLocation','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonTrackLocation' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonTrackLocation','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCheckServer' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonCheckServer','Admin','INTERNAL','INTERNAL','appzillonCheckServer','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCheckServer' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCheckServer','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'getCnvUIWelcomeMsg' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('getCnvUIWelcomeMsg','Admin','INTERNAL','INTERNAL','getCnvUIWelcomeMsg','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='getCnvUIWelcomeMsg' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','getCnvUIWelcomeMsg','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'getFirstCnvUIDlg' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('getFirstCnvUIDlg','Admin','INTERNAL','INTERNAL','getFirstCnvUIDlg','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='getFirstCnvUIDlg' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','getFirstCnvUIDlg','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'getCnvUIDlg' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('getCnvUIDlg','Admin','INTERNAL','INTERNAL','getCnvUIDlg','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='getCnvUIDlg' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','getCnvUIDlg','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'processNLPData' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('processNLPData','Admin','INTERNAL','INTERNAL','processNLPData','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='processNLPData' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','processNLPData','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetIntfDef' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonGetIntfDef','Admin','INTERNAL','INTERNAL','appzillonGetIntfDef','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetIntfDef' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetIntfDef','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetAppMasterDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonGetAppMasterDetails','Admin','INTERNAL','INTERNAL','appzillonGetAppMasterDetails','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetAppMasterDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetAppMasterDetails','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetAppSecTokens' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonGetAppSecTokens','Admin','INTERNAL','INTERNAL','appzillonGetAppSecTokens','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetAppSecTokens' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetAppSecTokens','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'workflowAcquire' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('workflowAcquire','Admin','INTERNAL','INTERNAL','workflowAcquire','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='workflowAcquire' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','workflowAcquire','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'workflowAssign' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('workflowAssign','Admin','INTERNAL','INTERNAL','workflowAssign','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='workflowAssign' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','workflowAssign','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'workflowUnassign' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('workflowUnassign','Admin','INTERNAL','INTERNAL','workflowUnassign','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='workflowUnassign' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','workflowUnassign','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'workflowQuery' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('workflowQuery','Admin','INTERNAL','INTERNAL','workflowQuery','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='workflowQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','workflowQuery','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'workflowReassign' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('workflowReassign','Admin','INTERNAL','INTERNAL','workflowReassign','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='workflowReassign' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','workflowReassign','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'workflowSave' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('workflowSave','Admin','INTERNAL','INTERNAL','workflowSave','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='workflowSave' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','workflowSave','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'workflowStart' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('workflowStart','Admin','INTERNAL','INTERNAL','workflowStart','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='workflowStart' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','workflowStart','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'workflowTerminate' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('workflowTerminate','Admin','INTERNAL','INTERNAL','workflowTerminate','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='workflowTerminate' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','workflowTerminate','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'workflowNext' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('workflowNext','Admin','INTERNAL','INTERNAL','workflowNext','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='workflowNext' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','workflowNext','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'workflowPrev' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('workflowPrev','Admin','INTERNAL','INTERNAL','workflowPrev','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='workflowPrev' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','workflowPrev','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetUserAppAccessToken' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonGetUserAppAccessToken','Admin','INTERNAL','INTERNAL','appzillonGetUserAppAccessToken','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetUserAppAccessToken' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetUserAppAccessToken','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonReloadLogger' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonReloadLogger','Admin','INTERNAL','INTERNAL','appzillonReloadLogger','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonReloadLogger' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonReloadLogger','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'apzParseMetaJSON' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('apzParseMetaJSON','Admin','INTERNAL','INTERNAL','apzParseMetaJSON','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='apzParseMetaJSON' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','apzParseMetaJSON','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'apzParseProductJSON' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('apzParseProductJSON','Admin','INTERNAL','INTERNAL','apzParseProductJSON','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='apzParseProductJSON' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','apzParseProductJSON','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'apzParseWidgetJSON' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('apzParseWidgetJSON','Admin','INTERNAL','INTERNAL','apzParseWidgetJSON','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='apzParseWidgetJSON' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','apzParseWidgetJSON','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'apzPersistHTMLInfo' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('apzPersistHTMLInfo','Admin','INTERNAL','INTERNAL','apzPersistHTMLInfo','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='apzPersistHTMLInfo' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','apzPersistHTMLInfo','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateTenant' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonCreateTenant','Admin','INTERNAL','INTERNAL','appzillonCreateTenant','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateTenant' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateTenant','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'reloadServerProperties' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('reloadServerProperties','Admin','INTERNAL','INTERNAL','reloadServerProperties','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='reloadServerProperties' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','reloadServerProperties','Admin',sysdate,1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'reloadEnvProperties' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('reloadEnvProperties','Admin','INTERNAL','INTERNAL','reloadEnvProperties','N','Admin',sysdate, 'admin' , sysdate, 'admin' , sysdate, 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='reloadEnvProperties' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','reloadEnvProperties','Admin',sysdate,1);

/*Internal Interface Generation End*/
DELETE FROM TB_ASMI_USER_APP_ACCESS WHERE (APP_ID='Admin' AND USER_ID='admin');
	insert into TB_ASMI_USER_APP_ACCESS (USER_ID, APP_ID, ALLOWED_APP_ID,APP_ACCESS, CREATE_USER_ID, CREATE_TS) VALUES ('admin', 'Admin','Admin', 'A', 'admin', sysdate);

DELETE FROM TB_ASMI_APP_MASTER WHERE (APP_ID='Admin');
INSERT INTO TB_ASMI_APP_MASTER (APP_ID,PARENT_APPID,CONTAINER_APP,OTA_REQ,REMOTE_DEBUG,EXPIRY_DATE,APP_DESCRIPTION,DEFAULT_LANGUAGE,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_TS,MAKER_ID,CHECKER_ID,CHECKER_TS,AUTH_STATUS,APP_NAME,MICRO_APP_TYPE,APP_ICON,APP_VERSION,IDE_VERSION)
 VALUES('Admin','','N','N','N',TO_TIMESTAMP('18/01/2038','DD/MM/YYYY'),'Appzillon Admin','en','Admin',sysdate,'1',sysdate,'admin','admin',sysdate,'A','AppzillonAdmin','INTERNAL','appicon.png','1.0.0.0','3.11.0.23072021');
DELETE FROM TB_ASMI_USER WHERE (APP_ID='Admin' AND USER_ID='admin');
INSERT INTO TB_ASMI_USER (USER_ID, APP_ID, PIN, USER_NAME,LOGIN_STATUS, FAIL_COUNT, USER_ACTIVE, USER_LOCKED,LANGUAGE,EXTERNALIDENTIFIER,USER_ADDR1, USER_ADDR2, USER_ADDR3, USER_ADDR4, USER_EML1, USER_EML2, USER_PHNO1, USER_PHNO2, USER_LVL, CREATE_USER_ID, CREATE_TS, VERSION_NO,USER_LOCK_TS,PIN_CHANGE_TS,AUTH_STATUS,MAKER_ID) VALUES('admin','Admin','d9fc787aab72f21dd502ab3c1907e9cd826cbe2f7672a8b3fa29919bbac60bfc','admin','Y',1,'Y','N','en',null,'','','','','','','','',1,'Admin',sysdate,1,sysdate,sysdate,'A','admin');

DELETE FROM TB_ASMI_USER_DEVICES WHERE (APP_ID='Admin' AND USER_ID='admin' AND DEVICE_ID='SIMULATOR');
INSERT INTO TB_ASMI_USER_DEVICES(APP_ID,DEVICE_ID, USER_ID,DEVICE_STATUS,CREATE_USER_ID,CREATE_TS,VERSION_NO,AUTH_STATUS) VALUES('Admin','SIMULATOR','admin','ACTIVE','Admin',sysdate,'1','A');

DELETE FROM TB_ASMI_USER_ROLE WHERE (APP_ID='Admin' AND USER_ID='admin' AND ROLE_ID='admin');
INSERT INTO TB_ASMI_USER_ROLE(USER_ID,ROLE_ID, APP_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('admin','admin','Admin','Admin',sysdate,'1');

DELETE FROM TB_ASMI_USER_DEVICES WHERE (APP_ID='Admin' AND USER_ID='admin' AND DEVICE_ID='WEB');
INSERT INTO TB_ASMI_USER_DEVICES(APP_ID,DEVICE_ID, USER_ID,DEVICE_STATUS,CREATE_USER_ID,CREATE_TS,VERSION_NO,AUTH_STATUS) VALUES('Admin','WEB','admin','ACTIVE','Admin',sysdate,'1','A');

DELETE FROM TB_ASMI_USER_APP_ACCESS WHERE (APP_ID='Admin' AND USER_ID='adminauth');
	insert into TB_ASMI_USER_APP_ACCESS (USER_ID, APP_ID, ALLOWED_APP_ID,APP_ACCESS, CREATE_USER_ID, CREATE_TS) VALUES ('adminauth', 'Admin','Admin', 'A', 'admin', sysdate);

DELETE FROM TB_ASMI_APP_MASTER WHERE (APP_ID='Admin');
INSERT INTO TB_ASMI_APP_MASTER (APP_ID,PARENT_APPID,CONTAINER_APP,OTA_REQ,REMOTE_DEBUG,EXPIRY_DATE,APP_DESCRIPTION,DEFAULT_LANGUAGE,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_TS,MAKER_ID,CHECKER_ID,CHECKER_TS,AUTH_STATUS,APP_NAME,MICRO_APP_TYPE,APP_ICON,APP_VERSION,IDE_VERSION)
 VALUES('Admin','','N','N','N',TO_TIMESTAMP('18/01/2038','DD/MM/YYYY'),'Appzillon Admin','en','Admin',sysdate,'1',sysdate,'admin','admin',sysdate,'A','AppzillonAdmin','INTERNAL','appicon.png','1.0.0.0','3.11.0.23072021');
DELETE FROM TB_ASMI_USER WHERE (APP_ID='Admin' AND USER_ID='adminauth');
INSERT INTO TB_ASMI_USER (USER_ID, APP_ID, PIN, USER_NAME,LOGIN_STATUS, FAIL_COUNT, USER_ACTIVE, USER_LOCKED,LANGUAGE,EXTERNALIDENTIFIER,USER_ADDR1, USER_ADDR2, USER_ADDR3, USER_ADDR4, USER_EML1, USER_EML2, USER_PHNO1, USER_PHNO2, USER_LVL, CREATE_USER_ID, CREATE_TS, VERSION_NO,USER_LOCK_TS,PIN_CHANGE_TS,AUTH_STATUS,MAKER_ID) VALUES('adminauth','Admin','19bf2a35a00ac8d8ccf594a0db46383bfec91a476ecdc336ed46dbf045bcfd8b','admin','Y',1,'Y','N','en',null,'','','','','','','','',1,'Admin',sysdate,1,sysdate,sysdate,'A','adminauth');

DELETE FROM TB_ASMI_USER_DEVICES WHERE (APP_ID='Admin' AND USER_ID='adminauth' AND DEVICE_ID='SIMULATOR');
INSERT INTO TB_ASMI_USER_DEVICES(APP_ID,DEVICE_ID, USER_ID,DEVICE_STATUS,CREATE_USER_ID,CREATE_TS,VERSION_NO,AUTH_STATUS) VALUES('Admin','SIMULATOR','adminauth','ACTIVE','Admin',sysdate,'1','A');

DELETE FROM TB_ASMI_USER_ROLE WHERE (APP_ID='Admin' AND USER_ID='adminauth' AND ROLE_ID='admin');
INSERT INTO TB_ASMI_USER_ROLE(USER_ID,ROLE_ID, APP_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('adminauth','admin','Admin','Admin',sysdate,'1');

DELETE FROM TB_ASMI_USER_DEVICES WHERE (APP_ID='Admin' AND USER_ID='adminauth' AND DEVICE_ID='WEB');
INSERT INTO TB_ASMI_USER_DEVICES(APP_ID,DEVICE_ID, USER_ID,DEVICE_STATUS,CREATE_USER_ID,CREATE_TS,VERSION_NO,AUTH_STATUS) VALUES('Admin','WEB','adminauth','ACTIVE','Admin',sysdate,'1','A');

DELETE FROM TB_ASNF_APP_OS_NOTIF_CODE WHERE (APP_ID='Admin' AND OS_ID='WEB' AND NOTIF_CODE='Notification' AND ACTION_CODE='test1');
insert into TB_ASNF_APP_OS_NOTIF_CODE (APP_ID, OS_ID,NOTIF_CODE,ACTION_CODE,ACTION_DISPLAY,CREATE_TS) VALUES ('Admin','WEB', 'Notification','test1','test1',sysdate);

DELETE FROM TB_ASNF_APP_OS_NOTIF_CODE WHERE (APP_ID='Admin' AND OS_ID='WEB' AND NOTIF_CODE='Notification' AND ACTION_CODE='test2');
insert into TB_ASNF_APP_OS_NOTIF_CODE (APP_ID, OS_ID,NOTIF_CODE,ACTION_CODE,ACTION_DISPLAY,CREATE_TS) VALUES ('Admin','WEB', 'Notification','test2','test2',sysdate);

/* Screens */
DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__AdminOperations' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__AdminOperations','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','AdminOperations','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__AppAccess' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__AppAccess','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','AppAccess','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__ChangePassword' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__ChangePassword','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','ChangePassword','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__ChangePasswordBeforeLogin' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__ChangePasswordBeforeLogin','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','ChangePasswordBeforeLogin','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Customer' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Customer','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Customer','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Dashboard' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Dashboard','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Dashboard','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__DeviceGroup' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__DeviceGroup','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','DeviceGroup','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__DlgMasters' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__DlgMasters','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','DlgMasters','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__FileServices' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__FileServices','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','FileServices','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__InactiveUsers' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__InactiveUsers','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','InactiveUsers','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Interfaces' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Interfaces','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Interfaces','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Jobscheduler' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Jobscheduler','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Jobscheduler','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Landing' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Landing','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Landing','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Login' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Login','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Login','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Notifications' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Notifications','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Notifications','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__PushNotifications' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__PushNotifications','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','PushNotifications','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Role' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Role','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Role','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Screens' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Screens','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Screens','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__SecurityParameters' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__SecurityParameters','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','SecurityParameters','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Statistics' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Statistics','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Statistics','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__TaskRepair' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__TaskRepair','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','TaskRepair','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__TransactionDetails' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__TransactionDetails','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','TransactionDetails','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__Usage' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__Usage','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','Usage','','N');

DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'Admin__User' AND APP_ID='Admin');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('Admin__User','Admin','New Screen','Admin',sysdate,1,'admin',sysdate,'admin',sysdate,'A','MAIN','User','','N');

/* ScreenWidgets */
DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__AdminOperations' AND APP_ID='Admin' AND CALLFORM_ID='Admin__AdminOperations');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__AdminOperations','Admin__AdminOperations',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__AppAccess' AND APP_ID='Admin' AND CALLFORM_ID='Admin__AppAccess');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__AppAccess','Admin__AppAccess',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__ChangePassword' AND APP_ID='Admin' AND CALLFORM_ID='Admin__ChangePassword');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__ChangePassword','Admin__ChangePassword',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__ChangePasswordBeforeLogin' AND APP_ID='Admin' AND CALLFORM_ID='Admin__ChangePasswordBeforeLogin');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__ChangePasswordBeforeLogin','Admin__ChangePasswordBeforeLogin',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Customer' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Customer');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Customer','Admin__Customer',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Dashboard' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Dashboard');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Dashboard','Admin__Dashboard',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__DeviceGroup' AND APP_ID='Admin' AND CALLFORM_ID='Admin__DeviceGroup');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__DeviceGroup','Admin__DeviceGroup',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__DlgMasters' AND APP_ID='Admin' AND CALLFORM_ID='Admin__DlgMasters');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__DlgMasters','Admin__DlgMasters',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__FileServices' AND APP_ID='Admin' AND CALLFORM_ID='Admin__FileServices');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__FileServices','Admin__FileServices',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__InactiveUsers' AND APP_ID='Admin' AND CALLFORM_ID='Admin__InactiveUsers');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__InactiveUsers','Admin__InactiveUsers',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Interfaces' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Interfaces');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Interfaces','Admin__Interfaces',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Jobscheduler' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Jobscheduler');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Jobscheduler','Admin__Jobscheduler',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Landing' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Landing');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Landing','Admin__Landing',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Login' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Login');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Login','Admin__Login',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Notifications' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Notifications');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Notifications','Admin__Notifications',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__PushNotifications' AND APP_ID='Admin' AND CALLFORM_ID='Admin__PushNotifications');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__PushNotifications','Admin__PushNotifications',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Role' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Role');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Role','Admin__Role',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Screens' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Screens');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Screens','Admin__Screens',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__SecurityParameters' AND APP_ID='Admin' AND CALLFORM_ID='Admin__SecurityParameters');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__SecurityParameters','Admin__SecurityParameters',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Statistics' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Statistics');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Statistics','Admin__Statistics',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__TaskRepair' AND APP_ID='Admin' AND CALLFORM_ID='Admin__TaskRepair');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__TaskRepair','Admin__TaskRepair',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__TransactionDetails' AND APP_ID='Admin' AND CALLFORM_ID='Admin__TransactionDetails');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__TransactionDetails','Admin__TransactionDetails',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__Usage' AND APP_ID='Admin' AND CALLFORM_ID='Admin__Usage');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__Usage','Admin__Usage',sysdate);

DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'Admin__User' AND APP_ID='Admin' AND CALLFORM_ID='Admin__User');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('Admin','Admin__User','Admin__User',sysdate);

/* Interfaces */
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__AdminOperationsReport' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__appzillonAppUsageReport' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__appzillonCustomer' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__appzillonCustomerDetails' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__appzillonDashBoard' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__appzillonLoginReport' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__appzillonMessageStatistics' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__appzillonNotificationAppDetail' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__appzillonPushNotification' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__appzillonSaveAppAccess' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__appzillonSearchTxnLogging' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__DashboardDummy' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__deleteRole_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__deleteRole_Delete' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__DeviceGroupsDetail_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__DeviceGroupsQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__DeviceGroupsQuery_Delete' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__DeviceGroupsSummary' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__DlgMasterDetail_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__DlgMasterDetail_New' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__DlgMasterDetailModifyDel_Modify' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__DlgMasterDetailModifyDel_Delete' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__FetchAllowAppIdSQL' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__FetchAppMasterAppIDSQL' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__FetchNotificationCode_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__FetchNotificationCodeSQL' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__GeoCoding' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__getTransactionReqResp' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__InactiveUsersReport' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__InactiveUsersSql' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__InterfaceQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__InterfaceQuery_Delete' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__InterfacesDetailQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__InterfacesDetailQuery_New' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__InterfacesDetailQuery_Modify' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__InterfacesDetailQuery_Authorize' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__MessageStatistics_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__NotificationsReport' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__QuartzJobData' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__QuartzSchedulerDetailQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__QuartzSchedulerDetailQuery_New' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__QuartzSchedulerDetailQuery_Modify' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__QuartzSchedulerDetailQuery_Delete' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__QuartzSchedulerQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__RoleDetailDesc_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__RoleDetailDesc_New' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__RoleDetailDesc_Modify' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__RoleDetailQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__RoleDetailQuery_New' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__RoleDetailUpdate_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__RoleDetailUpdate_Modify' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__RoleDetailUpdate_Authorize' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__RolesQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__ScreenDetailQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__ScreenDetailQuery_New' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__ScreenDetailQuery_Modify' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__ScreenDetailQuery_Authorize' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__ScreensQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__ScreensQuery_Delete' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__SecurityParamsDetailQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__SecurityParamsDetailQuery_New' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__SecurityParamsDetailQuery_Modify' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__SecurityParamsDetailQuery_Authorize' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__SecurityParamsQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__SecurityParamsQuery_Delete' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__StatisticsReport' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__TaskRepairDetailQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__TaskRepairDetailQuery_Modify' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__TaskRepairDetailQuery_Delete' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__TaskRepairQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__TransactionReport' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__UsageReport' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__UserDetailsQuery_Query' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__UserModifyStatusSQL' AND APP_ID='Admin');
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'Admin__UserProfileDetailQuery_Query' AND APP_ID='Admin');

/* SMS Delete and Insert scripts */
DELETE FROM TB_ASMI_ROLE_MASTER WHERE (APP_ID='Admin'  AND ROLE_ID='admin');
INSERT INTO TB_ASMI_ROLE_MASTER(ROLE_ID, APP_ID,ROLE_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,INTERFACE_ALLOWED,SCREEN_ALLOWED,CONTROL_ALLOWED,AUTH_STATUS) VALUES('admin','Admin','AdminiStration','Admin',sysdate,1,'A','A','A','A');

/* Role-Screen Mapping */
DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__AdminOperations' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__AdminOperations','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__AppAccess' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__AppAccess','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__ChangePassword' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__ChangePassword','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__ChangePasswordBeforeLogin' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__ChangePasswordBeforeLogin','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Customer' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Customer','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Dashboard' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Dashboard','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__DeviceGroup' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__DeviceGroup','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__DlgMasters' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__DlgMasters','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__FileServices' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__FileServices','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__InactiveUsers' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__InactiveUsers','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Interfaces' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Interfaces','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Jobscheduler' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Jobscheduler','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Landing' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Landing','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Login' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Login','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Notifications' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Notifications','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__PushNotifications' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__PushNotifications','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Role' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Role','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Screens' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Screens','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__SecurityParameters' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__SecurityParameters','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Statistics' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Statistics','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__TaskRepair' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__TaskRepair','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__TransactionDetails' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__TransactionDetails','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__Usage' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__Usage','admin','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='Admin' AND SCREEN_ID='Admin__User' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','Admin__User','admin','Admin',sysdate,1);

/* Role-Interface Mapping */
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__AdminOperationsReport' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__AdminOperationsReport','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__appzillonAppUsageReport' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__appzillonAppUsageReport','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__appzillonCustomer' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__appzillonCustomer','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__appzillonCustomerDetails' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__appzillonCustomerDetails','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__appzillonDashBoard' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__appzillonDashBoard','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__appzillonLoginReport' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__appzillonLoginReport','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__appzillonMessageStatistics' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__appzillonMessageStatistics','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__appzillonNotificationAppDetail' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__appzillonNotificationAppDetail','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__appzillonPushNotification' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__appzillonPushNotification','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__appzillonSaveAppAccess' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__appzillonSaveAppAccess','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__appzillonSearchTxnLogging' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__appzillonSearchTxnLogging','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__DashboardDummy' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__DashboardDummy','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__deleteRole_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__deleteRole_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__deleteRole_Delete' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__deleteRole_Delete','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__DeviceGroupsDetail_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__DeviceGroupsDetail_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__DeviceGroupsQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__DeviceGroupsQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__DeviceGroupsQuery_Delete' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__DeviceGroupsQuery_Delete','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__DeviceGroupsSummary' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__DeviceGroupsSummary','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__DlgMasterDetail_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__DlgMasterDetail_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__DlgMasterDetail_New' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__DlgMasterDetail_New','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__DlgMasterDetailModifyDel_Modify' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__DlgMasterDetailModifyDel_Modify','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__DlgMasterDetailModifyDel_Delete' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__DlgMasterDetailModifyDel_Delete','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__FetchAllowAppIdSQL' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__FetchAllowAppIdSQL','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__FetchAppMasterAppIDSQL' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__FetchAppMasterAppIDSQL','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__FetchNotificationCode_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__FetchNotificationCode_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__FetchNotificationCodeSQL' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__FetchNotificationCodeSQL','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__GeoCoding' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__GeoCoding','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__getTransactionReqResp' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__getTransactionReqResp','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__InactiveUsersReport' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__InactiveUsersReport','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__InactiveUsersSql' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__InactiveUsersSql','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__InterfaceQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__InterfaceQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__InterfaceQuery_Delete' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__InterfaceQuery_Delete','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__InterfacesDetailQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__InterfacesDetailQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__InterfacesDetailQuery_New' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__InterfacesDetailQuery_New','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__InterfacesDetailQuery_Modify' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__InterfacesDetailQuery_Modify','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__InterfacesDetailQuery_Authorize' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__InterfacesDetailQuery_Authorize','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__MessageStatistics_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__MessageStatistics_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__NotificationsReport' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__NotificationsReport','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__QuartzJobData' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__QuartzJobData','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__QuartzSchedulerDetailQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__QuartzSchedulerDetailQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__QuartzSchedulerDetailQuery_New' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__QuartzSchedulerDetailQuery_New','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__QuartzSchedulerDetailQuery_Modify' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__QuartzSchedulerDetailQuery_Modify','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__QuartzSchedulerDetailQuery_Delete' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__QuartzSchedulerDetailQuery_Delete','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__QuartzSchedulerQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__QuartzSchedulerQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__RoleDetailDesc_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__RoleDetailDesc_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__RoleDetailDesc_New' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__RoleDetailDesc_New','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__RoleDetailDesc_Modify' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__RoleDetailDesc_Modify','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__RoleDetailQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__RoleDetailQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__RoleDetailQuery_New' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__RoleDetailQuery_New','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__RoleDetailUpdate_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__RoleDetailUpdate_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__RoleDetailUpdate_Modify' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__RoleDetailUpdate_Modify','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__RoleDetailUpdate_Authorize' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__RoleDetailUpdate_Authorize','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__RolesQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__RolesQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__ScreenDetailQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__ScreenDetailQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__ScreenDetailQuery_New' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__ScreenDetailQuery_New','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__ScreenDetailQuery_Modify' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__ScreenDetailQuery_Modify','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__ScreenDetailQuery_Authorize' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__ScreenDetailQuery_Authorize','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__ScreensQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__ScreensQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__ScreensQuery_Delete' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__ScreensQuery_Delete','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__SecurityParamsDetailQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__SecurityParamsDetailQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__SecurityParamsDetailQuery_New' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__SecurityParamsDetailQuery_New','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__SecurityParamsDetailQuery_Modify' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__SecurityParamsDetailQuery_Modify','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__SecurityParamsDetailQuery_Authorize' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__SecurityParamsDetailQuery_Authorize','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__SecurityParamsQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__SecurityParamsQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__SecurityParamsQuery_Delete' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__SecurityParamsQuery_Delete','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__StatisticsReport' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__StatisticsReport','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__TaskRepairDetailQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__TaskRepairDetailQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__TaskRepairDetailQuery_Modify' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__TaskRepairDetailQuery_Modify','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__TaskRepairDetailQuery_Delete' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__TaskRepairDetailQuery_Delete','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__TaskRepairQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__TaskRepairQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__TransactionReport' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__TransactionReport','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__UsageReport' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__UsageReport','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__UserDetailsQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__UserDetailsQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__UserModifyStatusSQL' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__UserModifyStatusSQL','Admin',sysdate,1);

DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='Admin__UserProfileDetailQuery_Query' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('Admin','admin','Admin__UserProfileDetailQuery_Query','Admin',sysdate,1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__DeviceGroup__deviceGroupAdd');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__DeviceGroup__deviceGroupAdd' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__DeviceGroup__deviceGroupAdd');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__DeviceGroup__deviceGroupAdd' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__DeviceGroup__deviceGroupEdit');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__DeviceGroup__deviceGroupEdit' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__DeviceGroup__deviceGroupEdit');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__DeviceGroup__deviceGroupEdit' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__Interfaces__InterfaceAdd');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__Interfaces__InterfaceAdd' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__Interfaces__InterfaceAdd');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__Interfaces__InterfaceAdd' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__Interfaces__InterfaceDetailEdit');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__Interfaces__InterfaceDetailEdit' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__Interfaces__InterfaceDetailEdit');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__Interfaces__InterfaceDetailEdit' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__Interfaces__IntfAuthorize');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__Interfaces__IntfAuthorize' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__Interfaces__IntfAuthorize');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__Interfaces__IntfAuthorize' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__Jobscheduler__screenAdd');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__Jobscheduler__screenAdd' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__Jobscheduler__screenAdd');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__Jobscheduler__screenAdd' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__Role__roleAdd');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__Role__roleAdd' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__Role__roleAdd');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__Role__roleAdd' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__Role__roleEdit');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__Role__roleEdit' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__Role__roleEdit');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__Role__roleEdit' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__Role__RoleAuthenticateBtn');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__Role__RoleAuthenticateBtn' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__Role__RoleAuthenticateBtn');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__Role__RoleAuthenticateBtn' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__Screens__screenAdd');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__Screens__screenAdd' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__Screens__screenAdd');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__Screens__screenAdd' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__Screens__screenEdit');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__Screens__screenEdit' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__Screens__screenEdit');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__Screens__screenEdit' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__Screens__screenAuthenticate');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__Screens__screenAuthenticate' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__Screens__screenAuthenticate');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__Screens__screenAuthenticate' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__SecurityParameters__securityParameterEdit');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__SecurityParameters__securityParameterEdit' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__SecurityParameters__securityParameterEdit');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__SecurityParameters__securityParameterEdit' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__SecurityParameters__securityParameterAuthenticate');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__SecurityParameters__securityParameterAuthenticate' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__SecurityParameters__securityParameterAuthenticate');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__SecurityParameters__securityParameterAuthenticate' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__TaskRepair__taskRepairEdit');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__TaskRepair__taskRepairEdit' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__TaskRepair__taskRepairEdit');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__TaskRepair__taskRepairEdit' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__User__userAdd');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__User__userAdd' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__User__userAdd');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__User__userAdd' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__User__userEdit');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__User__userEdit' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__User__userEdit');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__User__userEdit' , 'Admin',sysdate, 1);

DELETE FROM TB_ASMI_CONTROLS_MASTER WHERE (APP_ID='Admin' AND CONTROL_ID='Admin__User__userAuthenticate');
insert  into TB_ASMI_CONTROLS_MASTER(APP_ID, CONTROL_ID , CONTROL_DESC,CREATED_BY,CREATED_TS ,  MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS , VERSION_NO) values('Admin', 'Admin__User__userAuthenticate' , '' , 'admin' , sysdate , 'admin' , sysdate, 'admin' , sysdate, 'A' ,1);

DELETE FROM TB_ASMI_ROLE_CONTROLS WHERE (APP_ID='Admin' AND ROLE_ID='admin' AND CONTROL_ID='Admin__User__userAuthenticate');
insert  into TB_ASMI_ROLE_CONTROLS(APP_ID, ROLE_ID , CONTROL_ID,CREATED_BY,CREATED_TS,VERSION_NO) values('Admin','admin', 'Admin__User__userAuthenticate' , 'Admin',sysdate, 1);

