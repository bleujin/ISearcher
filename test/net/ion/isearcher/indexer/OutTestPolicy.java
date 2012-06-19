package net.ion.isearcher.indexer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.common.IKeywordField;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.events.ICollectorEvent;
import net.ion.isearcher.impl.HashBean;
import net.ion.isearcher.indexer.policy.AbstractWritePolicy;
import net.ion.isearcher.indexer.policy.Data;
import net.ion.isearcher.indexer.policy.FutureData;
import net.ion.isearcher.indexer.policy.RealData;
import net.ion.isearcher.indexer.write.IWriter;

public class OutTestPolicy extends AbstractWritePolicy  implements IKeywordField {

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
			Map<String, HashBean> datas = data.getHashData();

			String idValue = doc.getIdValue();
			if (datas.containsKey(idValue)) {
				String oldValue = datas.get(idValue).getBodyValue();
				String newValue = doc.get(ICollectorEvent.ISBody);

				Debug.debug("UPDATE", idValue, "Modified:" + StringUtil.equals(oldValue, newValue));
				return Action.Update;
			} else {
				Debug.debug("INSERT", idValue, doc.get(ISKey), datas.size());
				return Action.Insert;
			}

		} catch (ExecutionException ex) {
			throw new IOException(ex.getMessage());
		}

	}
}
