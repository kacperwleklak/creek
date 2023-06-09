set PG_PORT=5433
set DBNAME=mem:creek
set COMMUNICATION_REPLICAS_PORT=10001
set COMMUNICATION_REPLICAS_HOST=localhost
set SERVER_PORT=8081
set COMMUNICATION_REPLICAS_NODES=localhost:10001,localhost:10002,localhost:10003
set COMMUNICATION_REPLICAS_ID=1
set LOG_LEVEL=debug
set CAB_PROBABILITY=0.00

java -Xms512m -Xmx1024m -jar ..\..\creek-impl\target\creek-impl-0.0.1-SNAPSHOT.jar