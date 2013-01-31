package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyDocument.Action;
import net.ion.nsearcher.index.IndexSession;

public class DeleteOnlyPolicy extends AbstractWritePolicy{

	public DeleteOnlyPolicy(){
	}
	
	public Action apply(IndexSession session, MyDocument doc) throws IOException {
		return session.deleteDocument(doc);
	}

}
