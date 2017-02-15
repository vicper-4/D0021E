package Sim;

public class ConstantGenerator extends Generator
{
	private double period;
	
	ConstantGenerator(double period)
	{
		super();
		this.period = period;
	}

	public double nextSend()
	{
		return period;
	}
}
