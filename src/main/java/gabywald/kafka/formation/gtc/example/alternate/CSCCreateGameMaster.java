package gabywald.kafka.formation.gtc.example.alternate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import gabywald.kafka.formation.gtc.example.model.GTCGameMaster;
import gabywald.kafka.formation.gtc.example.model.GTCModelWrapper;

/**
 * ConsumerSwitchCommand implementation to create GTCGameMaster's instances. 
 * @author Gabriel Chandesris (20180824)
 */
public class CSCCreateGameMaster implements ConsumerSwitchCommand {
	
	private static final Logger log = Logger.getLogger( CSCCreateGameMaster.class );

	@Override
	public String execute(JsonNode jsonnode) {
		CSCCreateGameMaster.log.info( "Create gamemaster !" );
		String nameGM	= jsonnode.get( GTCGameMaster.GAMEMASTER_JSON_NAME ).asText();
		ArrayNode array = (ArrayNode) jsonnode.get( GTCGameMaster.GAMEMASTER_JSON_GAMES );
		
		List<String> games = new ArrayList<String>();
		Iterator<JsonNode> iteOnElts = array.elements();
		while (iteOnElts.hasNext()) 
			{ games.add( iteOnElts.next().asText() ); };
		
		GTCGameMaster gtcgm = GTCModelWrapper.getInstance4Consumer().createGameMaster(nameGM, games);
		
		return "\t created gamemaster: " + gtcgm;
	}

}
