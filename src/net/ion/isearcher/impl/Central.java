package net.ion.isearcher.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.ion.framework.db.Page;
import net.ion.framework.logging.LogBroker;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.indexer.write.Mutex;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.SearchRequest;
import net.ion.isearcher.searcher.SearchResponse;
import net.ion.isearcher.searcher.filter.FilterUtil;
import net.ion.isearcher.searcher.processor.PostProcessor;
import net.ion.isearcher.searcher.processor.SearchTask;
import net.ion.isearcher.util.CloseUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

public abstract class Central {

	protected static Map<String, Central> STORE = new ConcurrentHashMap<String, Central>();
	private static ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(2);

	private String lastOwner;
	private DaemonIndexer dindexer = null ;
	private Mutex mutex = new Mutex();

	public static Central createOrGet(File path) throws IOException {
		synchronized (Central.class) {
			if (!path.exists()) {
				path.mkdirs();
			}
		}

		return createOrGet(FSDirectory.open(path));
	}

	static MultiCentral createOrGet(Directory buffer, Directory store) {
		synchronized (STORE) {
			try {
				String storeKey = buffer.getLockID() + store.getLockID();
				if (STORE.containsKey(storeKey)) {
					Central saved = STORE.get(storeKey);
					return (MultiCentral)saved;
				} else {
					MultiCentral central = new MultiCentral(buffer, store);
					STORE.put(storeKey, central);

					return central;
				}
			} catch (IOException ex) {
				throw new IllegalStateException(ex.getMessage());
			}
		}
	}

	public static Central createOrGet(Directory dir) {
		synchronized (STORE) {
			try {
				String storeKey = dir.getLockID();
				if (STORE.containsKey(storeKey)) {
					return STORE.get(storeKey);
				} else {
					Central central = new SingleCentral(dir);
					STORE.put(storeKey, central);

					return central;
				}
			} catch (IOException ex) {
				throw new IllegalStateException(ex.getMessage());
			}
		}
	}
	
	public static Central createOrGet(IndexReader reader) {
		return new ReadOnlyCentral(reader);
	}
	
	public static synchronized void clearStore() {
		Central[] centrals = STORE.values().toArray(new Central[0]);
		for (Central central : centrals) {
			try {
				central.destroySelf();
				LogBroker.getLogger(Central.class).info(central + ": destroyed");
				if (central != null && central.getDir() != null && IndexWriter.isLocked(central.getDir()))
					IndexWriter.unlock(central.getDir());
			} catch (Throwable ignore) {
				ignore.printStackTrace();
			}
		}
		STORE.clear();
	}

	public static ScheduledExecutorService getScheduler() {
		return Central.SCHEDULER;
	}

	protected static class SearcherCloser implements Runnable {
		private IndexSearcher oldSearcher;

		SearcherCloser(IndexSearcher oldSearcher) {
			this.oldSearcher = oldSearcher;
		}

		public void run() {
			CloseUtils.silentClose(oldSearcher);
		}

	}
	public ISearcher newSearcher() throws IOException {
		ISearcher searcher = new MySearcher(this);
		return searcher;
	}


	public IReader newReader() throws CorruptIndexException, IOException {
		return new IReader(this.getIndexReader());
	}



	public IWriter newIndexer(Analyzer analyzer) throws LockObtainFailedException, IOException {
		return new DefaultWriter(this, analyzer, mutex);
	}
	
	public IWriter newDaemonIndexer(Analyzer analyzer) throws LockObtainFailedException, IOException {
//		return CacheWriter.create(this, analyzer) ;
		return CacheCopyWriter.create(this, analyzer) ;
		// return DaemonWriter.create(this, analyzer) ;
	}

	public IWriter testIndexer(Analyzer analyzer) throws LockObtainFailedException, IOException {
		return new TemporaryWriter(this, analyzer, mutex);
	}
	
	public synchronized DaemonIndexer newDaemonHander() {
		if (dindexer == null){
			this.dindexer = DaemonIndexer.create(this) ;
		}
		return dindexer;
	}

	protected final Mutex getMutex(){
		return mutex ;
	}
	
	protected abstract IndexWriter getIndexWriter(IWriter owner, Analyzer analyzer) throws LockObtainFailedException, IOException ;

	protected abstract IndexSearcher getIndexSearcher() throws IOException ;

	protected abstract void createNewWriter(Analyzer analyzer, IWriter owner) throws IOException ;

	protected abstract IndexReader getIndexReader() throws IOException;

	public abstract void destroySelf();

	public abstract Directory getDir();

	public String getOwner() {
		return lastOwner;
	}

	public void setOwner(String newOwner) {
		this.lastOwner = newOwner;
	}

	public abstract ICentralFilter centralFilter();

	public abstract void copyFrom(Analyzer analyzer, Directory... srcDirs) throws IOException;

}



class ReadOnlyCentral extends Central {

	private IndexSearcher searcher ;
	private IndexReader reader ;
	private CentralFilter cfilter = new CentralFilter() ;
	
	public ReadOnlyCentral(IndexReader reader) {
		this.reader = reader ;
		this.searcher = new IndexSearcher(reader) ;
	}

	
	public ISearcher newSearcher() throws IOException {
		ISearcher searcher = new ReadOnlySearcher(this);
		return searcher;
	}

	public IReader newReader() throws CorruptIndexException, IOException {
		return new IReader(reader);
	}

	public IWriter newIndexer(Analyzer analyzer) throws LockObtainFailedException, IOException {
		throw new UnsupportedOperationException("readonly central") ;
	}

	public IWriter newDaemonIndexer(Analyzer analyzer) throws LockObtainFailedException, IOException {
		throw new UnsupportedOperationException("readonly central") ;
	}

	public IWriter testIndexer(Analyzer analyzer) throws LockObtainFailedException, IOException {
		throw new UnsupportedOperationException("readonly central") ;
	}

	public synchronized DaemonIndexer newDaemonHander() {
		throw new UnsupportedOperationException("readonly central") ;
	}
	
	@Override
	public void copyFrom(Analyzer analyzer, Directory... srcDirs) throws IOException {
		throw new UnsupportedOperationException("readonly central") ;
	}

	@Override
	protected void createNewWriter(Analyzer analyzer, IWriter owner) throws IOException {
		throw new UnsupportedOperationException("readonly central") ;
	}

	@Override
	public void destroySelf() {
		IOUtil.closeQuietly(searcher) ;
		IOUtil.closeQuietly(reader) ;
	}

	
	
	
	@Override
	public Directory getDir() {
		return reader.directory();
	}

	@Override
	public ICentralFilter centralFilter() {
		return cfilter;
	}

	
	@Override
	protected IndexReader getIndexReader() throws IOException {
		return reader;
	}

	@Override
	protected synchronized IndexSearcher getIndexSearcher() throws IOException {
		IndexReader newReader = IndexReader.openIfChanged(reader);
		if (newReader != null){
			IOUtil.close(searcher) ;
			IOUtil.close(reader) ;
			this.reader = newReader ;
			this.searcher = new IndexSearcher(reader) ;
		}
		
		return searcher;
	}

	@Override
	protected IndexWriter getIndexWriter(IWriter owner, Analyzer analyzer) throws LockObtainFailedException, IOException {
		throw new UnsupportedOperationException("readonly central") ;
	}

}



class ReadOnlySearcher implements ISearcher {
	
	private Central central ;
	private Set<Filter> filters = new HashSet<Filter>();

	private Set<PostProcessor> postListeners = new HashSet<PostProcessor>();
	private Set<PreProcessor> preListeners = new HashSet<PreProcessor>();

	ReadOnlySearcher(final Central central) throws IOException {
		this.central = central ;
	}

	public final ISearcher andFilter(final Filter filter){
		if (filter == null) return this;
		filters.add(central.centralFilter().getFilter(filter)) ;
		// filters.add(filter) ;
		return this ;
	}
	

	public final SearchResponse searchTest(final String query) throws ParseException, IOException {
		return search(SearchRequest.test(query)) ; 
	}
	
	public Set<Filter> confirmFilterSet(ISearchRequest srequest){
		Set<Filter> result = new HashSet<Filter>() ;
		for (Filter filter : filters) {
			result.add(central.centralFilter().getKeyFilter(filter)) ;
		}
		
		if (srequest.getFilter() != null) {
			result.add(srequest.getFilter()) ;
		}
		return result ;
	}
	
	public final SearchResponse search(final ISearchRequest srequest) throws ParseException, IOException {
		handlePreprocess() ;
		
		Query query =  srequest.getQuery() ;
		Sort sort = srequest.getSort() ;
		
		Filter currentFilter = makeFilter(srequest);
		if (currentFilter == null) currentFilter = new QueryWrapperFilter(query) ;

		long startTime = System.currentTimeMillis() ;
		TopDocs topDocs = getIndexSearcher().search(query, currentFilter, getLimitCount(srequest.getPage()), sort) ;
		SearchResponse result = makeSearchResult(srequest, topDocs, startTime);
		
		return result ; 
	}
	
	private int getLimitCount(Page page) {
		return page.getPageNo() * page.getListNum() + 1;
	}

	private void handlePreprocess() {
		for (PreProcessor pre : preListeners) {
			pre.process() ;
		}
	}

	public final SearchResponse searchLimit(final ISearchRequest srequest) throws ParseException, IOException {
		handlePreprocess() ;
		
		Filter currentFilter = makeFilter(srequest);
		Query query = srequest.getQuery();
		
		long startTime = System.currentTimeMillis() ;
		TopDocs topDocs = null ;
		if (currentFilter == null) {
			topDocs = getIndexSearcher().search(query, getLimitCount(srequest.getPage())) ;
		} else {
			topDocs = getIndexSearcher().search(query, currentFilter, getLimitCount(srequest.getPage())) ;
		}

		SearchResponse result = makeSearchResult(srequest, topDocs, startTime);
		return result ; 
	}

	private Filter makeFilter(final ISearchRequest srequest) {
		Filter currentFilter = FilterUtil.and(filters) ;
		if (srequest.getFilter() != null) {
			currentFilter = FilterUtil.and(currentFilter, srequest.getFilter()) ;
		}
		return currentFilter;
	}
	
	private SearchResponse makeSearchResult(final ISearchRequest srequest, final TopDocs docs, final long startTime) throws CorruptIndexException, IOException {
		SearchResponse result = SearchResponse.create(this, srequest, docs, startTime) ;
		
		for(PostProcessor processor : postListeners) {
			processor.postNotify(new SearchTask(srequest, result)) ;
		}
		return result ;
	}
	
	public final MyDocument doc(final int docId) throws IOException{
		return MyDocument.loadDocument(getIndexSearcher().doc(docId)) ;
	}
	
	public final Explanation getDocumentExplain(final ISearchRequest srequest, final int docId) throws IOException, ParseException {
		return getIndexSearcher().explain(srequest.getQuery(), docId);
	}

	
	// use only test..
	public final void forceClose() throws IOException{
		// CloseUtils.silentClose(this.dir) ;
		CloseUtils.silentClose(getIndexSearcher()) ;
	}

	IndexSearcher getIndexSearcher() throws IOException {
		return central.getIndexSearcher() ;
//		return this.searcher = central.getIndexSearcher() ;
	}
	
	public void reopen() throws IOException{
		forceClose() ;
	}

//	public final boolean isModified(final ISearcher that) throws IOException{
//		return this.getIndexSearcher() != that.getIndexSearcher() ;
//	}
	
	final MyDocument[] allDocs() throws IOException {
		List<MyDocument> list = ListUtil.newList() ;
		IndexReader indexReader = getIndexSearcher().getIndexReader();
		for (int i = 0, max = indexReader.maxDoc(); i < max; i++) {
			MyDocument doc = MyDocument.loadDocument(indexReader.document(i)) ;
			list.add(doc) ;
		}
		return list.toArray(new MyDocument[0]);
	}

	public final void addPostListener(final PostProcessor processor) {
		postListeners.add(processor) ;
	}
	public final void addPreListener(final PreProcessor processor) {
		preListeners.add(processor) ;
	}
} 


