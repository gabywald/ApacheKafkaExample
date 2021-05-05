package gabywald.kafka.formation;

import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * 
 * @author Gabriel Chandesris (20180621)
 */
public class KafkaExerciseProducer {

	private static Scanner in;
	public static void main(String[] argv) { // throws Exception 
		if (argv.length != 1) {
			System.err.println("Please specify 1 parameters (topic name) ");
			System.exit( 0 ); // System.exit(-1);
		} // END "if (argv.length != 1)"
		String topicName			= argv[0];
		KafkaExerciseProducer.in	= new Scanner(System.in);
		System.out.println("Enter message(type exit to quit)");

		// ***** Configure the Producer
		Properties configProperties = new Properties();
		configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
							"localhost:9092");
		configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
							"org.apache.kafka.common.serialization.ByteArraySerializer");
		configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
							"org.apache.kafka.common.serialization.StringSerializer");

		// ***** org.apache.kafka.clients.producer.Producer
		Producer<String, String> producer = new KafkaProducer<String, String>(configProperties);
		String line = KafkaExerciseProducer.in.nextLine();
		while ( ! line.equals("exit") ) {
			ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topicName, line);
			producer.send(rec);
			line = KafkaExerciseProducer.in.nextLine();
		} // END "while ( ! line.equals("exit") )"
		KafkaExerciseProducer.in.close();
		producer.close();
	}

}
