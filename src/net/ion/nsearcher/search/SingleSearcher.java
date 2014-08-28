package net.ion.nsearcher.search;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.reader.InfoReader;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;

public class SingleSearcher implements Closeable, ISearchable {

	private DirectoryReader dreader;
	private IndexSearcher isearcher;
	private ExecutorService es;
	private CachedFilter filters = new CachedFilter();

	private final Central central;

	private SingleSearcher(Central central, DirectoryReader dreader) {
		this.central = central;
		this.dreader = dreader;
		this.isearcher = new IndexSearcher(dreader);
		this.es = central.searchConfig().searchExecutor();
	}

	public static SingleSearcher create(SearchConfig sconfig, Central central) throws IOException {
		return new SingleSearcher(central, DirectoryReader.open(central.dir()));
	}

	public SearchResponse search(SearchRequest sreq, Filter filters) throws IOException {
		reloadReader();

		Lock rlock = central.readLock() ;
		boolean locked = false ;
		try {
			locked = rlock.tryLock(); 
			long startTime = System.currentTimeMillis();
			
			TopDocs docs = isearcher.search(sreq.query(), filters, sreq.limit(), sreq.sort());
			return SearchResponse.create(this, sreq, docs, startTime);
		} finally {
			if (locked) rlock.unlock(); 
		}
	}

	public int totalCount(SearchRequest sreq, Filter filters) {
		try {
			// reloadReader() ;
			TopDocs docs = isearcher.search(sreq.query(), filters, Integer.MAX_VALUE);
			return docs.totalHits;
		} catch (IOException e) {
			return -1;
		}
	}

	public <T> Future<T> submit(Callable<T> task) {
		return es.submit(task);
	}

	private void reloadReader() throws IOException {
		boolean locked = false;
		Lock lock = central.writeLock();
		try {
			locked = lock.tryLock(); 
			if (locked){
				DirectoryReader newReader = DirectoryReader.openIfChanged(dreader);
				// try {
				// newReader = IndexReader.openIfChanged(ireader);
				// } catch(AlreadyClosedException e){
				// newReader = IndexReader.open(central.dir()) ;
				// }
	
				if (newReader != null) {
					// TODO : after closer
					
					dreader.close();
	
					filters.clear();
					this.dreader = newReader;
					this.isearcher = new IndexSearcher(this.dreader);
				}
			}
		} finally {
			if (locked) lock.unlock();
		}
	}

	public ReadDocument doc(int docId, SearchRequest request) throws IOException {
		Set<String> fields = request.selectorField();
		if (fields == null || fields.size() == 0) {
			return ReadDocument.loadDocument(dreader.document(docId));
		}
		return ReadDocument.loadDocument(dreader.document(docId, request.selectorField()));
	}

	public InfoReader reader() {
		return InfoReader.create(this);
	}

	public IndexReader indexReader() throws IOException {

		reloadReader();
		return isearcher.getIndexReader();
	}

	public DirectoryReader dirReader() {
		return dreader;
	}

	public synchronized void close() throws IOException {
		isearcher.getIndexReader().close();
		dreader.close();
	}

	public CachedFilter cachedFilter() {
		return filters;
	}

	public Central central() {
		return this.central;
	}

	public SearchConfig searchConfig() {
		return central.searchConfig();
	}

}
