package net.ion.nsearcher.rest;

import java.io.File;
import java.io.IOException;

import net.ion.framework.util.PathMaker;
import net.ion.nsearcher.Searcher;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.radon.core.let.AbstractServerResource;

public class SearchResource extends AbstractServerResource {

	protected InfoReader getInfoReader() throws IOException {
		return getCentral().newReader();
	}

	protected Searcher getSearcher() throws IOException {
		return getCentral().newSearcher();
	}

	protected Indexer getIndexer() throws IOException {
		return getCentral().newIndexer() ;
	}

	protected Central getCentral() throws IOException {
		String basePath = getContext().getAttributeObject(ISearcherApplication.FileLocation, String.class);
		String dirNm = (String) (getRequest().getAttributes().get("dir"));
		String dirPath = PathMaker.getFilePath(basePath, dirNm);
		final File file = new File(dirPath);

		synchronized (this) {
			Central found = getContext().getAttributeObject(file.getCanonicalPath(), Central.class);
			if (found == null) {
				found = CentralConfig.newLocalFile().dirFile(file).build() ;
				getContext().putAttribute(file.getCanonicalPath(), found) ;
			}
			
			return found ;
		}

	}
}
