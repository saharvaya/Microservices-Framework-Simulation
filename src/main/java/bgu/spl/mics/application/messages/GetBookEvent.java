/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/**
 * An Event intended to request a book from the inventory,
 */
public class GetBookEvent implements Event<Integer>  {

    //Fields
    private String bookTitle; // Book title to get
    private Customer customer; // Customer to retrieve the book for

    //Constructor
    public GetBookEvent(String bookTitle, Customer customer)
    {
        this.bookTitle = bookTitle;
        this.customer = customer;
    }

    //Getters
    public String getBookTitle()
    {
        return this.bookTitle;
    }
    public Customer getCustomer() { return this.customer; }
}
