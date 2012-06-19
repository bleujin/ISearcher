package net.ion.isearcher.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.indexer.write.AbstractIWriter;
import net.ion.isearcher.indexer.write.Mutex;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;

/*
 * WorkingWriter도 Lock을 가져야 하는가 ?
 * WorkingWriter의 정의는 아직 IndexWriter에는 반영하지 않은 RecentWriter이다. 
 *   yes : 따라서 Lock에 대해 자유로울수는 없다. commit나 rollback는 여전히 동작해야 하기 때문이다. 음.. 정말 =ㅅ=?
 *   no : 아마도 한두건 write할텐데 lock은 오버스럽지 않을까????
 *   WorkingWriter는 멀티 쓰레드 구조가 아닌 Single Thread 구조로 될것이다. 즉 requestQueue에 놓여진 순서대로 하면 되지 않을까/.
 *     -> Exception이 발생하면 어떻게 될까? 
 * DefaultWrite의 newIndexer가 호출되기 전에 이 WorkingWrite는 항상 fileIndex에 commit 시킨다.  
 */

public class RamWriter extends AbstractIWriter {

	private Central central;
	private Analyzer analyzer;
	private Mutex mutex;
	private boolean modified ;
	private IndexWriter indexWriter ;
	
	RamWriter(Central central, Analyzer analyzer, Mutex mutex) throws CorruptIndexException, LockObtainFailedException, IOException {
		this.central = central;
		this.analyzer = analyzer;
		this.mutex = mutex;
		RAMDirectory dir = new RAMDirectory();
		this.indexWriter = new IndexWriter(dir, analyzer, MaxFieldLength.UNLIMITED) ;
	}

	public synchronized void begin(String owner) throws LockObtainFailedException {
		boolean success = mutex.tryLock(this);
		if (!success)
			throw new LockObtainFailedException("exception.indexer.lock.obtain_failed:owner[" + central.getOwner() + "]");
		central.setOwner(owner) ;
	}

	public void end() throws IOException {
		if (mutex.isOwner(this)) {
			try {
				getIndexWriter().commit();
				// indexWriter.optimize() ;
				// CloseUtils.silentClose(indexWriter) ; // recycle use indexWriter...
			} catch (OutOfMemoryError ex) {
				throw new IOException(ex.getMessage()) ;
			} finally {
				try {
					mutex.unLock(this, modified);
					modified = false ;
				} catch (LockObtainFailedException ignore) {
					ignore.printStackTrace() ;
				}
			}
		}
	}

	public void optimize() throws IOException {
		getIndexWriter().optimize();
	}
	
	public boolean isLocked() throws IOException {
		return IndexWriter.isLocked(getIndexWriter().getDirectory()) ;
	}

	public Map<String, HashBean> loadHashMap() throws IOException {
		Map<String, HashBean> map = new HashMap<String, HashBean>();

		IndexReader reader = getIndexWriter().getReader();

		for (int i = 0, last = reader.maxDoc(); i < last; i++) {
			if (reader.isDeleted(i))
				continue;
			Document doc = reader.document(i);
			HashBean bean = new HashBean(getIdValue(doc), getBodyValue(doc));
			map.put(getIdValue(doc), bean);
		}

		reader.close();
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
		modified = true ;
	}

	public Action updateDocument(MyDocument doc) throws IOException {
		getIndexWriter().updateDocument(new Term(ISKey, doc.getIdValue()), doc.toLuceneDoc());
		modified = true ;
		return Action.Update ;
	}

	public Action deleteDocument(MyDocument doc) throws IOException {
		getIndexWriter().deleteDocuments(new Term(ISKey, doc.getIdValue()));
		modified = true ;
		return Action.Delete ;
	}
	
	public Action appendFrom(Directory srcDir) throws IOException {
		getIndexWriter().addIndexes(srcDir) ;
		modified = true ;
		return Action.Insert;
	}		

	public Action deleteAll() throws IOException {
		getIndexWriter().deleteAll();
		modified = true ;
		return Action.DeleteAll ;
	}
	
	public Action deleteTerm(Term term) throws IOException{
		getIndexWriter().deleteDocuments(term) ;
		modified = true ;
		return Action.DeleteAll ;
	}
	
	public Action deleteQuery(Query query) throws IOException{
		getIndexWriter().deleteDocuments(query) ;
		modified = true ;
		return Action.DeleteAll ;
	}

	public synchronized void rollback() throws IOException {
		getIndexWriter().rollback(); // in lucene, call close after rollback
		modified = false ;
		central.createNewWriter(this.analyzer, this) ;
	}
	
	public void close() throws IOException {
		getIndexWriter().close();
	}

	public void commit() throws IOException {
		getIndexWriter().commit() ;
	}
	

	private IndexWriter getIndexWriter() throws LockObtainFailedException, IOException{
		return this.indexWriter ;
	}
}
