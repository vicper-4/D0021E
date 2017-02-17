package Sim;

import java.util.TreeMap;

// This class implements the simulation engine
// As long as there are events in the queue, the simulation 
// will run. When empty, the engine stops

public final class SimEngine implements Runnable {

	private static SimEngine _instance;
	private final TreeMap _simTimeTree = new TreeMap();
	private boolean _quit = false;
	private static double _simTime = 0;

	// Stuffs
	private static int[] jitters;           //TODO: Should be a linked list with jitters for every node?
	private static int _sent = 0;           //messages sent from nodes
	private static int _recv = 0;           //messages received by nodes
	private static double totalTransit = 0; //sum of transit times of all packages.
	private static double jitter = 0;       //jitter of the system

	public static void setJitter(double jitter) {
		SimEngine.jitter += jitter;
	}



	// This method is called to when scheduling an event for some target. Examples of events are messages,
	// timer events etc.
	
	public EventHandle register(SimEnt registrator, SimEnt target, Event event, double delayedExecution)
	{
		double scheduleForTime = getTime() + delayedExecution;
		EventHandle handle = new EventHandle(registrator, target, event, new SimTimeSlot(scheduleForTime));
		_simTimeTree.put(handle._simSlot, handle);
		return handle;
	}
	
	public static double getTime()
	{
		return _simTime;
	}
	
	// To erase a scheduled event, this method can be used
	
	public void deregister (EventHandle handle) 
	{
	
		_simTimeTree.remove(handle._simSlot);
	}
	
	// To force a stop of the motor, even when events are still
	// present in the event list. This method can be used
	
	public void stop()
	{
		_quit = true;
	}
	
	// To empty all events in the queue and restart the engine
	// this method can be used. You however need to add a new 
	// event directly otherwise the engine will stop due to no events 
	
	public void reset()
	{
		_simTimeTree.clear();
		_simTime = 0;
		_quit = false;

		_sent = 0;
		_recv = 0;
		totalTransit = 0;
		jitter = 0;
	}
	
	
	// We can only have one engine in the simulator so this method
	// sees to that. In other words we have implemented a singleton
	
	public static SimEngine instance() 
	{
		if (_instance==null) 
		{
			_instance = new SimEngine();
		}
		return _instance;
		
	}	
	
	// This is the motor itself, is fetches events from the event list as long as there 
	// still are events present or until the stop method has been called
	
	public void run()
	{	
		EventHandle handleToNextEvent=null;
		SimTimeSlot nextEventToExecute=null;
		
		do
		{
			if (_simTimeTree.size() == 0)
				_quit=true;
			else
			{
				nextEventToExecute = (SimTimeSlot) _simTimeTree.firstKey();
				handleToNextEvent = (EventHandle) _simTimeTree.get(nextEventToExecute);
				_simTime=nextEventToExecute._msek;

				// TODO: Brainfarts
				// Calculate jitter
				//jitter = handleToNextEvent._target.getJitter();
				//System.out.println("\t -Simengine currjitter: " + jitter + "-");

				handleToNextEvent._event.entering(handleToNextEvent._target);
				handleToNextEvent._target.recv(handleToNextEvent._registrator, handleToNextEvent._event);
				deregister(handleToNextEvent);
			}
		} while (!_quit);

		// Collect data
		//jitter = Node.getJitter(); //TODO: Should reference the end node in a transaction?
		int droprate = (int) (((double) (_sent - _recv) / (double) _sent) * 100);
		double transitTime = totalTransit / _recv;

		//Prints results at end or run. 
		System.out.printf("%nResults %n");
		System.out.printf("------- %n");
		System.out.printf("Droprate: %d%% sent: %d, received: %d %n", droprate, _sent, _recv);
		System.out.printf("Average transit time: %.4fms. %n", transitTime);
		System.out.printf("Average jitter: %.4fms %n", jitter);
		reset();
	}

	/**
	 * Called when a node sends a message
	 */
	public static void msgSent() {
		_sent++;
	}

	/**
	 * Called when a node sends a message
	 */
	public static void msgRecv() {
		_recv++;
	}
}
