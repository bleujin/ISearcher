package net.ion.isearcher.indexer.policy;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.indexer.write.IWriter;

import org.apache.lucene.index.Term;

public class DeleteTermPolicy extends AbstractWritePolicy {
	private Term term;

	public DeleteTermPolicy(Term term) {
		this.term = term;
	}

	@Override
	public void begin(IWriter writer){
		try {
			writer.deleteTerm(term);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Action apply(IWriter writer, MyDocument doc) throws IOException {
		return Action.Delete;
	}

}
