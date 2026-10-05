--------------------------------------------------------
--  Migration script from 3.7.6 to 3.7.7
--------------------------------------------------------

ALTER TABLE "TB_ASNF_TXN_LOG" ADD "USER_ID" VARCHAR(100) DEFAULT NULL;