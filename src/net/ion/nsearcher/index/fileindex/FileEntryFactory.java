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
import java.util.Map;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.vfs.VFile;

import org.apache.commons.io.FilenameUtils;
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

public class FileEntryFactory {

	private Parser tikaParser = new AutoDetectParser();
	private H2TParser hwpParser = new H2TParser();

	public static FileEntryFactory create() {
		return new FileEntryFactory();
	}

	public FileEntry makeEntry(File file, FailIndexFileHandler<FileEntry> failHandler) {
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
							return FileEntry.create(file, content, meta);

						} finally {
							IOUtil.closeQuietly(output);
							IOUtil.closeQuietly(input);
						}
					}
				}
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
					return FileEntry.create(file, writer.getBuffer(), meta);
				} finally {
					IOUtil.closeQuietly(input);
				}
			}
		} catch (IOException ex) {
			return failHandler.onFail(file, ex);
		} catch (SAXException ex) {
			return failHandler.onFail(file, ex);
		} catch (TikaException ex) {
			return failHandler.onFail(file, ex);
		} catch (Throwable ex){
			return failHandler.onFail(file, new IllegalArgumentException(ex.getMessage()));
		}
		return failHandler.onFail(file, new IllegalArgumentException("not supported file "));
	}
	
	public VFileEntry makeEntry(VFile vfile, FailIndexVFileHandler<VFileEntry> failHandler) {
		try {
			if ("hwp".equals(vfile.getName().getExtension())) {
				for (int version : new Integer[] { HWPVER.HML2, HWPVER.HWP3, HWPVER.HWP5 }) {
					boolean able = hwpParser.IsHanFile(vfile.getInputStream(), version);
					if (able) {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						OutputStream output = new BufferedOutputStream(out);
						HWPMeta hwpmeta = new HWPMeta();
						InputStream input = null ;
						try {
							input = new BufferedInputStream(vfile.getInputStream());
							boolean parsed = hwpParser.GetText(input, hwpmeta, output, version);

							Map<String, String> meta = MapUtil.<String> chainKeyMap().put("title", hwpmeta.getTitle()).put("subject", hwpmeta.getSubject()).put("createtime", hwpmeta.getCreatetime()).put("keyword", hwpmeta.getKeyword()).put("comment", hwpmeta.getComment())
									.put("version", "" + hwpmeta.getVer()).toMap();

							output.flush();
							IOUtil.closeQuietly(output);
							IOUtil.closeQuietly(input);

							StringBuffer content = new StringBuffer(new String(out.toByteArray(), Charset.forName("UTF-8")));
							return VFileEntry.create(vfile, content, meta);

						} finally {
							IOUtil.close(output, input);
						}
					}
				}
			} else {
				StringWriter writer = new StringWriter();
				ContentHandler handler = new BodyContentHandler(writer);

				InputStream input = null;
				try {
					Metadata metadata = new Metadata();
					input = new BufferedInputStream(vfile.getInputStream());
					tikaParser.parse(input, handler, metadata, new ParseContext());

					Map<String, String> meta = MapUtil.newMap();
					for (String name : metadata.names()) {
						meta.put(name, metadata.get(name));
					}
					return VFileEntry.create(vfile, writer.getBuffer(), meta);
				} finally {
					IOUtil.closeQuietly(input);
				}
			}
		} catch (IOException ex) {
			return failHandler.onFail(vfile, ex);
		} catch (SAXException ex) {
			return failHandler.onFail(vfile, ex);
		} catch (TikaException ex) {
			return failHandler.onFail(vfile, ex);
		} catch (Throwable ex){
			return failHandler.onFail(vfile, new IllegalArgumentException(ex.getMessage()));
		}
		return failHandler.onFail(vfile, new IllegalArgumentException("not supported file "));
	}
}
