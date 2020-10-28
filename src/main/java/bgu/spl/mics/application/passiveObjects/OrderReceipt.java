/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable{

	//Fields
	private static AtomicInteger currentOrderId; // Global order receipt tracking for current order ID
	private int orderId; // A unique order number ID
	private String seller; // A string representing the selling service that made the receipt
	private int customerId; // Purchasing customer ID number
	private String bookTitle; // Purchased book title
	private int price; // Price charged for the order
	private int issuedTick; // The Timer tick that the book was processes and before sending book to customer
	private int orderTick; // The Timer tick in which the order was made
	private int proccessTick; // The TImer tick in which the selling service started processing the order
	private String address; // Address to send the purchased book to
	private int distance; // Distance information for delivery purposes
	private boolean awaitingPayment;

	//Constructor
	public OrderReceipt(){
		if(currentOrderId == null)
		{
			currentOrderId = new AtomicInteger(0);
		}
		this.awaitingPayment=false;
	}

	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return this.orderId;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return this.seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return this.customerId;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return this.bookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return this.price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return this.issuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {
		return this.orderTick;
	}

	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return this.proccessTick;
	}

	/**
	 * Retrieves customer address for delivery of the current receipt purchase
	 * @return A string representing the customers address
	 */
	public String getAddress() { return this.address; }

	/**
	 * Retrieves the distance to deliver the purchased item.
	 * @return distance given in KM to customers address.
	 */
	public int getDistance() { return this.distance; }

	/**
	 * Returns a boolean value indicating if this current receipt got paid
	 * @return true if receipt is already paid for.
	 */
	public boolean awaitingPayment()
	{
		return this.awaitingPayment;
	}

	/**
	 * Increment the global atomic ID identifier and set the current receipt order id.
	 */
	public void incrementOrderId()
	{
		for(;;) {
			if(currentOrderId.compareAndSet(currentOrderId.get(), currentOrderId.get()+1)) {
				this.orderId = currentOrderId.get();
				break;
			}
		}
	}

	//Information setters for current receipt

	public void setProccessTick(int proccessTick) {
		this.proccessTick = proccessTick;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setIssuedTick(int issuedTick) {
		this.issuedTick = issuedTick;
	}

	public void setOrderTick(int orderTick) {
		this.orderTick = orderTick;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setAwaitingPayment(){
		this.awaitingPayment = true;
	}
}
