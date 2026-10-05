# m h  dom mon dow   command
#0 1 * * * /..<FULL_PATH>../src/main/resources/db/changelog/postgresql/release-3.14.0/platform/ddl/postgres_scheduler.sh




table_name_param=$(date '+%Y_%m_%d');

PGPASSWORD=xxxx psql -h 18.118.80.177 -p 5432 -U test test -v table_name_param=$table_name_param -f /..<FULL_PATH>../src/main/resources/db/changelog/postgresql/release-3.14.0/platform/ddl/sub_partition.sql
