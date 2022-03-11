package gabywald.kafka.formation.gtc.example;

import java.util.LinkedList;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gabywald.kafka.formation.gtc.example.GTCPropertiesLoader.GTCConfigProperties;
import gabywald.kafka.formation.gtc.example.model.GTCDataLoader;

/**
 * Producer-side Thread / Runnable Class ; sent data to Kafka Server. 
 * @author Gabriel Chandesris (20180625)
 */
public class GTCProducer extends GTCParent implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(GTCProducer.class);
  private LinkedList<String> data2sent = new LinkedList<String>();
  private Properties configProperties = new Properties();

  public static GTCProducer getProducerFromConfig() {
    GTCPropertiesLoader pl = new GTCPropertiesLoader();
    pl.loadProps(GTCDataLoader.PATH_BASE + "gtcProducer.properties");
    String[] parameters = new String[4];
    parameters[0] = pl.get(GTCConfigProperties.TOPIC_NAME.getName());
    parameters[1] = pl.get(GTCConfigProperties.BOOTSRAP_SERVERS.getName());
    parameters[2] = pl.get(GTCConfigProperties.KEY_SERIALIZER.getName());
    parameters[3] = pl.get(GTCConfigProperties.VALUE_SERIALIZER.getName());
    return new GTCProducer(parameters[0], parameters[1], parameters[2], parameters[3]);
    // return new GTCProducer(	null, null, null, null );
  }

  public GTCProducer(String topicName, String bootStrapServer, String keySerializer, String valueSerializer) {
    // ***** Configure the Producer
    this.configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
    this.configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
    this.configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
    this.configProperties.put(GTCConfigProperties.TOPIC_NAME.toString(), topicName);
    this.topicName = topicName;
  }

  public void sendData(String data) {
    this.data2sent.add(data);
    // this.show( "SEND: //" + data + "//"   );
  }

  public void show(String msg) {
    if (this.frame != null) {
      this.frame.addText(msg + "\n");
    } else {
      GTCProducer.log.info("this.frame is null !");
    }
    GTCProducer.log.info(msg);
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
    GTCProducer.log.info("GTCProducer STARTING !");
    Producer<String, String> producer = new KafkaProducer<String, String>(this.configProperties);
    while (this.isRunning) {
      GTCProducer.log.info("this.data2sent ...");
      while (this.data2sent.isEmpty()) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          GTCProducer.log.warn("InterruptedException");
        }
      } // END "while (this.data2sent.isEmpty()) "
      GTCProducer.log.info("this.data2sent: (" + this.data2sent.size() + ")");
      String nextSend = this.data2sent.removeFirst();
      GTCProducer.log.info("nextSend------: (" + nextSend.length() + ")");
      if (!nextSend.equals("exit")) {
        ProducerRecord<String, String> rec = new ProducerRecord<String, String>(this.topicName, nextSend);
        producer.send(rec);
        this.show("SENT //" + nextSend + "//");
      } // END "while ( ! line.equals("exit") )"
      else {
        this.isRunning = false;
        GTCProducer.log.info(nextSend);
      }
    } // END "while (this.isRunning)"
    producer.close();
    GTCProducer.log.info("GTCProducer END !");
  }
}
