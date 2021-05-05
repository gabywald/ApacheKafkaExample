#!/bin/bash

## cd installation directory of Apache Kafka

nohup ./bin/kafka-server-stop.sh &
nohup ./bin/zookeeper-server-stop.sh & 
