package gabywald.kafka.formation.gtc.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import gabywald.kafka.formation.gtc.example.GTCPropertiesLoader.GTCConfigProperties;
import gabywald.kafka.formation.gtc.example.model.GTCDataLoader;
import gabywald.kafka.formation.gtc.example.model.GTCGame;
import gabywald.kafka.formation.gtc.example.model.GTCGameMaster;
import gabywald.kafka.formation.gtc.example.model.GTCModelWrapper;
import gabywald.kafka.formation.gtc.example.model.GTCPlayer;
import gabywald.kafka.formation.gtc.example.model.GTCTable;

/**
 * Consumer-side Thread / Runnable Class ; get data from Kafka Server. 
 * @author Gabriel Chandesris (20180625 + 20210401)
 * */
public class GTCConsumer extends GTCParent implements Runnable {

  private static final Logger log = Logger.getLogger(GTCConsumer.class);
  private Properties configProperties = new Properties();
  private KafkaConsumer<String, String> kafkaConsumer;

  public static GTCConsumer getConsumerFromConfig() {
    GTCPropertiesLoader pl = new GTCPropertiesLoader();
    pl.loadProps(GTCDataLoader.PATH_BASE + "gtcConsumer.properties");
    String[] parameters = new String[6];
    parameters[0] = pl.get(GTCConfigProperties.TOPIC_NAME.getName());
    parameters[1] = pl.get(GTCConfigProperties.BOOTSRAP_SERVERS.getName());
    parameters[2] = pl.get(GTCConfigProperties.KEY_DESERIALIZER.getName());
    parameters[3] = pl.get(GTCConfigProperties.VALUE_DESERIALIZER.getName());
    parameters[4] = pl.get(GTCConfigProperties.GROUP_ID.getName());
    parameters[5] = pl.get(GTCConfigProperties.CLIENT_ID.getName());
    return new GTCConsumer(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5]);
    // return new GTCConsumer(	null, null, null, null, null, null);
  }

  /**
   * 
   * @param topicName
   * @param bootStrapServer
   * @param keyDeSerializer
   * @param valueDeSerializer
   * @param groupID
   * @param clientID
   */
  public GTCConsumer(String topicName, String bootStrapServer, String keyDeSerializer, String valueDeSerializer, String groupID, String clientID) {
    this.configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
    this.configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
    this.configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
    this.configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
    this.configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientID);
    // this.configProperties.put(???, topicName);
    this.topicName = topicName;
  }

  public void show(String msg) {
    if (this.frame != null) {
      this.frame.addText(msg + "\n");
    } else {
      GTCConsumer.log.info("this.frame is null !");
    }
    GTCConsumer.log.info(msg);
    //		try { Thread.sleep(1000); }
    //		catch (InterruptedException e) { GTCProducer.log.warn( "InterruptedException" ); }
  }

  public void start() {
    this.isRunning = true;
    Thread gtcpTHR = new Thread(this);
    gtcpTHR.start();
  }

  @Override
  public void run() {
    // ***** Figure out where to start processing messages from
    this.kafkaConsumer = new KafkaConsumer<String, String>(this.configProperties);
    this.kafkaConsumer.subscribe(Arrays.asList(this.topicName));
    // ***** Start processing messages
    try {
      ObjectMapper om = GTCModelWrapper.getObjectMapper();
      while (this.isRunning) {
        ConsumerRecords<String, String> records = this.kafkaConsumer.poll(100);
        for (ConsumerRecord<String, String> record : records) {
          // System.out.println(record.value());
          this.frame.addText("Received: //" + record.value() + "// \n");
          String msg = null;
          try {
            JsonNode jsonObj = om.readTree(record.value());
            Iterator<String> entryNames = jsonObj.fieldNames();
            while (entryNames.hasNext()) {
              String key = entryNames.next();
              switch (key) {
                case GTCGame.GAME_JSON_CREATE:
                  GTCConsumer.log.info("Create game !");
                  JsonNode game = jsonObj.get(key);
                  String nameGa = game.get(GTCGame.GAME_JSON_NAME).asText();
                  String descGa = game.get(GTCGame.GAME_JSON_DESCRIPTION).asText();
                  GTCGame gtcg = GTCModelWrapper.getInstance4Consumer().createGame(nameGa, descGa);
                  msg = "\t created game: " + gtcg;
                  break;
                case GTCGameMaster.GAMEMASTER_JSON_CREATE:
                  GTCConsumer.log.info("Create gamemaster !");
                  JsonNode gamemaster = jsonObj.get(key);
                  String nameGM = gamemaster.get(GTCGameMaster.GAMEMASTER_JSON_NAME).asText();
                  ArrayNode array = (ArrayNode) gamemaster.get(GTCGameMaster.GAMEMASTER_JSON_GAMES);
                  List<String> games = new ArrayList<String>();
                  Iterator<JsonNode> iteOnElts = array.elements();
                  while (iteOnElts.hasNext()) {
                    games.add(iteOnElts.next().asText());
                  } ;
                  GTCGameMaster gtcgm = GTCModelWrapper.getInstance4Consumer().createGameMaster(nameGM, games);
                  msg = "\t created gamemaster: " + gtcgm;
                  break;
                case GTCTable.TABLE_JSON_CREATE:
                  GTCConsumer.log.info("Create table !");
                  JsonNode table = jsonObj.get(key);
                  String nameG = table.get(GTCTable.TABLE_JSON_GAME).asText();
                  String gmName = table.get(GTCTable.TABLE_JSON_GAMEMASTER).asText();
                  int min = table.get(GTCTable.TABLE_JSON_MINIMUM).asInt();
                  int max = table.get(GTCTable.TABLE_JSON_MAXIMUM).asInt();
                  GTCTable gtct = GTCModelWrapper.getInstance4Consumer().createTable(nameG, gmName, min, max);
                  msg = "\t created table: " + gtct;
                  break;
                case GTCPlayer.PLAYER_JSON_CREATE:
                  GTCConsumer.log.info("Create Player !");
                  JsonNode player = jsonObj.get(key);
                  String nameP = player.get(GTCPlayer.PLAYER_JSON_NAME).asText();
                  String phone = player.get(GTCPlayer.PLAYER_JSON_PHONE).asText();
                  String gameName = player.get(GTCPlayer.PLAYER_JSON_GAME).asText();
                  GTCConsumer.log.info("nameP: " + nameP);
                  GTCConsumer.log.info("phone: " + phone);
                  GTCConsumer.log.info("gameName: " + gameName);
                  GTCPlayer gtcp = GTCModelWrapper.getInstance4Consumer().createPlayer(nameP, phone, gameName);
                  msg = "\t created player: " + gtcp;
                  break;
                default:
                  GTCConsumer.log.warn("Unknown key : {" + key + "}");
              } // END "switch( key )"
              if (msg != null) {
                this.show(msg);
              } // END "if (msg != null)"
            }
          } catch (IOException e) {
            // e.printStackTrace();
            GTCConsumer.log.error("IOException: {" + e.getMessage() + "}");
          } finally {
            // Nothing
          }
        } // END "for (ConsumerRecord<String, String> record : records)"
      }
    } catch (WakeupException ex) {
      String msg = "Exception caught {" + ex.getMessage() + "}";
      this.show(msg);
    } finally {
      this.kafkaConsumer.close();
      String msg = "After closing KafkaConsumer";
      this.show(msg);
      //			System.exit(0);
      //			try { Thread.sleep(100000); }
      //			catch (InterruptedException e) { GTCConsumer.log.warn( "InterruptedException" ); }
    }
  }
}
