package gabywald.kafka.formation.gtc.example.launcher;

import gabywald.kafka.formation.gtc.example.view.GTCMainFrame;

/**
 * Producer-side Launcher for Swing GUI. 
 * @author Gabriel Chandesris (20180627)
 */
public class GTCProducerLauncher {

	public static void main(String[] args) {
		GTCMainFrame producerView = GTCMainFrame.getProducerView();
		producerView.setVisible( true );
	}

}
