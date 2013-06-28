package net.ion.nsearcher.config;

import java.io.IOException;

import org.apache.lucene.store.Directory;

public class OldConfigFromDir extends CentralConfig {

	
	private Directory dir;
	public OldConfigFromDir(Directory dir) {
		this.dir  = dir ;
	}

	@Override
	public Directory buildDir() throws IOException {
		return dir;
	}

}
