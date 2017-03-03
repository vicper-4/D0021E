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

		//Link link1 = new LossyLink(1.5f,0.2f,0.05f);
		//Link link2 = new LossyLink(2.0f, 0.1f, 0.08f);
	
		Sink sink1 = new Sink();
		Sink sink2 = new Sink();
		Sink sink3 = new Sink();
		// Create two end hosts that will be
		// communicating via the router
		Node host1 = new Node(1,1, sink1);
		Node host2 = new Node(2,1, sink2);
		Node host3 = new Node(3,1, sink3);

		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);
		host3.setPeer(link4);

		// Creates as router and connect
		// links to it. Information about
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(4);
		Router router2 = new Router(2);
		routeNode.connectInterface(0, link1);
		routeNode.connectInterface(1, link2);

		// Connect the two routers
		routeNode.connectInterface(3, link3);
		router2.connectInterface(0, link3);
		router2.connectInterface(1, link4);



		// Generate some traffic
		Generator gen1 = new ConstantGenerator(5);
		Generator gen2 = new GaussianGenerator(4, 1);
		Generator gen3 = new PoissonGenerator(5);
		host1.up(2, 1, 100, gen1, 1000);
		host2.up(3, 1, 100, gen1, 2000);
		host3.up(1, 1, 35, gen1, 3000);

		//Event disConEv1 = new DisconnectEnt(link2, host2);
		Event disConEv2 = new DisconnectEnt(link2, routeNode);
		//Event conEv1 = new ConnectEnt(link2, host2);
		Event conEv2 = new ConnectEnt(link2, routeNode, 2);
		//link2.send(link2, disConEv1, 20);
		link2.send(link2, disConEv2, 35);
		//link2.send(link2, conEv1, 50);
		link2.send(link2, conEv2, 65);

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
