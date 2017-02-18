package Sim;

/**
 * Network trafic generator for constant bit-rates.
 */
public class ConstantGenerator extends Generator
{
	private double period;
	
	ConstantGenerator(double period)
	{
		super();
		this.period = period;
	}

    /**
     * @return Value specified in constructor.
     */
	public double nextSend()
	{
		return period;
	}
}
