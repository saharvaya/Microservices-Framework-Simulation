/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	public LogisticsService(int serviceId) {
		super("logistics " + serviceId);
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TimeOutBroadcast.class, (finalTick) -> {
			this.terminate();
			sendBroadcast(new TerminatedBroadcast());
		});

		this.subscribeEvent(DeliveryEvent.class, (delivery) -> {
			sendEvent(new DeliverEvent(delivery.getReceipt())); // Pass a new Deliver Event with the given receipt to request from resource service to send a vehicle to delivery to address on the receipt.
		});

		// Notify timer service init process completed, service is subscribed to all relevant events and broadcasts
		sendBroadcast(new InitializedBroadcast());
	}
}
