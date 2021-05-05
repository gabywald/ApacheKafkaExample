package gabywald.kafka.formation.gtc.example.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * Load data from internal / sample files ; 
 * <br/>Create Data and instances of Classes for example. 
 * @author Gabriel Chandesris (20180703)
 */
public class GTCDataLoader {

  private static final Logger log = Logger.getLogger(GTCDataLoader.class);
  public static final String PATH_BASE = "src/main/resources/";
  public static final String PATH_DIRECTORY = PATH_BASE + "initializationSamples/";
  public static final String[] DATA_FILES = { "agentaliases.txt", "gameMasters.txt", "games2create.txt", };

  private static InputStreamReader get(String path) throws FileNotFoundException {
    return new FileReader(path);
  }

  public static InputStreamReader getAgentAliases() throws FileNotFoundException {
    return GTCDataLoader.get(GTCDataLoader.PATH_DIRECTORY + GTCDataLoader.DATA_FILES[0]);
  }

  /**
   * Load names from a file. 
   * @return (List&lt;String&gt;)
   */
  public static List<String> getNames() {
    List<String> toReturn = new ArrayList<String>();
    try {
      InputStreamReader isr = GTCDataLoader.getAgentAliases();
      BufferedReader brin = new BufferedReader(isr);
      String line = null;
      while ((line = brin.readLine()) != null) {
        toReturn.add(line);
      } // END "while ((line = brin.readLine()) != null)"
    } catch (FileNotFoundException e) {
      GTCDataLoader.log.error("FileNotFoundException: {" + GTCDataLoader.PATH_DIRECTORY + GTCDataLoader.DATA_FILES[0] + "}");
    } catch (IOException e) {
      GTCDataLoader.log.error("IOException: {" + GTCDataLoader.PATH_DIRECTORY + GTCDataLoader.DATA_FILES[0] + "}");
    }
    return toReturn;
  }

  public static List<GTCGame> getGames() {
    List<GTCGame> toReturn = new ArrayList<GTCGame>();
    try {
      InputStreamReader isr = GTCDataLoader.get(GTCDataLoader.PATH_DIRECTORY + GTCDataLoader.DATA_FILES[2]);
      BufferedReader brin = new BufferedReader(isr);
      String line = null;
      while ((line = brin.readLine()) != null) {
        String[] splitter = line.split("\t");
        switch (splitter.length) {
          case 2:
            toReturn.add(new GTCGame(splitter[0], splitter[1]));
            break;
          case 1:
            toReturn.add(new GTCGame(splitter[0]));
            break;
          default:
            GTCDataLoader.log.warn("splitter.length: (" + splitter.length + ") {" + line + "}");
        }
      } // END "while ((line = brin.readLine()) != null)"
    } catch (FileNotFoundException e) {
      GTCDataLoader.log.error("FileNotFoundException: {" + GTCDataLoader.PATH_DIRECTORY + GTCDataLoader.DATA_FILES[2] + "}");
    } catch (IOException e) {
      GTCDataLoader.log.error("IOException: {" + GTCDataLoader.PATH_DIRECTORY + GTCDataLoader.DATA_FILES[2] + "}");
    }
    return toReturn;
  }

  public static List<GTCGameMaster> getGameMasters() {
    List<GTCGameMaster> toReturn = new ArrayList<GTCGameMaster>();
    try {
      InputStreamReader isr = GTCDataLoader.get(GTCDataLoader.PATH_DIRECTORY + GTCDataLoader.DATA_FILES[1]);
      BufferedReader brin = new BufferedReader(isr);
      String line = null;
      while ((line = brin.readLine()) != null) {
        String[] splitter = line.split("\t");
        switch (splitter.length) {
          case 2:
            toReturn.add(new GTCGameMaster(splitter[0], splitter[1].split(";")));
            break;
          default:
            GTCDataLoader.log.warn("splitter.length: (" + splitter.length + ") {" + line + "}");
        }
      } // END "while ((line = brin.readLine()) != null)"
    } catch (FileNotFoundException e) {
      GTCDataLoader.log.error("FileNotFoundException: {" + GTCDataLoader.PATH_DIRECTORY + GTCDataLoader.DATA_FILES[1] + "}");
    } catch (IOException e) {
      GTCDataLoader.log.error("IOException: {" + GTCDataLoader.PATH_DIRECTORY + GTCDataLoader.DATA_FILES[1] + "}");
    }
    return toReturn;
  }

  /** 
   * To generate new "from scratch" (list of games have to exist)
   * @return List
   */
  public static List<GTCGameMaster> getNewGameMasters() {
    List<GTCGameMaster> toReturn = new ArrayList<GTCGameMaster>();
    // ***** Loading games *****
    List<GTCGame> games = GTCDataLoader.getGames();
    if (games.isEmpty()) {
      GTCDataLoader.log.warn("No games are defined !");
      return toReturn;
    }
    // ***** Loading names *****
    List<String> anames = GTCDataLoader.getNames();
    if (anames.isEmpty()) {
      GTCDataLoader.log.warn("No names are defined !");
    }
    // ***** Create GMs here ! *****
    Random rand = new Random();
    for (int j = 0; j < rand.nextInt(20) + 5; j++) {
      String gmName = anames.get(rand.nextInt(anames.size()));
      if (rand.nextInt() % 5 == 0) {
        toReturn.add(new GTCGameMaster(gmName, new String[] { "*" }));
      } else {
        String[] tabGames = new String[rand.nextInt(games.size() - 1) + 1];
        for (int i = 0; i < tabGames.length; i++) {
          tabGames[i] = games.get(rand.nextInt(games.size() - 1)).getName();
        }
        toReturn.add(new GTCGameMaster(gmName, tabGames));
      }
    } // END "for (int j = 0 ; j < rand.nextInt(20)+5 ; j++)"
    return toReturn;
  }

  public static Collection<GTCTable> createTables(Collection<GTCGameMaster> masters, Collection<GTCGame> games) {
    List<GTCTable> toReturn = new ArrayList<GTCTable>();
    Random rand = new Random();
    for (int j = 0; j < masters.size() * 3; j++) {
      for (GTCGameMaster gm : masters) {
        String gameName = gm.getGames().get(rand.nextInt(gm.getGames().size()));
        String gmName = gm.getName();
        int min = rand.nextInt(4) + 1;
        int max = rand.nextInt(4) + 4;
        while (max < min) {
          max = rand.nextInt(4) + 4;
        }
        GTCTable table = new GTCTable(gameName, gmName, min, max);
        toReturn.add(table);
      } // END "for (GTCGameMaster gm : masters)"
    } // END "for (int j = 0 ; j < masters.size()*3 ; j++)
    return toReturn;
  }

  public static Collection<GTCPlayer> createPlayers(Collection<GTCTable> tables) {
    List<GTCPlayer> toReturn = new ArrayList<GTCPlayer>();
    // ***** Loading names *****
    List<String> anames = GTCDataLoader.getNames();
    if (anames.isEmpty()) {
      GTCDataLoader.log.warn("No names are defined !");
    }
    Random rand = new Random();
    for (int j = 0; j < tables.size()/* *10 */ ; j++) {
      for (GTCTable table : tables) {
        String gameName = table.getGameName();
        String playerName = anames.get(rand.nextInt(anames.size()));
        String phoneNumber = "00 00 00 00 00";
        toReturn.add(new GTCPlayer(playerName, phoneNumber, gameName));
      } // END "for (GTCTable table : tables)"
    } // END "for (int j = 0 ; j < tables.size()*10 ; j++)"
    return toReturn;
  }
}
