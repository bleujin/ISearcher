package net.ion.nsearcher.index.channel.persistor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;


public abstract class AbstractPersistor<E> implements Persistor<E> {

	public int getElementSize(E element) {
		try {
			byte[] byteArray;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
			this.write(element, outputStream);
			byteArray = byteArrayOutputStream.toByteArray();
			return byteArray.length;
		} catch (Exception e) {
			return this.getDataSize();
		}
	}
}
