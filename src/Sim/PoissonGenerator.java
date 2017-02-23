package Sim;

/**
 * Network traffic generator for Poisson distribution as described in the link
 * @see <a href="https://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables">Generating Poisson-distributed random variables</a>
 */
public class PoissonGenerator extends Generator
{
	private double lambda;

    /**
     * @param mean some magic value that makes this do what it is supposed to.
     */
	PoissonGenerator(double mean)
	{
		super();
		this.lambda = Math.exp(-mean);
	}

    //TODO Only returns whole ms. consider making it more granular
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

		return ((double)(k-1));
	}
}
