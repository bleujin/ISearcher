package net.ion.bleujin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface WriteJob<T> {
	public T handle(OutputStream output) throws Exception;
}


