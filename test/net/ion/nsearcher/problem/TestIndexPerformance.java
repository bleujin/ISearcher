package net.ion.nsearcher.problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.lucene.analysis.debug.standard.DStandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;

import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.IndexField;
import net.ion.nsearcher.common.ManualIndexingStrategy;
import net.ion.nsearcher.common.AbDocument;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.common.FieldIndexingStrategy.FieldType;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;
import net.ion.radon.impl.util.CsvReader;
import junit.framework.TestCase;

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
//
//			@Override
//			public IndexField manual(String name, String value, Store store, Index index) {
//				return new IndexField(FieldType.Text, name, value, store, index);
//			}
//			@Override
//			public IndexField unknown(String name, Object obj) {
//				return new IndexField(FieldType.Text, name, ObjectUtil.toString(obj), Store.YES, Index.ANALYZED);
//			}
//			@Override
//			public IndexField unknown(String name, String value) {
//				return new IndexField(FieldType.Text, name, value, Store.YES, Index.ANALYZED);
//			}
//		}) ;
		
		Indexer indexer = central.newIndexer();

		indexer.index(new SampleWriteJob(100000)) ;
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
		int max = 100000 ;
		while(line != null && line.length > 0 && max-- > 0 ){
			Document doc = new Document() ; 
			for (int ii = 0, last = headers.length; ii < last ; ii++) {
				if (line.length > ii) doc.add(new IndexField(FieldType.Text, headers[ii], line[ii], Store.YES, Index.ANALYZED)) ;
			}
			
			iwriter.addDocument(doc) ;
//			iwriter.updateDocument(new Term(RandomUtil.nextRandomString(10), RandomUtil.nextRandomString(10)), doc) ;
			line = reader.readLine() ;
			if ((max % 5000) == 0) {
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
