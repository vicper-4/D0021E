package Sim;

public class HomeAgent extends Node {
	private class AddressEntry {
		public NetworkAddr _address;
		public NetworkAddr _homeAddress;

		public double _valid;
		public double _preferred;

		public AddressEntry _next;

		public MessageBuffer buffer;

		public AddressEntry(NetworkAddr address, NetworkAddr homeAddress, double valid, double preferred)
		{
			_address = address;
			_homeAddress = homeAddress;
			_valid = valid;
			_preferred = preferred;
			buffer = new MessageBuffer(10); // Consider setting buffer size somewhere else
		}
		
		public boolean isRegistered(NetworkAddr destination)
		{
			if (_homeAddress.equals(destination))
			{
				return true;
			}
			else if ( _next != null )
			{
				return _next.isRegistered(destination);
			}
			else
			{
				return false;
			}
		}

		public NetworkAddr getRedirAddr(NetworkAddr homeAddr)
		{
			double time = SimEngine.getTime();
			
			if ((_homeAddress.equals(homeAddr)) && (_valid >= time))
			{
				return _address;
			}
			else if (_next != null)
			{
				return _next.getRedirAddr(homeAddr);
			}
			else
			{
				return null;
			}
		}
		
		public AddressEntry setRedirAddr(NetworkAddr homeAddr, NetworkAddr redirAddr)
		{
			double time = SimEngine.getTime();
			
			if ((_homeAddress.equals(homeAddr)) && (_valid >= time))
			{
				_address = redirAddr;
				System.out.println("HA " + _id.toString() + 
								   " recived bind update from registered node " 
								   + redirAddr.toString());
				
				return this;

			}
			else if (_next != null)
			{
				return _next.setRedirAddr(homeAddr, redirAddr);
			}
			else
			{
				//TODO Send NACK
				System.out.println("HA " + _id.toString() + 
								   " recived bind update from non-registered node " 
								   + redirAddr.toString() +
								   ". Ignores it.");

				return null;
			}
		}
		
		public void addOrUpdate(NetworkAddr src, double valid, double preferred)
		{
			if ( _homeAddress.equals(src) || _address.equals(src) )
			{
				//TODO check that the new times have sensable values i.e. not
				//in the past. 
				_valid = valid;
				_preferred = preferred;

				System.out.println("HA updates entry for " + src.toString()); 
			}
			else if ( _next != null )
			{
				_next.addOrUpdate(src, valid, preferred);
			}
			else
			{
				_next = new AddressEntry ( null, src, valid, preferred);
			}
		}

		public void bufferMsg(NetworkAddr destination, Message msg)
		{
			if (_homeAddress.equals(destination))
			{
				buffer.addMsg(msg);
			}
			else if ( _next != null )
			{
				_next.bufferMsg(destination, msg);
			}
			else
			{
				//Maybe print something to indicate that the a message to a
				//non-registered node was recived
			}
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
		else if (ev instanceof BindUpdate)
		{
			recvBindUpdate(ev);
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
		NetworkAddr destination = ((Message) ev).destination();

		if (addrList != null)
		{
			NetworkAddr redirAddr = addrList.getRedirAddr( destination );

			if (redirAddr != null)
			{
				send(_peer, new RedirMsg( _id, redirAddr, 0, ev), 0);

				System.out.println( "HA " + _id.toString() + 
									" recives message intended for registerd mobile node " +
									redirAddr.toString() +
									". Redirects it."
								  );
			}
			else if ( addrList.isRegistered(destination) )
			{
				System.out.println( "HA " + _id.toString() + 
									" recives message intended for registerd mobile node." +
									" Tries to buffer it."
								  );
				
				addrList.bufferMsg( destination, (Message)ev );
			}
		}
	}

	private void recvBindUpdate(Event ev)
	{
		MessageBuffer buffer = null;

		if (addrList != null)
		{
			buffer = addrList.setRedirAddr(	((BindUpdate) ev).getDeprecated(),
									((Message) ev).source()
								 ).buffer;
		}
		else
		{
			System.out.println( "HA " + _id.toString() + 
								"recived BindUpdate from " +
								((Message) ev).source().toString() +
								", but has no registered MNs"
							  );
		}

		if (buffer != null)
		{
			Message tmpMsg = buffer.popMsg();
			while (tmpMsg != null)
			{
				send(_peer, new RedirMsg( _id, ((Message) ev).source(), 0, tmpMsg), 0);
				tmpMsg = buffer.popMsg();
			}
		}
	}

	private void recvRegReq(Event ev)
	{
		System.out.println( "HA " + _id.toString() + 
							" recives request to register mobile node " + 
							((RegReq) ev).source().toString()
						  );

		if ( addrList != null )
		{
			addrList.addOrUpdate( ((RegReq) ev).source(), 
								  ((RegReq) ev)._valid, 
								  ((RegReq) ev)._preferred
								);
		}
		else
		{
			addrList = new AddressEntry(null, 
										((RegReq) ev).source(), 
										((RegReq) ev)._valid, 
										((RegReq) ev)._preferred
									   );
		}
		
		System.out.println("Sends bind update to router");

		send(_peer, 
			 new BindUpdate( ((Message) ev).source(), 
			 				 _broadcast, 10, 0, _id),
			 0
			);
	}
}
