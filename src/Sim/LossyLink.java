package Sim;

import java.util.Random;

/**
 * This implements a link with loss, jitter and delay.
 */
public class LossyLink extends Link{
	private float delay;
	private float jitter;
	private float droprate;

	/**
	 * @param delay		Avrage delay of message passing thru this link. (ms)
	 * @param jitter	Jitter of the link. Random uniform distribution. (ms)
	 * @param droprate	Droprate of the link as probability from 0.0 - 1.0.
	 */
	public LossyLink(float delay, float jitter, float droprate)
	{
		super();
		this.delay = delay;
        if(jitter < delay){
		    this.jitter = jitter;
        }
        else
        {
            this.jitter = delay;
        }

		this.droprate = droprate;
	}

	/**
	 * Called when a message enters the link.\
	 * Infers delay, jitter and droprate
	 * @param src	Message sender.
	 * @param ev	Event.
	 */
	@Override
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof Message)
		{
			Random random = new Random();
			float dt = (random.nextFloat() * 2 - 1) * jitter;
			double wait = delay + dt;
			if (1.0f - random.nextFloat() > droprate)
			{
				//System.out.println("Link recv msg, passes it through");
				if (src == _connectorA)
				{
					send(_connectorB, ev, wait);
				}
				else
				{
					send(_connectorA, ev, wait);
				}
			}
			else
			{
				//System.out.println("!!-\t LossyLink recv msg, randomly drops it");
			}
		}
	}
}
