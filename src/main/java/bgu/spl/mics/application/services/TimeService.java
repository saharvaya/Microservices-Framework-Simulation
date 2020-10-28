/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

import javax.swing.*;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

	//Fields
	private final int SERVICE_COUNT; // Number of initial running services.
	private final Object TIMER_INITIALIZE_LOCK = new Object(); // A lock object to help wait for timer initialization

	private final int speed; //	Number of milliseconds for each tick
	private final int duration; // Total amount of ticks before termination
	private Timer globalTimer;	// Global system timer to be executed for duration ticks
	private static int currentTick; // Current timer tick
	private int initializationWait; // Service count to wait for to be initialized before starting timer
	private int terminationWait; // Service count to wait for to be terminated
	private boolean initialized; // Indicated whether this service done its initialization phase

	public TimeService(int speed, int duration, int serviceCount) {
		super("time");
		this.speed = speed;
		this.duration = duration;
		currentTick = 0;
		initialized = false;
		SERVICE_COUNT = serviceCount-1;
		this.initializationWait = SERVICE_COUNT; //	initializationWait is the number of services to wait for to initialize before starting timer, we exclude the timer itself.
		this.terminationWait = 0; // terminationWait is the number of services already terminated, will terminate timer if equals to SERVICE_COUNT
	}

	@Override
	protected void initialize() {
		//Every speed Increment current tick and send a broadcast notifying the current tick.
		globalTimer = new Timer(speed, (e)-> {
			currentTick++;
			if(currentTick == duration)
				stopTimer();
			else sendBroadcast(new TickBroadcast(currentTick));
		});

		this.subscribeBroadcast(InitializedBroadcast.class, (init) -> {
			this.initializationWait--;
			if(initializationWait == 0) {
				//All services done initializing, start global timer tick count
				this.globalTimer.start();
			}
		});

		this.subscribeBroadcast(TerminatedBroadcast.class, (term) -> {
			this.terminationWait++;
			if(terminationWait == SERVICE_COUNT) {
				//All services done terminating and do not expect response from timer, timer service can be terminated.
				this.terminate();
			}
		});

		this.subscribeEvent(GetCurrentTickEvent.class, (tick) -> {
			complete(tick, currentTick); //Resolve waiting future with the current system tick
		});

		initialized = true;
		synchronized (TIMER_INITIALIZE_LOCK) {
			TIMER_INITIALIZE_LOCK.notify(); // Notify to Lock holder that the timer service is initialized
		}
	}

	private void stopTimer()
	{
		//Send a broadcast to all running services notifying them to stop, current system tick reached the duration.
		sendBroadcast(new TimeOutBroadcast());
		globalTimer.stop(); // Stop the global timer process
	}

	/**
	 * Retrieves current timer init status.
	 * @return true if timer is initialized.
	 */
	public synchronized boolean isInitialized()
	{
		return initialized;
	}

	/**
	 * @return A locking object for this timer service
	 */
	public Object getTimerLOCK()
	{
		return this.TIMER_INITIALIZE_LOCK;
	}
}
