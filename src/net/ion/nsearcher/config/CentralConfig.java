package net.ion.nsearcher.config;

import java.io.IOException;

import net.ion.nsearcher.common.SearchConstant;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.util.Version;

public abstract class CentralConfig {

	private IndexConfigBuilder iconfigBuilder ;
	private SearchConfigBuilder sconfigBuilder ;
	
	private LockFactory lockFactory ;
	private Version version = SearchConstant.LuceneVersion ;
	
	public abstract Directory buildDir() throws IOException ;

	public CentralConfig(){
		this.iconfigBuilder = new IndexConfigBuilder(this) ;
		this.sconfigBuilder = new SearchConfigBuilder(this) ;
	}
	
	public static CentralLocalConfig newLocalFile() {
		return new CentralLocalConfig();
	}

	public static CentralConfig newRam() {
		return new CentralRamConfig();
	}
	
	public CentralConfig lockFactory(LockFactory lockFactory) {
		this.lockFactory = lockFactory ;
		return this;
	}
	
	public CentralConfig version(Version version) {
		this.version = version ;
		return this;
	}
	
	public Version version(){
		return version ;
	}

	public final Directory innerBuildDir() throws IOException{
		Directory dir = buildDir() ;
		String[] files = dir.listAll();
		if (files == null || files.length == 0) {
			IndexWriterConfig wconfig = new IndexWriterConfig(SearchConstant.LuceneVersion, new StandardAnalyzer(SearchConstant.LuceneVersion));
			IndexWriter iwriter = new IndexWriter(dir, wconfig);
			iwriter.close() ;
		}

		if (lockFactory != null) dir.setLockFactory(lockFactory) ;
		
		return dir ;
	}
	
	public Central build() throws CorruptIndexException, IOException {
		return Central.create(this);
	}

	public IndexConfigBuilder indexConfigBuilder(){
		return iconfigBuilder ;
	}
	
	public SearchConfigBuilder searchConfigBuilder(){
		return sconfigBuilder ;
	}

	
}
