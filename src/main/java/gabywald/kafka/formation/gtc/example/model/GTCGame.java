package gabywald.kafka.formation.gtc.example.model;

/**
 * Define a Game. 
 * @author Gabriel Chandesris (20180627)
 */
public class GTCGame {
	
	public static final String GAME_JSON_CREATE			= "creategame";
	public static final String GAME_JSON_NAME			= "name";
	public static final String GAME_JSON_DESCRIPTION	= "description";
	
	private String name, description;
	
	public GTCGame(String name) {
		this(name, null);
	}
	
	public GTCGame(String name, String desc) {
		this.name			= name;
		this.description	= desc;
	}
	
	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}