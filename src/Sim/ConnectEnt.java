package Sim;

public class ConnectEnt extends MoveEntEvent{
	private SimEnt _to;
	private int _interface;
	
	ConnectEnt (SimEnt _ent, SimEnt _to, int _interface)
	{
		super(_ent);
		this._to = _to;
		this._interface = _interface;
	}

	ConnectEnt (SimEnt _ent, SimEnt _to)
	{
		super(_ent);
		this._to = _to;
	}

	public int getInterface()
	{
		return _interface;
	}
	
	public SimEnt getTarget()
	{
		return _to;
	}
}
