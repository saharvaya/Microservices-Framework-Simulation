/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

/**
 * An Event intended to start a new Delivery.
 */
public class DeliveryEvent implements Event<Boolean> {

    //Fields
    private OrderReceipt receipt; // Order receipt containing details for delivery

    //Constructor
    public DeliveryEvent(OrderReceipt receipt)
    {
        this.receipt = receipt;
    }

    //Getters
    public OrderReceipt getReceipt()
    {
        return this.receipt;
    }
}
