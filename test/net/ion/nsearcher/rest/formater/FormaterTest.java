package net.ion.nsearcher.rest.formater;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.ion.framework.db.DBController;
import net.ion.framework.db.Rows;
import net.ion.framework.rest.XMLHandler;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class FormaterTest extends ISTestCase{

	
	public void testXMLFormater() throws Exception {
		TestRowsFormater xf = new TestRowsFormater(new WrapperXMLHander(new String[] { "pct_free", "pct_used" }, new String[] { "pct_free", "pct_used" }, "table_name")) ;
		DBController dc = createTestDBController() ;
		
		Rows rows = dc.createUserCommand("select * from user_tables").execQuery() ;
		
		Representation r =  xf.getRepresentation(rows) ;
		
		Debug.debug(r.getText()) ;
		
		dc.destroySelf() ;
	}
}

class TestRowsFormater implements IRowsFormater {
	private XMLHandler handler ;
	public TestRowsFormater(XMLHandler handler) {
		this.handler = handler ;
	}
	
	public Representation getRepresentation(ResultSet rows) throws SQLException{
		
		StringBuffer buffer = new StringBuffer() ;
		buffer.append(SearchXMLFormater.XML_HEADER) ;

		buffer.append(handler.toXML(rows).toString()) ;
		
		Representation result = new StringRepresentation(buffer, MediaType.APPLICATION_XML);
		result.setCharacterSet(CharacterSet.UTF_8) ;
		return result;
	}
	
}

