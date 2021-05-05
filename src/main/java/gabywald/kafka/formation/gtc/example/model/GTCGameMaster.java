package gabywald.kafka.formation.gtc.example.model;

import java.util.Arrays;
import java.util.List;

/**
 * Define a Game Master. 
 * @author Gabriel Chandesris (20180627)
 */
public class GTCGameMaster {
	
	public static final String GAMEMASTER_JSON_CREATE	= "creategamemaster";
	public static final String GAMEMASTER_JSON_NAME		= "name";
	public static final String GAMEMASTER_JSON_GAMES	= "games";
	
	private String name;
	
	private List<String> games;
	
	public GTCGameMaster(String name) {
		this(name, new String[0]);
	}
	
	public GTCGameMaster(String name, String[] games) {
		this.name	= name;
		this.games	= Arrays.asList( games );
	}
	
	public GTCGameMaster(String name, List<String> games) {
		this.name	= name;
		this.games	= games;
	}
	
	public String getName() {
		return this.name;
	}

	public List<String> getGames() {
		return this.games;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setGames(List<String> games) {
		this.games = games;
	}

	public boolean accept(String gameName) {
		if ( (this.games.size() == 1) 
				&& (this.games.contains( "*" )) ) 
			{ return true; }
		return this.games.contains( gameName );
	}

}
