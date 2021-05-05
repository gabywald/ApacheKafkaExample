package gabywald.kafka.formation.gtc.example;

import gabywald.kafka.formation.gtc.example.view.GTCMainFrame;

/**
 * Parent class for Producer / Consumer connectors Runnable / Thread. 
 * @author Gabriel Chandesris (20180626)
 * @see GTCConsumer
 * @See GTCProducer
 */
public abstract class GTCParent {
	
	// private static final Logger log = Logger.getLogger( GTCParent.class );

	protected boolean isRunning			= false;
	
	protected GTCMainFrame frame		= null;
	
	protected String topicName			= null;
	
	public boolean isRunning() 
		{ return this.isRunning; }
	
	public void setFrame(GTCMainFrame frame) {
		this.frame = frame;
	}
	
	public GTCMainFrame getFrame() {
		return this.frame;
	}
	
}
