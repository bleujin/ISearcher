package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.WithinThreadExecutor;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.config.IndexConfig;
import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.index.IndexJobs;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.util.Version;

public class TestMultiSearchReload extends TestCase{

	public void testTermFilter() throws Exception {
		Central c1 = CentralConfig.newRam().build() ;
		Central c2 = CentralConfig.newRam().build() ;

		Version mversion = SearchConstant.LuceneVersion;
		StandardAnalyzer dftAnal = new StandardAnalyzer(mversion);
		SearchConfig nconfig = SearchConfig.create(new WithinThreadExecutor(), mversion, dftAnal, SearchConstant.ISALL_FIELD);
		IndexConfig iconfig = IndexConfig.create( mversion, new WithinThreadExecutor(), dftAnal, new IndexWriterConfig(mversion, dftAnal), FieldIndexingStrategy.DEFAULT);
		Searcher msearcher = CompositeSearcher.create(nconfig, iconfig, ListUtil.toList(c1, c2));
		
		Searcher c1searcher = c1.newSearcher() ;
		
		c1.newIndexer().index(IndexJobs.create("bleujin", 2)) ;
		c2.newIndexer().index(IndexJobs.create("hero", 2)) ;

		msearcher.search("").debugPrint();
		
		assertEquals(2, c1searcher.search("").size()) ;
		
	}
}
