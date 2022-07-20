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

## 📝 TODO-list
* CAB module:
  * Waiting with ACCEPT until predicate Q becomes true
* Creek module:
  * Rollback sql operations