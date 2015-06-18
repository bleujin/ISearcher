package net.ion.bleujin.lucene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.ion.framework.util.FileUtil;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import junit.framework.TestCase;

public class TestIndexMerge extends TestCase{
	
	public void testEnha() throws Exception {
		
		FileUtil.deleteDirectory(new File("./resource/enha"));
		Central central = CentralConfig.newLocalFile().dirFile("./resource/enha").build() ;
		
		Indexer indexer = central.newIndexer() ;
		final int maxcount = 200000 ;
		final AtomicInteger count = new AtomicInteger() ;
		
		indexer.index(new IndexJob<Void>() {

			@Override
			public Void handle(final IndexSession isession) throws Exception {
				Files.walkFileTree(Paths.get(new File("C:/crawl/enha/wiki").toURI()), new SimpleFileVisitor<Path>() {
					private long start = System.currentTimeMillis();

					public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
						File file = path.toFile();
						try {
							if (file.isDirectory())
								return FileVisitResult.CONTINUE;
							int icount = count.incrementAndGet();
							
							if (icount >= maxcount) return FileVisitResult.TERMINATE ;
							
							if ((icount % 300) == 0) {
								System.out.println(count.get() + " committed. elapsed time for unit : " + (System.currentTimeMillis() - start));
								this.start = System.currentTimeMillis();
								isession.continueUnit();
							}

							String content = IOUtil.toStringWithClose(new FileInputStream(file), "UTF-8");
							String wpath = makePathString(path) ;
							isession.newDocument(wpath).text("content", content).update() ;

							return FileVisitResult.CONTINUE;
						} catch (Throwable e) {
							System.err.println(file);
							throw new IOException(e);
						}
					}
				});
				return null;
			}
			public String makePathString(Path path) {
				Iterator<Path> iter = path.iterator() ;
				List<String> result = ListUtil.newList() ;
				while(iter.hasNext()){
					result.add(String.valueOf(iter.next()));
				}
				return "/" + StringUtil.join(result, "/") ;
			}
			
		}) ;
		
		
	}

}
