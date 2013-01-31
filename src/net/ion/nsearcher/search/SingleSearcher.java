package net.ion.nsearcher.search;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.ion.framework.util.SetUtil;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.reader.InfoReader;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;

public class SingleSearcher implements Closeable{

	private IndexReader ireader;
	private IndexSearcher isearcher;
	private ExecutorService es = Executors.newSingleThreadExecutor() ;
	private InfoReader reader ;
	private CachedFilter filters = new CachedFilter() ;

	private final CentralConfig config ;
	private SingleSearcher(CentralConfig config, IndexReader ireader) {
		this.config = config ;
		this.ireader = ireader ;
		this.isearcher = new IndexSearcher(ireader) ;
		this.reader = InfoReader.create(this) ;
	}

	public static SingleSearcher create(CentralConfig config, Directory dir) throws IOException {
		return new SingleSearcher(config, IndexReader.open(dir));
	}

	public SearchResponse search(SearchRequest sreq, Filter filters) throws IOException {
		reloadReader() ;
		
		long startTime = System.currentTimeMillis() ;
		TopDocs docs = isearcher.search(sreq.query(), filters, sreq.limit(), sreq.sort());
		return SearchResponse.create(this, sreq, docs, startTime) ;
	}
	
	public <T> Future<T> submit(Callable<T> task){
		return es.submit(task) ;
	}
	
	private synchronized void reloadReader() throws IOException {
		IndexReader newReader = IndexReader.openIfChanged(ireader);
		if (newReader != null){
			// TODO : after closer
			
			ireader.close() ;
			filters.clear() ;
			this.ireader = newReader ;
			this.isearcher = new IndexSearcher(this.ireader) ;
		}
	}
	
	public MyDocument doc(int docId) throws IOException{
		return MyDocument.loadDocument(ireader.document(docId));
	}

	public InfoReader reader() {
		return reader;
	}

	public IndexReader indexReader() throws IOException {
		reloadReader() ;
		return isearcher.getIndexReader();
	}
	
	public void close() throws IOException {
		ireader.close() ;
		isearcher.close() ;
	}

	public CachedFilter cachedFilter(){
		return filters ;
	}	


	
}
