package net.ion.nsearcher.config;

import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class CentralRamConfig extends CentralConfig {

	@Override
	public Directory buildDir() throws IOException {
		return new RAMDirectory();
	}
}
