package net.ion.nsearcher.rest.formater;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.ion.framework.db.bean.handlers.XMLNodeListHandler;
import net.ion.framework.rest.XMLHandler;

import org.apache.ecs.xml.XML;

public class WrapperXMLHander implements XMLHandler {

	private XMLNodeListHandler real;

	public WrapperXMLHander(String[] attrNames, String[] colNames, String valueColName) {
		this.real = new XMLNodeListHandler(attrNames, colNames, valueColName);
	}

	public XML toXML(ResultSet rows) throws SQLException {
		return (XML) real.handle(rows);
	}

}
