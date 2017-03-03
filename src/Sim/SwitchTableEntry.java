package Sim;

//This class represent a routing table entry by including
//the link connecting to an interface as well as the node 
//connected to the other side of the link


public class SwitchTableEntry extends TableEntry{

	SwitchTableEntry(int _port, NetworkAddr _mac)
	{
		super(_port, _mac);
	}
	
	public int getPort()
	{
		return super.getNic();
	}

	public int getId()
	{
		return super.getAddr().nodeId();
	}

}
