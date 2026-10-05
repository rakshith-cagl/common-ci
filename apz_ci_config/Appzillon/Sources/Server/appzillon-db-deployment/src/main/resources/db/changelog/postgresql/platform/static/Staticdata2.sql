
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetUser' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetUser','Admin','INTERNAL','INTERNAL','appzillonGetUser','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetUser','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateRoleMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateRoleMaster','Admin','INTERNAL','INTERNAL','appzillonUpdateRoleMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateRoleMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteRoleMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteRoleMaster','Admin','INTERNAL','INTERNAL','appzillonDeleteRoleMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteRoleMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetRoleMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetRoleMaster','Admin','INTERNAL','INTERNAL','appzillonGetRoleMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetRoleMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetScreensIntfByAppID' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetScreensIntfByAppID','Admin','INTERNAL','INTERNAL','appzillonGetScreensIntfByAppID','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetScreensIntfByAppID' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetScreensIntfByAppID','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetIntfScrByAppIDRoleID' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetIntfScrByAppIDRoleID','Admin','INTERNAL','INTERNAL','appzillonGetIntfScrByAppIDRoleID','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetIntfScrByAppIDRoleID' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetIntfScrByAppIDRoleID','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchIntfMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchIntfMaster','Admin','INTERNAL','INTERNAL','appzillonSearchIntfMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchIntfMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateIntfMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateIntfMaster','Admin','INTERNAL','INTERNAL','appzillonCreateIntfMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateIntfMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteIntfMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteIntfMaster','Admin','INTERNAL','INTERNAL','appzillonDeleteIntfMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteIntfMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateIntfMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateIntfMaster','Admin','INTERNAL','INTERNAL','appzillonUpdateIntfMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateIntfMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonPasswordValidate' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonPasswordValidate','Admin','INTERNAL','INTERNAL','appzillonPasswordValidate','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonPasswordValidate' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonPasswordValidate','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'JMSRespFetchReq' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('JMSRespFetchReq','Admin','INTERNAL','INTERNAL','JMSRespFetchReq','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='JMSRespFetchReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','JMSRespFetchReq','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonMessageStatistics' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonMessageStatistics','Admin','INTERNAL','INTERNAL','appzillonMessageStatistics','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonMessageStatistics' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonMessageStatistics','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDashBoard' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDashBoard','Admin','INTERNAL','INTERNAL','appzillonDashBoard','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDashBoard' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDashBoard','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonErrorLogging' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonErrorLogging','Admin','INTERNAL','INTERNAL','appzillonErrorLogging','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonErrorLogging' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonErrorLogging','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'deviceStatus_Req' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('deviceStatus_Req','Admin','INTERNAL','INTERNAL','deviceStatus_Req','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='deviceStatus_Req' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','deviceStatus_Req','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetChildAppDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetChildAppDetails','Admin','INTERNAL','INTERNAL','appzillonGetChildAppDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetChildAppDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetChildAppDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateAppMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateAppMaster','Admin','INTERNAL','INTERNAL','appzillonCreateAppMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateAppMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateAppMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateAppMaster','Admin','INTERNAL','INTERNAL','appzillonUpdateAppMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateAppMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchAppMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchAppMaster','Admin','INTERNAL','INTERNAL','appzillonSearchAppMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchAppMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteAppMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteAppMaster','Admin','INTERNAL','INTERNAL','appzillonDeleteAppMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteAppMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateAppFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateAppFile','Admin','INTERNAL','INTERNAL','appzillonCreateAppFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCreateAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCreateAppFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateAppFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateAppFile','Admin','INTERNAL','INTERNAL','appzillonUpdateAppFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateAppFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchAppFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchAppFile','Admin','INTERNAL','INTERNAL','appzillonSearchAppFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchAppFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteAppFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteAppFile','Admin','INTERNAL','INTERNAL','appzillonDeleteAppFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteAppFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchDeviceMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchDeviceMaster','Admin','INTERNAL','INTERNAL','appzillonSearchDeviceMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchDeviceMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchDeviceMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteDeviceMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteDeviceMaster','Admin','INTERNAL','INTERNAL','appzillonDeleteDeviceMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteDeviceMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteDeviceMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateDeviceMaster' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateDeviceMaster','Admin','INTERNAL','INTERNAL','appzillonUpdateDeviceMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateDeviceMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateDeviceMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'searchTaskRepair' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('searchTaskRepair','Admin','INTERNAL','INTERNAL','searchTaskRepair','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='searchTaskRepair' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','searchTaskRepair','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'updateTaskRepair' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('updateTaskRepair','Admin','INTERNAL','INTERNAL','updateTaskRepair','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='updateTaskRepair' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','updateTaskRepair','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotifyMobileNumber' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonNotifyMobileNumber','Admin','INTERNAL','INTERNAL','appzillonNotifyMobileNumber','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonNotifyMobileNumber' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonNotifyMobileNumber','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotifyDevice' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonNotifyDevice','Admin','INTERNAL','INTERNAL','appzillonNotifyDevice','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonNotifyDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonNotifyDevice','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonInsertBeacon' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonInsertBeacon','Admin','INTERNAL','INTERNAL','appzillonInsertBeacon','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonInsertBeacon' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonInsertBeacon','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFetchBeaconDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFetchBeaconDetails','Admin','INTERNAL','INTERNAL','appzillonFetchBeaconDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonFetchBeaconDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonFetchBeaconDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateBeaconDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateBeaconDetails','Admin','INTERNAL','INTERNAL','appzillonUpdateBeaconDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateBeaconDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateBeaconDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFetchARDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFetchARDetails','Admin','INTERNAL','INTERNAL','appzillonFetchARDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonFetchARDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonFetchARDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonInsertDragDrop' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonInsertDragDrop','Admin','INTERNAL','INTERNAL','appzillonInsertDragDrop','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonInsertDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonInsertDragDrop','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateDragDrop' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateDragDrop','Admin','INTERNAL','INTERNAL','appzillonUpdateDragDrop','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUpdateDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUpdateDragDrop','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteDragDrop' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteDragDrop','Admin','INTERNAL','INTERNAL','appzillonDeleteDragDrop','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeleteDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeleteDragDrop','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchDragDrop' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchDragDrop','Admin','INTERNAL','INTERNAL','appzillonSearchDragDrop','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSearchDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSearchDragDrop','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCustomer' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCustomer','Admin','INTERNAL','INTERNAL','appzillonCustomer','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCustomer' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCustomer','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCustomerLocation' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCustomerLocation','Admin','INTERNAL','INTERNAL','appzillonCustomerLocation','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCustomerLocation' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCustomerLocation','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCustomerDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCustomerDetails','Admin','INTERNAL','INTERNAL','appzillonCustomerDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonCustomerDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonCustomerDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeviceGrpQuery' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeviceGrpQuery','Admin','INTERNAL','INTERNAL','appzillonDeviceGrpQuery','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonDeviceGrpQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonDeviceGrpQuery','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAppScreensQuery' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonAppScreensQuery','Admin','INTERNAL','INTERNAL','appzillonAppScreensQuery','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonAppScreensQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonAppScreensQuery','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSaveCustomizationData' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSaveCustomizationData','Admin','INTERNAL','INTERNAL','appzillonSaveCustomizationData','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSaveCustomizationData' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSaveCustomizationData','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetCustomizerDetails' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetCustomizerDetails','Admin','INTERNAL','INTERNAL','appzillonGetCustomizerDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetCustomizerDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetCustomizerDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetQueryDesignerData' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetQueryDesignerData','Admin','INTERNAL','INTERNAL','appzillonGetQueryDesignerData','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetQueryDesignerData' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetQueryDesignerData','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSaveAppAccess' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSaveAppAccess','Admin','INTERNAL','INTERNAL','create and update user app access','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonSaveAppAccess' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonSaveAppAccess','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAuthenticationRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonAuthenticationRequest','Admin','INTERNAL','INTERNAL','appzillonAuthenticationRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonAuthenticationRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonAuthenticationRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonChangePassword' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonChangePassword','Admin','INTERNAL','INTERNAL','appzillonChangePassword','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonChangePassword' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonChangePassword','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotificationRegistration' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonNotificationRegistration','Admin','INTERNAL','INTERNAL','appzillonNotificationRegistration','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonNotificationRegistration' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonNotificationRegistration','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonReLoginRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonReLoginRequest','Admin','INTERNAL','INTERNAL','appzillonReLoginRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonReLoginRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonReLoginRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAuditLog' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonAuditLog','Admin','INTERNAL','INTERNAL','appzillonAuditLog','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonAuditLog' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonAuditLog','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonOTAFileDownloadReq' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonOTAFileDownloadReq','Admin','INTERNAL','INTERNAL','appzillonOTAFileDownloadReq','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonOTAFileDownloadReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonOTAFileDownloadReq','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetAppFile' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonGetAppFile','Admin','INTERNAL','INTERNAL','appzillonGetAppFile','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonGetAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonGetAppFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonMailRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonMailRequest','Admin','INTERNAL','INTERNAL','appzillonMailRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonMailRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonMailRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUserRegistration' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonUserRegistration','Admin','INTERNAL','INTERNAL','appzillonUserRegistration','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonUserRegistration' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonUserRegistration','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonInterfaceAuthRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonInterfaceAuthRequest','Admin','INTERNAL','INTERNAL','appzillonInterfaceAuthRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonInterfaceAuthRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonInterfaceAuthRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonScreenAuthRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonScreenAuthRequest','Admin','INTERNAL','INTERNAL','appzillonScreenAuthRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonScreenAuthRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonScreenAuthRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFetchPrivilegeService' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonFetchPrivilegeService','Admin','INTERNAL','INTERNAL','appzillonFetchPrivilegeService','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonFetchPrivilegeService' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonFetchPrivilegeService','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonLogoutRequest' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonLogoutRequest','Admin','INTERNAL','INTERNAL','appzillonLogoutRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonLogoutRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonLogoutRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonLOVReq' AND APP_ID='Admin');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF,TXN_LOG_REQ,TXN_LOG_PAYLOAD_REQ,DG_TXN_LOG_REQ) values('appzillonLOVReq','Admin','INTERNAL','INTERNAL','appzillonLOVReq','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'','N','N','N');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='Admin' AND INTERFACE_ID='appzillonLOVReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('Admin','admin','appzillonLOVReq','Admin',now(),1);

