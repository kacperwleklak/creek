# Creek

Diploma thesis - Implementation and performance evaluation of the Creek replication protocol

## 🏃 Running project

#### Recommended
Open this directory in terminal and run following commands:

1. Create network
```shell
docker network create creek_network
```

2. Build docker image
```shell
docker build -t kacperwleklak/creek .
```

3. Run cluster of nodes
```shell
docker-compose up -d
```

#### Alternative
It is also possible to run each replica from sources using
`pl.poznan.put.kacperwleklak.creek.CreekApplicationRunner` as a main class, but you need to remember
about providing environment variables (see `docker-compose.yml` file and)

## 💻 Usage

#### Message example
```shell
curl --location --request POST 'http://localhost:8082/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "operation": "s=3;x=5;y=s+x",
    "strong": true
}'
```
Read the docs for operation syntax: [Mvel documentation](http://mvel.documentnode.com/)

#### Temporary test endpoints:
Returns StateObject
```shell
curl --location --request GET 'http://localhost:8082/state'
```

Returns list of responses to clients.
```shell
curl --location --request GET 'http://localhost:8082/responses'
```


## 📝 TODO-list
* CAB module:
  * Waiting with ACCEPT until predicate Q becomes true
* Creek module:
  * SQL statements parsing
  * Client interface with request response