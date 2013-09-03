package net.ion.nsearcher.index.handler;

import java.io.File;
import java.io.IOException;

import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.event.CollectorEvent;
import net.ion.nsearcher.index.event.FileEvent;

public class FileDocumentHandler implements DocumentHandler {

	public FileDocumentHandler() {
	}


	public WriteDocument[] makeDocument(IndexSession isession, CollectorEvent _event) throws IOException {
		if (! (_event instanceof FileEvent)) return new WriteDocument[0] ;

		FileEvent event = (FileEvent)_event ;

		File file = event.getFile();
		WriteDocument doc = isession.newDocument(String.valueOf(event.getEventId())).event(event).name(file.getName());

		MyField name = MyField.text("name", file.getName());
		doc.add(name);
		// doc.add(new Field("content", IOUtils.toString(new FileInputStream(file)), Store.YES, Index.ANALYZED));
		doc.add(MyField.number("size", file.length()));
		doc.add(MyField.text("path", file.getAbsolutePath()));

		return new WriteDocument[]{doc};
	}
}
