package gabywald.kafka.formation;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.Options;

/**
 * 
 * See https://lenadroid.github.io/posts/distributed-data-streaming-action.html -- 
 * See https://gist.github.com/miguno/f64e2cc0b764db34908b6e754ff04c94 -- 
 * @author gchandesris
 * @deprecated for notes purposes... 
 */
public class WordCountApplication {
	public static void main(final String[] args) throws Exception {
		Properties config = new Properties();
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
		config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // "kafka-broker1:9092");
		config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		config.put(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG, CustomRocksDBConfig.class);

		KStreamBuilder builder = new KStreamBuilder();
		KStream<String, String> textLines = builder.stream("TextLinesTopic");
		KTable<String, Long> wordCounts = textLines
				.flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
				.groupBy((key, word) -> word)
				.count("Counts");
		wordCounts.to(Serdes.String(), Serdes.Long(), "WordsWithCountsTopic");

		KafkaStreams streams = new KafkaStreams(builder, config);
		streams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	}

	public static class CustomRocksDBConfig implements RocksDBConfigSetter {

		@Override
		public void setConfig(final String storeName, final Options options, final Map<String, Object> configs) {
			// See #1 below.
			BlockBasedTableConfig tableConfig = new org.rocksdb.BlockBasedTableConfig();
			tableConfig.setBlockCacheSize(16 * 1024 * 1024L);
			// See #2 below.
			tableConfig.setBlockSize(16 * 1024L);
			// See #3 below.
			tableConfig.setCacheIndexAndFilterBlocks(true);
			options.setTableFormatConfig(tableConfig);
			// See #4 below.
			options.setMaxWriteBufferNumber(2);
		}

	}

}
