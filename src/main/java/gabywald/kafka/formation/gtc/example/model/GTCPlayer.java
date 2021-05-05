package gabywald.kafka.formation.gtc.example.model;

/**
 * Defines a Player. 
 * @author Gabriel Chandesris (20180627)
 */
public class GTCPlayer {
	
	public static final String PLAYER_JSON_CREATE	= "createplayer";
	public static final String PLAYER_JSON_NAME		= "name";
	public static final String PLAYER_JSON_PHONE	= "phone";
	public static final String PLAYER_JSON_GAME		= "game";
	
	private String name;
	private String phone;
	private String game;
	
	public GTCPlayer(String name, String phone) {
		this(name, phone, null);
	}
	
	public GTCPlayer(String name, String phone, String game) {
		this.name	= name;
		this.phone	= phone;
		this.game	= game;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getGame() {
		return this.game;
	}

	public void setGame(String game) {
		this.game = game;
	}
	
}
