package net.ion.nsearcher.config;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CentralLocalConfig extends CentralConfig{

	
	private File dirPath ;
	CentralLocalConfig() {
	}

	public CentralConfig dirFile(String path) {
		return dirFile(new File(path));
	}

	public CentralConfig dirFile(File path) {
		if (!path.exists()) {
			path.mkdirs();
		}
		this.dirPath = path ;
		return this;
	}
	

	@Override
	public Directory buildDir() throws IOException {
		if (dirPath == null || (!dirPath.exists())) throw new IllegalStateException("exception.isearcher.dir.not_defined_path") ;
		if (! dirPath.canWrite()) throw new IllegalStateException("exception.isearcher.dir.cannt_write") ;
		
		return FSDirectory.open(dirPath);
	}

	
}
