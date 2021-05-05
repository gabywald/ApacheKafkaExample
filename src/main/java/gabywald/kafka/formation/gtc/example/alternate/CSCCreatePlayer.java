package gabywald.kafka.formation.gtc.example.alternate;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import gabywald.kafka.formation.gtc.example.model.GTCModelWrapper;
import gabywald.kafka.formation.gtc.example.model.GTCPlayer;

/**
 * ConsumerSwitchCommand implementation to create GTCPlayer's instances. 
 * @author Gabriel Chandesris (20180824)
 */
public class CSCCreatePlayer implements ConsumerSwitchCommand {
	
	private static final Logger log = Logger.getLogger( CSCCreatePlayer.class );

	@Override
	public String execute(JsonNode jsonnode) {
		CSCCreatePlayer.log.info( "Create Player !" );
		String nameP	= jsonnode.get( GTCPlayer.PLAYER_JSON_NAME ).asText();
		String phone	= jsonnode.get( GTCPlayer.PLAYER_JSON_PHONE ).asText();
		String gameName	= jsonnode.get( GTCPlayer.PLAYER_JSON_GAME ).asText();
		
		CSCCreatePlayer.log.info( "nameP: " + nameP );
		CSCCreatePlayer.log.info( "phone: " + phone );
		CSCCreatePlayer.log.info( "gameName: " + gameName );
		
		GTCPlayer gtcp	= GTCModelWrapper.getInstance4Consumer().createPlayer(nameP, phone, gameName);
		
		return "\t created player: " + gtcp;
	}

}
