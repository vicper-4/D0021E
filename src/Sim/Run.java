package Sim;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main (String [] args)
	{
 		//Creates two links
		Link link1 = new Link();
		Link link2 = new Link();
		// Link link1 = new LossyLink(4.0f,0.2f,0.2f);
		//Link link2 = new LossyLink(2.0f, 2.0f, 0.2f);
	
        Sink sink1 = new Sink();
        Sink sink2 = new Sink();
		// Create two end hosts that will be
		// communicating via the router
		Node host1 = new Node(1,1, sink1);
		Node host2 = new Node(2,1, sink2);

		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);

		// Creates as router and connect
		// links to it. Information about
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(3);
		routeNode.connectInterface(0, link1, host1);
		routeNode.connectInterface(1, link2, host2);

		// Generate some traffic
		Generator gen1 = new ConstantGenerator(3);
		Generator gen2 = new GaussianGenerator(5, 1);
		Generator gen3 = new PoissonGenerator(4);
		// host1 will send 500 messages with time interval 5 to network 2, node 1. Sequence starts with number 1000
		host2.StartSending(2, 2, 4, gen1, 1000); 
		// host2 will send 100 messages with time interval 10 to network 1, node 1. Sequence starts with number 2000
		//host2.StartSending(1, 1, 4, gen1, 2000);

        MoveEnt moveEvent = new MoveEnt((new NetworkAddr(3, 1)), 3);

		host1.send(link1, moveEvent, 4);
		
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
