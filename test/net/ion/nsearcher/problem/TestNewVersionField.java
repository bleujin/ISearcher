package net.ion.nsearcher.problem;

import junit.framework.TestCase;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.search.QueryUtil;

public class TestNewVersionField extends TestCase{

	public void testFieldName() throws Exception {
		Central c = CentralConfig.newRam().build();
		
		c.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().add(MyField.number("AGE", 20)).add(MyField.number("AGE", 25)).add(MyField.number("age", 30)).update(); ;
				return null;
			}
		}) ;

		assertEquals(1, c.newSearcher().createRequest("AGE:20").find().size());
		assertEquals(0, c.newSearcher().createRequest("age:20").find().size());
		assertEquals(1, c.newSearcher().createRequest("AGE:25").find().size());
		assertEquals(1, c.newSearcher().createRequest("age:30").find().size());
	}
	
	public void testInsensitive() throws Exception {
		Central c = CentralConfig.newRam().build();
		c.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.fieldIndexingStrategy(FieldIndexingStrategy.SENSITIVE_FIELDNAME) ;
				isession.newDocument().add(MyField.number("AGE", 20)).add(MyField.number("AGE", 25)).add(MyField.number("age", 30)).update(); ;
				return null;
			}
		}) ;
		
		assertEquals(1, c.newSearcher().search("20").size());
		assertEquals(1, c.newSearcher().search("25").size());
		assertEquals(1, c.newSearcher().search("30").size());

		
		assertEquals(1, c.newSearcher().search("age:20").size());
		assertEquals(1, c.newSearcher().search("age:25").size());
	}
	
	public void testIfOri() throws Exception {
		Central c = CentralConfig.newRam().build();
		c.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.fieldIndexingStrategy(FieldIndexingStrategy.SENSITIVE_FIELDNAME) ;
				isession.newDocument().add(MyField.number("AGE", 20)).add(MyField.number("AGE", 25)).add(MyField.number("age", 30)).add(MyField.keyword("age", "40")) .update(); ;
				return null;
			}
		}) ;

		assertEquals(1, c.newSearcher().createRequest(QueryUtil.createTerm("age", 20)).find().size()) ;
		assertEquals(1, c.newSearcher().createRequest(QueryUtil.createTerm("age", 25)).find().size()) ;
		assertEquals(1, c.newSearcher().createRequest(QueryUtil.createTerm("age", 30)).find().size()) ;
		assertEquals(0, c.newSearcher().createRequest(QueryUtil.createTerm("age", 40)).find().size()) ;
	}
	
	
}
