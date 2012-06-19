package net.ion.isearcher.http;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;

public class EncodingUtil {

	private static final String DEFAULT_CHARSET = "ISO-8859-1";

	private static final Log LOG = LogFactory.getLog(EncodingUtil.class);

	public static String formUrlEncode(NameValuePair[] pairs, String charset) {
		try {
			return doFormUrlEncode(pairs, charset);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Encoding not supported: " + charset);
			try {
				return doFormUrlEncode(pairs, DEFAULT_CHARSET);
			} catch (UnsupportedEncodingException fatal) {
				// Should never happen. ISO-8859-1 must be supported on all JVMs
				throw new IllegalArgumentException("Encoding not supported: " + DEFAULT_CHARSET);
			}
		}
	}

	private static String doFormUrlEncode(NameValuePair[] pairs, String charset) throws UnsupportedEncodingException {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < pairs.length; i++) {
			URLCodec codec = new URLCodec();
			NameValuePair pair = pairs[i];
			if (pair.getName() != null) {
				if (i > 0) {
					buf.append("&");
				}
				buf.append(codec.encode(pair.getName(), charset));
				buf.append("=");
				if (pair.getValue() != null) {
					buf.append(codec.encode(pair.getValue(), charset));
				}
			}
		}
		return buf.toString();
	}

	public static String getString(final byte[] data, int offset, int length, String charset) {

		if (data == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}

		if (charset == null || charset.length() == 0) {
			throw new IllegalArgumentException("charset may not be null or empty");
		}

		try {
			return new String(data, offset, length, charset);
		} catch (UnsupportedEncodingException e) {

			if (LOG.isWarnEnabled()) {
				LOG.warn("Unsupported encoding: " + charset + ". System encoding used");
			}
			return new String(data, offset, length);
		}
	}


	public static String getString(final byte[] data, String charset) {
		return getString(data, 0, data.length, charset);
	}

	public static byte[] getBytes(final String data, String charset) {

		if (data == null) {
			throw new IllegalArgumentException("data may not be null");
		}

		if (charset == null || charset.length() == 0) {
			throw new IllegalArgumentException("charset may not be null or empty");
		}

		try {
			return data.getBytes(charset);
		} catch (UnsupportedEncodingException e) {

			if (LOG.isWarnEnabled()) {
				LOG.warn("Unsupported encoding: " + charset + ". System encoding used.");
			}

			return data.getBytes();
		}
	}

	public static byte[] getAsciiBytes(final String data) {

		if (data == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}

		try {
			return data.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("HttpClient requires ASCII support");
		}
	}


	public static String getAsciiString(final byte[] data, int offset, int length) {

		if (data == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}

		try {
			return new String(data, offset, length, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("HttpClient requires ASCII support");
		}
	}

	public static String getAsciiString(final byte[] data) {
		return getAsciiString(data, 0, data.length);
	}

	private EncodingUtil() {
	}

}
