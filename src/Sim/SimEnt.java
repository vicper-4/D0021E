package Sim;

// All entities like, nodes, switch, router, link etc that handles events
// need to inherit from this class

public abstract class SimEnt {
	

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
	
}
