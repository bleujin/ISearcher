package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.common.AbDocument.Action;
import net.ion.nsearcher.index.IndexSession;

import org.apache.lucene.index.Term;

public class DeleteTermPolicy extends AbstractWritePolicy {
	private Term term;

	public DeleteTermPolicy(Term term) {
		this.term = term;
	}

	@Override
	public void begin(IndexSession session){
		try {
			session.deleteTerm(term);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Action apply(IndexSession session, WriteDocument doc) throws IOException {
		return Action.Delete;
	}

}
