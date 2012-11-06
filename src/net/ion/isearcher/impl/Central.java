package net.ion.isearcher.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.ion.framework.logging.LogBroker;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.indexer.write.Mutex;
import net.ion.isearcher.util.CloseUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
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
		ISearcher searcher = new ISearcher(this);
		return searcher;
	}


	public IReader newReader() throws CorruptIndexException, IOException {
		return new IReader(this);
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

	public abstract Filter getFilter(Filter filter);

	public abstract Filter getKeyFilter(Filter find);

	protected abstract boolean existFilter(Filter filter);

	public abstract void copyFrom(Analyzer analyzer, Directory... srcDirs) throws IOException;

}
