package Sim;

// Just a class that works like a table entry hosting
// a link connecting and the node at the other end

public class TableEntry {

	private int _interface;
	private NetworkAddr _address;
	private TableEntry next;
	
	TableEntry(int _interface, NetworkAddr _address)
	{
		this._interface = _interface;
		this._address = _address;
	}

	public void setNext(TableEntry next)
	{
		this.next = next;
	}
	
	protected int getNic()
	{
		return _interface;
	}

	protected NetworkAddr getAddr()
	{
		return _address;
	}
	
	public TableEntry getNext()
	{
		return next;
	}
}
