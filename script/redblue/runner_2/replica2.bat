set PG_PORT=5434
set DBNAME=mem:creek
set COMMUNICATION_REPLICAS_PORT=10002
set COMMUNICATION_REPLICAS_HOST=localhost
set SERVER_PORT=8082
set COMMUNICATION_REPLICAS_NODES=localhost:10001,localhost:10002
set COMMUNICATION_REPLICAS_ID=2
set LOG_LEVEL=info
set TOKEN_TTL=1000

java -Xms1024m -Xmx2048m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:9002 -jar ..\..\..\redblue-impl\target\redblue-impl-0.0.1-SNAPSHOT.jar

