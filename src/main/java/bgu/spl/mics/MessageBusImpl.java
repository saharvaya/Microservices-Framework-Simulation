/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//Fields
	private Map<Class<? extends Message>, RoundRobinArrayList<ServiceEventQueue<Message>>> messageSubscriptions; // Maps Messages to A round robin behavior ArrayList of Messages Queues
	private List<ServiceEventQueue<Message>> registeredServices; // A list containing all services that are registered to the MessageBus singleton
	private Map<Event<?>, Future<?>> eventFutures; // Maps an Event to his corresponding future result object.
	private final Object subscriptionLock = new Object(); // Locking object used to ensure all services are subscribing to correct messages
	private volatile boolean startedUnregisterProcess; // Determines if services registered to this message bus started unregistering, should stop accepting new events

	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance()
	{
		return SingletonHolder.instance;
	}

	//Constructor
	private MessageBusImpl() {
		messageSubscriptions = new ConcurrentHashMap<>();
		registeredServices = new CopyOnWriteArrayList<>();
		eventFutures = new ConcurrentHashMap<>();
		this.startedUnregisterProcess = false;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subscribeMessage(type, m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subscribeMessage(type, m);
	}

	/**
	 * Subscribes MicroService {@code m} to receive messages of {@code type} class.
	 * @param type a message class type to subscribe the service to.
	 * @param m the service to subscribe
	 */
	private void subscribeMessage(Class<? extends Message> type, MicroService m)
	{
		synchronized (subscriptionLock) { // Use locking object to ensure all services are subscribing to the correct messages
			ServiceEventQueue<Message> serviceQueue = getRegisteredService(m);
			if (serviceQueue == null) // If null than service is not registered to the MessageBus, should not accept message subscriptions for this service
				return;
			serviceQueue.getSubscribedMessagesList().add(type); //Add to the service queue registered messages list the current message type
			if (messageSubscriptions.get(type) == null) // No round robin list has been initiated for this type of message class yet.
				messageSubscriptions.put(type, new RoundRobinArrayList<>()); // Map the message class type to that round robin array list.
			messageSubscriptions.get(type).add(serviceQueue);  // Add the service with its message queue to the round robin array list.
		}
	}

	/**
	 * Finds the service {@code m} in this MessageBus registered services list.
	 * @param m service to find
	 * @return The message queue associated with this service if service is registered, else return null.
	 */
	private ServiceEventQueue<Message> getRegisteredService(MicroService m) {
		for(ServiceEventQueue<Message> serviceQueue : registeredServices)
		{
			if(m.equals(serviceQueue.getService()))
				return serviceQueue;
		}
		return null;
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		@SuppressWarnings("unchecked")
		Future<T> future = (Future<T>) eventFutures.get(e);
		//Resolve the future object corresponding with given event and remove it from the futures map
		future.resolve(result);
		eventFutures.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		RoundRobinArrayList<ServiceEventQueue<Message>> subscribers = messageSubscriptions.get(b.getClass());
		if(subscribers != null) {
			//Send broadcast to all its subscribers
			for (ServiceEventQueue<Message> service : subscribers) {
				service.add(b);
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		RoundRobinArrayList<ServiceEventQueue<Message>> subscribers = messageSubscriptions.get(e.getClass());
		if (!startedUnregisterProcess && subscribers != null && subscribers.size() != 0) {
			//Create and map a new future corresponding to given event
			Future<T> future = new Future<>();
			eventFutures.put(e, future);
			//Add event to relevant message queue in round robin fashion
			try {
				subscribers.getCurrentItem().add(e);
			//If ArrayIndexOutOfBoundsException is thrown than unregistration process started, return null
			} catch (ArrayIndexOutOfBoundsException unregisterException) { return null; }
			return future;
		} else return null;
	}

	@Override
	public void register(MicroService m) {
		if (m == null) return;
		registeredServices.add(new ServiceEventQueue<>(m));
	}

	@Override
	public void unregister(MicroService m) {
		if(!startedUnregisterProcess)
			startedUnregisterProcess = true;
		if (m == null) return;
		//Find the service to remove and clear all references of that service from MessageBus
		for (Iterator<ServiceEventQueue<Message>> it = registeredServices.iterator(); it.hasNext();) {
			ServiceEventQueue<Message> service = it.next();
			if (service.getService().equals(m)) {
				clearServiceReferences(service);
				break;
			}
		}
	}

	/**
	 * Clears all references to the given service associated queue {@code service}
	 * No references of that service should exist in the MessageBus after.
	 * @param service
	 */
	private void clearServiceReferences(ServiceEventQueue<Message> service)
	{
		for(Message msg : service) //For each remaining message, if it is an event, resolve corresponding future with null value
			if(msg instanceof Event)
				complete((Event<?>)msg, null);
		service.clear(); // Clear message queue for unregistering service
		@SuppressWarnings("unchecked")
		final List<Class<? extends Message>> subscribed = service.getSubscribedMessagesList();
			for (Class<? extends Message> messageType : subscribed) {
				RoundRobinArrayList<ServiceEventQueue<Message>> services = messageSubscriptions.get(messageType);
				services.remove(service); // Remove from each round robin list the service is in.
			}
		registeredServices.remove(service); // Remove from registered MessageBus service list
	}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		ServiceEventQueue<Message> service = getRegisteredService(m);
		if(service != null)
			return service.take(); //Return the next message from the wanted service message queue if exists
		else {
			System.out.println("ERROR");
			return null;
		}
	}

	/**
	 *Private class that encapsulates a MicroServices with its corresponding message queue.
	 * Serves as wrapping class to MicroServices in the MessageBus.
	 * @param <T> Type of objects in service queue, ex: Message, Event, Broadcast
	 */
	private class ServiceEventQueue<T> extends LinkedBlockingQueue<T> {
		//Fields
		private MicroService service; // Service associated with this message queue
		private List<Class<? extends Message>> subscribedMessages; //List of message class type this service is registered to.
		
		private ServiceEventQueue(MicroService m)
		{
			this.service = m;
			this.subscribedMessages = new CopyOnWriteArrayList<>();
		}

		//Getters
		private MicroService getService()
		{
			return service;
		}

		private List<Class<? extends Message>> getSubscribedMessagesList() { return this.subscribedMessages; }
	}

	/**
	 * Private class that extends ArrayList and holds a current index to get.
	 * Method getCurrentItem which is supplied by this class return the next item in a round robin fashion.
	 * @param <T> Type of objects in the round robin list.
	 */
	private class RoundRobinArrayList<T> extends CopyOnWriteArrayList<T> {
		//Fields
		private AtomicInteger index; // Current list index to retrieve next
		
	    private RoundRobinArrayList() {
	    	this.index = new AtomicInteger();
	        this.index.set(0);
	    }

		/**
		 * Increments current round robin index, if index reached list size then increment will return to first list index.
		 * @return The current index before incrementing.
		 */
		private int incrementAndGetIndex()
		{
			int current_ind = index.get();
			for(;;) {
				if(this.index.compareAndSet(index.get(), (index.get() == this.size()-1) ? 0 : this.index.get() + 1))
					break;
			}
			return current_ind;
		}

		//Getters
	    private int getIndex() {
	        return this.index.get();
	    }

		/**
		 * @return Item in current round robin index.
		 */
		private synchronized T getCurrentItem()
	    {
	    	return this.get(incrementAndGetIndex());
	    }
	}
}
