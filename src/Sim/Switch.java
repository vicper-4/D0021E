package Sim;

// This class implements a simple switch

public class Switch extends SimEnt{

	private SwitchTableEntry [] _switchTable;
	private int _ports;
	
	// When creating the switch, the number of ports must be specified
	
	Switch(int ports)
	{
		_switchTable = new SwitchTableEntry[ports];
		_ports=ports;
	}
	
	// This method connects links to the switch and also informs the 
    // switch of the host connects to the other end of the link
	
	public void connectPort(int portNumber, SimEnt link, SimEnt node)
	{
		if (portNumber<_ports)
		{
			_switchTable[portNumber] = new SwitchTableEntry(link, node);
		}
		else
			System.out.println("Trying to connect to port not in switch");
		
		((Link) link).setConnector(this);
	}

	// This method searches for an entry in the switch-table that matches
	// the host number in the destination field of a frame. The link
	// that connects host the switch port is returned 
	
	private SimEnt getPort(int nodeAddress)
	{
		SimEnt port=null;
		for(int i=0; i<_ports; i++)
			if (_switchTable[i] != null)
			{
				if (((Node) _switchTable[i].node()).getAddr().nodeId() == nodeAddress)
				{
					port = _switchTable[i].link();
				}
			}
		return port;
	}
	
	
	// Called when a frame is received by the switch
	
	public void recv(SimEnt source, Event event)
	{
		if (event instanceof Message)
		{
			System.out.println("Switch handles frame with seq: " + ((Message) event).seq() + " from node: "+ ((Message) event).source().nodeId());
			SimEnt sendNext = getPort(((Message) event).destination().nodeId());
			System.out.println("Switch forwards to host: " + ((Message) event).destination().nodeId());		
			send (sendNext, event, 0);
	
		}	
	}
}
