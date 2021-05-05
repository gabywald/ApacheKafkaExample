
@echo off

set RACINE_INSTALL_KAFKA
REM set RACINE_INSTALL_KAFKA=install\kafka_2.11-1.1.0\
REM set "RACINE_INSTALL_KAFKA=Documents\ApacheKafka\install\kafka_2.11-1.1.0\"
REM echo %RACINE_INSTALL_KAFKA%

echo "Lancement serveur ZooKeeper"
REM bin/zookeeper-server-start.sh config/zookeeper.properties
:: install\kafka_2.11-1.1.0\bin\windows\zookeeper-server-start.bat install\kafka_2.11-1.1.0\config\zookeeper.properties
REM echo %RACINE_INSTALL_KAFKA%bin\windows\zookeeper-server-start.bat %RACINE_INSTALL_KAFKA%config\zookeeper.properties
REM call %RACINE_INSTALL_KAFKA%bin\windows\zookeeper-server-start.bat %RACINE_INSTALL_KAFKA%config\zookeeper.properties
echo bin\windows\zookeeper-server-start.bat config\zookeeper.properties
start bin\windows\zookeeper-server-start.bat config\zookeeper.properties

pause

echo "Lancement serveur Apache Kafka"
REM bin/kafka-server-start.sh config/server.properties
echo bin\windows\kafka-server-start.bat config\server.properties
start bin\windows\kafka-server-start.bat config\server.properties

echo "END"

REM bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic javaworld
REM bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic javaworld --from-beginning
REM bin/kafka-console-producer.sh --broker-list localhost:9092 --topic javaworld

REM bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic javaworld
REM bin\windows\kafka-console-consumer.bat --zookeeper localhost:2181 --topic javaworld --from-beginning
REM bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic javaworld

REM bin\windows\zookeeper-server-stop.bat
REM bin\windows\kafka-server-stop.bat

REM bin\windows\kafka-topics.bat –describe –zookeeper localhost:2181
REM bin\windows\kafka-console-producer.bat –broker-list localhost:9092 --topic <sujet>
REM bin\windows\kafka-console-consumer.bat --zookeeper localhost:2181 --from-beginning --topic <sujet>

REM bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic <sujet>

REM bin\windows\kafka-topics.bat --zookeeper localhost:2181 --delete --topic GTC
REM bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic GTC --from-beginning
REM bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic GTC


