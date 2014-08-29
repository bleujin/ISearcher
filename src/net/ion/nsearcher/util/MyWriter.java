package net.ion.nsearcher.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class MyWriter extends Writer{

	
	private BufferedWriter inner;
	public MyWriter(OutputStream output, String charset) throws UnsupportedEncodingException{
		this.inner = new BufferedWriter(new OutputStreamWriter(output, charset)) ;
	}
	
	public MyWriter(OutputStream output) throws UnsupportedEncodingException {
		this(output, "UTF-8") ;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		inner.write(cbuf, off, len);
	}

	public MyWriter write(CharSequence... msg){
		for (CharSequence m : msg) {
			write(m) ;
		}
		return this ;
	}

	public MyWriter writeLn(CharSequence... msg) throws IOException{
		for (CharSequence m : msg) {
			write(m) ;
		}
		write("\n") ;
		return this ;
	}

	
	@Override
	public void flush() throws IOException {
		inner.flush(); 
	}

	@Override
	public void close() throws IOException {
		inner.close(); 
	}

}
