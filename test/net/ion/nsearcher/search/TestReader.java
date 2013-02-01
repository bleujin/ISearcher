package net.ion.nsearcher.search;

import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.nsearcher.reader.InfoReader.InfoHandler;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;

public class TestReader extends ISTestCase {

	public void testCommit() throws Exception {
		Central central = writeDocument();

		InfoReader reader = central.newReader();
		reader.info(new InfoHandler<Void>() {
			public Void view(IndexReader dreader) throws IOException {
				for (IndexCommit commit : dreader.listCommits(dreader.directory())) {
					Debug.debug(commit.getDirectory(), commit.getFileNames(), commit.getSegmentsFileName(), commit.getUserData());
					Debug.debug(commit.isDeleted(), commit.getGeneration());
				}
				return null;
			}
		});

		// central.testIndexer(getAnalyzer()).end() ;
	}
}
