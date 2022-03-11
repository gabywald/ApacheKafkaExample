package gabywald.kafka.formation;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Gabriel Chandesris (20180621)
 */
public class KafkaExerciseConsumer {

  private static final Logger log = LoggerFactory.getLogger(KafkaExerciseConsumer.class);
  private static Scanner in;
  private static boolean stop = false;

  public static void main(String[] argv) {
    if (argv.length != 2) {
      System.err.printf("Usage: %s <topicName> <groupId>\n", Consumer.class.getSimpleName());
      System.exit(-1);
    }
    KafkaExerciseConsumer.in = new Scanner(System.in);
    String topicName = argv[0];
    String groupId = argv[1];
    ConsumerThread consumerRunnable = new ConsumerThread(topicName, groupId);
    consumerRunnable.start();
    String line = "";
    while (!line.equals("exit")) {
      line = in.next();
    }
    consumerRunnable.getKafkaConsumer().wakeup();
    System.out.println("Stopping consumer .....");
    try {
      consumerRunnable.join();
    } catch (InterruptedException e) {
      // e.printStackTrace();
      KafkaExerciseConsumer.log.error("consumerRunnable.join(); fail ! (InterruptedException)");
    }
  }

  private static class ConsumerThread extends Thread {

    private String topicName;
    private String groupId;
    private KafkaConsumer<String, String> kafkaConsumer;

    public ConsumerThread(String topicName, String groupId) {
      this.topicName = topicName;
      this.groupId = groupId;
    }

    public void run() {
      Properties configProperties = new Properties();
      configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
      configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
      configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
      configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
      // ***** Figure out where to start processing messages from
      this.kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
      this.kafkaConsumer.subscribe(Arrays.asList(this.topicName));
      // ***** Start processing messages
      try {
        while (true) {
          ConsumerRecords<String, String> records = this.kafkaConsumer.poll(100);
          for (ConsumerRecord<String, String> record : records) {
            KafkaExerciseConsumer.log.info(record.value());
          }
        }
      } catch (WakeupException ex) {
        KafkaExerciseConsumer.log.error("Exception caught {" + ex.getMessage() + "}");
      } finally {
        this.kafkaConsumer.close();
        KafkaExerciseConsumer.log.info("After closing KafkaConsumer");
      }
    }

    public KafkaConsumer<String, String> getKafkaConsumer() {
      return this.kafkaConsumer;
    }
  }
}
