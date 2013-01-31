package net.ion.nsearcher.exception;

public class IndexException extends Exception {
	private static final long serialVersionUID = -2217677510193438329L;

	public IndexException(String message) {
		super(message);
	}

	public IndexException(String message, Throwable cause) {
		super(message, cause);
	}

}
