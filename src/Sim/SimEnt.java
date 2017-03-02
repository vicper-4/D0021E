package Sim;

// All entities like, nodes, switch, router, link etc that handles events
// need to inherit from this class

public abstract class SimEnt {


	private int _recv;
	private double totalTransit;
	private double jitter;

	protected SimEnt()
	{	
	}
	
	// Called when erasing an entity like node or link etc. The SimEngine is called in case
	// that de-registration of the entity is needed 
	
	protected final void kill()
	{
		this.destructor();
	}
	
	// To be implemented in child classes if cleaning up is needed when the entity is killed
	
	protected void destructor()
	{
		// no op, can be added in child classes
	}
	
	// This method schedules a coming event in the SimEngine
	
	protected final EventHandle send(SimEnt destination, Event event, double delayExecution)
	{
		// Makes sure that events are not made in negative time
		if(delayExecution < 0.0) delayExecution = 0.0;
		// this object is the registrator/source submitting the event
		return SimEngine.instance().register(this, destination, event, delayExecution);
	}
	
	
	//Erases a scheduled event from the SimEngine
	
	protected final void eraseScheduledEvent(EventHandle handleToEvent)
	{
		SimEngine.instance().deregister(handleToEvent);
	}
	
	
	// To be implemented in child classes acting on events/messages received

	public abstract void recv(SimEnt source, Event event);

	/**
	 * Called when a node receives a message
	 *
	 * @param tt The transit time of the message
	 */
	public void calculateJitter(double tt) {
		_recv++;
		totalTransit += tt;

		//Algorithm from RFC1889 A.8
		double d = tt - totalTransit / _recv;
		if (d < 0) d = -d;
		jitter += (1.0 / ((double) _recv)) * (d - jitter);

		SimEngine.setJitter(jitter);
	}

	public double getJitter() {
		return jitter;
	}

}
