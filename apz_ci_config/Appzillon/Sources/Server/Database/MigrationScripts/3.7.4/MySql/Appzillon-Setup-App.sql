--------------------------------------------------------
--  Migration script from 3.7.4 to 3.7.5
--------------------------------------------------------
ALTER TABLE `TB_ASNF_DEVICES_MASTER`
ADD COLUMN `NOTIF_MSG_SERVER` VARCHAR(255) DEFAULT NULL;

ALTER TABLE `TB_ASMI_APP_OS_VERSION`
ADD COLUMN `APP_ACTION` VARCHAR(10) NOT NULL DEFAULT 'IGNORE';
