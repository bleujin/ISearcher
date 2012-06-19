package net.ion.isearcher.indexer.policy;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.indexer.write.IWriter;

public class RecreatePolicy extends AbstractWritePolicy {

	@Override
	public void begin(IWriter writer){
		try {
			writer.deleteAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Action apply(IWriter writer, MyDocument doc) throws IOException {
		return writer.insertDocument(doc);
	}

}
