oracle_liquibase_command_to_generate_changelog

./liquibase --driver=oracle.jdbc.driver.OracleDriver --classpath=ojdbc7.jar --changeLogFile=oracle_changelog.xml --url="jdbc:oracle:thin:@//192.168.1.1:1521/test" --username=test --password=xxxx --liquibaseSchemaName=test generateChangeLog
