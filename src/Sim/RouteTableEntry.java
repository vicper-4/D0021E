package Sim;

// This class represent a routing table entry by including
// the link connecting to an interface as well as the node 
// connected to the other side of the link

public class RouteTableEntry extends TableEntry{
	RouteTableEntry(int _interface, NetworkAddr _address)
	{
		super(_interface, _address);
	}
	
	public int getInterface()
	{
		return super.getNic();
	}

	public int getAddress()
	{
		return super.getAddr().networkId();
	}
}
