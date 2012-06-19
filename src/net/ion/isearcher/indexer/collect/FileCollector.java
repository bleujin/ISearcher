package net.ion.isearcher.indexer.collect;

import java.io.File;
import java.io.IOException;

import net.ion.isearcher.events.FileEvent;
import net.ion.isearcher.exception.ShutDownException;
import net.ion.isearcher.indexer.handler.DocumentHandler;
import net.ion.isearcher.indexer.handler.FileDocumentHandler;

public class FileCollector extends AbstractCollector {

	private File file;
	private boolean includeSub;
	private String eventSeed;
	private DocumentHandler handler;

	public FileCollector(File file, boolean includeSub) {
		this(DEFAULT_NAME + "/" + file.getName(), file, includeSub);
	}

	public FileCollector(String name, File file, boolean includeSub) {
		super(name);
		this.file = file;
		this.includeSub = includeSub;
		this.eventSeed = file.getName();
		this.handler = new FileDocumentHandler() ;
	}

	public void collect() {
		try {
			fireStart();
			lookupFile(file, includeSub);
		} catch (ShutDownException ignore) {
			// ignore.printStackTrace() ;
			System.out.println(ignore.getMessage()) ;
		} catch (IOException ignore) {
			ignore.printStackTrace();
		} finally {
			fireEnd();
		}
	}

	// private Document makeDocument(String path) throws IOException {
	// File file = new File(path);
	// Document doc = new Document();
	// doc.add(new Field("name", file.getName(), Store.YES, Index.ANALYZED));
	// //doc.add(new Field("content", IOUtils.toString(new FileInputStream(file)), Store.YES, Index.ANALYZED));
	// doc.add(new Field("size", file.length() + "", Store.YES, Index.ANALYZED));
	// doc.add(new Field("path", file.getAbsolutePath(), Store.YES, Index.NO));
	//
	// return doc;
	// }

	void lookupFile(File file, boolean includeSub) throws IOException {

		if (isShutDownState()) {
			throw ShutDownException.throwIt(this.getClass());
		}

		if (file.isDirectory() && includeSub) {

			File[] files = file.listFiles();
			if (files == null)
				return;
			for (File f : files) {
				lookupFile(f, includeSub);
			}
		}
		if (file.isFile()) fireCollectEvent(new FileEvent(this, file));
	}

	public DocumentHandler getDocumentHandler() {
		return handler;
	}

	public void setDocumentHandler(DocumentHandler handler){
		this.handler = handler ;
	}
}
