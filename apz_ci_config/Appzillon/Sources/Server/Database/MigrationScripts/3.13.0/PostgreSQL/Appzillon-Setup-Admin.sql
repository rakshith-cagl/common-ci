--------------------------------------------------------
--  Migration script from 3.12.0 to 3.13.0
--------------------------------------------------------
ALTER TABLE TB_ASMI_INTF_MASTERS
  ADD COLUMN CAPTCHA_TYPE VARCHAR(7) DEFAULT 'N';


