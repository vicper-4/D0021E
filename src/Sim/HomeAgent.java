package Sim;

public class HomeAgent extends Node {
	private class AddressEntry {
		public NetworkAddr _address;

		public double _valid;
		public double _preferred;

		public AddressEntry _next;

		public AddressEntry(NetworkAddr address, double valid, double preferred)
		{
			_address = address;
			_valid = valid;
			_preferred = preferred;
		}
	}

	private AddressEntry addrList;

	public HomeAgent (int network, int node, Sink sink)
	{
		super(network, node, sink);
		addrList = null;
	}

	@Override
	public void recv(SimEnt src, Event ev)
	{
		//TODO add RA handling
		if (ev instanceof RegReq)
		{
			recvRegReq(ev);
		}
		else if (ev instanceof Message)
		{
			recvMsg(ev);
		}
	}
	
	private void recvMsg(Event ev)
	{
		double time = SimEngine.getTime();
		AddressEntry tmpAddr = addrList;
		AddressEntry redirAddr = null;

		while(tmpAddr != null)
		{
			if ( (tmpAddr._address.nodeId() == ((Message) ev).destination().nodeId()) &&
				 (tmpAddr._valid > time)
			   )
			{
				if (redirAddr == null) {
					redirAddr = tmpAddr;
				} else if ( (tmpAddr._preferred > redirAddr._preferred) || 
							( (tmpAddr._valid > redirAddr._valid) && 
							  (redirAddr._preferred < time)
							)
						  ) {
					redirAddr = tmpAddr;
				}
			}
			tmpAddr = tmpAddr._next;
		}

		send(_peer, new RedirMsg( _id, redirAddr._address, 0, ev), 0);
	}

	private void recvRegReq(Event ev)
	{
		AddressEntry entry = new AddressEntry( ((RegReq) ev).source(), 
											   ((RegReq) ev)._valid, 
											   ((RegReq) ev)._preferred
											 );
		entry._next = addrList;
		addrList = entry;
	}
}
