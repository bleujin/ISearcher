package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.TermQuery;

import junit.framework.TestCase;
import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

public class TestSearchResponse extends TestCase {

	private Central central;
	private Searcher searcher;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.central = CentralConfig.newRam().build() ;
		this.searcher = central.newSearcher() ;

		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 100 ; i++) {
					isession.newDocument("bleujin_" + i).keyword("name", "bleujin").number("int", i).unknown("age", 30).text("explain", "my name is bleujin").update() ;
				}
				return null;
			}
		}) ;
	}
	
	public void testPageList() throws Exception {
		final SearchResponse sres = central.newSearcher().createRequest("int:[10 TO 50]").sort("int desc").find();
		for(ReadDocument rdoc : sres.getDocument(Page.create(5, 2, 5))) {
			Debug.line(rdoc);
		}
		
		ReadDocument doc15 = sres.documentById("bleujin_23") ;
		
		Debug.line(sres.preDocBy(doc15), sres.nextDocBy(doc15)) ;
		
	}
	
	public void testInOpern() throws Exception {
		BooleanQuery bq = new BooleanQuery() ;
		for (int artid : new int[]{30, 40, 50}) {
			bq.add(new TermQuery(new Term("int", ""+artid)), Occur.SHOULD);
		}
		
		central.newSearcher().createRequest(bq).find().debugPrint();
	}
	
	
	

}
