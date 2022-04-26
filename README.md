# Creek

Diploma thesis - Implementation and performance evaluation of the Creek replication protocol

## 🏃 Running project

Recommended way of running this project is to use provided docker-compose file.
```shell
docker-compose up -d
```
It is also possible to run each replica from sources using
`pl.poznan.put.kacperwleklak.CreekApplication` as a main class, but you need to remember
about providing environment variables (see `docker-compose.yml` file and)

## 💻 Usage

### Weak message example
```shell
curl --location --request POST 'http://localhost:8082/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "uuid": "123",
    "operation": {
        "operationType": "read",
        "key": "x"
    }
}'
```
Please note that current implementation of Creek protocol does not feature weak messages
broadcast. So, if You want to deliver this message to all the replicas, You need to manually
send the message to each one.

They are some types of predefined operation types, but their behaviour is not implemented
yet.

### Strong message example
```shell
curl --location --request POST 'http://localhost:8082/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "uuid": "123456",
    "operation": {
        "operationType": "read",
        "key": "x"
    },
    "predicate": {
        "predicateType": "ARE_MESSAGES_DELIVERED",
        "messagesMustBeDelivered": ["123", "456"]
    }
}'
```
This is an example of strong operation, which will be delivered after meeting specified condition
(predicate).

## 📝 TODO-list
* CAB module:
  * Waiting with ACCEPT until predicate Q becomes true
* Creek module:
  * To implement