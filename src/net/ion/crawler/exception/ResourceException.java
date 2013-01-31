package net.ion.crawler.exception;

import net.ion.radon.aclient.Status;

public class ResourceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final Status status;

	public ResourceException(int code) {
		this(new Status(code));
	}

	public ResourceException(int code, String name, String description, String uri) {
		this(new Status(code, name, description, uri));
	}

	public ResourceException(int code, String name, String description, String uri, Throwable cause) {
		this(new Status(code, cause, name, description, uri), cause);
	}

	public ResourceException(int code, Throwable cause) {
		this(new Status(code, cause), cause);
	}

	public ResourceException(Status status) {
		this(status, (Throwable) null);
	}

	public ResourceException(Status status, String description) {
		this(new Status(status, description));
	}

	public ResourceException(Status status, String description, Throwable cause) {
		this(new Status(status, cause, description), cause);
	}

	public ResourceException(Status status, Throwable cause) {
		super((status == null) ? null : status.getReasonPhrase(), cause);
		this.status = status;
	}

	public ResourceException(Throwable cause) {
		this(new Status(Status.SERVER_ERROR_INTERNAL, cause), cause);
	}

	public Status getStatus() {
		return this.status;
	}

	@Override
	public String toString() {
		return getStatus().toString();
	}
}
