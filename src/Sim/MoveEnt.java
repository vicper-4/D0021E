package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class Message implements Event{
	private SimEnt _link;
	private NetworkAddr _newNetwork;
	
	Message (SimEnt _link, NetworkAddr newNetwork)
	{
		this.link = _link;
		this.newNet = newNetwork;
	}

	public SimEnt link()
	{
		return _link;
	}

	public networkId()
	{
		return _newNetwork;
	}
	
	public void entering(SimEnt locale)
	{
	}
}
	
