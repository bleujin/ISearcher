package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.WithinThreadExecutor;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.index.IndexJobs;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class TestMultiSearchReload extends TestCase{

	public void testTermFilter() throws Exception {
		Central c1 = CentralConfig.newRam().build() ;
		Central c2 = CentralConfig.newRam().build() ;

		SearchConfig nconfig = SearchConfig.create(new WithinThreadExecutor(), SearchConstant.LuceneVersion, new StandardAnalyzer(SearchConstant.LuceneVersion), SearchConstant.ISALL_FIELD);
		Searcher msearcher = CompositeSearcher.create(nconfig, ListUtil.toList(c1, c2));
		
		Searcher c1searcher = c1.newSearcher() ;
		
		c1.newIndexer().index(IndexJobs.create("bleujin", 2)) ;
		c2.newIndexer().index(IndexJobs.create("hero", 2)) ;

		msearcher.search("").debugPrint();
		
		assertEquals(2, c1searcher.search("").size()) ;
		
	}
}
