package net.ion.nsearcher.index;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.exception.IndexException;
import net.ion.nsearcher.search.SingleSearcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;

public class Indexer {

	private final CentralConfig config ;
	private Directory dir;
	private SingleSearcher searcher ;
	
	private ExecutorService singleExecutor = Executors.newSingleThreadExecutor() ;
	private IndexExceptionHandler<Void> ehandler = IndexExceptionHandler.DEFAULT ;
	
	private Indexer(CentralConfig config, Directory dir, SingleSearcher searcher) {
		this.config = config ;
		this.dir = dir;
		this.searcher = searcher ;
	}

	public static Indexer create(CentralConfig config, Directory dir, SingleSearcher searcher) {
		return new Indexer(config, dir, searcher);
	}

	public <T> T index(String name, Analyzer analyzer, IndexJob<T> indexJob) throws IndexException {
		try {
			return asyncIndex(name, analyzer, indexJob).get() ;
		} catch (InterruptedException e) {
			ehandler.onException(e) ;
		} catch (ExecutionException e) {
			ehandler.onException(e) ;
		}
		return null ;
	}

	
	public <T> T index(Analyzer analyzer, IndexJob<T> indexJob, IndexExceptionHandler<T> handler) {
		try {
			return asyncIndex("test", analyzer, indexJob).get() ;
		} catch (InterruptedException e) {
			return handler.onException(e) ;
		} catch (ExecutionException e) {
			return handler.onException(e) ;
		}
	}

	public <T> T index(Analyzer analyzer, IndexJob<T> indexJob) {
		return index(analyzer, indexJob, new IndexExceptionHandler<T>() {
			public T onException(Throwable ex) {
				ehandler.onException(ex) ;
				return null;
			}
		}) ;
	}
	
	public Indexer onExceptionHander(IndexExceptionHandler<Void> ehandler){
		this.ehandler = ehandler ;
		return this ;
	}
	
	
	public <T> Future<T> asyncIndex(final String name, final Analyzer analyzer, final IndexJob<T> indexJob) {
		return singleExecutor.submit(new Callable<T>(){
			public T call() throws Exception {
				IndexSession session = null ;
				try {
					
					session = IndexSession.create(config, dir, searcher, analyzer);
					session.begin(name) ;
					T result = indexJob.handle(session);
					
					session.end() ;
					return result;
				} catch(Throwable ex) {
					try {if (session != null) session.rollback();} catch(IOException ignore){ignore.printStackTrace();} ;
					ehandler.onException(ex) ;
//					return null ;
					throw new IndexException(ex.getMessage(), ex) ;
				} finally {
					if(session != null) session.release() ;
				}
			}
		}) ;
	}

	public void close() {
		singleExecutor.shutdown() ;
	}
	
}
