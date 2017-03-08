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

	// First entry in list of registered addresses. 
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
		else if (ev instanceof RouterAdvertisement)
		{
			super.recvRouterAdvertisement();
		}
		else if (ev instanceof RedirMsg)
		{
			recvRedir(ev);
		}
		else if (ev instanceof Message)
		{
			recvMsg(ev);
		}
	}

	private void recvRedir(Event ev)
	{
		//TODO check that the sender is registered
		System.out.println("HA " +_id.toString() + " recives message from registerd mobile node");
		send(_peer, ((RedirMsg) ev).getOriginal(), 0);
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

		if (redirAddr != null)
		{
			send(_peer, new RedirMsg( _id, redirAddr._address, 0, ev), 0);

			System.out.println( "HA " + _id.toString() + 
								" recives message intended for registerd mobile node " +
								redirAddr._address.toString()
							  );
		}
	}

	private void recvRegReq(Event ev)
	{
		System.out.println( "HA " + _id.toString() + 
							" recives request to register mobile node " + 
							((RegReq) ev).source().toString()
						  );

		AddressEntry entry = new AddressEntry( ((RegReq) ev).source(), 
											   ((RegReq) ev)._valid, 
											   ((RegReq) ev)._preferred
											 );
		entry._next = addrList;
		addrList = entry;
		
		System.out.println("Sends bind update to router");

		send(_peer, 
			 new BindUpdate( new NetworkAddr(_id.networkId(), ((Message) ev).source().nodeId()), 
			 				 ((Message) ev).source(), 0, 0, _id),
			 0
			);
	}
}
