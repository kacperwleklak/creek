set PG_PORT=5434
set DBNAME=./creek2
set COMMUNICATION_REPLICAS_PORT=10002
set COMMUNICATION_REPLICAS_HOST=localhost
set SERVER_PORT=8082
set COMMUNICATION_REPLICAS_NODES=localhost:10001,localhost:10002,localhost:10003,localhost:10004
set COMMUNICATION_REPLICAS_ID=26

java -jar ..\..\creek-impl\target\creek-impl-0.0.1-SNAPSHOT.jar