package gabywald.kafka.formation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Reducer;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.Options;

import gabywald.kafka.formation.gtc.example.model.GTCModelWrapper;
import gabywald.kafka.formation.gtc.example.model.GTCTable;

/**
 * 
 * see https://kafka.apache.org/documentation/streams/ 
 * see https://kafka.apache.org/11/documentation/streams/tutorial 
 * @author Gabriel Chandesris (20180709)
 */
public class KafkaStream {

	public static void main(String[] args) {

		//		if (args.length != 2) {
		//			System.out.println("Usage : KafkaStream <kafka-hosts> <zk-hosts>");
		//			System.exit(1);
		//		}

		String kafka	= "localhost:9092"; // args[0];
		// String zk		= args[1];

		//		Properties streamsConfiguration = new Properties();
		//		// Donne un nom à l'application. Toutes les instances de cette application pourront se partager les partitions 
		//		// de mêmes topics grâce à cet identifiant.
		//		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "GTC");
		//		// Broker Kafka
		//		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafka);
		//		// Noeud Zookeeper
		//		// streamsConfiguration.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, zk);

		Properties config = new Properties();
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, 			"GTC-application");
		config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, 			kafka);
		config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, 	Serdes.String().getClass());
		config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, 	Serdes.String().getClass());
		
		config.put(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG, CustomRocksDBConfig.class);

		// deprecated // KStreamBuilder builderdeprecated = new KStreamBuilder();

		StreamsBuilder builder = new StreamsBuilder();

		KStream<String, String> mainStream		= builder.stream( "GTC" );
		KStream<String, String> gamesStream		= builder.stream( "games" );
		KStream<String, String> tablesStream	= builder.stream( "tables" );
		KStream<String, String> gmsStream		= builder.stream( "gms" );
		
		KTable<String, String> tableCounts = mainStream
				// .flatMapValues(value -> Arrays.asList( (value.contains("createtable"))?"true":"false") )
				.flatMapValues( value -> {
					List<String> toReturn = new ArrayList<String>();
					if (value.contains(GTCTable.TABLE_JSON_CREATE)) {
						// JSONObject jsonObj		= new JSONObject( value );
						// JSONObject tableAsJSON	= jsonObj.get( GTCTable.TABLE_JSON_CREATE ).getObject();
						// ...
						
						GTCTable gtct = GTCModelWrapper.reloadTableFrom( value );
						
						if (gtct != null) 
							{ toReturn.add( gtct.toString() ); }
						
						System.out.println( "***** {" + value + "} *****" );
						System.out.println( "+++++ {" + gtct + "} +++++" );
					} // else { toReturn.add( "false" ); }
					return toReturn;
				} )
				.groupBy((key, value) -> value)
				.reduce(new Reducer<String>() {
					@Override
					public String apply(String value1, String value2) {
						System.out.println( "///// {" + value1 + "/" + value2 + "} /////" );
						return value2;
					}
				});
				// .count();
		// .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
		// tableCounts.toStream().to("CreateTableCounts", Produced.with(Serdes.String(), Serdes.Long()));
		
		// ***** Second argument to avoid errors from maintaining some data and facilitate conversion *****
		tableCounts.toStream().to("CreateTable", Produced.with(Serdes.String(), Serdes.String()));

		//		// Initialisation des ser/déserialiseurs pour lire et écrire dans les topics
		//		Serde<GTCGame> gameSerde		= Serdes.serdeFrom(new JSONSerializer<>(), new JSONDeSerializer<>(GTCGame.class));
		//		Serde<GTCGameMaster> gmSerde	= Serdes.serdeFrom(new JSONSerializer<>(), new JSONDeSerializer<>(GTCGameMaster.class));
		//		Serde<GTCPlayer> playerSerde	= Serdes.serdeFrom(new JSONSerializer<>(), new JSONDeSerializer<>(GTCPlayer.class));
		//		Serde<GTCTable> tableSerde		= Serdes.serdeFrom(new JSONSerializer<>(), new JSONDeSerializer<>(GTCTable.class));

		//		// Création d'un KStream (flux) à partir du topic "games"
		//		KStream<String, GTCGame> achats = builder.stream(Serdes.String(), gameSerde, "games");
		//		
		//		// Création d'une KTable (table) à partir du topic "tables"
		//		KTable<String, GTCTable> referentiel = builder.table(Serdes.String(), tableSerde, "tables");

		// TODO ...

		// Enfin, on démarre l'application
		KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.start();
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
