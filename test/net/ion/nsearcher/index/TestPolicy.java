package net.ion.nsearcher.index;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.exception.IndexException;
import net.ion.nsearcher.index.collect.FileCollector;
import net.ion.nsearcher.index.policy.DeleteTermInsertPolicy;
import net.ion.nsearcher.index.policy.IWritePolicy;
import net.ion.nsearcher.index.policy.MergePolicy;
import net.ion.nsearcher.index.policy.RecreatePolicy;
import net.ion.nsearcher.index.report.DefaultReporter;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

public class TestPolicy extends ISTestCase{
	
	public void testMergePolicy() throws Exception {
		executeTest(new MergePolicy());
	}
	
	public void testRecreatePolicy() throws Exception {
		executeTest(new RecreatePolicy());
	}

	public void testRecreateTermPolicy() throws Exception {
		executeTest(new DeleteTermInsertPolicy("name", "smith"));
	}
	
	
	private void executeTest(IWritePolicy policy) throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException, IndexException, ExecutionException {
		Central central = writeDocument() ;

		FileCollector col = new FileCollector(getTestDirFile(), true);
		Indexer writer = central.newIndexer();

		NonBlockingListener adapterListener = getNonBlockingListener(writer);
		adapterListener.getDefaultIndexer().setWritePolicy(policy) ;
		
		col.addListener(adapterListener) ;
		col.addListener(new DefaultReporter(false)) ;
		
		col.collect() ;
		adapterListener.waitForCompleted() ;
		central.close() ;
	}
}
