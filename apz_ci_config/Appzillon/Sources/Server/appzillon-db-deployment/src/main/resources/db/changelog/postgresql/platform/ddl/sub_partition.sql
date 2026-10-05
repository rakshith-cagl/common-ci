
CREATE TABLE tb_asmi_cs_noncedetails_p:table_name_param PARTITION OF tb_asmi_cs_noncedetails
FOR VALUES FROM (':today') TO (':tomorrow')
PARTITION by hash (device_id);

create table tb_asmi_cs_noncedetails_device_id_01_p:table_name_param partition of tb_asmi_cs_noncedetails_p:table_name_param FOR VALUES WITH (MODULUS 8, REMAINDER 0);
create table tb_asmi_cs_noncedetails_device_id_02_p:table_name_param partition of tb_asmi_cs_noncedetails_p:table_name_param FOR VALUES WITH (MODULUS 8, REMAINDER 1);
create table tb_asmi_cs_noncedetails_device_id_03_p:table_name_param partition of tb_asmi_cs_noncedetails_p:table_name_param FOR VALUES WITH (MODULUS 8, REMAINDER 2);
create table tb_asmi_cs_noncedetails_device_id_04_p:table_name_param partition of tb_asmi_cs_noncedetails_p:table_name_param FOR VALUES WITH (MODULUS 8, REMAINDER 3);
create table tb_asmi_cs_noncedetails_device_id_05_p:table_name_param partition of tb_asmi_cs_noncedetails_p:table_name_param FOR VALUES WITH (MODULUS 8, REMAINDER 4);
create table tb_asmi_cs_noncedetails_device_id_06_p:table_name_param partition of tb_asmi_cs_noncedetails_p:table_name_param FOR VALUES WITH (MODULUS 8, REMAINDER 5);
create table tb_asmi_cs_noncedetails_device_id_07_p:table_name_param partition of tb_asmi_cs_noncedetails_p:table_name_param FOR VALUES WITH (MODULUS 8, REMAINDER 6);
create table tb_asmi_cs_noncedetails_device_id_08_p:table_name_param partition of tb_asmi_cs_noncedetails_p:table_name_param FOR VALUES WITH (MODULUS 8, REMAINDER 7);
