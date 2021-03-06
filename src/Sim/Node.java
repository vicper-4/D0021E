package Sim;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Node extends SimEnt {


	protected NetworkAddr _id;
	private NetworkAddr _deprecated_id;
	private NetworkAddr homeAgent;
	protected SimEnt _peer;
	private int _sentmsg=0;
	private int _seq = 0;
	private Generator gen;
	private Sink sink;
	private boolean _assignedRouter = false;
	NetworkAddr _localBroadcast = null;


	public Node (int network, int node, Sink sink)
	{
		super();
		_id = new NetworkAddr(network, node);
		this.sink = sink;
		_localBroadcast = new NetworkAddr(_id.networkId(),0xff);
	}

	/**
	 * Set a new id (network address / ip address)
	 * @param _id   the id to update to.
	 */
	public void update_id(NetworkAddr _id) {
		this._deprecated_id = this._id;
		this._id = _id;
		_localBroadcast = new NetworkAddr(_id.networkId(),0xff);

		// Send Bind Update to closest router
		sendBindUpdate(_localBroadcast, 0);

		// Register with HomeAgent. TODO should probably check so that it has a registered router
		if (homeAgent != null)
		{
			// TODO Send a bindUpdate to HomeAgent, not regReq
			sendBindUpdate(homeAgent, 4);
		}
	}

	public void setHomeAgent(NetworkAddr homeAgent)
	{
		this.homeAgent = homeAgent;
	}

	// Sets the peer to communicate with. This node is single homed
	public void setPeer (SimEnt peer)
	{
		if( peer instanceof Link &&
			this._peer == null )
		{
			this._peer = peer;
			((Link) _peer).setConnector(this);

			//TODO only for testing, should be removed
			// Gives node new net address to simulate a move between nets
			if(_sentmsg >0){
				update_id(new NetworkAddr(7,this._id.nodeId()));
				System.out.printf("Node %s has moved, setting new net address to %s,"
						+ "status of _assignedRouter is %s %n",
						_deprecated_id, _id, _assignedRouter);
				send(this, new TimerEvent(), 0); // Makes sure that the eventloop for this Node does not stop after a move if all messages are sent.
			}
		}
	}

	/**
	 * Disconnects the link.
	 */
	public void unsetPeer(SimEnt peer)
	{
		if(peer instanceof Link &&
		   peer == this._peer )
		{
			// Register with Home Agent so it can start buffering messages
			sendRegReq();

			// Unset the link
			((Link) _peer).unsetConnector(this);
			this._peer = null;

			_assignedRouter = false;
			System.out.println("-- Node " + _id + " disconnects interface from link and drops assigned router");
		}
	}

	public NetworkAddr getAddr()
	{
		return _id;
	}

	public NetworkAddr getDepr()
	{
		return _deprecated_id;
	}
	
//**********************************************************************************	
	// Just implemented to generate some traffic for demo.
	// In one of the labs you will create some traffic generators
	//TODO look over startSending (now up). Should stuff be moved to generator?
	
	private int _stopSendingAfter = 0; //messages
	private int _timeBetweenSending = 10; //time between messages
	private int _toNetwork = 0;
	private int _toHost = 0;

	public void up(int network, int node, int number, Generator gen, int startSeq)
	{
		_stopSendingAfter = number;
		this.gen = gen;
		_toNetwork = network;
		_toHost = node;
		_seq = startSeq;
		startSending();
	}

	private void startSending()
	{
		send(this, new TimerEvent(),0);
	}

	/**
	 * Checks if the message is for this node and should be accepted.
//	 * @param destination Destination address of the message.
	 * @return a boolean
	 */
	/* Method not fully implemented
	protected boolean acceptMsg(NetworkAddr destination)

	{
		int netId = destination.networkId();
		int nodeId = destination.nodeId();

		switch (netId) {
			case _localBroadcast.networkId():
				return true;
			case _
		}

		return ( netId == _localBroadcast.networkId() ||
				 net == _
	}
	*/

//**********************************************************************************
	
	// This method is called upon that an event destined for this node triggers.
	
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof RouterAdvertisement)
		{
			recvRouterAdvertisement();
		}
		else if (ev instanceof TimerEvent)
		{
			recvTimerEvent();
		}
		else if (ev instanceof BindUpdate)
		{
			recvBindUpdate( ((BindUpdate) ev).source() );
		}
		else if (ev instanceof RedirMsg)
		{
			recvRedirMsg(ev);
		}
		else if (ev instanceof Message)
		{
			recvMsg(ev);
		}
	}

	/**
	 * When a timer event is received (i.e. one clock tick) it checks for an assigned router.
	 * If no router has been assigned it will send a router solicitation, otherwise it will send
	 * the next message in the queue.
	 */
	private void recvTimerEvent()
	{
		// Check for router handshake
		if (!_assignedRouter) {
			System.out.printf("%n?? Node %s knows of no router, sends RouterSolicitation\n", _id);
			send(_peer, new RouterSolicitation(_id, _broadcast,1), 0);
			send(this, new TimerEvent(), 2);
		}
		else if (_stopSendingAfter > _sentmsg)
		{
			_sentmsg++;

			// TODO sending of messages should be factored out into own method.
			send(_peer, new Message(_id, new NetworkAddr(_toNetwork,
														 _toHost),
									_seq), 0);
			send(this, new TimerEvent(),gen.delay());
			SimEngine.msgSent(); // Report to SimEngine that a message has been sent.

			// Presentation:
			System.out.printf("Node %s sent message with seq: %d at time %s%n",
					_id, _seq, SimEngine.getTime());
			_seq++;
		}
	}

	/**
	 * Updates the state of _assignedRouter and sends a bindAck back to the router (TODO does it really work like this?)
	 */
	void recvRouterAdvertisement()
	{
		System.out.printf("%n!! Node %s received RouterAdvertisement %n",_id);

		if (!_assignedRouter)
		{
			_assignedRouter = true;
			// TODO refactor (or overload) sendBindAck so that can be used instead.
			send(_peer, new BindAck(_id, _localBroadcast, 11, 0), 0);
		}
	}

	/**
	 * Passes messages to the sink and keeps track of if a BindUpdate should be sent
	 * @param ev    received message to be handled
	 */
	private void recvMsg(Event ev)
	{
		// Make time calculations
		double currTime = SimEngine.getTime();
		double tt = currTime - ((Message) ev).timeSent;
		// TODO Should SimEnt still calculate jitter?
		calculateJitter(tt);

		sink.recv((Message)ev, currTime);   // Pass message to sink
		SimEngine.msgRecv(tt, getJitter()); // Report to SimEngine that a message has been received. TODO should SimEngine.msgRecv() be refactored?

		System.out.printf("Node %d.%d receives message with seq: %d"
						+ " at time %f. Transport time was: %f ms %n",
				_id.networkId(),
				_id.nodeId(),
				((Message) ev).seq(),
				currTime,
				tt);

		// If message received was sent to deprecated address
		if (_deprecated_id != null) {
			if ((((Message) ev).destination().networkId() == this._deprecated_id.networkId())
					&& (((Message) ev).destination().nodeId() == this._deprecated_id.nodeId())) {
				System.out.printf("Node " + _id.toString() + " received message to deprecated address %n");
			}
		}
	}

	/**
	 * Called when a message that has been redirected from an Home Agent is received.
	 * Will unpack the encapsulated message and forward it to itself.
	 * @param ev encapsulated message from Home Agent.
	 */
	private void recvRedirMsg(Event ev)
	{
		// should do something like below IF you want route optimisation.
		// Is currently checked for in recvMsg(). Do note this code is deprecated and won't work no more

		// sendBindUpdate(((Message) ((RedirMsg) ev).getOriginal()).source());

		send(this, ((RedirMsg) ev).getOriginal(), 0);
	}

	/**
	 * Prints various statistics about this node.
	 */
	public void printStat()
	{
		System.out.printf("Sent: %-4d  |  Received: %d %n", _sentmsg, sink.getReceived());
		System.out.printf("Average period: %fms %n", sink.getAvgrPeriod());
		System.out.printf("Average delay: %fms %n", sink.getAvgrDelay());
		System.out.printf("Average jitter: %fms %n", sink.getAvgrJitter());
		//System.out.printf("Deviation from average period, counting only early: %fms %n", sink.getAvgrNegativePeriodDeviation());
		//System.out.printf("Deviation from average period, counting only late: %fms %n", sink.getAvgrPossitivePeriodDeviation());
		//System.out.printf("Delay: %fms %n", sink.getDelay());
		//System.out.printf("Jitter: %fms %n", sink.getJitter());
		//System.out.printf("Time since last received message: %fms %n", sink.getPeriod());
		//System.out.printf("Deviation from average period: %fms %n", sink.getPeriodDeviation());

		System.out.println();
	}

	/**
	 * Sends an encapsulated message which will be unpacked at destination
	 * and forwarded
	 * @param forwarder address of the node that will forward the encapsulated message
	 * @param seq       sequence number
	 * @param message   message to be forwarded
	 */
	private void sendRedirMsg (NetworkAddr forwarder, int seq, Message message)
	{
		int delay = 0;

		send(_peer,
				new RedirMsg(_id, forwarder, seq, message),
				delay);
	}

	/**
	 * Sends a Bind Update.
	 * @param destination   the record to be updated
	 * @param ttl           Time To Live
	 */
	private void sendBindUpdate(NetworkAddr destination, int ttl) //TODO should take a TTL
	{

		//generate a new message to the sender
		int delay = 0;
		int seq   = 10; // bad hack to indicate it's a BindUpdate

		send(_peer,
				new BindUpdate(_id,
						destination,
						seq,
						ttl,
						_deprecated_id),
				delay);
	}

	/**
	 * Update this nodes record of another nodes network address
	 * @param newAddr the new address of remote node
	 */
	private void recvBindUpdate(NetworkAddr newAddr)
	{
		System.out.println("recvBindUpdate on " + _id.networkId() + "." + _id.nodeId());
		_toNetwork = newAddr.networkId();
		_toHost = newAddr.nodeId();
		sendBindAck();
	}

	/**
	 * Should be sent on receiving a BindUpdate
	 */
	private void sendBindAck()
	{
		// generate ack to sender
		int delay = 0;
		int seq   = 11; // Bad way to see it's a BindAck

		send(_peer,
				new BindAck(_id,
						new NetworkAddr(_toNetwork, _toHost),
						seq),
				delay);

		System.out.println("Node" + _id +" sends Bind Ack.");
	}

	/**
	 * Sends a registration request to a default preconfigured
	 */
	private void sendRegReq()
	{
		final int seq = 20; //For easy identification of RegReqs.
		final double validTime = SimEngine.getTime() + 100;
		final double preferredTime = SimEngine.getTime() + 150;

		System.out.printf("Node %s sends RegReq to %s%n", _id, homeAgent);

		send(_peer,
				new RegReq(_id,
						homeAgent,
						seq,
						validTime,
						preferredTime),
				0);
	}

	private void sendRegReq(NetworkAddr homeAgent, int seq, int validTime, int preferredTime)
	{
		send(_peer,
				new RegReq(_id, homeAgent, seq, validTime, preferredTime),
				0);
	}
}
