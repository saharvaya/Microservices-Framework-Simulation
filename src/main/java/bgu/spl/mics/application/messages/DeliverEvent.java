/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

/**
 * An Event intended to request a vehicle and deliver to destination
 */
public class DeliverEvent implements Event<Boolean> {

    //Fields
    private OrderReceipt receipt; // A purchase receipt containing delivery information

    //Constructor
    public DeliverEvent(OrderReceipt receipt)
    {
        this.receipt = receipt;
    }

    //Getters
    public OrderReceipt getReceipt()
    {
        return this.receipt;
    }
}
