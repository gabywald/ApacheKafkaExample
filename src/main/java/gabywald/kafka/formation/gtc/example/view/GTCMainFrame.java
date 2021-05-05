package gabywald.kafka.formation.gtc.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gabywald.kafka.formation.gtc.example.GTCConsumer;
import gabywald.kafka.formation.gtc.example.GTCProducer;
import gabywald.kafka.formation.gtc.example.model.GTCDataLoader;
import gabywald.kafka.formation.gtc.example.model.GTCGame;
import gabywald.kafka.formation.gtc.example.model.GTCGameMaster;
import gabywald.kafka.formation.gtc.example.model.GTCModelWrapper;
import gabywald.kafka.formation.gtc.example.model.GTCPlayer;
import gabywald.kafka.formation.gtc.example.model.GTCTable;

/**
 * GUI (Graphical User Interface) example to use Kafka Consumer / Producer sample for GTC ("Gestionnaire de Tables de Conventions"). 
 * @author Gabriel Chandesris (20180625)
 */
@SuppressWarnings("serial")
public class GTCMainFrame extends JFrame {

  private static final Logger log = Logger.getLogger(GTCMainFrame.class);
  private static GTCMainFrame gtcConsumerInstance = null;
  private static GTCMainFrame gtcProducerInstance = null;
  public static final Font consumerFont = new Font(Font.MONOSPACED, Font.BOLD, 20);
  static {
    // NOTE : could be initialized via exchanging some messages AND / OR created by user as example
    // // // ***** Create some games
    List<GTCGame> games = GTCDataLoader.getGames();
    for (GTCGame gameName : games) {
      GTCModelWrapper.getInstance4Consumer().addGame(gameName);
      GTCModelWrapper.getInstance4Producer().addGame(gameName);
    } // END "for (String gameName : GTCMainFrame.games)"
    // // // ***** Create some Game Masters
    List<GTCGameMaster> gmsFromFile = GTCDataLoader.getGameMasters();
    for (GTCGameMaster gm : gmsFromFile) {
      GTCModelWrapper.getInstance4Consumer().addGameMaster(gm);
      GTCModelWrapper.getInstance4Producer().addGameMaster(gm);
    } // END "for (String gameName : GTCMainFrame.games)"
  }
  private JTextArea textArea = new JTextArea();

  private GTCMainFrame(String name) {
    this.setTitle(name);
    this.setLayout(new BorderLayout());
    boolean isResizeable = true;
    this.setResizable(isResizeable);
    this.setLocation(25, 100);
    this.setSize(1024, 768);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    /** this.setVisible(true); */
  }

  public void addText(String message) {
    if (this.textArea == null) {
      return;
    }
    this.textArea.append(message);
    this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
    this.textArea.update(this.textArea.getGraphics());
    // SwingUtilities.updateComponentTreeUI(this.textArea);
    this.textArea.getParent().update(this.textArea.getParent().getGraphics());
  }

  public static GTCMainFrame getConsumerView() {
    if (GTCMainFrame.gtcConsumerInstance != null) {
      return GTCMainFrame.gtcConsumerInstance;
    }
    GTCMainFrame.gtcConsumerInstance = new GTCMainFrame("GTC -- Consumer View");
    JPanel mainPanelConsumer = new JPanel(new BorderLayout());
    mainPanelConsumer.setPreferredSize(new Dimension(GTCMainFrame.gtcConsumerInstance.getWidth() - 200, GTCMainFrame.gtcConsumerInstance.getHeight() - 10));
    GTCMainFrame.gtcConsumerInstance.textArea = new JTextArea();
    GTCMainFrame.gtcConsumerInstance.textArea.setBackground(Color.BLACK);
    GTCMainFrame.gtcConsumerInstance.textArea.setForeground(Color.GREEN);
    GTCMainFrame.gtcConsumerInstance.textArea.setFont(GTCMainFrame.consumerFont);
    JScrollPane textScroll = new JScrollPane(GTCMainFrame.gtcConsumerInstance.textArea);
    textScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    textScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    GTCMainFrame.gtcConsumerInstance.textArea.setText("Consumer text Console View\n");
    mainPanelConsumer.add(textScroll, BorderLayout.CENTER);
    JPanel buttonsOnRight = new JPanel(new GridLayout(10, 1));
    buttonsOnRight.setPreferredSize(new Dimension(150, GTCMainFrame.gtcConsumerInstance.getHeight() - 10));
    buttonsOnRight.add(GTCMainFrame.createJButton("List Tables"));
    buttonsOnRight.add(GTCMainFrame.createJButton("List GameMasters"));
    buttonsOnRight.add(GTCMainFrame.createJButton("List Players"));
    mainPanelConsumer.add(buttonsOnRight, BorderLayout.EAST);
    GTCMainFrame.gtcConsumerInstance.setContentPane(mainPanelConsumer);
    GTCMainFrame.gtcConsumerInstance.pack();
    GTCConsumer gtcc = GTCConsumer.getConsumerFromConfig();
    gtcc.setFrame(GTCMainFrame.gtcConsumerInstance);
    gtcc.start();
    // TODO ...
    return GTCMainFrame.gtcConsumerInstance;
  }

  public static GTCMainFrame getProducerView() {
    if (GTCMainFrame.gtcProducerInstance != null) {
      return GTCMainFrame.gtcProducerInstance;
    }
    GTCMainFrame.gtcProducerInstance = new GTCMainFrame("GTC -- Producer View");
    int nextLocationX = GTCMainFrame.gtcProducerInstance.getLocation().x + GTCMainFrame.gtcProducerInstance.getWidth() + 10;
    GTCMainFrame.gtcProducerInstance.setLocation(nextLocationX, GTCMainFrame.gtcProducerInstance.getLocation().y);
    JPanel mainPanelConsumer = new JPanel(new BorderLayout());
    //		mainPanelConsumer.setPreferredSize(new Dimension(	GTCMainFrame.gtcConsumerInstance.getWidth() - 200, 
    //															GTCMainFrame.gtcConsumerInstance.getHeight() - 10));
    GTCMainFrame.gtcProducerInstance.textArea = new JTextArea();
    GTCMainFrame.gtcProducerInstance.textArea.setBackground(Color.LIGHT_GRAY);
    GTCMainFrame.gtcProducerInstance.textArea.setForeground(Color.BLACK);
    GTCMainFrame.gtcProducerInstance.textArea.setFont(GTCMainFrame.consumerFont);
    JScrollPane textScroll = new JScrollPane(GTCMainFrame.gtcProducerInstance.textArea);
    textScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    textScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    GTCMainFrame.gtcProducerInstance.textArea.setText("Producer text Console View\n");
    mainPanelConsumer.add(textScroll, BorderLayout.CENTER);
    JPanel buttonsOnRight = new JPanel(new GridLayout(10, 1));
    buttonsOnRight.setPreferredSize(new Dimension(150, GTCMainFrame.gtcProducerInstance.getHeight() - 10));
    JButton createTableButton = GTCMainFrame.createJButton("Create Table");
    //		JButton createGMButton		= GTCMainFrame.createJButton("Create GameMaster");
    //		JButton createPlayerButton	= GTCMainFrame.createJButton("Create Player");
    JButton createPlayerSButton = GTCMainFrame.createJButton("Create PlayerS");
    JButton sendMessagesButton = GTCMainFrame.createJButton("Send MessageS");
    //		GTCConsumer gtcc = GTCConsumer.getConsumerFromConfig();
    //		gtcc.setFrame( GTCMainFrame.gtcConsumerInstance );
    //		gtcc.start();
    GTCProducer gtcp = GTCProducer.getProducerFromConfig();
    gtcp.setFrame(GTCMainFrame.gtcProducerInstance);
    gtcp.start();
    createTableButton.addActionListener(new ActionListener() {
      // private GTCProducer gtcp = GTCProducer.getProducerFromConfig();

      @Override
      public void actionPerformed(ActionEvent arg0) {
        // JOptionPane / JDialog / ...
        // see example : https://inversionconsulting.blogspot.com/2009/06/java-joptionpane-examples-part-4.html
        JComboBox<String> comboBoxGame = new JComboBox<String>(GTCModelWrapper.getInstance4Producer().getListOfGames().toArray(new String[0]));
        JComboBox<String> comboBoxMaster = new JComboBox<String>(GTCModelWrapper.getInstance4Producer().getListOfMasters().toArray(new String[0]));
        List<Integer> rangeMini = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        JComboBox<Integer> comboBoxMini = new JComboBox<Integer>(rangeMini.stream().toArray(Integer[]::new));
        List<Integer> rangeMaxi = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        JComboBox<Integer> comboBoxMaxi = new JComboBox<Integer>(rangeMaxi.stream().toArray(Integer[]::new));
        Object[] array = { new JLabel("Select game"), comboBoxGame, new JLabel("Select Master"), comboBoxMaster, new JLabel("Min:"), comboBoxMini,
            new JLabel("Max:"), comboBoxMaxi, };
        JOptionPane pane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = pane.createDialog(null, "Create a Table");
        dialog.setResizable(true);
        dialog.setVisible(true);
        while (dialog.isVisible()) {
          ;
        }
        String selectedGame = comboBoxGame.getItemAt(comboBoxGame.getSelectedIndex());
        String selectedMaster = comboBoxMaster.getItemAt(comboBoxMaster.getSelectedIndex());
        int selectedMini = comboBoxMini.getItemAt(comboBoxMini.getSelectedIndex());
        int selectedMaxi = comboBoxMaxi.getItemAt(comboBoxMaxi.getSelectedIndex());
        //				GTCMainFrame.log.info( "selectedGame: " + selectedGame );
        //				GTCMainFrame.log.info( "selectedMini: " + selectedMini );
        //				GTCMainFrame.log.info( "selectedMaxi: " + selectedMaxi );
        GTCModelWrapper.getInstance4Producer().createTable(selectedGame, selectedMaster, selectedMini, selectedMaxi);
        if (!gtcp.isRunning()) {
          gtcp.start();
        }
        GTCMainFrame.log.info("game " + selectedGame + " " + selectedMini + "-" + selectedMaxi);
        ObjectMapper om = GTCModelWrapper.getObjectMapper();
        ObjectNode jsonObj = om.createObjectNode();
        ObjectNode table = jsonObj.putObject(GTCTable.TABLE_JSON_CREATE);
        table.put(GTCTable.TABLE_JSON_GAME, selectedGame);
        table.put(GTCTable.TABLE_JSON_GAMEMASTER, selectedMaster);
        table.put(GTCTable.TABLE_JSON_MINIMUM, selectedMini);
        table.put(GTCTable.TABLE_JSON_MAXIMUM, selectedMaxi);
        try {
          gtcp.sendData(om.writeValueAsString(jsonObj));
        } catch (JsonProcessingException e) {
          // e.printStackTrace();
          GTCMainFrame.log.warn("JsonProcessingException");
        }
      }
    });
    createPlayerSButton.addActionListener(new ActionListener() {
      // private GTCProducer gtcp = GTCProducer.getProducerFromConfig();

      @Override
      public void actionPerformed(ActionEvent arg0) {
        List<Integer> rangeMaxi = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        JComboBox<Integer> comboBoxMaxi = new JComboBox<Integer>(rangeMaxi.stream().toArray(Integer[]::new));
        Object[] array = { new JLabel("Number of players:"), comboBoxMaxi, };
        JOptionPane pane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = pane.createDialog(null, "Result Data");
        dialog.setResizable(true);
        dialog.setVisible(true);
        while (dialog.isVisible()) {
          ;
        }
        int numberOfPlayers = comboBoxMaxi.getItemAt(comboBoxMaxi.getSelectedIndex());
        GTCMainFrame.log.info("numberOfPlayers: " + numberOfPlayers);
        String[] tables = GTCModelWrapper.getInstance4Producer().getListOfTables().toArray(new String[0]);
        if (tables.length == 0) {
          GTCMainFrame.log.warn("No table at this time !");
        }
        Random rand = new Random();
        if (!gtcp.isRunning()) {
          gtcp.start();
        }
        //				JSONObject jsonObj	= new JSONObject();
        //				jsonObj.put("players", numberOfPlayers);
        //				for (int i = 0 ; i < numberOfPlayers ; i++) {
        //					JSONObject player	= new JSONObject();
        //					player.put("name", "player" + i);
        //					player.put("phone", "00 00 00 00 00");
        //					jsonObj.put("player" + i, player);
        //				} // END "for (int i = 0 ; i < numberOfPlayers ; i++)"
        //
        //				this.gtcp.sendData( jsonObj.toJSON() );
        ObjectMapper om = GTCModelWrapper.getObjectMapper();
        for (int i = 0; i < numberOfPlayers; i++) {
          GTCMainFrame.log.info("\t" + "player" + i);
          /*JsonNode*/ObjectNode jsonObj = om.createObjectNode();
          // new JsonNode();
          /*JsonNode*/ObjectNode table = jsonObj.putObject(GTCPlayer.PLAYER_JSON_CREATE); // objectMapper.createObjectNode();
          table.put(GTCPlayer.PLAYER_JSON_NAME, "player" + i);
          if (tables.length == 0) {
            JOptionPane.showMessageDialog(GTCMainFrame.getConsumerView(), // 
                "No table where to add Players.", // 
                "Error Message. ", // 
                JOptionPane.ERROR_MESSAGE);
            return;
          }
          table.put(GTCPlayer.PLAYER_JSON_GAME, tables[rand.nextInt(tables.length)]);
          table.put(GTCPlayer.PLAYER_JSON_PHONE, "00 00 00 00 00");
          try {
            gtcp.sendData(om.writeValueAsString(jsonObj));
          } catch (JsonProcessingException e) {
            // e.printStackTrace();
            GTCMainFrame.log.warn("JsonProcessingException");
          }
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            // e.printStackTrace();
            GTCMainFrame.log.warn("InterruptedException");
          }
        } // END "for (int i = 0 ; i < numberOfPlayers ; i++)"
      }
    });
    sendMessagesButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (!gtcp.isRunning()) {
          gtcp.start();
        }
        ObjectMapper om = GTCModelWrapper.getObjectMapper();
        gtcp.show("Create some Games !");
        Collection<GTCGame> games = GTCModelWrapper.getInstance4Producer().getGames();
        for (GTCGame game : games) {
          GTCMainFrame.log.info("Producer : game created " + game);
          ObjectNode jsonObj = om.createObjectNode();
          jsonObj.putPOJO(GTCGame.GAME_JSON_CREATE, game);
          try {
            gtcp.sendData(om.writeValueAsString(jsonObj));
          } catch (JsonProcessingException e) {
            // e.printStackTrace();
            GTCMainFrame.log.warn("JsonProcessingException");
          }
        } // END "for (GTCGame game : games)"
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          ;
        }
        if (!gtcp.isRunning()) {
          gtcp.start();
        }
        gtcp.show("Create some Game Masters !");
        Collection<GTCGameMaster> masters = GTCModelWrapper.getInstance4Producer().getMasters();
        for (GTCGameMaster master : masters) {
          GTCMainFrame.log.info("Producer : master created " + master);
          ObjectNode jsonObj = om.createObjectNode();
          jsonObj.putPOJO(GTCGameMaster.GAMEMASTER_JSON_CREATE, master);
          try {
            gtcp.sendData(om.writeValueAsString(jsonObj));
          } catch (JsonProcessingException e) {
            // e.printStackTrace();
            GTCMainFrame.log.warn("JsonProcessingException");
          }
        } // END "for (GTCGameMaster master : masters) "
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          ;
        }
        if (!gtcp.isRunning()) {
          gtcp.start();
        }
        gtcp.show("Create some Tables !");
        Collection<GTCTable> tables = GTCDataLoader.createTables(masters, games);
        for (GTCTable table : tables) {
          GTCMainFrame.log.info("Producer : table created " + table);
          ObjectNode jsonObj = om.createObjectNode();
          jsonObj.putPOJO(GTCTable.TABLE_JSON_CREATE, table);
          try {
            gtcp.sendData(om.writeValueAsString(jsonObj));
          } catch (JsonProcessingException e) {
            // e.printStackTrace();
            GTCMainFrame.log.warn("JsonProcessingException");
          }
        } // END "for (GTCTable table : tables)"
        try {
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          ;
        }
        if (!gtcp.isRunning()) {
          gtcp.start();
        }
        gtcp.show("Create some Players !");
        Collection<GTCPlayer> players = GTCDataLoader.createPlayers(tables);
        for (GTCPlayer player : players) {
          GTCMainFrame.log.info("Producer : player created " + player);
          ObjectNode jsonObj = om.createObjectNode();
          jsonObj.putPOJO(GTCPlayer.PLAYER_JSON_CREATE, player);
          try {
            gtcp.sendData(om.writeValueAsString(jsonObj));
          } catch (JsonProcessingException e) {
            // e.printStackTrace();
            GTCMainFrame.log.warn("JsonProcessingException");
          }
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            ;
          }
        } // END "for (GTCPlayer player : players)"
        //  ...
      }
    });
    buttonsOnRight.add(createTableButton);
    //		buttonsOnRight.add( createGMButton );
    //		buttonsOnRight.add( createPlayerButton );
    buttonsOnRight.add(createPlayerSButton);
    buttonsOnRight.add(sendMessagesButton);
    mainPanelConsumer.add(buttonsOnRight, BorderLayout.WEST);
    GTCMainFrame.gtcProducerInstance.setContentPane(mainPanelConsumer);
    GTCMainFrame.gtcProducerInstance.pack();
    //		Component[] comps = buttonsOnRight.getComponents();
    //		GTCMainFrame.log.info( "comps.length: (" + comps.length + ")" );
    //		for (int i = 0 ; i < comps.length ; i++) 
    //			{ GTCMainFrame.log.info( "\t" + comps[i] ); }
    // TODO ...
    return GTCMainFrame.gtcProducerInstance;
  }

  private static JButton createJButton(String name) {
    JButton toReturn = new JButton(name);
    toReturn.setPreferredSize(new Dimension(140, 10));
    // toReturn.setFont( GTCMainFrame.consumerFont  );
    return toReturn;
  }
}
