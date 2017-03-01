package Sim;

// This class implements a simple router

public class Router extends SimEnt{

	private RouteTableEntry [] _routingTable;
	private int _interfaces;
	private int _now=0;

	// When created, number of interfaces are defined
	
	Router(int interfaces)
	{
		_routingTable = new RouteTableEntry[interfaces];
		_interfaces=interfaces;
	}
	
	// This method connects links to the router and also informs the 
	// router of the host connects to the other end of the link
	
	public void connectInterface(int interfaceNumber, SimEnt link, SimEnt node)
	{
		if (interfaceNumber<_interfaces)
			_routingTable[interfaceNumber] = new RouteTableEntry(link, node);
		else
			System.out.println("Trying to connect to port not in router");
		
		((Link) link).setConnector(this);
	}

	/**
	* This method disconnects links from the router and also removes router
	* information about what is connected to the other end of that link
	*/
	public void disconnectInterface(SimEnt link)
	{
		for(int i=0; i<_interfaces; i++)
			if (_routingTable[i] != null)
			{
				if (link == _routingTable[i].link())
				{
					_routingTable[i] = null;
					((Link) link).unsetConnector(this);
				}
			}
	}

	// This method searches for an entry in the routing table that matches
	// the network number in the destination field of a messages. The link
	// represents that network number is returned
	
	private SimEnt getInterface(int networkAddress)
	{
		boolean pref, depr;
		SimEnt routerInterface=null;
		for(int i=0; i<_interfaces; i++)
			if (_routingTable[i] != null)
			{
				pref = (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress);
				
				if (((Node) _routingTable[i].node()).getDepr() != null)
					depr = (((Node) _routingTable[i].node()).getDepr().networkId() == networkAddress);
				else
					depr = false;
				
				if ( pref || depr)
				{
					routerInterface = _routingTable[i].link();
				}
			}
		return routerInterface;
	}
	
	// When messages are received at the router this method is called
	
	public void recv(SimEnt source, Event event)
	{
		if (event instanceof Message)
		{
			System.out.println("Router handles packet with seq: " + ((Message) event).seq()+" from node: "+((Message) event).source().networkId()+"." + ((Message) event).source().nodeId() );
			SimEnt sendNext = getInterface(((Message) event).destination().networkId());

			if(sendNext != null)
			{
				send (sendNext, event, _now);
				System.out.println("Router sends to node: " + ((Message) event).destination().networkId()+"." + ((Message) event).destination().nodeId());
			}
		}
	}
}
