--------------------------------------------------------
--  Migration script from 3.12.0 to 3.13.0
--------------------------------------------------------

ALTER TABLE TB_ASMI_INTF_MASTER
ADD CAPTCHA_TYPE VARCHAR2(7) DEFAULT 'N';

