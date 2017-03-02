package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class BindUpdate extends Message{
	final double timeSent;
	private NetworkAddr _source;
	private NetworkAddr _destination;
	private int _seq=0;

	BindUpdate (NetworkAddr from, NetworkAddr to, int seq)
	{
		super(from,to,seq);
		//super(from, to, seq);
		timeSent = SimEngine.getTime();
		_source = from;
		_destination = to;
		_seq=seq;
	}
	
	public NetworkAddr source()
	{
		return _source; 
	}
	
	public NetworkAddr destination()
	{
		return _destination; 
	}
	
	public int seq()
	{
		return _seq; 
	}

	public void entering(SimEnt locale)
	{
	}
}
	
