/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * A Broadcast to notify subscribers of the current system timer ick
 */
public class TickBroadcast implements Broadcast {

	//Fields
	private int currentTick; // The current tick broadcast

	//Constructor
	public TickBroadcast(int currentTick) {
		this.currentTick = currentTick;
	}

	//Getters
	public int getCurrentTick() {
		return currentTick;
	}
}
