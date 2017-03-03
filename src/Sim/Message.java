package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class Message implements Event{
	final double timeSent;
	private NetworkAddr _source;
	private NetworkAddr _destination;
	private int _seq=0;
	int ttl = 4; // default value for ttl

	Message (NetworkAddr from, NetworkAddr to, int seq)
	{
		timeSent = SimEngine.getTime();
		_source = from;
		_destination = to;
		_seq=seq;
	}

	public Message(NetworkAddr _source, NetworkAddr _destination, int _seq, int ttl) {
		timeSent = SimEngine.getTime();
		this._source = _source;
		this._destination = _destination;
		this._seq = _seq;
		this.ttl = ttl;
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
	
