package net.ion.isearcher.indexer.policy;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.indexer.write.IWriter;

public class UpdatePolicy extends AbstractWritePolicy {

	public Action apply(final IWriter writer, MyDocument doc) throws IOException {
		return writer.updateDocument(doc);
	}

}
