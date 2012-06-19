package net.ion.isearcher.indexer.handler;

import java.io.File;
import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.events.CollectorEvent;
import net.ion.isearcher.events.FileEvent;

public class FileDocumentHandler implements DocumentHandler {

	public FileDocumentHandler() {
	}


	public MyDocument[] makeDocument(CollectorEvent _event) throws IOException {
		if (! (_event instanceof FileEvent)) return new MyDocument[0] ;

		FileEvent event = (FileEvent)_event ;

		File file = event.getFile();
		MyDocument doc = MyDocument.newDocument(event, file.getName());

		MyField name = MyField.text("name", file.getName());
		name.setBoost(HEAD_BOOST) ;
		doc.add(name);
		// doc.add(new Field("content", IOUtils.toString(new FileInputStream(file)), Store.YES, Index.ANALYZED));
		doc.add(MyField.text("size", file.length() + ""));
		doc.add(MyField.text("path", file.getAbsolutePath()));

		return new MyDocument[]{doc};
	}
}
