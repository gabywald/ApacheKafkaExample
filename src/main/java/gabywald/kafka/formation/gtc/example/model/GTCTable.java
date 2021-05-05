package gabywald.kafka.formation.gtc.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a Game Table. 
 * @author Gabriel Chandesris (20180627)
 */
public class GTCTable {
	
	public static final String TABLE_JSON_CREATE		= "createtable";
	public static final String TABLE_JSON_GAME			= "game";
	public static final String TABLE_JSON_GAMEMASTER	= "gm";
	public static final String TABLE_JSON_MINIMUM		= "min";
	public static final String TABLE_JSON_MAXIMUM		= "max";
	
	private String game;
	private String gm;
	private int min, max;
	private List<GTCPlayer> players;
	
//	public GTCTable() {
//		this(null, null, -1, -1);
//	}
	
	public GTCTable(String game, String gm, 
					int min, int max) {
		this.game	= game;
		this.gm		= gm;
		this.min	= min;
		this.max	= max;
		this.players	= new ArrayList<GTCPlayer>();
	}
	
	public boolean addPlayer(GTCPlayer player) {
		if (player == null) { return false; }
		if (this.players.size() < this.max) {
			this.players.add(player);
			return true;
		} else { return false; }
	}
	
	public boolean isStartable() {
		return (this.players.size() > this.min);
	}
	
	public String getGameName() {
		return this.game;
	}

	public String getGameMaster() {
		return this.gm;
	}

	public int getMin() {
		return this.min;
	}

	public int getMax() {
		return this.max;
	}

	public List<GTCPlayer> getPlayers() {
		return this.players;
	}
	
}
