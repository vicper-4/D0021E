package Sim;

public class RegReq extends Message {
	public double _valid;
	public double _preferred;

	RegReq (NetworkAddr from, NetworkAddr to, int seq, double valid, double preferred)
	{
		super(from, to, seq);
		_valid = valid;
		_preferred = preferred;
	}
}
