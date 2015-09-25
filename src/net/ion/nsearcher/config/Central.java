package net.ion.nsearcher.config;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.nsearcher.search.CompositeSearcher;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.SearcherImpl;
import net.ion.nsearcher.search.SingleSearcher;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;

public class Central implements Closeable{

	private Directory dir ;
	private final SingleSearcher singleSearcher ;
	private final Indexer indexer ;

	private final IndexConfig iconfig;
	private final SearchConfig sconfig;
	private long startTime ;
	private ReadWriteLock rwlock = new ReentrantReadWriteLock() ;
	
	private Central(CentralConfig config, Directory dir) throws IOException{
		this.iconfig = config.indexConfigBuilder().buildSelf(config) ;
		this.sconfig = config.searchConfigBuilder().buildSelf(config) ;
		this.dir = dir ;
		this.singleSearcher = SingleSearcher.create(sconfig, this) ;
		this.indexer = Indexer.create(config, iconfig, this, singleSearcher) ;
		this.startTime = System.currentTimeMillis() ;
	}

	static Central create(CentralConfig config) throws CorruptIndexException, IOException {
		Directory dir = config.innerBuildDir() ;
		return new Central(config, dir);
	}

	public Searcher newSearcher() throws IOException {
		return new SearcherImpl(singleSearcher, sconfig, iconfig); 
	}
	
	public Searcher newSearcher(Central another, Central... other) throws IOException {
		
		List<Central> all = ListUtil.newList() ;
		all.add(this) ;
		all.add(another) ;
		all.addAll(ListUtil.toList(other)) ;
		
		SearchConfig nconfig = SearchConfig.create(this.searchConfig().searchExecutor(), SearchConstant.LuceneVersion, new StandardAnalyzer(SearchConstant.LuceneVersion), SearchConstant.ISALL_FIELD) ;
		return CompositeSearcher.create(another.sconfig, another.iconfig, all) ;
	}

	
	public Indexer newIndexer() {
		return indexer  ;
	}

	public void close() throws IOException {
		destroySelf(); 
	}

	public void destroySelf() {
		IOUtil.close(singleSearcher);
		IOUtil.closeQuietly(indexer);
		IOUtil.closeQuietly(dir) ;
	}

	public long startTime(){
		return startTime ;
	}
	
	public Directory dir(){
		return dir ;
	}
	
	public InfoReader newReader() {
		return singleSearcher.reader();
	}

	public IndexConfig indexConfig() {
		return iconfig;
	}
	
	public SearchConfig searchConfig() {
		return sconfig;
	}
	
	public Lock readLock(){
		
		return rwlock.readLock() ;
	}

	public Lock writeLock(){
		return rwlock.writeLock() ;
	}


}
