set PG_PORT=5434
set DBNAME=./creek2
set COMMUNICATION_REPLICAS_PORT=10002
set COMMUNICATION_REPLICAS_HOST=localhost
set SERVER_PORT=8082
set COMMUNICATION_REPLICAS_NODES=localhost:10001,localhost:10002
set COMMUNICATION_REPLICAS_ID=26

"C:\Program Files\graalvm\bin\java" -jar -jar -Xms512m -Xmx1024m ..\..\creek-impl\target\creek-impl-0.0.1-SNAPSHOT.jar