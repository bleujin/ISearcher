package net.ion.isearcher.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.ion.framework.util.ChainMap;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.indexer.write.AbstractIWriter;
import net.ion.isearcher.indexer.write.Mutex;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

// indexWriter is Thread Safe

public class DefaultWriter extends AbstractIWriter {

	private Central central;
	private Analyzer analyzer;
	private Mutex mutex;
	private boolean modified;

	DefaultWriter(Central central, Analyzer analyzer, Mutex mutex) {
		this.central = central;
		this.analyzer = analyzer;
		this.mutex = mutex;
	}

	public synchronized void begin(String owner) throws LockObtainFailedException {
		boolean success = mutex.tryLock(this);
		if (!success)
			throw new LockObtainFailedException("exception.indexer.lock.obtain_failed:owner[" + central.getOwner() + "]");
		central.setOwner(owner);
	}

	public void commit() throws IOException {
		if (mutex.isOwner(this)){
			getIndexWriter().commit(new ChainMap().put("name", "bleujin").put("date", new Date().toString()).toMap()) ;
		}
	}
	
	public void end() throws IOException {
		if (mutex.isOwner(this)) {
			try {
				getIndexWriter().commit();
				// indexWriter.optimize() ;
				// CloseUtils.silentClose(indexWriter) ; // recycle use indexWriter...
			} catch (OutOfMemoryError ex) {
				throw new IOException(ex.getMessage());
			} finally {
				try {
					mutex.unLock(this, modified);
					modified = false;
				} catch (LockObtainFailedException ignore) {
					ignore.printStackTrace();
				}
			}
		}
	}

	public void optimize() throws IOException {
		getIndexWriter().optimize();
	}

	public boolean isLocked() throws IOException {
		return IndexWriter.isLocked(getIndexWriter().getDirectory());
	}

	public Map<String, HashBean> loadHashMap() throws IOException {
		Map<String, HashBean> map = new HashMap<String, HashBean>();

		IndexReader reader = central.getIndexReader() ;

		for (int i = 0, last = reader.maxDoc(); i < last; i++) {
			if (reader.isDeleted(i))
				continue;
			Document doc = reader.document(i);
			HashBean bean = new HashBean(getIdValue(doc), getBodyValue(doc));
			map.put(getIdValue(doc), bean);
		}

		return Collections.unmodifiableMap(map);
	}

	private String getIdValue(Document doc) {
		return doc.get(ISKey);
	}

	private String getBodyValue(Document doc) {
		return doc.get(ISBody);
	}

	@Override
	protected void myWriteDocument(MyDocument doc) throws IOException {
		Document luceneDoc = doc.toLuceneDoc();
		getIndexWriter().addDocument(luceneDoc);
		modified = true;
	}

	public Action updateDocument(MyDocument doc) throws IOException {
		getIndexWriter().updateDocument(new Term(ISKey, doc.getIdValue()), doc.toLuceneDoc());
		modified = true;
		return Action.Update;
	}

	public Action deleteDocument(MyDocument doc) throws IOException {
		getIndexWriter().deleteDocuments(new Term(ISKey, doc.getIdValue()));
		modified = true;
		return Action.Delete;
	}
	public Action appendFrom(Directory srcDir) throws IOException {
		getIndexWriter().addIndexes(srcDir) ;
		modified = true ;
		return Action.Insert;
	}		


	public Action deleteAll() throws IOException {
		getIndexWriter().deleteAll();
		modified = true;
		return Action.DeleteAll;
	}

	public Action deleteTerm(Term term) throws IOException {
		getIndexWriter().deleteDocuments(term);
		modified = true;
		return Action.DeleteAll;
	}

	public Action deleteQuery(Query query) throws IOException {
		getIndexWriter().deleteDocuments(query);
		modified = true;
		return Action.DeleteAll;
	}

	public synchronized void rollback() throws IOException {
		getIndexWriter().rollback(); // in lucene, call close after rollback
		modified = false;
		central.createNewWriter(this.analyzer, this);
	}

	public void close() throws IOException {
		if (mutex.getOwner() == this) {
			getIndexWriter().close();
		}
	}

	private IndexWriter getIndexWriter() throws LockObtainFailedException, IOException {
		return central.getIndexWriter(this, this.analyzer);
	}
}
