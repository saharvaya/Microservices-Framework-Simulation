/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

	//Fields
	private List<BookInventoryInfo> inventory; // A list containing current book inventory information
	private final Object availabilityLock = new Object();

	/**
	 * Retrieves the single instance of this class.
	 */
	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}

	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}

	//Constructor
	private Inventory() {
		inventory = Collections.synchronizedList(new ArrayList<>());
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[] inventory) {
		//Clears current inventory if given array is empty - Used for testing purposes
		if(inventory.length == 0) {
			this.inventory.clear();
			return;
		}
		for(BookInventoryInfo bookInfo : inventory)
		{
			if(bookInfo != null)
				this.inventory.add(bookInfo);
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		synchronized (availabilityLock) {
			BookInventoryInfo bookInfo = getBookInfoByName(book);
			if (bookInfo != null) {
				if (bookInfo.reduceAmount()) { // If amount of book is now zero due to amount reduction
					this.inventory.remove(bookInfo);
				}
				return OrderResult.SUCCESSFULLY_TAKEN;
			}
		}
		return OrderResult.NOT_IN_STOCK;
	}
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		BookInventoryInfo bookInfo;
		synchronized (availabilityLock) {
			bookInfo = getBookInfoByName(book);
		}
		return (bookInfo != null) ? bookInfo.getPrice() : -1;
	}
	
	private BookInventoryInfo getBookInfoByName(String book)
	{
		for (BookInventoryInfo bookInformation : inventory) {
			if (bookInformation.getBookTitle().equals(book)) {
				return bookInformation;
			}
		}
		return null;
	}
	
	/**
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		 try {
			 HashMap<String, Integer> inventory = new HashMap<>();
			 for(BookInventoryInfo bookInfo : this.inventory)
			 {
			 	inventory.put(bookInfo.getBookTitle(), bookInfo.getAmountInInventory());
			 }
			 FileOutputStream fileOut = new FileOutputStream(filename);
			 ObjectOutputStream out = new ObjectOutputStream(fileOut);
			 out.writeObject(inventory);
			 out.close();
			 fileOut.close();
		  } catch (IOException i) {
			 i.printStackTrace();
		  }
	}
}
