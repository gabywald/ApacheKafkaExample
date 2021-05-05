#!/bin/bash

## cd installation directory of Apache Kafka

nohup ./bin/zookeeper-server-start.sh config/zookeeper.properties &
nohup ./bin/kafka-server-start.sh config/server.properties &
