package net.ion.nsearcher.rest.formater;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.core.StreamingOutput;

public interface IRowsFormater {
	public StreamingOutput outputStreaming(ResultSet rows) throws SQLException ;
}
