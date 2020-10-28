/*
 * Sumbitters:
 * Itay Bouganim, ID:305278384
 * Sahar Vaya, ID:205583453
 */
package bgu.spl.mics;

/**
 * a callback is a function designed to be called when a message is received.
 */
public interface Callback<T> {

    public void call(T c);

}
