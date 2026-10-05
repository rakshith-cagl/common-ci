--------------------------------------------------------
--  Migration script from 3.18.0 to 3.19.0
--------------------------------------------------------
 --NOTE : Take backup of the existing table data

 DROP TABLE IF EXISTS TB_ASMI_CS_NONCEDETAILS CASCADE;

CREATE TABLE TB_ASMI_CS_NONCEDETAILS (
  DEVICE_ID varchar(100) ,
  APP_ID varchar(100),
  REQUEST_ID varchar(255),
  CLIENT_NONCE varchar(100),
  SERVER_NONCE varchar(100),
  STATUS varchar(100) ,
  SERVER_TOKEN varchar(100),
  CREATE_TS timestamp NULL DEFAULT CURRENT_TIMESTAMP,
CREATED_ON DATE
)
PARTITION BY RANGE(CREATED_ON);


 --NOTE : Have to do partition and subpartition on daily basis or write a procedure to do it automatically

CREATE TABLE public.tb_asmi_cs_noncedetails_p2021_10_08 PARTITION OF public.tb_asmi_cs_noncedetails
FOR VALUES FROM ('2021-10-08') TO ('2021-10-09')
PARTITION by hash (device_id);

create table tb_asmi_cs_noncedetails_p2021_10_08_device_id_01 partition of tb_asmi_cs_noncedetails_p2021_10_08 FOR VALUES WITH (MODULUS 8, REMAINDER 0);
create table tb_asmi_cs_noncedetails_p2021_10_08_device_id_02 partition of tb_asmi_cs_noncedetails_p2021_10_08 FOR VALUES WITH (MODULUS 8, REMAINDER 1);
create table tb_asmi_cs_noncedetails_p2021_10_08_device_id_03 partition of tb_asmi_cs_noncedetails_p2021_10_08 FOR VALUES WITH (MODULUS 8, REMAINDER 2);
create table tb_asmi_cs_noncedetails_p2021_10_08_device_id_04 partition of tb_asmi_cs_noncedetails_p2021_10_08 FOR VALUES WITH (MODULUS 8, REMAINDER 3);
create table tb_asmi_cs_noncedetails_p2021_10_08_device_id_05 partition of tb_asmi_cs_noncedetails_p2021_10_08 FOR VALUES WITH (MODULUS 8, REMAINDER 4);
create table tb_asmi_cs_noncedetails_p2021_10_08_device_id_06 partition of tb_asmi_cs_noncedetails_p2021_10_08 FOR VALUES WITH (MODULUS 8, REMAINDER 5);
create table tb_asmi_cs_noncedetails_p2021_10_08_device_id_07 partition of tb_asmi_cs_noncedetails_p2021_10_08 FOR VALUES WITH (MODULUS 8, REMAINDER 6);
create table tb_asmi_cs_noncedetails_p2021_10_08_device_id_08 partition of tb_asmi_cs_noncedetails_p2021_10_08 FOR VALUES WITH (MODULUS 8, REMAINDER 7);

