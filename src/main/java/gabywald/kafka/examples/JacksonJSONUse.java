package gabywald.kafka.examples;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gabywald.kafka.formation.gtc.example.model.GTCGame;
import gabywald.kafka.formation.gtc.example.model.GTCGameMaster;
import gabywald.kafka.formation.gtc.example.model.GTCPlayer;
import gabywald.kafka.formation.gtc.example.model.GTCTable;

public class JacksonJSONUse {
	
	private static final Logger log = LoggerFactory.getLogger( JacksonJSONUse.class );

	public static void main(String[] args) {
		
		String strJSONData = "{\n" + 
				"  \"name\": \"David\",\n" + 
				"  \"role\": \"Manager\",\n" + 
				"  \"city\": \"Los Angeles\"\n" + 
				"}";
		
		// https://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial -- 
		ObjectMapper objectMapper = new ObjectMapper();

		//create JsonNode
		try {
			JsonNode rootNode = objectMapper.readTree( strJSONData );
			
			Iterator<String> entryNames = rootNode.fieldNames();
			while (entryNames.hasNext()) {
				String entryName = entryNames.next();
				System.out.println("\t" + entryName + " => " + rootNode.get( entryName ));
			}
			
//			for (JsonNode node : rootNode) 
//				{ System.out.println(node.asText()); }
			
			Random rand			= new Random();
			String[] tables		= { "table1", "table2", "table3" };
			int numberOfPlayers	= 30;
			for (int i = 0 ; i < numberOfPlayers ; i++) {
				JacksonJSONUse.log.info( "\t" + "player" + i );
				/*JsonNode*/ObjectNode jsonObj	= objectMapper.createObjectNode(); // new JsonNode();
				/*JsonNode*/ObjectNode table	= jsonObj.putObject( GTCPlayer.PLAYER_JSON_CREATE ); // objectMapper.createObjectNode();
				// jsonObj.put(GTCPlayer.PLAYER_JSON_CREATE, table);
				table.put(GTCPlayer.PLAYER_JSON_NAME, "player"+i);
				table.put(GTCPlayer.PLAYER_JSON_GAME, tables[ rand.nextInt(tables.length) ]);
				table.put(GTCPlayer.PLAYER_JSON_PHONE, "00 00 00 00 00");
				
				JacksonJSONUse.log.info( objectMapper.writeValueAsString(jsonObj) );
				
				// objectMapper.writeValue(System.out, jsonObj);
				
			} // END "for (int i = 0 ; i < numberOfPlayers ; i++)"
			
			/* ***** */
			
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			
			GTCPlayer gtcpToJSON		= new GTCPlayer("toto", "00 00 00 00 00", "table42" );
			StringWriter strgtcp		= new StringWriter();
			objectMapper.writeValue(strgtcp, gtcpToJSON);
			System.out.println("GTCPlayer JSON is: \n" + strgtcp);
			
			GTCTable gtctToJSON			= new GTCTable("table42", "gmName", 2, 6);
			StringWriter strgtct		= new StringWriter();
			objectMapper.writeValue(strgtct, gtctToJSON);
			System.out.println("GTCTable JSON is: \n" + strgtct);
			
			GTCGameMaster gtcgmToJSON	= new GTCGameMaster("Kevin", tables);
			StringWriter strgtcgm		= new StringWriter();
			objectMapper.writeValue(strgtcgm, gtcgmToJSON);
			System.out.println("GTCGameMaster JSON is: \n" + strgtcgm);
			
			GTCGame gtcgToJSON			= new GTCGame( "table42", "description de table42" );
			StringWriter strgtcg		= new StringWriter();
			objectMapper.writeValue(strgtcg, gtcgToJSON);
			System.out.println("GTCGame JSON is: \n" + strgtcg);
			
			System.out.println();
			System.out.println();
			System.out.println( strgtcgm.toString() );
			
			/* ***** */
			ObjectNode jsonObjGMContainer	= objectMapper.createObjectNode();
			// ObjectNode jsonObjGMtmp			= 
			jsonObjGMContainer.putObject( GTCGameMaster.GAMEMASTER_JSON_CREATE );
			ObjectNode jsonObjGM			= (ObjectNode) objectMapper.readTree( strgtcgm.toString() );
			( (ObjectNode) jsonObjGMContainer.path( GTCGameMaster.GAMEMASTER_JSON_CREATE ) ).setAll( jsonObjGM );
			
			StringWriter strgtcgmCont		= new StringWriter();
			objectMapper.writeValue(strgtcgmCont, jsonObjGMContainer);
			System.out.println("jsonObjGMContainer JSON is: \n" + strgtcgmCont);
			
			System.out.println( jsonObjGM.get( GTCGameMaster.GAMEMASTER_JSON_GAMES ) );
			
			String nameGM		= jsonObjGM.get( GTCGameMaster.GAMEMASTER_JSON_NAME ).asText();
			ArrayNode array 	= (ArrayNode) jsonObjGM.get( GTCGameMaster.GAMEMASTER_JSON_GAMES );
			
			List<String> games	= new ArrayList<String>();
			Iterator<JsonNode> iteOnElts = array.elements();
			while (iteOnElts.hasNext()) 
				{ games.add( iteOnElts.next().asText() ); }
			
			System.out.println( nameGM );
			games.stream().map( n -> "\t" + n ).forEach( System.out::println );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
