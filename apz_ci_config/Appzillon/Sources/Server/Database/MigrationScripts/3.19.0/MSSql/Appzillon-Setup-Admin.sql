--------------------------------------------------------
--  Migration script from 3.18.0 to 3.19.0
--------------------------------------------------------
--NOTE : Take backup of the existing table data

DROP TABLE IF EXISTS "TB_ASMI_CS_NONCEDETAILS";

CREATE TABLE "TB_ASMI_CS_NONCEDETAILS" (
  "DEVICE_ID" NVARCHAR(100),
  "APP_ID" NVARCHAR(100),
  "REQUEST_ID" NVARCHAR(255),
  "CLIENT_NONCE" NVARCHAR(100),
  "SERVER_NONCE" NVARCHAR(100),
  "STATUS" NVARCHAR(100),
  "SERVER_TOKEN" NVARCHAR(100),
  "CREATE_TS" DATETIME DEFAULT CURRENT_TIMESTAMP,
  "CREATED_ON" DATE,
   INDEX TB_ASMI_CS_NONCEDETAILS_INDEX ("SERVER_NONCE")
  );
 /* creating function */

   CREATE PARTITION FUNCTION [Hashing] (TINYINT) AS RANGE LEFT FOR VALUES (0, 1, 2, 3, 4, 5, 6, 7)

     /* creating partition schema  */
    CREATE PARTITION SCHEME [ps_Hashing] AS PARTITION [Hashing] ALL TO ('PRIMARY')

    /*  adding HashVaule column */
    ALTER TABLE [dbo].[TB_ASMI_CS_NONCEDETAILS]
    ADD [HASH_VALUE] AS (CONVERT([tinyint], abs(binary_checksum([DEVICE_ID])%(12)),(0)))
    PERSISTED NOT NULL

    /* creating clustered index */
    CREATE CLUSTERED INDEX [TB_ASMI_CS_NONCEDETAILS_INDEX_1]
    ON [dbo].[TB_ASMI_CS_NONCEDETAILS]
    ([DEVICE_ID] ASC, [HASH_VALUE])
    ON ps_Hashing(HASH_VALUE)


   /*  create unique index */
    create unique index TB_ASMI_CS_NONCEDETAILS_IDX01 on TB_ASMI_CS_NONCEDETAILS(DEVICE_ID, APP_ID, REQUEST_ID, CLIENT_NONCE, SERVER_NONCE, HASH_VALUE);


