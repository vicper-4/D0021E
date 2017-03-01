package Sim;


public abstract class MoveEntEvent implements Event{
	private SimEnt _ent;
	
	MoveEntEvent (SimEnt _ent)
	{
		this._ent = _ent;
	}

	public SimEnt ent()
	{
		return _ent;
	}

	public abstract SimEnt getTarget();

	public void entering(SimEnt locale)
	{
	}
}
