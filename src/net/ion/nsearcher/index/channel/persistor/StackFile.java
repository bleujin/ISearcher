package net.ion.nsearcher.index.channel.persistor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.ion.framework.util.IOUtil;

public class StackFile<E> implements Stack<E> {

	private RandomAccessFile dataFile;
	private Persistor<E> persistor;
	private int persistorSize;

	public StackFile(String fileName, Persistor<E> persistor) throws IOException {
		this.persistor = persistor;
		this.persistorSize = persistor.getDataSize();
		this.dataFile = new RandomAccessFile(fileName, "rw");
		dataFile.seek(dataFile.length());
	}

	public void push(E element) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		persistor.write(element, dos);
		byte[] bytes = baos.toByteArray();
		dataFile.write(bytes);
	}

	public E pop() throws IOException {
		int length = (int) dataFile.length();
		
		byte[] buffer = null ;
		if (persistorSize == Persistor.VARIABLE_SIZE){ // variable length
			byte intLength = Integer.SIZE / 8 ;
			dataFile.seek(length - intLength) ; 
			int dataSize = dataFile.readInt() ; 
			dataFile.seek(length - intLength - dataSize) ;
			buffer = new byte[dataSize];
			dataFile.read(buffer) ;
			dataFile.setLength(length - intLength - dataSize);
		} else { // fixed length
			dataFile.seek(length - persistorSize);
			buffer = new byte[persistorSize];
			dataFile.read(buffer);
			dataFile.setLength(length - persistorSize);
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		DataInputStream dis = new DataInputStream(bais);
		return persistor.read(dis);
	}

	public void close() {
		IOUtil.closeQuietly(this.dataFile) ;
	}
}
