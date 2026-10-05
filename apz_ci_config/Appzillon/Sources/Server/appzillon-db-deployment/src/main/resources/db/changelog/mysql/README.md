mysql_liquibase_command_to_generate_changelog

./liquibase --driver=com.mysql.jdbc.Driver --classpath=mysql-connector-java-8.0.22.jar --changeLogFile=mysql_changelog2.xml --url="jdbc:mysql://localhost:3306/appzillon_db?currentSchema=test" --username=test --password=xxxx --liquibaseSchemaName=test generateChangeLog
