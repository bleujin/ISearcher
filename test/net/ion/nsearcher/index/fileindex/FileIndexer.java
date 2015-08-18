package net.ion.nsearcher.index.fileindex;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import rcc.h2tlib.parser.H2TParser;
import rcc.h2tlib.parser.HWPMeta;
import rcc.h2tlib.parser.HWPVER;

public class FileIndexer {

	private Central central;
	private ExecutorService es;
	private List<File> targets;

	public FileIndexer(Central central, ExecutorService es, List<File> targets) {
		this.central = central;
		this.es = es;
		this.targets = targets;
	}

	public <T> Future<List<T>> index(final FileIndexHandler<T> fileIndexHandler) {
		return index(central.indexConfig().indexAnalyzer(), fileIndexHandler);
	}

	public <T> Future<List<T>> index(final Analyzer analyzer, final FileIndexHandler<T> fileIndexHandler) {
		return es.submit(new Callable<List<T>>() {
			@Override
			public List<T> call() throws Exception {
				Indexer indexer = central.newIndexer();
				return indexer.index(analyzer, new IndexJob<List<T>>() {
					@Override
					public List<T> handle(IndexSession isession) throws Exception {

						AutoDetectTika extractor = new AutoDetectTika();
						List result = ListUtil.newList();
						for (File file : targets) {
							result.add(extractor.handle(file, isession, fileIndexHandler));
						}

						return result;
					}
				});
			}
		});
	}

}

class AutoDetectTika {

	private Parser tikaParser = new AutoDetectParser();
	private H2TParser hwpParser = new H2TParser();


	public <T> T handle(File file, IndexSession isession, FileIndexHandler<T> findexHandler) {

		try {
			if ("hwp".equals(FilenameUtils.getExtension(file.getName()))) {

				for (int version : new Integer[] { HWPVER.HML2, HWPVER.HWP3, HWPVER.HWP5 }) {
					boolean able = hwpParser.IsHanFile(file.getAbsolutePath(), version);
					if (able) {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						OutputStream output = new BufferedOutputStream(out);
						HWPMeta hwpmeta = new HWPMeta();
						InputStream input = new BufferedInputStream(new FileInputStream(file));
						try {
							boolean parsed = hwpParser.GetText(input, hwpmeta, output, version);

							Map<String, String> meta = MapUtil.<String> chainKeyMap().put("title", hwpmeta.getTitle()).put("subject", hwpmeta.getSubject()).put("createtime", hwpmeta.getCreatetime()).put("keyword", hwpmeta.getKeyword()).put("comment", hwpmeta.getComment())
									.put("version", "" + hwpmeta.getVer()).toMap();

							output.flush();
							IOUtil.closeQuietly(output);
							IOUtil.closeQuietly(input);

							StringBuffer content = new StringBuffer(new String(out.toByteArray(), Charset.forName("UTF-8")));
							return findexHandler.onSuccess(isession, FileEntry.create(file, content, meta));

						} finally {
							IOUtil.closeQuietly(output);
							IOUtil.closeQuietly(input);
						}
					}
				}
				return findexHandler.onFail(isession, file, new IllegalArgumentException("not supported hwp file"));
			} else {

				StringWriter writer = new StringWriter();
				ContentHandler handler = new BodyContentHandler(writer);

				InputStream input = null;
				try {
					Metadata metadata = new Metadata();
					input = new BufferedInputStream(new FileInputStream(file));
					tikaParser.parse(input, handler, metadata, new ParseContext());

					Map<String, String> meta = MapUtil.newMap();
					for (String name : metadata.names()) {
						meta.put(name, metadata.get(name));
					}

					return findexHandler.onSuccess(isession, FileEntry.create(file, writer.getBuffer(), meta));
				} finally {
					IOUtil.closeQuietly(input);
				}
			}

		} catch (IOException ex) {
			return findexHandler.onFail(isession, file, ex);
		} catch (SAXException ex) {
			return findexHandler.onFail(isession, file, ex);
		} catch (TikaException ex) {
			return findexHandler.onFail(isession, file, ex);
		}

	}
}
