package net.ion.nsearcher.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.search.EachDocHandler;
import net.ion.nsearcher.search.EachDocIterator;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.radon.util.csv.CsvReader;

import org.apache.lucene.analysis.ko.MyKoreanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.FieldInfo.DocValuesType;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class TestBoost extends TestCase{

	
	public void testInCentral() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		
		Indexer indexer = central.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 10; i++) {
					WriteDocument wdoc = isession.newDocument("idx" + i) ;
					wdoc.boost(1.0f * (i%3 +1)) ;
					MyField index = MyField.text("index", "3", Store.YES).boost(2.0f);
					wdoc.add(index) ;
					wdoc.update() ;
				}
				return null;
			}
		}) ;
		
		
		SearchRequest request = central.newSearcher().createRequest("index:3");
		request.find().debugPrint(); 
		
		request.find().eachDoc(new EachDocHandler<Void>() {
			@Override
			public Void handle(EachDocIterator iter) {
				while(iter.hasNext()){
					ReadDocument doc = iter.next() ;
					Debug.line(doc.getField("index").boost()) ;
				}
				return null;
			}
		}) ;
		
	}
	
	
	public void testInLucene() throws Exception {

		Directory directory = new RAMDirectory() ;
		
		IndexWriterConfig iwconfig = new IndexWriterConfig(Version.LUCENE_44, new StandardAnalyzer());
		IndexWriter iw = new IndexWriter(directory, iwconfig) ;
		
		File file = new File("C:/temp/freebase-datadump-tsv/data/medicine/drug_label_section.tsv") ;
		
		CsvReader reader = new CsvReader(new BufferedReader(new FileReader(file)));
		reader.setFieldDelimiter('\t') ;
		String[] headers = reader.readLine();
		String[] line = reader.readLine() ;
		int max = 3 ;
		
		while(line != null && line.length > 0 && max-- > 0 ){
			String path = "/bleujin/" + max;
			Document doc = createDoc(path, iw, headers, line) ;
//			iw.updateDocument(new Term("id", path), doc);
			iw.addDocument(doc);
			Debug.debug(line);
			line = reader.readLine() ;
		}
		iw.commit();
		iw.close(); 
		
		
		DirectoryReader dreader = DirectoryReader.open(directory);
		IndexSearcher isearcer = new IndexSearcher(dreader) ;
		ScoreDoc[] finds = isearcer.search(new MatchAllDocsQuery(), 10).scoreDocs;
		for (ScoreDoc sdoc : finds) {
			
			Document doc = isearcer.doc(sdoc.doc) ;
			for (IndexableField field : doc.getFields()){
				Terms terms = dreader.getTermVector(sdoc.doc, field.name()) ;
				if (terms != null){
					TermsEnum tenum = terms.iterator(null) ;
					while(tenum.next() != null){
						Debug.line(new String(tenum.term().bytes), field.name(), sdoc.doc);
					}
				}
			}
		}
		
		directory.close(); 
	}
	
	private Document createDoc(String id, IndexWriter iw, String[] headers, String[] values) {
		Document doc = new Document() ;
		doc.add(new StringField("id", id, Store.YES));

		FieldType mytype = new FieldType() ;
		mytype.setIndexed(true);
		mytype.setStored(true);
		mytype.setStoreTermVectors(true);
		mytype.setTokenized(true);

		for(int i = 0 ; i <headers.length ; i++){
			String name = headers[i] ;
			String value = (values.length > i) ? values[i] : "" ;
			if (value.contains(" ")) {
				Field field = new Field(name, value, mytype);
				field.setBoost(2.0f);
				doc.add(field);
			} else {
				StringField field = new StringField(name, value, Store.YES);
				doc.add(field) ;
			}
		}

		return doc;
	}
}
