postgresql_liquibase_command_to_generate_changelog

./liquibase --driver=org.postgresql.Driver --classpath=postgresql-42.2.21.jar --changeLogFile=postgres_changelog.xml --url="jdbc:postgresql://localhost:5432/appzillon?currentSchema=test" --username=test --password=xxxx --liquibaseSchemaName=test generateChangeLog
