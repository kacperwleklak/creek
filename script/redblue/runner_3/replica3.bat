set PG_PORT=5435
set DBNAME=mem:creek
set COMMUNICATION_REPLICAS_PORT=10003
set COMMUNICATION_REPLICAS_HOST=localhost
set SERVER_PORT=8083
set COMMUNICATION_REPLICAS_NODES=localhost:10001,localhost:10002,localhost:10003
set COMMUNICATION_REPLICAS_ID=3
set LOG_LEVEL=debug

java -Xms512m -Xmx1024m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:9003 -jar ..\..\..\redblue-impl\target\redblue-impl-0.0.1-SNAPSHOT.jar

