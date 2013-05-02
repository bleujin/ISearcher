package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.common.MyDocument.Action;
import net.ion.nsearcher.index.IndexSession;

public class RecreatePolicy extends AbstractWritePolicy {

	@Override
	public void begin(IndexSession session){
		try {
			session.deleteAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Action apply(IndexSession session, WriteDocument doc) throws IOException {
		return session.insertDocument(doc);
	}

}
