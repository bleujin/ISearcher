package net.ion.nsearcher.collector;

import java.io.File;

import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.HashFunction;
import net.ion.nsearcher.index.collect.FileCollector;

public class FileCollectorTest extends ISTestCase{

	public void testGetCollectorName() throws Exception {
		FileCollector col = new FileCollector("NAME", new File("resource/sample"), true) ;
		assertEquals("6688229", col.getCollectName()) ;
		assertEquals("6688229", String.valueOf(HashFunction.hashGeneral("NAME"))) ;
	}
}
