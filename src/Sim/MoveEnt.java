package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class MoveEnt implements Event{
	private SimEnt _link;
	private NetworkAddr _newNetwork;
	private int _routerInterface;
	
	MoveEnt (SimEnt _link, NetworkAddr newNetwork, int routerInterface)
	{
		this._link = _link;
		this._newNetwork = newNetwork;
		this._routerInterface = routerInterface;
	}

	public SimEnt link()
	{
		return _link;
	}

	public int networkId()
	{
		return _newNetwork.networkId();
	}

	public int interface()
	{
		return _routerInterface;
	}
	
	public void entering(SimEnt locale)
	{
	}
}
	
