package net.ion.nsearcher.common;

import net.ion.framework.util.ObjectUtil;
import net.ion.nsearcher.common.FieldIndexingStrategy.FieldType;

import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public abstract class ManualIndexingStrategy extends FieldIndexingStrategy{

	@Override
	public IndexField date(String name, int yyyymmdd, int hh24miss) {
		return null;
	}

	@Override
	public IndexField keyword(String name, String value) {
		return null;
	}

	@Override
	public IndexField noStoreText(String name, String value) {
		return null;
	}

	@Override
	public IndexField number(String name, long number) {
		return null;
	}

	@Override
	public IndexField number(String name, double number) {
		return null;
	}

	@Override
	public IndexField text(String name, String value) {
		return null;
	}

}
