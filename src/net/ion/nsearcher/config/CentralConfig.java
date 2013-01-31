package net.ion.nsearcher.config;

import java.io.IOException;


import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;

public abstract class CentralConfig {

	private IndexWriterConfigBuilder wconfigBuilder ;
	
	
	public abstract Directory buildDir() throws IOException ;

	public CentralConfig(){
		this.wconfigBuilder = new IndexWriterConfigBuilder(this) ;
	}
	
	public static CentralLocalConfig newLocalFile() {
		return new CentralLocalConfig();
	}

	public static CentralConfig newRam() {
		return new CentralRamConfig();
	}

	public Central build() throws CorruptIndexException, IOException {
		return Central.create(this);
	}

	public IndexWriterConfigBuilder writerConfig(){
		return wconfigBuilder ;
	}
	
}
