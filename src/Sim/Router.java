package Sim;

// This class implements a simple router

public class Router extends SimEnt{

	private RouteTableEntry _routingTable;
	private int _interfaces;
	private SimEnt [] _interface;
	private int _now=0;

	// When created, number of interfaces are defined
	
	Router(int interfaces)
	{
		_interface = new SimEnt[interfaces];
		_interfaces=interfaces;
	}
	
	// This method connects links to the router and also informs the 
	// router of the host connects to the other end of the link
	
	public void connectInterface(int interfaceNumber, SimEnt link)
	{
		if (interfaceNumber<_interfaces && _interface[interfaceNumber] == null)
		{
			_interface[interfaceNumber] = link;
		}
		else
			System.out.println("Trying to connect to port not in router or not empty");
		
		((Link) link).setConnector(this);
	}

	/**
	* This method disconnects links from the router
	* @param link the link to disconnect
	*/
	public void disconnectLink(SimEnt link)
	{
		for(int i=0; i<_interfaces; i++)
			if (link == _interface[i])
			{
				_interface[i] = null;
				((Link) link).unsetConnector(this);
			}
	}

	///**
	//* This method disconnects the link connected to a interface from the router
	//* @param i interface to disconnect
	//*/
	//public void disconnectInterface(int i)
	//{
	//	if (_interface[i] != null)
	//	{
	//		((Link) _interface[i]).unsetConnector(this);
	//		_interface[i] = null;
	//	}
	//}

	private int getInterface(int networkAddress)
	{
		if ( _routingTable == null )
			return -1;
		RouteTableEntry entry = (RouteTableEntry)_routingTable;

		do {
			if ( entry.getAddress() == networkAddress )
				return entry.getInterface();

			entry = (RouteTableEntry)entry.getNext();
		}while(entry != null);

		return -1;
	}

	// This method searches for an entry in the routing table that matches
	// the network number in the destination field of a messages. The link
	// returned is the one thru which the router previously has recived a
	// message from that address
	
	private SimEnt getLink(int networkAddress)
	{
		int i = getInterface(networkAddress);

		if ( i >= 0 && i < _interfaces )
			return _interface[i];

		return null;
	}

	private int getLinkPlacement(SimEnt link)
	{
		for ( int i = 0; i < _interfaces; i++ )
		{
			if ( _interface[i] == link )
				return i;
		}

		return -1;
	}

	public void addTableEntry(int _interface, NetworkAddr _address)
	{
		RouteTableEntry newEntry = new RouteTableEntry(_interface, _address);
		
		newEntry.setNext(_routingTable);
		_routingTable = newEntry;
	}
	
	// When events are received at the router this method is called
	
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof Message)
		{
			recvMsg(src, ev);
		}
	}

	private void recvMsg(SimEnt src, Event ev)
	{
		System.out.println("Router handles packet with seq: " + ((Message) ev).seq()+" from node: "+((Message) ev).source().networkId()+"." + ((Message) ev).source().nodeId() );
		SimEnt sendNext = getLink(((Message) ev).destination().networkId());

		// Send it along
		if ( sendNext != null && ((Message) ev).ttl-- >= 0 )
		{
			send(sendNext, ev, _now);

			System.out.println( "Router sends to node: " + 
								((Message) ev).destination().networkId() + 
								"." + 
								((Message) ev).destination().nodeId() +
								" through interface " + getLinkPlacement(sendNext)
							  );
		}
		//TODO should we realy send to all interfaces?
		else if ( ((Message) ev).ttl-- >= 0)
		{
			System.out.println( "Router forwards message to unknown address to all interfaces. ");

			for(int i = 0; i < _interfaces; i++)
			{
					send(_interfaces[i], ev, _now);
			}
		}

		//Check if the sender is known. if not add it to the routing table
		if ( (getLink( ((Message) ev).source().networkId() ) == null) ||
			 (getLink( ((Message) ev).source().networkId() ) != src) )
		{
			System.out.println("Router adds node: "+((Message) ev).source().networkId()+"." + ((Message) ev).source().nodeId() + " at interface: " + getLinkPlacement(src));
			addTableEntry(getLinkPlacement(src), ((Message) ev).source());
		}
	}

	private void forwardMsg(SimEnt next, Event ev, double delay)
	{
			send(next, ev, delay);
	}
}
