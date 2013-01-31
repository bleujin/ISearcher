package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyDocument.Action;
import net.ion.nsearcher.index.IndexSession;

import org.apache.lucene.index.Term;

public class DeleteTermInsertPolicy extends AbstractWritePolicy {

	private Term term;

	public DeleteTermInsertPolicy(String field, String value) {
		this.term = new Term(field, value);
	}

	@Override
	public void begin(IndexSession session){
		try {
			session.deleteTerm(term);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Action apply(IndexSession session, MyDocument doc) throws IOException {
		return session.insertDocument(doc);
	}

}
