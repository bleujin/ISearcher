package net.ion.nsearcher.index;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.reader.InfoReader.InfoHandler;
import junit.framework.TestCase;

public class TestReaderInfo extends TestCase {

	public void testIndexCommitUserData() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		central.newIndexer().index(IndexJobs.create("/hero", 2)) ;
		central.newIndexer().index(IndexJobs.create("/jin", 3)) ;
		central.newIndexer().index(IndexJobs.create("/bleu", 2)) ;
		
		central.newReader().info(new InfoHandler<Void>() {
			@Override
			public Void view(IndexReader ireader, DirectoryReader dreader) throws IOException {
				List<IndexCommit> cms = DirectoryReader.listCommits(dreader.directory()) ;
				
				for(IndexCommit ic : cms){
					Debug.line(ic.getUserData(), ic.getSegmentsFileName(), dreader.getIndexCommit().getSegmentsFileName()) ;
				}
				return null;
			}
		}) ;
	}
}
