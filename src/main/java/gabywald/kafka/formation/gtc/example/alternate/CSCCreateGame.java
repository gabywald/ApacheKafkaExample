package gabywald.kafka.formation.gtc.example.alternate;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import gabywald.kafka.formation.gtc.example.model.GTCGame;
import gabywald.kafka.formation.gtc.example.model.GTCModelWrapper;

/**
 * ConsumerSwitchCommand implementation to create GTCGame's instances. 
 * @author Gabriel Chandesris (20180824)
 */
public class CSCCreateGame implements ConsumerSwitchCommand {
	
	private static final Logger log = Logger.getLogger( CSCCreateGame.class );

	@Override
	public String execute(JsonNode jsonnode) {
		CSCCreateGame.log.info( "Create game !" );
		String nameGa	= jsonnode.get( GTCGame.GAME_JSON_NAME ).asText();
		String descGa	= jsonnode.get( GTCGame.GAME_JSON_DESCRIPTION ).asText();
		
		GTCGame gtcg	= GTCModelWrapper.getInstance4Consumer().createGame(nameGa, descGa);
		
		return "\t created game: " + gtcg;
	}

}
