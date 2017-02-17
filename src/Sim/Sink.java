package Sim;

public class Sink
{
    private long recived;
    private double last;
    private double avgrPeriod;
    private double period;
    private double periodDeviation;
    private double avgrPeriodDeviation;
    private double avgrPossitivePeriodDeviation;
    private double avgrNegativePeriodDeviation;
    private double avgrDelay;
    private double delay;
    private double avgrJitter;
    private double jitter;

    Sink()
    {
        reset();
    }

    public void recv(Message ev, double currTime)
    {
        double tt = currTime - ev.timeSent;
        
        //-------------------messages recived-------------------

        recived++;
        
        //-------------------period-------------------
        
        period = currTime - last;
        
        avgrPeriod -= avgrPeriod/recived;
        avgrPeriod += period/recived;

        //-------------------period deviation-------------------

        periodDeviation = period - avgrPeriod;

        avgrPeriodDeviation -= avgrPeriodDeviation/recived;
        avgrPeriodDeviation += Math.abs(periodDeviation)/recived;
        
        //TODO consider not using hardcoded weight for new values.
        if(periodDeviation > 0)
        {
            avgrPossitivePeriodDeviation -= avgrPossitivePeriodDeviation / 100.0;
            avgrPossitivePeriodDeviation += periodDeviation/100.0;
        }
        else
        {
            avgrNegativePeriodDeviation -= avgrPossitivePeriodDeviation / 100.0;
            avgrNegativePeriodDeviation += -periodDeviation/100.0;
        }

        //-------------------jitter-------------------

        jitter = Math.abs(delay - tt);
        avgrJitter -= avgrJitter/recived;
        avgrJitter += jitter/recived;

        //-------------------delay-------------------
        
        delay = tt;
        avgrDelay -= avgrDelay/recived;
        avgrDelay += delay/recived;

        //-------------------last recived-------------------
        
        last = currTime;
    }

    private void reset()
    {
        recived = 0;
        last = 0.0;
        avgrPeriod = 0.0;
        period = 0.0;
        periodDeviation = 0.0;
        avgrPeriodDeviation = 0.0;
        avgrPossitivePeriodDeviation = 0.0;
        avgrNegativePeriodDeviation = 0.0;
        avgrDelay = 0.0;
        delay = 0.0;
        avgrJitter = 0.0;
        jitter = 0.0;
    }
    //TODO add get functions fall all variables
}
