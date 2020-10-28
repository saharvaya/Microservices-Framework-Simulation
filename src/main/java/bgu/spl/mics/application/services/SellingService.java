/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	//Fields
	private MoneyRegister register; // Money Register Object
	private OrderReceipt currentReciept; // Current working receipt, resets each new selling process

	public SellingService(int serviceId) {
		super("selling " + serviceId);
		register = MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TimeOutBroadcast.class, (finalTick) -> {
			this.terminate();
			sendBroadcast(new TerminatedBroadcast());
		});

		this.subscribeEvent(BookOrderEvent.class, (orderDetails) -> {
			//Initialize a new order receipt process to handle
			this.currentReciept = new OrderReceipt();
			Future<Integer> processTick = sendEvent(new GetCurrentTickEvent());
			//Get the current tick and set the process start tick
			if (processTick != null) {
				currentReciept.setProccessTick(processTick.get());
				//Check availability and get book if is currently available and customer funds are sufficient.
				Integer bookTakenPrice;
				synchronized (orderDetails.getCustomer()) {
					Future<Integer> bookTaken = sendEvent(new GetBookEvent(orderDetails.getBookTitle(), orderDetails.getCustomer()));
					bookTakenPrice = (bookTaken != null) ? bookTaken.get() : null;
				}

				if (bookTakenPrice != null) { // If equals to null than book is not is stock or buying customer has insufficient funds, else, Meaning order was successful and customer purchased book
					setOrderReceipt(orderDetails, bookTakenPrice); //Book was taken, update current receipt details
					finalizeOrderAndDeliver(orderDetails, bookTakenPrice); // Charge buying customer, set issued tick to receipt, file the receipt and deliver purchase to customer.
				} else resetReceipt(orderDetails); // Reset current selling process receipt, encountered problem while purchasing
			} else resetReceipt(orderDetails);
		});

		// Notify timer service init process completed, service is subscribed to all relevant events and broadcasts
		sendBroadcast(new InitializedBroadcast());
	}

	/**
	 * Set current selling process receipts details.
	 * @param orderDetails the current order event corresponding receipt details
	 * @param price current book order price
	 */
	private void setOrderReceipt(BookOrderEvent orderDetails, int price)
	{
		currentReciept.incrementOrderId();
		currentReciept.setOrderTick(orderDetails.getOrderTick());
		currentReciept.setPrice(price);
		currentReciept.setBookTitle(orderDetails.getBookTitle());
		currentReciept.setCustomerId(orderDetails.getCustomer().getId());
		currentReciept.setSeller(this.getName());
		currentReciept.setDistance(orderDetails.getCustomer().getDistance());
		currentReciept.setAddress(orderDetails.getCustomer().getAddress());
		currentReciept.setAwaitingPayment();
	}

	/**
	 * Finalizes the current working receipt, charges the customer for the purchase price,
	 * files the receipt and send the purchase to delivery.
	 * @param orderDetails the current order event corresponding receipt details
	 * @param price price to charge customer for.
	 */
	private void finalizeOrderAndDeliver(BookOrderEvent orderDetails, int price)
	{
		//Get the current tick and set the issued time tick
		Future<Integer> issuedTick = sendEvent(new GetCurrentTickEvent());
		if (issuedTick != null && currentReciept.awaitingPayment()) {
			register.chargeCreditCard(orderDetails.getCustomer(), price); //Charge customer for the price of purchased book
			currentReciept.setIssuedTick(issuedTick.get());
			//Complete the current receipt event, customer will get the receipt
			complete(orderDetails, currentReciept);
			//File receipt in the stores money register
			register.file(currentReciept);
			//Send a request for a new delivery for the current purchase with the relevant receipt.
			sendEvent(new DeliveryEvent(currentReciept));
		} else resetReceipt(orderDetails);
	}

	/**
	 * Recent current working receipt.
	 * @param currentOrder order to resolve the receipt for.
	 */
	private void resetReceipt(BookOrderEvent currentOrder)
	{
		this.currentReciept = null; // Set current receipt to null
		complete(currentOrder, null); // Resolve current receipt to null
	}

}
