/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo implements Serializable{

	//Fields
	private String bookTitle; // Book title information
	private int amount; // Book current amount in inventory
	private int price; // Book pricing

	//Constructor
	public BookInventoryInfo(String bookTitle, int amount, int price)
	{
		this.bookTitle = bookTitle;
		this.amount = amount;
		this.price = price;
	}
	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return this.bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return this.amount;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return this.price;
	}
	
	/**
	 * Reduces the current book information amount in stock.
	 * @return true if book amount is 0 after reduce (book no longer available), false otherwise. 
	 */
	public synchronized boolean reduceAmount()
	{
		this.amount = (this.amount == 0) ? 0 : this.amount-1;
		return this.amount == 0;
	}
}
