package net.ion.nsearcher.common;

import net.ion.framework.util.Debug;

import org.apache.commons.logging.Log;

public class DebugLog implements Log {


	public void trace(Object arg0) {
		Debug.debug("trace", arg0) ;
	}

	public void trace(Object arg0, Throwable arg1) {
		Debug.debug("trace", arg0, arg1) ;
	}

	public void warn(Object arg0) {
		Debug.debug("warn", arg0) ;
	}

	public void warn(Object arg0, Throwable arg1) {
		Debug.debug("warn", arg0, arg1) ;
	}
	public void debug(Object arg0) {
		Debug.debug("debug", arg0) ;
	}

	public void debug(Object arg0, Throwable arg1) {
		Debug.debug("debug", arg0, arg1) ;
	}

	public void error(Object arg0) {
		Debug.debug("error", arg0) ;
	}

	public void error(Object arg0, Throwable arg1) {
		Debug.debug("error", arg0, arg1) ;
	}

	public void fatal(Object arg0) {
		Debug.debug("fatal", arg0) ;
	}

	public void fatal(Object arg0, Throwable arg1) {
		Debug.debug("fatal", arg0, arg1) ;
	}

	public void info(Object arg0) {
		Debug.debug("info", arg0) ;
	}

	public void info(Object arg0, Throwable arg1) {
		Debug.debug("info", arg0, arg1) ;
	}

	public boolean isDebugEnabled() {
		return true;
	}

	public boolean isErrorEnabled() {
		return true;
	}

	public boolean isFatalEnabled() {
		return true;
	}

	public boolean isInfoEnabled() {
		return true;
	}

	public boolean isTraceEnabled() {
		return true;
	}

	public boolean isWarnEnabled() {
		return true;
	}


}
