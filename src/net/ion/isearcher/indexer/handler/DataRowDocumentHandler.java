package net.ion.isearcher.indexer.handler;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.events.CollectorEvent;
import net.ion.isearcher.events.DataRowEvent;
import net.ion.isearcher.events.KeyValues;

public class DataRowDocumentHandler implements DocumentHandler {

	public DataRowDocumentHandler() {
	}

	public MyDocument[] makeDocument(CollectorEvent _event) throws IOException {
		if (! (_event instanceof DataRowEvent)) return new MyDocument[0] ;
		
		DataRowEvent event = (DataRowEvent)_event ;
		KeyValues keyValues = event.getKeyValues();
		String[] keyColumns = event.getKeyColumns() ;
		
		String docName = "" ;
		for (String key : keyColumns) {
			docName += keyValues.get(key) + "_" ;
		}
		
		MyDocument doc = MyDocument.newDocument(event, docName);
		for (String colName : keyValues.getKeySet()) {
			Object value = keyValues.get(colName);
			if (value != null) {
//				MyField myfield = MyField.text(colName, value.toString()) ;
//				if (ArrayUtils.contains(keyColumns, colName)) myfield.setBoost(HEAD_BOOST) ;
				
				doc.add(MyField.unknown(colName, value));
			}
		}
		return new MyDocument[]{doc};
	}


}
