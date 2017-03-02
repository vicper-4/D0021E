package Sim;

// This class implements a link without any loss, jitter or delay

public class Link extends SimEnt{
	//TODO Consider reverting to private SimEnt's and solving it another way
	//in LossyLink.recv
	protected SimEnt _connectorA=null;
	protected SimEnt _connectorB=null;
	private int _now=0;
	
	public Link()
	{
		super();	
	}
	
	// Connects the link to some simulation entity like
	// a node, switch, router etc.
	
	public void setConnector(SimEnt connectTo)
	{
		if (_connectorA == null) 
			_connectorA=connectTo;
		else
			_connectorB=connectTo;
	}

	/**
	 * Disconnects the link from a connected simulation entity.
	 */
	public void unsetConnector(SimEnt disconnectFrom)
	{
		if (_connectorA == disconnectFrom) 
			_connectorA=null;
		else if (_connectorB == disconnectFrom)
			_connectorB=null;
	}

	// Called when a message enters the link
	
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof Message)
		{
			System.out.println("Link recv msg, passes it through");
			if (src == _connectorA)
			{
				send(_connectorB, ev, _now);
			}
			else
			{
				send(_connectorA, ev, _now);
			}
		} 
		else if (ev instanceof MoveEnt)
		{
			SimEnt router = null;
			SimEnt node = null;
			
			System.out.println("-------------Link recv move request");
			
			if (_connectorB instanceof Router)
			{
				router = _connectorB;
				_connectorB = null;
				node = _connectorA;
			}
			else
			{
				router = _connectorA;
				_connectorA = null;
				node = _connectorB;
			}

			((Node)node).set_id(((MoveEnt)ev).networkId());
			((Router)router).disconnectInterface((SimEnt)this);
			((Router)router).connectInterface(((MoveEnt)ev).getInterface(), this, node);
		}
	}	
}
