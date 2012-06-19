package net.ion.isearcher.indexer;

import java.io.IOException;

import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.indexer.collect.FileCollector;
import net.ion.isearcher.indexer.policy.DeleteTermInsertPolicy;
import net.ion.isearcher.indexer.policy.IWritePolicy;
import net.ion.isearcher.indexer.policy.MergePolicy;
import net.ion.isearcher.indexer.policy.RecreatePolicy;
import net.ion.isearcher.indexer.report.DefaultReporter;
import net.ion.isearcher.indexer.write.IWriter;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

public class PolicyTest extends ISTestCase{
	
	public void testMergePolicy() throws Exception {
		executeTest(new MergePolicy());
	}
	
	public void testRecreatePolicy() throws Exception {
		executeTest(new RecreatePolicy());
	}

	public void testRecreateTermPolicy() throws Exception {
		executeTest(new DeleteTermInsertPolicy("name", "smith"));
	}
	
	
	private void executeTest(IWritePolicy policy) throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException {
		Directory dir = writeDocument() ;
		Central central = Central.createOrGet(dir) ;

		FileCollector col = new FileCollector(getTestDirFile(), true);
		IWriter writer = central.testIndexer(getAnalyzer()) ;

		NonBlockingListener adapterListener = getAdapterListener(writer);
		adapterListener.getDefaultIndexer().setWritePolicy(policy) ;
		
		col.addListener(adapterListener) ;
		col.addListener(new DefaultReporter(false)) ;
		
		col.collect() ;
		adapterListener.joinIndexer() ;
		central.destroySelf() ;
	}
}
