package net.ion.isearcher.indexer.policy;

import java.io.IOException;
import java.util.Map;

import net.ion.isearcher.indexer.write.IWriter;

public class RealData implements Data {
	private final Map data;

	public RealData(IWriter writer) throws IOException {
		this.data = writer.loadHashMap();
	}

	public Map getHashData() {
		return this.data;
	}
}
