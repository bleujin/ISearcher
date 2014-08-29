package net.ion.nsearcher.rest.formater;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import net.ion.framework.db.DBController;
import net.ion.framework.db.Rows;
import net.ion.framework.rest.XMLHandler;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.util.MyWriter;

public class FormaterTest extends ISTestCase{

	
	public void testXMLFormater() throws Exception {
		TestRowsFormater xf = new TestRowsFormater(new WrapperXMLHander(new String[] { "pct_free", "pct_used" }, new String[] { "pct_free", "pct_used" }, "table_name")) ;
		DBController dc = createTestDBController() ;
		
		Rows rows = dc.createUserCommand("select * from user_tables").execQuery() ;
		
		StreamingOutput r =  xf.outputStreaming(rows) ;
		
		Debug.debug(r) ;
		
		dc.destroySelf() ;
	}
}

class TestRowsFormater implements IRowsFormater {
	private XMLHandler handler ;
	public TestRowsFormater(XMLHandler handler) {
		this.handler = handler ;
	}
	
	public StreamingOutput outputStreaming(final ResultSet rows) throws SQLException{
		
		return new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try {
					MyWriter writer = new MyWriter(output) ;
					writer.append(SearchXMLFormater.XML_HEADER) ;
					writer.write(handler.toXML(rows).toString()) ;
				} catch (SQLException e) {
					throw new IOException(e) ;
				}
			}
		};
	}
	
}

