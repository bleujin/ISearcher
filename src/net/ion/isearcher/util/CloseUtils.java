package net.ion.isearcher.util;

import java.io.Closeable;
import java.io.IOException;

import net.ion.isearcher.indexer.write.IWriter;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.store.Directory;

public class CloseUtils {

	
	
	public static void silentClose(Closeable closeable ) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ignore) {
		}
	}
	
	public static void silentClose(Searchable searcher) {
		try {
			if (searcher != null) {
				searcher.close();
			}
		} catch (IOException ignore) {
		}
	}
	
	public static void silentClose(IndexWriter writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException ignore) {
		}
	}

	public static void silentClose(IndexReader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException ignore) {
		}
	}

	public static void silentClose(Directory dir) {
		try {
			if (dir != null) {
				dir.close();
			}
		} catch (IOException ignore) {
		}
	}

	public static void silentClose(IWriter iwrite) {
		try {
			if (iwrite != null) {
				iwrite.close();
			}
		} catch (IOException ignore) {
		}
	}

}
