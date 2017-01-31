package Sim;

// This class represent the network address, it consist of a network identity
// "_networkId" represented as an integer (if you want to link this to IP number it can be
// compared to the network part of the IP address like 132.17.9.0). Then _nodeId represent
// the host part.

public class NetworkAddr {
	private int _networkId;
	private int _nodeId;
	
	NetworkAddr(int network, int node)
	{
		_networkId=network;
		_nodeId=node;
	}
	
	public int networkId()
	{
		return _networkId;
	}
	
	public int nodeId()
	{
		return _nodeId;
	}
}
