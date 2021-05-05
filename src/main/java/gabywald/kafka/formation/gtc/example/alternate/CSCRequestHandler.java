package gabywald.kafka.formation.gtc.example.alternate;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Replace the Switch statement in GTCConsumer. 
 * @author Gabriel Chandesris (20180824)
 */
public class CSCRequestHandler {
	
	private Map<String, ConsumerSwitchCommand> commandMap = 
			new HashMap<String, ConsumerSwitchCommand>();
	
	public void put(String action, ConsumerSwitchCommand cscinstance) {
		this.commandMap.put(action, cscinstance);
	}
	
	public String handleRequest(String action, JsonNode jsonnode) throws CSCException {
		if ( ! this.commandMap.containsKey(action)) 
			{ throw new CSCException( "Key {" + action + "} unknown / not defined !"); }
		ConsumerSwitchCommand command = this.commandMap.get(action);
		return command.execute( jsonnode );
	}
	
	@SuppressWarnings("serial")
	public class CSCException extends Exception {
		public CSCException(String message) {
			super(message);
		}
	}
}
