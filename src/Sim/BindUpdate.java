package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class BindUpdate extends Message{
	private NetworkAddr _deprecatedId;

	BindUpdate (NetworkAddr from, NetworkAddr to, int seq, NetworkAddr deprecated)
	{
		super(from,to,seq);
		_deprecatedId = deprecated;
	}
}
	
