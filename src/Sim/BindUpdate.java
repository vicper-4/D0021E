package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class BindUpdate extends Message{

	BindUpdate (NetworkAddr from, NetworkAddr to, int seq)
	{
		super(from,to,seq);
	}
}
	
