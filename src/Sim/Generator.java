package Sim;

import java.util.Random;

abstract public class Generator
{
	Random rand = new Random();
	
	abstract public double nextSend();

    public double delay()
    {
        double delay = nextSend();
        if(delay >= 0) {return delay;}
        else {return delay();}
    }
}
