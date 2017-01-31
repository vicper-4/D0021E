package Sim;


// This interface is to be used for messages sent between Simulation Entities
// like packets and timing events calling itself etc. The method entering is called
// when the event triggers.

public interface Event {
	public void entering(SimEnt locale);
}

