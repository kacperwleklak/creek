set PG_PORT=5433
set DBNAME=mem:creek
set COMMUNICATION_REPLICAS_PORT=10001
set COMMUNICATION_REPLICAS_HOST=localhost
set SERVER_PORT=8081
set COMMUNICATION_REPLICAS_NODES=localhost:10001,localhost:10002
set COMMUNICATION_REPLICAS_ID=1
set LOG_LEVEL=debug

java -Xms512m -Xmx1024m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:9001 -jar ..\..\..\redblue-impl\target\redblue-impl-0.0.1-SNAPSHOT.jar

