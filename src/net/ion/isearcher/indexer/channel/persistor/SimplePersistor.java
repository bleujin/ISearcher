package net.ion.isearcher.indexer.channel.persistor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface SimplePersistor<E> {
        void write(E element, DataOutputStream stream) throws IOException;
        E read(DataInputStream stream) throws IOException;
        public int getElementSize(E element);
}

