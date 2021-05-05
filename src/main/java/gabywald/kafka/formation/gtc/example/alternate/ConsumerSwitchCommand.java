package gabywald.kafka.formation.gtc.example.alternate;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Command Interface to replace Switch cases... 
 * @author Gabriel Chandesris (20180824)
 */
public interface ConsumerSwitchCommand {
	/** 
	 * The function to execute. 
	 * @param jsonnode (JsonNode) the JSON node which represent item to be instantiated. 
	 * @return (String) message. 
	 */
	public String execute(JsonNode jsonnode);
}
