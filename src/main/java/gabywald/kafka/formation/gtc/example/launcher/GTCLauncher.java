package gabywald.kafka.formation.gtc.example.launcher;

import gabywald.kafka.formation.gtc.example.view.GTCMainFrame;

/**
 * 
 * @author Gabriel Chandesris (20180625)
 * @deprecated PoC
 */
public class GTCLauncher {
	
	public static void main(String[] argv) { 
		
		GTCMainFrame consumerView = GTCMainFrame.getConsumerView();
		consumerView.setVisible( true );
		
		
		
		GTCMainFrame producerView = GTCMainFrame.getProducerView();
		producerView.setVisible( true );
		
	}
	
}
