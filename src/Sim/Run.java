package Sim;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main (String [] args)
	{
 		//Creates two links
		Link link1 = new Link();
		Link link2 = new Link();
		Link link3 = new Link();
		Link link4 = new Link();
		Link link5 = new Link();
		Link link6 = new Link();
		Link link7 = new Link();
		//Link link1 = new LossyLink(0.4f,0.2f,0.0f);
		//Link link2 = new LossyLink(0.2f, 0.1f, 0.0f);
	

		// Creates as router and connect
		// links to it. Information about
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router router1 = new Router(3);
		Router router2 = new Router(3);
		
		// Connect links to the routers
		router1.connectInterface(0, link5);
		router1.connectInterface(1, link4);
		router1.connectInterface(2, link1);
		router2.connectInterface(0, link5);
		router2.connectInterface(1, link6);
		router2.connectInterface(2, link7);

		// Create a switch
		Switch switch1 = new Switch(3);
		switch1.connectPort(0, link1);
		switch1.connectPort(1, link2);
		switch1.connectPort(2, link3);

		// Create hosts communicating via the routers
		Node host1 = new Node(4,1, new Sink());
		Node host2 = new Node(1,2, new Sink());
		Node host3 = new Node(6,3, new Sink());

		// Setup a Home Agent
		Node ha = new HomeAgent(1,4, new Sink());
		ha.setPeer(link2);
		host2.setHA(new NetworkAddr(1,4));

		//Connect links to hosts
		host1.setPeer(link4);
		host2.setPeer(link3);
		host3.setPeer(link6);

		// Generate some traffic
		Generator gen1 = new ConstantGenerator(5);
		Generator gen2 = new GaussianGenerator(4, 1);
		Generator gen3 = new PoissonGenerator(5);
		host1.up(1, 2, 100, gen2, 1000);
		host2.up(6, 3, 3,   gen2, 2000);
		host3.up(4, 1, 3,   gen2, 3000);

		//events to move a MN
		Event disConEv2 = new DisconnectEnt(link3, host2);
		Event disConEv3 = new DisconnectEnt(link3, switch1);
		Event conEv2 = new ConnectEnt(link7, host2);
		host2.send(link3, disConEv2, 35);
		switch1.send(link3, disConEv3, 35);
		host2.send(link7, conEv2, 75);

		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());

		t.start();
		try
		{
			t.join();

			System.out.println("\nHost 1.1\n-------");
			host1.printStat();
			System.out.println("Host 2.1\n-------");
			host2.printStat();
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?");
		}
	}
}
