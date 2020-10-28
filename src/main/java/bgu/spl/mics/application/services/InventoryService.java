/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.GetBookEvent;
import bgu.spl.mics.application.messages.InitializedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TimeOutBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	//Fields
	private Inventory inventory; // Stores book inventory reference
	private final int UNAVAILABLE_BOOK_PRICE = -1;
	
	public InventoryService(int serviceId) {
		super("inventory " + serviceId);
		this.inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TimeOutBroadcast.class, (finalTick) -> {
			this.terminate();
			sendBroadcast(new TerminatedBroadcast());
		});

		this.subscribeEvent(GetBookEvent.class, (orderRequest) -> {
			//Get the book price if it is currently available in stock
			int price = inventory.checkAvailabiltyAndGetPrice(orderRequest.getBookTitle());
			OrderResult result = null;
			//price != UNAVAILABLE_BOOK_PRICE meaning book is available in the book store, Check customer has enough credit to fund book, and that book is available in stock and retrieve the book order result.
			if (price != UNAVAILABLE_BOOK_PRICE && orderRequest.getCustomer().getAvailableCreditAmount() >= price) {
				result = inventory.take(orderRequest.getBookTitle());
			}
			//Complete event, if book was successfully taken return book price else return null
			complete(orderRequest, (result == OrderResult.SUCCESSFULLY_TAKEN) ? price : null);
		});

		// Notify timer service init process completed, service is subscribed to all relevant events and broadcasts
		sendBroadcast(new InitializedBroadcast());
	}
}
