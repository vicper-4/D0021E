package Sim;

import java.util.Random;

abstract public class Generator
{
	Random rand = new Random();
	
	abstract public double nextSend();
}
