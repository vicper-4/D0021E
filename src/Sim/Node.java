package Sim;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Node extends SimEnt {
	// TODO check that this shit is optimal:
	private static int _recv;
	private static double totalTransit;
	private static double jitter;
	// end

	private NetworkAddr _id;
	private SimEnt _peer;
	private int _sentmsg=0;
	private int _seq = 0;

	
	public Node (int network, int node)
	{
		super();
		_id = new NetworkAddr(network, node);
	}	
	
	
	// Sets the peer to communicate with. This node is single homed
	
	public void setPeer (SimEnt peer)
	{
		_peer = peer;
		
		if(_peer instanceof Link )
		{
			 ((Link) _peer).setConnector(this);
		}
	}
	
	
	public NetworkAddr getAddr()
	{
		return _id;
	}
	
//**********************************************************************************	
	// Just implemented to generate some traffic for demo.
	// In one of the labs you will create some traffic generators
	
	private int _stopSendingAfter = 0; //messages
	private int _timeBetweenSending = 10; //time between messages
	private int _toNetwork = 0;
	private int _toHost = 0;

	public void StartSending(int network, int node, int number, int timeInterval, int startSeq)
	{
		_stopSendingAfter = number;
		_timeBetweenSending = timeInterval;
		_toNetwork = network;
		_toHost = node;
		_seq = startSeq;
		send(this, new TimerEvent(),0);	
	}

//**********************************************************************************
	
	// This method is called upon that an event destined for this node triggers.
	
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof TimerEvent)
		{			
			if (_stopSendingAfter > _sentmsg)
			{
				_sentmsg++;
				send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost),_seq),0);
				send(this, new TimerEvent(),_timeBetweenSending);
				SimEngine.msgSent();

				// Presentation:
				System.out.println("Node " + _id.networkId() + "."
						+ _id.nodeId() + " sent message with seq: " + _seq + " at time " + SimEngine.getTime());
				_seq++;
			}
		}
		if (ev instanceof Message)
		{
			double currTime = SimEngine.getTime();
			System.out.println("Node " + _id.networkId() + "." + _id.nodeId()
					+ " receives message with seq: " + ((Message) ev).seq()
					+ " at time " + SimEngine.getTime()
					+ " It took " + (currTime-((Message) ev).timeSent) + " ms.");

			// TODO: move this functionality to this class.
			msgRecv(currTime - ((Message) ev).timeSent);
		}
	}

	/**
	 * Called when a node recives a message
	 *
	 * @param tt The transit time of the message
	 */
	public static void msgRecv(double tt) {
		_recv++;
		totalTransit += tt;

		//Algorithm from RFC1889 A.8
		double d = tt - totalTransit / _recv;
		if (d < 0) d = -d;
		jitter += (1.0 / ((double) _recv)) * (d - jitter);

		System.out.println(":: Current average jitter: " + jitter + "ms");
	}
}
