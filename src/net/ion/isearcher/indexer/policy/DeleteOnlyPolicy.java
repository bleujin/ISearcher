package net.ion.isearcher.indexer.policy;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.indexer.write.IWriter;

public class DeleteOnlyPolicy extends AbstractWritePolicy{

	public DeleteOnlyPolicy(){
	}
	
	public Action apply(IWriter writer, MyDocument doc) throws IOException {
		return writer.deleteDocument(doc);
	}

}
