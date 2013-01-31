package net.ion.nsearcher.index.channel.persistor;


public interface Persistor<E> extends SimplePersistor<E> {
	int VARIABLE_SIZE = 0;

	public int getDataSize();
}
