package net.ion.nsearcher.index.policy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.common.AbDocument.Action;
import net.ion.nsearcher.index.IndexSession;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

public class MergePolicy extends AbstractWritePolicy {

	private Map<String, HashBean> hashData = new HashMap<String, HashBean>();

	public void begin(IndexSession session) throws IOException {
		
		IndexReader reader = session.reader() ;
		for (int i = 0, last = reader.maxDoc(); i < last; i++) {
//			if (reader.isDeleted(i))
//				continue;
			Document doc = reader.document(i);
			HashBean bean = new HashBean(session.getIdValue(doc), session.getBodyValue(doc));
			hashData.put(session.getIdValue(doc), bean);
		}
	}
	
	public Action apply(final IndexSession wsession, WriteDocument doc) throws IOException {
		try {
			String idValue = doc.idValue();
			String newValue = doc.bodyValue();

			if (doc.getAction().isDelete()) {
				return wsession.deleteDocument(doc);
			}

			if (hashData.containsKey(idValue)) {
				String oldValue = hashData.get(idValue).getBodyValue();
				if (oldValue != null && oldValue.equals(newValue)) // same key and same body
					return Action.Unknown;
				else
					return wsession.updateDocument(doc);
			} else {
				return wsession.insertDocument(doc);
			}

		} catch (NullPointerException ex) {
			throw new IOException("exception.isearcher.document.index:" + ex.getMessage());
		}

	}

}
