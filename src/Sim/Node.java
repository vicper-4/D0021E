package Sim;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Node extends SimEnt {

	/**
	 * Set a new id (network address / ip address)
 	 * @param _id
	 */
	public void set_id(NetworkAddr _id) {
		this._deprecated_id = this._id;
		this._id = _id;
	}

	private NetworkAddr _id;
	private NetworkAddr _deprecated_id;
	private SimEnt _peer;
	private int _sentmsg=0;
	private int _seq = 0;
	private Generator gen;
	private Sink sink;
	private boolean _assignedRouter = false;


	public Node (int network, int node, Sink sink)
	{
		super();
		_id = new NetworkAddr(network, node);
		this.sink = sink;
	}
	
	
	// Sets the peer to communicate with. This node is single homed
	
	public void setPeer (SimEnt peer)
	{
		if( peer instanceof Link &&
			this._peer == null )
		{
			this._peer = peer;
			((Link) _peer).setConnector(this);
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
			((Link) _peer).unsetConnector(this);
			this._peer = null;
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
	//TODO look over start sending. Should stuff be moved to generator?
	
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
		//startSending();
	}

	private void startSending()
	{
		send(this, new TimerEvent(),0);
	}

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
		else if (ev instanceof Message)
		{
			recvMsg(ev);
		}
	}

	// TODO Javadoc
	private void recvTimerEvent()
	{
		// TODO check for router handshake
		if (!_assignedRouter) {
			System.out.printf("%n?? Node %d.%d knows of no router, sends RouterSolicitation\n",_id.networkId(), _id.nodeId());
			send(_peer, new RouterSolicitation(_id, _broadcast,1), 0);
			send(this, new TimerEvent(), 2);
		}
		else if (_stopSendingAfter > _sentmsg)
		{
			_sentmsg++;
			send(_peer, new Message(_id, new NetworkAddr(_toNetwork,
														 _toHost),
									_seq), 0);
			send(this, new TimerEvent(),gen.delay());
			SimEngine.msgSent(); // Report to SimEngine that a message has been sent.

			// Presentation:
			System.out.println("Node " + _id.networkId() + "."
					+ _id.nodeId() + " sent message with seq: " + _seq
					+ " at time " + SimEngine.getTime());
			_seq++;
		}

	}

	private void recvRouterAdvertisement()
	{
		System.out.printf("%n!! Node %d.%d received RouterAdvertisement %n",_id.networkId(), _id.nodeId());
		_assignedRouter = true;
		startSending();
	}

	/** TODO Javadoc
	 * Passes messages to the sink and keeps track of if a BindUpdate should be sent
	 * @param ev  TODO
	 */
	private void recvMsg(Event ev)
	{
		// Make calculations
		double currTime = SimEngine.getTime();
		// TODO Should Node still calculate jitter?
		double tt = currTime - ((Message) ev).timeSent;
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

		// TODO block should probably be removed
		// Set message sender as target of new messages
		// recvBindUpdate( ((Message) ev).source() );

		// If message received was sent to deprecated address,
		// give sender my current address.
		if (_deprecated_id != null) {
			if ((((Message) ev).destination().networkId() == this._deprecated_id.networkId())
					&& (((Message) ev).destination().nodeId() == this._deprecated_id.nodeId())) {
				sendBindUpdate(((Message) ev).source());
			}
		}

	}

	// TODO? Javadoc
	public void printStat()
	{
		//System.out.printf("Time since last received message: %fms %n", sink.getPeriod());
		//System.out.printf("Deviation from average period: %fms %n", sink.getPeriodDeviation());
		System.out.printf("Average period: %fms %n", sink.getAvgrPeriod());
		System.out.printf("Deviation from average period, counting only early: %fms %n", sink.getAvgrNegativePeriodDeviation());
		System.out.printf("Deviation from average period, counting only late: %fms %n", sink.getAvgrPossitivePeriodDeviation());
		//System.out.printf("Delay: %fms %n", sink.getDelay());
		System.out.printf("Average delay: %fms %n", sink.getAvgrDelay());
		//System.out.printf("Jitter: %fms %n", sink.getJitter());
		System.out.printf("Average jitter: %fms %n", sink.getAvgrJitter());
	}

	/**
	 * Sends a Bind Update to last sender to update its record of the network address of this node.
	 * @param sender the record to be updated
	 */
	private void sendBindUpdate(NetworkAddr sender)
	{
		_toNetwork = sender.networkId();
		_toHost = sender.nodeId();

		//generate a new message to the sender
		int delay = 0;
		int seq   = 0;
		send(_peer,
			 new BindUpdate(_id,
						 new NetworkAddr(_toNetwork, _toHost),
						 seq,
					     _deprecated_id),
			 delay);

		System.out.printf("Link received message to deprecated address,"
				+ " new address sent to sender %n");
	}

	/**
	 * Update this nodes record of another nodes network address
	 * @param newAddr the new address of remote node
	 */
	private void recvBindUpdate(NetworkAddr newAddr)
	{
		// if real BindAck it would also send an ack to the mobile node
		System.out.println("recvBindUpdate on " + _id.networkId() + "." + _id.nodeId());
		_toNetwork = newAddr.networkId();
		_toHost = newAddr.nodeId();
		sendBindAck();
	}

	// TODO Javadoc
	private void sendBindAck()
	{

		// generate ack to sender
		int delay = 0;
		int seq   = 0;
		send( _peer,
			new BindAck( _id,
						new NetworkAddr( _toNetwork, _toHost ),
						seq ),
			delay );
	}
}
