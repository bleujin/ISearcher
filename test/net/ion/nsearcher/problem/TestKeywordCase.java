package net.ion.nsearcher.problem;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.MyField.MyFieldType;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;

public class TestKeywordCase extends TestCase{

	private Central cen;


	public void setUp() throws Exception {
		this.cen = CentralConfig.newRam().build() ;
	}

	
	public void testMarch() throws Exception {
		cen.indexConfig().fieldIndexingStrategy(new FieldIndexingStrategy() {
			@Override
			public void save(Document doc, MyField myField, Field ifield) {
				if (myField.myFieldtype() == MyFieldType.Keyword && (!IKeywordField.Field.reservedId(myField.name()))){
					ifield.setStringValue(ifield.stringValue().toLowerCase()) ;
					FieldIndexingStrategy.DEFAULT.save(doc, myField, ifield);
					return ;
				}
				FieldIndexingStrategy.DEFAULT.save(doc, myField, ifield);
			}
		}) ;
		
		cen.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				String[] names = new String[]{"March", "April", "January", "September", "October"} ;
				for (String name : names) {
					isession.newDocument(name).text("text", name).keyword("keyword", name).unknown("unknown", name).insert()  ;
				}
				return null;
			}
		}) ;
		
		
		assertEquals(1, cen.newSearcher().createRequest("Text:March").find().size()) ;
		Debug.line(cen.newSearcher().createRequest("Keyword:March").query()) ;
		assertEquals(1, cen.newSearcher().createRequest("Keyword:March").find().size()); 
	}
}
