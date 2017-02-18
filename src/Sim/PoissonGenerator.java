package Sim;

/**
 * Generator for Poisson distribution.
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
	 * @return 	Pseudo-random microsecond on Poisson distribution.
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
