package Sim;

// This class makes use of time stamps in milliseconds (or any granularity that you like to use)
// In the case that two events are scheduled for the same time, the id of the entity 
// are used to solve the conflict of who goes first. 


class SimTimeSlot implements Comparable {
	double _msek;
	long _resolver;
	private static long _discriminator=0;
	
	SimTimeSlot(double msek) 
	{
		_msek = msek;
		_resolver = _discriminator;
		_discriminator++;
		
	}
	
	// This method is called when an event is scheduled to be inserted into 
	// the treeMap handled by the register method in the simulation engine.
	
	public int compareTo(Object obj)
	{
		SimTimeSlot other = (SimTimeSlot) obj;
		
		if (this._msek < other._msek)
			return -1;
		else if (this._msek > other._msek)
			return +1;
		else
		{
			if (this._resolver < other._resolver)
				return -1;
			else if (this._resolver > other._resolver)
				return +1;
			else 
				return 0;
		}
	}
}