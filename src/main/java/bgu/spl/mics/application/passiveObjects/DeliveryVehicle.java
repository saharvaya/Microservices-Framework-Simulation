/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.TimeUnit;

/**
 * Passive data-object representing a delivery vehicle of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class DeliveryVehicle {

	//Fields
	private int license; // Vehicle licence plate number
	private int speed; // Vehicle delivery speed given in Milliseconds/KM

	//Constructor
	 public DeliveryVehicle(int license, int speed) {
	 	this.license = license;
	 	this.speed = speed;
	  }
	/**
     * Retrieves the license of this delivery vehicle.   
     */
	public int getLicense() {
		return this.license;
	}
	
	/**
     * Retrieves the speed of this vehicle person.   
     * <p>
     * @return Number of ticks needed for 1 Km.
     */
	public int getSpeed() {
		return this.speed;
	}
	
	/**
     * Simulates a delivery by sleeping for the amount of time that 
     * it takes this vehicle to cover {@code distance} KMs.  
     * <p>
     * @param address	The address of the customer.
     * @param distance	The distance from the store to the customer.
     */
	public void deliver(String address, int distance) {
		try {
			long deliveryTime = ((long)distance*getSpeed()); // distance given in KM, speed given in Milliseconds/KM -> Delivery Time = KM * (Milliseconds/KM) = Milliseconds
			TimeUnit.MILLISECONDS.sleep(deliveryTime);	//Sleeps for the delivery time in Milliseconds
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
