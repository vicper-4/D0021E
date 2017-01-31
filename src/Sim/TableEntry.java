package Sim;

// Just a class that works like a table entry hosting
// a link connecting and the node at the other end

public class TableEntry {

	private SimEnt _link;
	private SimEnt _node;
	
	TableEntry(SimEnt link, SimEnt node)
	{
		_link=link;
		_node=node;
	}
	
	protected SimEnt link()
	{
		return _link;
	}

	protected SimEnt node()
	{
		return _node;
	}
	
}
