package net.ion.nsearcher.config;

import java.io.Closeable;
import java.io.IOException;

import net.ion.framework.util.IOUtil;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.SingleSearcher;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;

public class Central implements Closeable{

	private Directory dir ;
	private final SingleSearcher singleSearcher ;
	private final Indexer indexer ;

	private final IndexConfig iconfig;
	private final SearchConfig sconfig;
	
	private Central(CentralConfig config, Directory dir) throws IOException{
		this.iconfig = config.indexConfigBuilder().buildSelf(config) ;
		this.sconfig = config.searchConfigBuilder().buildSelf(config) ;
		this.dir = dir ;
		this.singleSearcher = SingleSearcher.create(sconfig, this) ;
		this.indexer = Indexer.create(config, iconfig, this, singleSearcher) ;
	}

	static Central create(CentralConfig config) throws CorruptIndexException, IOException {
		Directory dir = config.innerBuildDir() ;
		return new Central(config, dir);
	}

	public Searcher newSearcher() throws IOException {
		return new Searcher(singleSearcher, sconfig); 
	}
	
	public Indexer newIndexer() {
		return indexer  ;
	}

	public void close() throws IOException {
		indexer.close() ;
		singleSearcher.close();
		dir.close() ;
	}

	public void destroySelf() {
		IOUtil.closeQuietly(indexer);
		IOUtil.close(singleSearcher);
		IOUtil.closeQuietly(dir) ;
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
	
	



	
	
}
