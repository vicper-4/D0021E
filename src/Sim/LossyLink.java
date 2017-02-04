package Sim;

/**
 * This implements a link with loss, jitter and delay.
 */

public class LossyLink extends Link{
    private int delay;
    private float jitter;
    private float droprate;

    /**
     * @param delay     Avrage delay of packets passing thru this link.
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
     * Called when a message enters the link, infers delay, jitter and droprate
     * @param src   Message sender.
     * @param ev    Event. 
     */
    @Override
    public void recv(SimEnt src, Event ev)
    {
        //TODO implement delay, jitter and loss
    }
}

