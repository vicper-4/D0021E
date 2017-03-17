package Sim;

// This class implements a simple router

public class Router extends SimEnt{

	private RouteTableEntry _routingTable;
	private int _interfaces;
	private SimEnt [] _interface;
	private final int _now=0;

	// When created, number of interfaces are defined
	
	Router(int interfaces)
	{
		_interface = new SimEnt[interfaces];
		_interfaces=interfaces;
		send(this, new TimerEvent(), 10);
	}
	
	// This method connects links to the router and also informs the 
	// router of the host connects to the other end of the link
	
	public void connectInterface(int interfaceNumber, SimEnt link)
	{
		if (interfaceNumber<_interfaces && _interface[interfaceNumber] == null)
		{
			_interface[interfaceNumber] = link;
			((Link) link).setConnector(this);
		}
		else
			System.out.println("Trying to connect to port not in router or not empty");
		
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

	private int getInterface(int networkAddress, int srcInterface)
	{
		RouteTableEntry entry = (RouteTableEntry)_routingTable;

		while(entry != null)
		{
			if ( entry.getAddress() == networkAddress && entry.getInterface() != srcInterface )
				return entry.getInterface();

			entry = (RouteTableEntry)entry.getNext();
		}

		return -1;
	}

	// This method searches for an entry in the routing table that matches
	// the network number in the destination field of a messages. The link
	// returned is the one thru which the router previously has recived a
	// message from that address
	
	private SimEnt getLink(int networkAddress, int srcInterface)
	{
		int i = getInterface(networkAddress, srcInterface);

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
		if (ev instanceof TimerEvent)
		{
			recvTimerEvent(ev);
		}
		else if (ev instanceof RouterSolicitation)
		{
			recvRouterSolicitation(src, ev);
		}
		else if (ev instanceof RouterAdvertisement); // Do not forward router advertisements
		else if (ev instanceof Message)
		{
			recvMsg(src, ev);
		}
	}

	private void recvTimerEvent(Event ev)
	{
		if(sentAdvertisements < 20){ // TODO ugly hack to make it stop!
			sendRouterAdvertisement(this, ev);
			send(this, new TimerEvent(), 50);
		}
	}

	private int sentAdvertisements = 0;
	private void sendRouterAdvertisement(SimEnt src, Event ev)
	{
		RouterAdvertisement advertisement = new RouterAdvertisement(_broadcast);

		System.out.println("!! " + this + " sending RouterAdvertisement on all interfaces at time " + SimEngine.getTime());
		for(int i = 0; i < _interfaces; i++)
		{
			send(_interface[i], advertisement, _now);
		}
		sentAdvertisements++;
	}

	private void recvRouterSolicitation(SimEnt src, Event ev)
	{
		System.out.println("!! " + this + " received RouterSolicitation, sending RouterAdvertisement");
		sendRouterAdvertisement(src, ev);
	}

	private void recvMsg(SimEnt src, Event ev)
	{
		System.out.println(this + " handles packet with seq: " + ((Message) ev).seq()+" from node: "+((Message) ev).source().networkId()+"." + ((Message) ev).source().nodeId() );
		
		SimEnt sendNext = null;
		if (((Message) ev).destination() != null )
			sendNext = getLink(((Message) ev).destination().networkId(), getLinkPlacement(src));

		// Send it along
		if ( sendNext != null && ((Message) ev).ttl > 0 )
		{
			//Decrement ttl by one
			((Message) ev).ttl--;

			send(sendNext, ev, _now);

			System.out.println( this + " sends to node: " + 
								((Message) ev).destination().networkId() + 
								"." + 
								((Message) ev).destination().nodeId() +
								" through interface " + getLinkPlacement(sendNext)
							  );
		}
		//TODO should we realy send to all interfaces?
		else if ( ((Message) ev).ttl > 0 )
		{
			//Decrement ttl by one
			((Message) ev).ttl--;

			System.out.println( this + " forwards message to unknown address to all interfaces. ");

			for(int i = 0; i < _interfaces; i++)
			{
				//Do not send packets back to the sender.
				if(_interface[i] != src)
				{
					//System.out.println(src + " -- " + _interface[i]);
					send(_interface[i], ev, _now);
				}
				else {
					System.out.println("Skips interface " + getLinkPlacement(src));
				}
			}
		}

		//Check if the sender is known (check all interfaces). if not add it to the routing table
		if ( getLink( ((Message) ev).source().networkId(), -1 ) != src )
		{
			System.out.println( this + " adds node: "+((Message) ev).source().networkId()+"." + ((Message) ev).source().nodeId() + " at interface: " + getLinkPlacement(src));
			
			addTableEntry(getLinkPlacement(src), ((Message) ev).source());
		}
	}

	private void forwardMsg(SimEnt next, Event ev, double delay)
	{
			send(next, ev, delay);
	}
}
