--------------------------------------------------------
--  Migration script from 3.7.4 to 3.7.5
--------------------------------------------------------
ALTER TABLE TB_ASMI_DEVICE_MASTER
ADD APP_VERSION VARCHAR2(50) NULL;
