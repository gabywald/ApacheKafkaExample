package gabywald.kafka.formation.gtc.example.alternate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gabywald.kafka.formation.gtc.example.GTCParent;
import gabywald.kafka.formation.gtc.example.GTCPropertiesLoader;
import gabywald.kafka.formation.gtc.example.GTCPropertiesLoader.GTCConfigProperties;
import gabywald.kafka.formation.gtc.example.alternate.CSCRequestHandler.CSCException;
import gabywald.kafka.formation.gtc.example.model.GTCDataLoader;
import gabywald.kafka.formation.gtc.example.model.GTCGame;
import gabywald.kafka.formation.gtc.example.model.GTCGameMaster;
import gabywald.kafka.formation.gtc.example.model.GTCModelWrapper;
import gabywald.kafka.formation.gtc.example.model.GTCPlayer;
import gabywald.kafka.formation.gtc.example.model.GTCTable;

/**
 * Consumer-side Thread / Runnable Class ; get data from Kafka Server. 
 * @author Gabriel Chandesris (20180625)
 * */
public class GTCCSCConsumer extends GTCParent implements Runnable {

  private static final Logger log = Logger.getLogger(GTCCSCConsumer.class);
  private Properties configProperties = new Properties();
  private KafkaConsumer<String, String> kafkaConsumer;

  public static GTCCSCConsumer getConsumerFromConfig() {
    GTCPropertiesLoader pl = new GTCPropertiesLoader();
    pl.loadProps(GTCDataLoader.PATH_BASE + "gtcConsumer.properties");
    String[] parameters = new String[6];
    parameters[0] = pl.get(GTCConfigProperties.TOPIC_NAME.getName());
    parameters[1] = pl.get(GTCConfigProperties.BOOTSRAP_SERVERS.getName());
    parameters[2] = pl.get(GTCConfigProperties.KEY_DESERIALIZER.getName());
    parameters[3] = pl.get(GTCConfigProperties.VALUE_DESERIALIZER.getName());
    parameters[4] = pl.get(GTCConfigProperties.GROUP_ID.getName());
    parameters[5] = pl.get(GTCConfigProperties.CLIENT_ID.getName());
    return new GTCCSCConsumer(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5]);
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
  public GTCCSCConsumer(String topicName, String bootStrapServer, String keyDeSerializer, String valueDeSerializer, String groupID, String clientID) {
    this.configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
    this.configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
    this.configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
    this.configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
    this.configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientID);
    // this.configProperties.put(	GTCConfigProperties.TOPIC_NAME.toString(), 		topicName);
    this.topicName = topicName;
  }

  public void show(String msg) {
    if (this.frame != null) {
      this.frame.addText(msg + "\n");
    } else {
      GTCCSCConsumer.log.info("this.frame is null !");
    }
    GTCCSCConsumer.log.info(msg);
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
          // ***** Instanciate alternatives here !
          CSCRequestHandler cscr = new CSCRequestHandler();
          cscr.put(GTCGame.GAME_JSON_CREATE, new CSCCreateGame());
          cscr.put(GTCGameMaster.GAMEMASTER_JSON_CREATE, new CSCCreateGameMaster());
          cscr.put(GTCTable.TABLE_JSON_CREATE, new CSCCreateTable());
          cscr.put(GTCPlayer.PLAYER_JSON_CREATE, new CSCCreatePlayer());
          try {
            JsonNode jsonObj = om.readTree(record.value());
            Iterator<String> entryNames = jsonObj.fieldNames();
            while (entryNames.hasNext()) {
              String key = entryNames.next();
              JsonNode jsonnode = jsonObj.get(key);
              // ***** Switch is done here !
              try {
                msg = cscr.handleRequest(key, jsonnode);
              } catch (CSCException e) {
                // e.printStackTrace();
                msg = e.getMessage();
              }
              if (msg != null) {
                this.show(msg);
              } // END "if (msg != null)"
            }
          } catch (IOException e) {
            // e.printStackTrace();
            GTCCSCConsumer.log.error("IOException: {" + e.getMessage() + "}");
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
