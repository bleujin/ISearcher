package net.ion.isearcher.indexer.collect;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.db.RowsUtils;
import net.ion.framework.db.bean.ResultSetHandler;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.CaseInsensitiveHashMap;
import net.ion.framework.util.Debug;
import net.ion.isearcher.crawler.util.HashFunction;
import net.ion.isearcher.events.DataRowEvent;
import net.ion.isearcher.events.KeyValues;
import net.ion.isearcher.exception.ShutDownException;
import net.ion.isearcher.indexer.handler.DataRowDocumentHandler;
import net.ion.isearcher.indexer.handler.DocumentHandler;

import org.apache.commons.lang.ArrayUtils;

public class DatabaseCollector extends AbstractCollector {

	private Queryable query;
	private String[] keyColumns;
	private String[] parentColumns;
	private DocumentHandler handler;

	public DatabaseCollector(Queryable query, String keyColumn) {
		this(query, new String[] { keyColumn });
	}

	public DatabaseCollector(Queryable query, String[] keyColumns) {
		this(query, keyColumns, keyColumns);
	}

	public DatabaseCollector(Queryable cmd, String[] keyColumns, String[] parentColumns) {
		super(DEFAULT_NAME + "/" + cmd.getProcSQL());
		this.query = cmd;
		this.keyColumns = keyColumns;
		this.parentColumns = parentColumns;
		this.handler = new DataRowDocumentHandler();
	}

	public void collect() {
		try {
			fireStart();
			DocumentMakerHandler handler = new DocumentMakerHandler(this);
			query.execHandlerQuery(handler);
		} catch (ShutDownException ignore) {
			Debug.debug("SHUTDOWN......");
		} catch (SQLException ignore) { // TODO when occured exception, what doing ?
			ignore.printStackTrace();
		} finally {
			fireEnd();
		}
	}

	public Queryable getQuery() {
		return query;
	}

	public String[] getKeyColumns() {
		return keyColumns;
	}

	private static class DocumentMakerHandler implements ResultSetHandler {

		private static final long serialVersionUID = -4197073445282957962L;
		private DatabaseCollector parent;

		public DocumentMakerHandler(DatabaseCollector parent) {
			this.parent = parent;
		}

		private Map<String, Object> currentRowToMap(ResultSetMetaData rsmd, ResultSet rs) throws SQLException {
			Map<String, Object> result = new CaseInsensitiveHashMap<Object>();
			int cols = rsmd.getColumnCount();
			for (int i = 1; i <= cols; i++)
				result.put(rsmd.getColumnName(i), getValue(rsmd, rs, i));

			return result;
		}

		private static Object getValue(ResultSetMetaData meta, ResultSet rs, int i) throws SQLException {
			// TODO BLOB..
			if (meta.getColumnType(i) == 2005) // clob..
				return RowsUtils.clobToString(rs.getClob(i));
			else
				return rs.getObject(i);
		}

		public Object handle(ResultSet rs) throws SQLException {
			ResultSetMetaData meta = rs.getMetaData();

			long beforeKey = 0L;
			KeyValues keyValues = KeyValues.create();
			while (rs.next()) {

				if (parent.isShutDownState()) {
					throw ShutDownException.throwIt(this.getClass());
				}

				long currKey = newKew(rs);

				Map<String, Object> row = currentRowToMap(meta, rs);

				if (isNewEvent(beforeKey, currKey)) {
					if (beforeKey != 0L)
						parent.fireCollectEvent(new DataRowEvent(parent, keyValues));

					keyValues = KeyValues.create();
					for (Entry<String, Object> entry : row.entrySet()) {
						keyValues.add(entry.getKey(), entry.getValue());
					}

					beforeKey = currKey;
				} else {
					for (Entry<String, Object> entry : row.entrySet()) {
						if (ArrayUtils.contains(parent.getKeyColumns(), entry.getKey()))
							continue;
						keyValues.add(entry.getKey(), entry.getValue());
					}
				}
			}
			parent.fireCollectEvent(new DataRowEvent(parent, keyValues));

			return null;
		}

		private boolean isNewEvent(long beforeKey, long currKey) {
			return beforeKey != currKey;
		}

		private long newKew(ResultSet rs) throws SQLException {
			StringBuilder newKeyBuilder = new StringBuilder();
			for (String key : parent.getKeyColumns()) {
				newKeyBuilder.append(rs.getString(key) + " ");
			}
			long currKey = HashFunction.hashGeneral(newKeyBuilder.toString());
			return currKey;
		}
	}

	public DocumentHandler getDocumentHandler() {
		return handler;
	}

	public void setDocumentHandler(DocumentHandler handler) {
		this.handler = handler;
	}

}
