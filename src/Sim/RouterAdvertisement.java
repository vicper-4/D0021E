package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class RouterAdvertisement extends Message{

	RouterAdvertisement(NetworkAddr to)
	{
		super(null,to,0);
	}
}
	
