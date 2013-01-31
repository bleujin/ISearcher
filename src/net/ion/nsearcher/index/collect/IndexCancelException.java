package net.ion.nsearcher.index.collect;

import net.ion.nsearcher.exception.SearcerException;


public class IndexCancelException extends SearcerException {
	
	private static final long serialVersionUID = -1146336586525798624L;
	public IndexCancelException(String message){
		super(message) ;
	}
	
	public IndexCancelException(String message, Throwable cause){
		super(message, cause) ;
	}
	
}
