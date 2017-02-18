package Sim;

/**
 * Network trafic enerator for Poisson distridution.
 */
public class PoissonGenerator extends Generator
{
	private double lambda;

	PoissonGenerator(double mean)
	{
		super();
		this.lambda = Math.exp(-mean);
	}

    //TODO Only returns whole ms. give more granularity
	/**
	 * @return 	Psudo-random microsecond on Poisson distrubution. 
	 */
	public double nextSend()
	{
		int k = 0;

		for(double p = 1.0; p > lambda; k++)
		{
			p *= rand.nextDouble();
		}

		return ((double)k);
	}
}
