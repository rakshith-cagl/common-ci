--------------------------------------------------------
--  Migration script from 3.7.8 to 3.7.9
--------------------------------------------------------
DROP TABLE IF EXISTS TB_ASMI_AZURE_TOKEN;

ALTER TABLE TB_ASMI_USER
  ADD COLUMN LAST_NAME VARCHAR(100);

ALTER TABLE TB_ASHS_USER
  ADD COLUMN LAST_NAME VARCHAR(100);

