package Sim;

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

	public double nextSend()
	{
		return rand.nextGaussian() * deviation + mean;
	}
}
