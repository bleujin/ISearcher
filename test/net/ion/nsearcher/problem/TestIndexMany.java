package net.ion.nsearcher.problem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

public class TestIndexMany extends TestCase {

	
	public void testEnha() throws Exception {
		Central central = CentralConfig.newLocalFile().dirFile(new File("./resource/enha")).build() ;
		
		Indexer indexer = central.newIndexer() ;
		
		indexer.index(new IndexJob<Void>() {
			AtomicInteger count = new AtomicInteger(0) ;
			@Override
			public Void handle(IndexSession isession) throws Exception {
				File homeDir = new File("C:/crawl/enha/wiki");
				saveProperty(isession, homeDir, "");
				return null;
			}
			
			private void saveProperty(IndexSession isession, File file, String path) throws IOException{
				if (file.isDirectory()) {
					for (File sfile : file.listFiles()) {
						saveProperty(isession, sfile, path + "/" + sfile.getName());
					}
				} else {
					String content = IOUtil.toStringWithClose(new FileInputStream(file), "UTF-8");
					isession.newDocument(path).text("content", content).update();
					
					int icount = count.incrementAndGet();
					if ((icount % 500) == 0){
						System.out.println(count.get() + " committed");
						isession.continueUnit(); 
					}
				}
			}
		}) ;
		
		central.newSearcher().createRequest("").find().totalCount() ;
	}
	
	public void testEnhaFind() throws Exception {
		Central central = CentralConfig.newLocalFile().dirFile(new File("./resource/enha")).build() ;
		Debug.line(central.newSearcher().createRequest("").find().totalCount()) ;
	}

}
