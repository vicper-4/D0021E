package Sim;

/**
 * Network trafic generator for normal distrubution.
 */
public class GaussianGenerator extends Generator
{
	private double mean;
	private double deviation;

	GaussianGenerator(double mean, double deviation)
	{
		super();
		this.mean = mean;
		this.deviation = deviation;
	}

    /**
     * @return A psudo-random microsecond from constraints specified in constructor
     */
	public double nextSend()
	{
		return rand.nextGaussian() * deviation + mean;
	}
}
