package gabywald.kafka.formation.gtc.example.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Creates and contains datas (Classes' instances) for Producer and / or Consumer. 
 * <br/><i>DP Singleton</i> "extended" / MultiTon
 * <br/>Specific instances for different "sides" of application. 
 * @author Gabriel Chandesris (20180627)
 */
public class GTCModelWrapper {
	
	private static final Logger log = Logger.getLogger( GTCModelWrapper.class );
	
	/** Jackson / JSON purposes. */
	private static final ObjectMapper objectMapper = new ObjectMapper();
	public static ObjectMapper getObjectMapper() 
		{ return GTCModelWrapper.objectMapper; }
	
	/** Game name, and table. */
	private Map<String, GTCTable> tables;
	/** Game name and game. */
	private Map<String, GTCGame> games;
	
	private Map<String, GTCGameMaster> masters;
	
	private List<GTCPlayer> players;
	
	private static GTCModelWrapper instance, instanceConsumer, instanceProducer;
	
	private GTCModelWrapper() {
		this.tables 	= new HashMap<String, GTCTable>();
		this.games		= new HashMap<String, GTCGame>();
		this.masters	= new HashMap<String, GTCGameMaster>();
		this.players	= new ArrayList<GTCPlayer>();
	}
	
	/**
	 * 
	 * @return (GTCModelWrapper)
	 * @deprecated Use Consumer / Producer 's instances. 
	 */
	public static GTCModelWrapper getInstance() {
		if (GTCModelWrapper.instance == null) 
			{ GTCModelWrapper.instance = new GTCModelWrapper(); }
		return GTCModelWrapper.instance;
	}
	
	public static GTCModelWrapper getInstance4Consumer() {
		if (GTCModelWrapper.instanceConsumer == null) 
			{ GTCModelWrapper.instanceConsumer = new GTCModelWrapper(); }
		return GTCModelWrapper.instanceConsumer;
	}
	
	public static GTCModelWrapper getInstance4Producer() {
		if (GTCModelWrapper.instanceProducer == null) 
			{ GTCModelWrapper.instanceProducer = new GTCModelWrapper(); }
		return GTCModelWrapper.instanceProducer;
	}
	
	/**
	 * If a  Game with this name already exists : return it, 
	 * otherwise add it in the list. 
	 * @param game (GTCGame)
	 * @return (GTCGame)
	 */
	public GTCGame addGame(GTCGame game) {
		if (this.games.containsKey( game.getName() )) 
			{ return this.games.get( game.getName() ); }
		this.games.put(game.getName(), game);
		return game;
	}
	
	/**
	 * If a GameMaster with this name already exists : return it, 
	 * otherwise add it in the list. 
	 * @param gm  (GTCGameMaster)
	 * @return (GTCGameMaster)
	 */
	public GTCGameMaster addGameMaster(GTCGameMaster gm) {
		if (this.masters.containsKey( gm.getName() )) 
			{ return this.masters.get( gm.getName() ); }
		this.masters.put(gm.getName(), gm);
		return gm;
	}
	
	/**
	 * If a Game with this name already exists : return it, 
	 * otherwise create it and add it in the list. 
	 * @param name (String)
	 * @param desc (String)
	 * @return (GTCGame)
	 */
	public GTCGame createGame(String name, String desc) {
		if (this.games.containsKey( name )) 
			{ return this.games.get( name ); }
		GTCGame toReturn = new GTCGame( name, desc );
		this.games.put(name, toReturn);
		return toReturn;
	}
	
	/**
	 * If a GameMaster with this name already exists : return it, 
	 * otherwise create it and add it in the list. 
	 * @param name (String)
	 * @param games (String[])
	 * @return (GTCGameMaster)
	 */
	public GTCGameMaster createGameMaster(String name, String[] games) {
		if (this.masters.containsKey( name )) 
			{ return this.masters.get( name ); }
		GTCGameMaster toReturn = new GTCGameMaster(name, games);
		this.masters.put(name, toReturn);
		return toReturn;
	}
	
	/**
	 * If a GameMaster with this name already exists : return it, 
	 * otherwise create it and add it in the list. 
	 * @param name (String)
	 * @param games (List&lt;String&gt;)
	 * @return (GTCGameMaster)
	 */
	public GTCGameMaster createGameMaster(String name, List<String> games) {
		if (this.masters.containsKey( name )) 
			{ return this.masters.get( name ); }
		GTCGameMaster toReturn = new GTCGameMaster(name, games);
		this.masters.put(name, toReturn);
		return toReturn;
	}

	/**
	 * If a the game is not in the list, return null : Table is not created, 
	 * otherwise create it and add it in the list. 
	 * @param gameName (String)
	 * @param gm (String)
	 * @param min (int)
	 * @param max (int)
	 * @return (GTCTable)
	 */
	public GTCTable createTable(	String gameName, String gm, 
									int min, int max) {
		GTCGame game = this.games.get( gameName );
		if (game == null) { return null; }
		GTCTable toReturn = new GTCTable(game.getName(), gm, min, max);
		this.tables.put(gameName, toReturn); // GTCTable tmp = 
		return toReturn;
	}
	
	/**
	 * If a the game is not in the list, Player's instance is added to a dedicated Player's list, 
	 * otherwise Player's instance is added to the Table's instance. 
	 * @param name (String)
	 * @param phone (String)
	 * @param gameName (String)
	 * @return (GTCPlayer)
	 */
	public GTCPlayer createPlayer(String name, String phone, String gameName) {
		GTCPlayer toReturn	= new GTCPlayer(name, phone, gameName);
		GTCTable game		= this.tables.get(gameName);
		
		GTCModelWrapper.log.info("game: " + game);
		
		if (game == null) 
			{ this.players.add(toReturn); }
		else {
			// NOTE : player is added only if enough space at the table !
			boolean isAdded = game.addPlayer(toReturn);
			if ( ! isAdded) {
				this.players.add(toReturn);
			}
		}
		return toReturn;
	}
	
	private static <T> Object reloadFrom(final String json, final Class<T> valueType) {
		try {
			return GTCModelWrapper.objectMapper.readValue(json, valueType);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static GTCPlayer reloadPlayerFrom(String json) 
		{ return (GTCPlayer) GTCModelWrapper.reloadFrom(json, GTCPlayer.class); }
	
	public static GTCGame reloadGameFrom(String json) 
		{ return (GTCGame) GTCModelWrapper.reloadFrom(json, GTCGame.class); }
	
	public static GTCGameMaster reloadGameMasterFrom(String json) 
		{ return (GTCGameMaster) GTCModelWrapper.reloadFrom(json, GTCGameMaster.class); }
	
	public static GTCTable reloadTableFrom(String json) 
		{ return (GTCTable) GTCModelWrapper.reloadFrom(json, GTCTable.class); }
	
	public Collection<GTCGame> getGames() {
		return this.games.values();
	}
	
	public Collection<GTCTable> getTables() {
		return this.tables.values();
	}
	
	public Collection<GTCGameMaster> getMasters() {
		return this.masters.values();
	}
	
	public Set<String> getListOfGames() {
		return this.games.keySet();
	}
	
	public Set<String> getListOfTables() {
		return this.tables.keySet();
	}
	
	public Set<String> getListOfMasters() {
		return this.masters.keySet();
	}
	
}
