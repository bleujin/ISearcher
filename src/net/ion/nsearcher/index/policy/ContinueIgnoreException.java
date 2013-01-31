package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.index.IndexSession;

public class ContinueIgnoreException implements ExceptionPolicy {

	private boolean end = false;

	public boolean isEnd() {
		return end;
	}

	public void whenExceptionOccured(IndexSession session, IOException e) {
		Debug.debug(e);
	}

}
