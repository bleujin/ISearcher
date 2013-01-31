package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyDocument.Action;
import net.ion.nsearcher.index.IndexSession;

public class UpdatePolicy extends AbstractWritePolicy {

	public Action apply(final IndexSession session, MyDocument doc) throws IOException {
		return session.updateDocument(doc);
	}

}
