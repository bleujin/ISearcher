package net.ion.nsearcher.exception;

public class SearcerException extends Exception {
	
	private static final long serialVersionUID = 6045265349277187822L;
	public SearcerException(String message) {
		super(message);
	}

	public SearcerException(String message, Throwable cause) {
		super(message, cause);
	}

}
