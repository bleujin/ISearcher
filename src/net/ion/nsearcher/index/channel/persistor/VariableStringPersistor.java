package net.ion.nsearcher.index.channel.persistor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VariableStringPersistor extends AbstractPersistor<String> {
	// private static char ZERO= Character.forDigit(0,2);
	public VariableStringPersistor() {
	}

	public String read(DataInputStream stream) throws IOException {
		StringBuffer sb = new StringBuffer();
		while (stream.available() > 0) {
			sb.append(stream.readChar());
		}
		return sb.toString();

	}

	public void write(String element, DataOutputStream stream) throws IOException {
		int size = element.length();
		for (int i = 0; i < size; i++) {
			stream.writeChar(element.charAt(i));
		}
		stream.writeInt(size*2); // writeChar .. char is 2 byte.
	}

	public int getElementSize(String element) {
		return (element.length() * 2) + 1;
	}

	public int getDataSize() {
		return VARIABLE_SIZE;
	}

}
