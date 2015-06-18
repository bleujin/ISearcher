package net.ion.nsearcher.problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.radon.util.csv.CsvReader;

public class SampleWriteJob implements IndexJob<Void> {

	private int max = 0 ;
	
	public SampleWriteJob(int max){
		this.max = max ;
	}
	
	public Void handle(IndexSession isession) throws Exception {
		isession.setIgnoreBody(true) ;
		File file = new File("C:/temp/freebase-datadump-tsv/data/medicine/drug_label_section.tsv") ;
		
		CsvReader reader = new CsvReader(new BufferedReader(new FileReader(file)));
		reader.setFieldDelimiter('\t') ;
		String[] headers = reader.readLine();
		String[] line = reader.readLine() ;
		
		while(line != null && line.length > 0 && max-- > 0 ){
//			if (headers.length != line.length ) continue ;
			
			WriteDocument doc = isession.newDocument("/" + max) ; 
			for (int ii = 0, last = headers.length; ii < last ; ii++) {
				if (line.length > ii) doc.unknown(headers[ii], line[ii]) ;
			}
			isession.insertDocument(doc) ;
			
//			Document doc = new Document() ; 
//			for (int ii = 0, last = headers.length; ii < last ; ii++) {
//				if (line.length > ii) doc.add(new IndexField(FieldType.Text, headers[ii], line[ii], Store.YES, Index.ANALYZED)) ;
//			}
//			isession.testDocument(doc) ;
			
			line = reader.readLine() ;
			if (max != 0 && (max % 10000) == 0) {
				System.out.print('.') ;
				isession.continueUnit() ;
			} 
		}
		reader.close() ;
		Debug.line("endJob") ;
		return null;
	}
}