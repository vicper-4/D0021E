package Sim;

import java.util.Random;

/**
 * Abstract for network trafic generators.
 */
abstract public class Generator
{
	Random rand = new Random();
	
	abstract public double nextSend();

    /**
     * runs nextSend() untill it returns a possitive number.
     * @return output from nextSend()
     */
    public double delay()
    {
        double delay = nextSend();
        if(delay >= 0) {return delay;}
        else {return delay();}
    }
}
