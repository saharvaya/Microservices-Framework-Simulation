/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	//Fields
	private LinkedBlockingQueue<DeliveryVehicle> vehicles; // List of currently available delivery vehicles
	private Semaphore vehiclePermits; // Semaphore Locking used to permit vehicles only in currently available

	/**
	 * Retrieves the single instance of this class.
	 */
	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}

	//Constructor
	private ResourcesHolder()
	{
		vehicles = new LinkedBlockingQueue<>();
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> future = new Future<>();
		try {
			this.vehiclePermits.acquire(); // Acquire one permit to a vehicle if available
			DeliveryVehicle vehicle = vehicles.poll(); // Extracts a vehicle for delivery from the available vehicles queue
			future.resolve(vehicle); // Resolve promise with extracted vehicle
		}
		catch (InterruptedException e){}
		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		vehicles.add(vehicle); // Add vehicle back to available vehicle list
		this.vehiclePermits.release(); // Release a permit to next vehicle request for delivery
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		this.vehicles.addAll(Arrays.asList(vehicles)); // Load all given vehicles to available vehicle list.
		this.vehiclePermits = new Semaphore(this.vehicles.size(), true); // Once vehicles loaded in resource holder, set a fair semaphore with vehicle count permits.
	}

}
