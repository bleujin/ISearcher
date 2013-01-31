package net.ion.nsearcher.index.channel.persistor;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class ObjectPersistor<E> implements Persistor<E>{

	public int getDataSize() {
		return VARIABLE_SIZE;
	}

	public int getElementSize(E element) {
		return 0 ;
	}
	
	public E read(DataInputStream stream) throws IOException {
		ObjectInputStream input = new ObjectInputStream(stream) ;
		try {
			return (E)input.readObject() ;
		} catch (ClassNotFoundException e) {
			throw new IOException(e.getMessage()) ;
		}
	}

	public void write(E element, DataOutputStream stream) throws IOException {
		if (! (element instanceof Serializable)) throw new IOException("must be serialized") ;
		ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
		(new ObjectOutputStream(bout)).writeObject(element) ;
		byte[] objectArrays = bout.toByteArray() ;
		stream.write(objectArrays) ;
		stream.writeInt(objectArrays.length) ;
	}

}
