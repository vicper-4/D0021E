package Sim;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Node extends SimEnt {

	private NetworkAddr _id;
	private SimEnt _peer;
	private int _sentmsg=0;
	private int _seq = 0;
	private Generator gen;
    private Sink sink;

	
	public Node (int network, int node, Sink sink)
	{
		super();
		_id = new NetworkAddr(network, node);
        this.sink = sink;
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

	public void StartSending(int network, int node, int number, Generator gen, int startSeq)
	{
		_stopSendingAfter = number;
		this.gen = gen;
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
				send(_peer, new Message(_id, new NetworkAddr(_toNetwork,
															 _toHost),
										_seq), 0);
				send(this, new TimerEvent(),gen.nextSend());
				SimEngine.msgSent(); // Report to SimEngine that a message has been sent.

				// Presentation:
				System.out.println("Node " + _id.networkId() + "."
						+ _id.nodeId() + " sent message with seq: " + _seq + " at time " + SimEngine.getTime());
				_seq++;
			}
		}
		if (ev instanceof Message)
		{
			double currTime = SimEngine.getTime();
            double tt = currTime - ((Message) ev).timeSent;

            sink.recv((Message)ev, currTime);

			calculateJitter(tt);

			System.out.println("Node " + _id.networkId() + "." + _id.nodeId()
					+ " receives message with seq: " + ((Message) ev).seq()
					+ " at time " + currTime
					+ " It took " + (tt) + " ms.");
            printStat();

			SimEngine.msgRecv(tt, getJitter()); // Report to SimEngine that a message has been received.
		}
	}

    public void printStat()
    {
        System.out.printf("Time since last recived message: %fms %n", sink.getPeriod());
        System.out.printf("Deviation from avrage period: %fms %n", sink.getPeriodDeviation());
        System.out.printf("Avrage period: %fms %n", sink.getAvgrPeriod());
        System.out.printf("%n");
    }
}
