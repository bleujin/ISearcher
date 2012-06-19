package net.ion.isearcher.collector;

import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.crawler.util.HashFunction;
import net.ion.isearcher.indexer.collect.FileCollector;

public class FileCollectorTest extends ISTestCase{

	public void testGetCollectorName() throws Exception {
		FileCollector col = new FileCollector("NAME", getTestDirFile(), true) ;
		assertEquals("6688229", col.getCollectName()) ;
		assertEquals("6688229", String.valueOf(HashFunction.hashGeneral("NAME"))) ;
	}
}
