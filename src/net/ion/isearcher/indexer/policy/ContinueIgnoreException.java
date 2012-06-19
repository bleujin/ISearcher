package net.ion.isearcher.indexer.policy;

import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.isearcher.indexer.write.IWriter;

public class ContinueIgnoreException implements ExceptionPolicy {

	private boolean end = false;

	public boolean isEnd() {
		return end;
	}

	public void whenExceptionOccured(IWriter writer, IOException e) {
		Debug.debug(e);
	}

}
