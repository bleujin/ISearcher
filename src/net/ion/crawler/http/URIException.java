package net.ion.crawler.http;

import org.apache.http.HttpException;

public class URIException extends HttpException {

	public URIException() {
	}

	public URIException(int reasonCode) {
		this.reasonCode = reasonCode;
	}

	public URIException(int reasonCode, String reason) {
		super(reason);
		this.reason = reason;
		this.reasonCode = reasonCode;
	}

	public URIException(String reason) {
		super(reason);
		this.reason = reason;
		reasonCode = 0;
	}

	public int getReasonCode() {
		return reasonCode;
	}

	/**
	 * @deprecated Method setReasonCode is deprecated
	 */

	public void setReasonCode(int reasonCode) {
		this.reasonCode = reasonCode;
	}

	/**
	 * @deprecated Method getReason is deprecated
	 */

	public String getReason() {
		return reason;
	}

	/**
	 * @deprecated Method setReason is deprecated
	 */

	public void setReason(String reason) {
		this.reason = reason;
	}

	public static final int UNKNOWN = 0;
	public static final int PARSING = 1;
	public static final int UNSUPPORTED_ENCODING = 2;
	public static final int ESCAPING = 3;
	public static final int PUNYCODE = 4;
	protected int reasonCode;
	protected String reason;
}
