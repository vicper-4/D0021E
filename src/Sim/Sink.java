package Sim;

public class Sink
{
    private long recived;
    private double last;
    private double avgrPeriod;
    private double period;
    private double periodDeviation;
    private double avgrPossitivePeriodDeviation;
    private double avgrPeriodDeviation;
    private double avgrNegativePeriodDeviation;
    private double avgrDelay;
    private double delay;
    private double avgrJitter;
    private double jitter;

    public double getAvgrPeriod() {
        return avgrPeriod;
    }

    public long getRecived() {
        return recived;
    }

    public double getPeriod() {
        return period;
    }

    public double getPeriodDeviation() {
        return periodDeviation;
    }

    public double getAvgrPeriodDeviation() {
        return avgrPeriodDeviation;
    }

    public double getAvgrPossitivePeriodDeviation() {
        return avgrPossitivePeriodDeviation;
    }

    public double getAvgrNegativePeriodDeviation() {
        return avgrNegativePeriodDeviation;
    }

    public double getAvgrDelay() {
        return avgrDelay;
    }

    public double getDelay() {
        return delay;
    }

    public double getAvgrJitter() {
        return avgrJitter;
    }

    public double getJitter() {
        return jitter;
    }


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
        
        if(avgrPeriod == 0) avgrPeriod = period;
        avgrPeriod -= avgrPeriod/recived;
        avgrPeriod += period/recived;

        //-------------------period deviation-------------------

        periodDeviation = period - avgrPeriod;

        if(avgrPeriodDeviation == 0) avgrPeriodDeviation = periodDeviation;
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
            avgrNegativePeriodDeviation -= avgrNegativePeriodDeviation / 100.0;
            avgrNegativePeriodDeviation += -periodDeviation/100.0;
        }

        //-------------------jitter-------------------

        jitter = Math.abs(delay - tt);
		avgrJitter += (1.0 / ((double) recived)) * (Math.abs(tt - avgrDelay) - avgrJitter);

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
}
