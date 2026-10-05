--------------------------------------------------------
--  Migration script from 3.7.6 to 3.7.7
--------------------------------------------------------
DROP TABLE IF EXISTS `APZ_SERVER_PROPS`;

CREATE TABLE `APZ_SERVER_PROPS` (
`APP_ID` nvarchar(10),
`APZ_SERVER_PROPS_KEY` nvarchar(100),
`APZ_SERVER_PROPS_VALUE` nvarchar(500) NULL,
`APZ_SERVER_PROPS_DESC` nvarchar(100) NULL,
PRIMARY KEY (`APP_ID`, `APZ_SERVER_PROPS_KEY`)
);
DROP TABLE IF EXISTS `APZ_ENV_PROPS`;

CREATE TABLE `APZ_ENV_PROPS` (
`APP_ID` nvarchar(10),
`ENVIRONMENT` nvarchar(10),
`APZ_ENV_PROPS_KEY` nvarchar(100),
`APZ_ENV_PROPS_VALUE` nvarchar(500) NULL,
`APZ_ENV_PROPS_DESC` nvarchar(100) NULL,
PRIMARY KEY (`APP_ID`, `APZ_ENV_PROPS_KEY`,`ENVIRONMENT`)
);


