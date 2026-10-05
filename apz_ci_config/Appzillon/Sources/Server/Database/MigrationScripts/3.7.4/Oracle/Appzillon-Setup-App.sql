--------------------------------------------------------
--  Migration script from 3.7.4 to 3.7.5
--------------------------------------------------------
ALTER TABLE TB_ASNF_DEVICES_MASTER
ADD NOTIF_MSG_SERVER VARCHAR2(255) DEFAULT NULL;

ALTER TABLE TB_ASMI_APP_OS_VERSION
ADD APP_ACTION VARCHAR2(10) NOT NULL DEFAULT 'IGNORE';