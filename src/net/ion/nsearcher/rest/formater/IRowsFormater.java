package net.ion.nsearcher.rest.formater;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.restlet.representation.Representation;

public interface IRowsFormater {
	public Representation getRepresentation(ResultSet rows) throws SQLException ;
}
