package gabywald.kafka.formation.gtc.example.alternate;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import gabywald.kafka.formation.gtc.example.model.GTCModelWrapper;
import gabywald.kafka.formation.gtc.example.model.GTCTable;

/**
 * ConsumerSwitchCommand implementation to create GTCTable's instances. 
 * @author Gabriel Chandesris (20180824)
 */
public class CSCCreateTable implements ConsumerSwitchCommand {
	
	private static final Logger log = Logger.getLogger( CSCCreateTable.class );

	@Override
	public String execute(JsonNode jsonnode) {
		CSCCreateTable.log.info( "Create table !" );
		String nameG	= jsonnode.get( GTCTable.TABLE_JSON_GAME ).asText();
		String gmName	= jsonnode.get( GTCTable.TABLE_JSON_GAMEMASTER ).asText();
		int min			= jsonnode.get( GTCTable.TABLE_JSON_MINIMUM ).asInt();
		int max			= jsonnode.get( GTCTable.TABLE_JSON_MAXIMUM ).asInt();
		
		GTCTable gtct = GTCModelWrapper.getInstance4Consumer().createTable(nameG, gmName, min, max);
		
		return "\t created table: " + gtct;
	}

}
