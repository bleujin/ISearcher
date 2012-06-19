package net.ion.isearcher.indexer.collect;

import net.ion.isearcher.exception.ISearcerException;

public class IndexCancelException extends ISearcerException {
	
	private static final long serialVersionUID = -1146336586525798624L;
	public IndexCancelException(String message){
		super(message) ;
	}
	
	public IndexCancelException(String message, Throwable cause){
		super(message, cause) ;
	}
	
}
