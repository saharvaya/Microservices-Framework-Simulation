/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

/**
 * Event used in order to start a new book order from a selling service.
 */
public class BookOrderEvent implements Event<OrderReceipt> {

    //Fields
    private String bookTitle; // Book title to order
    private int orderTick; // The tick for the order to initiate
    private Customer customer; // Purchasing customer

    //Constructor
    public BookOrderEvent(String bookTitle, int orderTick, Customer customer)
    {
        this.bookTitle = bookTitle;
        this.orderTick = orderTick;
        this.customer = customer;
    }

    //Getters
    public String getBookTitle()
    {
        return this.bookTitle;
    }

    public int getOrderTick()
    {
        return this.orderTick;
    }

    public Customer getCustomer()
    {
        return this.customer;
    }
}
