package Sim;

import java.util.Random;

/**
 * This implements a link with loss, jitter and delay.
 */

public class LossyLink extends Link{
    private int delay;
    private float jitter;
    private float droprate;

    /**
     * @param delay     Avrage delay of message passing thru this link.
     * @param jitter    Jitter of the link.
     * @param droprate  Droprate of the link as probability from 0.0 - 1.0.
     */
    public LossyLink(int delay, float jitter, float droprate)
    {
        super();
        this.delay = delay;
        this.jitter = jitter;
        this.droprate = droprate;
    }

    /**
     * Called when a message enters the link.\
     * Infers delay, jitter and droprate
     * @param src   Message sender.
     * @param ev    Event. 
     */
    @Override
    public void recv(SimEnt src, Event ev)
    {
        if (ev instanceof Message)
        {
            Random random = new Random();
            //TODO implement jitter
            int wait = (int)(delay + jitter);
            if (1.0f - random.nextFloat() > droprate)
            {
                System.out.println("Link recv msg, passes it through");
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
                System.out.println("Link recv msg, drops it");
            }
        }
    }
}

