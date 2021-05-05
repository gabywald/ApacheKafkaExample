package gabywald.kafka.formation.gtc.example.launcher;

import gabywald.kafka.formation.gtc.example.view.GTCMainFrame;

/**
 * Consumer-side Launcher for Swing GUI. 
 * @author Gabriel Chandesris (20180627)
 */
public class GTCConsumerLauncher {

	public static void main(String[] args) {
		GTCMainFrame consumerView = GTCMainFrame.getConsumerView();
		consumerView.setVisible( true );
	}

}
