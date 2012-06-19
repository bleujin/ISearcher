package net.ion.isearcher.exception;

public class ISearcerException extends Exception {
	
	private static final long serialVersionUID = 6045265349277187822L;
	public ISearcerException(String message) {
		super(message);
	}

	public ISearcerException(String message, Throwable cause) {
		super(message, cause);
	}

}
