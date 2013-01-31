package net.ion.nsearcher.index.event;

import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.HashFunction;
import net.ion.nsearcher.index.collect.DatabaseCollector;
import net.ion.nsearcher.index.collect.ICollector;

public class DataRowEvent extends CollectorEvent {

	private DatabaseCollector collector;
	private KeyValues keyValues;

	public DataRowEvent(DatabaseCollector collector, KeyValues row) {
		super() ;
		this.collector = collector;
		this.keyValues = row;
	}

	public long getEventId() {
		Queryable query = collector.getQuery();
		DBManager dbManager = query.getDBController().getDBManager();
		String jdbcPath = dbManager.getJdbcURL() + DIV + dbManager.getUserId();

		String keyString = StringUtil.deleteWhitespace(jdbcPath + DIV + query.getProcFullSQL()).toLowerCase();
		StringBuilder idField = new StringBuilder(keyString);
		String[] keys = collector.getKeyColumns();

		for (String key : keys) {
			Object obj = keyValues.get(key);
			if (obj != null)
				idField.append(DIV + obj.toString());
		}

		return HashFunction.hashGeneral(idField.toString());
	}

	public long getEventBody() {
		StringBuilder valueField = new StringBuilder();
		valueField.append(getEventId());

		for (String key : keyValues.getKeySet()) {
			Object obj = keyValues.get(key);
			if (obj != null)
				valueField.append(DIV + obj.toString());
		}
		return HashFunction.hashGeneral(DIV + valueField.toString());
	}

	public KeyValues getKeyValues() {
		return keyValues;
	}

	public String[] getKeyColumns() {
		return collector.getKeyColumns();
	}

	public ICollector getCollector() {
		return collector ;
	}

	public String getCollectorName() {
		return collector.getCollectName();
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String key : collector.getKeyColumns()) {
			builder.append(key + ":" + getKeyValues().get(key));
		}
		builder.append(" row");
		return builder.toString();
	}

}
