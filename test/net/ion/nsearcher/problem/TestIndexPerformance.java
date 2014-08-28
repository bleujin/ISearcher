package net.ion.nsearcher.problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;
import net.ion.radon.impl.util.CsvReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;

public class TestIndexPerformance extends TestCase {

	private Central central;
	private FSDirectory dir;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final File homeDir = new File("./resource/findex");
		FileUtil.deleteDirectory(homeDir) ;
		homeDir.mkdir() ;
//		this.dir = FSDirectory.open(homeDir);
		this.dir = MMapDirectory.open(homeDir) ;
		this.central = CentralConfig.oldFromDir(dir).build();
	}
	
	@Override
	protected void tearDown() throws Exception {
		central.destroySelf() ;
		super.tearDown();
	}
	
	// 107sec(defalut( / 61 sec fieldIndexStrategy override(
	public void testUseCentral() throws Exception {
		long start = System.currentTimeMillis() ;
//		central.indexConfig().fieldIndexingStrategy(new ManualIndexingStrategy(){
//			@Override
//			public void unknown(Document doc, MyField field, String name, Object obj) {
//				doc.add(new Field(name, obj.toString(), Store.YES, Index.ANALYZED)) ;
//			}
//
//			@Override
//			public void unknown(Document doc, MyField field, String name, String value) {
//				doc.add(new Field(name, value, Store.YES, Index.ANALYZED)) ;
//			}
//
//			@Override
//			public void manual(Document doc, String name, String value, Store store, Index index) {
//				doc.add(new Field(name, value, store, index)) ;
//			}
//		}) ;
		
		Indexer indexer = central.newIndexer();

		indexer.index(new SampleWriteJob(20000)) ;
		Debug.line(System.currentTimeMillis() - start) ;
	}
	
	//29 sec(keyword, analyzed) / 18sec(keyword, not analyzed) / 28sec (text, analyzed)
	public void testUseLucene() throws Exception {
		long start = System.currentTimeMillis() ;
		IndexWriterConfig wconfig = new IndexWriterConfig(SearchConstant.LuceneVersion, new MyKoreanAnalyzer(SearchConstant.LuceneVersion));
		IndexWriter iwriter = new IndexWriter(dir, wconfig);
		File file = new File("C:/temp/freebase-datadump-tsv/data/medicine/drug_label_section.tsv") ;
		
		CsvReader reader = new CsvReader(new BufferedReader(new FileReader(file)));
		reader.setFieldDelimiter('\t') ;
		String[] headers = reader.readLine();
		String[] line = reader.readLine() ;
		int max = 20000 ;
		while(line != null && line.length > 0 && max-- > 0 ){
			Document doc = new Document() ; 
			for (int ii = 0, last = headers.length; ii < last ; ii++) {
				if (line.length > ii) {
					doc.add(new Field(headers[ii], line[ii], Store.YES, Index.ANALYZED)) ;
				}
			}
			
			iwriter.addDocument(doc) ;
//			iwriter.updateDocument(new Term(RandomUtil.nextRandomString(10), RandomUtil.nextRandomString(10)), doc) ;
			line = reader.readLine() ;
			if ((max % 20000) == 0) {
				System.out.print('.') ;
				iwriter.commit() ;
			} 
		}
		iwriter.close() ;
		Debug.line(System.currentTimeMillis() - start) ;
	}
	
	
	public void testSeqBatch() throws Exception {
		
		
	}
	
}
