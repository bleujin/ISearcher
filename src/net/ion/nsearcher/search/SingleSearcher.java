package net.ion.nsearcher.search;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.nsearcher.search.DocCollector.ColResult;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
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

	public SearchResponse search(final SearchRequest sreq, Filter filters) throws IOException {
		reloadReader();

		Lock rlock = central.readLock() ;
		boolean locked = false ;
		try {
			locked = rlock.tryLock(); 
			long startTime = System.currentTimeMillis();
			
			if (sreq.collector() != AbstractDocCollector.BLANK){
				final AtomicInteger total = new AtomicInteger() ;
				final List<Integer> docs = ListUtil.newList() ;
				try {
					isearcher.search(sreq.query(), filters, new Collector() {
						public void setScorer(Scorer scorer) throws IOException {
						}
						public void setNextReader(AtomicReaderContext atomicreadercontext) throws IOException {
						}
						public void collect(int docId) throws IOException {
							ColResult cresult = sreq.collector().accept(dreader, sreq, docId);
							if (cresult == ColResult.ACCEPT) {
								docs.add(docId) ;
								total.incrementAndGet() ;
							}
							if (cresult == ColResult.BREAK) throw new IllegalStateException("break") ;
						}
						public boolean acceptsDocsOutOfOrder() {
							return false;
						}
					});
				} catch(IllegalStateException ignore){
				} 
				return SearchResponse.create(this, sreq, docs, total.intValue(), startTime) ;
			}
			
			
			TopDocs docs = isearcher.search(sreq.query(), filters, sreq.limit(), sreq.sort());
			return SearchResponse.create(this, sreq, docs, startTime);
		} finally {
			if (locked) rlock.unlock(); 
		}
	}

	public Document findById(String id) throws IOException{
		TopDocs tdocs = isearcher.search(new TermQuery(new Term(IKeywordField.DocKey, id)), 1);
		ScoreDoc[] sdocs = tdocs.scoreDocs ;
		for(ScoreDoc doc : sdocs){
			return dreader.document(doc.doc) ;
		}
		return null ;
	}
	
	
	public int totalCount(SearchRequest sreq, Filter filters) {
		try {
			// reloadReader() ;
			if (sreq.collector() == AbstractDocCollector.BLANK) throw new IllegalArgumentException("with collector condition, this method meanless") ;
			
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
