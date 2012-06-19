package net.ion.isearcher.indexer.policy;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.impl.HashBean;
import net.ion.isearcher.indexer.write.IWriter;

public class MergePolicy extends AbstractWritePolicy {

	private boolean isFirst = true;

	private boolean isFirst() {
		return isFirst;
	}

	private Object mutex = new Object();
	private Data data = null;

	public Action apply(final IWriter writer, MyDocument doc) throws IOException {
		if (isFirst()) {
			synchronized (mutex) {
				if (isFirst()) {
					final FutureData future = new FutureData();
					new Thread() {
						public void run() {
							RealData realdata;
							try {
								realdata = new RealData(writer);
								future.setRealData(realdata);
							} catch (IOException ignore) {
							}
						}
					}.start();
					data = future;
					isFirst = false ;
				}
			}
		}

		try {
			Map<String, HashBean> hashData = data.getHashData();

			String idValue = doc.getIdValue();
			String newValue = doc.getBodyValue();

			if (doc.getAction().isDelete()) {
				return writer.deleteDocument(doc);
			}

			if (hashData.containsKey(idValue)) {
				String oldValue = hashData.get(idValue).getBodyValue();
				if (oldValue.equals(newValue)) // same key and same body
					return Action.Unknown;
				else
					return writer.updateDocument(doc);
			} else {
				return writer.insertDocument(doc);
			}

		} catch (NullPointerException ex) {
			throw new IOException("exception.isearcher.document.index:" + ex.getMessage());
		} catch (ExecutionException ex) {
			throw new IOException(ex.getMessage());
		}

	}

}
