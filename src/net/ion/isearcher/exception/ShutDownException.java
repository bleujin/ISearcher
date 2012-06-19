package net.ion.isearcher.exception;



public class ShutDownException extends RuntimeException{

	private ShutDownException(String message){
		super(message) ;
	}

	private ShutDownException(Class clazz) {
		super(clazz.getName() + " shutdown") ;
	}
	
	public static ShutDownException throwIt(Class clazz){
		return new ShutDownException(clazz) ;
	}
}
