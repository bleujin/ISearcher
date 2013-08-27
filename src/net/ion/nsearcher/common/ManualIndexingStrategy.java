package net.ion.nsearcher.common;


public abstract class ManualIndexingStrategy extends FieldIndexingStrategy{

	@Override
	public IndexField date(String name, int yyyymmdd, int hh24miss) {
		return IndexField.BLANK;
	}

	@Override
	public IndexField keyword(String name, String value) {
		return IndexField.BLANK;
	}

	@Override
	public IndexField noStoreText(String name, String value) {
		return IndexField.BLANK;
	}

	@Override
	public IndexField number(String name, long number) {
		return IndexField.BLANK;
	}

	@Override
	public IndexField number(String name, double number) {
		return IndexField.BLANK;
	}

	@Override
	public IndexField text(String name, String value) {
		return IndexField.BLANK;
	}

}
