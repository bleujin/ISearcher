package net.ion.nsearcher.index;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.config.IndexConfig;
import net.ion.nsearcher.exception.IndexException;
import net.ion.nsearcher.search.SingleSearcher;

import org.apache.lucene.analysis.Analyzer;

public class Indexer {

	private Central central;
	private IndexConfig iconfig;
	private SingleSearcher searcher ;
	
	private IndexExceptionHandler<Void> ehandler = IndexExceptionHandler.DEFAULT ;
	
	private Indexer(CentralConfig config, IndexConfig iconfig, Central central, SingleSearcher searcher) {
		this.central = central;
		this.iconfig = iconfig ;
		this.searcher = searcher ;
	}

	public static Indexer create(CentralConfig config, IndexConfig iconfig, Central central, SingleSearcher searcher) {
		return new Indexer(config, iconfig, central, searcher);
	}

	public <T> T index(IndexJob<T> indexJob) {
		return index(central.indexConfig().indexAnalyzer(), indexJob) ;
	}
	
	public <T> T index(Analyzer analyzer, IndexJob<T> indexJob) {
		return index(analyzer, indexJob, new IndexExceptionHandler<T>() {
			public T onException(Throwable ex) {
				ehandler.onException(ex) ;
				return null;
			}
		}) ;
	}

	public <T> T index(String name, Analyzer analyzer, IndexJob<T> indexJob) {
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
			return asyncIndex("emanon", analyzer, indexJob).get() ;
		} catch (InterruptedException e) {
			return handler.onException(e) ;
		} catch (ExecutionException e) {
			return handler.onException(e) ;
		}
	}

	
	public Indexer onExceptionHander(IndexExceptionHandler<Void> ehandler){
		this.ehandler = ehandler ;
		return this ;
	}

	public <T> Future<T> asyncIndex(IndexJob<T> indexJob) {
		return asyncIndex(central.indexConfig().indexAnalyzer(), indexJob) ;
	}

	public <T> Future<T> asyncIndex(final Analyzer analyzer, IndexJob<T> indexJob) {
		return asyncIndex("emanon", analyzer, indexJob) ;
	}


	public <T> Future<T> asyncIndex(final String name, final Analyzer analyzer, final IndexJob<T> indexJob) {
		return asyncIndex(name, analyzer, indexJob, ehandler) ;
	}
	
	public <T> Future<T> asyncIndex(final String name, final Analyzer analyzer, final IndexJob<T> indexJob, final IndexExceptionHandler handler) {
		return iconfig.indexExecutor().submit(new Callable<T>(){
			public T call() throws Exception {
				IndexSession session = null ;
				try {
					session = IndexSession.create(searcher, analyzer);
					session.begin(name) ;
					T result = indexJob.handle(session);
					
					session.commit() ;
					return result;
				} catch(Throwable ex) {
					if (session != null) session.rollback();
					handler.onException(ex) ;
//					return null ;
					throw new IndexException(ex.getMessage(), ex) ;
				} finally {
					session.end() ;
				}
			}
		}) ;
	}

	public void close() {
		iconfig.indexExecutor().shutdown() ;
	}

	
}
