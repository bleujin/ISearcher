package net.ion.nsearcher.index.channel.persistor;

import java.io.IOException;

public interface Stack<E> {
    public void push(E element) throws IOException;
    public E pop() throws IOException;

}
