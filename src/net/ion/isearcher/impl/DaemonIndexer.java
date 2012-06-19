package net.ion.isearcher.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.isearcher.common.IKeywordField;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.indexer.write.AbstractIWriter;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.processor.LimitedChannel;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexFileNameFilter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class DaemonIndexer {

	private Central central;
	private LimitedChannel<JobEntry> channel;

	private ExecutorService eservice = Executors.newSingleThreadExecutor();

	private Map<String, Long> threadJobMap = MapUtil.newMap() ;
	
	private DaemonIndexer(Central central, int maxChannelCount) {
		this.central = central;
		this.channel = new LimitedChannel<JobEntry>(maxChannelCount);
	}

	public static DaemonIndexer create(Central central) {
		return new DaemonIndexer(central, 500);
	}

	public static DaemonIndexer create(Central central, int maxJobExtry) {
		return new DaemonIndexer(central, maxJobExtry);
	}

	public IWriter newIndexer(Analyzer analyzer) throws LockObtainFailedException, IOException {
		return central.newIndexer(analyzer);
	}

	public ISearcher newSearcher() throws IOException {
		return central.newSearcher();
	}

	ExecutorService getExecutorService() {
		return eservice;
	}

	public synchronized void shutdown() {
		eservice.shutdown();
	}

	public <T> Future<T> addIndexJob(final JobEntry<T> indexJob) {

		Callable<T> call = new Callable<T>() {

			public T call() throws Exception {
				IWriter writer = null;
				T result = null;
				try {
					writer = newIndexer(indexJob.getAnalyzer());
					writer.begin(DaemonIndexer.class.getCanonicalName());

					result = indexJob.handle(writer);

					writer.end();
					writer = null;
				} catch (Throwable ex) {
					indexJob.onException(ex);
				} finally {
					silentEnd(writer);
					completeJob(indexJob);
				}

				return result;
			}

		};
		
		threadJobMap.put(indexJob.getId(), Thread.currentThread().getId()) ;
		return eservice.submit(call);
	}

	private void silentEnd(IWriter writer) {
		if (writer == null)
			return;
		try {
			writer.end();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void waitForFlushed() {
		Long threadId = Thread.currentThread().getId() ;
		while (threadJobMap.containsValue(threadId)) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	synchronized <T> void completeJob(JobEntry<T> job) {
		threadJobMap.remove(job.getId()) ;
		notifyAll();
	}

	LimitedChannel<JobEntry> getChannel() {
		return channel;
	}

}

class CacheWriter implements IWriter {

	private static int BufferUnitCount = 3000; // expect 15M

	private Central dest;
	private Analyzer analyzer ;

	private Central cacheCentral;
	private List<Query> deleteQuery ;
	private boolean commited ;
	private IWriter cacheWriter ;
	
	private int count = 0 ;

	CacheWriter(Central dest, Analyzer analyzer) throws LockObtainFailedException, IOException {
		this.dest = dest;
		this.analyzer = analyzer ;
	}

	static CacheWriter create(Central target, Analyzer analyzer) throws LockObtainFailedException, IOException {
		return new CacheWriter(target, analyzer);
	}

	public void begin(String owner) throws IOException {
		this.cacheCentral = Central.createOrGet(new RAMDirectory()) ;
		this.cacheWriter = cacheCentral.newIndexer(analyzer) ;
		this.deleteQuery = ListUtil.newList();
		cacheWriter.begin(owner) ;
		this.commited = false ;
	}

	public void close() throws IOException {
		end() ;
	}

	public void commit() throws IOException {
		cacheWriter.commit() ;
		cacheWriter.end() ;
		
		final Directory srcDir = cacheCentral.getDir() ;
		
		dest.newDaemonHander().addIndexJob(new JobEntry<Boolean>() {
			public Analyzer getAnalyzer() {
				return analyzer;
			}
			public Boolean handle(IWriter writer) throws IOException {
				for (Query query : deleteQuery) {
					writer.deleteQuery(query) ;
				}
				writer.appendFrom(srcDir) ;
				cacheCentral.destroySelf() ;
				return true;
			}

			public void onException(Throwable ex) {
				ex.printStackTrace() ;
			}
		}) ;
		
		this.commited = true ;
	}

	private void waitForCommit(){
		dest.newDaemonHander().waitForFlushed() ;
	}
	
	public void end() throws IOException {
		if (! commited) commit() ;
		waitForCommit() ;
	}

	public Action deleteAll() throws IOException {
		return deleteQuery(new MatchAllDocsQuery());
	}

	public Action deleteDocument(MyDocument doc) throws IOException {
		return deleteQuery(new TermQuery(new Term(IKeywordField.ISKey, doc.getISKey())));
	}

	public Action deleteQuery(Query query) throws IOException {
		deleteQuery.add(query);
		return Action.Delete;
	}
	
	public Action appendFrom(Directory srcDir) throws IOException {
		return dest.newIndexer(analyzer).appendFrom(srcDir) ;
	}		

	public Action deleteTerm(Term term) throws IOException {
		return deleteQuery(new TermQuery(term));
	}

	public Action insertDocument(MyDocument doc) throws IOException {
		if (++count % BufferUnitCount == 0){
			end() ;
			begin("..ing") ;
		}
		
		
		return cacheWriter.insertDocument(doc);
	}

	public boolean isLocked() throws IOException {
		return cacheWriter.isLocked();
	}

	public Map<String, HashBean> loadHashMap() throws IOException {
		return cacheWriter.loadHashMap();
	}

	public Action updateDocument(MyDocument doc) throws IOException {
		deleteQuery.add(new TermQuery(new Term(IKeywordField.ISKey, doc.getISKey()))) ;
		insertDocument(doc) ;
		return Action.Update;
	}
	
	public void optimize() throws IOException {
	}

	public void rollback() throws IOException {
		cacheWriter.rollback() ;
		cacheCentral.destroySelf() ;
	}


}


class DaemonWriter extends AbstractIWriter {

	private Central central;
	private final Analyzer analyzer;
	private DaemonIndexer dindexer;

	private Future lastFutures = null ;

	private DaemonWriter(Central central, Analyzer analyzer) {
		this.central = central;
		this.analyzer = analyzer;
		this.dindexer = central.newDaemonHander();
	}

	public static DaemonWriter create(Central c, Analyzer analyzer) {
		return new DaemonWriter(c, analyzer);
	}

	public void begin(String owner) throws LockObtainFailedException {
		// no action
	}

	public void close() throws IOException {
		
	}

	public void rollback() throws IOException {
//		for (Future future : futures) {
//			future.cancel(true) ;
//		}
//		futures.clear() ;
	}

	@Override
	protected void myWriteDocument(final MyDocument doc) throws IOException {
		Future<Action> future = dindexer.addIndexJob(new JobEntry<Action>() {
			public Analyzer getAnalyzer() {
				return analyzer;
			}

			public Action handle(IWriter writer) throws IOException {
				return writer.insertDocument(doc);
			}

			public void onException(Throwable ex) {
				ex.printStackTrace();
			}
		});
		lastFutures = future;
	}

	public Action updateDocument(final MyDocument doc) throws IOException {
		Future<Action> future = dindexer.addIndexJob(new JobEntry<Action>() {
			public Analyzer getAnalyzer() {
				return analyzer;
			}

			public Action handle(IWriter writer) throws IOException {
				return writer.updateDocument(doc);
			}

			public void onException(Throwable ex) {
				ex.printStackTrace();
			}
		});
		lastFutures = future;
		return Action.Update;
	}

	public Action deleteQuery(final Query query) throws IOException {
		Future<Action> future = dindexer.addIndexJob(new JobEntry<Action>() {
			public Analyzer getAnalyzer() {
				return analyzer;
			}

			public Action handle(IWriter writer) throws IOException {
				return writer.deleteQuery(query);
			}

			public void onException(Throwable ex) {
				ex.printStackTrace();
			}
		});
		lastFutures = future;
		return Action.Delete;
	}
	
	public Action appendFrom(final Directory srcDir) throws IOException {

		Future<Action> future = dindexer.addIndexJob(new JobEntry<Action>() {
			public Analyzer getAnalyzer() {
				return analyzer;
			}

			public Action handle(IWriter writer) throws IOException {
				return writer.appendFrom(srcDir);
			}

			public void onException(Throwable ex) {
				ex.printStackTrace();
			}
		});
		lastFutures = future;
		return Action.Insert ;
	}
	
	

	public void commit() throws IOException {
		try {
			if (lastFutures != null) lastFutures.get();
		} catch (ExecutionException ex) {
			throw new IOException(ex.getMessage());
		} catch (InterruptedException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	public void end() throws IOException {
		commit();
	}

	public Action deleteAll() throws IOException {
		return deleteQuery(new MatchAllDocsQuery());
	}

	public Action deleteDocument(MyDocument doc) throws IOException {
		return deleteTerm(new Term(ISKey, doc.getIdValue()));
	}

	public Action deleteTerm(Term term) throws IOException {
		return deleteQuery(new TermQuery(term));
	}

	public boolean isLocked() throws IOException {
		return false;
	}

	public Map<String, HashBean> loadHashMap() throws IOException {
		Map<String, HashBean> map = new HashMap<String, HashBean>();

		IndexReader reader = central.getIndexReader();

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

	public void optimize() throws IOException {

	}

}