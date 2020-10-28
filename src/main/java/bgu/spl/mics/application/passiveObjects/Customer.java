/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable{

	//Fields
	private int id; //Customers Unique ID number
	private String name; // Customers name
	private String address; // Customers address
	private int distance; // Customers distance for deliveries
	private CreditCard creditCard; // Customers credit card details
	private List<SessionOrder> orderSchedule; // Customers future orders list
	private List<OrderReceipt> orderReceipts; // Customers order receipts for purchases already made

	//Constructor
	public Customer()
	{
		this.orderReceipts = new ArrayList<>();
	}

	/**
	 * Nested class representing customers credit card information.
	 */
	private class CreditCard implements Serializable {

		private int number; // Credit card number
		private volatile int amount; // Credit card current balance

		//Constructor
		public CreditCard(int number, int amount)
		{
			this.number = number;
			this.amount = amount;
		}

		/**
		 * Charges current customer credit card by the amount given.
		 * @param billingSum an amount to charge customer credit card with.
		 */
		public void billCard(int billingSum)
		{
			this.amount-=billingSum;
		}
	}

	/**
	 * Nested class representing a specific customer book order,
	 * Including book title and the timer tick the order is to be made in.
	 */
	public class SessionOrder implements Serializable {

		private String bookTitle; //Book title to order in this session
		private int tick; // The tick to make the current order

		/**
		 * Retrieves book title for current order.
		 * @return string representing the book title for the order.
		 */
		public String getBookTitle() {
			return bookTitle;
		}

		/**
		 * Retrieves the tick for the order be ordered in.
		 * @return the tick number to make the order for the {@code bookTitle}
		 */
		public int getTick() {
			return tick;
		}
	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return this.name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return this.id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return this.address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return this.distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return this.orderReceipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return this.creditCard.amount;
	}

	public CreditCard getCreditCard()
	{
		return this.creditCard;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return this.creditCard.number;
	}

	/**
	 * Charges current customers credit card by {@code amount}
	 * @param amount the amount of money to charge customers credit card with.
	 */
	public void billCreditCard(int amount)
	{
		this.creditCard.billCard(amount);
	}

	/**
	 * Retrieves Customers order schedule as read from input file.
	 * @return List of Book Orders, containing book title and timer tick to make the order.
	 */
	public List<SessionOrder> getOrderSchedule() {
		return orderSchedule;
	}
}
