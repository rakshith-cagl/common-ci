--------------------------------------------------------
--  Migration script from 3.18.0 to 3.19.0
--------------------------------------------------------
--NOTE : Take backup of the existing table data

DROP TABLE IF EXISTS `TB_ASMI_CS_NONCEDETAILS`;


CREATE TABLE `TB_ASMI_CS_NONCEDETAILS` (
`DEVICE_ID` VARCHAR(100),
`APP_ID` VARCHAR(100),
`REQUEST_ID` VARCHAR(255),
`CLIENT_NONCE` VARCHAR(100),
`SERVER_NONCE` VARCHAR(100),
`STATUS` VARCHAR(100),
`SERVER_TOKEN` VARCHAR(100),
`CREATE_TS` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
`CREATED_ON` DATE
)
/* this partition by key, partition by key is not available in mysql DB */
PARTITION BY KEY(DEVICE_ID)
PARTITIONS 64;


