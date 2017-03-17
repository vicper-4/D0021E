package Sim;

public class RedirMsg extends Message {
	Event _msg;

	RedirMsg (NetworkAddr from, NetworkAddr to, int seq, Event msg)
	{
		super(from, to, seq);
		_msg = msg;
	}

	public Event getOriginal()
	{
		return _msg;
	}
}
