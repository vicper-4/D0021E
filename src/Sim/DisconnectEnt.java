package Sim;

public class DisconnectEnt extends MoveEntEvent{
	private SimEnt _from;
	private int _interface;
	
	DisconnectEnt (SimEnt _ent, int _interface)
	{
		super(_ent);
		this._interface = _interface;
	}

	DisconnectEnt (SimEnt _ent, SimEnt _from)
	{
		super(_ent);
		this._from = _from;
	}

	public int getInterface()
	{
		return _interface;
	}

	public SimEnt getTarget()
	{
		return _from;
	}
}
