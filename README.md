

# Spring Boot + Kafka + CockroachDB

Testing out using Spring Boot 3.4 docker-compose integration, and also playing around a little with Postgres, CockroachDB and transactional Kafka publishing.

# CockroachDB

Some useful links
- basic not that useful
  - https://github.com/heroiclabs/nakama/blob/master/docker-compose.yml
- basic setup not that useful
  - https://gist.github.com/dbist/ebb1f39f580ad9d07c04c3a3377e2bff

## Creating the initial database

Compose file has an `init-crdb` service which
- Starts up after the main database
- With setup script mounted
- Connects to cluster and runs the mounted script -> creates the initial database (name hardcoded in script)

See
- https://stackoverflow.com/questions/45884185/cockroachdb-docker-compose-script-with-sql-commands


# PostgreSQL

Not much to say, very simple compose file. Postgres is kind enough to create a user, password and database given 
environment variables (unlike cockroachdb setup which is a bit more involved).
Couldn't be easier.

# Database + Kafka transaction testing
Start up the app, then use `app.http` to create customers, also has endpoint to fetch all customers.

When Customer is created the following happens
- Data saved to the database
- Customer Kafka event is published
- Activity Kafka event is published
  - This publish event will fail randomly (rand(2) == 0 , kind of thing)
- One consumer for each topic consumes the events and prints at INFO level


# Kafka

## Transactions

- https://docs.spring.io/spring-kafka/reference/kafka/transactions.html
- https://www.confluent.io/blog/transactions-apache-kafka/
- https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/
- https://cwiki.apache.org/confluence/display/KAFKA/KIP-447%3A+Producer+scalability+for+exactly+once+semantics
- https://docs.google.com/document/d/1LhzHGeX7_Lay4xvrEXxfciuDWATjpUXQhrEIkph9qRE/edit?tab=t.0#heading=h.beexgkt7kkor
- https://raphaeldelio.medium.com/chaining-kafka-and-database-transactions-with-spring-boot-an-in-depth-look-2a7e0e4fe57c
  - Pretty good explainer

### Config
Ensure that the Kafka producer factory is created in such a way that it has transactional capabilities.
```yaml
spring:
    kafka:
        producer:
          # This enables the kafka transaction manager stuff for the producer
          transaction-id-prefix: kafka-tx-

        consumer:
          properties:
            isolation.level: read_committed
```
Now there are two transaction managers configured in the application, this means we need to be specific of which one is in used
- @Transactional("kafkaTransactionManager") for Kafka Consumers
- @Transactional("transactionManager") for when database transaction manager is needed

If we don't do this we get this error since it doesn't know which one to use
```
A component required a single bean, but 2 were found:
 	- transactionManager: defined by method 'transactionManager' in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]
 	- kafkaTransactionManager: defined by method 'kafkaTransactionManager' in class path resource [org/springframework/boot/autoconfigure/kafka/KafkaAutoConfiguration.class]

```

### EOSMode.V2 
Spring for Apache Kafka version 3.0 and later only supports EOSMode.V2 , fetch-offset-request fencing (2.5+ brokers).  So requires Brokers which are on version 2.5 or later.


### Rollback notes
Found this "It is important to know that Spring rollbacks are only on unchecked exceptions by default. To rollback checked exceptions, we need to specify the rollbackFor on the @Transactional annotation"

From https://docs.spring.io/spring-kafka/reference/kafka/exactly-once.html
```
With mode V2, it is not necessary to have a producer for each group.id/topic/partition because consumer metadata is sent along with the offsets to the transaction and the broker can determine if the producer is fenced using that information instead.

Refer to KIP-447 for more information.

V2 was previously BETA; the EOSMode has been changed to align the framework with KIP-732.
```