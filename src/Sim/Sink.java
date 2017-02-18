package Sim;

/**
 * Class for receiving/consuming network traffic
 */
public class Sink
{
    private long received;
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

    public long getReceived() {
        return received;
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

    /**
     * handles receiving of messages/traffic.
     * @param ev        Incoming message/traffic
     * @param currTime  Current time.
     */
    public void recv(Message ev, double currTime)
    {
        calcStat(currTime - ev.timeSent, currTime);

        last = currTime;
    }

    /**
     * Calls the other methods that calculate statistics.
     * @param tt        Transit time of the received package.
     * @param currTime  Current time.
     */
    private void calcStat(double tt, double currTime) {
        received(1);
        period(currTime);
        deviation();
        delay(tt);
        jitter(tt);
    }

    /**
     * Increments the number of received packages.
     * @param n Number to increment by.
     */
    private void received(long n) {
        received += n;
    }

    /**
     * calculated period of the message and updates the average period.
     * @param currTime  Current time.
     */
    private void period(double currTime) {
        period = currTime - last;

        if(avgrPeriod == 0) {avgrPeriod = period;}

        avgrPeriod -= avgrPeriod/ received;
        avgrPeriod += period/ received;
    }

    /**
     * calculated deviation in period of the message and updates the average period deviation.
     */
    private void deviation() {
        periodDeviation = period - avgrPeriod;

        if(avgrPeriodDeviation == 0) {avgrPeriodDeviation = periodDeviation;}

        avgrPeriodDeviation -= avgrPeriodDeviation/ received;
        avgrPeriodDeviation += Math.abs(periodDeviation)/ received;

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
    }

    /**
     * calculated jitter of the message and updates the average jitter.
     * @param tt  Transit time.
     */
    private void jitter(double tt) {
        jitter = Math.abs(delay - tt);
		avgrJitter += (1.0 / ((double) received)) * (Math.abs(tt - avgrDelay) - avgrJitter);
    }

    /**
     * calculated period of the message and updates the average period.
     * @param tt  Transit time.
     */
    private void delay(double tt) {
        delay = tt;
        avgrDelay -= avgrDelay/ received;
        avgrDelay += delay/ received;
    }

    /**
     * Resets all gathered statistics.
     */
    private void reset()
    {
        received = 0;
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
