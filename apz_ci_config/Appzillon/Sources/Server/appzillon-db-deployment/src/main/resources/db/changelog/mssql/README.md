mssql_liquibase_command_to_generate_changelog

./liquibase --driver=com.microsoft.sqlserver.jdbc.SQLServerDriver --classpath=mssql-jdbc-9.3.1.jre8-preview.jar --changeLogFile=mssql_changelog.xml --url="jdbc:sqlserver://192.168.2.4:1433;DatabaseName=test" --username=test --password=xxxx --liquibaseSchemaName=test generateChangeLog
