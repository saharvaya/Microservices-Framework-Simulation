/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * A Broadcast to be sent by system timer service to notify services duration amount of ticks past
 * broadcast receivers will terminate as soon as getting this broadcast.
 */
public class TimeOutBroadcast implements Broadcast {

}
