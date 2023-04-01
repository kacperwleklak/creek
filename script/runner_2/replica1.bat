set PG_PORT=5433
set DBNAME=./creek1
set COMMUNICATION_REPLICAS_PORT=10001
set COMMUNICATION_REPLICAS_HOST=localhost
set SERVER_PORT=8081
set COMMUNICATION_REPLICAS_NODES=localhost:10001,localhost:10002
set COMMUNICATION_REPLICAS_ID=1

"C:\Program Files\graalvm\bin\java" -jar -Xms512m -Xmx1024m ..\..\creek-impl\target\creek-impl-0.0.1-SNAPSHOT.jar