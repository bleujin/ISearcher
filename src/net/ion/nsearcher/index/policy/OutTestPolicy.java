package net.ion.nsearcher.index.policy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyDocument.Action;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.event.ICollectorEvent;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

public class OutTestPolicy extends AbstractWritePolicy implements IKeywordField {

	private Map<String, HashBean> hashData = new HashMap<String, HashBean>();

	public void begin(IndexSession session) throws IOException {

		IndexReader reader = session.reader();
		for (int i = 0, last = reader.maxDoc(); i < last; i++) {
			// if (reader.isDeleted(i))
			// continue;
			Document doc = reader.document(i);
			HashBean bean = new HashBean(session.getIdValue(doc), session.getBodyValue(doc));
			hashData.put(session.getIdValue(doc), bean);
		}
	}

	public Action apply(final IndexSession writer, MyDocument doc) throws IOException {

		String idValue = doc.getIdValue();
		if (hashData.containsKey(idValue)) {
			String oldValue = hashData.get(idValue).getBodyValue();
			String newValue = doc.get(ICollectorEvent.ISBody);

			Debug.debug("UPDATE", idValue, "Modified:" + StringUtil.equals(oldValue, newValue));
			return Action.Update;
		} else {
			Debug.debug("INSERT", idValue, doc.get(ISKey), hashData.size());
			return Action.Insert;
		}

	}
}
