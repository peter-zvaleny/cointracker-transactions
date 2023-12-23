
# bitcoin transaction sync

syncs bitcoin transactions to mongo db


### prerequisites:

- java 17
- docker (for tests)
- mongo db (to run - at localhost or change `resources/application.yaml`)

### how to build:
```
./gradlew build
```

### how to run:
```
./gradlew bootRun
```
or
```
./gradlew bootTestRun
```
(to start a mongodb in docker container - destroyed when closed)

### api:

GET http://localhost:8080/transactions/<address>
list of transactions for the address (first 50)

POST http://localhost:8080/transactions/:address
initializes a background sync of transactions (first 2 pages)


### notes:

- made some assumptions about the API - got blocked
- looks like current balance can be fetched from the latest transaction
- what if new transaction is added while iterating over pages