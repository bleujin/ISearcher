package net.ion.nsearcher.common;

import java.io.Reader;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.FieldIndexingStrategy.FieldType;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.FieldInfo.IndexOptions;

public class IndexField implements Fieldable {

	private static final long serialVersionUID = -7320846412631501001L;
	private Fieldable inner;
	private List<Fieldable> more = ListUtil.newList() ;


	IndexField(Fieldable inner) {
		this.inner = inner;
	}

	protected IndexField(FieldType fieldType, String name, String value, Field.Store store, Field.Index index) {
		this(new Field(fieldType == FieldType.Manual ? name : name.toLowerCase(), value, store, index));
	}

	static IndexField load(Fieldable field){
		return new IndexField(field) ;
	}
	
	
	public void addMoreField(Fieldable field) {
		more.add(field);
	}

	public String toString() {
		return getRealField().toString();
	}

	public byte[] binaryValue() {
		return getRealField().getBinaryValue();
	}

	public int getBinaryLength() {
		return getRealField().getBinaryLength();
	}

	public int getBinaryOffset() {
		return getRealField().getBinaryOffset();
	}

	public byte[] getBinaryValue() {
		return getRealField().getBinaryValue();
	}

	public byte[] getBinaryValue(byte[] bytes) {
		return getRealField().getBinaryValue(bytes);
	}

	public float getBoost() {
		return getRealField().getBoost();
	}

	public boolean getOmitNorms() {
		return getRealField().getOmitNorms();
	}

	public boolean isBinary() {
		return getRealField().isBinary();
	}

	public boolean isIndexed() {
		return getRealField().isIndexed();
	}

	public boolean isLazy() {
		return getRealField().isLazy();
	}

	public boolean isStoreOffsetWithTermVector() {
		return getRealField().isStorePositionWithTermVector();
	}

	public boolean isStorePositionWithTermVector() {
		return getRealField().isStorePositionWithTermVector();
	}

	public boolean isStored() {
		return getRealField().isStored();
	}

	public boolean isTermVectorStored() {
		return getRealField().isTermVectorStored();
	}

	public boolean isTokenized() {
		return getRealField().isTokenized();
	}

	public String name() {
		return getRealField().name();
	}

	public Reader readerValue() {
		return getRealField().readerValue();
	}

	public void setBoost(float f) {
		getRealField().setBoost(f);
	}

	public void setOmitNorms(boolean flag) {
		getRealField().setOmitNorms(flag);
	}

	public String stringValue() {
		return getRealField().stringValue();
	}

	public TokenStream tokenStreamValue() {
		return inner.tokenStreamValue();
	}

	public Fieldable getRealField() {
		return inner;
	}

	public Fieldable[] getMoreField() {
		if (more == null || more.size() == 0)
			return new Fieldable[0];
		return more.toArray(new Fieldable[0]);
	}

	public IndexOptions getIndexOptions() {
		return inner.getIndexOptions();
	}

	public void setIndexOptions(IndexOptions option) {
		inner.setIndexOptions(option) ;
	}

}
