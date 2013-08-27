package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.common.AbDocument.Action;
import net.ion.nsearcher.index.IndexSession;

public class DeleteOnlyPolicy extends AbstractWritePolicy{

	public DeleteOnlyPolicy(){
	}
	
	public Action apply(IndexSession session, WriteDocument doc) throws IOException {
		return session.deleteDocument(doc);
	}

}
