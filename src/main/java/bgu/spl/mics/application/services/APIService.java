/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Customer.SessionOrder;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {

	//Fields
	private Customer customer; // A Customer connected to the current API
	private List<SessionOrder> orderSchedule; // Current connected customer orders schedule
	
	public APIService(Customer customer, List<SessionOrder> orderSchedule) {
		super("customer " + customer.getId());
		this.customer = customer;
		this.orderSchedule = orderSchedule;
		this.orderSchedule.sort(Comparator.comparing(SessionOrder::getTick)); // Sort connected customer order schedule by order tick
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TimeOutBroadcast.class, (finalTick) -> {
			this.terminate();
			sendBroadcast(new TerminatedBroadcast());
		});

		this.subscribeBroadcast(TickBroadcast.class, (tickBroadcast) -> {
			if(this.customer.getAvailableCreditAmount() > 0) {
				List<Future<OrderReceipt>> futures = new ArrayList<>(); // Future resolved order receipts list
				List<SessionOrder> expired = null;
				for (SessionOrder order : orderSchedule) {
					if (tickBroadcast.getCurrentTick() == order.getTick()) {
						futures.add(sendEvent(new BookOrderEvent(order.getBookTitle(), tickBroadcast.getCurrentTick(), customer)));
						if (expired == null)
							expired = new ArrayList<>();
						expired.add(order); // An event was sent for this current order, add order to expired order list
					} else if (order.getTick() > tickBroadcast.getCurrentTick())
						break;
				}
				removeExpiredOrders(expired); // Remove expired orders from customers order schedule
				for (Future<OrderReceipt> future : futures) {
					//Wait for each receipt in current order to complete the buying process and add the receipt to the customers receipts list.
					if (future != null) {
						OrderReceipt receipt = future.get();
						if (receipt != null) // Will be resolved to null if order failed
							customer.getCustomerReceiptList().add(receipt);
					}
				}
			}
		});

		// Notify timer service init process completed, service is subscribed to all relevant events and broadcasts
		sendBroadcast(new InitializedBroadcast());
	}

	/**
	 * Removes argument given expired orders list from current buying customer order schedule list.
	 * @param expired A list of expired orders, meaning a buying process has already started for them.
	 */
	private void removeExpiredOrders(List<SessionOrder> expired)
	{
		if(expired != null) {
			for (SessionOrder ex : expired) {
				orderSchedule.remove(ex);
			}
		}
	}
}
