--------------------------------------------------------
--  Migration script from 3.7.4 to 3.7.5
--------------------------------------------------------
ALTER TABLE TB_ASMI_DEVICE_MASTER
ADD COLUMN APP_VERSION VARCHAR(50) NULL;
