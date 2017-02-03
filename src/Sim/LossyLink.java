package Sim;

// This implements a link with loss, jitter and delay

public class LossyLink extends Link{
    private int delay;
    private float jitter;
    private float droprate;

    public LossyLink(int delay, float jitter, float droprate)
    {
        this.delay = delay;
        this.jitter = jitter;
        this.droprate = droprate;
        super();
    }

    @Override
    public void recv(SimEnt src, Event ev)
    {
        //TODO implement delay, jitter and loss
    }
}

