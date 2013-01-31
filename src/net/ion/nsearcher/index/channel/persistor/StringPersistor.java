package net.ion.nsearcher.index.channel.persistor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringPersistor extends MaxSizeAbstractPersistor<java.lang.String> {
	private static char ZERO = Character.forDigit(0, 2);

	public StringPersistor(int maxSize) {
		super(maxSize);
	}

	public String read(DataInputStream stream) throws IOException {
		int i = 0;
		StringBuffer sb = new StringBuffer();
		stream.readByte();
		byte size = stream.readByte();
		for (; i < size; i++) {
			sb.append(stream.readChar());
		}
		return sb.toString();
	}

	public void write(String element, DataOutputStream stream) throws IOException {
		if (this.maxSize <= (element.length() + 1) * 2) {
			throw new IOException("over maxLength:" + element);
		}
		int i = 0; // va acumulando la cantidad de bytes escritos
		byte size = (byte) element.length();
		stream.writeByte(0);
		stream.writeByte(size);
		for (i = 0; i < size; i++) {
			stream.writeChar(element.charAt(i));
		}
		// lo multiplico por 2 ya que cada char ocupa 2 bytes y le sumo 1 por el byte con la cantidad de chars
		i = (i + 1) * 2;
		for (; i < this.maxSize; i = i + 2) {
			stream.writeChar(ZERO);
		}
	}

	@Override
	public int getElementSize(String element) {
		return (element.length() * 2) + 1;
	}

}
