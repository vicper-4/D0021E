package Sim;

//TODO figure out why this gives verry stable values that are way too small
/**
 * Generator for Poisson distridution.
 */
public class PoissonGenerator extends Generator
{
	private double lambda;

	PoissonGenerator(double mean)
	{
		super();
		this.lambda = Math.exp(-mean*1000.0);
	}

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

		return ((double)k)/1000.0;
	}
}
