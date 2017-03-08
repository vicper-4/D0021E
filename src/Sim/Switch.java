//TODO Reimplement Switch as Router was with the new TableEntry
package Sim;

// This class implements a simple switch

public class Switch extends SimEnt{

	private SwitchTableEntry _switchTable;
	private int _ports;
	private SimEnt [] _port;
	private final int _now = 0;
	
	// When creating the switch, the number of ports must be specified
	Switch(int ports)
	{
		_port = new SimEnt[ports];
		_ports=ports;
	}
	
	// This method connects links to the switch and also informs the 
	// switch of the host connects to the other end of the link
	
	public void connectPort(int portNumber, SimEnt link)
	{
		if (portNumber<_ports && _port[portNumber] == null)
		{
			_port[portNumber] = link;
			((Link) link).setConnector(this);
		}
		else
			System.out.println("Trying to connect to port not in switch");
		
	}

	/**
	* This method disconnects links from the switch and also removes switch
	* table information about what is connected to the other end of that link
	*/
	public void disconnectPort(SimEnt link)
	{
		for(int i=0; i<_ports; i++)
		{
			if (link == _port[i])
			{
				_port[i] = null;
				((Link) link).unsetConnector(this);
			}
		}
	}

	// This method searches for an entry in the switch-table that matches
	// the host number in the destination field of a frame. The link
	// that connects host the switch port is returned 
	
	private int getPort(int nodeAddress, int srcPort)
	{
		SwitchTableEntry entry = (SwitchTableEntry)_switchTable;

		while(entry != null)
		{
			if ( entry.getId() == nodeAddress && entry.getPort() != srcPort)
				return entry.getPort();

			entry = (SwitchTableEntry)entry.getNext();
		}

		return -1;
	}

	private SimEnt getLink(int nodeAddress, int srcPort)
	{
		int i = getPort(nodeAddress, srcPort);

		if ( i >= 0 && i < _ports )
			return _port[i];

		return null;
	}

	public void addTableEntry(int _interface, NetworkAddr _address)
	{
		SwitchTableEntry newEntry = new SwitchTableEntry(_interface, _address);
		
		newEntry.setNext(_switchTable);
		_switchTable = newEntry;
	}

	private int getLinkPlacement(SimEnt link)
	{
		for ( int i = 0; i < _ports; i++ )
		{
			if ( _port[i] == link )
				return i;
		}

		return -1;
	}
	
	// Called when a frame is received by the switch
	
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof Message)
		{
			System.out.println("Switch handles frame from port: " + getLinkPlacement(src));
		
			SimEnt sendNext = null;
			if (((Message) ev).destination() != null )
				sendNext = getLink(((Message) ev).destination().nodeId(), getLinkPlacement(src));
			
			if (sendNext != null)
			{
				System.out.println("Switch forwards to host: " + ((Message) ev).destination().nodeId());

				if (sendNext != src)
				{
					send (sendNext, ev, 0);
				}
			}
			else
			{
				System.out.println( this + " does not know of recipient. Sends frame to all ports. ");
				for(int i = 0; i < _ports; i++)
				{
					//Do not send packets back to the sender.
					if(_port[i] != src)
						send(_port[i], ev, _now);
					else
						System.out.println( "Skipps port " + getLinkPlacement(src));
				}
			}

			if ( ((Message) ev).source() != null )
			{
				if ( getLink( ((Message) ev).source().nodeId(), -1) != src )
				{
					System.out.println( this + " adds node: "+((Message) ev).source().networkId()+"." + ((Message) ev).source().nodeId() + " at interface: " + getLinkPlacement(src));

					addTableEntry(getLinkPlacement(src), ((Message) ev).source());
				}
			}
		}
	}
}
