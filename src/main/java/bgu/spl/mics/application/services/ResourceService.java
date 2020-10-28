/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliverEvent;
import bgu.spl.mics.application.messages.InitializedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TimeOutBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	//Fields
	private ResourcesHolder resources; // Resource Holder reference

	public ResourceService(int serviceId) {
		super("resource " + serviceId);
		this.resources = ResourcesHolder.getInstance();
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TimeOutBroadcast.class, (finalTick) -> {
			this.terminate();
			sendBroadcast(new TerminatedBroadcast());
		});

		this.subscribeEvent(DeliverEvent.class, (delivery) -> {
			//Try to acquire a vehicle if available
			Future<DeliveryVehicle> futureVehicle = resources.acquireVehicle();
			DeliveryVehicle vehicle = futureVehicle.get();
			//After vehicle acquired deliver vehicle to address on given receipt, vehicle will travel the distance written on the receipt and will not be available during that time.
			vehicle.deliver(delivery.getReceipt().getAddress(), delivery.getReceipt().getDistance());
			//After vehicle has returned release the permit for that vehicle to be able to take another delivery.
			resources.releaseVehicle(vehicle);
		});

		// Notify timer service init process completed, service is subscribed to all relevant events and broadcasts
		sendBroadcast(new InitializedBroadcast());
	}
}
